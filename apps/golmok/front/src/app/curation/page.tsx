"use client";

import { useEffect, useState, Suspense } from "react";
import { useSearchParams } from "next/navigation";
import { StoryCard } from "@/components/StoryCard";
import { apiFetch } from "@/lib/api-client";
import { PERIOD_TAGS, CATEGORIES } from "@/lib/constants";

type Story = {
  id: string;
  title: string;
  description: string;
  imageUrl: string;
  category: string;
  periodTag: string | null;
  createdBy: { nickname: string };
  place: { locationName: string };
  _count: { reactions: number };
};

function CurationContent() {
  const searchParams = useSearchParams();
  const [stories, setStories] = useState<Story[]>([]);
  const [sort, setSort] = useState(searchParams.get("sort") || "latest");
  const [tag, setTag] = useState(searchParams.get("tag") || "");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    setLoading(true);
    setError(false);
    const params = new URLSearchParams();
    if (sort) params.set("sort", sort);
    if (tag) params.set("tag", tag);

    apiFetch<Story[]>(`/stories/curation?${params}`)
      .then((data) => {
        setStories(data);
      })
      .catch(() => {
        setStories([]);
        setError(true);
      })
      .finally(() => setLoading(false));
  }, [sort, tag]);

  const allTags = [...PERIOD_TAGS, ...CATEGORIES];

  return (
    <div className="mx-auto max-w-6xl px-4 py-8">
      <div className="mb-8">
        <h1 className="font-serif text-3xl font-bold text-ink">큐레이션</h1>
        <p className="mt-2 text-sepia">
          뉴트로 감성으로 큐레이션된 지역 기억 콘텐츠
        </p>
      </div>

      <div className="mb-6 flex flex-wrap gap-4">
        <div className="flex gap-2">
          {[
            { value: "latest", label: "최신순" },
            { value: "popular", label: "인기순" },
          ].map((s) => (
            <button
              key={s.value}
              onClick={() => setSort(s.value)}
              className={`rounded-lg px-4 py-2 text-sm font-medium transition-colors ${
                sort === s.value
                  ? "bg-rust text-white"
                  : "bg-cream-dark text-sepia-dark hover:bg-sepia/10"
              }`}
            >
              {s.label}
            </button>
          ))}
        </div>
      </div>

      <div className="mb-8 flex flex-wrap gap-2">
        <button
          onClick={() => setTag("")}
          className={`tag-pill cursor-pointer ${
            !tag ? "bg-rust text-white" : "hover:bg-sepia/20"
          }`}
        >
          전체
        </button>
        {allTags.map((t) => (
          <button
            key={t}
            onClick={() => setTag(t)}
            className={`tag-pill cursor-pointer ${
              tag === t ? "bg-rust text-white" : "hover:bg-sepia/20"
            }`}
          >
            {t}
          </button>
        ))}
      </div>

      {loading ? (
        <p className="py-20 text-center text-sepia">불러오는 중...</p>
      ) : error ? (
        <p className="py-20 text-center text-sepia">
          스토리 목록을 불러오지 못했습니다. 잠시 후 다시 시도해주세요.
        </p>
      ) : stories.length === 0 ? (
        <p className="py-20 text-center text-sepia">
          해당 조건의 스토리가 없습니다
        </p>
      ) : (
        <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {stories.map((story) => (
            <StoryCard
              key={story.id}
              id={story.id}
              title={story.title}
              description={story.description}
              imageUrl={story.imageUrl}
              category={story.category}
              periodTag={story.periodTag}
              locationName={story.place.locationName}
              nickname={story.createdBy.nickname}
              likeCount={story._count.reactions}
            />
          ))}
        </div>
      )}
    </div>
  );
}

export default function CurationPage() {
  return (
    <Suspense fallback={<p className="py-20 text-center text-sepia">불러오는 중...</p>}>
      <CurationContent />
    </Suspense>
  );
}
