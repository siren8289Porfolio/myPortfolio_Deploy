"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { StoryCard } from "@/components/StoryCard";
import { apiFetch } from "@/lib/api-client";

type Region = { id: string; name: string; description?: string };
type Story = {
  id: string;
  title: string;
  description: string;
  imageUrl: string;
  category: string;
  periodTag: string | null;
  createdBy: { nickname: string };
  place: { locationName: string };
  _count?: { reactions: number };
};

export default function HomePage() {
  const [regions, setRegions] = useState<Region[]>([]);
  const [stories, setStories] = useState<Story[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      apiFetch<Region[]>("/regions").catch(() => []),
      apiFetch<Story[]>("/stories/curation?sort=latest").catch(() => []),
    ]).then(([regionsData, storiesData]) => {
      setRegions(regionsData);
      setStories(storiesData);
      setLoading(false);
    });
  }, []);

  const recentStories = stories.slice(0, 6);

  return (
    <div>
      <section className="relative overflow-hidden bg-gradient-to-br from-cream via-cream-dark to-sepia/10 px-4 py-20">
        <div className="relative mx-auto max-w-4xl text-center">
          <p className="mb-4 text-sm font-medium tracking-widest text-rust uppercase">
            Local Newtro Archive
          </p>
          <h1 className="font-serif text-4xl font-bold text-ink sm:text-5xl md:text-6xl">
            다시, <span className="text-rust">골목</span>
          </h1>
          <p className="mx-auto mt-6 max-w-2xl text-lg leading-relaxed text-sepia-dark">
            사라져가는 지역의 사진, 장소, 이야기를 지도 위에 기록하고
            <br className="hidden sm:inline" />
            뉴트로 감성으로 다시 만나보세요
          </p>
          <div className="mt-8 flex flex-wrap justify-center gap-4">
            <Link href="/map" className="btn-primary">
              지도로 탐색하기
            </Link>
            <Link href="/stories/new" className="btn-secondary">
              기억 남기기
            </Link>
          </div>
          <div className="mt-12 flex justify-center gap-8 text-center">
            <div>
              <p className="font-serif text-3xl font-bold text-rust">
                {stories.length}
              </p>
              <p className="text-sm text-sepia">기록된 스토리</p>
            </div>
            <div>
              <p className="font-serif text-3xl font-bold text-rust">
                {regions.length}
              </p>
              <p className="text-sm text-sepia">파일럿 지역</p>
            </div>
          </div>
        </div>
      </section>

      <section className="mx-auto max-w-6xl px-4 py-16">
        <h2 className="font-serif text-2xl font-bold text-ink">파일럿 지역</h2>
        <p className="mt-2 text-sepia">지역을 선택해 골목의 기억을 탐색하세요</p>
        <div className="mt-8 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {regions.map((region) => (
            <Link
              key={region.id}
              href={`/map?regionId=${region.id}`}
              className="vintage-card group rounded-xl p-6 transition-all"
            >
              <h3 className="font-serif text-xl font-semibold text-ink group-hover:text-rust">
                {region.name}
              </h3>
              {region.description && (
                <p className="mt-2 text-sm text-sepia line-clamp-2">
                  {region.description}
                </p>
              )}
              <span className="mt-4 inline-block text-sm text-rust">
                지도 보기 →
              </span>
            </Link>
          ))}
        </div>
      </section>

      <section className="bg-cream-dark/50 px-4 py-16">
        <div className="mx-auto max-w-6xl">
          <div className="flex items-end justify-between">
            <div>
              <h2 className="font-serif text-2xl font-bold text-ink">최근 기록</h2>
              <p className="mt-2 text-sepia">새롭게 아카이브된 지역의 기억</p>
            </div>
            <Link href="/curation" className="text-sm text-rust hover:underline">
              전체 보기 →
            </Link>
          </div>
          <div className="mt-8 grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
            {recentStories.map((story) => (
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
                likeCount={story._count?.reactions}
              />
            ))}
          </div>
          {!loading && recentStories.length === 0 && (
            <p className="py-12 text-center text-sepia">
              아직 등록된 스토리가 없습니다. 첫 번째 기억을 남겨보세요!
            </p>
          )}
        </div>
      </section>

      <section className="mx-auto max-w-4xl px-4 py-20 text-center">
        <h2 className="font-serif text-3xl font-bold text-ink">
          당신의 골목 이야기를 들려주세요
        </h2>
        <p className="mt-4 text-sepia">
          오래된 사진 한 장, 잊혀진 장소의 이름, 할머니의 이야기까지
          <br />
          모든 기억이 소중한 지역의 역사가 됩니다
        </p>
        <Link href="/stories/new" className="btn-primary mt-8 inline-flex">
          스토리 등록하기
        </Link>
      </section>
    </div>
  );
}
