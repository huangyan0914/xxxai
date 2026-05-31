import type { GlobalThemeOverrides } from 'naive-ui'
import { cloneDeep } from 'lodash-es'
import { themeSetting } from '@/settings'
import { EnumStorageKey } from '@/enum'
import {
  getThemeColor,
  getColorPalette,
  addColorAlpha,
  setLocal,
  getLocal,
  removeLocal
} from '@/utils'

/** 初始化主题配置 */
export function initThemeSettings() {
  const isProd = import.meta.env.PROD
  // 生产环境才缓存主题配置，本地开发实时调整配置更改配置的json
  const storageSettings = getThemeSettings()
  if (isProd && storageSettings) {
    return storageSettings
  }

  const themeColor = getThemeColor() || themeSetting.themeColor
  const info = themeSetting.isCustomizeInfoColor
    ? themeSetting.otherColor.info
    : getColorPalette(themeColor, 7)
  const otherColor = { ...themeSetting.otherColor, info }
  const setting = cloneDeep({ ...themeSetting, themeColor, otherColor })
  return setting
}

type ColorType = 'primary' | 'info' | 'success' | 'warning' | 'error'
type ColorScene = '' | 'Suppl' | 'Hover' | 'Pressed' | 'Active'
type ColorKey = `${ColorType}Color${ColorScene}`
type ThemeColor = Partial<Record<ColorKey, string>>

interface ColorAction {
  scene: ColorScene
  handler: (color: string) => string
}

/** 获取主题颜色的各种场景对应的颜色 */
function getThemeColors(colors: [ColorType, string][]) {
  const colorActions: ColorAction[] = [
    { scene: '', handler: color => color },
    { scene: 'Suppl', handler: color => color },
    { scene: 'Hover', handler: color => getColorPalette(color, 5) },
    { scene: 'Pressed', handler: color => getColorPalette(color, 7) },
    { scene: 'Active', handler: color => addColorAlpha(color, 0.1) }
  ]

  const themeColor: ThemeColor = {}

  colors.forEach(color => {
    colorActions.forEach(action => {
      const [colorType, colorValue] = color
      const colorKey: ColorKey = `${colorType}Color${action.scene}`
      themeColor[colorKey] = action.handler(colorValue)
    })
  })

  return themeColor
}

/** 获取naive的主题颜色 */
export function getNaiveThemeOverrides(
  colors: Record<ColorType, string>
): GlobalThemeOverrides {
  const { primary, success, warning, error } = colors

  const info = themeSetting.isCustomizeInfoColor
    ? colors.info
    : getColorPalette(primary, 7)
  const themeColors = getThemeColors([
    ['primary', primary],
    ['info', info],
    ['success', success],
    ['warning', warning],
    ['error', error]
  ])

  const colorLoading = primary

  return {
    common: {
      ...themeColors,
      textColor1: '#191919'
    },
    Layout: {
      color: '#D9D9D9',
      headerColor: '#D9D9D9',
      siderColor: '#D9D9D9',
      siderBorderColor: '#BEBEBE',
      headerBorderColor: '#BEBEBE'
    },
    LoadingBar: {
      colorLoading
    },
    Checkbox: {
      colorDisabledChecked: 'rgb(232 232 232)'
    },
    Input: {
      placeholderColor: '#99A0B2',
      placeholderColorDisabled: '#99A0B2'
    },
    InternalSelection: {
      placeholderColor: '#99A0B2',
      placeholderColorDisabled: '#99A0B2'
    },

    // 大X样式开始
    Menu: {
      itemTextColorActive: '#409EFF',
      itemColorActive: '#E9E9E9',
      itemColor: '#E9E9E9',
      itemTextColor: '#191919',
      itemHeight: '48px'
    },
    Breadcrumb: {
      itemLineHeight: '32px'
    },
    DataTable: {
      thPaddingMedium: '8px',
      tdPaddingMedium: '8px',
      thColor: '#2F4050',
      tdColor: '#EBEBEB',
      thTextColor: '#E5E5E5',
      borderColor: '#BEBEBE',
      paginationMargin: '24px'
    },
    Button: {
      // colorError: '#fb5152',
      colorPrimary: '#409eff',
      textColorFocus: '#191919',
      textColor: '#191919',
      textColorHover: '#191919',
      border: '1px solid #bebebe',
      borderFocus: '1px solid #bebebe',
      borderHover: '1px solid #bebebe'
    },
    Tag: {
      colorWarning: '#ff7d19',
      textColorWarning: '#ff7d19',
      colorInfo: '#425fff',
      textColorInfo: '#425fff',
      fontSizeMedium: '12px'
    },
    Form: {
      labelTextColor: '#999',
      border: '#bebebe'
    },
    Card: {
      padding: '12px 16px 12px 16px',
      paddingMedium: '12px 16px 12px 16px',
      titleTextColor: '#e5e5e5',
      closeIconColor: '#D3D3D3',
      closeIconColorHover: '#D3D3D3',
      closeIconColorPressed: '#D3D3D3'
    },
    Modal: {
      padding: '12px 16px 12px 16px',
      paddingMedium: '12px 16px 12px 16px',
      closeIconColor: '#D3D3D3',
      closeIconColorHover: '#D3D3D3',
      closeIconColorPressed: '#D3D3D3'
    },
    Pagination: {
      itemColorActive: '#007eff',
      itemTextColorActive: '#e5e5e5',
      itemBorderActive: 'none',
      itemBorder: '1px solid #bebebe',
      itemColor: '#e5e5e5'
    }
  }
}

/** 获取缓存中的主题配置 */
function getThemeSettings() {
  return getLocal<Theme.Setting>(EnumStorageKey['theme-settings'])
}

/** 获取缓存中的主题配置 */
export function setThemeSettings(settings: Theme.Setting) {
  return setLocal(EnumStorageKey['theme-settings'], settings)
}

/** 清除缓存配置 */
export function clearThemeSettings() {
  removeLocal(EnumStorageKey['theme-settings'])
}
