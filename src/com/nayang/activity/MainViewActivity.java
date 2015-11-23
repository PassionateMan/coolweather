package com.nayang.activity;

import com.example.coolweather_v1.R;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MainViewActivity extends TabActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		//setContentView(R.layout.main_view_layout);

		Resources m_resource = getResources();
		TabHost m_tabHost = getTabHost();
		LayoutInflater.from(this).inflate(R.layout.main_view_layout, m_tabHost.getTabContentView(), true);

		TabSpec m_spec;
		Intent m_activity;
		m_activity = new Intent(MainViewActivity.this,WeatherViewActivity.class);
		m_spec = m_tabHost.newTabSpec("tab01").setIndicator("ÃÏ∆¯").setContent(m_activity);
		m_tabHost.addTab(m_spec);
		
		m_activity = new Intent(MainViewActivity.this,HandleLocationActivity.class);
		m_spec = m_tabHost.newTabSpec("tab02").setIndicator("Œª÷√").setContent(m_activity);
		m_tabHost.addTab(m_spec);

//		m_activity = new Intent(MainViewActivity.this,AlarmClockActivity.class);
//		m_spec = m_tabHost.newTabSpec("tab02").setIndicator("ƒ÷÷”").setContent(m_activity);
//		m_tabHost.addTab(m_spec);

		m_tabHost.setCurrentTab(1);


	}

}
