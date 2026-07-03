import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  base: '/briefly/',
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/briefly/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/briefly\/api/, '/briefly'),
      },
    },
  },
})
