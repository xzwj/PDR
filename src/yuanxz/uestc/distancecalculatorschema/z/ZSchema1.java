package yuanxz.uestc.distancecalculatorschema.z;

import android.util.Log;
import yuanxz.uestc.distancecalculatorschema.CalculateSchema;
import yuanxz.uestc.distancecalculatorschema.IResultPost;
import yuanxz.uestc.stepcount.StepInfo;

public class ZSchema1 extends CalculateSchema{

	public static final String TAG="Schema1";
	
	public ZSchema1(IResultPost resultPoster) {
		super(resultPoster);
		// TODO Auto-generated constructor stub
	}

	@Override
	public double calculateStepLen(StepInfo stepInfo) {
		// TODO Auto-generated method stub
		double k=0.48;//0.41->0.48
		double difference=stepInfo.getMaxAcceleration()-stepInfo.getMinAcceleration();
		double result=k*Math.pow(difference, 0.25);
		
		Log.d(TAG,"schema1 result:"+result);
		
		//debug
//		if(Double.isNaN(result)){
//			Log.e(TAG,"Math.pow("+difference+",0.25)=NaN");
//		}
		
		return  result;
	}

}
