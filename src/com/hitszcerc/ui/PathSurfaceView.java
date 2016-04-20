package com.hitszcerc.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.hitszcerc.activity.R;
import com.hitszcerc.astar.AStar;
import com.hitszcerc.astar.MapList;



public class PathSurfaceView extends SurfaceView implements Callback, Runnable {

	private SurfaceHolder mHolder;
	private Canvas mCavas;
	Activity mActivity;//activity引用
	Paint paint;//画笔引用
	int span;
	
	/**
	 * 用于绘制的线程
	 */
	private Thread t;
	/**
	 * 控制线程开启关闭
	 */
	private boolean isRunning;
	
	public PathSurfaceView(Activity mActivity) {
		super(mActivity);
		this.mActivity=mActivity;
		mHolder=getHolder();
		mHolder.addCallback(this);
		//可获得焦点
		setFocusable(true);
		setFocusableInTouchMode(true);
		//设置常量
		setKeepScreenOn(true);
		
		paint = new Paint();//创建画笔
		paint.setAntiAlias(true);//打开抗锯齿
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		isRunning=true;
		t=new Thread(this);
		t.start();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isRunning=false;
	}

	@Override
	public void run() {
		
		//不断进行绘制
		while(isRunning){
			draw();
		}
	}

	private void draw() {
		try {
			mCavas=mHolder.lockCanvas();
			
			if(mCavas!=null){
				int map[][]=MapList.map;//获取地图
				int row=map.length;//地图行数
				
				int col=map[0].length;//地图列数
				
				DisplayMetrics dm = new DisplayMetrics();
				mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
				int width = dm.widthPixels;//宽度
				span = width/col;
				mCavas.drawARGB(255, 128, 128, 128);//设置背景颜色		

				for(int i=0;i<row;i++)//绘制地图
				{
					for(int j=0;j<col;j++)
					{
						if(map[i][j]==1)
						{
							paint.setColor(Color.BLACK);	//设置画笔颜色为黑色	
						}
						else if(map[i][j]==0)
						{
							paint.setColor(Color.WHITE);//设置画笔颜色为白色
						}else{
							paint.setColor(Color.BLUE);
						}
						mCavas.drawRect(2+j*(span+1),2+i*(span+1),2+j*(span+1)+span,2+i*(span+1)+span, paint);//绘制矩形
						
					}
				}
				map=null;
				drawtarget();
				drawPath();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(mCavas!=null){
				mHolder.unlockCanvasAndPost(mCavas);
			}
		}
		
	}
	
	private void drawtarget() {
		//绘制出发点
		Bitmap bitmapTmpS=BitmapFactory.decodeResource(mActivity.getResources(),R.drawable.source);
		mCavas.drawBitmap(bitmapTmpS, MapList.source[0]*(span+1),MapList.source[1]*(span+1) , paint);
		//绘制目标点
		Bitmap bitmapTmpT=BitmapFactory.decodeResource(mActivity.getResources(),R.drawable.target);
		mCavas.drawBitmap(bitmapTmpT, MapList.target[0]*(span+1),MapList.target[1]*(span+1), paint);
		
	}
	
	public void clear(Canvas aCanvas) { 
		Paint paint = new Paint(); 
		paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR)); 
		aCanvas.drawPaint(paint); 
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC)); 
		invalidate(); 
		}

	private void drawPath() {
		//获取搜索路径结果
		paint.setColor(Color.BLACK);//设置画笔黑色
		paint.setStrokeWidth(3);//设置画笔宽度	
		for(int i=1;i<AStar.resultList.size();i++){
			mCavas.drawLine//绘制线段
		    (
		    	AStar.resultList.get(i-1).getY()*(span+1)+span/2+2,AStar.resultList.get(i-1).getX()*(span+1)+span/2+2,
		    	AStar.resultList.get(i).getY()*(span+1)+span/2+2,AStar.resultList.get(i).getX()*(span+1)+span/2+2,paint
		    );
		}
		
		
	}

	public void repaint(SurfaceHolder holder)
	{
		Canvas canvas = holder.lockCanvas();//获取画布
		try{
			synchronized(holder){
				//clear(mCavas);
				onDraw(canvas);//绘制
				//drawPath();
				//AStar.resultList=null;
			}			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if(canvas != null){
				holder.unlockCanvasAndPost(canvas);
			}
		}
	}
	

}
