"use client";

import { useEffect, useState } from "react";
import { useAuth } from "@/lib/auth-context";
import { useParams } from "next/navigation";
import Image from "next/image";
import Link from "next/link";
import dynamic from "next/dynamic";
import { apiFetch, apiFetchRaw, resolveAssetUrl } from "@/lib/api-client";

const StoryMap = dynamic(
  () => import("@/components/StoryMap").then((m) => m.StoryMap),
  { ssr: false }
);

type Story = {
  id: string;
  title: string;
  description: string;
  imageUrl: string;
  thumbnailUrl?: string;
  status: string;
  periodTag: string | null;
  category: string;
  sourceType: string | null;
  likeCount?: number;
  liked?: boolean;
  createdAt: string;
  createdBy: { nickname: string };
  place: {
    locationName: string;
    latitude: number;
    longitude: number;
    region: { name: string };
  };
  tags: { tag: { name: string } }[];
};

export default function StoryDetailClient() {
  const { user } = useAuth();
  const params = useParams();
  const storyId = params.storyId as string;
  const [story, setStory] = useState<Story | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  const [liked, setLiked] = useState(false);
  const [likeCount, setLikeCount] = useState(0);
  const [showReport, setShowReport] = useState(false);
  const [reportReason, setReportReason] = useState("");
  const [message, setMessage] = useState("");

  useEffect(() => {
    if (!storyId || storyId === "_") return;
    setLoading(true);
    setError(false);
    apiFetch<Story>(`/stories/${storyId}`)
      .then((data) => {
        setStory(data);
        setLikeCount(data.likeCount ?? 0);
        setLiked(Boolean(data.liked));
      })
      .catch(() => {
        setStory(null);
        setError(true);
      })
      .finally(() => setLoading(false));
  }, [storyId]);

  async function handleLike() {
    if (!user) return;
    const method = liked ? "DELETE" : "POST";
    const res = await apiFetchRaw(`/stories/${storyId}/likes`, { method });
    const json = await res.json();
    if (json.success) {
      setLiked(json.data.liked);
      setLikeCount(json.data.likeCount);
    }
  }

  async function handleShare() {
    const url = window.location.href;
    if (navigator.share) {
      await navigator.share({ title: story?.title, url });
    } else {
      await navigator.clipboard.writeText(url);
      setMessage("링크가 복사되었습니다");
      setTimeout(() => setMessage(""), 2000);
    }
  }

  async function handleReport() {
    if (!reportReason.trim()) return;
    const res = await apiFetchRaw(`/stories/${storyId}/reports`, {
      method: "POST",
      body: JSON.stringify({ reason: reportReason }),
    });
    if (res.ok) {
      setMessage("신고가 접수되었습니다");
      setShowReport(false);
      setReportReason("");
    }
  }

  if (loading) {
    return (
      <div className="flex min-h-[50vh] items-center justify-center">
        <p className="text-sepia">불러오는 중...</p>
      </div>
    );
  }

  if (error || !story) {
    return (
      <div className="flex min-h-[50vh] flex-col items-center justify-center gap-4">
        <p className="text-sepia">스토리를 불러오지 못했습니다.</p>
        <Link href="/curation" className="btn-secondary text-sm">
          큐레이션으로 돌아가기
        </Link>
      </div>
    );
  }

  const statusLabel: Record<string, string> = {
    PENDING: "검수 대기",
    APPROVED: "공개",
    REJECTED: "반려",
    HIDDEN: "숨김",
  };

  return (
    <div className="mx-auto max-w-4xl px-4 py-8">
      <div className="mb-4 flex items-center gap-2 text-sm text-sepia">
        <Link href="/" className="hover:text-rust">
          홈
        </Link>
        <span>/</span>
        <Link href="/curation" className="hover:text-rust">
          큐레이션
        </Link>
        <span>/</span>
        <span className="text-ink">{story.title}</span>
      </div>

      <div className="vintage-card overflow-hidden rounded-2xl">
        <div className="relative aspect-[16/9]">
          <Image
            src={resolveAssetUrl(story.imageUrl)}
            alt={story.title}
            fill
            className="object-cover"
            priority
          />
        </div>

        <div className="p-6 sm:p-8">
          <div className="flex flex-wrap items-start justify-between gap-4">
            <div>
              <div className="flex flex-wrap gap-2">
                <span className="tag-pill">{story.category}</span>
                {story.periodTag && (
                  <span className="tag-pill bg-rust/10 text-rust">
                    {story.periodTag}
                  </span>
                )}
                {story.status !== "APPROVED" && (
                  <span className="tag-pill bg-amber-100 text-amber-800">
                    {statusLabel[story.status]}
                  </span>
                )}
              </div>
              <h1 className="mt-3 font-serif text-3xl font-bold text-ink">
                {story.title}
              </h1>
              <p className="mt-2 text-sepia">
                {story.place.region.name} · {story.place.locationName}
              </p>
            </div>
          </div>

          <p className="mt-6 leading-relaxed text-sepia-dark whitespace-pre-wrap">
            {story.description}
          </p>

          <div className="mt-6 flex flex-wrap gap-4 text-sm text-sepia">
            <span>등록: {story.createdBy.nickname}</span>
            <span>
              {new Date(story.createdAt).toLocaleDateString("ko-KR")}
            </span>
            {story.sourceType && <span>출처: {story.sourceType}</span>}
          </div>

          <div className="mt-8 flex flex-wrap gap-3">
            {user && story.status === "APPROVED" && (
              <button onClick={handleLike} className="btn-secondary !px-4 !py-2">
                {liked ? "♥" : "♡"} 좋아요 {likeCount > 0 && `(${likeCount})`}
              </button>
            )}
            {story.status === "APPROVED" && (
              <button onClick={handleShare} className="btn-secondary !px-4 !py-2">
                공유하기
              </button>
            )}
            {user && (
              <button
                onClick={() => setShowReport(!showReport)}
                className="text-sm text-sepia hover:text-rust"
              >
                신고
              </button>
            )}
          </div>

          {message && (
            <p className="mt-3 text-sm text-forest">{message}</p>
          )}

          {showReport && (
            <div className="mt-4 rounded-lg bg-cream-dark p-4">
              <textarea
                value={reportReason}
                onChange={(e) => setReportReason(e.target.value)}
                className="input-field min-h-[80px]"
                placeholder="신고 사유를 입력해주세요"
              />
              <button onClick={handleReport} className="btn-primary mt-2 text-sm">
                신고 제출
              </button>
            </div>
          )}
        </div>
      </div>

      <div className="mt-8">
        <h2 className="font-serif text-xl font-semibold text-ink">위치</h2>
        <div className="mt-4">
          <StoryMap
            markers={[
              {
                storyId: story.id,
                title: story.title,
                latitude: story.place.latitude,
                longitude: story.place.longitude,
                thumbnailUrl: resolveAssetUrl(story.thumbnailUrl ?? story.imageUrl),
                category: story.category,
              },
            ]}
            center={[story.place.latitude, story.place.longitude]}
            zoom={16}
          />
        </div>
      </div>
    </div>
  );
}
