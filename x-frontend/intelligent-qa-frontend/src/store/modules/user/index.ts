import { defineStore } from 'pinia'
import { UserState } from '../types'

import Avatar from '@/assets/logo.png'

const defaultAvatar = Avatar

const useUserStore = defineStore('user-info', {
  state: (): UserState => {
    return {
      userId: 0,
      roleId: 0,
      token: '',
      userName: '',
      nickName: '小电',
      avatar: defaultAvatar
    }
  },
  actions: {
    saveUser(userInfo: UserState) {
      return new Promise<UserState>(resolve => {
        this.userId = userInfo.userId
        this.roleId = userInfo.roleId
        this.token = userInfo.token
        this.userName = userInfo.userName
        this.nickName = userInfo.nickName
        this.avatar = userInfo.avatar || defaultAvatar
        resolve(userInfo)
      })
    },
    isTokenExpire() {
      return !this.token
    },
    changeNickName(newNickName: string) {
      this.nickName = newNickName
    },
    logout() {
      return new Promise<void>(resolve => {
        this.$reset()
        localStorage.clear()
        sessionStorage.clear()
        resolve()
      })
    }
  }
})

export function useUserStoreContext() {
  return useUserStore()
}
export default useUserStore
