"""
Test client for jieqi-ai-sys server
"""
import requests
import json

BASE_URL = 'http://localhost:5000'

def test_health():
    """Test the health endpoint"""
    try:
        response = requests.get(f'{BASE_URL}/health')
        print("Health Check Response:")
        print(json.dumps(response.json(), indent=2))
        print(f"Status Code: {response.status_code}")
    except Exception as e:
        print(f"Error testing health endpoint: {e}")

def test_get_move():
    """Test the move recommendation endpoint"""
    try:
        data = {
            "board_state": (
                '               \n'  # 0
                '               \n'  # 1
                '               \n'  # 2
                '   defgkgfed   \n'  # 3
                '   .........   \n'  # 4
                '   .h.....h.   \n'  # 5
                '   i.i.i.i.i   \n'  # 6
                '   .........   \n'  # 7
                '   .........   \n'  # 8
                '   I.I.I.I.I   \n'  # 9
                '   .H.....H.   \n'  # 10
                '   .........   \n'  # 11
                '   DEFGKGFED   \n'  # 12
                '               \n'  # 13
                '               \n'  # 14
                '               '  # 15
            ) + ' ',  # Add the 256th character
            "current_player": "RED",
            "mapping": {},
            "captured_pieces": {
                "red": [],
                "black": []
            },
            "game_history": []
        }
        
        response = requests.post(f'{BASE_URL}/api/v1/chess/move', 
                                 json=data, 
                                 headers={'Content-Type': 'application/json'})
        print("\nGet Move Response:")
        print(json.dumps(response.json(), indent=2))
        print(f"Status Code: {response.status_code}")
    except Exception as e:
        print(f"Error testing move endpoint: {e}")

def test_analyze_board():
    """Test the board analysis endpoint"""
    try:
        data = {
            "board_state": (
                '               \n'  # 0
                '               \n'  # 1
                '               \n'  # 2
                '   defgkgfed   \n'  # 3
                '   .........   \n'  # 4
                '   .h.....h.   \n'  # 5
                '   i.i.i.i.i   \n'  # 6
                '   .........   \n'  # 7
                '   .........   \n'  # 8
                '   I.I.I.I.I   \n'  # 9
                '   .H.....H.   \n'  # 10
                '   .........   \n'  # 11
                '   DEFGKGFED   \n'  # 12
                '               \n'  # 13
                '               \n'  # 14
                '               '  # 15
            ) + ' ',  # Add the 256th character
            "current_player": "RED",
            "depth": 5
        }
        
        response = requests.post(f'{BASE_URL}/api/v1/chess/analyze', 
                                 json=data, 
                                 headers={'Content-Type': 'application/json'})
        print("\nAnalyze Board Response:")
        print(json.dumps(response.json(), indent=2))
        print(f"Status Code: {response.status_code}")
    except Exception as e:
        print(f"Error testing analyze endpoint: {e}")

def test_recognize_board():
    """Test the board recognition endpoint"""
    try:
        data = {
            "image_data": "base64_encoded_image_data_would_be_here",
            "board_orientation": "normal"
        }
        
        response = requests.post(f'{BASE_URL}/api/v1/chess/recognize', 
                                 json=data, 
                                 headers={'Content-Type': 'application/json'})
        print("\nRecognize Board Response:")
        print(json.dumps(response.json(), indent=2))
        print(f"Status Code: {response.status_code}")
    except Exception as e:
        print(f"Error testing recognize endpoint: {e}")

if __name__ == "__main__":
    print("Testing jieqi-ai-sys server endpoints...\n")
    
    test_health()
    test_get_move()
    test_analyze_board()
    test_recognize_board()
    
    print("\nTesting completed.")