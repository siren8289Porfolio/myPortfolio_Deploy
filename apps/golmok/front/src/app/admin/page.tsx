"use client";

import { useEffect, useState } from "react";
import { useAuth } from "@/lib/auth-context";
import { useRouter } from "next/navigation";
import Image from "next/image";
import { apiFetch, apiFetchRaw } from "@/lib/api-client";

type PendingStory = {
  id: string;
  title: string;
  description: string;
  imageUrl: string;
  status: string;
  category: string;
  periodTag: string | null;
  createdAt: string;
  createdBy: { nickname: string; email: string };
  place: {
    locationName: string;
    region: { name: string };
  };
};

export default function AdminPage() {
  const { user, loading: authLoading, isAdmin } = useAuth();
  const router = useRouter();
  const [stories, setStories] = useState<PendingStory[]>([]);
  const [loading, setLoading] = useState(true);
  const [processing, setProcessing] = useState<string | null>(null);

  useEffect(() => {
    if (authLoading) return;
    if (!user) {
      router.push("/auth/signin");
      return;
    }
    if (!isAdmin) {
      router.push("/");
      return;
    }
    fetchStories();
  }, [authLoading, user, isAdmin, router]);

  function fetchStories() {
    setLoading(true);
    apiFetch<PendingStory[]>("/admin/stories")
      .then((data) => {
        setStories(data);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }

  async function updateStatus(
    storyId: string,
    newStatus: "APPROVED" | "REJECTED" | "HIDDEN"
  ) {
    setProcessing(storyId);
    const res = await apiFetchRaw(`/admin/stories/${storyId}/status`, {
      method: "PATCH",
      body: JSON.stringify({ status: newStatus }),
    });

    if (res.ok) {
      setStories((prev) => prev.filter((s) => s.id !== storyId));
    }
    setProcessing(null);
  }

  if (authLoading || loading) {
    return (
      <div className="flex min-h-[50vh] items-center justify-center">
        <p className="text-sepia">불러오는 중...</p>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-5xl px-4 py-8">
      <h1 className="font-serif text-3xl font-bold text-ink">콘텐츠 검수</h1>
      <p className="mt-2 text-sepia">
        등록된 콘텐츠를 검토하고 승인/반려 처리하세요
      </p>

      {stories.length === 0 ? (
        <div className="mt-12 rounded-xl bg-cream-dark py-16 text-center">
          <p className="text-sepia">검수 대기 중인 콘텐츠가 없습니다</p>
        </div>
      ) : (
        <div className="mt-8 space-y-6">
          {stories.map((story) => (
            <div
              key={story.id}
              className="vintage-card flex flex-col gap-4 rounded-xl p-4 sm:flex-row"
            >
              <div className="relative h-40 w-full shrink-0 overflow-hidden rounded-lg sm:h-32 sm:w-48">
                <Image
                  src={story.imageUrl}
                  alt={story.title}
                  fill
                  className="object-cover"
                />
              </div>
              <div className="flex-1">
                <div className="flex flex-wrap gap-2">
                  <span className="tag-pill">{story.category}</span>
                  {story.periodTag && (
                    <span className="tag-pill">{story.periodTag}</span>
                  )}
                  <span
                    className={`tag-pill ${
                      story.status === "PENDING"
                        ? "bg-amber-100 text-amber-800"
                        : "bg-red-100 text-red-800"
                    }`}
                  >
                    {story.status === "PENDING" ? "검수 대기" : "숨김"}
                  </span>
                </div>
                <h3 className="mt-2 font-serif text-lg font-semibold">
                  {story.title}
                </h3>
                <p className="mt-1 text-sm text-sepia line-clamp-2">
                  {story.description}
                </p>
                <p className="mt-2 text-xs text-sepia">
                  {story.place.region.name} · {story.place.locationName} ·{" "}
                  {story.createdBy.nickname} ({story.createdBy.email})
                </p>
                <div className="mt-4 flex flex-wrap gap-2">
                  <button
                    onClick={() => updateStatus(story.id, "APPROVED")}
                    disabled={processing === story.id}
                    className="rounded-lg bg-forest px-4 py-2 text-sm font-medium text-white hover:bg-forest/90 disabled:opacity-50"
                  >
                    승인
                  </button>
                  <button
                    onClick={() => updateStatus(story.id, "REJECTED")}
                    disabled={processing === story.id}
                    className="rounded-lg bg-rust px-4 py-2 text-sm font-medium text-white hover:bg-rust/90 disabled:opacity-50"
                  >
                    반려
                  </button>
                  <button
                    onClick={() => updateStatus(story.id, "HIDDEN")}
                    disabled={processing === story.id}
                    className="btn-secondary !px-4 !py-2 text-sm"
                  >
                    숨김
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
