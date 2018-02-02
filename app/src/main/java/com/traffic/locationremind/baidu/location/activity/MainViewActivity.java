package com.traffic.locationremind.baidu.location.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.traffic.locationremind.R;
import com.traffic.locationremind.baidu.location.object.MarkObject;
import com.traffic.locationremind.baidu.location.object.MarkObject.MarkClickListener;
import com.traffic.locationremind.baidu.location.view.GifView;
import com.traffic.locationremind.baidu.location.view.LineMap;
import com.traffic.locationremind.baidu.location.view.LineMapColor;
import com.traffic.locationremind.baidu.location.view.LineMapColorView;
import com.traffic.locationremind.baidu.location.view.LineMapView;
import com.traffic.locationremind.common.util.ReadExcelDataUtil;
import com.traffic.locationremind.manager.bean.LineInfo;
import com.traffic.locationremind.manager.bean.StationInfo;
import com.traffic.locationremind.manager.database.DataHelper;

import java.util.ArrayList;
import java.util.List;

public class MainViewActivity extends Activity implements ReadExcelDataUtil.DbWriteFinishListener{

	private LineMapView sceneMap;
	private LineMapColorView lineMap;
	private GifView gif;
	private Button scaleMorebtn;
	private Button scaleLessbtn;
	private Button screenbtn;
	private TextView currentLineInfoText;
	private TextView hintText;
	private int currentIndex = 1;
	private DataHelper mDataHelper;//数据库
	private List<LineInfo> mLineInfoList = new ArrayList<LineInfo>();//地图线路
	private List<StationInfo> mStationInfoList = new ArrayList<StationInfo>();//地图站台信息
	private initDataThread minitDataThread;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			switch (msg.what){
				case 1:
					if(mLineInfoList != null && mLineInfoList.size() > 0){
						//线路颜色-------------------------------------
						initLineColorMap();

						for (int j=0;j<mLineInfoList.size();j++){
							if(mLineInfoList.get(j).getLineid() == currentIndex){
								mStationInfoList = mLineInfoList.get(j).getStationInfoList();
								currentLineInfoText.setText(mLineInfoList.get(j).getLinename()+"\n"+mLineInfoList.get(j).getLineinfo());
								currentLineInfoText.setBackgroundColor(mLineInfoList.get(j).colorid);
								break;
							}
						}
						int countRow = mStationInfoList.size()/LineMap.ROWMAXCOUNT+1;
						Bitmap bitmap = Bitmap.createBitmap((int) sceneMap.getViewWidth(), MarkObject.ROWHEIGHT*countRow,
								Bitmap.Config.ARGB_8888);
						bitmap.eraseColor(MainViewActivity.this.getResources().getColor(R.color.white));//填充颜色
						sceneMap.setBitmap(bitmap);
						sceneMap.postInvalidate();
						//----------------------------------------
						gif.setVisibility(View.GONE);
						setViewVisible(View.VISIBLE);
						hintText.setVisibility(View.GONE);
					}
					break;
				case 2:
					for (int j=0;j<mLineInfoList.size();j++){
						if(mLineInfoList.get(j).getLineid() == currentIndex){
							mStationInfoList = mLineInfoList.get(j).getStationInfoList();
							currentLineInfoText.setText(mLineInfoList.get(j).getLinename()+"\n"+mLineInfoList.get(j).getLineinfo());
							currentLineInfoText.setBackgroundColor(mLineInfoList.get(j).colorid);
							break;
						}
					}
					int countRow = mStationInfoList.size()/LineMap.ROWMAXCOUNT+1;
					Bitmap bitmap = Bitmap.createBitmap((int) sceneMap.getViewWidth(), MarkObject.ROWHEIGHT*countRow,
							Bitmap.Config.ARGB_8888);
					bitmap.eraseColor(MainViewActivity.this.getResources().getColor(R.color.white));//填充颜色
					sceneMap.setBitmap(bitmap);
					sceneMap.postInvalidate();
					//sceneMap.draw();
					break;
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_view);
		sceneMap = (LineMapView) findViewById(R.id.sceneMap);
		currentLineInfoText = (TextView) findViewById(R.id.text);
		lineMap = (LineMapColorView) findViewById(R.id.lineMap);
		scaleMorebtn = (Button) findViewById(R.id.button_in);
		scaleLessbtn = (Button) findViewById(R.id.button_out);
		screenbtn = (Button) findViewById(R.id.full_screen);
		gif = (GifView) findViewById(R.id.gif);
		hintText = (TextView) findViewById(R.id.hint);
		///-----------------

		mDataHelper = DataHelper.getInstance(this);
		ReadExcelDataUtil.getInstance().addDbWriteFinishListener(this);
		scaleMorebtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sceneMap.zoomIn();
			}
		});
		scaleLessbtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sceneMap.zoomOut();
			}
		});
		screenbtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!sceneMap.getFullScree()){
					sceneMap.setFullScree(true);
					//text.setVisibility(View.GONE);
					sceneMap.setInitScale();
					Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(),R.drawable.shenzhen);
					sceneMap.setBitmap(bitmap2);
					sceneMap.postInvalidate();
				}

			}
		});
		if(ReadExcelDataUtil.getInstance().hasWrite){
			initData();
			gif.setVisibility(View.GONE);
			setViewVisible(View.VISIBLE);
			hintText.setVisibility(View.GONE);
		}else{
			// 设置背景gif图片资源
			gif.setMovieResource(R.raw.two);
			gif.setVisibility(View.VISIBLE);
			hintText.setVisibility(View.VISIBLE);
			hintText.setText(this.getResources().getString(R.string.loading));
			setViewVisible(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		sceneMap.setPauseState(false);
	}

	@Override
	protected void onPause() {
		super.onPause();
		sceneMap.setPauseState(true);
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onStop() {
		super.onStop();
	}

	public void setViewVisible(int visible){
		scaleMorebtn.setVisibility(visible);
		scaleLessbtn.setVisibility(visible);
		screenbtn.setVisibility(visible);
		sceneMap.setVisibility(visible);
		lineMap.setVisibility(visible);
		currentLineInfoText.setVisibility(visible);
	}

	private void initData(){
		minitDataThread = new initDataThread();
		minitDataThread.start();
	}

	class initDataThread extends Thread {
		@Override
		public void run() {
			mLineInfoList = mDataHelper.getLineList(LineInfo.LINEID,"ASC");
			for(int n = 0;n<mLineInfoList.size();n++){
				//Log.d("initDataThread","lineid = "+mLineInfoList.get(n).getLineid()+" lineName = "+mLineInfoList.get(n).getLinename());
				mLineInfoList.get(n).setStationInfoList(mDataHelper.QueryByStationLineNo(mLineInfoList.get(n).getLineid()));
				for(int i= 0 ;i<mLineInfoList.get(n).getStationInfoList().size();i++){
					mLineInfoList.get(n).getStationInfoList().get(i).colorId = mLineInfoList.get(n).colorid;
				}

			}
			//----------------------------------------
			currentIndex = mLineInfoList.get(0).getLineid();
			//单一线路-------------------------------------
			initLineMap(currentIndex);
			Message msg = new Message();
			msg.what = 1;
			mHandler.sendMessage(msg);
		}
	}

	private void initLineColorMap(){
		int colorRow = mLineInfoList.size()/ LineMapColor.ROWMAXCOUNT+1;
		Bitmap bitmap1 = Bitmap.createBitmap((int) lineMap.getViewWidth(), MarkObject.ROWCOLORHEIGHT*colorRow,
                Bitmap.Config.ARGB_8888);
		bitmap1.eraseColor(this.getResources().getColor(R.color.white));//填充颜色
		lineMap.setBitmap(bitmap1);
		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) lineMap.getLayoutParams();
        // 取控件aaa当前的布局参数
        linearParams.height = MarkObject.ROWCOLORHEIGHT*colorRow;
        lineMap.setLayoutParams(linearParams); // 使设置好的布局参数应用到控件
		float disx = lineMap.getViewWidth()/LineMapColorView.ROWMAXCOUNT;
		//Log.d("zxc", "disx = "+disx+this.getResources().getColor(lineColor[0]));
		for(int n=0;n<mLineInfoList.size();n++){
			MarkObject markObject = new MarkObject();
			float row = n/(LineMapColor.ROWMAXCOUNT)+1;
			float cloume = n%(LineMapColor.ROWMAXCOUNT);
			float x = cloume*disx+disx/2-MarkObject.rectSizewidth/2;
			float y = row*MarkObject.ROWHEIGHT-MarkObject.ROWHEIGHT/2-MarkObject.rectSizeHeight/2;
			//Log.d("zxc", "row = "+row+" cloume = "+cloume+" x = "+x+" y = "+y);
			markObject.setLineid(mLineInfoList.get(n).getLineid());
			markObject.setX(x);
			markObject.setY(y);
			markObject.setName(mLineInfoList.get(n).getLinename());
			markObject.setCurrentsize(MarkObject.rectSizeHeight);
			markObject.setColorId(mLineInfoList.get(n).colorid);
			markObject.setmBitmap(drawNewColorBitmap(MarkObject.rectSizewidth,MarkObject.rectSizeHeight,markObject.getColorId()));
			markObject.setMarkListener(new MarkClickListener() {

				@Override
				public void onMarkClick(int x, int y,final MarkObject markObject) {
					// TODO Auto-generated method stub
					if(currentIndex != markObject.getLineid() || sceneMap.getFullScree()){
						currentIndex = markObject.getLineid();
						currentLineInfoText.setBackgroundColor(markObject.getColorId());
						//Toast.makeText(MainViewActivity.this, markObject.getName(), Toast.LENGTH_SHORT).show();
						//initLineMap(markObject.getLineid());
						new Thread() {
							@Override
							public void run() {
								initLineMap(markObject.getLineid());
								Message msg = new Message();
								msg.what = 2;
								mHandler.sendMessage(msg);
							}
						}.start();
					}
				}
			});
			lineMap.addMark(markObject);
		}
	}
	
	private void initLineMap(int lineId){

		sceneMap.setFullScree(false);
		sceneMap.clearMark();
		for (int j=0;j<mLineInfoList.size();j++){
			if(mLineInfoList.get(j).getLineid() == lineId){
				mStationInfoList = mLineInfoList.get(j).getStationInfoList();
				//currentLineInfoText.setText(mLineInfoList.get(j).getLinename()+"\n"+mLineInfoList.get(j).getLineinfo());
				//currentLineInfoText.setBackgroundColor(mLineInfoList.get(j).colorid);
				break;
			}
		}
		int countRow = mStationInfoList.size()/LineMap.ROWMAXCOUNT+1;
		/*int countRow = mStationInfoList.size()/LineMap.ROWMAXCOUNT+1;
		Bitmap bitmap = Bitmap.createBitmap((int) sceneMap.getViewWidth(), MarkObject.ROWHEIGHT*countRow,  
                Bitmap.Config.ARGB_8888);
		bitmap.eraseColor(this.getResources().getColor(R.color.white));//填充颜色  
		sceneMap.setBitmap(bitmap);*/
		float initX = 1f/LineMap.ROWMAXCOUNT/2f;
		float initY = 1f/countRow/2;
		
		for(int n=0;n<mStationInfoList.size();n++){
			MarkObject markObject = new MarkObject();
			float row = n/(LineMap.ROWMAXCOUNT)+1;
			float cloume = n%(LineMap.ROWMAXCOUNT);
			
			float x = cloume/(LineMap.ROWMAXCOUNT)+initX;
			float y = row/countRow-initY;
			//Log.d("zxc", "n = "+n+" row = "+row+" colume = "+cloume+" x = "+x+" y = "+y);
			markObject.setMapX(x);
			markObject.setMapY(y);
			markObject.setName(mStationInfoList.get(n).getCname().split(" ")[0]);
			markObject.setColorId(mStationInfoList.get(n).colorId);
			markObject.setRadius(MarkObject.size/3);
			markObject.setCurrentsize(MarkObject.size+MarkObject.DIFF);
			markObject.setmBitmap(drawNewBitmap(MarkObject.size/3,MarkObject.size+MarkObject.DIFF,MarkObject.size+MarkObject.DIFF,markObject.getColorId()));
			markObject.setMarkListener(new MarkClickListener() {

				@Override
				public void onMarkClick(int x, int y,MarkObject markObject) {
					// TODO Auto-generated method stub
					Toast.makeText(MainViewActivity.this, markObject.getName(), Toast.LENGTH_SHORT)
							.show();
				}
			});
			sceneMap.addMark(markObject);
		}

	}
	/**
	* 往图片上写入文字、图片等内容
	*/
	private Bitmap drawNewBitmap(float radius,int width,int hight,int colorId) {	
		Bitmap bitmap = Bitmap.createBitmap(width, hight,  Bitmap.Config.ARGB_8888);
		bitmap.eraseColor(this.getResources().getColor(R.color.white)); 
		Canvas canvas = new Canvas(bitmap);  
		Paint paint = new Paint();  
		paint.setColor(colorId);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(radius/2);
		paint.setStyle(Paint.Style.STROKE); 
		canvas.drawCircle(width/2, hight/2, radius, paint);
		return bitmap;
	}
	
	/**
	* 往图片上写入文字、图片等内容
	*/
	private Bitmap drawNewColorBitmap(int width,int hight,int colorId) {	
		Bitmap bitmap = Bitmap.createBitmap(width, hight,  Bitmap.Config.ARGB_8888);
		bitmap.eraseColor(colorId);
		/*Canvas canvas = new Canvas(bitmap);  
		Paint paint = new Paint();  
		paint.setColor(this.getResources().getColor(colorId));
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL); 
		canvas.drawRect(new Rect(0,width,0,hight), paint);*/
		return bitmap;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	public void dbWriteFinishNotif(){
		initData();
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		ReadExcelDataUtil.getInstance().removeDbWriteFinishListener(this);
		lineMap.releaseSource();
		sceneMap.releaseSource();
		mLineInfoList.clear();
		mStationInfoList.clear();
	}
}
