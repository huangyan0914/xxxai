module.exports = {
  env: {
    browser: true,
    es2021: true,
    node: true
  },
  extends: [
    'plugin:vue/vue3-recommended',
    'plugin:@typescript-eslint/recommended',
    'prettier',
    'plugin:prettier/recommended'
  ],
  parser: 'vue-eslint-parser',
  parserOptions: {
    ecmaVersion: 'latest',
    parser: '@typescript-eslint/parser',
    sourceType: 'module'
  },
  plugins: ['vue', '@typescript-eslint', 'prettier'],
  rules: {
    'vue/multi-word-component-names': 0, // 驼峰报错-横线
    '@typescript-eslint/no-var-requires': 0, // require导入
    'vue/require-default-prop': 0, // props默认值
    'vue/valid-define-props': 0, // 引用本地声明的变量
    'vue/v-on-event-hyphenation': 0, // 事件驼峰
    '@typescript-eslint/no-unused-vars': 0,
    '@typescript-eslint/no-explicit-any': 0,
    'vue/no-unused-components': 0,
    'vue/no-reserved-component-names': 0,
    '@typescript-eslint/no-empty-function': 0,
    'vue/no-v-html': 0,
    '@typescript-eslint/ban-types': 0,
    'vue/no-setup-props-destructure': 0,
    '@typescript-eslint/no-this-alias': 0
  }
}
