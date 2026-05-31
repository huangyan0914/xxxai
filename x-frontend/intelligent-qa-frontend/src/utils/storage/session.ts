import { encrypto, decrypto } from '../crypto'
import { APP_TITLE } from '../../../config/constant'

export function setSession(key: string, value: unknown) {
  const json = encrypto(value)
  sessionStorage.setItem(`${key}-${APP_TITLE}`, json)
}

export function getSession<T>(key: string) {
  const json = sessionStorage.getItem(`${key}-${APP_TITLE}`)
  let data: T | null = null
  if (json) {
    try {
      data = decrypto(json)
    } catch {
      // 防止解析失败
    }
  }
  return data
}

export function removeSession(key: string) {
  window.sessionStorage.removeItem(`${key}-${APP_TITLE}`)
}

export function clearSession() {
  window.sessionStorage.clear()
}
