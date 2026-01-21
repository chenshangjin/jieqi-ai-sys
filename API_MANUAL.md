# jieqi-ai-sys API 手册

## 概述

jieqi-ai-sys API 是一个为中国象棋（揭棋）AI系统设计的HTTP接口，允许Android客户端与AI算法进行通信。该API提供棋局分析、最佳着法推荐和棋盘识别功能。

## 维护信息

此代码和文档主要由Lingma AI助手维护。

## 基础信息

- **基础URL**: `http://localhost:5000` (默认)
- **协议**: HTTP/HTTPS
- **内容类型**: `application/json`
- **认证**: 无需认证（生产环境应添加适当的安全措施）

## API 端点

### 1. 健康检查

检查服务器运行状态。

#### GET /health

##### 请求示例
```http
GET /health HTTP/1.1
Host: localhost:5000
```

##### 响应示例
```json
{
  "service": "jieqi-ai-sys server",
  "status": "healthy",
  "timestamp": 1768955703.9854765
}
```

##### 响应字段
- `service` (string): 服务名称
- `status` (string): 服务状态 ("healthy")
- `timestamp` (number): 时间戳

---

### 2. 获取最佳着法

根据当前棋盘状态获取AI推荐的最佳着法。

#### POST /api/v1/chess/move

##### 请求参数
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| board_state | string | 是 | 256字符的棋盘状态表示 |
| current_player | string | 是 | 当前玩家 ("RED" 或 "BLACK") |
| mapping | object | 否 | 暗子到明子的映射关系 |
| captured_pieces | object | 否 | 已吃掉的棋子信息 |
| game_history | array | 否 | 游戏历史记录 |

##### 请求示例
```http
POST /api/v1/chess/move HTTP/1.1
Host: localhost:5000
Content-Type: application/json

{
  "board_state": "               \n               \n               \n   defgkgfed   \n   .........   \n   .h.....h.   \n   i.i.i.i.i   \n   .........   \n   .........   \n   I.I.I.I.I   \n   .H.....H.   \n   .........   \n   DEFGKGFED   \n               \n               \n               \u0000 ",
  "current_player": "RED",
  "mapping": {},
  "captured_pieces": {
    "red": [],
    "black": []
  },
  "game_history": []
}
```

##### 响应示例
```json
{
  "success": true,
  "best_move": {
    "uci_move": "e3e4",
    "from_position": 151,
    "to_position": 135,
    "eval": -3696,
    "depth": 6,
    "pv": [
      "e3e4"
    ],
    "confidence": 0.85
  },
  "evaluation": -3696,
  "message": "Move calculated successfully"
}
```

##### 响应字段
- `success` (boolean): 请求是否成功
- `best_move` (object): 最佳着法信息
  - `uci_move` (string): UCI坐标格式的着法 (如 "e3e4")
  - `from_position` (number): 起始位置索引
  - `to_position` (number): 目标位置索引
  - `eval` (number): 局面评估分数
  - `depth` (number): 搜索深度
  - `pv` (array): 主要变化序列
  - `confidence` (number): 置信度
- `evaluation` (number): 局面评估分数
- `message` (string): 响应消息

---

### 3. 棋盘分析

对当前棋盘局面进行详细分析。

#### POST /api/v1/chess/analyze

##### 请求参数
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| board_state | string | 是 | 256字符的棋盘状态表示 |
| current_player | string | 是 | 当前玩家 ("RED" 或 "BLACK") |
| depth | number | 否 | 搜索深度，默认为3 |

##### 请求示例
```http
POST /api/v1/chess/analyze HTTP/1.1
Host: localhost:5000
Content-Type: application/json

{
  "board_state": "               \n               \n               \n   defgkgfed   \n   .........   \n   .h.....h.   \n   i.i.i.i.i   \n   .........   \n   .........   \n   I.I.I.I.I   \n   .H.....H.   \n   .........   \n   DEFGKGFED   \n               \n               \n               \u0000 ",
  "current_player": "RED",
  "depth": 3
}
```

##### 响应示例
```json
{
  "success": true,
  "analysis": {
    "material_balance": 0,
    "position_score": 0,
    "threats": [],
    "opportunities": [
      "捕获对手在 b9 的棋子",
      "捕获对手在 h9 的棋子"
    ],
    "recommendations": [
      "考虑发展棋子",
      "控制中心区域"
    ],
    "win_probability": 0.5
  },
  "message": "Analysis completed successfully"
}
```

##### 响应字段
- `success` (boolean): 请求是否成功
- `analysis` (object): 局面分析结果
  - `material_balance` (number): 子力平衡
  - `position_score` (number): 局面分数
  - `threats` (array): 威胁列表
  - `opportunities` (array): 机会列表
  - `recommendations` (array): 建议列表
  - `win_probability` (number): 获胜概率
- `message` (string): 响应消息

---

### 4. 棋盘识别

（模拟功能）从图像数据识别棋盘状态。

#### POST /api/v1/chess/recognize

##### 请求参数
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| image_data | string | 是 | Base64编码的图像数据 |
| board_orientation | string | 否 | 棋盘方向 ("normal", "rotated")，默认为 "normal" |

##### 请求示例
```http
POST /api/v1/chess/recognize HTTP/1.1
Host: localhost:5000
Content-Type: application/json

{
  "image_data": "base64_encoded_image_data_would_be_here",
  "board_orientation": "normal"
}
```

##### 响应示例
```json
{
  "success": true,
  "board_state": "               \n               \n               \n   defgkgfed   \n   .........   \n   .h.....h.   \n   i.i.i.i.i   \n   .........   \n   .........   \n   P.P.P.P.P   \n   .C.....C.   \n   .........   \n   RNBAKABNR   \n               \n               \n                ",
  "recognized_pieces": [
    {
      "position": 195,
      "piece_type": "R",
      "certainty": "high"
    },
    {
      "position": 196,
      "piece_type": "N",
      "certainty": "high"
    }
  ],
  "message": "Board recognized successfully"
}
```

##### 响应字段
- `success` (boolean): 请求是否成功
- `board_state` (string): 识别出的256字符棋盘状态
- `recognized_pieces` (array): 识别出的棋子列表
  - `position` (number): 棋子位置索引
  - `piece_type` (string): 棋子类型
  - `certainty` (string): 置信度 ("high", "medium", "low")
- `message` (string): 响应消息

## 棋盘状态格式

### 256字符格式
AI引擎使用256字符的字符串表示整个棋盘：
- 每行15个字符 + 1个换行符，共15行
- 最后添加1个空格字符，总计256字符
- 字符含义：
  - `R,N,B,A,K,C,P` - 红方明子
  - `r,n,b,a,k,c,p` - 黑方明子
  - `D,E,F,G,H,I` - 红方暗子
  - `d,e,f,g,h,i` - 黑方暗子
  - `.` - 空位

### 示例
```
'               \n'  # 第 0 行
'               \n'  # 第 1 行
'               \n'  # 第 2 行
'   defgkgfed   \n'  # 第 3 行
'   .........   \n'  # 第 4 行
'   .h.....h.   \n'  # 第 5 行
'   i.i.i.i.i   \n'  # 第 6 行
'   .........   \n'  # 第 7 行
'   .........   \n'  # 第 8 行
'   I.I.I.I.I   \n'  # 第 9 行
'   .H.....H.   \n'  # 第 10 行
'   .........   \n'  # 第 11 行
'   DEFGKGFED   \n'  # 第 12 行
'               \n'  # 第 13 行
'               \n'  # 第 14 行
'               '     # 第 15 行
```

## 错误处理

### 通用错误响应格式
```json
{
  "success": false,
  "error": "错误消息",
  "message": "详细错误信息"
}
```

### 常见错误码
- `400 Bad Request`: 请求参数错误
- `500 Internal Server Error`: 服务器内部错误

## 使用示例

### JavaScript 示例
```javascript
// 获取最佳着法
async function getBestMove(boardState, currentPlayer) {
  const response = await fetch('http://localhost:5000/api/v1/chess/move', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      board_state: boardState,
      current_player: currentPlayer,
      mapping: {},
      captured_pieces: { red: [], black: [] },
      game_history: []
    })
  });
  
  const result = await response.json();
  return result;
}

// 分析棋盘
async function analyzeBoard(boardState, currentPlayer) {
  const response = await fetch('http://localhost:5000/api/v1/chess/analyze', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      board_state: boardState,
      current_player: currentPlayer,
      depth: 3
    })
  });
  
  const result = await response.json();
  return result;
}
```

### Python 示例
```python
import requests
import json

# 获取最佳着法
def get_best_move(board_state, current_player):
    url = "http://localhost:5000/api/v1/chess/move"
    data = {
        "board_state": board_state,
        "current_player": current_player,
        "mapping": {},
        "captured_pieces": {"red": [], "black": []},
        "game_history": []
    }
    
    response = requests.post(url, json=data)
    return response.json()

# 分析棋盘
def analyze_board(board_state, current_player):
    url = "http://localhost:5000/api/v1/chess/analyze"
    data = {
        "board_state": board_state,
        "current_player": current_player,
        "depth": 3
    }
    
    response = requests.post(url, json=data)
    return response.json()
```

## 部署说明

### 依赖安装
```bash
pip install -r server/requirements.txt
```

### 启动服务器
```bash
cd jieqi-ai-sys
python server/start_server.py
```

### 服务器配置
- 默认端口: 5000
- 支持跨域访问 (CORS)
- 自动初始化AI引擎

## 安全考虑

- 生产环境应添加API认证机制
- 应限制API请求频率
- 敏感操作应添加额外验证
- 传输数据应使用HTTPS加密