# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.1] - 2026-01-19

### Changed
- 🌍 **跨平台兼容性改进** (`src/ai/engine.py`)
  - 添加 `os` 模块支持跨平台路径处理
  - 新增动态Python路径配置: 自动添加 `src` 目录到路径
  - 优化 `readline` 模块导入: 使用 try-except 处理 Windows/Unix 兼容性
    - Unix 系统: 正常导入 readline 模块
    - Windows 系统: 自动跳过导入,避免 ImportError

- 🛠️ **GUI路径配置优化** (`src/gui/main.py`)
  - 移除硬编码的绝对路径: `C:\Users\anpro\Desktop\Jieqi-main\musesfish_pvs_20210815.py`
  - 新增 `PROJECT_ROOT` 变量动态计算项目根目录
  - AI引擎路径改为相对路径: `src/ai/engine.py`
  - subprocess 启动时设置工作目录 `cwd=PROJECT_ROOT`

- 📝 **编码兼容性增强** (`src/gui/main.py`)
  - 优化 stdout 解码逻辑,支持多种中文编码
  - 优先使用 UTF-8 解码
  - UTF-8 失败时自动回退到 GBK 编码 (中文常用编码)
  - 提升在不同系统环境下的中文显示稳定性

- 📚 **文档更新同步**
  - 更新 CHANGELOG.md 记录 v2.0.1 改动
  - 更新 README.md 中的文件路径说明
  - 更新快速开始命令中的文件路径
  - 更新 readline 问题解决方案说明
  - 更新项目结构图反映新的目录组织
  - 更新文件版本说明表
  - 更新示例代码中的导入路径

### Fixed
- 🐛 **Windows系统readline导入错误**
  - 问题: Windows 下 `ImportError: No module named readline`
  - 解决: 使用条件导入,在 Windows 上自动跳过该模块

- 🐛 **硬编码路径导致跨平台部署失败**
  - 问题: GUI 中硬编码用户特定路径,无法在其他机器运行
  - 解决: 使用相对路径和动态根目录计算

- 🐛 **中文编码显示乱码**
  - 问题: 不同系统环境输出编码不一致导致解码失败
  - 解决: 实现编码回退机制,支持 UTF-8 和 GBK

- 🛡️ **异常处理增强**
  - 为 GUI draw 方法添加 try-except 异常捕获
  - 防止单次解析错误导致整个界面崩溃
  - 添加错误日志输出便于调试

---

## [2.0.0] - 2026-01-19

### Added
- 🎯 完全重构项目目录结构
  - 创建 `src/` 目录组织Python源代码
  - 创建 `cpp/` 目录统一C++项目
  - 创建 `tests/` 目录存放单元测试
  - 创建 `docs/` 目录存放文档
  - 创建 `scripts/` 目录存放辅助脚本
  - 创建 `archive/` 目录存放历史版本

- 📦 项目配置和工具
  - `setup.py` - Python包配置
  - `requirements.txt` - 依赖管理
  - `.editorconfig` - 编辑器配置
  - `CONTRIBUTING.md` - 贡献指南
  - `CHANGELOG.md` - 版本日志

- 📚 改进的文档
  - 全面扩展 README.md
  - 项目算法设计说明
  - 环境配置指南

### Changed
- 🏗️ 目录结构优化
  - `gui.py` → `src/gui/main.py`
  - `musesfish_pvs_v2_fixed.py` → `src/ai/engine.py`
  - `board/*` → `src/board/`
  - `cppjieqi/*` → `cpp/src/`
  - 配置文件集中到 `cpp/config/`

- 🧹 代码清理
  - 移除重复的 `cppjieqi2` 目录
  - 整理历史版本到 `archive/` 目录

### Fixed
- ✅ 解决README文档混乱问题
- ✅ 统一版权信息和许可证

---

## [1.0.0] - 2021-08-15

### Initial Release
- 基础AI算法实现
- Windows GUI界面
- C++高性能版本

[2.0.0]: https://github.com/chenshangjin/jieqi-ai-sys/releases/tag/v2.0.0
[1.0.0]: https://github.com/chenshangjin/jieqi-ai-sys/releases/tag/v1.0.0
