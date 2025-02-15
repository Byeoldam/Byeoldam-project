import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  css: {
    postcss: './postcss.config.js', // 별도로 설정된 postcss.config.js를 참조
  },
  server: {
    host: '0.0.0.0',  // Docker에서 접근 가능하도록 설정
    port: 5173,       // 컨테이너에서 사용할 포트
    strictPort: true, // 고정된 포트 사용
    watch: {
      usePolling: true, // 파일 변경 감지를 위한 설정 (Docker 환경에서 필요할 수 있음)
    },
    allowedHosts: ['byeoldam.store', 'www.byeoldam.store', 'localhost', '0.0.0.0']
  }
})
