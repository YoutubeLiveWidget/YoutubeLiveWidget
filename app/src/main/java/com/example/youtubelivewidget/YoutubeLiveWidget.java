package com.example.youtubelivewidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class YoutubeLiveWidget extends AppWidgetProvider {

    private static class YoutubeAsyncTask extends AsyncTask<Void, Void, Boolean> {

        private String channelId;
        private RemoteViews views;
        private int imageViewId;
        private Context context;
        private final int appWidgetId;

        YoutubeAsyncTask(Context context, String channelId, RemoteViews views, int imageViewId, int appWidgetId) {
            this.context = context;
            this.views = views;
            this.imageViewId = imageViewId;
            this.channelId = channelId;
            this.appWidgetId = appWidgetId;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Log.d("YoutubeLiveWidget", "doInBackground running");

            //YoutubeAPIの初期設定
            String API_KEY = BuildConfig.YOUTUBE_API_KEY;
            String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + channelId + "&maxResults=1&fields=items/snippet/liveBroadcastContent&key=" + API_KEY;
            Boolean channelIsLive = false;

            //ライブ配信中かどうかを判断する
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    Log.d("response", "200");
                    InputStream inputStream = connection.getInputStream();
                    StringBuilder stringBuilder = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    JSONObject response = new JSONObject(stringBuilder.toString());
                    String totalResults = response.getJSONArray("items").getJSONObject(0).getJSONObject("snippet").getString("liveBroadcastContent");
                    if (totalResults.equals("live")) {
                        Log.d("YoutubeLiveWidget", "配信中");
                        channelIsLive = true;
                    } else {
                        Log.d("YoutubeLiveWidget", "配信していません");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return channelIsLive;
        }

        @Override
        protected void onPostExecute(Boolean channelIsLive) {
            super.onPostExecute(channelIsLive);
//            Log.d("画像透過処理", "実行");
            //ライブ配信中なら透過度を上げる
            if (channelIsLive) {
                views.setInt(imageViewId, "setAlpha", 255);
            } else {
                // 配信されていない場合は、背景色を白色にする
                views.setInt(imageViewId, "setAlpha", 100);
            }

            // アプリウィジェットマネージャーを使用して更新する
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_design);
            new YoutubeAsyncTask(context, context.getResources().getString(R.string.liver_TokinoSora_id), views, R.id.imageView_01, appWidgetId).execute();
//            new YoutubeAsyncTask(context, context.getResources().getString(R.string.liver_Robocosan_id), views, R.id.imageView_02, appWidgetId).execute();
//            new YoutubeAsyncTask(context, context.getResources().getString(R.string.liver_YozoraMel_id), views, R.id.imageView_03, appWidgetId).execute();
//            new YoutubeAsyncTask(context, context.getResources().getString(R.string.liver_AkiRosenthal_id), views, R.id.imageView_04, appWidgetId).execute();
//            new YoutubeAsyncTask(context, context.getResources().getString(R.string.liver_AkaiHaato_id), views, R.id.imageView_05, appWidgetId).execute();
//            new YoutubeAsyncTask(context, context.getResources().getString(R.string.liver_ShirakamiFubuki_id), views, R.id.imageView_06, appWidgetId).execute();
//            new YoutubeAsyncTask(context, context.getResources().getString(R.string.liver_NatsuiroMatsuri_id), views, R.id.imageView_07, appWidgetId).execute();
//            new YoutubeAsyncTask(context, context.getResources().getString(R.string.liver_MinatoAqua_id), views, R.id.imageView_08, appWidgetId).execute();
//            new YoutubeAsyncTask(context, context.getResources().getString(R.string.liver_MurasakiShion_id), views, R.id.imageView_09, appWidgetId).execute();
//            new YoutubeAsyncTask(context, context.getResources().getString(R.string.liver_NakiriAyame_id), views, R.id.imageView_10, appWidgetId).execute();
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onDisabled(Context context) {

    }
}
