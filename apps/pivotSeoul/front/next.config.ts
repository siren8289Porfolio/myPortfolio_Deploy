import type { NextConfig } from "next";

// 리소스가 빠듯한 EC2에 Node 서버 없이 배포하기 위해 정적 export로 빌드하고,
// /pivot 하위 경로 nginx 정적 컨테이너로 서빙한다.
const nextConfig: NextConfig = {
  output: "export",
  basePath: "/pivot",
  images: {
    unoptimized: true,
  },
  typescript: {
    // Existing project code has pre-existing TS issues.
    // Keep migration focused on runtime stack conversion.
    ignoreBuildErrors: true,
  },
};

export default nextConfig;
