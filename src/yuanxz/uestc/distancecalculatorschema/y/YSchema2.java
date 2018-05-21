package yuanxz.uestc.distancecalculatorschema.y;

import java.util.Iterator;

import android.util.Log;

import yuanxz.uestc.distancecalculatorschema.CalculateSchema;
import yuanxz.uestc.distancecalculatorschema.IResultPost;
import yuanxz.uestc.samplerecord.SampleRecord;
import yuanxz.uestc.stepcount.StepInfo;

public class YSchema2 extends CalculateSchema{

	private static final String TAG="YSchema2"; 
	
	public YSchema2(IResultPost resultPoster) {
		super(resultPoster);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double calculateStepLen(StepInfo stepInfo) {
		// TODO Auto-generated method stub
		Log.i(TAG,"frequency="+calculateFrequency(stepInfo));
		Log.i(TAG,"variance="+calculateVariance(stepInfo));
		return 0;
	}

	
	/**
	 * 求记录的平均值
	 * @param sampleRecord
	 * @return
	 */
	private float calculateAverageValue(SampleRecord sampleRecord){
		float sum=0;
		Iterator<Float> it=sampleRecord.iterator();
		while(it.hasNext()){
			sum+=it.next();
		}
		return sum/(sampleRecord.size());
	} 
	
	
	private float calculateFrequency(StepInfo stepInfo){
		float interval=(
				stepInfo.getStepFinishTime().getTimeInMillis()
				-
				stepInfo.getStepStartTime().getTimeInMillis()
				);
		interval/=1000;
		return 1/interval;
	}
	
	private float calculateVariance(StepInfo stepInfo){
		SampleRecord sr=stepInfo.getYSampleRecord();
		float averageValue=calculateAverageValue(sr);
		float sum=0;
		Iterator<Float> it=sr.iterator();
		while(it.hasNext()){
			sum+=(Math.pow((it.next()-averageValue), 2));
		}
		return sum/(sr.size());
	}
	
	
	
	
	
}
