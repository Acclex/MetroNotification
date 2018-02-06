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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.traffic.locationremind.R;
import com.traffic.locationremind.baidu.location.activity.MainViewActivity;
import com.traffic.locationremind.baidu.location.object.NotificationObject;
import com.traffic.locationremind.baidu.location.service.RemonderLocationService;

import java.util.HashMap;
import java.util.Map;

public class NotificationUtil {

	public final static int notificationId = 1;
	private Context mContext;
	// NotificationManager ： 是状态栏通知的管理类，负责发通知、清楚通知等。
	private NotificationManager manager;
	// 定义Map来保存Notification对象
	private Map<Integer, Notification> map = null;

	public NotificationUtil(Context context) {
		this.mContext = context;
		// NotificationManager 是一个系统Service，必须通过 getSystemService()方法来获取。
		manager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		map = new HashMap<Integer, Notification>();
	}

	public void showNotification(int notificationId, NotificationObject mNotificationObject) {
		// 判断对应id的Notification是否已经显示， 以免同一个Notification出现多次
		if (!map.containsKey(notificationId)) {
			// 创建通知对象
			Notification notification = new Notification();
			// 设置通知栏滚动显示文字
			notification.tickerText = mContext.getResources().getString(R.string.arrived_reminder);
			// 设置显示时间
			notification.when = System.currentTimeMillis();
			// 设置通知显示的图标
			notification.icon = R.drawable.cm_mainmap_notice_green;
			// 设置通知的特性: 通知被点击后，自动消失
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			// 设置点击通知栏操作
			Intent in = new Intent(mContext, MainViewActivity.class);// 点击跳转到指定页面
			PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, in, 0);
			notification.contentIntent = pIntent;
			// 设置通知的显示视图
			RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.reminder_notify);

			int layoutId = remoteViews.getLayoutId();
			ViewGroup notificationRoot = (ViewGroup) LayoutInflater.from(mContext).inflate(layoutId,null);
			TextView lineName = (TextView)notificationRoot.findViewById(R.id.line);
			TextView start = (TextView)notificationRoot.findViewById(R.id.start);
			TextView end = (TextView)notificationRoot.findViewById(R.id.end);
			TextView next = (TextView)notificationRoot.findViewById(R.id.next);
			TextView time = (TextView)notificationRoot.findViewById(R.id.time);

			lineName.setText(mNotificationObject.getLineName());
			start.setText(mNotificationObject.getStartStation());
			end.setText(mNotificationObject.getEndStation());
			next.setText(mNotificationObject.getNextStation());
			time.setText(mNotificationObject.getTime());

			// 设置暂停按钮的点击事件
			Intent close = new Intent(mContext,RemonderLocationService.class);// 设置跳转到对应界面
			close.setAction(RemonderLocationService.CLOSE_REMINDER_SERVICE);
			PendingIntent pauseIn = PendingIntent.getService(mContext, 0, in,
					0);
			// 这里可以通过Bundle等传递参数
			remoteViews.setOnClickPendingIntent(R.id.close, pauseIn);
			/********** 简单分隔 **************************/
			// 设置通知的显示视图
			notification.contentView = remoteViews;
			// 发出通知
			manager.notify(notificationId, notification);
			map.put(notificationId, notification);// 存入Map中
		}
	}

	/**
	 * 取消通知操作
	 *
	 * @description：
	 * @author ldm
	 * @date 2016-5-3 上午9:32:47
	 */
	public void cancel(int notificationId) {
		manager.cancel(notificationId);
		map.remove(notificationId);
	}

	public void updateProgress(int notificationId, NotificationObject mNotificationObject) {
		Notification notify = map.get(notificationId);
		if (null != notify) {
			// 修改进度条
			int layoutId = notify.contentView.getLayoutId();
			ViewGroup notificationRoot = (ViewGroup) LayoutInflater.from(mContext).inflate(layoutId,null);
			TextView lineName = (TextView)notificationRoot.findViewById(R.id.line);
			TextView start = (TextView)notificationRoot.findViewById(R.id.start);
			TextView end = (TextView)notificationRoot.findViewById(R.id.end);
			TextView next = (TextView)notificationRoot.findViewById(R.id.next);
			TextView timeTextView = (TextView)notificationRoot.findViewById(R.id.time);

			lineName.setText(mNotificationObject.getLineName());
			start.setText(mNotificationObject.getStartStation());
			end.setText(mNotificationObject.getEndStation());
			next.setText(mNotificationObject.getNextStation());
			timeTextView.setText(mNotificationObject.getTime());
			manager.notify(notificationId, notify);
		}
	}
}