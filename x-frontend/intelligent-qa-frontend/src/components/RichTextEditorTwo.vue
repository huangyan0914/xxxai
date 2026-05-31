<template>
  <div class="rich-text-editor">
    <QuillEditor
      ref="quillEditorRef"
      v-model:content="content"
      :options="editorOptions"
      :contentType="'html'"
      :style="{ minHeight: minHeight }"
      @textChange="onTextChange"
      @editorChange="onEditorChange"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  minHeight: {
    type: String,
    default: '200px'
  },
  placeholder: {
    type: String,
    default: '请输入内容...'
  },
  readOnly: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'text-change', 'editor-change'])

const quillEditorRef = ref()
const content = ref(props.modelValue)

// 编辑器配置
const editorOptions = {
  theme: 'snow',
  placeholder: props.placeholder,
  readOnly: props.readOnly,
  modules: {
    toolbar: [
      ['bold', 'italic', 'underline', 'strike'],
      ['blockquote', 'code-block'],
      [{ header: 1 }, { header: 2 }],
      [{ list: 'ordered' }, { list: 'bullet' }],
      [{ script: 'sub' }, { script: 'super' }],
      [{ indent: '-1' }, { indent: '+1' }],
      [{ direction: 'rtl' }],
      [{ size: ['small', false, 'large', 'huge'] }],
      [{ header: [1, 2, 3, 4, 5, 6, false] }],
      [{ color: [] }, { background: [] }],
      [{ font: [] }],
      [{ align: [] }],
      ['clean'],
      ['link', 'image', 'video']
    ]
  }
}

// toolbar标题,划过富文本头部提示信息
const titleConfig = [
  { Choice: '.ql-insertMetric', title: '跳转配置' },
  { Choice: '.ql-bold', title: '加粗' },
  { Choice: '.ql-italic', title: '斜体' },
  { Choice: '.ql-underline', title: '下划线' },
  { Choice: '.ql-header', title: '段落格式' },
  { Choice: '.ql-strike', title: '删除线' },
  { Choice: '.ql-blockquote', title: '块引用' },
  { Choice: '.ql-code', title: '插入代码' },
  { Choice: '.ql-code-block', title: '插入代码段' },
  { Choice: '.ql-font', title: '字体' },
  { Choice: '.ql-size', title: '字体大小' },
  { Choice: '.ql-list[value="ordered"]', title: '编号列表' },
  { Choice: '.ql-list[value="bullet"]', title: '项目列表' },
  { Choice: '.ql-direction', title: '文本方向' },
  { Choice: '.ql-header[value="1"]', title: 'h1' },
  { Choice: '.ql-header[value="2"]', title: 'h2' },
  { Choice: '.ql-align', title: '对齐方式' },
  { Choice: '.ql-color', title: '字体颜色' },
  { Choice: '.ql-background', title: '背景颜色' },
  { Choice: '.ql-image', title: '图像' },
  { Choice: '.ql-video', title: '视频' },
  { Choice: '.ql-link', title: '添加链接' },
  { Choice: '.ql-formula', title: '插入公式' },
  { Choice: '.ql-clean', title: '清除字体格式' },
  { Choice: '.ql-script[value="sub"]', title: '下标' },
  { Choice: '.ql-script[value="super"]', title: '上标' },
  { Choice: '.ql-indent[value="-1"]', title: '向左缩进' },
  { Choice: '.ql-indent[value="+1"]', title: '向右缩进' },
  { Choice: '.ql-header .ql-picker-label', title: '标题大小' },
  { Choice: '.ql-header .ql-picker-item[data-value="1"]', title: '标题一' },
  { Choice: '.ql-header .ql-picker-item[data-value="2"]', title: '标题二' },
  { Choice: '.ql-header .ql-picker-item[data-value="3"]', title: '标题三' },
  { Choice: '.ql-header .ql-picker-item[data-value="4"]', title: '标题四' },
  { Choice: '.ql-header .ql-picker-item[data-value="5"]', title: '标题五' },
  { Choice: '.ql-header .ql-picker-item[data-value="6"]', title: '标题六' },
  { Choice: '.ql-header .ql-picker-item:last-child', title: '标准' },
  { Choice: '.ql-size .ql-picker-item[data-value="small"]', title: '小号' },
  { Choice: '.ql-size .ql-picker-item[data-value="large"]', title: '大号' },
  { Choice: '.ql-size .ql-picker-item[data-value="huge"]', title: '超大号' },
  { Choice: '.ql-size .ql-picker-item:nth-child(2)', title: '标准' },
  { Choice: '.ql-align .ql-picker-item:first-child', title: '居左对齐' },
  {
    Choice: '.ql-align .ql-picker-item[data-value="center"]',
    title: '居中对齐'
  },
  {
    Choice: '.ql-align .ql-picker-item[data-value="right"]',
    title: '居右对齐'
  },
  {
    Choice: '.ql-align .ql-picker-item[data-value="justify"]',
    title: '两端对齐'
  }
]
// 给富文本框工具栏加上鼠标悬浮中文提示
const initTitle = () => {
  for (let item of titleConfig) {
    // .editor 是富文本编辑器的类名
    let tip = document.querySelector('.ql-toolbar ' + item.Choice)
    if (tip) {
      tip.setAttribute('title', item.title)
    }
  }
}
onMounted(() => {
  initTitle()
  // setContent(props.modelValue)
})
// 内容变化事件
const onTextChange = (delta, oldDelta, source) => {
  emit('text-change', delta, oldDelta, source)
  if (quillEditorRef.value) {
    const html = quillEditorRef.value.getHTML()
    // 只有当内容真正改变时才触发更新
    if (html !== props.modelValue) {
      emit('update:modelValue', html)
    }
  }
}

// 编辑器变化事件
const onEditorChange = event => {
  emit('editor-change', event)
}

// 监听外部传入的值变化
watch(
  () => props.modelValue,
  newValue => {
    if (quillEditorRef.value) {
      const quill = quillEditorRef.value.getQuill()
      if (newValue !== quill.root.innerHTML) {
        content.value = newValue
      }
    }
  }
  // { immediate: true }
)

// 获取纯文本内容
const getText = () => {
  if (quillEditorRef.value) {
    return quillEditorRef.value.getQuill().getText()
  }
  return ''
}

// 获取HTML内容
const getHTML = () => {
  if (quillEditorRef.value) {
    return quillEditorRef.value.getQuill().getHTML()
  }
  return ''
}

// 设置内容
const setContent = html => {
  if (quillEditorRef.value) {
    const quill = quillEditorRef.value
    // 使用Quill的updateContents来确保变更事件被正确触发
    quill.setHTML(html)
    // 手动触发内容更新事件，确保v-model同步
    content.value = html
    emit('update:modelValue', html)
  }
}

// 清空内容
const clearContent = () => {
  if (quillEditorRef.value) {
    quillEditorRef.value.setHTML('')
  }
}

// 暴露方法给父组件
defineExpose({
  getText,
  getHTML,
  setContent,
  clearContent
})
</script>

<style scoped>
.rich-text-editor {
  border: 1px solid #ddd;
  border-radius: 4px;
}

:deep(.ql-toolbar) {
  border-top-left-radius: 4px;
  border-top-right-radius: 4px;
}

:deep(.ql-container) {
  border-bottom-left-radius: 4px;
  border-bottom-right-radius: 4px;
}
</style>
