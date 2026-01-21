#!/usr/bin/env python3
"""
Start script for jieqi-ai-sys server
"""
import os
import sys
from app import app

def main():
    print("Starting jieqi-ai-sys server...")
    print("Loading AI engine...")
    
    # Change to the project root directory
    project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    os.chdir(project_root)
    
    print(f"Working directory set to: {os.getcwd()}")
    print("Server is ready! Access it at http://localhost:5000")
    print("Available endpoints:")
    print("  GET  /health - Health check")
    print("  POST /api/v1/chess/move - Get best move")
    print("  POST /api/v1/chess/recognize - Recognize board from image")
    print("  POST /api/v1/chess/analyze - Analyze board position")
    
    try:
        app.run(host='0.0.0.0', port=5000, debug=False)
    except KeyboardInterrupt:
        print("\nShutting down server...")
        # Cleanup would go here if needed
        sys.exit(0)

if __name__ == '__main__':
    main()