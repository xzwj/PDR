package yuanxz.uestc.stepstate.y;

import java.util.Iterator;

import yuanxz.uestc.samplerecord.SampleRecord;
import yuanxz.uestc.stepcount.StepInfo;
import yuanxz.uestc.stepstate.AbsoluteStateTransMonitor;
import yuanxz.uestc.stepstate.PreStateRelatedABSTransMonitor;
import yuanxz.uestc.stepstate.StepState;
import yuanxz.uestc.stepstate.z.ZIdleStepState;

public class YTroughState extends StepState{

	public static final int Y_TROUGH_STATE_ID=2;
	
	public YTroughState(float criticalValue,
			float maxPersistTime, float minPersistTime) {
		super(Y_TROUGH_STATE_ID, 
//				new AbsoluteStateTransMonitor(criticalValue,-Float.MAX_VALUE,(float)-0.5),
				new PreStateRelatedABSTransMonitor(criticalValue,-Float.MAX_VALUE,(float)-0.4),
				maxPersistTime, minPersistTime);
		// TODO Auto-generated constructor stub
	}


	@Override
	protected boolean findNewExtremum(float realtimeData) {
		// TODO Auto-generated method stub
		return ((realtimeData<stepStatus.getExtremum())||(!stepStatus.extremumIsValidate()));
	}


	@Override
	public StepState start(StepState preState, StepInfo stepInfo) {
		// TODO Auto-generated method stub
		//这里主要利用前一个状态的极值等来设定当前状态的分界值等
		((PreStateRelatedABSTransMonitor)stateTransMonitor).start(preState, stepInfo);
		return this;
	}


	@Override
	public float getCriticalValue() {
		// TODO Auto-generated method stub
		return stateTransMonitor.getUpperCriticalValue();
	}

	
}
