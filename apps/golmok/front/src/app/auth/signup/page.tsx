"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import Link from "next/link";

export default function SignUpPage() {
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [nickname, setNickname] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError("");
    setLoading(true);

    const res = await fetch("/api/v1/auth/signup", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, password, nickname }),
    });

    const data = await res.json();
    setLoading(false);

    if (!data.success) {
      setError(data.message || "회원가입에 실패했습니다");
      return;
    }

    router.push("/auth/signin");
  }

  return (
    <div className="flex min-h-[calc(100vh-73px)] items-center justify-center px-4 py-12">
      <div className="vintage-card w-full max-w-md rounded-2xl p-8">
        <h1 className="font-serif text-2xl font-bold text-ink text-center">
          회원가입
        </h1>
        <p className="mt-2 text-center text-sm text-sepia">
          다시, 골목의 첫 번째 기록자가 되어보세요
        </p>

        <form onSubmit={handleSubmit} className="mt-8 space-y-4">
          <div>
            <label className="mb-1 block text-sm font-medium text-sepia-dark">
              닉네임
            </label>
            <input
              type="text"
              value={nickname}
              onChange={(e) => setNickname(e.target.value)}
              className="input-field"
              required
              minLength={2}
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-sepia-dark">
              이메일
            </label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="input-field"
              required
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium text-sepia-dark">
              비밀번호
            </label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="input-field"
              required
              minLength={8}
            />
          </div>

          {error && <p className="text-sm text-rust">{error}</p>}

          <button type="submit" disabled={loading} className="btn-primary w-full">
            {loading ? "가입 중..." : "회원가입"}
          </button>
        </form>

        <p className="mt-6 text-center text-sm text-sepia">
          이미 계정이 있으신가요?{" "}
          <Link href="/auth/signin" className="text-rust hover:underline">
            로그인
          </Link>
        </p>
      </div>
    </div>
  );
}
