package yuanxz.uestc.distancecalculatorschema.z;

import java.util.Iterator;

import android.util.Log;
import android.view.View.MeasureSpec;
import yuanxz.uestc.distancecalculatorschema.CalculateSchema;
import yuanxz.uestc.distancecalculatorschema.IResultPost;
import yuanxz.uestc.samplerecord.SampleRecord;
import yuanxz.uestc.stepcount.StepInfo;

public class KalmanSchema extends CalculateSchema{

	private static final String TAG="KalmanSchema";
	
	private KalmanFilter kalmanFilter;
	
	private StepLenMeasureSchema schema;
	
	public KalmanSchema(IResultPost resultPoster) {
		super(resultPoster);
		// TODO Auto-generated constructor stub
		kalmanFilter=new KalmanFilter();
		schema=new StepLenMeasureSchema4();
	}

	@Override
	public double calculateStepLen(StepInfo stepInfo) {
		// TODO Auto-generated method stub
		
		Log.d(TAG,"kalman 准备开始计算");
		double result=schema.measureStepLen(stepInfo);
		Log.d(TAG,"计算值："+result);
		
		float kalmanValue=kalmanFilter.update((float)result);
		
		Log.d(TAG,"卡尔曼滤波值："+kalmanValue);
		
		return kalmanValue;
	}
	
	
	private class KalmanFilter{
		/**
		 * 系统不确定值，表示对经验值的不确定程度，即上下浮动0.03米
		 */
		private static final float SYSTEM_UNCERTAINTY=(float)0.03;//
		/**
		 * 测量部确定性，表示对测量值的不确定程度，即上下浮动0.05米
		 */
		private static final float MEASURE_UNCERTAINTY=(float)0.2;//
		
		
		private static final float initStepLen=(float)0.75;
		private static final float initP=SYSTEM_UNCERTAINTY*SYSTEM_UNCERTAINTY;//均方差，不是平方的量纲
		private static final float systemNoiseCovariance=SYSTEM_UNCERTAINTY*SYSTEM_UNCERTAINTY;
		private static final float measurementNoiseCovariance=MEASURE_UNCERTAINTY*MEASURE_UNCERTAINTY;
		
		
		private float stepLen;//上一次的步长
		private float p;//不确定方差
		
		public KalmanFilter(){
			p=initP;
			stepLen=initStepLen;
		}
		
		
		/**
		 * 状态预测
		 * @param preStepLen
		 * @return
		 */
		private float predict(float preStepLen){
			float predictStepLen;
			predictStepLen=preStepLen;//预测前后两次的步长是一样的
			return predictStepLen;
		}
		
		/**
		 * 估计值的均方差p
		 * @param preAverageError
		 * @return
		 */
		private float predictP(float p){
			return (p+systemNoiseCovariance);
		}
		
		/**
		 * 估计卡尔曼增益
		 * @return
		 */
		private float calculateKalmanGain(float predictP){
			return (predictP)/(predictP+measurementNoiseCovariance);
		}
		
		
		/**
		 * 更新
		 * @param measureValue
		 * @return
		 */
		private float update(float measureValue){
			//预测
			float predictValue=predict(stepLen);
			float predictP=predictP(p);
			float kalmanGain=calculateKalmanGain(predictP);
			//update
			p=updateP(kalmanGain, predictP);
			stepLen=predictValue+kalmanGain*(measureValue-predictValue);
			return stepLen; 
		}
		
		/**
		 * 更新P
		 * @param kalmanGain
		 * @param predictP
		 * @return
		 */
		private float updateP(float kalmanGain,float predictP){
			p=(1-kalmanGain)*predictP;
			return p;
		}
	}
	
	private interface StepLenMeasureSchema{
		public double measureStepLen(StepInfo stepInfo);
	}
	
	private class StepLenMeasureSchema1 implements StepLenMeasureSchema{

		@Override
		public double measureStepLen(StepInfo stepInfo) {
			// TODO Auto-generated method stub
			double k=0.48;//0.41->0.48
			double difference=stepInfo.getMaxAcceleration()-stepInfo.getMinAcceleration();
			double result=k*Math.pow(difference, 0.25);
			return result;
		}
		
	}
	
	private class StepLenMeasureSchema2 implements StepLenMeasureSchema{

		private double averageAccelerationSum=0;
		@Override
		public double measureStepLen(StepInfo stepInfo) {
			// TODO Auto-generated method stub
			return 0.81*((calculateAverageAcceleration(stepInfo)-stepInfo.getMinAcceleration())/(stepInfo.getMaxAcceleration()-stepInfo.getMinAcceleration()));
		}

		private double calculateAverageAcceleration(StepInfo stepInfo){
			double currentAverageAcceleration=(stepInfo.getMaxAcceleration()+stepInfo.getMinAcceleration())/(double)2;
			averageAccelerationSum+=Math.abs(currentAverageAcceleration);
			return averageAccelerationSum/(double)stepInfo.getStepNum();
		}
		
	}
	
	
	private class StepLenMeasureSchema3 implements StepLenMeasureSchema{

		private double averageAccelerationSum=0;
		
		@Override
		public double measureStepLen(StepInfo stepInfo) {
			// TODO Auto-generated method stub
			return 0.55*(Math.pow(calculateAverageAcceleration(stepInfo), (double)1/(double)3));
		}
		
		private double calculateAverageAcceleration(StepInfo stepInfo){
			double currentAverageAcceleration=(stepInfo.getMaxAcceleration()+stepInfo.getMinAcceleration())/(double)2;
			averageAccelerationSum+=Math.abs(currentAverageAcceleration);
			return averageAccelerationSum/(double)stepInfo.getStepNum();
		}
		
	}
	
	private class StepLenMeasureSchema4 implements StepLenMeasureSchema{

		@Override
		public double measureStepLen(StepInfo stepInfo) {
			// TODO Auto-generated method stub
			SampleRecord sr=stepInfo.getZSampleRecord();
			float averageAcc=calculateAverageAcc(sr);
			float displace=calculateDisplace(sr,averageAcc);
			float maxAcc=stepInfo.getMaxAcceleration();
			float minAcc=stepInfo.getMinAcceleration();
			double len=displace*((maxAcc-minAcc)/(averageAcc-minAcc));
			len=0.0319*Math.sqrt(Math.abs(len));//0.0249->0.0319
			return len;
		}
		
		/**
		 * 计算平均加速度
		 * @param stepInfo
		 * @return
		 */
		private float calculateAverageAcc(SampleRecord sampleRecord){
			float sum=0;
			int size=sampleRecord.size();
			Log.d(TAG, "size="+size);
			
			Iterator<Float> it=sampleRecord.iterator();
			
			while(it.hasNext()){
				sum+=it.next();
			}
			
			return sum/size;
		}
		
		/**
		 * 根据《Enhancing the Performance of Pedometers Using a Single Accelerometer》
		 * 使用离散的方法来代替二次积分
		 * @param stepInfo
		 * @return
		 */
		private float calculateDisplace(SampleRecord sampleRecord,float averageAcc){
			float displace=0;
			float velocity=0;
			Iterator<Float> it=sampleRecord.iterator();
			while(it.hasNext()){
				velocity+=((it.next())-averageAcc);
				displace+=velocity;
			}
			return displace;
		}
		
	}
}
