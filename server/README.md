# jieqi-ai-sys Server

这是一个为jieqi-ai-sys项目设计的简单HTTP服务器，用于处理Android客户端的请求并与AI引擎通信。

## 功能

- **棋局分析**: 接收棋盘状态并返回局面分析
- **着法推荐**: 根据当前局面提供最佳着法建议
- **棋盘识别**: （模拟）接收图像数据并识别棋盘状态
- **健康检查**: 服务器状态监控

## API端点

完整的API文档请参见 [API_MANUAL.md](../API_MANUAL.md)。

### GET /health
健康检查端点，返回服务器状态。

**响应示例:**
```json
{
  "status": "healthy",
  "service": "jieqi-ai-sys server",
  "timestamp": 1768813623.305025
}
```

### POST /api/v1/chess/move
获取最佳着法推荐。

**请求体:**
```json
{
  "board_state": "256-character board representation",
  "current_player": "RED|BLACK",
  "mapping": {},
  "captured_pieces": {
    "red": [],
    "black": []
  },
  "game_history": []
}
```

**响应示例:**
```json
{
  "success": true,
  "best_move": {
    "uci_move": "e3e4",  // 实际AI响应，非模拟数据
    "from_position": 151,
    "to_position": 135,
    "eval": -3696,
    "depth": 6,
    "pv": ["e3e4"],
    "confidence": 0.85
  },
  "evaluation": -3696,
  "message": "Move calculated successfully"
}
```

### POST /api/v1/chess/analyze
分析当前棋盘局面。

**请求体:**
```json
{
  "board_state": "256-character board representation",
  "current_player": "RED|BLACK",
  "depth": 5
}
```

### POST /api/v1/chess/recognize
（模拟）从图像识别棋盘状态。

## 启动服务器

```bash
cd jieqi-ai-sys
python server/start_server.py
```

服务器将在 http://localhost:5000 上运行。

## 依赖

- Flask
- Flask-CORS
- requests (用于测试)

## 注意事项

- 服务器已连接到真实的AI引擎，可返回实际的计算结果
- 服务器设计为开发服务器，生产环境需使用WSGI服务器（如Gunicorn）