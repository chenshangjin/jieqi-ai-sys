[TOC]

# 揭棋AI系统

**AI算法 + Windows界面 + Android客户端**

[项目参考](https://github.com/miaosiSari/Jieqi) | [License: GPLv3](./LICENSE.md)

## 项目概述

这是一个中国象棋（揭棋）的AI解决方案，支持在Android手机天天象棋应用中使用AI辅助。

### 工作流程

1. 玩家在天天象棋应用中截图或实时采集棋局画面
2. 将棋局数据上传至服务器
3. 服务器使用AI算法分析当前局面
4. 服务器返回最佳着法建议
5. 玩家根据建议执行走棋

### 核心特性

- **高性能搜索算法**：使用Alpha-Beta剪枝和PVS（Principal Variation Search）
- **多语言实现**：Python版本用于开发，C++版本用于高性能部署
- **揭棋特化**：支持处理暗子/明子状态切换
- **开局库支持**：集成中象开局库优化搜索
- **可视化界面**：Pygame-based Windows GUI

---

## 环境搭建

### Python环境

1. **创建虚拟环境**（推荐使用Conda）

```bash
conda create -n jieqi python=3.12
conda activate jieqi
```

2. **安装依赖**

```bash
pip install pygame
```

**说明**：项目使用Pygame用于GUI展示，readline模块不需要（已注释）。

### C++ 环境（可选）

如需编译高性能C++版本，需要：
- CMake 3.15+
- C++17 编译器 (GCC/Clang/MSVC)

---

## 项目结构

```
jieqi-ai-sys/
├── README.md                          # 项目文档
├── LICENSE.md                         # GPLv3 协议
├── gui.py                             # Windows GUI界面
├── board/                             # 棋盘核心模块
│   ├── board.py                       # 棋盘数据结构 (1345行)
│   ├── common.py                      # 棋力评估表和函数
│   ├── library.py                     # 开局库数据
│   └── ALL_ZERO_TUPLE.txt             # 初始棋局配置
├── musesfish_pvs_v2_fixed.py         # ✅ 推荐：最新AI主程序
├── musesfish_pvs.py                  # 旧版AI实现
├── musesfish_mtd_*.py                # MTD搜索算法版本
├── musesfish_pvs_v1_fixed.py         # 历史版本
└── cppjieqi/                         # C++高性能版本
    ├── cppjieqi2/                    # 第二代实现
    ├── CMakeLists.txt
    ├── main.cpp
    ├── board/                        # 棋盘实现 (AIBoard3/4/5)
    ├── global/                       # 全局配置
    ├── score/                        # 棋力评估引擎
    ├── players.conf                  # 玩家配置文件
    └── score.conf                    # 评估参数配置
```

---

## 1. AI算法模块

### 快速开始

```bash
conda activate jieqi
python musesfish_pvs_v2_fixed.py
```

### 使用方法

- **着法输入**：使用4位UCCI坐标表示，如 `h2e2`（炮二平五）
- **默认配置**：玩家先走，AI后走
- **初始状态**：程序启动时会初始化暗子到明子的映射

### 核心算法

#### 搜索算法
- **Alpha-Beta剪枝**：经典博弈树搜索优化
- **PVS (Principal Variation Search)**：变分搜索，比Alpha-Beta更高效
- **Quiescence Search (静止搜索)**：避免地平线效应

#### 评估函数

**棋子价值（参考象眼权值）**：

| 棋子 | 价值 |
|------|------|
| 帅/将 | 2500 |
| 车   | 233  |
| 炮   | 101  |
| 马   | 108  |
| 相/士 | 23   |
| 兵   | 44   |

**评估方式**：
- 使用位置评估表(PST)评估棋子位置优劣
- 结合子力价值评估当前局面
- 动态调整折扣因子(discount_factor)应对搜索深度

#### 优化技术
- **开局库(Kaijuku)**：存储已知的最佳开局序列
- **空着启发(Null Move)**：快速剪枝低分值节点
- **置换表(Transposition Table)**：缓存已搜索的局面

### 棋盘编码

棋盘的每个位置用一个整数表示，采用二进制编码：

```
位  | 含义                  | 值
----|----------------------|--------
0-2 | 棋子类型            | 0=无 1=车 2=马 3=相 4=士 5=帅 6=炮 7=兵
3   | 明/暗标记           | 0=明 1=暗
4   | 红/黑标记           | 1=红 0=黑
5   | 不确定标记(unused)  | 0/1
```

**棋盘坐标系**：

```
  0 1 2 3 4 5 6 7 8
0 ┌───────────────┐
1 │ 一 二 三 四 五 │
2 │
3 │ 九字区域       │
4 │
5 │
6 │
7 │
8 │
9 └───────────────┘
```

### 关键类和方法

#### `Position` 类
```python
Position(board, score, turn)  # 局面位置
- set()              # 初始化
- gen_moves()        # 生成合法着法
- move(move)         # 执行着法
- rotate()           # 局面旋转(用于搜索)
- nullmove()         # 空着
- value(move)        # 计算着法价值
- mymove_check()     # 验证己方着法
```

#### `Searcher` 类
```python
Searcher()
- alphabeta(pos, alpha, beta, depth, root)  # Alpha-Beta搜索
- search(pos, history)                       # 主搜索接口
- calc_average()                             # 计算平均值
```

### 文件版本说明

| 文件 | 说明 | 状态 |
|------|------|------|
| musesfish_pvs_v2_fixed.py | 最新稳定版，修复多项BUG | ✅ 推荐 |
| musesfish_pvs.py | 原始版本 | 已过时 |
| musesfish_pvs_v1_fixed.py | v1修复版 | ⚠️ 旧版 |
| musesfish_mtd_*.py | 基于MTD搜索的版本 | 实验性 |

---

## 2. Windows GUI界面

### 快速开始

```bash
pip install pygame
python gui.py
```

### 功能说明

- **棋盘显示**：使用Pygame绘制中象棋盘
- **AI集成**：启动AI子进程实时交互
- **着法输入**：通过键盘输入或鼠标点击
- **局面更新**：实时读取AI返回的棋局状态

### 已知问题及解决方案

#### ❌ 问题1：readline模块导入失败

**症状**：`ImportError: No module named readline`

**根本原因**：readline模块在Windows上需要特殊安装，且项目实际未使用

**解决方案**：
- ✅ 在 `musesfish_pvs_v2_fixed.py` 中注释掉 `import readline` 一行
- 该模块不是必需的

#### ❌ 问题2：程序下两步后卡住

**症状**：GUI界面响应延迟，无法继续操作

**可能原因**：
- AI搜索阻塞主线程
- 进程间通信缓冲区满
- 棋子颜色计算错误导致无限循环

**调试建议**：
- 检查是否使用了线程隔离AI搜索
- 确认Queue的读写没有死锁
- 添加debug日志跟踪搜索深度和时间

#### ⚠️ 问题3：棋子颜色显示错误

**现象**：红黑棋子颜色显示不符合预期

**可能原因**：
```python
# gui.py 中的颜色定义
RED_CHESS_COLOR = [255, 255, 255]      # 当前：白色
BLACK_CHESS_COLOR = [0, 0, 0]         # 当前：黑色
```

**修复建议**：
```python
RED_CHESS_COLOR = [255, 0, 0]          # 改为红色
BLACK_CHESS_COLOR = [0, 0, 0]         # 保持黑色
```

### 代码结构

| 类/函数 | 作用 |
|---------|------|
| `Board` | 棋盘管理和绘制 |
| `ChessInfo` | 单个棋子的信息 |
| `ChessPiece` | 棋子类（包含渲染元数据） |

---

## 3. C++ 高性能版本

### 编译指南

#### Ubuntu/Linux
```bash
cd cppjieqi
mkdir build && cd build
cmake .. && make
```

#### Windows (MinGW)
```bash
cd cppjieqi
mkdir build && cd build
cmake .. -G "MinGW Makefiles" && make
```

#### macOS
```bash
cd cppjieqi
mkdir build && cd build
cmake .. && make
```

**注**：Windows终端需设置UTF-8字符集避免中文乱码

### 配置文件

#### `players.conf` - 玩家配置

```
3              # 红方：玩家3或AI等级3
0              # 黑方：玩家0(人类)
100            # 胜利阈值：谁先赢得100盘谁获胜
game.log       # 对局日志文件(用@前缀表示清空)
```

**玩家配置说明**：
- `0` = 人类玩家
- `1, 2, 3, ...` = AI等级（数字越大等级越高）
- 红方总是先手，奇数局红方由玩家1执黑色相反

#### `score.conf` - 评估参数

配置棋力评估函数的各项系数。

### 模块说明

| 模块 | 文件 | 功能 |
|------|------|------|
| Board | board/*.cpp/h | 棋盘实现，包括：<br/>- `AIBoard3/4/5`：不同搜索深度的AI<br/>- `God`：完美信息对手<br/>- `Human`：人类玩家输入处理<br/>- `Thinker`：思考器接口 |
| Global | global/*.cpp/h | 全局配置管理 |
| Score | score/*.cpp/h | 棋力评估引擎 |

### AI版本

- **AIBoard3**：较弱的AI，搜索深度3~4
- **AIBoard4**（调试中）：中等强度，搜索深度4~5，双递归优化
- **AIBoard5**：较强的AI，搜索深度5~6，不确定子明子化处理

推荐先使用AIBoard3或AIBoard5，AIBoard4仍在完善中。

---

## 4. Android客户端

### 规划功能

#### 📱 半自动版本（优先级：高）

需要实现：
- [ ] 悬浮窗显示（始终在天天象棋上方）
- [ ] 一键截图按钮
- [ ] 屏幕上传功能
- [ ] AI建议显示文本框
- [ ] 建议着法的可视化标注

**技术方案**：
- FloatingActionButton 或 WindowManager 实现悬浮窗
- MediaStore API 截图
- OkHttp 上传到服务器
- JSON 解析服务器返回

#### 🤖 全自动版本（优先级：中）

需要实现：
- [ ] 定时自动截图或视频流采集
- [ ] 图像识别算法（识别棋子位置）
- [ ] OCR识别已吃掉的棋子
- [ ] 自动走棋（无障碍服务或触摸注入）

**技术方案**：
- OpenCV 或 ML Kit 图像识别
- TensorFlow 训练棋子分类器
- AccessibilityService 自动走棋

---

## 待完成的功能（TODO）

### AI 算法层面

- [ ] **动态暗子映射**：不在初始化时建立全部映射，而是根据天天象棋的翻子事件逐步更新
- [ ] **灵活的先后手配置**：支持玩家先走或后走
- [ ] **多深度搜索策略**：根据剩余时间自适应搜索深度
- [ ] **开局库扩展**：添加更多中象开局库

### 图像识别层面

- [ ] **棋盘检测**：自动识别棋盘区域
- [ ] **棋子识别**：识别各棋子类型和位置
- [ ] **已吃棋子识别**：识别棋盘旁的已吃棋子区域
- [ ] **暗子可视化**：标注当前已知的暗子位置

### GUI 改进

- [ ] 修复棋子颜色显示问题
- [ ] 解决界面卡顿问题
- [ ] 添加搜索进度显示
- [ ] 支持重新开局和悔棋

### 服务器集成

- [ ] 构建HTTP服务器接收图像数据
- [ ] 实现棋局状态解析API
- [ ] 返回最佳着法及评估值
- [ ] 添加日志和统计功能

---

## 开发指南

### 运行单元测试

```bash
# 测试AI搜索正确性
python -c "from musesfish_pvs_v2_fixed import main; main()"
```

### 调试技巧

**添加日志**：
```python
# 在搜索时打印调试信息
debug_var = 'your debug message'
```

**性能分析**：
```python
import time
start = time.time()
# ... AI搜索 ...
print(f"Elapsed: {time.time() - start:.2f}s")
```

**棋局可视化**：
```python
from musesfish_pvs_v2_fixed import print_pos
print_pos(position)  # 打印当前棋局
```

---

## 贡献指南

欢迎提交问题(Issue)和改进(Pull Request)。主要改进方向：

1. **算法优化**：改进评估函数精度、扩展开局库
2. **BUG修复**：解决已知问题（GUI卡顿、颜色显示等）
3. **新功能**：实现图像识别、自动走棋等
4. **文档完善**：补充代码注释和使用说明

---

## FAQ

**Q: 为什么AI有时走棋很慢？**
A: 取决于搜索深度设置。可在搜索时添加时间限制（TimeLimit）来加速。

**Q: 如何切换到更强的AI？**
A: 在代码中调整搜索深度参数，或使用C++版本的AIBoard5。

**Q: 能否在自己的象棋应用中集成？**
A: 可以。只需将AI模块调用封装为HTTP服务，其他应用通过API调用即可。

**Q: 为什么称之为"作弊"？**
A: 使用AI在真实游戏中获得不公平优势，这是游戏规则不允许的行为。

---

## License

[GNU General Public License v3.0](./LICENSE.md)

Copyright © 2024 chenshangjin

---

## 参考资源

- [象棋AI基础：Alpha-Beta搜索](https://en.wikipedia.org/wiki/Alpha%E2%80%93beta_pruning)
- [UCCI协议](https://www.xqbase.com/protocol/cchess_ucci.htm)
- [象眼棋力评估](https://www.xqbase.com/)
- [ChineseChess-AlphaZero](https://github.com/NeymarL/ChineseChess-AlphaZero)
- [icyChessZero](https://github.com/bupticybee/icyChessZero)


