package ru.ra66it.updaterforspotify.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ru.ra66it.updaterforspotify.QueryPreferneces;


/**
 * Created by 2Rabbit on 28.09.2017.
 */

public class StartupReceiver extends BroadcastReceiver {

    private static final String TAG = "StartupReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received broadcast intent: " + intent.getAction());

        boolean isOn = QueryPreferneces.isAlarmOn(context);
        PollService.setServiceAlarm(context, isOn);
    }
}
