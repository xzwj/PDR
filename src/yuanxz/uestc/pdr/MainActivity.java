package yuanxz.uestc.pdr;


import java.util.Observable;
import java.util.Observer;

import com.example.pdr.R;

import yuanxz.uestc.message.StepCountMessage;
import yuanxz.uestc.stepcount.StepCountService;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.service.dreams.DreamService;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewDebug.FlagToString;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener {

	public static final float STANDARD_Y_ORIENTATION=80;//自定义正方向
	public static final int DYNAMIC_WINDOWS_SIZE = 4;//滑动窗口大小
	
	public static final int PATH1_ID=0;
	public static final int PATH2_ID=1;
	public static final int PATH_ALL_ID=2;
	
	public static final int START = 0;
	public static final int STOP = 1;
	public static final int RESET = 2;
	
	private int isStepCount;
	
	private SensorManager sensorManager;
	
	private TextView tv_schema1;
	private TextView tv_schema2;
	
	private TextView currentlen;
	private TextView totallen;
	private TextView stepnum;
	private TextView orientation;
	private TextView findir;
	
	private ImageView iv_mapArrow;
	private ImageView iv_mapArrow2;
	private RelativeLayout rl_mapInfo;

	
	private Messenger inMessenger;
	private Messenger outMessenger;
	
	public int stepNum;//步数
	public int steptmp;
	public boolean isExit;
	
	private double len1;//行走的总长度
	private double len2;//行走的总长度
	private double len3;//行走的总长度
	private double len4;//行走的总长度
	private double len5;
	private double kalmanLen;
	private double schema1Len2;
	private double deltaOrient;
	
	private int stepnumtmp;
	
	private float[] magneticFiledValues;
	private float[] accelerationValues;
	private float[] gyroscopeValues;
	private float[] tmp_gyro;
	

	
	private float[] angle;
	
	private static final float NS2S = 1.0f / 1000000000.0f;
	private final float[] deltaRotationVector = new float[4];
	private float timestamp;
	
	
	private float currentDirection;
	private float currentX;
	private float currentY;
	private float tmp_currentDirection;
	
	private float tmp20;
	
	private float dynamic_windows[];
	
	private PathMap map;	
	public ImageView iv;
	
	private int i = 0;//用于判断第一次
	private int j = 0;//滑动窗口小标记录转弯
	
	boolean isMultistep = false;
	
	private Handler mHandler = null;
	
	private Button startbutton;
	private Button resetbutton;
	private Button stopbutton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 隐去标题栏（应用程序的名字）
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 隐去状态栏部分(电池等图标和一切修饰部分)
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		

		iv = (ImageView)findViewById(R.id.anim_imageView);
		iv.setBackgroundResource(R.drawable.walking_anim);
		
		
		startbutton= (Button) findViewById(R.id.start_button);
		startbutton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isStepCount = START;
//				AnimationDrawable anim = (AnimationDrawable)iv.getBackground();
//				anim.start();
			}
			
		});
		
		stopbutton= (Button) findViewById(R.id.stop_button);
		stopbutton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isStepCount = STOP;
				AnimationDrawable anim = (AnimationDrawable)iv.getBackground();
				anim.stop();
			}
			
		});
		
		resetbutton = (Button)findViewById(R.id.reset_button);
		resetbutton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isStepCount = RESET;
				kalmanLen=0;
				stepNum=0;
				kalmanLen=0;
				String str_stepnum;
				java.text.DecimalFormat  df = new   java.text.DecimalFormat("#.##");
				str_stepnum = Integer.toString(stepNum);
				stepnum.setText(str_stepnum);
				currentlen.setText(df.format(0));
				totallen.setText(df.format(0));
				AnimationDrawable anim = (AnimationDrawable)iv.getBackground();
				anim.stop();
			}			
		});
		
		mHandler = new Handler() {  
            @Override  
            public void handleMessage(Message msg) {
            	AnimationDrawable anim = (AnimationDrawable)iv.getBackground();
            	switch (msg.what)
            	{
            	case START:
    				anim.start();
            		break;
            	case STOP:
    				anim.stop();
            		break;
            	default:
            		break;
            	}             
                super.handleMessage(msg);  
            }  
        };  
		
		setupRes();
		Intent intent=new Intent(this, StepCountService.class);
		bindService(intent, conn, BIND_AUTO_CREATE);
		
		init();
		
	}

	private int getStepNum()
	{
		return stepNum;
	}
	
	private double getstrideLength()
	{
		return schema1Len2;
	}
	
	private double getChangesOfOrientation()
	{
		return deltaOrient;
	}
	
	
	
//	@Override
//	protected void onPause() {
//		// TODO Auto-generated method stub
//		sensorManager.unregisterListener(this);
//		super.onPause();
//	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		if(sensorManager!=null){
			sensorManager.registerListener(
					this, 
					sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), 
					SensorManager.SENSOR_DELAY_GAME);
			sensorManager.registerListener(
					this, 
					sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
					SensorManager.SENSOR_DELAY_GAME);
			sensorManager.registerListener(
					this, 
					sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), 
					SensorManager.SENSOR_DELAY_GAME);
			sensorManager.registerListener(
					this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
					SensorManager.SENSOR_DELAY_UI);
			sensorManager.registerListener(
					this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
					SensorManager.SENSOR_DELAY_UI);
		}
		super.onResume();
	}

	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unbindService(conn);
		super.onDestroy();
//		System.exit(0);
	}

	
	/**
	 * 初始化资源
	 */
	private void setupRes(){
		
//		tv_schema1=(TextView)findViewById(R.id.textView_schema1);
		
		steptmp = 0;
		
		stepnum=(TextView)findViewById(R.id.stepnum_textView);		
		currentlen=(TextView)findViewById(R.id.currentlen_textView);
		totallen=(TextView)findViewById(R.id.totallen_textView);
		orientation=(TextView)findViewById(R.id.orientation_textView);
		findir = (TextView)findViewById(R.id.final_dir_textView);
		
		stepnum.setTextSize(40);
		currentlen.setTextSize(40);
		totallen.setTextSize(40);
//		orientation.setTextSize(40);
		
		stepnum.setTypeface(Typeface.createFromAsset(getAssets(), "font/Roboto-Light.ttf"));
		currentlen.setTypeface(Typeface.createFromAsset(getAssets(), "font/Roboto-Light.ttf"));
		totallen.setTypeface(Typeface.createFromAsset(getAssets(), "font/Roboto-Light.ttf"));
		orientation.setTypeface(Typeface.createFromAsset(getAssets(), "font/Roboto-Light.ttf"));
		findir.setTypeface(Typeface.createFromAsset(getAssets(), "font/Roboto-Light.ttf"));
//		iv_mapArrow=(ImageView)findViewById(R.id.imageView_mapArrow);
//		iv_mapArrow2=(ImageView)findViewById(R.id.imageView_mapArrow2);
		
		Thread m = new Monitor();
		m.start();
		
		rl_mapInfo=(RelativeLayout)findViewById(R.id.relativeLayout_mapInfo);
	}
	
	/**
	 * 监听步数变化，根据变化控制开始或者停止播放动画
	 * @author deathym
	 *
	 */
	public class Monitor extends Thread {  
        public void run() {  
            while (!Thread.currentThread().isInterrupted()) {  
            	
                Message msg = new Message();
                if(steptmp!=stepNum && isStepCount!=STOP && isStepCount!=RESET)
                {
                	msg.what = START;
                }
                else msg.what = STOP;
                steptmp = stepNum;
                mHandler.sendMessage(msg);  
                try {  
                    Thread.sleep(500);  
                } catch (InterruptedException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
    }
	
	/**
	 * 双击返回键退出
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return false;
		} 
		if(keyCode == KeyEvent.KEYCODE_HOME)
		{
			moveTaskToBack(false);
			return true;
		}
		else {
			return super.onKeyDown(keyCode, event);
		}
	}

	public void exit() {
		Handler mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg){
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				isExit = false;
			}
		};
		if (!isExit) {
			isExit = true;
			Toast.makeText(getApplicationContext(), "再按一次退出程序",
					Toast.LENGTH_SHORT).show();
			mHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
			System.exit(0);
		}
	}

	
	/**
	 * 初始化
	 */
	private void init(){
		
		magneticFiledValues=new float[3];
		accelerationValues=new float[3];
		gyroscopeValues =new float[3];
		angle = new float[3];		
		tmp_gyro = new float[3];
		tmp_gyro[0] = 0;
		tmp_gyro[1] = 0;
		tmp_gyro[2] = 0;
		
		sensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
		inMessenger=new Messenger(new MyUIUpdateHandler());
		
		stepNum=0;
		len1=0;
		len2=0;
		len3=0;
		len4=0;
		len5=0;
		kalmanLen=0;
		schema1Len2 = 0;
		deltaOrient = 0;
		isStepCount=2;		
		tmp20 = 0;
		
		currentDirection=0;
		currentX=0;
		currentY=0;		
		tmp_currentDirection = currentDirection;
		dynamic_windows = new float[DYNAMIC_WINDOWS_SIZE];
		
		for(int i = 0;i<DYNAMIC_WINDOWS_SIZE;i++)
		{
			dynamic_windows[i] = 0;
		}
			
		
		stepnumtmp = stepNum;	
		
		map=new PathMap(this,rl_mapInfo.getWidth(),rl_mapInfo.getHeight(),2);
		map.setStartPoint(0,0,PATH_ALL_ID);
		rl_mapInfo.addView(map);
		//map.setEndPointUpdateListener(listener);
	}
	
	
	/**
	 * 开始计步
	 */
	private void startCount(float[] value,float[] orientation){
		if(outMessenger!=null){
			try {
				Bundle data=new Bundle();
				data.putFloatArray("ACCELERATIONS", value);
				data.putFloatArray("ORIENTATIONS", orientation);
				sendMsg(StepCountMessage.MSG_START_STEP_COUNT, data);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println("发送开始计步消息失败！");
			}
		}
	}
	
	/**
	 * 停止计步
	 */
	private void stopCount()
	{
		if(outMessenger!=null){
			Bundle data = new Bundle();
			try {
				sendMsg(StepCountMessage.MSG_STOP_STEP_COUNT, data);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * 发送消息
	 * @param what
	 * @param data
	 * @throws RemoteException
	 */
	private void sendMsg(int what,Bundle data) throws RemoteException{
		Message message=Message.obtain();
		message.replyTo=inMessenger;
		message.what=what;
		message.setData(data);
		outMessenger.send(message);
	}
	
	
	private ServiceConnection conn=new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			outMessenger=new Messenger(service);
		}
	};
	
	
	private class MyUIUpdateHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			
			case StepCountMessage.MSG_ONE_STEP:
				Bundle data=msg.getData();
				double schema1Len2=data.getDouble(StepCountService.KALMAN_STEP_LEN);
				java.text.DecimalFormat  df = new   java.text.DecimalFormat("#.##");
				switch(isStepCount)
				{
				case START:
					stepNum=data.getInt("CURRENT_STEP_KEY");					
					
					if(schema1Len2!=0){
						
						String str_stepnum;
						str_stepnum = Integer.toString(stepNum);
						kalmanLen+=schema1Len2;
						stepnum.setText(str_stepnum);
//						strBuilder.append("当前步数："+stepNum+'\n');
						currentlen.setText(df.format(schema1Len2));
//						strBuilder.append("当前步长(m)："+df.format(schema1Len2)+'\n');
						totallen.setText(df.format(kalmanLen));

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
						
						/**
						 * 处理掉第一次，跟现实同一，第一步的时候没有任何转弯
						 */
						if(i == 0)
						{
							tmp_currentDirection = currentDirection;
							i++;
						}						
						/**
						 * 这里为了同一用数字表示，我们定义得到的方向向左为正，向右为负
						 */
						int tmp_j=0;
						float dir=0;
						float delta = tmp_currentDirection-currentDirection;
						float tmp = Math.abs(tmp_currentDirection-currentDirection);
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////						
						if(tmp < 180)
						{
							if(delta <= 0 && delta >=-160)
							{
								orientation.setText("右1:"+String.valueOf(tmp));
								if(delta <= -40)
								{
									findir.setText("右1:"+String.valueOf(tmp));
								}
								else
								{
									Check(-1*tmp);
								}								
							}
							else if( delta > 0 && delta < 160)
							{
								orientation.setText("左1:"+String.valueOf(tmp));
								if(delta >= 40 )
								{
									findir.setText("左1:"+String.valueOf(tmp));
								}
								else
								{
									Check(tmp);
								}								
							}
							else
							{
								orientation.setText("反向1");
							}
						}
						else
						{
							float trueDelta;
							if(delta < 0)
							{
								trueDelta = 360 + delta;
								tmp = trueDelta;
								if(50 <= trueDelta &&trueDelta < 160)
								{
									orientation.setText("左2:"+String.valueOf(trueDelta));
								}
								else if(trueDelta <= 40)
								{
									Check(tmp);
								}
								else
								{
									orientation.setText("反向2");
								}
							}
							else
							{
								trueDelta = 360 - delta;
								tmp = trueDelta;
								if(50< trueDelta && trueDelta < 160)
								{
									orientation.setText("右2:"+String.valueOf(trueDelta));
								}
								else if(trueDelta <= 40)
								{
									Check(-1*tmp);
								}
								else
								{
									orientation.setText("反向2");
								}
							}
						}
						tmp_currentDirection = currentDirection;
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
						
//						if(tmp > 180)
//						{
//							if(tmp_currentDirection-currentDirection>0)
//								deltaOrient = 360 - (tmp_currentDirection-currentDirection);
//							else
//								deltaOrient = 360 + (tmp_currentDirection-currentDirection);
//						}
//						else
//						{
//							deltaOrient = Math.abs(tmp_currentDirection-currentDirection);
//						}						
//						orientation.setText(String.valueOf(deltaOrient));
//						tmp_currentDirection = currentDirection;
//						orientation.setText(String.valueOf(currentDirection));
						
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
						
//						if(tmp>=10)
//						{
//							if(tmp_currentDirection-currentDirection > 0 )
//							{
//								/**
//								 * 这是向右转的特殊情况，产生阶跃信号，即上一次在第1象限，这次在第4项线，实际为左转但是为正值
//								 */
//								if(tmp_currentDirection-currentDirection>180)
//								{				
//									dir = -1*(360-(tmp_currentDirection-currentDirection));
//									dynamic_windows[j] = -1*(360-(tmp_currentDirection-currentDirection));
//									tmp_j = j;
//									j++;
//									j = j%DYNAMIC_WINDOWS_SIZE;
//									
//									orientation.setText("右:"+String.valueOf(dir));	
//								}
//								else
//								{
//									dir = tmp_currentDirection-currentDirection;
//									dynamic_windows[j] = tmp_currentDirection-currentDirection;
//									tmp_j = j;
//									j++;
//									j = j%DYNAMIC_WINDOWS_SIZE;
//									
//									orientation.setText("左"+String.valueOf(dir));
//								}															
//							}							
//							else
//							{
//								/**
//								 * 这是向左转的特殊情况，产生阶跃信号，即上一次在第4象限，这次在第1项线，实际为左转但是为负值
//								 */
//								if(tmp_currentDirection-currentDirection<-180)
//								{
//									dir = 360+tmp_currentDirection-currentDirection;
//									dynamic_windows[j] = 360+tmp_currentDirection-currentDirection;
//									tmp_j = j;
//									j++;
//									j = j%DYNAMIC_WINDOWS_SIZE;
//									
//									orientation.setText("左:"+String.valueOf(dir));
//								}
//								else
//								{
//									dir = -1*(currentDirection-tmp_currentDirection);
//									dynamic_windows[j] = -1*(currentDirection-tmp_currentDirection);
//									tmp_j = j;
//									j++;
//									j = j%DYNAMIC_WINDOWS_SIZE;
//									
//									orientation.setText("右"+String.valueOf(dir));
//								}								
//							}	
//							tmp_currentDirection = currentDirection;
//						}
//								
//
//						if(Math.abs(dir)>=60)
//						{
//							if(dir>0)
//							{
//								findir.setText("←左"+String.valueOf(Math.abs(dir)));
//							}
//							else
//							{
//								findir.setText("右→"+String.valueOf(Math.abs(dir)));
//							}
//						}
//						else
//						{							
//							float final_dir=0;
//							dynamic_windows[j] = dir;
//							tmp_j = j;
//							j++;
//							j = j%DYNAMIC_WINDOWS_SIZE;
//							
//							for(int i = 0;i<DYNAMIC_WINDOWS_SIZE;i++)
//							{
//								final_dir = final_dir+dynamic_windows[i];
//							}							
//							if(Math.abs(final_dir) > 80)
//							{
//								if(final_dir>0)
//								{								
//									findir.setText("←左"+String.valueOf(final_dir));	
//									for(int i = 0;i<DYNAMIC_WINDOWS_SIZE;i++)
//									{
//										dynamic_windows[i] = 0;
//									}
//								}
//								else
//								{
//									findir.setText("*右→"+String.valueOf(-1*final_dir));
//									for(int i = 0;i<DYNAMIC_WINDOWS_SIZE;i++)
//									{
//										dynamic_windows[i] = 0;
//									}
//								}
//							}
//							else
//							{
//								findir.setText("直走");
//							}
//							
//						}
						
					

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
					}

					break;
				}

			default:
				break;
			}
			super.handleMessage(msg);
		}
	}

	public void Check(float tmp)
	{
		dynamic_windows[j] = tmp;
		int tmp_j = j;
		j++;
		j = j%DYNAMIC_WINDOWS_SIZE;
		float final_dir=0;
		for(int i = 0;i<DYNAMIC_WINDOWS_SIZE;i++)
		{
			final_dir = final_dir+dynamic_windows[i];
		}
		if(Math.abs(final_dir) <= 60)
		{
			findir.setText("直走");
		}
		else if(final_dir > 60)
		{
			findir.setText("左："+final_dir);
		}
		else
		{
			findir.setText("右："+final_dir);
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		float[] value=event.values.clone();//当监听多个传感器时，需要用深拷贝，防止出现数据覆盖
		StringBuilder strBuilder=new StringBuilder();
		switch (event.sensor.getType()) {
		case Sensor.TYPE_LINEAR_ACCELERATION:
			strBuilder=new StringBuilder();			
			
			float[] orientations=getOrientation();
			
			switch(isStepCount){
				case START:
					startCount(value,getOrientation());
					break;
				case STOP:
					break;
				case RESET:
					stopCount();
					break;
			}
				
			
			break;
		case Sensor.TYPE_ACCELEROMETER:
			accelerationValues=event.values.clone();
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			magneticFiledValues=event.values.clone();
		
			break;
		case Sensor.TYPE_GYROSCOPE:
			gyroscopeValues=event.values.clone();

			   

			break;
		default:
			break;
		}
	}


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 获取当前各个倾斜方向
	 * @return
	 */
	private float[] getOrientation(){
		float[] orientationValues=new float[3];
		float[] R=new float[9];
		SensorManager.getRotationMatrix(R, null, accelerationValues, magneticFiledValues);
		SensorManager.getOrientation(R, orientationValues);
		for(int i=0;i<3;i++){
			orientationValues[i]=(float)Math.toDegrees(orientationValues[i]);
		}
		//为了和数学上的方向的正负表示一致，这里需要转换一下俯仰角的正负
		orientationValues[1]=-orientationValues[1];
		
		//将标准方向转换成自定义方向
		//rotateArrowTo(translateToCustomDirection(orientationValues[0]));

		currentDirection = orientationValues[0];
//		currentDirection=translateToCustomDirection(orientationValues[0]);
		
		return orientationValues;
	}
	
	
	/**
	 * 设置箭头动画
	 * @param direction
	 */
	private void rotateArrowTo(float direction){
		RotateAnimation rotateAnimation=
				new RotateAnimation(currentDirection, direction,
						Animation.RELATIVE_TO_SELF,0.5f, 
						Animation.RELATIVE_TO_SELF,0.5f);
		rotateAnimation.setDuration(200);
		rotateAnimation.setFillAfter(true);
		iv_mapArrow.startAnimation(rotateAnimation);
		iv_mapArrow2.startAnimation(rotateAnimation);
	}

	/**
	 * 将标准方向转换成自定义方向
	 * @param direction
	 * @return
	 */
	private float translateToCustomDirection(float direction){
		return direction-STANDARD_Y_ORIENTATION;
	}
	

	
	
	
}
