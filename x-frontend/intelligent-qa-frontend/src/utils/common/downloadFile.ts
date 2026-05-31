/**
 * 导出文件流
 * @param {*} data 文件流
 * @param {*} name 设置下载的文件名称
 */
export function downloadExportFile(res: any, name?: string) {
  const blob = new Blob([res.data])

  const url = window.URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = name
    ? name
    : decodeURIComponent(res.headers['content-disposition'])
    ? decodeURIComponent(res.headers['content-disposition'].split('=')[1])
    : name
  // a.download = decodeURI(name)
  a.click()
  window.URL.revokeObjectURL(url)
}
