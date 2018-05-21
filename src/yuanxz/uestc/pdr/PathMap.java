package yuanxz.uestc.pdr;

import android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class PathMap extends View{
	
	//地图比例
	public static final float X_PLOTTING_SCALE=(float)24.75;
	public static final float Y_PLOTTING_SCALE=(float)19.59;
	
	public static final float START_X=12;//地图起点x
	public static final float START_Y=153;//地图起点y
	
	public static final String TAG="PathMap";
	
	private Bitmap cachedBitmap;//双缓冲实现
	private Canvas cachedCanvas;
	private Paint pathPaint;
	private Path[] path;
	
	private int screenWidth;
	private int screenHeight;

	private float[] preX;
	private float[] preY;
	
	private EndPointUpdateListener listener;
	
	public PathMap(Context context,int pathNum) {
		super(context);
		// TODO Auto-generated constructor stub
		getWindowWidthHeight(context);
		init(pathNum);
		setBackgroundColor(Color.BLACK);
	}

	
	public PathMap(Context context,int width,int height,int pathNum){
		super(context);
		if(width<=0||height<=0){
			getWindowWidthHeight(context);
		}else{
			screenHeight=height;
			screenWidth=width;
		}
		init(pathNum);
	}
	
	
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		canvas.drawBitmap(cachedBitmap, 0, 0, pathPaint);
	}

	
	/**
	 * 设置起点
	 * @param x
	 * @param y
	 */
	public void setStartPoint(float x,float y,int id){
		for(int i=0;i<preX.length;i++){
			preX[i]=x-START_X;
			preY[i]=y-START_Y;
		}
		if(listener!=null){
			listener.endPointUpdated(x-START_X,y-START_Y ,id);
		}
	}
	
	/**
	 * 绘制路径
	 * @param stepLen
	 * @param direction
	 */
	public void lineToNextStandpoint(float stepLen,float direction,int id){
		float xOffset=calculateXOffset(stepLen, direction);
		float yOffset=calculateYOffset(stepLen, direction);
		if(isStartPointSeted(id)){
			System.out.println("preX="+preX+", preY="+preY);
			System.out.println("xOffset="+xOffset);
			System.out.println("yOffset="+yOffset);
			drawPathTo(preX[id]+xOffset, preY[id]+yOffset,id);
		}else{
			preX[id]=xOffset;
			preY[id]=yOffset;
			Log.d(TAG, "初始化坐标绘制位置到（"+preX+","+preY+")");
		}
	}
	
	
	
	
	/**
	 * 绘制路径到
	 * @param x
	 * @param y
	 */
	public void drawPathTo(float x,float y,int id){
		if(x!=Float.NaN&&y!=Float.NaN){
			if(isStartPointSeted(id)){//绘制一段
				path[id].moveTo(preX[id], preY[id]);
				path[id].quadTo(preX[id], preY[id], x, y);
			}else{
				System.out.println("初始化坐标绘制位置到（"+x+","+y+")");
			}
			preX[id]=x;
			preY[id]=y;
			cachedCanvas.drawPath(path[id], pathPaint);
			path[id].reset();
			invalidate();
			//通知路径改变
			if(listener!=null){
				listener.endPointUpdated(x,y,id );
			}
		}else{
			Log.e(TAG, "坐标计算错误！");
		}
	}
	
	public void setEndPointUpdateListener(EndPointUpdateListener listener){
		this.listener=listener;
	}
	
	
	/**
	 * 起点是否设置过
	 * @return
	 */
	private boolean isStartPointSeted(int id){
		if(Float.isNaN(preX[id])||Float.isNaN(preY[id])){
			return false;
		}else{
			return true;
		}
	}
	
	
	/**
	 * 根据步长和当前方位来计算X向和Y向的位移，
	 * 计算公式为：newX=X-len*sin(arc),newY=Y+len*cos(arc)
	 * @param stepLen
	 * @param direction
	 * @return
	 */
	private float calculateXOffset(float stepLen,float direction){
//		System.out.println(stepLen+"*sin("+direction+")="+(stepLen*Math.sin(direction*Math.PI/180)));
//		direction=direction-STANDARD_Y_ORIENTATION;//换算成地图方向
		float xOffset=(float) (stepLen*Math.sin(direction*Math.PI/180));
		xOffset*=X_PLOTTING_SCALE;//换算成地图像素点
		return (float)( -xOffset);
	}
	
	/**
	 * 根据步长和当前方位来计算X向和Y向的位移，
	 * 计算公式为：newX=X-len*sin(arc),newY=Y+len*cos(arc)
	 * @param stepLen
	 * @param direction
	 * @return
	 */
	private float calculateYOffset(float stepLen,float direction){
//		direction=direction-STANDARD_Y_ORIENTATION;//换算成地图方向
		float yOffset=(float)(stepLen*Math.cos(direction*Math.PI/180));
		yOffset*=Y_PLOTTING_SCALE;
		return yOffset;
	}
	
	
	private void init(int pathNum){
		cachedBitmap=Bitmap.createBitmap(screenWidth, screenHeight, Config.ARGB_8888);
		cachedCanvas=new Canvas(cachedBitmap);
		preX=new float[pathNum];
		preY=new float[pathNum];
		for(int i=0;i<pathNum;i++){
			preX[i]=Float.NaN;
			preY[i]=Float.NaN;
		}
//		preX=640;
//		preY=360;
		path=new Path[pathNum];
		for(int i=0;i<pathNum;i++){
			path[i]=new Path();
		}
		setPaintStyle();
	}
	
	/**
	 * 设置长宽
	 * @param context
	 */
	private void getWindowWidthHeight(Context context){
		WindowManager wManager=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Display display=wManager.getDefaultDisplay();
		screenHeight=display.getHeight();
		screenWidth=display.getWidth();
	}
	
	/**
	 *设置画壁风格 
	 */
	private void setPaintStyle(){
		pathPaint=new Paint();
		pathPaint.setColor(Color.RED);
		pathPaint.setStyle(Paint.Style.STROKE);
		pathPaint.setStrokeWidth(2);
		pathPaint.setAntiAlias(true);
		pathPaint.setDither(true);
	}
}
