package yuanxz.uestc.distancecalculatorschema.y;

import java.util.Iterator;

import android.util.Log;
import yuanxz.uestc.distancecalculatorschema.CalculateSchema;
import yuanxz.uestc.distancecalculatorschema.IResultPost;
import yuanxz.uestc.samplerecord.SampleRecord;
import yuanxz.uestc.stepcount.StepInfo;

public class YSchema1 extends CalculateSchema{

	public static final String TAG="YSchema1";
	
	public YSchema1(IResultPost resultPoster) {
		super(resultPoster);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double calculateStepLen(StepInfo stepInfo) {
		// TODO Auto-generated method stub
//		float[] velocity=IntegratingTool.integrate(0, 50, stepInfo);
		
		//debug
		StringBuilder sb1=new StringBuilder();
		sb1.append("acc:\n");
		SampleRecord sr=stepInfo.getYSampleRecord();
		Iterator<Float> it=sr.iterator();
		while(it.hasNext()){
			sb1.append(it.next()+",");
		}
		sb1.append('\n');
		sb1.append("零参考值："+stepInfo.getZeroReferenceValue());
		sb1.append('\n');
		Log.d(TAG, sb1.toString());
		
		float[] velocity=IntegratingTool.integrate(0, 50, stepInfo.getYSampleRecord(),stepInfo.getZeroReferenceValue());
		//debug
		StringBuilder sb=new StringBuilder();
		sb.append("velocity:\n");
		for(float v:velocity){
			sb.append(v+",");
		}
		sb.append('\n');
		Log.d(TAG, sb.toString());
		
		float distance=IntegratingTool.integrate(0, 20, velocity,0);
		Log.i(TAG,"stepLen="+distance);
		return distance;
	}
	
}
