package com.nayang.activity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.example.coolweather_v1.R;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;


public class HandleLocationActivity extends Activity {
	private TextView positionTextView;
	private LocationManager locationManager;
	private String provider;
	public static final int SHOW_LOCATION = 0;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_LOCATION:
				String currentPosition = (String) msg.obj;
				positionTextView.setText(currentPosition);
				break;
			default:
				break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_view);
		positionTextView = (TextView) findViewById(R.id.position_text_view);
		locationManager = (LocationManager) getSystemService(Context. LOCATION_SERVICE);
		// 获取所有可用的位置提供器
		List<String> providerList = locationManager.getProviders(true);
		if (providerList.contains(LocationManager.GPS_PROVIDER)) {
			provider = LocationManager.GPS_PROVIDER;
		} else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
			provider = LocationManager.NETWORK_PROVIDER;
		} else {
			// 当没有可用的位置提供器时，弹出Toast提示用户
			Toast.makeText(this, "No location provider to use", Toast.LENGTH_SHORT).show();
			return;
		}
		Location location = locationManager.getLastKnownLocation(provider);
		if (location != null) {
			// 显示当前设备的位置信息
			showLocation(location);
		}
		//每2小时更新一次，距离间隔为1公里
		locationManager.requestLocationUpdates(provider, 2*60*60*1000, 1000, locationListener);
	}
	protected void onDestroy() {
		super.onDestroy();
		if (locationManager != null) {
			// 关闭程序时将监听器移除
			locationManager.removeUpdates(locationListener);
		}
	}
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
			// 更新当前设备的位置信息
			showLocation(location);
		}
	};
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
				int i= 1;
				if(addresses.size()>0){
					//Address address =addresses.get(0);
					for (Address address : addresses) {
						
						countryName.append((i++)+address.getAdminArea()+"\n");
						countryName.append((i++)+address.getCountryName()+"\n");
						countryName.append((i++)+address.getFeatureName()+"\n");
						countryName.append((i++)+address.getLocality()+"\n");
						countryName.append((i++)+address.getPremises()+"\n");
						countryName.append((i++)+address.getSubAdminArea()+"\n");
						countryName.append((i++)+address.getSubThoroughfare()+"\n");
						countryName.append((i++)+address.getThoroughfare()+"\n");
						//countryName.append((i++)+address.toString()+"\n");
						
					}		 
						for (int j=0; j<addresses.get(0).getMaxAddressLineIndex();j++) {
							countryName.append((i++)+addresses.get(0).getAddressLine(j) +"\n");
						}
				   positionTextView.setText(countryName.toString());
				  
				}
			
				} catch (IOException e) {

				}
		
	}
}