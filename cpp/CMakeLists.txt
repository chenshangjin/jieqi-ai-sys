cmake_minimum_required(VERSION 3.15)

project(jieqi-ai-cpp)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_FLAGS "-O2 -g -Wall -Wextra")
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin)

# Include directories
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include)

# Source files
aux_source_directory(src/global/ DIR_SRCS)
aux_source_directory(src/score/ DIR_SRCS)
aux_source_directory(src/board/ DIR_SRCS)
aux_source_directory(src/ DIR_SRCS)

# Executable
add_executable(jieqi-ai ${DIR_SRCS})

# Optional: Show debug info
message(STATUS "C++ Standard: ${CMAKE_CXX_STANDARD}")
message(STATUS "Build type: ${CMAKE_BUILD_TYPE}")
