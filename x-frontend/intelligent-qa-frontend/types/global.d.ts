interface Window {
  $loadingBar?: import('naive-ui').LoadingBarProviderInst
  $dialog?: import('naive-ui').DialogProviderInst
  $message?: import('naive-ui').MessageProviderInst
  $notification?: import('naive-ui').NotificationProviderInst
  $wujie?: any
  __POWERED_BY_WUJIE__?: boolean
  __WUJIE_MOUNT: () => void
  __WUJIE_UNMOUNT: () => void
  __WUJIE: { mount: () => void }
}

/** 通用类型 */
declare namespace Common {
  /**
   * 策略模式
   * [状态, 为true时执行的回调函数]
   */
  type StrategyAction = [boolean, () => void]
}

/** 构建时间 */
declare const PROJECT_BUILD_TIME: string
