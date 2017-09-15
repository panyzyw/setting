package com.yongyida.robot.settings.utils;

import com.google.gson.Gson;
import com.yongyida.robot.settings.bean.QrBean;

public class BeanUtils {
	public static QrBean parseQrJson(String json){
		try{
			QrBean qrBean =  new Gson().fromJson(json, QrBean.class);
			return qrBean;
		}catch(Exception e){
			return null;
		}
	}
}
