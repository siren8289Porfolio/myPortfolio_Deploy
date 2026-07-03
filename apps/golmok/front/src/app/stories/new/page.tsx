"use client";

import { useEffect, useState } from "react";
import { useAuth } from "@/lib/auth-context";
import { useRouter } from "next/navigation";
import dynamic from "next/dynamic";
import Image from "next/image";
import { PERIOD_TAGS, CATEGORIES, SOURCE_TYPES } from "@/lib/constants";
import { apiFetch, apiUpload } from "@/lib/api-client";

const StoryMap = dynamic(
  () => import("@/components/StoryMap").then((m) => m.StoryMap),
  { ssr: false }
);

type Region = { id: string; name: string };

export default function NewStoryPage() {
  const { user, loading: authLoading } = useAuth();
  const router = useRouter();
  const [step, setStep] = useState(1);
  const [regions, setRegions] = useState<Region[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const [form, setForm] = useState({
    title: "",
    description: "",
    imageUrl: "",
    locationName: "",
    latitude: 35.8242,
    longitude: 127.148,
    regionId: "",
    periodTag: "",
    category: "",
    sourceType: "",
    copyrightAgreed: false,
  });

  useEffect(() => {
    if (!authLoading && !user) {
      router.push("/auth/signin");
    }
  }, [authLoading, user, router]);

  useEffect(() => {
    apiFetch<Region[]>("/regions").then((data) => {
      setRegions(data);
      if (data.length > 0) {
        setForm((f) => ({ ...f, regionId: data[0].id }));
      }
    });
  }, []);

  async function handleImageUpload(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0];
    if (!file) return;

    try {
      const data = await apiUpload(file);
      setForm((f) => ({ ...f, imageUrl: data.imageUrl }));
    } catch (err) {
      setError(err instanceof Error ? err.message : "업로드 실패");
    }
  }

  async function handleSubmit() {
    setError("");
    setLoading(true);

    try {
      const data = await apiFetch<{ storyId: string }>("/stories", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form),
      });
      router.push(`/stories/${data.storyId}`);
    } catch (err) {
      setError(err instanceof Error ? err.message : "등록에 실패했습니다");
    } finally {
      setLoading(false);
    }
  }

  if (authLoading) {
    return (
      <div className="flex min-h-[50vh] items-center justify-center">
        <p className="text-sepia">불러오는 중...</p>
      </div>
    );
  }

  if (!user) return null;

  return (
    <div className="mx-auto max-w-3xl px-4 py-8">
      <h1 className="font-serif text-3xl font-bold text-ink">기억 남기기</h1>
      <p className="mt-2 text-sepia">
        3단계로 지역의 기억을 등록하세요
      </p>

      {/* Step indicator */}
      <div className="mt-8 flex gap-2">
        {["위치 선택", "내용 입력", "등록 확인"].map((label, i) => (
          <div
            key={label}
            className={`flex-1 rounded-lg py-2 text-center text-sm font-medium ${
              step === i + 1
                ? "bg-rust text-white"
                : step > i + 1
                  ? "bg-forest/20 text-forest"
                  : "bg-cream-dark text-sepia"
            }`}
          >
            {i + 1}. {label}
          </div>
        ))}
      </div>

      <div className="vintage-card mt-8 rounded-2xl p-6">
        {step === 1 && (
          <div className="space-y-4">
            <div>
              <label className="mb-1 block text-sm font-medium">지역</label>
              <select
                value={form.regionId}
                onChange={(e) =>
                  setForm((f) => ({ ...f, regionId: e.target.value }))
                }
                className="input-field"
              >
                {regions.map((r) => (
                  <option key={r.id} value={r.id}>
                    {r.name}
                  </option>
                ))}
              </select>
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium">장소명</label>
              <input
                type="text"
                value={form.locationName}
                onChange={(e) =>
                  setForm((f) => ({ ...f, locationName: e.target.value }))
                }
                className="input-field"
                placeholder="예: 전주 한옥마을 골목"
              />
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium">
                지도에서 위치 클릭
              </label>
              <StoryMap
                markers={[]}
                selectable
                center={[form.latitude, form.longitude]}
                onLocationSelect={(lat, lng) =>
                  setForm((f) => ({ ...f, latitude: lat, longitude: lng }))
                }
              />
              <p className="mt-2 text-xs text-sepia">
                선택된 좌표: {form.latitude.toFixed(5)}, {form.longitude.toFixed(5)}
              </p>
            </div>
            <button
              onClick={() => setStep(2)}
              disabled={!form.locationName || !form.regionId}
              className="btn-primary w-full"
            >
              다음
            </button>
          </div>
        )}

        {step === 2 && (
          <div className="space-y-4">
            <div>
              <label className="mb-1 block text-sm font-medium">사진</label>
              {form.imageUrl ? (
                <div className="relative mb-2 h-48 w-full overflow-hidden rounded-lg">
                  <Image
                    src={form.imageUrl}
                    alt="업로드된 사진"
                    fill
                    className="object-cover"
                  />
                </div>
              ) : null}
              <input
                type="file"
                accept="image/*"
                onChange={handleImageUpload}
                className="input-field"
              />
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium">제목</label>
              <input
                type="text"
                value={form.title}
                onChange={(e) =>
                  setForm((f) => ({ ...f, title: e.target.value }))
                }
                className="input-field"
                placeholder="이 기억의 제목"
              />
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium">설명</label>
              <textarea
                value={form.description}
                onChange={(e) =>
                  setForm((f) => ({ ...f, description: e.target.value }))
                }
                className="input-field min-h-[120px] resize-y"
                placeholder="이 장소에 대한 기억, 이야기를 들려주세요"
              />
            </div>
            <div className="grid gap-4 sm:grid-cols-2">
              <div>
                <label className="mb-1 block text-sm font-medium">시대</label>
                <select
                  value={form.periodTag}
                  onChange={(e) =>
                    setForm((f) => ({ ...f, periodTag: e.target.value }))
                  }
                  className="input-field"
                >
                  <option value="">선택 안 함</option>
                  {PERIOD_TAGS.map((t) => (
                    <option key={t} value={t}>
                      {t}
                    </option>
                  ))}
                </select>
              </div>
              <div>
                <label className="mb-1 block text-sm font-medium">카테고리</label>
                <select
                  value={form.category}
                  onChange={(e) =>
                    setForm((f) => ({ ...f, category: e.target.value }))
                  }
                  className="input-field"
                  required
                >
                  <option value="">선택</option>
                  {CATEGORIES.map((c) => (
                    <option key={c} value={c}>
                      {c}
                    </option>
                  ))}
                </select>
              </div>
            </div>
            <div>
              <label className="mb-1 block text-sm font-medium">출처</label>
              <select
                value={form.sourceType}
                onChange={(e) =>
                  setForm((f) => ({ ...f, sourceType: e.target.value }))
                }
                className="input-field"
              >
                <option value="">선택 안 함</option>
                {SOURCE_TYPES.map((s) => (
                  <option key={s} value={s}>
                    {s}
                  </option>
                ))}
              </select>
            </div>
            <div className="flex gap-2">
              <button onClick={() => setStep(1)} className="btn-secondary flex-1">
                이전
              </button>
              <button
                onClick={() => setStep(3)}
                disabled={
                  !form.title || !form.description || !form.imageUrl || !form.category
                }
                className="btn-primary flex-1"
              >
                다음
              </button>
            </div>
          </div>
        )}

        {step === 3 && (
          <div className="space-y-4">
            <div className="rounded-lg bg-cream-dark p-4">
              <h3 className="font-serif font-semibold">{form.title}</h3>
              <p className="mt-2 text-sm text-sepia">{form.description}</p>
              <div className="mt-3 flex flex-wrap gap-2">
                <span className="tag-pill">{form.category}</span>
                {form.periodTag && (
                  <span className="tag-pill">{form.periodTag}</span>
                )}
                <span className="tag-pill">{form.locationName}</span>
              </div>
            </div>

            <label className="flex items-start gap-3 cursor-pointer">
              <input
                type="checkbox"
                checked={form.copyrightAgreed}
                onChange={(e) =>
                  setForm((f) => ({
                    ...f,
                    copyrightAgreed: e.target.checked,
                  }))
                }
                className="mt-1"
              />
              <span className="text-sm text-sepia-dark">
                업로드한 사진에 대한 사용 권한을 보유하고 있으며, 초상권 및
                저작권 관련 안내에 동의합니다. 타인의 권리를 침해하는 콘텐츠는
                관리자 검수 후 반려될 수 있습니다.
              </span>
            </label>

            {error && <p className="text-sm text-rust">{error}</p>}

            <div className="flex gap-2">
              <button onClick={() => setStep(2)} className="btn-secondary flex-1">
                이전
              </button>
              <button
                onClick={handleSubmit}
                disabled={!form.copyrightAgreed || loading}
                className="btn-primary flex-1"
              >
                {loading ? "등록 중..." : "등록하기"}
              </button>
            </div>

            <p className="text-center text-xs text-sepia">
              등록된 콘텐츠는 관리자 검수 후 공개됩니다
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
