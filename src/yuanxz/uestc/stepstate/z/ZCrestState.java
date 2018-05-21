package yuanxz.uestc.stepstate.z;

import java.util.Calendar;

import yuanxz.uestc.stepcount.StepInfo;
import yuanxz.uestc.stepstate.RelativeStateTransMonitor;
import yuanxz.uestc.stepstate.StepState;


public class ZCrestState extends StepState{

	public static final int CREST_STATE_ID=1;
	
	public ZCrestState(float threshold, float maxPersistTime,
			float minPersistTime) {
		super(CREST_STATE_ID, 
				new RelativeStateTransMonitor(threshold,1,Float.MAX_VALUE), 
				maxPersistTime, minPersistTime);
//		super(CREST_STATE_ID, new AbsoluteStateTransMonitor(0), maxPersistTime, minPersistTime);
		// TODO Auto-generated constructor stub
	}

//	@Override
//	void findExtremumData(float realtimeData, Calendar timestamp) {
//		// TODO Auto-generated method stub
//		if(realtimeData>extremum){
//			extremum=realtimeData;
//			extremumTimeStamp=timestamp;
////			setReferenceValue(extremum);
//			stateTransMonitor.setExtremumValue(extremum);
//		}
//	}


	@Override
	protected boolean findNewExtremum(float realtimeData) {
		// TODO Auto-generated method stub
		return (realtimeData>stepStatus.getExtremum())||(!stepStatus.extremumIsValidate());
	}

	@Override
	public StepState start(StepState preState, StepInfo stepInfo) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public float getCriticalValue() {
		// TODO Auto-generated method stub
		return stateTransMonitor.getBottomCriticalValue();
	}

}
