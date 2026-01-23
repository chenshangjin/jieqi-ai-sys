# 揭棋AI识别器 - Android客户端

## 项目概述

这是一个Android应用程序，用于从天天象棋应用的截图中识别棋盘状态，并将其发送到AI服务器获取推荐着法。该项目实现了方案一的Android端棋盘识别功能。

**项目生成者**: Lingma AI助手  
**生成日期**: 2026年1月23日

## 功能特性

- 棋盘图像识别与处理
- 使用OpenCV进行图像处理
- 与中国象棋AI服务器通信
- 获取AI推荐的着法

## 开发环境要求

- Android Studio Flamingo | 2022.2.1 或更高版本
- Gradle 7.4.1 或更高版本
- Android SDK 33
- 最低支持 Android API 21 (Android 5.0)

## 项目结构

```
android_client/
├── app/
│   ├── src/main/
│   │   ├── java/com/jieqi/chess/recognition/
│   │   │   ├── MainActivity.java          # 主界面
│   │   │   ├── BoardProcessor.java        # 棋盘处理器
│   │   │   ├── AIServiceClient.java       # AI服务客户端
│   │   │   └── JieQiApp.java              # 应用入口
│   │   ├── res/                           # 资源文件
│   │   └── AndroidManifest.xml
│   └── build.gradle                       # 模块构建配置
├── build.gradle                           # 项目构建配置
├── settings.gradle                        # 项目设置
└── README.md                              # 项目说明
```

## 依赖库

- OpenCV for Android 4.6.0
- OkHttp 4.10.0 (网络通信)
- Gson 2.10.1 (JSON处理)
- AndroidX 库

## 使用说明

### 1. 导入项目到Android Studio

1. 启动Android Studio
2. 选择 "Open an existing project"
3. 导航到 `android_client` 目录并选择
4. 等待Gradle同步完成

### 2. 配置AI服务器地址

在 `AIServiceClient.java` 文件中修改 `BASE_URL` 常量为实际的AI服务器地址：

```java
private static final String BASE_URL = "http://your-server-ip:5000";
```

### 3. 编译和运行

1. 确保已连接Android设备或启动了模拟器
2. 点击 "Run" 按钮或使用快捷键 Ctrl+R (Mac: Cmd+R)
3. 安装应用到设备

### 4. 使用步骤

1. 打开应用后点击"截取棋盘"按钮
2. 系统会调用相机拍照（模拟截取天天象棋截图）
3. 点击"识别棋盘"按钮处理图像
4. 点击"获取AI推荐"获取推荐着法

## 技术实现

### 棋盘检测
- 使用OpenCV进行边缘检测和轮廓识别
- 透视变换校正棋盘视角
- 9x10网格划分识别棋子位置

### 棋子识别
- 颜色特征分析判断红黑
- 形状特征分析判断棋子类型
- 转换为256字符格式供AI引擎使用

### 网络通信
- HTTP POST请求与AI服务器通信
- JSON格式传输棋盘状态
- 获取AI推荐着法

## 注意事项

1. 需要授予应用相机和存储权限
2. 确保AI服务器正在运行并可访问
3. 设备需要足够内存处理图像识别
4. 实际部署时需要优化棋子识别算法

## 扩展功能

- 集成无障碍服务直接截取天天象棋应用
- 实现更精确的棋子识别算法
- 添加棋谱记录和历史分析功能

## 许可证

此项目仅供学习和研究使用。