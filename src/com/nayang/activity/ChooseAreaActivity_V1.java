package com.nayang.activity;

import java.util.ArrayList;
import java.util.List;


import com.example.coolweather_v1.R;
import com.nayang.handleutil.GlobalVariable;
import com.nayang.handleutil.HttpCallbackListener;
import com.nayang.handleutil.RequestHttpUtil;
import com.nayang.handleutil.HandlerUtility;
import com.nayang.operatemodel.CityManager;
import com.nayang.operatemodel.WeatherDBManager;
import com.nayang.operatemodel.CountyManager;
import com.nayang.operatemodel.ProvinceManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity_V1 extends Activity {
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private WeatherDBManager coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	private boolean isFromWeatherActivity = false;
	/**
	 * ʡ�б�
	 */
	private List<ProvinceManager> provinceList;
	/**
	 * ���б�
	 */
	private List<CityManager> cityList;
	/**
	 * ���б�
	 */
	private List<CountyManager> countyList;
	/**
	 * ѡ�е�ʡ��
	 */
	private ProvinceManager selectedProvince;
	/**
	 * ѡ�еĳ���
	 */
	private CityManager selectedCity;
	/**
	 * ��ǰѡ�еļ���
	 */
	private int currentLevel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_ activity", false);
		SharedPreferences prefs = PreferenceManager. getDefaultSharedPreferences(this);
		if (prefs.getBoolean("city_selected", false)&&!isFromWeatherActivity) {
			Intent intent = new Intent(this, WeatherViewActivity.class);
			startActivity(intent);
			finish();
			return;
		}

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		init();
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(index);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(index);
					queryCounties();
				}else if (currentLevel == LEVEL_COUNTY) {
					String countyCode = countyList.get(index).getM_countyCode();
					Intent intent = new Intent(ChooseAreaActivity_V1.this, WeatherViewActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
					finish();
				}
			}
		});
		queryProvinces(); // ����ʡ������
		//GlobalVariable.is_mainActi_weatherActi_chooseAreaActi = true;
	}

	public void init(){
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		coolWeatherDB = WeatherDBManager.getInstance(this);//������Ӧ�����ݿ����c

	}
	/**
	 * ��ѯȫ�����е�ʡ�����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ��
	 */
	private void queryProvinces() {
		provinceList = coolWeatherDB.loadProvinces();
		if (provinceList.size() > 0) {
			dataList.clear();
			for (ProvinceManager province : provinceList) {
				dataList.add(province.getM_provinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "province");
		}
	}
	private void queryCities() {
		cityList = coolWeatherDB.loadCities(selectedProvince.getM_provinceId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (CityManager city : cityList) {
				dataList.add(city.getM_cityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getM_provinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getM_provinceCode(), "city");
		}
	}
	/**
	 * ��ѯѡ���������е��أ����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ��
	 */
	private void queryCounties() {
		countyList = coolWeatherDB.loadCounties(selectedCity.getM_cityId());
		if (countyList.size() > 0) {
			dataList.clear();
			for (CountyManager county : countyList) {
				dataList.add(county.getM_countyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getM_cityName());
			currentLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCity.getM_cityCode(), "county");
		}
	}
	/**
	 * ���ݴ���Ĵ��ź����ʹӷ������ϲ�ѯʡ�������ݡ�
	 */
	private void queryFromServer(final String code, final String type) {
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		RequestHttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			@Override
			public void onFinish(String response) {
				boolean result = false;
				if ("province".equals(type)) {
					result = HandlerUtility.handleProvincesRespond(coolWeatherDB,
							response);
				} else if ("city".equals(type)) {
					result = HandlerUtility.handleCitiesRespond(coolWeatherDB,
							response, selectedProvince.getM_provinceId());
				} else if ("county".equals(type)) {
					result = HandlerUtility.handleCountiesRespond(coolWeatherDB,
							response, selectedCity.getM_cityId());
				}
				if (result) {
					// ͨ��runOnUiThread()�����ص����̴߳����߼�
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("county".equals(type)) {
								queryCounties();
							}
						}
					});
				}

			}
			@Override
			public void onError(Exception e) {
				// ͨ��runOnUiThread()�����ص����̴߳����߼�
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity_V1.this,
								"��Ǹ������ʧ��", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	/**
	 * ��ʾ���ȶԻ���
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("����Ŭ��������...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	/**
	 * �رս��ȶԻ���
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	/**
	 * ����Back���������ݵ�ǰ�ļ������жϣ���ʱӦ�÷������б���ʡ�б�������ֱ���˳���
	 */
	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else {
			if (isFromWeatherActivity) {
				Intent intent = new Intent(this, WeatherViewActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}
}