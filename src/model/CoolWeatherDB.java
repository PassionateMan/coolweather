package model;

import java.util.ArrayList;
import java.util.List;

import db.CoolWeatherOpenHelp;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {
	
	
	private static final String DB_NAME="cool_weather";
	private static final int DB_VERSION = 1;
	private SQLiteDatabase m_db;
	private CoolWeatherDB m_coolWeather;
	
	private CoolWeatherDB(Context m_context){
		
		CoolWeatherOpenHelp m_dbHelper = new CoolWeatherOpenHelp(m_context, DB_NAME, null, DB_VERSION);
		m_db = m_dbHelper.getWritableDatabase();
	}
	public synchronized CoolWeatherDB getInstance(Context m_context){
		if(m_coolWeather==null){
			
			m_coolWeather = new CoolWeatherDB(m_context);
		}
		
		return m_coolWeather;
	}
	
	
	public void saveProvince(Province m_province){
		if(m_province!=null){
			ContentValues m_values = new ContentValues();
			m_values.put("id", m_province.getM_provinceId());
			m_values.put("province_name", m_province.getM_provinceName());
			m_values.put("province_code", m_province.getM_provinceCode());
			m_db.insert("Province", null, m_values);
		}
	}
	
	public List<Province> loadProvince(){
		List<Province> m_list = new ArrayList<Province>();
		
		Cursor m_cursor = m_db.query("Province", null, null, null, null, null, null);
		if(m_cursor.moveToFirst()){
			Province m_province = new Province();
			
			do{
				m_province.setM_provinceId(m_cursor.getInt(m_cursor.getColumnIndex("id")));
				m_province.setM_provinceName(m_cursor.getString(m_cursor.getColumnIndex("province_name")));
				m_province.setM_provinceCode(m_cursor.getString(m_cursor.getColumnIndex("province_code")));

				m_list.add(m_province);
			}while(m_cursor.moveToNext());
			
		}
		
		return m_list;
				
	}	
	public void saveCity(City m_city){
		
		if(m_city != null){
			ContentValues m_values = new ContentValues();
			m_values.put("id", m_city.getM_cityId());
			m_values.put("city_name", m_city.getM_cityName());
			m_values.put("city_code", m_city.getM_cityCode());
			m_values.put("province_id", m_city.getM_provinceId());
			
			m_db.insert("City", null, m_values);
		}
		
	}
	public List<City> loadCity(){
		
		List<City> m_list = new ArrayList<City>();
		Cursor m_cursor = m_db.query("City", null, null, null, null, null, null);
		if(m_cursor.moveToFirst()){
			
			do{
				City m_city = new City();
				m_city.setM_cityId(m_cursor.getInt(m_cursor.getColumnIndex("id")));
				m_city.setM_cityName(m_cursor.getString(m_cursor.getColumnIndex("city_name")));
				m_city.setM_cityCode(m_cursor.getString(m_cursor.getColumnIndex("city_code")));
				m_city.setM_provinceId(m_cursor.getString(m_cursor.getColumnIndex("province_id")));
				
				m_list.add(m_city);
			}while(m_cursor.moveToNext());
		}
		
		return m_list;
		
	}
	public void saveCounty(County m_county){
		if(m_county!=null){
			ContentValues m_values = new ContentValues();
			
			m_values.put("id", m_county.getM_countyId());
			m_values.put("county_name", m_county.getM_countyName());
			m_values.put("county_code", m_county.getM_countyCode());
			m_values.put("city_id", m_county.getM_cityId());
			
			m_db.insert("County", null, m_values);
			
		}
			
	}
	
	public List<County> loadCounty(){
		List<County> m_list = new ArrayList<County>();
		Cursor  m_cursor = m_db.query("County", null, null, null, null, null, null);
		
		if(m_cursor.moveToFirst()){
			do{
				County m_county = new County();
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
