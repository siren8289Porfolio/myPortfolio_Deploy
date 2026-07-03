package com.dasigolmok.domain.story.service;

import com.dasigolmok.domain.admin.entity.AuditAction;
import com.dasigolmok.domain.admin.entity.AuditLog;
import com.dasigolmok.domain.admin.repository.AuditLogRepository;
import com.dasigolmok.domain.analytics.repository.StoryCurationQueryRepository;
import com.dasigolmok.domain.region.entity.Place;
import com.dasigolmok.domain.region.repository.PlaceRepository;
import com.dasigolmok.domain.region.entity.Region;
import com.dasigolmok.domain.region.repository.RegionRepository;
import com.dasigolmok.domain.reaction.repository.ReactionRepository;
import com.dasigolmok.domain.reaction.entity.ReactionType;
import com.dasigolmok.domain.story.dto.*;
import com.dasigolmok.domain.story.entity.Story;
import com.dasigolmok.domain.story.entity.StoryImage;
import com.dasigolmok.domain.story.entity.StoryStatus;
import com.dasigolmok.domain.story.repository.StoryRepository;
import com.dasigolmok.domain.tag.entity.Tag;
import com.dasigolmok.domain.tag.repository.TagRepository;
import com.dasigolmok.domain.tag.entity.TagType;
import com.dasigolmok.domain.user.entity.User;
import com.dasigolmok.domain.user.repository.UserRepository;
import com.dasigolmok.global.exception.BusinessException;
import com.dasigolmok.global.response.ErrorCode;
import com.dasigolmok.global.security.SecurityUtils;
import com.dasigolmok.infra.StorageService;
import com.dasigolmok.infra.analytics.AnalyticsViewRefresher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoryService {

    private final StoryRepository storyRepository;
    private final RegionRepository regionRepository;
    private final PlaceRepository placeRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final ReactionRepository reactionRepository;
    private final AuditLogRepository auditLogRepository;
    private final StorageService storageService;
    private final StoryCurationQueryRepository storyCurationQueryRepository;
    private final AnalyticsViewRefresher analyticsViewRefresher;

    private static final int CURATION_LIMIT = 50;

    @Transactional
    public CreateStoryResponse createStory(CreateStoryRequest request) {
        String userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        if (!Boolean.TRUE.equals(request.getCopyrightAgreed())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "저작권 동의가 필요합니다.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));
        Region region = regionRepository.findById(request.getRegionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "지역을 찾을 수 없습니다."));

        Place place = Place.builder()
                .region(region)
                .name(request.getLocationName() != null ? request.getLocationName() : "미지정 장소")
                .address(request.getLocationName() != null ? request.getLocationName() : "")
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
        placeRepository.save(place);

        Story story = Story.builder()
                .user(user)
                .region(region)
                .place(place)
                .title(request.getTitle())
                .content(request.getDescription())
                .year(parseYear(request.getPeriodTag()))
                .status(StoryStatus.PENDING)
                .likeCount(0)
                .build();

        Set<Tag> tags = new HashSet<>();
        if (request.getPeriodTag() != null && !request.getPeriodTag().isBlank()) {
            tags.add(findOrCreateTag(request.getPeriodTag(), TagType.PERIOD));
        }
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            tags.add(findOrCreateTag(request.getCategory(), TagType.THEME));
        }
        if (request.getSourceType() != null && !request.getSourceType().isBlank()) {
            tags.add(findOrCreateTag(request.getSourceType(), TagType.CUSTOM));
        }
        story.setTags(tags);

        if (request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
            story.getImages().add(StoryImage.builder()
                    .story(story)
                    .imageUrl(request.getImageUrl())
                    .thumbnailUrl(request.getImageUrl())
                    .sortOrder(0)
                    .build());
        }

        storyRepository.save(story);
        auditLogRepository.save(AuditLog.builder()
                .user(user)
                .action(AuditAction.CREATE)
                .entityType("Story")
                .entityId(story.getId())
                .detail("스토리 등록")
                .build());
        analyticsViewRefresher.refreshAll();
        return new CreateStoryResponse(story.getId());
    }

    @Transactional(readOnly = true)
    public List<MapMarkerResponse> getMapStories(String regionId) {
        String resolvedRegionId = (regionId != null && !regionId.isBlank()) ? regionId : null;
        return storyRepository.findApprovedForMap(resolvedRegionId).stream()
                .map(this::toMapMarker)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StoryCardResponse> getCurationStories(String sort, String tag) {
        return storyCurationQueryRepository.findCuration(sort, tag, CURATION_LIMIT);
    }

    @Transactional(readOnly = true)
    public StoryDetailResponse getStory(String storyId) {
        Story story = storyRepository.findByIdAndDeletedAtIsNull(storyId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "스토리를 찾을 수 없습니다."));
        if (story.getStatus() != StoryStatus.APPROVED) {
            String userId = SecurityUtils.getCurrentUserId();
            boolean isOwner = userId != null && story.getUser().getId().equals(userId);
            boolean isAdmin = SecurityUtils.getCurrentUser() != null && SecurityUtils.getCurrentUser().isAdmin();
            if (!isOwner && !isAdmin) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "스토리를 찾을 수 없습니다.");
            }
        }
        boolean liked = false;
        String userId = SecurityUtils.getCurrentUserId();
        if (userId != null) {
            liked = reactionRepository.existsByUserIdAndStoryIdAndType(userId, storyId, ReactionType.LIKE);
        }
        return toDetail(story, liked);
    }

    public ImageUploadResponse uploadImage(MultipartFile file) {
        String url = storageService.saveImage(file);
        return ImageUploadResponse.builder()
                .imageUrl(url)
                .thumbnailUrl(url)
                .build();
    }

    private Tag findOrCreateTag(String name, TagType type) {
        return tagRepository.findByName(name).orElseGet(() ->
                tagRepository.save(Tag.builder().name(name).type(type).build()));
    }

    private int parseYear(String periodTag) {
        if (periodTag == null || periodTag.isBlank()) {
            return 2000;
        }
        String digits = periodTag.replaceAll("[^0-9]", "");
        if (digits.length() >= 4) {
            return Integer.parseInt(digits.substring(0, 4));
        }
        return 2000;
    }

    private String firstImage(Story story) {
        return story.getImages().isEmpty() ? "/uploads/placeholder.jpg" : story.getImages().get(0).getImageUrl();
    }

    private String findTagByType(Story story, TagType type) {
        return story.getTags().stream()
                .filter(t -> t.getType() == type)
                .map(Tag::getName)
                .findFirst()
                .orElse(null);
    }

    private String findCategory(Story story) {
        String theme = findTagByType(story, TagType.THEME);
        return theme != null ? theme : "거리";
    }

    private MapMarkerResponse toMapMarker(Story story) {
        return MapMarkerResponse.builder()
                .storyId(story.getId())
                .title(story.getTitle())
                .latitude(story.getPlace() != null ? story.getPlace().getLatitude() : null)
                .longitude(story.getPlace() != null ? story.getPlace().getLongitude() : null)
                .thumbnailUrl(firstImage(story))
                .category(findCategory(story))
                .locationName(story.getPlace() != null ? story.getPlace().getName() : null)
                .build();
    }

    public StoryCardResponse toCard(Story story) {
        return StoryCardResponse.builder()
                .id(story.getId())
                .title(story.getTitle())
                .description(story.getContent())
                .imageUrl(firstImage(story))
                .category(findCategory(story))
                .periodTag(findTagByType(story, TagType.PERIOD))
                .createdBy(StoryCardResponse.CreatedBy.builder()
                        .nickname(story.getUser().getNickname())
                        .email(story.getUser().getEmail())
                        .build())
                .place(StoryCardResponse.PlaceSummary.builder()
                        .locationName(story.getPlace() != null ? story.getPlace().getName() : null)
                        .name(story.getPlace() != null ? story.getPlace().getName() : null)
                        .region(StoryCardResponse.RegionSummary.builder()
                                .name(story.getRegion().getName())
                                .build())
                        .build())
                ._count(StoryCardResponse.CountSummary.builder()
                        .reactions(story.getLikeCount())
                        .build())
                .build();
    }

    private StoryDetailResponse toDetail(Story story, boolean liked) {
        String imageUrl = firstImage(story);
        List<StoryDetailResponse.TagItem> tags = story.getTags().stream()
                .map(t -> StoryDetailResponse.TagItem.builder()
                        .tag(StoryDetailResponse.TagRef.builder().name(t.getName()).build())
                        .build())
                .collect(Collectors.toList());
        return StoryDetailResponse.builder()
                .id(story.getId())
                .title(story.getTitle())
                .description(story.getContent())
                .imageUrl(imageUrl)
                .thumbnailUrl(imageUrl)
                .status(story.getStatus().name())
                .periodTag(findTagByType(story, TagType.PERIOD))
                .category(findCategory(story))
                .sourceType(findTagByType(story, TagType.CUSTOM))
                .likeCount(story.getLikeCount())
                .liked(liked)
                .createdAt(story.getCreatedAt().toString())
                .createdBy(StoryCardResponse.CreatedBy.builder()
                        .nickname(story.getUser().getNickname())
                        .build())
                .place(StoryDetailResponse.PlaceDetail.builder()
                        .locationName(story.getPlace() != null ? story.getPlace().getName() : null)
                        .latitude(story.getPlace() != null ? story.getPlace().getLatitude() : null)
                        .longitude(story.getPlace() != null ? story.getPlace().getLongitude() : null)
                        .region(StoryCardResponse.RegionSummary.builder()
                                .name(story.getRegion().getName())
                                .build())
                        .build())
                .tags(tags)
                .build();
    }
}
