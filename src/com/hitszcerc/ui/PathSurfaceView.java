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
	Activity mActivity;//activity����
	Paint paint;//��������
	int span;
	
	/**
	 * ���ڻ��Ƶ��߳�
	 */
	private Thread t;
	/**
	 * �����߳̿����ر�
	 */
	private boolean isRunning;
	
	public PathSurfaceView(Activity mActivity) {
		super(mActivity);
		this.mActivity=mActivity;
		mHolder=getHolder();
		mHolder.addCallback(this);
		//�ɻ�ý���
		setFocusable(true);
		setFocusableInTouchMode(true);
		//���ó���
		setKeepScreenOn(true);
		
		paint = new Paint();//��������
		paint.setAntiAlias(true);//�򿪿����
		
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
		
		//���Ͻ��л���
		while(isRunning){
			draw();
		}
	}

	private void draw() {
		try {
			mCavas=mHolder.lockCanvas();
			
			if(mCavas!=null){
				int map[][]=MapList.map;//��ȡ��ͼ
				int row=map.length;//��ͼ����
				
				int col=map[0].length;//��ͼ����
				
				DisplayMetrics dm = new DisplayMetrics();
				mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
				int width = dm.widthPixels;//���
				span = width/col;
				mCavas.drawARGB(255, 128, 128, 128);//���ñ�����ɫ		

				for(int i=0;i<row;i++)//���Ƶ�ͼ
				{
					for(int j=0;j<col;j++)
					{
						if(map[i][j]==1)
						{
							paint.setColor(Color.BLACK);	//���û�����ɫΪ��ɫ	
						}
						else if(map[i][j]==0)
						{
							paint.setColor(Color.WHITE);//���û�����ɫΪ��ɫ
						}else{
							paint.setColor(Color.BLUE);
						}
						mCavas.drawRect(2+j*(span+1),2+i*(span+1),2+j*(span+1)+span,2+i*(span+1)+span, paint);//���ƾ���
						
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
		//���Ƴ�����
		Bitmap bitmapTmpS=BitmapFactory.decodeResource(mActivity.getResources(),R.drawable.source);
		mCavas.drawBitmap(bitmapTmpS, MapList.source[0]*(span+1),MapList.source[1]*(span+1) , paint);
		//����Ŀ���
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
		//��ȡ����·�����
		paint.setColor(Color.BLACK);//���û��ʺ�ɫ
		paint.setStrokeWidth(3);//���û��ʿ��	
		for(int i=1;i<AStar.resultList.size();i++){
			mCavas.drawLine//�����߶�
		    (
		    	AStar.resultList.get(i-1).getY()*(span+1)+span/2+2,AStar.resultList.get(i-1).getX()*(span+1)+span/2+2,
		    	AStar.resultList.get(i).getY()*(span+1)+span/2+2,AStar.resultList.get(i).getX()*(span+1)+span/2+2,paint
		    );
		}
		
		
	}

	public void repaint(SurfaceHolder holder)
	{
		Canvas canvas = holder.lockCanvas();//��ȡ����
		try{
			synchronized(holder){
				//clear(mCavas);
				onDraw(canvas);//����
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
