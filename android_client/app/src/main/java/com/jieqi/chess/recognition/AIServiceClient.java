/*
 * AI服务客户端 - 与AI服务器通信
 *
 * 代码生成者: Lingma AI助手
 * 生成日期: 2026年1月23日
 */

package com.jieqi.chess.recognition;

import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AIServiceClient {
    private static final String TAG = "AIServiceClient";
    private static final String BASE_URL = "http://192.168.13.154:5000"; // 请根据实际服务器地址修改
    
    private final OkHttpClient client;

    public AIServiceClient() {
        this.client = new OkHttpClient();
    }

    /**
     * 获取AI推荐的着法
     */
    public String getMoveRecommendation(String boardState) throws Exception {
        String url = BASE_URL + "/api/v1/chess/move";
        
        // 构建请求体
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("board_state", boardState);
        requestBody.addProperty("current_player", "RED"); // 假设当前是红方
        
        JsonObject mapping = new JsonObject();
        requestBody.add("mapping", mapping);
        
        JsonObject capturedPieces = new JsonObject();
        capturedPieces.add("red", new com.google.gson.JsonArray());
        capturedPieces.add("black", new com.google.gson.JsonArray());
        requestBody.add("captured_pieces", capturedPieces);
        
        com.google.gson.JsonArray gameHistory = new com.google.gson.JsonArray();
        requestBody.add("game_history", gameHistory);

        RequestBody body = RequestBody.create(
            MediaType.get("application/json; charset=utf-8"),
            requestBody.toString()
        );

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("请求失败: " + response.code() + " " + response.message());
            }

            String responseBody = response.body().string();
            Log.d(TAG, "AI响应: " + responseBody);
            
            // 解析响应
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();
            
            if (jsonResponse.has("success") && jsonResponse.get("success").getAsBoolean()) {
                JsonObject bestMove = jsonResponse.getAsJsonObject("best_move");
                if (bestMove != null && bestMove.has("uci_move")) {
                    return bestMove.get("uci_move").getAsString();
                } else {
                    throw new Exception("AI响应中未包含有效的着法信息");
                }
            } else {
                String errorMsg = jsonResponse.has("message") ? 
                    jsonResponse.get("message").getAsString() : "未知错误";
                throw new Exception("AI服务错误: " + errorMsg);
            }
        }
    }

    /**
     * 分析当前棋盘局面
     */
    public String analyzeBoard(String boardState) throws Exception {
        String url = BASE_URL + "/api/v1/chess/analyze";
        
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("board_state", boardState);
        requestBody.addProperty("current_player", "RED");
        requestBody.addProperty("depth", 3);

        RequestBody body = RequestBody.create(
            MediaType.get("application/json; charset=utf-8"),
            requestBody.toString()
        );

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("请求失败: " + response.code() + " " + response.message());
            }

            String responseBody = response.body().string();
            Log.d(TAG, "局面分析响应: " + responseBody);
            
            return responseBody;
        }
    }

    /**
     * 测试服务器连接
     */
    public boolean testConnection() {
        String url = BASE_URL + "/health";
        
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        } catch (IOException e) {
            Log.e(TAG, "连接测试失败", e);
            return false;
        }
    }
}