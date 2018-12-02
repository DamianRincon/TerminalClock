package mx.rincon.damian.terminalclock;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.text.Html;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import mx.rincon.damian.terminalclock.Utils.DataHelper;

/**
 * Implementation of App Widget functionality.
 */
public class Terminal extends AppWidgetProvider {
    public static final String UPDATE = "android.appwidget.action.APPWIDGET_UPDATE";

    public PendingIntent createUptateIntent(Context context){
        Intent intent = new Intent(UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }



    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        new UpdateService().buildUpdate(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.terminal);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MILLISECOND,1);
        alarmManager.setRepeating(AlarmManager.RTC,calendar.getTimeInMillis(),500,
                createUptateIntent(context));

    }

    @Override
    public void onDisabled(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(createUptateIntent(context));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (UPDATE.equals(intent.getAction()))
            context.startService(new Intent(context, UpdateService.class));
    }

    public static class UpdateService extends Service{

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onStart(Intent intent, int startId) {
            super.onStart(intent, startId);
            RemoteViews remoteViews = buildUpdate(this);
            ComponentName widget = new ComponentName(this,Terminal.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            manager.updateAppWidget(widget,remoteViews);
        }

        public RemoteViews buildUpdate(Context context){
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.terminal);
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("EEEE d MMM''yy");
            @SuppressLint("SimpleDateFormat") DateFormat timeFormat = new SimpleDateFormat("HH:mm a z");
            String date = dateFormat.format(Calendar.getInstance().getTime());
            String time = timeFormat.format(Calendar.getInstance().getTime());
            int batteryLevel = cargaBateria();
            System.out.println(batteryLevel);
            String has = "";
            if (batteryLevel>0)
                for (int i=0; i<(batteryLevel/10);i++){ has+="#"; }
            else
                has="#";

            String timeColor = "<font color='#4caf50'>"+time+"</font>";
            String dateColor = "<font color='#4caf50'>"+date+"</font>";
            String hasColor = "<font color='#ffa000'>"+has+"</font>";
            String baterryColor = "<font color='#ffa000'>"+batteryLevel+"%</font>";

            String json = "{<br>" +
                    "\t\"time\" : \""+timeColor+"\",<br>" +
                    "\t\"date\" : \""+dateColor+"\",<br>" +
                    "\t\"batl\" : \""+hasColor+"\",<br>" +
                    "\t\"batt\" : \""+baterryColor+"\",<br>" +
                    "\t\"conn\" : \"unknown ssid\",<br>" +
                    "\t\"ip\"   : \"192.168.1.16\"<br>" +
                    "}";
            String username = DataHelper.getString(context,DataHelper.USERNAME) + "@" + DataHelper.getString(context,DataHelper.HOST) + ":~ $ now";
            remoteViews.setTextViewText(R.id.content_text, Html.fromHtml(json));
            remoteViews.setTextViewText(R.id.username, username);
            return remoteViews;
        }

        public int cargaBateria () {
            try {
                IntentFilter batIntentFilter =
                        new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent battery =
                        this.registerReceiver(null, batIntentFilter);
                int nivelBateria = battery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                return nivelBateria;
            }
            catch (Exception e) {
                System.out.println("Error al obtener estado de la batería");
                return 0;
            }
        }
    }
}

