import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  define: {
    global: 'window',
  },
  server: {
    host: true,     // ← обязательно для Docker
    port: 5173      // ← можно настроить под себя
  },
})
