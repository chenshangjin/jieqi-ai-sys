"""
Setup configuration for Jieqi AI System
"""

from setuptools import setup, find_packages

with open("README.md", "r", encoding="utf-8") as f:
    long_description = f.read()

setup(
    name="jieqi-ai",
    version="2.0.0",
    author="chenshangjin",
    author_email="your-email@example.com",
    description="A Chinese Chess (Jieqi) AI System with GUI and search algorithms",
    long_description=long_description,
    long_description_content_type="text/markdown",
    url="https://github.com/your-username/jieqi-ai-sys",
    project_urls={
        "Bug Tracker": "https://github.com/your-username/jieqi-ai-sys/issues",
        "Documentation": "https://github.com/your-username/jieqi-ai-sys/docs",
        "Source Code": "https://github.com/your-username/jieqi-ai-sys",
    },
    packages=find_packages(where="src"),
    package_dir={"": "src"},
    classifiers=[
        "Development Status :: 4 - Beta",
        "Intended Audience :: Developers",
        "Topic :: Games/Entertainment :: Board Games",
        "License :: OSI Approved :: GNU General Public License v3 (GPLv3)",
        "Programming Language :: Python :: 3",
        "Programming Language :: Python :: 3.8",
        "Programming Language :: Python :: 3.9",
        "Programming Language :: Python :: 3.10",
        "Programming Language :: Python :: 3.11",
        "Programming Language :: Python :: 3.12",
    ],
    python_requires=">=3.8",
    install_requires=[
        "pygame>=2.0.0,<3.0.0",
        "numpy>=1.20.0",
    ],
    entry_points={
        "console_scripts": [
            "jieqi-gui=src.gui.main:main",
            "jieqi-ai=src.ai.engine:main",
        ],
    },
)
