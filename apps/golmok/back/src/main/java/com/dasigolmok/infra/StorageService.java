package com.dasigolmok.infra;

import com.dasigolmok.global.exception.BusinessException;
import com.dasigolmok.global.response.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Service
public class StorageService {

    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");
    private static final long MAX_SIZE = 5 * 1024 * 1024;

    private final Path uploadPath;

    public StorageService(@Value("${app.upload.dir}") String uploadDir) throws IOException {
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.uploadPath);
    }

    public String saveImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "이미지 파일이 필요합니다.");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "지원하지 않는 이미지 형식입니다.");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "이미지 크기는 5MB 이하여야 합니다.");
        }

        String ext = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + ext;
        Path target = uploadPath.resolve(filename);
        try {
            file.transferTo(target);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "이미지 저장에 실패했습니다.");
        }
        return "/uploads/" + filename;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".jpg";
        }
        return filename.substring(filename.lastIndexOf('.'));
    }
}
