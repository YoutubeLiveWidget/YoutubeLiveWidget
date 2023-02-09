package com.example.youtubelivewidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchListResponse;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;

public class YoutubeLiveWidget extends AppWidgetProvider {

    private static class YoutubeAsyncTask extends AsyncTask<Void, Void, String> {
        private final RemoteViews views;
        private final AppWidgetManager appWidgetManager;
        private final int appWidgetId;

        String ImageURL;

        YoutubeAsyncTask(RemoteViews views, AppWidgetManager appWidgetManager, int appWidgetId) {
            this.views = views;
            this.appWidgetManager = appWidgetManager;
            this.appWidgetId = appWidgetId;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Log.d("hello", "world");
                //YoutubeAPIの初期設定
                YouTubeRequestInitializer initializer = new YouTubeRequestInitializer("");
                YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new GsonFactory(), request -> {}).setYouTubeRequestInitializer(initializer).build();
                //特定のチャンネルのデータを取得
                YouTube.Search.List search = youtube.search().list(Collections.singletonList("id,snippet"));
                search.setType(Collections.singletonList("video"));
                search.setEventType("live");
                search.setMaxResults(1L);
                search.setChannelId("UC0g1AE0DOjBYnLhkgoRWN1w");
                SearchListResponse searchResponse = search.execute();
                //ライブ配信中かどうかを判断する
                if (searchResponse.getPageInfo().getTotalResults() > 0) {
                    //ライブ配信中なら配信者のアイコン画像を貼り付ける
                    YouTube.Channels.List channelList = youtube.channels().list(Collections.singletonList("snippet"));
                    channelList.setId(Collections.singletonList("UC0g1AE0DOjBYnLhkgoRWN1w"));
                    ChannelListResponse channelResponse = channelList.execute();
                    Channel targetChannel = channelResponse.getItems().get(0);
                    String urlText = targetChannel.getSnippet().getThumbnails().getHigh().getUrl();
                    URL url = new URL(urlText);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    views.setImageViewBitmap(R.id.imageView, BitmapFactory.decodeStream(connection.getInputStream()));
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                }

                return "";
            } catch (Exception e){

                Log.d("Error", e.toString());
                return ImageURL = e.getStackTrace().toString();
            }
        }
    }

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_design);
        new YoutubeAsyncTask(views, appWidgetManager, appWidgetId).execute();
        Log.d("WIDGET", "Widget updated!");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            try {
                updateAppWidget(context, appWidgetManager, appWidgetId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onDisabled(Context context) {

    }
}
