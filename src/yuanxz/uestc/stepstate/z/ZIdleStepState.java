package yuanxz.uestc.stepstate.z;

import java.util.Calendar;

import yuanxz.uestc.stepcount.StepInfo;
import yuanxz.uestc.stepstate.RelativeStateTransMonitor;
import yuanxz.uestc.stepstate.StepState;


/**
 * @author yuanxz
 * 空闲状态，即静止时对应的状态
 */
public class ZIdleStepState extends StepState{

	/**
	 * 空闲状态ID
	 */
	public static final int IDLE_STATE_ID=0;
	
	
	public ZIdleStepState(float threshold, float maxPersistTime,
			float minPersistTime) {
		super(IDLE_STATE_ID, 
				new RelativeStateTransMonitor(threshold,-Float.MAX_VALUE,Float.MAX_VALUE), 
				maxPersistTime, minPersistTime);
		// TODO Auto-generated constructor stub
	}


	@Override
	protected boolean findNewExtremum(float realtimeData) {
		// TODO Auto-generated method stub
		return !stepStatus.extremumIsValidate();
	}


	@Override
	public StepState start(StepState preState, StepInfo stepInfo) {
		// TODO Auto-generated method stub
		return this;
	}


	@Override
	public float getCriticalValue() {
		// TODO Auto-generated method stub
		return stateTransMonitor.getReferenceValue();
	}

}
