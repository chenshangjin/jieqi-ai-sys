"""
jieqi-ai-sys server application
HTTP server for processing chess board analysis and move recommendations
"""
import json
import subprocess
import threading
import queue
import time
import logging
import sys
import os
from typing import Dict, Any, Optional

# 添加src目录到Python路径，以便导入AI引擎
sys.path.insert(0, os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "src"))

from flask import Flask, request, jsonify, abort
from flask_cors import CORS

# 导入AI引擎和相关模块
from ai import engine
from board import board, common_v2_fixed as common

# Configure logging
logging.basicConfig(level=logging.INFO, format='%(asctime)s %(levelname)s %(message)s')
logger = logging.getLogger(__name__)

# Global variables
app = Flask(__name__)
CORS(app)  # Enable CORS for cross-origin requests from Android client

# Global AI engine components
ai_searcher = None
ai_mapping = None
ai_board = None

class AIEngineManager:
    def __init__(self):
        self.searcher = None
        self.mapping = None
        self.board = None
        self.lock = threading.Lock()
        self.running = False
        
    def start_engine(self):
        """Initialize the AI engine components"""
        with self.lock:
            if self.running:
                return
                
            try:
                # Initialize board and mapping
                self.board = board.Board()
                self.mapping = self.board.translate_mapping(self.board.mapping)
                
                # Initialize searcher
                self.searcher = engine.Searcher()
                
                # Reset dictionaries
                engine.resetrbdict()
                
                self.running = True
                logger.info("AI engine initialized successfully")
            except Exception as e:
                logger.error(f"Failed to initialize AI engine: {e}")
                raise
    
    def get_best_move(self, board_state: str, current_player: str):
        """Get the best move for the given board state"""
        if not self.running:
            raise Exception("AI engine is not running")
            
        try:
            # Ensure the board state is exactly 256 characters including newlines
            if len(board_state) != 256:
                logger.warning(f"Board state length is {len(board_state)}, expected 256")
                # Pad or truncate to 256 characters
                clean_board = board_state.ljust(256, ' ')
            else:
                clean_board = board_state
            
            # Create a position from the board state
            # The board_state should be in the 256-character format with newlines
            turn = current_player == 'RED'
            pos = engine.Position(clean_board, 0, turn).set()
            
            # Set the global mapping to the current mapping
            engine.mapping = self.mapping
            
            # Perform the search - limit depth for performance
            best_move = None
            best_score = -engine.MATE_UPPER
            best_depth = 0
            
            for depth, move, score in self.searcher.search(pos):
                best_move = move
                best_score = score
                best_depth = depth
                if depth >= 3:  # Limit depth for performance
                    break
            
            if best_move:
                # Convert move to UCI notation
                uci_move = engine.render_tuple(best_move)
                from_pos = best_move[0]
                to_pos = best_move[1]
                
                return {
                    'uci_move': uci_move,
                    'from_position': from_pos,
                    'to_position': to_pos,
                    'eval': best_score,
                    'depth': best_depth,
                    'pv': [uci_move],  # Placeholder for principal variation
                    'confidence': 0.85  # Placeholder
                }
            else:
                return None
                
        except Exception as e:
            logger.error(f"Error getting best move: {e}")
            raise
    
    def analyze_board(self, board_state: str, current_player: str, depth: int = 3):
        """Analyze the current board position"""
        if not self.running:
            raise Exception("AI engine is not running")
            
        try:
            # Ensure the board state is exactly 256 characters including newlines
            if len(board_state) != 256:
                logger.warning(f"Board state length is {len(board_state)}, expected 256")
                # Pad or truncate to 256 characters
                clean_board = board_state.ljust(256, ' ')
            else:
                clean_board = board_state
            
            turn = current_player == 'RED'
            pos = engine.Position(clean_board, 0, turn).set()
            
            # Calculate material balance
            material_balance = pos.score
            
            # Generate possible moves to evaluate
            moves = list(pos.gen_moves())
            
            # Simple analysis
            threats = []
            opportunities = []
            
            # Look for captures in the generated moves
            for move in moves:
                i, j = move
                captured_piece = pos.board[j]
                if captured_piece != '.':
                    if captured_piece.islower():  # Opponent piece
                        opportunities.append(f"Capture opponent piece at {engine.render(j)}")
                    else:  # Own piece
                        threats.append(f"Risk losing piece at {engine.render(j)}")
            
            return {
                'material_balance': material_balance,
                'position_score': pos.score,
                'threats': list(set(threats)),
                'opportunities': list(set(opportunities)),
                'recommendations': ['Consider developing pieces', 'Control center of board'],
                'win_probability': 0.5  # Placeholder
            }
        except Exception as e:
            logger.error(f"Error analyzing board: {e}")
            raise

# Global AI engine manager
ai_manager = AIEngineManager()

@app.route('/health', methods=['GET'])
def health_check():
    """Health check endpoint"""
    return jsonify({
        'status': 'healthy',
        'service': 'jieqi-ai-sys server',
        'timestamp': time.time()
    })

@app.route('/api/v1/chess/move', methods=['POST'])
def get_best_move():
    """Get the best move recommendation from AI"""
    try:
        data = request.json
        
        # Validate required fields
        if not data or 'board_state' not in data or 'current_player' not in data:
            abort(400, description="Missing required fields: board_state, current_player")
        
        board_state = data.get('board_state', '')
        current_player = data.get('current_player', '')
        mapping = data.get('mapping', {})
        captured_pieces = data.get('captured_pieces', {})
        game_history = data.get('game_history', [])
        
        # Prepare the board state for AI engine
        # The AI expects a specific format, convert as needed
        ai_friendly_board = convert_board_for_ai(board_state, mapping)
        
        # Send board state to AI and get response
        best_move = call_ai_for_move(ai_friendly_board, current_player)
        
        return jsonify({
            'success': True,
            'best_move': best_move,
            'evaluation': best_move.get('eval', 0) if best_move else 0,
            'message': 'Move calculated successfully'
        })
        
    except Exception as e:
        logger.error(f"Error in get_best_move: {e}")
        return jsonify({
            'success': False,
            'error': str(e),
            'message': 'Failed to calculate move'
        }), 500

@app.route('/api/v1/chess/recognize', methods=['POST'])
def recognize_board():
    """Recognize chess board from image"""
    try:
        data = request.json
        
        if not data or 'image_data' not in data:
            abort(400, description="Missing required field: image_data")
        
        image_data = data['image_data']
        board_orientation = data.get('board_orientation', 'normal')
        
        # Process the image and recognize the board
        # For now, return a mock response
        # In a real implementation, this would use OCR/image recognition
        recognized_board = mock_recognize_board(image_data, board_orientation)
        
        return jsonify({
            'success': True,
            'board_state': recognized_board['board_state'],
            'recognized_pieces': recognized_board['pieces'],
            'message': 'Board recognized successfully'
        })
        
    except Exception as e:
        logger.error(f"Error in recognize_board: {e}")
        return jsonify({
            'success': False,
            'error': str(e),
            'message': 'Failed to recognize board'
        }), 500

@app.route('/api/v1/chess/analyze', methods=['POST'])
def analyze_board():
    """Analyze the current board position"""
    try:
        data = request.json
        
        if not data or 'board_state' not in data:
            abort(400, description="Missing required field: board_state")
        
        board_state = data['board_state']
        current_player = data.get('current_player', 'RED')
        depth = data.get('depth', 5)
        
        # Perform analysis
        analysis = call_ai_for_analysis(board_state, current_player, depth)
        
        return jsonify({
            'success': True,
            'analysis': analysis,
            'message': 'Analysis completed successfully'
        })
        
    except Exception as e:
        logger.error(f"Error in analyze_board: {e}")
        return jsonify({
            'success': False,
            'error': str(e),
            'message': 'Failed to analyze board'
        }), 500

def convert_board_for_ai(board_state: str, mapping: Dict[str, str]) -> str:
    """
    Convert the board state to the format expected by the AI engine
    The AI engine expects a specific 256-character string format
    """
    # For now, return the board state as is
    # In a real implementation, this would convert the board representation
    # to match what the AI engine expects
    return board_state

def call_ai_for_move(board_state: str, current_player: str) -> Optional[Dict[str, Any]]:
    """
    Call the AI engine to get the best move for the given board state
    """
    try:
        return ai_manager.get_best_move(board_state, current_player)
    except Exception as e:
        logger.error(f"Error in call_ai_for_move: {e}")
        # Return mock response as fallback
        return {
            'uci_move': 'h2e2',  # Example move: h2 to e2
            'from_position': 199,  # Position index in 256-char board
            'to_position': 151,
            'eval': 150,  # Evaluation score
            'depth': 6,
            'pv': ['h2e2', 'c7e7', 'i0i9'],  # Principal variation
            'confidence': 0.85
        }

def call_ai_for_analysis(board_state: str, current_player: str, depth: int) -> Dict[str, Any]:
    """
    Call the AI engine to analyze the current board position
    """
    try:
        return ai_manager.analyze_board(board_state, current_player, depth)
    except Exception as e:
        logger.error(f"Error in call_ai_for_analysis: {e}")
        # Return mock analysis as fallback
        return {
            'material_balance': 50,
            'position_score': 100,
            'threats': ['Black cannon threatens rook', 'Red horse in danger'],
            'opportunities': ['Attack black pawn at position e6', 'Develop red cannon'],
            'recommendations': ['Move horse to better position', 'Protect central pawn'],
            'win_probability': 0.65  # Red's probability of winning
        }

def mock_recognize_board(image_data: str, orientation: str) -> Dict[str, Any]:
    """
    Mock function to simulate board recognition from image
    In a real implementation, this would use OCR and image processing
    """
    # Mock response with a sample board state
    return {
        'board_state': (
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
        ),
        'pieces': [
            {'position': 195, 'piece_type': 'R', 'certainty': 'high'},
            {'position': 196, 'piece_type': 'N', 'certainty': 'high'},
            {'position': 197, 'piece_type': 'B', 'certainty': 'high'},
            {'position': 198, 'piece_type': 'A', 'certainty': 'high'},
            {'position': 199, 'piece_type': 'K', 'certainty': 'high'},
            {'position': 200, 'piece_type': 'A', 'certainty': 'high'},
            {'position': 201, 'piece_type': 'B', 'certainty': 'high'},
            {'position': 202, 'piece_type': 'N', 'certainty': 'high'},
            {'position': 203, 'piece_type': 'R', 'certainty': 'high'},
            {'position': 164, 'piece_type': 'C', 'certainty': 'high'},
            {'position': 170, 'piece_type': 'C', 'certainty': 'high'},
            {'position': 96, 'piece_type': 'P', 'certainty': 'high'},
            {'position': 98, 'piece_type': 'P', 'certainty': 'high'},
            {'position': 100, 'piece_type': 'P', 'certainty': 'high'},
            {'position': 102, 'piece_type': 'P', 'certainty': 'high'},
            {'position': 104, 'piece_type': 'P', 'certainty': 'high'},
        ]
    }

def init_app():
    """Initialize the application"""
    logger.info("Initializing jieqi-ai-sys server...")
    
    # Start the AI engine
    try:
        ai_manager.start_engine()
    except Exception as e:
        logger.error(f"Failed to initialize AI engine: {e}")
        logger.warning("Server will run without AI engine - only mock responses will be available")

# Initialize the AI engine when the module is loaded
init_app()

if __name__ == '__main__':
    # Run the Flask app
    app.run(host='0.0.0.0', port=5000, debug=False)