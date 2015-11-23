package com.nayang.handleutil;

public interface HttpCallbackListener {
	void onFinish(String respond);
	void onError(Exception e);

}
