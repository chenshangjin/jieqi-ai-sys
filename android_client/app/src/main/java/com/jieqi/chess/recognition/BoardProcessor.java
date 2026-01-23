/*
 * 棋盘处理器 - 处理棋盘图像识别
 *
 * 代码生成者: Lingma AI助手
 * 生成日期: 2026年1月23日
 */

package com.jieqi.chess.recognition;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoardProcessor {
    private static final String TAG = "BoardProcessor";

    /**
     * 处理棋盘图像并返回256字符格式的棋盘状态
     */
    public String processBoard(Bitmap bitmap) {
        Mat srcMat = new Mat();
        Utils.bitmapToMat(bitmap, srcMat);

        try {
            // 检测棋盘
            Mat boardMat = detectBoard(srcMat);
            
            if (boardMat != null) {
                // 识别棋子
                char[][] boardArray = recognizePieces(boardMat);
                
                // 转换为256字符格式
                return convertTo256Format(boardArray);
            } else {
                Log.e(TAG, "未能检测到棋盘");
                // 返回空棋盘作为备选
                return generateEmptyBoard();
            }
        } finally {
            srcMat.release();
        }
    }

    /**
     * 检测棋盘区域
     */
    private Mat detectBoard(Mat src) {
        Mat gray = new Mat();
        Mat blurred = new Mat();
        Mat edges = new Mat();
        Mat temp = new Mat();

        try {
            // 转换为灰度图
            Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGBA2GRAY);
            
            // 高斯模糊
            Imgproc.GaussianBlur(gray, blurred, new org.opencv.core.Size(5, 5), 0);
            
            // 边缘检测
            Imgproc.Canny(blurred, edges, 50, 150);
            
            // 形态学操作增强直线
            Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new org.opencv.core.Size(3, 3));
            Imgproc.morphologyEx(edges, temp, Imgproc.MORPH_CLOSE, kernel);
            
            // 查找轮廓
            List<org.opencv.core.MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(temp, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
            
            // 寻找最大的四边形轮廓（棋盘边界）
            Point[] largestRect = findLargestQuadrilateral(contours, src.size());
            
            if (largestRect != null && isValidChessboard(largestRect)) {
                // 透视变换校正棋盘
                return perspectiveTransform(src, largestRect);
            } else {
                Log.e(TAG, "未找到有效的棋盘轮廓");
                return null;
            }
        } finally {
            gray.release();
            blurred.release();
            edges.release();
            temp.release();
        }
    }

    /**
     * 寻找最大的四边形轮廓
     */
    private Point[] findLargestQuadrilateral(List<org.opencv.core.MatOfPoint> contours, org.opencv.core.Size imageSize) {
        double maxArea = 0;
        Point[] largestRect = null;
        
        for (org.opencv.core.MatOfPoint contour : contours) {
            // 近似轮廓为多边形
            org.opencv.core.MatOfPoint2f contour2f = new org.opencv.core.MatOfPoint2f(contour.toArray());
            org.opencv.core.MatOfPoint2f approxCurve = new org.opencv.core.MatOfPoint2f();
            
            double peri = Imgproc.arcLength(contour2f, true);
            Imgproc.approxPolyDP(contour2f, approxCurve, 0.04 * peri, true);
            
            Point[] points = approxCurve.toArray();
            
            // 检查是否为四边形
            if (points.length == 4) {
                double area = Math.abs(Imgproc.contourArea(contour2f));
                // 检查面积是否合理（至少占图像的10%）
                double minArea = imageSize.area() * 0.1;
                if (area > maxArea && area > minArea) {
                    maxArea = area;
                    largestRect = points;
                }
            }
            
            contour2f.release();
            approxCurve.release();
        }
        
        return largestRect;
    }

    /**
     * 验证是否为有效的象棋棋盘
     */
    private boolean isValidChessboard(Point[] corners) {
        if (corners == null || corners.length != 4) {
            return false;
        }
        
        // 检查大致的长宽比（中国象棋棋盘比例约为9:10）
        double width = distance(corners[0], corners[1]);
        double height = distance(corners[1], corners[2]);
        double ratio = width / height;
        
        // 中国象棋棋盘的比例约为 9:10 = 0.9
        return ratio > 0.6 && ratio < 1.2;
    }

    /**
     * 计算两点间距离
     */
    private double distance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    /**
     * 透视变换校正图像
     */
    private Mat perspectiveTransform(Mat src, Point[] srcPoints) {
        // 将四个角点按顺序排列（左上、右上、右下、左下）
        Point[] orderedPoints = orderCorners(srcPoints);
        
        // 定义目标棋盘尺寸
        int width = 900;  // 标准棋盘宽度
        int height = 1000; // 标准棋盘高度
        
        // 目标点坐标
        Point[] dstPoints = {
            new Point(0, 0),           // 左上
            new Point(width - 1, 0),   // 右上
            new Point(width - 1, height - 1), // 右下
            new Point(0, height - 1)   // 左下
        };
        
        org.opencv.core.MatOfPoint2f srcMat = new org.opencv.core.MatOfPoint2f(orderedPoints);
        org.opencv.core.MatOfPoint2f dstMat = new org.opencv.core.MatOfPoint2f(dstPoints);
        
        Mat transformMatrix = Imgproc.getPerspectiveTransform(srcMat, dstMat);
        
        Mat corrected = new Mat(height, width, src.type());
        Imgproc.warpPerspective(src, corrected, transformMatrix, corrected.size());
        
        srcMat.release();
        dstMat.release();
        transformMatrix.release();
        
        return corrected;
    }

    /**
     * 将四个角点按顺序排列（左上、右上、右下、左下）
     */
    private Point[] orderCorners(Point[] corners) {
        Point[] ordered = new Point[4];
        
        // 计算所有点的中心
        double cx = 0, cy = 0;
        for (Point p : corners) {
            cx += p.x;
            cy += p.y;
        }
        cx /= 4;
        cy /= 4;
        
        // 根据相对于中心点的位置排序
        for (Point p : corners) {
            if (p.x < cx && p.y < cy) {
                ordered[0] = p; // 左上
            } else if (p.x >= cx && p.y < cy) {
                ordered[1] = p; // 右上
            } else if (p.x >= cx && p.y >= cy) {
                ordered[2] = p; // 右下
            } else {
                ordered[3] = p; // 左下
            }
        }
        
        return ordered;
    }

    /**
     * 识别棋盘上每个位置的棋子
     */
    public char[][] recognizePieces(Mat boardImage) {
        char[][] boardState = new char[10][9]; // 10行9列的中国象棋棋盘
        
        // 初始化为空
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 9; j++) {
                boardState[i][j] = '.';
            }
        }
        
        // 将棋盘划分为10x9的网格
        double cellWidth = boardImage.width() / 9.0;
        double cellHeight = boardImage.height() / 10.0;
        
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 9; col++) {
                // 提取每个网格的图像
                Rect roi = new Rect(
                    (int)(col * cellWidth),
                    (int)(row * cellHeight),
                    (int)cellWidth,
                    (int)cellHeight
                );
                
                Mat cell = new Mat(boardImage, roi);
                
                // 识别该网格中的棋子
                char piece = recognizePiece(cell, row, col);
                boardState[row][col] = piece;
                
                cell.release(); // 释放Mat资源
            }
        }
        
        return boardState;
    }

    /**
     * 识别单个网格中的棋子
     */
    private char recognizePiece(Mat cell, int row, int col) {
        // 检查是否为空格
        if (isEmptyCell(cell)) {
            return '.';
        }
        
        // 分析颜色特征判断红黑
        boolean isRed = isRedPiece(cell);
        
        // 分析形状特征判断棋子类型
        char pieceType = recognizePieceType(cell);
        
        // 根据颜色和类型返回对应字符
        if (isRed) {
            return Character.toUpperCase(pieceType);
        } else {
            return Character.toLowerCase(pieceType);
        }
    }

    /**
     * 检查网格是否为空
     */
    private boolean isEmptyCell(Mat cell) {
        // 简单的空格检测：检查平均亮度
        Mat hsv = new Mat();
        Mat gray = new Mat();
        
        try {
            // 转换到HSV空间
            Imgproc.cvtColor(cell, hsv, Imgproc.COLOR_RGB2HSV);
            
            // 计算平均亮度（V通道）
            org.opencv.core.Core.extractChannel(hsv, gray, 2); // V channel
            
            Scalar mean = Core.mean(gray);
            double avgBrightness = mean.val[0];
            
            // 如果平均亮度较高，可能是背景
            // 阈值可能需要根据实际情况调整
            return avgBrightness > 180;
        } finally {
            hsv.release();
            gray.release();
        }
    }

    /**
     * 检查棋子是否为红色
     */
    private boolean isRedPiece(Mat cell) {
        Mat hsv = new Mat();
        Mat redMask = new Mat();
        
        try {
            // 转换到HSV颜色空间
            Imgproc.cvtColor(cell, hsv, Imgproc.COLOR_RGB2HSV);
            
            // 定义红色范围HSV值
            Scalar lowerRed1 = new Scalar(0, 50, 50);
            Scalar upperRed1 = new Scalar(10, 255, 255);
            Scalar lowerRed2 = new Scalar(170, 50, 50);
            Scalar upperRed2 = new Scalar(180, 255, 255);
            
            Mat mask1 = new Mat();
            Mat mask2 = new Mat();
            
            // 创建两个红色范围的掩码
            Core.inRange(hsv, lowerRed1, upperRed1, mask1);
            Core.inRange(hsv, lowerRed2, upperRed2, mask2);
            
            // 合并两个掩码
            Core.add(mask1, mask2, redMask);
            
            // 计算红色像素占比
            double totalPixels = cell.rows() * cell.cols();
            double redPixels = Core.countNonZero(redMask);
            double ratio = redPixels / totalPixels;
            
            // 如果超过一定比例是红色，则认为是红子
            return ratio > 0.05; // 5%阈值
        } finally {
            hsv.release();
            redMask.release();
        }
    }

    /**
     * 识别棋子类型（简化版）
     */
    private char recognizePieceType(Mat cell) {
        // 这是一个简化的棋子类型识别实现
        // 在实际应用中，可能需要更复杂的特征提取或机器学习模型
        
        // 基于棋子的形状和纹理特征进行识别
        // 这里我们使用一些基本的形状分析
        
        Mat gray = new Mat();
        Mat binary = new Mat();
        
        try {
            Imgproc.cvtColor(cell, gray, Imgproc.COLOR_RGB2GRAY);
            Imgproc.threshold(gray, binary, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
            
            // 查找轮廓
            List<org.opencv.core.MatOfPoint> contours = new ArrayList<>();
            Mat hierarchy = new Mat();
            Imgproc.findContours(binary, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
            
            // 简单的分类逻辑：根据轮廓数量和形状
            if (contours.isEmpty()) {
                return 'U'; // Unknown
            }
            
            // 对于中国象棋，我们可以基于轮廓的特征进行分类
            // 这里返回一个默认值，实际实现需要更复杂的逻辑
            return 'P'; // 默认为兵/卒，实际应根据特征判断
        } finally {
            gray.release();
            binary.release();
        }
    }

    /**
     * 将二维棋盘数组转换为256字符格式
     */
    private String convertTo256Format(char[][] board) {
        StringBuilder sb = new StringBuilder();
        
        // 按照AI引擎期望的格式构建256字符字符串
        for (int i = 0; i < 16; i++) {
            if (i < 15) {
                // 每行15个字符
                for (int j = 0; j < 15; j++) {
                    if (i >= 3 && i <= 11 && j >= 3 && j <= 11) {
                        // 棋盘区域 (3-11行，3-11列)
                        int row = i - 3; // 对应棋盘的行 (0-9)
                        int col = j - 3; // 对应棋盘的列 (0-8)
                        
                        if (row < 10 && col < 9) {
                            sb.append(board[row][col]);
                        } else {
                            sb.append(' ');
                        }
                    } else {
                        // 非棋盘区域
                        sb.append(' ');
                    }
                }
                sb.append('\n');
            } else {
                // 最后一行只添加空格
                for (int j = 0; j < 15; j++) {
                    sb.append(' ');
                }
            }
        }
        
        // 添加第256个字符
        sb.append(' ');
        
        return sb.toString();
    }

    /**
     * 生成空棋盘作为默认值
     */
    private String generateEmptyBoard() {
        char[][] board = new char[10][9];
        for (int i = 0; i < 10; i++) {
            Arrays.fill(board[i], '.');
        }
        return convertTo256Format(board);
    }
}