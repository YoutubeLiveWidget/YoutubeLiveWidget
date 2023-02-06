package com.example.youtubelivewidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

public class YoutubeLiveWidget extends AppWidgetProvider {

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_design);

        SharedPreferences preferences = context.getSharedPreferences("PREFS", 0);
        String value = preferences.getString("value", "");

        views.setTextViewText(R.id.text_title, "" + value);

        appWidgetManager.updateAppWidget(appWidgetId, views);

        Log.d("WIDGET", "Widget updated!");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId: appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetIds);
        }
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }
}
