# C++ 高性能版本

这是揭棋AI的C++高性能实现版本。

## 快速开始

### 编译

#### Linux/macOS
```bash
cd cpp
mkdir build && cd build
cmake .. && make
```

#### Windows (MinGW)
```bash
cd cpp
mkdir build && cd build
cmake .. -G "MinGW Makefiles" && make
```

或使用提供的脚本：
```bash
bash scripts/build_cpp.sh
```

### 运行

```bash
./build/bin/jieqi-ai
```

## 配置

### players.conf - 玩家配置

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

### score.conf - 评估参数

配置棋力评估函数的各项系数。

### default.conf - 默认配置

全局配置参数，包括搜索深度、时间限制等。

## 目录结构

```
cpp/
├── CMakeLists.txt          # CMake配置
├── src/                    # 源代码
│   ├── main.cpp           # 主程序入口
│   ├── board/             # 棋盘实现
│   ├── global/            # 全局配置
│   └── score/             # 棋力评估
├── include/               # 头文件（未来扩展）
├── config/                # 配置文件
│   ├── players.conf
│   ├── score.conf
│   └── default.conf
├── data/                  # 数据文件
│   ├── openings/          # 开局库
│   └── logs/              # 对局日志
└── build/                 # 构建目录（编译后生成）
```

## 模块说明

### Board
棋盘实现，包括：
- `AIBoard3/4/5` - 不同搜索深度的AI
- `God` - 完美信息对手
- `Human` - 人类玩家输入处理
- `Thinker` - 思考器接口

### Global
全局配置管理和工具函数。

### Score
棋力评估引擎，包含评估函数和启发式算法。

## AI版本

- **AIBoard3** - 较弱的AI，搜索深度3~4
- **AIBoard4**（调试中） - 中等强度，搜索深度4~5
- **AIBoard5** - 较强的AI，搜索深度5~6

推荐先使用AIBoard3或AIBoard5。

## 注意

- Windows终端需设置UTF-8字符集避免中文乱码
- 如果不希望显示电脑吃玩家的暗子，请注释CMakeLists.txt中的 `add_definitions(-DSHOWDARK)`
