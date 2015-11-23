package com.nayang.activity;

import com.example.coolweather_v1.R;
import com.nayang.receiver.AlarmClockReceiver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class AlarmClockActivity extends Activity implements OnClickListener{

	private Button m_set;
	private Button m_repeat;
	private Button m_cancel;
	private AlarmManager m_alarmManager;
	private PendingIntent operation;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_clock_layout);

		init();
		m_set.setOnClickListener(this);
		m_repeat.setOnClickListener(this);
		m_cancel.setOnClickListener(this);


	}

	public void init(){
		m_set = (Button) findViewById(R.id.set_clock);
		m_repeat = (Button) findViewById(R.id.repeat_clock);
		m_cancel = (Button) findViewById(R.id.cancel_clock);

		Intent intent = new Intent(AlarmClockActivity.this,AlarmClockReceiver.class);
		intent.setAction("ALARM_ACTION");
		operation = PendingIntent.getBroadcast(this, 0, intent, 0);
		m_alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()){

		case R.id.set_clock:{
			Toast.makeText(this, "set_alarm clock now!", Toast.LENGTH_SHORT).show();
			m_alarmManager.set(AlarmManager.RTC_WAKEUP, 3000, operation);

			break;
		}
		case R.id.repeat_clock:{
			Toast.makeText(this, "repeat_alarm clock now!", Toast.LENGTH_SHORT).show();

			m_alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 5000, 1000, operation);

			break;
		}
		case R.id.cancel_clock:{
			Toast.makeText(this, "cancel_alarm clock now!", Toast.LENGTH_SHORT).show();

			m_alarmManager.cancel(operation);
			break;
		}
		default:break;

		}

	}
}
