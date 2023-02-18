package com.example.youtubelivewidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
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

        String liveUrl;

        //YoutubeAsyncTask(context, context.getResources().getString(R.string.liver_TokinoSora_id), views, R.id.imageView_01, appWidgetId).execute();
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
            String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&channelId=" + channelId + "&type=video&eventType=live&key=" + API_KEY;
            boolean channelIsLive = false;

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
                    int totalResults = response.getJSONObject("pageInfo").getInt("totalResults");
                    if (totalResults > 0) {
                        Log.d("YoutubeLiveWidget", "配信中");
                        channelIsLive = true;
                        liveUrl = "https://www.youtube.com/watch?v=" + response.getJSONArray("items").getJSONObject(0).getJSONObject("id").getString("videoId");
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
                Intent youtubeLiveIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(liveUrl));
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, youtubeLiveIntent, 0);
                views.setOnClickPendingIntent(imageViewId, pendingIntent);
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
        int updatePeriodMillis = 30;//何分枚処理（30分）
        updatePeriodMillis *= 60 * 1000;
        for (int appWidgetId : appWidgetIds) {

            Intent intent = new Intent(context, YoutubeLiveWidget.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), updatePeriodMillis, pendingIntent);


            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_design);
            new YoutubeAsyncTask(context, context.getResources().getString(R.string.liver_TokinoSora_id), views, R.id.imageView_01, appWidgetId).execute();
            new YoutubeAsyncTask(context, context.getResources().getString(R.string.liver_Robocosan_id), views, R.id.imageView_02, appWidgetId).execute();
            new YoutubeAsyncTask(context, context.getResources().getString(R.string.liver_AZKi_id), views, R.id.imageView_03, appWidgetId).execute();
            new YoutubeAsyncTask(context, context.getResources().getString(R.string.liver_SakuraMiko_id), views, R.id.imageView_04, appWidgetId).execute();
            new YoutubeAsyncTask(context, context.getResources().getString(R.string.liver_HoshimachiSuisei_id), views, R.id.imageView_05, appWidgetId).execute();
            new YoutubeAsyncTask(context, context.getResources().getString(R.string.liver_YozoraMel_id), views, R.id.imageView_06, appWidgetId).execute();
            new YoutubeAsyncTask(context, context.getResources().getString(R.string.liver_AkiRosenthal_id), views, R.id.imageView_07, appWidgetId).execute();
            new YoutubeAsyncTask(context, context.getResources().getString(R.string.liver_AkaiHaato_id), views, R.id.imageView_08, appWidgetId).execute();
            new YoutubeAsyncTask(context, context.getResources().getString(R.string.liver_ShirakamiFubuki_id), views, R.id.imageView_09, appWidgetId).execute();
            new YoutubeAsyncTask(context, context.getResources().getString(R.string.liver_NatsuiroMatsuri_id), views, R.id.imageView_10, appWidgetId).execute();
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onDisabled(Context context) {

    }
}
