const API_BASE = "/mido/api";

async function handle(res) {
  if (!res.ok) {
    let message = `요청 실패 (HTTP ${res.status})`;
    try {
      const body = await res.json();
      if (body?.message) message = body.message;
    } catch {
      // 응답 본문이 JSON이 아닌 경우 기본 메시지 사용
    }
    throw new Error(message);
  }
  if (res.status === 204) return null;
  return res.json();
}

export function createVerification(payload) {
  return fetch(`${API_BASE}/verifications/manual`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  }).then(handle);
}

export function uploadFile(id, file) {
  const form = new FormData();
  form.append("file", file);
  return fetch(`${API_BASE}/verifications/${id}/upload`, {
    method: "POST",
    body: form,
  }).then(handle);
}

export function getContext(id) {
  return fetch(`${API_BASE}/verifications/${id}/context`).then(handle);
}
