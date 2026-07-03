import { getStoredToken } from "./auth-context";

const API_BASE = "/api/v1";

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
