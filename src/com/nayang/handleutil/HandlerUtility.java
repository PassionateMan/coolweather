package com.nayang.handleutil;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.nayang.operatemodel.CityManager;
import com.nayang.operatemodel.WeatherDBManager;
import com.nayang.operatemodel.CountyManager;
import com.nayang.operatemodel.ProvinceManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class HandlerUtility {

	public synchronized static boolean handleProvincesRespond(WeatherDBManager coolWeather,String respond){


		if(!TextUtils.isEmpty(respond)){
			String[] m_allProvinces = respond.split(",");
			if(m_allProvinces != null && m_allProvinces.length>0){
				for(String province :m_allProvinces){
					String[] m_str = province.split("\\|");
					ProvinceManager m_province = new ProvinceManager();
					m_province.setM_provinceCode(m_str[0]);
					m_province.setM_provinceName(m_str[1]);

					coolWeather.saveProvince(m_province);
				}
				return true;
			}			
		}
		return false;
	}

	public static boolean handleCitiesRespond(WeatherDBManager coolWeather,String respond,int provinceId){


		if(!TextUtils.isEmpty(respond)){
			String[] m_allCities = respond.split(",");
			if(m_allCities != null && m_allCities.length>0){
				for(String city :m_allCities){
					String[] m_str = city.split("\\|");
					CityManager m_city = new CityManager();
					m_city.setM_cityCode(m_str[0]);
					m_city.setM_cityName(m_str[1]);
					m_city.setM_provinceId(provinceId);

					coolWeather.saveCity(m_city);
				}
				return true;
			}			
		}
		return false;
	}

	public static boolean handleCountiesRespond(WeatherDBManager coolWeather,String respond,int cityId){


		if(!TextUtils.isEmpty(respond)){
			String[] m_allCounties = respond.split(",");
			if(m_allCounties != null && m_allCounties.length>0){
				for(String county :m_allCounties){
					String[] m_str = county.split("\\|");

					CountyManager m_county = new CountyManager();
					m_county.setM_countyCode(m_str[0]);
					m_county.setM_countyName(m_str[1]);
					m_county.setM_cityId(cityId);

					coolWeather.saveCounty(m_county);				
				}
				return true;
			}			
		}
		return false;
	}

	public static void handleWeatherResponse(Context context,String response){
		try {
			JSONObject m_JSon = new JSONObject(response);
			JSONObject m_object = m_JSon.getJSONObject("weatherinfo");
			String m_cityName = m_object.getString("city");
			String m_weatherCode = m_object.getString("cityid");
			String m_tempStart = m_object.getString("temp1");
			String m_tempEnd = m_object.getString("temp2");
			String m_weatherDesp = m_object.getString("weather");
			String m_publicTime = m_object.getString("ptime");

			saveWeatherInfo(context,m_cityName,m_weatherCode,m_tempStart,m_tempEnd,m_weatherDesp,m_publicTime);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void saveWeatherInfo(Context context,String cityName,String weatherCode,
			String tempStart,String tempEnd,String weatherDesp,String pTime){


		SimpleDateFormat m_date = new SimpleDateFormat("yyyyƒÍM‘¬d»’", Locale.CHINA);

		SharedPreferences.Editor m_sharePreEdit = PreferenceManager.getDefaultSharedPreferences(context).edit();
		m_sharePreEdit.putBoolean("select_city", true);
		m_sharePreEdit.putString("city_name", cityName);
		m_sharePreEdit.putString("weather_code", weatherCode);
		m_sharePreEdit.putString("temp_start", tempStart);
		m_sharePreEdit.putString("temp_end", tempEnd);
		m_sharePreEdit.putString("publish_time", pTime);
		m_sharePreEdit.putString("weather_desp", weatherDesp);
		m_sharePreEdit.putString("current_time", m_date.format(new java.util.Date()));
		m_sharePreEdit.commit();

	}

}
