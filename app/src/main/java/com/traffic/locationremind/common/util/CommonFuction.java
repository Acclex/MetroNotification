/**
 * Copyright (C) 2015~2050 by foolstudio. All rights reserved.
 * 
 * ��Դ�ļ��д��벻����������������ҵ��;�����߱�������Ȩ��
 * 
 * ���ߣ�������
 * 
 * �������䣺foolstudio@qq.com
 * 
*/

package com.traffic.locationremind.common.util;

import android.content.Context;
import android.content.SharedPreferences;

public class CommonFuction {

	public static final String SHNAME = "subwab_info";
	public static final String CITYNO = "cityno";
	public final static String HEAD = "head";
	public final static String TAIL = "tail";

	public static void writeSharedPreferences(Context context,String key,String value){
		SharedPreferences sp = context.getSharedPreferences(SHNAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(key, value);
		editor.putInt("age", 11);
		editor.commit();
	}

	public static String getSharedPreferencesValue(Context context,String key){
		SharedPreferences sp = context.getSharedPreferences(SHNAME, Context.MODE_PRIVATE);
		String name = sp.getString(key, null);
		return name;
	}
}
