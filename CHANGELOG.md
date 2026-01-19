# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
