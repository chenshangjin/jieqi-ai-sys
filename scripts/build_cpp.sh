#!/bin/bash

# 编译C++版本的脚本

set -e

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
CPP_DIR="$PROJECT_ROOT/cpp"
BUILD_DIR="$CPP_DIR/build"

echo "🏗️  Building Jieqi AI C++ version..."

# Create build directory
mkdir -p "$BUILD_DIR"
cd "$BUILD_DIR"

# Configure with CMake
echo "📋 Configuring..."
cmake .. -DCMAKE_BUILD_TYPE=Release

# Build
echo "🔨 Compiling..."
make -j$(nproc)

echo "✅ Build complete!"
echo "📦 Binary location: $BUILD_DIR/bin/jieqi-ai"
echo ""
echo "To run:"
echo "  $BUILD_DIR/bin/jieqi-ai"
