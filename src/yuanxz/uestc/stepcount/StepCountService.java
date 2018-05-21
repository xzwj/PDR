package yuanxz.uestc.stepcount;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import yuanxz.uestc.datarecord.ExcelOperation;
import yuanxz.uestc.distancecalculatorschema.CalculateSchema;
import yuanxz.uestc.distancecalculatorschema.IResultPost;
import yuanxz.uestc.distancecalculatorschema.y.YSchema1;
import yuanxz.uestc.distancecalculatorschema.y.YSchema2;
import yuanxz.uestc.distancecalculatorschema.z.KalmanSchema;
import yuanxz.uestc.distancecalculatorschema.z.ZSchema1;
import yuanxz.uestc.distancecalculatorschema.z.ZSchema2;
import yuanxz.uestc.distancecalculatorschema.z.ZSchema3;
import yuanxz.uestc.distancecalculatorschema.z.ZSchema4;
import yuanxz.uestc.message.StepCountMessage;
import yuanxz.uestc.stepstate.y.YStepCounter;
import yuanxz.uestc.stepstate.y.YStepStateSwitcher;
import yuanxz.uestc.stepstate.z.ZStepCounter;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.test.suitebuilder.annotation.Smoke;

public class StepCountService extends Service implements Observer{

	public static final String RECORD_FILE_PATH="/sdcard/PDR_DATA/accValue";
	
	public static final String CURRENT_STEP_KEY="CURRENT_STEP_KEY";
	public static final String CURRENT_STEP_LEN1="CURRENT_STEP_LEN1";
	public static final String CURRENT_STEP_LEN2="CURRENT_STEP_LEN2";
	public static final String CURRENT_STEP_LEN3="CURRENT_STEP_LEN3";
	public static final String CURRENT_STEP_LEN4="CURRENT_STEP_LEN4";
	public static final String Y_STEP_LEN="Y_STEP_LEN";
	public static final String Y_STEP_LEN2="Y_STEP_LEN2";
	public static final String Y_STEP_LEN3="Y_STEP_LEN3";
	public static final String KALMAN_STEP_LEN="KALMAN_STEP_LEN";
	
	
	private Messenger outMessenger;//向外发送信息的信使
	private Messenger inMessenger;//向内接收信息的信使
	
	
	private ZStepCounter z_stepCounter;
	private YStepCounter y_stepCounter;
//	private ExcelOperation excelOperation;
	
	private CalculateSchema schema1;
	private CalculateSchema schema2;
	private CalculateSchema schema3;
	private CalculateSchema schema4;
	private CalculateSchema ySchema;
	private CalculateSchema ySchema2;
	private CalculateSchema ySchema3;
	private CalculateSchema kalmanSchema;
	
	//充当低通滤波器
	private AverageSmoothTool vSmoothTool=new AverageSmoothTool(5);
	private AverageSmoothTool fSmoothTool=new AverageSmoothTool(5);
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		init();
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return inMessenger.getBinder();
	}

	
	/**
	 * 初始化
	 */
	private void init(){
//		excelOperation=new ExcelOperation();
//		excelOperation.open(RECORD_FILE_PATH);
		z_stepCounter=new ZStepCounter(this);
		y_stepCounter=new YStepCounter(this);
		inMessenger=new Messenger(new StepCountHandler());
		schema1=new ZSchema1(new MyResultPoster(CURRENT_STEP_LEN1));
		schema2=new ZSchema2(new MyResultPoster(CURRENT_STEP_LEN2));
		schema3=new ZSchema3(new MyResultPoster(CURRENT_STEP_LEN3));
		schema4=new ZSchema4(new MyResultPoster(CURRENT_STEP_LEN4));
		ySchema=new YSchema1(new MyResultPoster(Y_STEP_LEN));
		ySchema2=new YSchema1(new MyResultPoster(Y_STEP_LEN2));
		ySchema3=new YSchema2(new MyResultPoster(Y_STEP_LEN3));
		kalmanSchema=new KalmanSchema(new MyResultPoster(KALMAN_STEP_LEN));
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		schema1.stop();
		schema2.stop();
		schema3.stop();
		schema4.stop();
		ySchema.stop();
		ySchema2.stop();
		ySchema3.stop();
		kalmanSchema.stop();
		
		z_stepCounter.stopCount();
		y_stepCounter.stopCount();
		
//		excelOperation.writeToExcel();
//		excelOperation.close();
		super.onDestroy();
	}
	
	/**
	 * 处理传感器获取到的数据，将其传入内部进行处理
	 * @author deathym
	 *
	 */
	private class StepCountHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			outMessenger=msg.replyTo;
			switch (msg.what) {
			case StepCountMessage.MSG_START_STEP_COUNT://开始计步
				Bundle data=msg.getData();
				float[] accelerationValues=data.getFloatArray("ACCELERATIONS");
				float[] orientationValues=data.getFloatArray("ORIENTATIONS");
				float[] realAcc=calculateRealAcc(accelerationValues, orientationValues);
				startCount(realAcc);
				
//				excelOperation.write(0,Calendar.getInstance().getTimeInMillis()%(1e8));
//				excelOperation.write(1, accelerationValues[0]);
//				excelOperation.write(2, accelerationValues[1]);
//				excelOperation.write(3, accelerationValues[2]);
//				excelOperation.write(4, realAcc[0]);//水平
//				excelOperation.write(5,vSmoothTool.averageSmooth(realAcc[0]));
//				excelOperation.write(6,realAcc[1]);//垂直
//				excelOperation.write(7,fSmoothTool.averageSmooth(realAcc[1]));
				
//				excelOperation.write(5,orientationValues[0]);
//				excelOperation.write(6,orientationValues[1]);
//				excelOperation.write(7,orientationValues[2]);
//				excelOperation.write(8,Math.cos(orientationValues[0]*Math.PI/180));
//				excelOperation.write(9,Math.cos(orientationValues[1]*Math.PI/180));
//				excelOperation.write(10,Math.cos(orientationValues[2]*Math.PI/180));
				
				break;
			case StepCountMessage.MSG_STOP_STEP_COUNT://停止计步
				stopCount();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
		
	}

	/**
	 * 通过实时数据开始计步
	 * @param realtimeData
	 */
	private void startCount(float[] realtimeData){
//		z_stepCounter.startCount(realtimeData, Calendar.getInstance());
		z_stepCounter.startCount(realtimeData[1], Calendar.getInstance());
//		y_stepCounter.startCount(realtimeData[0], Calendar.getInstance());
	}
	/**
	 * 停止计步
	 */
	private void stopCount()
	{
		z_stepCounter.stopCount();
	}

	
	@Override
	public void update(Observable observable, Object data) {
		// TODO Auto-generated method stub
		StepInfo stepInfo=(StepInfo)data;
		stepInfo.addSchema(new CalculateSchema[]{schema4,kalmanSchema});
//		schema1.addTask(stepInfo);
//		schema2.addTask(stepInfo);
//		schema3.addTask(stepInfo);
//		schema4.addTask(stepInfo);
//		ySchema.addTask(stepInfo);
//		ySchema.addTask(stepInfo);
//		ySchema2.addTask(stepInfo);
//		ySchema3.addTask(stepInfo);
//		kalmanSchema.addTask(stepInfo);
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
	
	
	private class MyResultPoster implements IResultPost{

		private String resultKey;
		
		public MyResultPoster(String resultKey){
			this.resultKey=resultKey;
		}
		
		@Override
		public void postResult(double result,int currentStep) {
			// TODO Auto-generated method stub
			try {
				Bundle msgData=new Bundle();
				msgData.putDouble(resultKey, result);
				msgData.putInt(CURRENT_STEP_KEY, currentStep);
				sendMsg(StepCountMessage.MSG_ONE_STEP, msgData);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * 计算真实水平方向和垂直方向的加速度
	 * @param accValues
	 * @param orientation
	 * @return float[0]:水平加速度 ,float[1]:垂直加速度
	 */
	private float[] calculateRealAcc(float[] accValues,float[] orientation){
		float rightAccTemp=accValues[0];
		float verticalAccTemp=accValues[2];
		float forwardAccTemp=accValues[1];
		float rollAngle=orientation[2];//翻滚
		float pitchAngle=orientation[1];//俯仰
		
		
		
		//使用到的力的合成公式：a为俯仰角，b为翻滚角
		//水平力=forward*cosa-verticle*sina
		//垂直力=-right*sinb+forward*sina*cosb+verticle*cosa*cosb
		//其中verticle是测得的垂直方向的加速度，forward为测得的水平方向的加速度，此公式最重要的是考虑到夹角的正负和加速度的正负
		//考虑翻滚对垂直加速度的影响
		double cosRollAngle=Math.cos(rollAngle*Math.PI/180);
		double sinRollAngle=Math.sin(rollAngle*Math.PI/180);
		double sinPitchAngle=Math.sin(pitchAngle*Math.PI/180);
		double cosPitchAngel=Math.cos(pitchAngle*Math.PI/180);
		float forwardAcc=(float)(forwardAccTemp*cosPitchAngel-verticalAccTemp*sinPitchAngle);
		float verticleAcc=(float) (-rightAccTemp*sinRollAngle+forwardAccTemp*sinPitchAngle*cosRollAngle+verticalAccTemp*cosPitchAngel*cosRollAngle);
		
		return new float[]{forwardAcc,verticleAcc};
	}
	
	
	
//	/**
//	 * 计算真实水平方向和垂直方向的加速度
//	 * @param accValues
//	 * @param orientation
//	 * @return float[0]:水平加速度 ,float[1]:垂直加速度
//	 */
//	private float[] calculateRealAcc(float[] accValues,float[] orientation){
//		
//		float verticalAccTemp=accValues[2];
//		float forwardAccTemp=accValues[1];
//		float rollAngle=orientation[2];//翻滚
//		float pitchAngle=orientation[1];//俯仰
//		
//		
//		
//		//使用到的力的合成公式：
//		//水平力=verticle*sin+forward*cos
//		//垂直力=verticle*cos-forward*sin
//		//其中verticle是测得的垂直方向的加速度，forward为测得的水平方向的加速度，此公式最重要的是考虑到夹角的正负和加速度的正负
//		//考虑翻滚对垂直加速度的影响
//		double cosRollAngle=Math.cos(rollAngle*Math.PI/180);
//		double sinPitchAngle=Math.sin(pitchAngle*Math.PI/180);
//		double cosPitchAngel=Math.cos(pitchAngle*Math.PI/180);
//		//暂不考虑翻滚
////		verticalAccTemp=(float) (verticalAccTemp/cosRollAngle);//不准确 （）
//		float verticleAcc=(float) (verticalAccTemp*cosPitchAngel+forwardAccTemp*sinPitchAngle);
//		float forwardAcc=(float)(forwardAccTemp*cosPitchAngel-verticalAccTemp*sinPitchAngle);
//		
//		
//		
//		////////////////////////////////////////////////////////////////////
//		//将x和z轴的数据合成
////		verticalAccTemp=(float) (accValues[2]/(Math.cos(orientation[2])));
//		//最后合成Y轴数据
////		verticalAccTemp=(float)(verticalAccTemp/(Math.cos(orientation[1])));
//		//只合成Y轴数据，由于翻滚角很容易变化，因此不考虑X轴的数据
////		verticalAccTemp=(float)(accValues[2]/(Math.cos(orientation[1]*Math.PI/180)));
//		////////////////////////////////////////////////////////////////////
//		
//		////////////////////////////////////////////////////////////////////////////////////
//		/*
//		double cosRollAngle=Math.cos(rollAngle*Math.PI/180);
//		double sinPitchAngle=Math.sin(pitchAngle*Math.PI/180);
//		double cosPitchAngel=Math.cos(pitchAngle*Math.PI/180);
//		
//		double verticalDenominator=(cosPitchAngel*cosPitchAngel-sinPitchAngle*sinPitchAngle);
//		double forwardDenominator=(sinPitchAngle*sinPitchAngle-cosPitchAngel*cosPitchAngel);
//		StringBuilder sb=new StringBuilder("/////////////////////////////////////////////"+'\n');
//		sb.append(":"+verticalAccTemp+'\n');
//		sb.append("forwardAcc:"+forwardAccTemp+'\n');
//		//考虑翻滚对垂直加速度的影响
//		verticalAccTemp=(float) (verticalAccTemp/cosRollAngle);
//		sb.append("verticalAcc:"+verticalAccTemp+'\n');
//		//由于垂直的力和前向的力都会叠加到手机坐标系中的y轴和z轴，因此通过解方程来算出垂直于地面和水平方向的加速度
//		verticalAccTemp=(float) ((verticalAccTemp*cosPitchAngel-forwardAccTemp*sinPitchAngle)/verticalDenominator);
//		sb.append("verticalAcc:"+verticalAccTemp+'\n');
//		forwardAccTemp=(float)((verticalAccTemp*sinPitchAngle-forwardAccTemp*cosPitchAngel)/forwardAccTemp);
//		
//		sb.append("--->verticalAcc:"+verticalAccTemp+'\n');
//		sb.append("--->forwardAcc:"+forwardAccTemp+'\n');
//		sb.append("--->verticleDifference:"+Math.abs(verticalAccTemp-accValues[2])+'\n');
//		sb.append("--->rollAngle:"+rollAngle+'\n');
//		sb.append("--->pitchAngle:"+pitchAngle+'\n');		
//		sb.append("cosRollAngle:"+cosRollAngle+'\n');
//		sb.append("sinPitchAngle:"+sinPitchAngle+'\n');
//		sb.append("cosPitchAngle:"+cosPitchAngel+'\n');		
//		sb.append("Vdenominator:"+verticalDenominator+'\n');
//		sb.append("Fdenominator:"+forwardDenominator+'\n');
//		sb.append("/////////////////////////////////////////////");
//		System.out.println(sb);
//		*/
//		////////////////////////////////////////////////////////////////////////////////////
//		
//		return new float[]{forwardAcc,verticleAcc};
//	}
	
	/**
	 * @author xingzhong
	 * simple moving average
	 */
	private class AverageSmoothTool{
		private int smoothWindow=0;
		private float[] averageSmoothValue;
		
		private int currentSize=0;
		
		private float sumTemp=0;
		
		public AverageSmoothTool(int windowSize){
			this.smoothWindow=windowSize;
			averageSmoothValue=new float[smoothWindow];
			Arrays.fill(averageSmoothValue, 0);
		}
		
		public float averageSmooth(float data){
			updateSmoothValue(data);
			return smooth();
		}
		
		/**
		 * 用来平均的数是否初始化完毕
		 * @return
		 */
		private boolean isSmoothValueInited(){
			return (smoothWindow==currentSize);
		}
		
		/**
		 * 更新用来平均的数组，淘汰老数据，插入新数据。若数据不够，则不淘汰
		 * @param newValue
		 */
		private void updateSmoothValue(float newValue){
			if(!isSmoothValueInited()){//数据不够
				averageSmoothValue[currentSize++]=newValue;
				sumTemp+=newValue;
			}else{
				sumTemp-=averageSmoothValue[0];
				//更新
				for(int i=1;i<smoothWindow;i++){
					averageSmoothValue[i-1]=averageSmoothValue[i];
				}
				averageSmoothValue[smoothWindow-1]=newValue;
				
				sumTemp+=newValue;
			}
		}
		
		/**
		 * 简单求平均
		 * @return
		 */
		private float smooth(){
			return sumTemp/currentSize;
		}
		
		
	}
	
	
}
