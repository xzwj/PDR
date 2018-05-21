package yuanxz.uestc.distancecalculatorschema.z;

import java.util.ArrayList;
import java.util.Iterator;

import android.util.Log;

import yuanxz.uestc.distancecalculatorschema.CalculateSchema;
import yuanxz.uestc.distancecalculatorschema.IResultPost;
import yuanxz.uestc.samplerecord.SampleRecord;
import yuanxz.uestc.stepcount.StepInfo;

public class ZSchema4 extends CalculateSchema{
	private static final String TAG="ZSchema4";
	
	public ZSchema4(IResultPost resultPoster) {
		super(resultPoster);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double calculateStepLen(StepInfo stepInfo) {
		// TODO Auto-generated method stub
		Log.d(TAG,"zs4 准备开始计算");
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
