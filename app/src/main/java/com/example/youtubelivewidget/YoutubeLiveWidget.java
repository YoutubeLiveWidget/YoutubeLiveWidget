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
import java.util.stream.Collectors;

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
                //ラミィのチャンネルのデータを取得
                SearchListResponse searchListResponse = youtube.search().list(Collections.singletonList("snippet")).setChannelId("UCC2jToWuZRpMhyYyTt0xJYw").execute();

//                VideoListResponse videoListResponse = youtube.videos().list(Collections.singletonList("snippet")).setId(Collections.singletonList("")).execute();
//                Stream<Video> temp = videoListResponse.getItems().stream().filter(video -> video.getSnippet().getLiveBroadcastContent() == "live");
                Log.d("Live Broadcast",searchListResponse.getItems().stream().map(searchResult -> searchResult.getSnippet().getTitle()).collect(Collectors.joining(",")));
                String liveData = String.valueOf(searchListResponse.getItems().stream().filter(searchResult -> searchResult.getSnippet().getLiveBroadcastContent() == "live"));
                Log.d("Live Broadcast", liveData);
//                Log.d("Live Broadcast", temp.map(video -> video.getSnippet().getTitle()).collect(Collectors.joining(",")));

                YouTube.Channels.List channelList = youtube.channels().list(Collections.singletonList("snippet"));
                channelList.setId(Collections.singletonList("UCFKOVgVbGmX65RxO3EtH3iw"));
                ChannelListResponse channelResponse = channelList.execute();
                Channel targetChannel = channelResponse.getItems().get(0);
                    String urlText = targetChannel.getSnippet().getThumbnails().getHigh().getUrl();
                URL url = new URL(urlText);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                views.setImageViewBitmap(R.id.imageView, BitmapFactory.decodeStream(connection.getInputStream()));
                appWidgetManager.updateAppWidget(appWidgetId, views);
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
