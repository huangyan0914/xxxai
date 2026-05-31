import { http } from '@/utils/http/axios'

/**
 * @description: 凭证列表
 */
export function getVoucherList(params: any) {
  return http.request({
    url: '/api/dp-certificate/list',
    method: 'get',
    params
  })
}
/**
 * @description: 添加凭证
 */
export function voucherAdd(params: any) {
  return http.request(
    {
      url: '/api/dp-certificate/save',
      method: 'post',
      params
    },
    {
      isShowSuccessMessage: true
    }
  )
}
/**
 * @description: 编辑凭证
 */
export function voucherPut(params: any) {
  return http.request(
    {
      url: '/api/dp-certificate/update',
      method: 'put',
      params
    },
    {
      isShowSuccessMessage: true
    }
  )
}
/**
 * @description: 归属人员列表
 */
export function getUnusedAccount() {
  return http.request({
    url: '/api/dp-certificate/getUnusedAccount',
    method: 'get'
  })
}
/**
 * @description: 凭证状态启用
 */
export function startVoucher(params: any) {
  return http.request(
    {
      url: `/api/dp-certificate/enable/${params}`,
      method: 'put',
      params
    },
    {
      isShowSuccessMessage: true
    }
  )
}
/**
 * @description: 凭证状态停用
 */
export function stopVoucher(params: any) {
  return http.request(
    {
      url: `/api/dp-certificate/disable/${params}`,
      method: 'put',
      params
    },
    {
      isShowSuccessMessage: true
    }
  )
}
/**
 * @description: 删除凭证
 */
export function voucherDel(id: any) {
  return http.request(
    {
      url: `/api/dp-certificate/remove/${id}`,
      method: 'delete'
    },
    {
      isShowSuccessMessage: true
    }
  )
}
