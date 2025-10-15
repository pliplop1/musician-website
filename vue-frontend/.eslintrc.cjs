/* eslint-env node */
module.exports = {
  root: true,
  env: { browser: true, es2022: true, node: true },
  extends: [
    'eslint:recommended',
    'plugin:vue/vue3-recommended',
    '@vue/eslint-config-prettier'
  ],
  parserOptions: { ecmaVersion: 'latest', sourceType: 'module' },
  rules: {
    'vue/no-v-html': 'off',
    'no-console': ['warn', { allow: ['error'] }],
    'no-debugger': 'warn'
  },
  ignorePatterns: ['dist/', 'node/', 'node_modules/']
}

