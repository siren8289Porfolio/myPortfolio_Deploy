"use client";

import { useEffect, useState, Suspense } from "react";
import { useSearchParams } from "next/navigation";
import dynamic from "next/dynamic";
import { apiFetch } from "@/lib/api-client";
import type { MapMarker } from "@/types";

const StoryMap = dynamic(
  () => import("@/components/StoryMap").then((m) => m.StoryMap),
  { ssr: false }
);

type Region = {
  id: string;
  name: string;
};

function MapContent() {
  const searchParams = useSearchParams();
  const regionId = searchParams.get("regionId") || "";
  const [markers, setMarkers] = useState<MapMarker[]>([]);
  const [regions, setRegions] = useState<Region[]>([]);
  const [selectedRegion, setSelectedRegion] = useState(regionId);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    apiFetch<{ id: string; name: string }[]>("/regions").then(setRegions);
  }, []);

  useEffect(() => {
    setLoading(true);
    const params = selectedRegion ? `?regionId=${selectedRegion}` : "";
    apiFetch<MapMarker[]>(`/stories/map${params}`)
      .then((data) => {
        setMarkers(data);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, [selectedRegion]);

  const center: [number, number] =
    markers.length > 0
      ? [markers[0].latitude, markers[0].longitude]
      : [35.8242, 127.148];

  return (
    <div className="mx-auto max-w-6xl px-4 py-8">
      <div className="mb-6">
        <h1 className="font-serif text-3xl font-bold text-ink">스토리맵</h1>
        <p className="mt-2 text-sepia">
          지도 위에서 지역의 기억을 탐색하세요
        </p>
      </div>

      <div className="mb-4 flex flex-wrap gap-2">
        <button
          onClick={() => setSelectedRegion("")}
          className={`tag-pill cursor-pointer transition-colors ${
            !selectedRegion ? "bg-rust text-white" : "hover:bg-sepia/20"
          }`}
        >
          전체
        </button>
        {regions.map((r) => (
          <button
            key={r.id}
            onClick={() => setSelectedRegion(r.id)}
            className={`tag-pill cursor-pointer transition-colors ${
              selectedRegion === r.id
                ? "bg-rust text-white"
                : "hover:bg-sepia/20"
            }`}
          >
            {r.name}
          </button>
        ))}
      </div>

      {loading ? (
        <div className="flex h-[500px] items-center justify-center rounded-xl bg-cream-dark">
          <p className="text-sepia">지도를 불러오는 중...</p>
        </div>
      ) : (
        <StoryMap markers={markers} center={center} zoom={markers.length ? 15 : 13} />
      )}

      <p className="mt-4 text-center text-sm text-sepia">
        마커를 클릭하면 스토리를 확인할 수 있습니다 · 총 {markers.length}개의 기억
      </p>
    </div>
  );
}

export default function MapPage() {
  return (
    <Suspense
      fallback={
        <div className="flex h-[500px] items-center justify-center">
          <p className="text-sepia">지도를 불러오는 중...</p>
        </div>
      }
    >
      <MapContent />
    </Suspense>
  );
}
