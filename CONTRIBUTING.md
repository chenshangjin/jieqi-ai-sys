# 贡献指南

感谢您有兴趣为揭棋AI系统做出贡献！本文档提供了贡献过程的指导。

## 如何贡献

### 报告Bug

如果你发现了一个bug，请创建一个Issue。请在Issue中包含：

1. **问题描述**：清楚地描述问题
2. **复现步骤**：提供复现问题的步骤
3. **预期行为**：说明应该发生什么
4. **实际行为**：说明实际发生了什么
5. **环境信息**：Python版本、操作系统等
6. **日志或截图**：如果可能，附加相关日志或截图

### 建议功能

如果你有新功能的想法，请创建一个Issue并描述：

1. **功能描述**：新功能是什么
2. **使用场景**：为什么需要这个功能
3. **可能的实现方式**：如果你有想法，可以分享
4. **可能的影响**：这个功能可能影响哪些部分

### 提交代码

#### 准备环境

1. **Fork本仓库**

```bash
git clone https://github.com/your-username/jieqi-ai-sys.git
cd jieqi-ai-sys
```

2. **创建虚拟环境**

```bash
conda create -n jieqi-dev python=3.12
conda activate jieqi-dev
```

3. **安装依赖**

```bash
pip install -e ".[dev]"
pip install pytest pytest-cov flake8 black isort
```

#### 开发工作流

1. **创建特性分支**

```bash
git checkout -b feature/your-feature-name
```

2. **进行修改**

遵循以下代码风格指南：

- **Python代码**：
  - 遵循 [PEP 8](https://www.python.org/dev/peps/pep-0008/)
  - 使用 Black 格式化代码（`black .`）
  - 使用 isort 组织导入（`isort .`）
  - 使用 flake8 检查（`flake8 src/`）
  - 添加类型注解（type hints）

- **C++代码**：
  - 遵循 Google C++ Style Guide
  - 使用 clang-format 格式化代码
  - 添加代码注释解释复杂逻辑

3. **编写测试**

为新功能添加单元测试：

```bash
# 在 tests/ 目录下创建测试文件
# 命名约定：test_<module_name>.py

# 运行测试
pytest

# 查看覆盖率
pytest --cov=src
```

4. **提交代码**

遵循提交消息规范（Conventional Commits）：

```bash
git add <files>
git commit -m "type: description

optional body

Fixes #issue_number"
```

提交类型（type）：
- `feat`: 新功能
- `fix`: 错误修复
- `refactor`: 代码重构
- `docs`: 文档更新
- `test`: 测试相关
- `chore`: 构建、依赖等不影响代码的更改
- `perf`: 性能优化

例子：

```bash
git commit -m "feat: 添加多深度搜索策略

- 实现根据剩余时间自适应搜索深度的功能
- 添加了TimeLimit参数用于控制搜索时间
- 添加相关单元测试

Fixes #42"
```

5. **推送并创建Pull Request**

```bash
git push origin feature/your-feature-name
```

然后在GitHub上创建Pull Request。

#### Pull Request指南

在PR中，请：

1. **清晰的标题**：简明扼要地描述修改
2. **详细的描述**：
   - 修改了什么
   - 为什么要修改
   - 如何修改的
   - 是否有breaking changes
3. **关闭的Issue**：如果修复了Issue，使用 `Fixes #issue_number`
4. **测试结果**：确保所有测试通过
5. **自检清单**：
   - [ ] 代码遵循项目风格指南
   - [ ] 已添加/更新相关文档
   - [ ] 已添加相关单元测试
   - [ ] 所有测试通过
   - [ ] 没有新的警告信息

## 代码审查过程

1. 维护者将审查你的PR
2. 如果需要修改，会提出建议
3. 进行必要的修改后，重新推送
4. 审查通过后，PR将被合并

## 开发建议

### 算法优化

- 改进评估函数的精度
- 优化搜索算法的性能
- 扩展开局库

### 功能改进

- 修复已知的GUI问题
- 实现图像识别模块
- 添加自动走棋功能
- 构建HTTP服务器接口

### 文档改进

- 补充代码注释
- 完善使用文档
- 添加示例代码
- 创建教程

## 行为准则

为了保持一个友好、尊重的社区，所有参与者都应该：

- 尊重他人的观点和想法
- 给予和接受建设性的批评
- 专注于对项目最好的东西
- 对其他社区成员表示同情

## 问题反馈

如有任何问题，可以：

- 创建Issue讨论
- 在讨论区提问
- 发邮件联系维护者

## 许可证

通过贡献代码，你同意将你的贡献在GPLv3许可证下发布。

---

感谢您的贡献！🎉
