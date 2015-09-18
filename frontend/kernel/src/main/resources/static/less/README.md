# less 文件

onboard 主要使用 less 的层级特性，用来管理 CSS 冲突。

每个 less 文件对应一个 DOM 元素，通常以 ID 标识。文件组织与DOM树相对应，解决了不同模块 CSS 的冲突问题。

onboard 中引入的 less 文件为 ./onboard/onboard.less，其他文件都通过该入口文件直接或间接引入。