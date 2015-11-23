package com.nayang.receiver;

import com.nayang.service.AutoUpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoUpdateReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		//Log.d("mytest2", "update now");
		Intent i = new Intent(context, AutoUpdateService.class);
		context.startService(i);
	}
}
