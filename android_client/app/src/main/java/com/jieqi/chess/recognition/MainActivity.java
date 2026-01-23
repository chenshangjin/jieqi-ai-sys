/*
 * 主活动类 - 揭棋AI识别器
 *
 * 代码生成者: Lingma AI助手
 * 生成日期: 2026年1月23日
 */

package com.jieqi.chess.recognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int CAPTURE_IMAGE_REQUEST_CODE = 101;

    private ImageView boardPreview;
    private TextView statusText;
    private TextView resultText;
    private Button captureButton;
    private Button processButton;
    private Button recommendationButton;

    private Bitmap capturedBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化OpenCV库
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV初始化失败");
        } else {
            Log.d(TAG, "OpenCV初始化成功");
        }

        initViews();
        setupClickListeners();
        checkPermissions();
    }

    private void initViews() {
        boardPreview = findViewById(R.id.boardPreview);
        statusText = findViewById(R.id.statusText);
        resultText = findViewById(R.id.resultText);
        captureButton = findViewById(R.id.captureButton);
        processButton = findViewById(R.id.processButton);
        recommendationButton = findViewById(R.id.recommendationButton);
    }

    private void setupClickListeners() {
        captureButton.setOnClickListener(v -> captureScreen());
        processButton.setOnClickListener(v -> processBoard());
        recommendationButton.setOnClickListener(v -> getRecommendation());
    }

    private void checkPermissions() {
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        boolean allGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (!allGranted) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (!allGranted) {
                Toast.makeText(this, "需要授权才能正常使用应用功能", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void captureScreen() {
        statusText.setText(R.string.status_processing);
        resultText.setText("");

        // 启动相机来模拟截图（实际应用中可能需要其他方式获取天天象棋截图）
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAPTURE_IMAGE_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                capturedBitmap = (Bitmap) extras.get("data");
                if (capturedBitmap != null) {
                    boardPreview.setImageBitmap(capturedBitmap);
                    statusText.setText(R.string.status_success);
                    processButton.setEnabled(true);
                }
            }
        }
    }

    private void processBoard() {
        if (capturedBitmap == null) {
            Toast.makeText(this, "请先截取棋盘", Toast.LENGTH_SHORT).show();
            return;
        }

        statusText.setText(R.string.status_processing);

        // 在后台线程中处理图像识别
        new Thread(() -> {
            try {
                // 使用BoardProcessor处理棋盘识别
                BoardProcessor processor = new BoardProcessor();
                String boardState = processor.processBoard(capturedBitmap);

                runOnUiThread(() -> {
                    resultText.setText("识别的棋盘状态:\n" + boardState.substring(0, Math.min(100, boardState.length())) + "...");
                    statusText.setText(R.string.status_success);
                    recommendationButton.setEnabled(true);
                });
            } catch (Exception e) {
                Log.e(TAG, "处理棋盘时出错", e);
                runOnUiThread(() -> {
                    statusText.setText(R.string.status_error);
                    resultText.setText("处理失败: " + e.getMessage());
                });
            }
        }).start();
    }

    private void getRecommendation() {
        if (capturedBitmap == null) {
            Toast.makeText(this, "请先截取并处理棋盘", Toast.LENGTH_SHORT).show();
            return;
        }

        statusText.setText(R.string.status_processing);

        // 在后台线程中获取AI推荐
        new Thread(() -> {
            try {
                // 使用AIServiceClient与服务器通信
                AIServiceClient aiClient = new AIServiceClient();
                String boardState = new BoardProcessor().processBoard(capturedBitmap);
                String recommendation = aiClient.getMoveRecommendation(boardState);

                runOnUiThread(() -> {
                    resultText.setText("AI推荐着法: " + recommendation);
                    statusText.setText("AI推荐获取成功");
                });
            } catch (Exception e) {
                Log.e(TAG, "获取AI推荐时出错", e);
                runOnUiThread(() -> {
                    statusText.setText(R.string.status_error);
                    resultText.setText("获取AI推荐失败: " + e.getMessage());
                });
            }
        }).start();
    }
}