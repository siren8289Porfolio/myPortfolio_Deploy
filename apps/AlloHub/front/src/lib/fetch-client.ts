"use client";

const TOKEN_KEY = "alloc_auth_token";
const ROLE_KEY = "alloc_auth_role";

export type UserRole = "operator" | "admin";

export function getAuthToken(): string | null {
  if (typeof window === "undefined") return null;
  return sessionStorage.getItem(TOKEN_KEY);
}

export function getAuthRole(): UserRole | null {
  if (typeof window === "undefined") return null;
  const role = sessionStorage.getItem(ROLE_KEY);
  if (role === "operator" || role === "admin") return role;
  return null;
}

export function setAuthSession(token: string, role: UserRole) {
  sessionStorage.setItem(TOKEN_KEY, token);
  sessionStorage.setItem(ROLE_KEY, role);
}

export function clearAuthToken() {
  sessionStorage.removeItem(TOKEN_KEY);
  sessionStorage.removeItem(ROLE_KEY);
}

// 정적 export로 빌드되어 /allohub 하위 경로에 서빙되므로, 상대경로 API 호출("/api/...")에
// 배포 base path를 자동으로 붙여준다. 로컬 개발(next dev)에서는 비워둬 기존 rewrite가 동작한다.
const BASE_PATH = process.env.NEXT_PUBLIC_BASE_PATH ?? "";

function withBasePath(input: RequestInfo): RequestInfo {
  if (typeof input === "string" && input.startsWith("/")) {
    return `${BASE_PATH}${input}`;
  }
  return input;
}

export async function apiFetch(
  input: RequestInfo,
  init?: RequestInit,
): Promise<Response> {
  const token = getAuthToken();
  const headers = new Headers(init?.headers);

  if (token) {
    headers.set("Authorization", `Bearer ${token}`);
  }
  if (init?.body && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }

  return fetch(withBasePath(input), { ...init, headers });
}

export { BASE_PATH, withBasePath };

export type ApiSuccess<T> = {
  success: true;
  message: string;
  data: T;
};

export type ApiErrorBody = {
  success: false;
  errorCode: string;
  message: string;
};

export async function parseApiResponse<T>(res: Response): Promise<{
  ok: boolean;
  data?: T;
  message?: string;
  errorCode?: string;
}> {
  const body = await res.json();
  if (body.success === true) {
    return { ok: true, data: body.data, message: body.message };
  }
  return {
    ok: false,
    message: body.message ?? "요청 처리에 실패했습니다",
    errorCode: body.errorCode,
  };
}
