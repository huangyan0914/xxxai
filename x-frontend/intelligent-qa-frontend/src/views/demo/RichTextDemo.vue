<template>
  <div class="rich-text-demo">
    <h1>富文本编辑器演示</h1>

    <div class="editor-section">
      <h2>基础富文本编辑器</h2>
      <RichTextEditor
        v-model:modelValue="content1"
        placeholder="请输入内容..."
        @text-change="onTextChange1"
        @editor-change="onEditorChange1"
        ref="editor1"
      />
      <!-- <div class="content-preview">
        <h3>HTML 内容预览:</h3>
        <div v-html="content1" class="preview-box"></div>
      </div> -->
    </div>

    <!-- <div class="editor-section">
      <h2>只读模式</h2>
      <RichTextEditor 
        v-model="content2" 
        :read-only="true"
      />
    </div> -->
    <!--     
    <div class="editor-section">
      <h2>自定义高度</h2>
      <RichTextEditor 
        v-model="content3" 
        min-height="400px"
        placeholder="请输入内容，这个编辑器更高一些..."
      />
    </div> -->

    <div class="actions">
      <button @click="getContent">获取所有内容</button>
      <button @click="setContent">设置内容</button>
      <button @click="clearContent">清空内容</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import RichTextEditor from '@/components/RichTextEditorTwo.vue'

const editor1 = ref(null)
// 基础编辑器内容
const content1 = ref(
  '<p>这是基础的富文本编辑器示例</p><p>您可以在这里<strong>加粗</strong>、<em>斜体</em>、<u>下划线</u>等操作。</p>'
)

// 事件处理
const onTextChange1 = (delta, oldDelta, source) => {
  console.log('文本发生变化:', { delta, oldDelta, source })
}

const onEditorChange1 = event => {
  console.log('编辑器发生变化:', event)
}

// 操作方法
const getContent = () => {
  alert(`编辑器1内容: ${content1.value}`)
}

const setContent = () => {
  content1.value =
    '<p>这是通过按钮设置的内容</p><p><strong>加粗文本</strong></p><p><em>斜体文本</em></p>'
  editor1.value.setContent(content1.value)
}

const clearContent = () => {
  content1.value = ''
  editor1.value.setContent('')
}
</script>

<style scoped>
.rich-text-demo {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.editor-section {
  margin-bottom: 30px;
}

.content-preview {
  margin-top: 20px;
}

.preview-box {
  border: 1px solid #ddd;
  padding: 10px;
  min-height: 100px;
  background-color: #f9f9f9;
}

.actions {
  margin-top: 30px;
  display: flex;
  gap: 10px;
}

button {
  padding: 8px 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}
</style>
