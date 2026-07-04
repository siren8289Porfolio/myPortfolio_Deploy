import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  base: "/mido/",
  server: {
    proxy: {
      // 백엔드 context-path가 이미 /mido 이므로 별도 rewrite 없이 그대로 전달한다.
      "/mido/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
});
