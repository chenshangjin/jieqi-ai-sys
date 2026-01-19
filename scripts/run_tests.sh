#!/bin/bash

# 运行Python测试的脚本

set -e

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

echo "🧪 Running tests..."

cd "$PROJECT_ROOT"

# Check if pytest is installed
if ! command -v pytest &> /dev/null; then
    echo "❌ pytest not found. Installing..."
    pip install pytest pytest-cov
fi

# Run tests with coverage
pytest tests/ -v --cov=src --cov-report=html --cov-report=term-missing

echo "✅ Tests complete!"
echo "📊 Coverage report: htmlcov/index.html"
