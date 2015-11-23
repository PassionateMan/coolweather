package com.nayang.operatemodel;

import java.util.ArrayList;
import java.util.List;

import com.nayang.database.WeatherOpenHelperClass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WeatherDBManager {
	
	private static final String DB_NAME="cool_weather";
	private static final int DB_VERSION = 1;
	private SQLiteDatabase m_db;
	private static WeatherDBManager m_coolWeather;
	
	private WeatherDBManager(Context m_context){
		
		WeatherOpenHelperClass m_dbHelper = new WeatherOpenHelperClass(m_context, DB_NAME, null, DB_VERSION);
		m_db = m_dbHelper.getWritableDatabase();
	}
	public synchronized static WeatherDBManager getInstance(Context m_context){
		if(m_coolWeather==null){
			
			m_coolWeather = new WeatherDBManager(m_context);
		}
		
		return m_coolWeather;
	}
	
	
	public void saveProvince(ProvinceManager m_province){
		if(m_province!=null){
			ContentValues m_values = new ContentValues();
			//m_values.put("id", m_province.getM_provinceId());
			m_values.put("province_name", m_province.getM_provinceName());
			m_values.put("province_code", m_province.getM_provinceCode());
			m_db.insert("Province", null, m_values);
		}
	}
	
	public List<ProvinceManager> loadProvinces(){
		List<ProvinceManager> m_list = new ArrayList<ProvinceManager>();
		
		Cursor m_cursor = m_db.query("Province", null, null, null, null, null, null);
		if(m_cursor.moveToFirst()){
	
			do{
				ProvinceManager m_province = new ProvinceManager();
				m_province.setM_provinceId(m_cursor.getInt(m_cursor.getColumnIndex("id")));
				m_province.setM_provinceName(m_cursor.getString(m_cursor.getColumnIndex("province_name")));
				m_province.setM_provinceCode(m_cursor.getString(m_cursor.getColumnIndex("province_code")));

				m_list.add(m_province);
			}while(m_cursor.moveToNext());
			
		}
		
		return m_list;
				
	}	
	public void saveCity(CityManager m_city){
		
		if(m_city != null){
			ContentValues m_values = new ContentValues();
			//m_values.put("id", m_city.getM_cityId());
			m_values.put("city_name", m_city.getM_cityName());
			m_values.put("city_code", m_city.getM_cityCode());
			m_values.put("province_id", m_city.getM_provinceId());
			
			m_db.insert("City", null, m_values);
		}
		
	}
	public List<CityManager> loadCities(int m_provinceId){
		
		List<CityManager> m_list = new ArrayList<CityManager>();
		Cursor m_cursor = m_db.query("City", null, "province_id= ?", new String[]{String.valueOf(m_provinceId)}, null, null, null);
		if(m_cursor.moveToFirst()){
			
			do{
				CityManager m_city = new CityManager();
				m_city.setM_cityId(m_cursor.getInt(m_cursor.getColumnIndex("id")));
				m_city.setM_cityName(m_cursor.getString(m_cursor.getColumnIndex("city_name")));
				m_city.setM_cityCode(m_cursor.getString(m_cursor.getColumnIndex("city_code")));
				m_city.setM_provinceId(m_cursor.getInt(m_cursor.getColumnIndex("province_id")));
				
				m_list.add(m_city);
			}while(m_cursor.moveToNext());
		}
		
		return m_list;
		
	}
	public void saveCounty(CountyManager m_county){
		if(m_county!=null){
			ContentValues m_values = new ContentValues();
			
			//m_values.put("id", m_county.getM_countyId());
			m_values.put("county_name", m_county.getM_countyName());
			m_values.put("county_code", m_county.getM_countyCode());
			m_values.put("city_id", m_county.getM_cityId());
			
			m_db.insert("County", null, m_values);
			
		}
			
	}
	
	public List<CountyManager> loadCounties(int m_cityId){
		List<CountyManager> m_list = new ArrayList<CountyManager>();
		Cursor  m_cursor = m_db.query("County", null, "city_id= ?", new String[]{String.valueOf(m_cityId)}, null, null, null);
		
		if(m_cursor.moveToFirst()){
			do{
				CountyManager m_county = new CountyManager();
				m_county.setM_countyId(m_cursor.getInt(m_cursor.getColumnIndex("id")));
				m_county.setM_countyName(m_cursor.getString(m_cursor.getColumnIndex("county_name")));
				m_county.setM_countyCode(m_cursor.getString(m_cursor.getColumnIndex("county_code")));
				m_county.setM_cityId(m_cursor.getInt(m_cursor.getColumnIndex("city_id")));
				
				m_list.add(m_county);
			}while(m_cursor.moveToNext());
		}
		return m_list;
	}

}
