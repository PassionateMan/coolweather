package com.nayang.receiver;

import com.nayang.activity.AlarmClockActivity;
import com.nayang.activity.WeatherViewActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmClockReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Toast.makeText(context, "wake you up now!", Toast.LENGTH_SHORT).show();
	   
	}

}
