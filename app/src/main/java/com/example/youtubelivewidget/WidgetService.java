package com.example.youtubelivewidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WidgetService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

//        // 新しいデータに変わったら表示させるデータを書き換える
//        SharedPreferences preferences = context.getSharedPreferences("PREFS", 0);
//        String value = preferences.getString("value", "");
//        value = "newData";
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putString("value", value);
//        editor.apply();
//
//        // ウィジェットの更新
//        Intent widgetIntent = new Intent(context, YoutubeLiveWidget.class);
//        widgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, YoutubeLiveWidget.class));
//        widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
//        context.sendBroadcast(widgetIntent);
//
//        Log.d("WIDGET", "Widget set to updated!");
    }
}