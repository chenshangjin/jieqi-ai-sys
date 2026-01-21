"""
Test script to verify AI integration with the server
"""
import sys
import os

# 添加src目录到Python路径
sys.path.insert(0, os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "src"))

import sys
sys.path.append('.')
from server.app import ai_manager, call_ai_for_move, call_ai_for_analysis
from board import board

def test_ai_integration():
    print("Testing AI integration...")
    
    # 初始化AI引擎
    try:
        ai_manager.start_engine()
        print("✓ AI engine initialized successfully")
    except Exception as e:
        print(f"✗ Failed to initialize AI engine: {e}")
        return
    
    # 测试初始棋盘状态
    initial_board = (
        '               \n'  # 0
        '               \n'  # 1
        '               \n'  # 2
        '   defgkgfed   \n'  # 3
        '   .........   \n'  # 4
        '   .h.....h.   \n'  # 5
        '   i.i.i.i.i   \n'  # 6
        '   .........   \n'  # 7
        '   .........   \n'  # 8
        '   P.P.P.P.P   \n'  # 9
        '   .C.....C.   \n'  # 10
        '   .........   \n'  # 11
        '   RNBAKABNR   \n'  # 12
        '               \n'  # 13
        '               \n'  # 14
        '                '  # 15
    )
    
    print("\nTesting get_best_move...")
    try:
        result = call_ai_for_move(initial_board, 'RED')
        print(f"✓ Best move calculated: {result}")
    except Exception as e:
        print(f"✗ Error getting best move: {e}")
    
    print("\nTesting analyze_board...")
    try:
        result = call_ai_for_analysis(initial_board, 'RED', 3)
        print(f"✓ Board analysis: {result}")
    except Exception as e:
        print(f"✗ Error analyzing board: {e}")
    
    print("\nAI integration test completed!")

if __name__ == "__main__":
    test_ai_integration()