import { Metadata } from "next";
import { Providers } from "@/components/Providers";
import { Header } from "@/components/Header";
import "./globals.css";

export const metadata: Metadata = {
  title: "다시, 골목 | 지역 기억 아카이브",
  description:
    "사라져가는 지역의 사진, 장소, 이야기를 지도 기반으로 기록하고 뉴트로 감성으로 공유하는 참여형 로컬 아카이빙 플랫폼",
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="ko">
      <body className="film-grain antialiased">
        <Providers>
          <Header />
          <main className="min-h-[calc(100vh-73px)]">{children}</main>
        </Providers>
      </body>
    </html>
  );
}
