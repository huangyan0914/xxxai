import path from 'path'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  base: '/child/ai-model-lifecycle-frontend',
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
  server: {
    host: '0.0.0.0',
    port: 9906,
    open: false,
    headers: {
      'Access-Control-Allow-Origin': '*'
    },
    proxy: {
      '/api': {
        target: 'http://localhost:9905',
        changeOrigin: true
      }
    }
  },
  build: {
//     outDir: 'ai-model-lifecycle-frontend',
    chunkSizeWarningLimit: 2000
  }
})
