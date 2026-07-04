"use client";

import Link from "next/link";
import { useAuth } from "@/lib/auth-context";
import { usePathname } from "next/navigation";

const navItems = [
  { href: "/", label: "홈" },
  { href: "/map", label: "지도" },
  { href: "/curation", label: "큐레이션" },
];

export function Header() {
  const { user, isAdmin, logout } = useAuth();
  const pathname = usePathname();

  return (
    <header className="sticky top-0 z-50 border-b border-sepia/10 bg-cream/90 backdrop-blur-md">
      <div className="mx-auto flex max-w-6xl items-center justify-between px-4 py-4">
        <Link href="/" className="group flex items-center gap-2">
          <span className="font-serif text-xl font-bold text-ink">
            다시, <span className="text-rust">골목</span>
          </span>
          <span className="hidden text-xs text-sepia sm:inline">
            지역 기억 아카이브
          </span>
        </Link>

        <nav className="flex items-center gap-1 sm:gap-2">
          {navItems.map((item) => (
            <Link
              key={item.href}
              href={item.href}
              className={`rounded-lg px-3 py-2 text-sm font-medium transition-colors ${
                pathname === item.href
                  ? "bg-rust/10 text-rust"
                  : "text-sepia-dark hover:bg-cream-dark"
              }`}
            >
              {item.label}
            </Link>
          ))}

          {isAdmin && (
            <Link
              href="/admin"
              className={`rounded-lg px-3 py-2 text-sm font-medium transition-colors ${
                pathname === "/admin"
                  ? "bg-forest/10 text-forest"
                  : "text-sepia-dark hover:bg-cream-dark"
              }`}
            >
              관리
            </Link>
          )}
        </nav>

        <div className="flex items-center gap-2">
          {user ? (
            <>
              <Link href="/stories/new" className="btn-primary hidden text-sm sm:inline-flex">
                기억 남기기
              </Link>
              <span className="hidden text-sm text-sepia sm:inline">
                {user.nickname}
              </span>
              <button
                onClick={() => {
                  logout();
                  window.location.href = `${process.env.NEXT_PUBLIC_BASE_PATH || ""}/`;
                }}
                className="btn-secondary text-sm !px-3 !py-2"
              >
                로그아웃
              </button>
            </>
          ) : (
            <>
              <Link href="/auth/signin" className="btn-secondary text-sm !px-3 !py-2">
                로그인
              </Link>
              <Link href="/auth/signup" className="btn-primary text-sm">
                가입
              </Link>
            </>
          )}
        </div>
      </div>
    </header>
  );
}
