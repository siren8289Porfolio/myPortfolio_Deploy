import { getStoredToken } from "./auth-context";

// static export(basePath=/golmok)로 배포되면 브라우저에서 호출하는 절대경로도
// /golmok 접두사가 붙어야 nginx가 golmok-app으로 올바르게 라우팅한다.
export const BASE_PATH = process.env.NEXT_PUBLIC_BASE_PATH || "";
const API_BASE = `${BASE_PATH}/api/v1`;

function authHeaders(extra?: HeadersInit): HeadersInit {
  const token = getStoredToken();
  return {
    ...(extra || {}),
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
}

export async function apiFetch<T>(
  path: string,
  options?: RequestInit
): Promise<T> {
  const res = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers: authHeaders(options?.headers),
  });
  const json = await res.json();

  if (!json.success) {
    throw new Error(json.message || "요청에 실패했습니다");
  }

  return json.data as T;
}

export async function apiUpload(file: File): Promise<{ imageUrl: string; thumbnailUrl: string }> {
  const formData = new FormData();
  formData.append("file", file);

  const res = await fetch(`${API_BASE}/stories/images`, {
    method: "POST",
    headers: authHeaders(),
    body: formData,
  });

  const json = await res.json();
  if (!json.success) throw new Error(json.message);
  return json.data;
}

export async function apiFetchRaw(path: string, options?: RequestInit) {
  return fetch(`${API_BASE}${path}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...authHeaders(options?.headers),
    },
  });
}

/**
 * 백엔드가 내려주는 imageUrl/thumbnailUrl은 컨텍스트 경로 없이 "/uploads/.."
 * 형태의 상대경로다. 절대 URL(http...)이면 그대로 두고, 그 외에는 basePath를
 * 붙여 nginx를 통해 golmok-app이 실제로 서빙하는 경로로 만든다.
 */
export function resolveAssetUrl(url: string | null | undefined): string {
  if (!url) return "";
  if (/^https?:\/\//.test(url)) return url;
  if (BASE_PATH && url.startsWith(BASE_PATH + "/")) return url;
  return `${BASE_PATH}${url}`;
}

/**
 * static export는 /stories/_ 셸만 빌드한다. Next <Link> 클라이언트 라우팅은
 * 미등록 storyId에서 깨지므로 전체 페이지 이동용 절대 경로를 쓴다.
 */
export function storyDetailHref(storyId: string): string {
  return `${BASE_PATH}/stories/${storyId}`;
}

/** nginx가 stories/_.html 을 서빙해도 URL의 실제 storyId를 읽는다. */
export function storyIdFromPathname(pathname: string): string | null {
  const match = pathname.match(/\/stories\/([^/?#]+)/);
  const id = match?.[1];
  if (!id || id === "_" || id === "new") return null;
  return id;
}
