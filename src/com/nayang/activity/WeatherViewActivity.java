package com.nayang.activity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.example.coolweather_v1.R;
import com.nayang.handleutil.GlobalVariable;
import com.nayang.handleutil.HttpCallbackListener;
import com.nayang.handleutil.RequestHttpUtil;
import com.nayang.handleutil.HandlerUtility;
import com.nayang.service.AutoUpdateService;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherViewActivity extends Activity implements OnClickListener{
	private LinearLayout weatherInfoLayout;
	/**
	 * ������ʾ������
	 */
	private TextView cityNameText;
	/**
	 * ������ʾ����ʱ��
	 */
	private TextView publishText;
	/**
	 * ������ʾ����������Ϣ
	 */
	private TextView weatherDespText;
	/**
	 * ������ʾ����1
	 */
	private TextView temp1Text;
	/**
	 * ������ʾ����2
	 */
	private TextView temp2Text;
	/**
	 * ������ʾ��ǰ����
	 */
	private TextView currentDateText;
	/**
	 * �л����а�ť
	 */
	private Button switchCity;
	/**
	 * ����������ť
	 */
	private Button refreshWeather;
	private String countyCode ;
	private StringBuffer m_warmToast;

	private LocationManager locationManager;
	private String provider;
	public static final int SHOW_LOCATION = 0;
	public static final int CANNOT_SHOW_LOCATION = 1;
	private TextView positionView;
	private RelativeLayout m_weather_desp_layout ;
	public static final int TEMP_RANGE = 6;
	private RelativeLayout m_otherInfoView;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_LOCATION:
				String currentPosition = (String) msg.obj;
				positionView.setText("����ǰλ�ã�"+currentPosition);
				break;
			case CANNOT_SHOW_LOCATION:
				positionView.setText("����ǰλ�ã�"+"�Բ����豸�޷���ȡ����ǰ��λ��."+
						"��ȷ�������豸�����������Լ�������λ����");
				break;
			default:
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		// ��ʼ�����ؼ�
		init();

		if(GlobalVariable.is_firstTimeComming){
			//countyCode = "300105";
			countyCode = "010101";
			GlobalVariable.is_firstTimeComming = false;
		}else{
			countyCode = getIntent().getStringExtra("county_code");
		}
		if (!TextUtils.isEmpty(countyCode)) {
			//Toast.makeText(this, countyCode, Toast.LENGTH_SHORT).show();
			// ���ؼ�����ʱ��ȥ��ѯ����
			publishText.setText("ͬ����...");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		} else {
			// û���ؼ�����ʱ��ֱ����ʾ��������
			showWeather();
		}
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);	
	}
	//*********************************λ�ü�����***********************************
	LocationListener locationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
		@Override
		public void onProviderEnabled(String provider) {
		}
		@Override
		public void onProviderDisabled(String provider) {
		}
		@Override
		public void onLocationChanged(Location location) {
			// ���µ�ǰ�豸��λ����Ϣ
			showLocation(location);
		}
	};
	//***************************************************************************
	public void init(){	
		//*********************************UI*************************************
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		m_weather_desp_layout = (RelativeLayout) findViewById(R.id.weather_desp_layout);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_date);
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		positionView = (TextView) findViewById(R.id.position_tv);
		m_otherInfoView = (RelativeLayout)findViewById(R.id.other_info_view);
		
		m_warmToast = new StringBuffer();
	   
		//**************************************************************************
		//*****************************�ж������Ƿ����*********************************
		boolean m_isNetWorkConnected = getNetworkState();
		if(!m_isNetWorkConnected){
			
			showTextOnToast("�޿������磬��ȷ�������Ƿ�����");
			Message m_msg = new Message();
			m_msg.what = CANNOT_SHOW_LOCATION;
			handler.sendMessage(m_msg);
			return;
		}
		m_otherInfoView.setVisibility(View.VISIBLE);
		//**************************************************************************
		//**********************************��ȡ����λ��*******************************
		locationManager = (LocationManager) getSystemService(Context. LOCATION_SERVICE);
		// ��ȡ���п��õ�λ���ṩ��
		List<String> providerList = locationManager.getProviders(true);
		if (providerList.contains(LocationManager.GPS_PROVIDER)) {
			provider = LocationManager.GPS_PROVIDER;
		} else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
			provider = LocationManager.NETWORK_PROVIDER;
		} else {
			// ��û�п��õ�λ���ṩ��ʱ������Toast��ʾ�û�
			Toast.makeText(this, "No location provider to use", Toast.LENGTH_SHORT).show();
			return;
		}
		Location location = locationManager.getLastKnownLocation(provider);
		if (location != null) {
			// ��ʾ��ǰ�豸��λ����Ϣ
			showLocation(location);
		}
		//ÿ2Сʱ����һ�Σ�������Ϊ1����
		locationManager.requestLocationUpdates(provider, 2*60*60*1000, 1000, locationListener);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent = new Intent(this, ChooseAreaActivity_V1.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishText.setText("ͬ����...");
			SharedPreferences prefs = PreferenceManager. getDefaultSharedPreferences(this);
			String weatherCode = prefs.getString("weather_code", "");
			if (!TextUtils.isEmpty(weatherCode)) {
				queryWeatherInfo(weatherCode);
			}
			break;
		default:
			break;
		}
	}
	/**
	 * ��ѯ�ؼ���������Ӧ���������š�
	 */
	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}
	/**
	 * ��ѯ������������Ӧ��������
	 */
	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		queryFromServer(address, "weatherCode");
	}
	/**
	 * ���ݴ���ĵ�ַ������ȥ���������ѯ�������Ż���������Ϣ��
	 */
	private void queryFromServer(final String address, final String type) {
		RequestHttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(final String response) {
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						// �ӷ��������ص������н�������������
						String[] array = response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				} else if ("weatherCode".equals(type)) {
					HandlerUtility.handleWeatherResponse(WeatherViewActivity.this, response);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							showWeather();
						}
					});
				}
			}
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						publishText.setText("ͬ��ʧ��");
					}
				});
			}
		});
	}
	/**
	 * ��SharedPreferences�ļ��ж�ȡ�洢��������Ϣ������ʾ�������ϡ�
	 */
	private void showWeather() {
		SharedPreferences prefs = PreferenceManager. getDefaultSharedPreferences(this);
		cityNameText.setText( prefs.getString("city_name", ""));
		temp1Text.setText("���·�Χ:"+prefs.getString("temp_end", ""));
		temp2Text.setText(prefs.getString("temp_start", ""));
		weatherDespText.setText(prefs.getString("weather_desp", ""));
		//publishText.setText("����" + prefs.getString("publish_time", "") + "����");
		currentDateText.setText("������������������ʱ�䣺"+prefs.getString("current_time", "")+" "+prefs.getString("publish_time", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);

		String t1 = prefs.getString("temp_end", "").toString().trim();
		String t2 = prefs.getString("temp_start", "").toString().trim();
		if(t1.length()>0){
			t1 = t1.substring(0, t1.length()-1);
			if(Integer.parseInt(t1)<=20){
				m_warmToast.delete(0, m_warmToast.length());
				m_warmToast.append("��~���������ˣ���ע�ⱣůŶ...");
			}
		}
		
		if(t2.length()>0){
			t2 = t2.substring(0, t2.length()-1);
			if(Integer.parseInt(t2)>=30){
				m_warmToast.delete(0, m_warmToast.length());
				m_warmToast.append("��~���������ˣ���ע���ʵ�����Ŷ...");
			}
		}
		if(Math.abs(Integer.parseInt(t2)-Integer.parseInt(t1))>TEMP_RANGE){
			
				m_warmToast.delete(0, m_warmToast.length());
				m_warmToast.append("��~�������²����ע�Ᵽ�����彡��Ŷ...");
			
		}
		if(m_warmToast.toString().length()>0){
			publishText.setText(m_warmToast.toString());
		}
		String weatherState = new String(prefs.getString("weather_desp", ""));

		if("��".equals(weatherState)){
			m_weather_desp_layout.setBackgroundResource(R.drawable.cloudy);
		}else if("����".equals(weatherState)){
			m_weather_desp_layout.setBackgroundResource(R.drawable.cloudy);
		}else if("С��".equals(weatherState)){
			m_weather_desp_layout.setBackgroundResource(R.drawable.little_rain);
		}else if("����".equals(weatherState)){
			m_weather_desp_layout.setBackgroundResource(R.drawable.little_rain);
		}else if("����".equals(weatherState)){
			m_weather_desp_layout.setBackgroundResource(R.drawable.little_rain);
		}else if("����".equals(weatherState)){
			m_weather_desp_layout.setBackgroundResource(R.drawable.big_rain);
		}else if("����".equals(weatherState)){
			m_weather_desp_layout.setBackgroundResource(R.drawable.big_rain);
		}else if("�ش���".equals(weatherState)){
			m_weather_desp_layout.setBackgroundResource(R.drawable.big_rain);
		}else if("��".equals(weatherState)){
			m_weather_desp_layout.setBackgroundResource(R.drawable.sun);
		}else if("����".equals(weatherState)){
			m_weather_desp_layout.setBackgroundResource(R.drawable.sun);
		}

		//Toast.makeText(this, Integer.parseInt(prefs.getString("temp_start", "")), Toast.LENGTH_SHORT).show();
		//�����Զ�����������Ϣ����
		Intent intent = new Intent(this, AutoUpdateService.class);
		startService(intent);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (locationManager != null) {
			// �رճ���ʱ���������Ƴ�
			locationManager.removeUpdates(locationListener);
		}
	}
	private void showLocation(final Location location) {
		String currentPosition = "latitude is " + location.getLatitude() + "\n"
				+ "longitude is " + location.getLongitude();
		double m_latitude = location.getLatitude();
		double m_longitude = location.getLongitude();
		Geocoder gc = new Geocoder(this, Locale.getDefault()); 
		List<Address> addresses = null; 
		try { 
			addresses = gc.getFromLocation(m_latitude, m_longitude, 10); 
			StringBuffer countryName=new StringBuffer();
			if(addresses.size()>0){
				countryName.append(addresses.get(0).getAddressLine(0));
				Message msg= new Message();
				msg.what = SHOW_LOCATION;
				msg.obj = countryName.toString();
				handler.sendMessage(msg);  
			}

		} catch (IOException e) {

		}

	}

	public boolean getNetworkState(){

		ConnectivityManager m_connectManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

		if(m_connectManager != null){
			NetworkInfo[] m_netInfo = m_connectManager.getAllNetworkInfo();
			if(m_netInfo !=null){
				for(int i=0;i<m_netInfo.length;i++){
					if(m_netInfo[i].getState() == NetworkInfo.State.CONNECTED){

						return true;
					}

				}

			}

		}
		return false;
	}
	public void showTextOnToast(String textContent){
		Toast.makeText(this, textContent, Toast.LENGTH_SHORT).show();
	}

}
