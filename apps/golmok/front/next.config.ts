import type { NextConfig } from "next";

const backendUrl = process.env.BACKEND_URL || "http://localhost:8080";

const nextConfig: NextConfig = {
  output: "export",
  basePath: "/golmok",
  trailingSlash: false,
  images: {
    unoptimized: true,
  },
  // rewrites()는 static export(next build 결과물을 nginx가 서빙)에는 적용되지
  // 않고 `next dev`에서만 동작한다. 로컬 개발 편의를 위해 유지한다.
  async rewrites() {
    return [
      {
        source: "/api/v1/:path*",
        destination: `${backendUrl}/api/v1/:path*`,
      },
      {
        source: "/uploads/:path*",
        destination: `${backendUrl}/uploads/:path*`,
      },
    ];
  },
};

export default nextConfig;
