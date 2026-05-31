import { isObject } from '../is/index'
import { NIcon } from 'naive-ui'
import { h } from 'vue'
import { UserOutlined, ApartmentOutlined } from '@vicons/antd'

export function deepMerge<T = any>(src: any = {}, target: any = {}): T {
  let key: string
  for (key in target) {
    src[key] = isObject(src[key])
      ? deepMerge(src[key], target[key])
      : (src[key] = target[key])
  }
  return src
}

/**
 * 判断是否 url
 * */
export function isUrl(url: string) {
  return /^(http|https):\/\//g.test(url)
}

/**
 * 处理树--查找父节点
 * @param {arr1} 处理的数组
 * @param {id} 当前节点的id
 * @return {*}
 */
export function dealFilterTreeFindPid(arr1, id) {
  const temp = []
  const forFn = function (arr, id) {
    for (let i = 0; i < arr.length; i++) {
      const item = arr[i]
      if (item.id === id) {
        temp.push(item)
        forFn(arr1, item.pid)
        break
      } else {
        if (item.children) {
          forFn(item.children, id)
        }
      }
    }
  }
  forFn(arr1, id)
  return temp
}
/**
 * 处理树--根据父节点id 禁用子节点
 * @param {arr1} 处理的数组
 * @param {id} 当前节点的id
 * @return {*}
 */
export function dealFilterTreeFindPidDisabledChild(arr1, id) {
  const forChildFn = function (childArr) {
    childArr.forEach(item => {
      item.disabled = true
      if (item.children) {
        forChildFn(item.children)
      }
    })
  }
  const forFn = function (arr, id) {
    for (let i = 0; i < arr.length; i++) {
      const item = arr[i]
      if (item.id === id) {
        if (item.children) {
          forChildFn(item.children)
        }
      } else {
        if (item.children) {
          forFn(item.children, id)
        }
      }
    }
  }
  forFn(arr1, id)
  return arr1
}

/**
 * 处理树--父节点不选中
 * @param {arr} 处理的数组
 * @param {disP} 父节点不选中
 * @param {disabled} 所有节点不选中
 * @return {*}
 */
export function dealFilterTree(arr, disP, disabled) {
  if (!arr) {
    return
  }

  const map = {}
  arr.forEach(item => {
    map[item.id] = item
  })
  const result = []
  arr.forEach(item => {
    item.prefix = () =>
      h(NIcon, null, {
        default: () =>
          /^u_/.test(item.id) ? h(UserOutlined) : h(ApartmentOutlined)
      })
    if (disabled) {
      // 所有节点禁用不选中
      item.disabled = true
    }
    const mapItem = map[item.pid]
    if (mapItem) {
      ;(mapItem.children || (mapItem.children = [])).push(item)
      if (disP) {
        // 父节点禁用不选中
        mapItem.disabled = true
      }
    } else {
      result.push(item)
    }
  })
  return result
}

/**
 * 处理树--父节点不选中
 * @param {arr} 处理的数组
 * @param {disp} 组织架构不选中, o_开头
 * @return {*}
 */
export function dealFilterTreeNoOrg(arr, disp) {
  if (!arr) {
    return
  }

  const map = {}
  arr.forEach(item => {
    map[item.id] = item
  })
  const result = []
  arr.forEach(item => {
    item.prefix = () =>
      h(NIcon, null, {
        default: () =>
          /^u_/.test(item.id) ? h(UserOutlined) : h(ApartmentOutlined)
      })
    if (disp) {
      item.disabled = /^o_/.test(item.id) ? true : false
    }
    const mapItem = map[item.pid]
    if (mapItem) {
      ;(mapItem.children || (mapItem.children = [])).push(item)
    } else {
      result.push(item)
    }
  })
  return result
}

/**
 * 导出文件流
 * @param {*} data 文件流
 */
export function exportFile(res: any, name?: string) {
  const blob = new Blob([res.data])
  const url = window.URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download =
    name ||
    (decodeURIComponent(res.headers['content-disposition']) !== 'undefined'
      ? decodeURIComponent(res.headers['content-disposition'].split('=')[1])
      : 'file.xls')
  a.click()
  window.URL.revokeObjectURL(url)
}
