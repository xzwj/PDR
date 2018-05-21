package yuanxz.uestc.stepstate.z;

import java.util.Calendar;

import yuanxz.uestc.stepcount.StepInfo;
import yuanxz.uestc.stepstate.RelativeStateTransMonitor;
import yuanxz.uestc.stepstate.StepState;


public class ZTroughState extends StepState{

	
	public static final int TROUGH_STATE_ID=2;
	
	public ZTroughState(float threshold, float maxPersistTime,
			float minPersistTime) {
		super(TROUGH_STATE_ID, 
				new RelativeStateTransMonitor(threshold,-Float.MAX_VALUE,-1), 
				maxPersistTime, minPersistTime);
//		super(TROUGH_STATE_ID, new AbsoluteStateTransMonitor(0), maxPersistTime, minPersistTime);
		// TODO Auto-generated constructor stub
	}




	@Override
	protected boolean findNewExtremum(float realtimeData) {
		// TODO Auto-generated method stub
		return (realtimeData<stepStatus.getExtremum())||(!stepStatus.extremumIsValidate());
	}




	@Override
	public StepState start(StepState preState, StepInfo stepInfo) {
		// TODO Auto-generated method stub
		return this;
	}




	@Override
	public float getCriticalValue() {
		// TODO Auto-generated method stub
		return stateTransMonitor.getUpperCriticalValue();
	}

}
