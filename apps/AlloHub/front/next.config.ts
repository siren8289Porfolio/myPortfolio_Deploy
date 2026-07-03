import type { NextConfig } from "next";

// 리소스가 빠듯한 EC2에 Node 서버 없이 배포하기 위해 정적 export로 빌드하고,
// /allohub 하위 경로 nginx 정적 컨테이너로 서빙한다. API 호출은 fetch-client.ts의
// NEXT_PUBLIC_BASE_PATH가 처리하므로 rewrites()는 사용하지 않는다.
const nextConfig: NextConfig = {
  output: "export",
  basePath: "/allohub",
  images: {
    unoptimized: true,
  },
};

export default nextConfig;
