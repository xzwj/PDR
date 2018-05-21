package yuanxz.uestc.distancecalculatorschema.z;

import yuanxz.uestc.distancecalculatorschema.CalculateSchema;
import yuanxz.uestc.distancecalculatorschema.IResultPost;
import yuanxz.uestc.stepcount.StepInfo;

public class ZSchema3 extends CalculateSchema{
	public ZSchema3(IResultPost resultPoster) {
		super(resultPoster);
		// TODO Auto-generated constructor stub
	}

	private double averageAccelerationSum=0;
	
	@Override
	public double calculateStepLen(StepInfo stepInfo) {
		// TODO Auto-generated method stub
		return 0.55*(Math.pow(calculateAverageAcceleration(stepInfo), (double)1/(double)3));
	}
	
	private double calculateAverageAcceleration(StepInfo stepInfo){
		double currentAverageAcceleration=(stepInfo.getMaxAcceleration()+stepInfo.getMinAcceleration())/(double)2;
		averageAccelerationSum+=Math.abs(currentAverageAcceleration);
		return averageAccelerationSum/(double)stepInfo.getStepNum();
	}
}
