package com.haksoftware.go4lunch.utils;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.haksoftware.go4lunch.R;
import com.haksoftware.go4lunch.model.Colleague;
import com.haksoftware.go4lunch.model.Restaurant;
import com.haksoftware.go4lunch.repository.FirebaseRepository;

import java.util.Calendar;
import java.util.List;

public class NotificationScheduler extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String restaurantId = intent.getStringExtra("restaurant_id");
            String restaurantName = intent.getStringExtra("restaurant_name");
            String title = context.getString(R.string.time_to_eat) + " " + restaurantName + " !";
            StringBuilder content = new StringBuilder();
            content.append(context.getString(R.string.participant)).append(" ");
            List<Colleague> colleagues = FirebaseRepository.getInstance().getColleaguesByRestaurant(restaurantId).getValue();
            if(colleagues != null && !colleagues.isEmpty()) {
                for (Colleague colleague :colleagues) {
                    content.append(colleague.getUserName()).append("; ");
                }
            }

            showNotification(context, title, content.toString());
        }
    }
    public static void scheduleNotification(Context context, Restaurant restaurant) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationScheduler.class);
        intent.putExtra("restaurant_id", restaurant.getRestaurantId());
        intent.putExtra("restaurant_name", restaurant.getName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long alarmTime = getNoonTimeMillis();
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);

    }
    public static void cancelNotification(Context context, Restaurant restaurant) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationScheduler.class);
        intent.putExtra("restaurant_name", restaurant);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
    }
    private void showNotification(Context context, String title, String content) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // For Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Go4Lunch", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_lunch);
        notificationManager.notify(0, builder.build());
    }
    private static long getNoonTimeMillis() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }
}