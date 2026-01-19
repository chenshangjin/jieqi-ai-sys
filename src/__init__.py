"""
揭棋AI系统

A Chinese Chess (Jieqi) AI System with search algorithms and GUI.
"""

__version__ = "2.0.0"
__author__ = "chenshangjin"
__license__ = "GPLv3"

from . import ai
from . import board
from . import gui

__all__ = ["ai", "board", "gui"]
