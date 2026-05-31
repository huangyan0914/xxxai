# WindiCSS 基础使用指南

您在项目中经常看到的 `class="wh-full"` 语法，实际上是来源于项目中所集成的新一代 utility-first CSS 框架：**WindiCSS**。

它可以被看作是 Tailwind CSS 的一个速度更快的替代品或超集，能够让开发者在 HTML/Vue 标签上直接通过类名（原子化 CSS）进行样式的编写，极大提升开发效率。

以下是针对我们当前项目的 WindiCSS 学习指南：

## 1. 为什么会有 `wh-full` ？

由于在原子化 CSS 中，我们经常需要同时设置宽度和高度为 100%。在默认的语法中，需要写成：
```html
<div class="w-full h-full"></div>
```
为了更加便捷，WindiCSS 提供了一个 **`shortcuts` (快捷方式)** 的功能。
打开项目根目录下的 `windi.config.ts` 文件，您可以看到类似的配置：

```typescript
export default defineConfig({
  shortcuts: {
    'wh-full': 'w-full h-full',
    'flex-center': 'flex justify-center items-center',
    'flex-col-center': 'flex-center flex-col',
    // ... 其他系统快捷写法
  }
})
```
**原理解析：**
这就是为什么您能使用 `wh-full` 的原因。当 WindiCSS 引擎在按需编译时看到 `wh-full`，它会自动等效展开渲染成 `w-full h-full` 的样式，避免每次都录入长长的一串复用 CSS。

## 2. 常用基础样式写法速查表

在项目中，您可以直接在 `class=""` 里使用以下语法（不仅限于此，涵盖几乎所有 CSS）：

### 尺寸 (Size)
- **宽度 `w-`**：`w-full` (100%), `w-screen` (100vw), `w-1/2` (50%), `w-10px` (固定 10px) 
- **高度 `h-`**：`h-full` (100%), `h-screen` (100vh), `h-100px` (固定 100px)

### 间距 (Spacing)
- **内边距 Padding `p-`**: 
  - `p-10px` (四面补白 10px)
  - `px-4` (左右补白), `py-4` (上下补白)
  - `pt-2` (上部补白 top), `pb-2` (下部 bottom), `pl-2` (左部 left), `pr-2` (右部 right)
- **外边距 Margin `m-`**: （规则和 p 相同）
  - `m-10px`, `mx-auto` (左右自动居中), `mt-4`

### 布局 & 弹性盒 (Flexbox)
- **基本声明**：`flex`, `inline-flex`
- **Flex 方向**：`flex-row` (横向), `flex-col` (纵向)
- **主轴对齐方式**：
  - `justify-start`, `justify-center`, `justify-end`, `justify-between`, `justify-around`
- **交叉轴对齐方式**：
  - `items-start`, `items-center`, `items-end`, `items-stretch`

### 字体与文本 (Typography)
- **字体大小**：`text-12px`, `text-sm`, `text-lg`, `text-xl`
- **字体粗细**：`font-normal`, `font-bold` (加粗), `font-medium`
- **对齐方式**：`text-left`, `text-center`, `text-right`
- **文本颜色**：`text-red-500`, `text-[#333333]` (支持直接写十六进制色值！)

### 背景与边框 (Background & Border)
- **背景颜色**：`bg-white`, `bg-[#F1F1F1]`, `bg-blue-500`
- **边框**：`border` (默认添加 1px 边框)
- **边框颜色**：`border-[#bebebe]`, `border-gray-200`
- **圆角**：`rounded` (默认小圆角), `rounded-md`, `rounded-full` (完全圆角/胶囊形), `rounded-10px`

## 3. 超好用的“方括号”任意值语法 (Value Auto-infer)

WindiCSS 区别于传统 CSS 的亮点在于，在项目中您无需在配置文件里预先声明所有的属性，**可以直接使用中括号写死具体像素值或色值**：

```html
<!-- 以前的写法：需要写 style="margin-top: 13px; font-size: 15px; background: #2f4050;" -->
<div class="mt-[13px] text-[15px] bg-[#2f4050]">
    自由组合！
</div>

<!-- 自动生成网格属性 -->
<div class="grid grid-cols-[1fr,2fr]">
</div>
```

## 4. 伪类与状态响应

支持通过冒号 `:` 直接添加状态：
```html
<!-- 鼠标移入 Hover -->
<button class="bg-blue-500 hover:bg-blue-600 text-white">Hover Me</button>

<!-- 焦点获取 Focus -->
<input class="border-gray-200 focus:border-blue-500" />
```

## 5. 项目中的实际建议

通过查看 `windi.config.ts` 您还可以利用项目中预设的其他几个实用布局类：
- `flex-center`：快速实现一个完美的水平垂直居中
- `flex-col-center`：垂直排列并且居中
- `ellipsis-text`：文本单行超出省略号

**后续学习资料**：如果您想要查找更多的原生支持类，可以随时参阅官方中文文档：
[WindiCSS 官方文档 (中文)](https://cn.windicss.org/utilities/)
