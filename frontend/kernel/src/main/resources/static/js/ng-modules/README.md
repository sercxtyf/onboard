# angular模块

该目录下包含了onboard web中的所有angular模块。

## 模块的职责

模块的组织对应着Onboard的特性，每个特性抽象为一个模块。该模块中包括实现该特性的：

- controller：   页面逻辑的控制器，尽量只包含交互逻辑
- service：      为了完成页面逻辑所需要的所有单例：数据获取与数据转换接口、通用的交互逻辑
- directive：    通用的html组件

## 文件的组织

一个模块对应一个文件夹。根模块（App）为onboard，在onboard中引入它需要的其他模块。