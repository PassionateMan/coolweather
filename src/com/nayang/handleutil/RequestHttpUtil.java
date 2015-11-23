package com.nayang.handleutil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.nayang.activity.WeatherViewActivity;

import android.util.Log;
import android.widget.Toast;


public class RequestHttpUtil {
	public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
	
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				HttpURLConnection m_connection = null;
				try {
					URL m_url = new URL(address);
					m_connection = (HttpURLConnection)m_url.openConnection();
					m_connection.setRequestMethod("GET");
					m_connection.setConnectTimeout(8000);
					m_connection.setReadTimeout(8000);
					
					InputStream m_in = m_connection.getInputStream();
					BufferedReader m_bufferReader = new BufferedReader(new InputStreamReader(m_in));
					String m_lineStr = null;
					StringBuilder m_respond = new StringBuilder();
					while((m_lineStr=m_bufferReader.readLine())!=null){
						m_respond.append(m_lineStr);
					}
					if(listener != null){
						listener.onFinish(m_respond.toString());
					   // Log.d("mytest2", m_respond.toString());
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					if(listener != null){
						listener.onError(e);
						
					}
					e.printStackTrace();
				}finally{
					if(m_connection != null){
						m_connection.disconnect();
						
					}
				}
				
			}
		}).start();
		
	}

}
