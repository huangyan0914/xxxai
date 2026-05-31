declare global {
  interface Window {
    __POWERED_BY_WUJIE__?: boolean
    __WUJIE_MOUNT?: () => void
    __WUJIE_UNMOUNT?: () => void
    __WUJIE?: {
      mount: () => void
    }
    $message?: any
  }
}

declare module '*.less'
declare module '*.css'

export {}
