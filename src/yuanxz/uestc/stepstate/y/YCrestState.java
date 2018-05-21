package yuanxz.uestc.stepstate.y;

import java.util.Iterator;

import android.util.Log;

import yuanxz.uestc.samplerecord.SampleRecord;
import yuanxz.uestc.stepcount.StepInfo;
import yuanxz.uestc.stepstate.AbsoluteStateTransMonitor;
import yuanxz.uestc.stepstate.PreStateRelatedABSTransMonitor;
import yuanxz.uestc.stepstate.StateTransMonitor;
import yuanxz.uestc.stepstate.StepState;
import yuanxz.uestc.stepstate.StepStatus;
import yuanxz.uestc.stepstate.z.ZIdleStepState;


public class YCrestState extends StepState{

	public static final String TAG="YCrestState";
	
	public static final int Y_CREST_STATE_ID=1;
	
	public YCrestState(float criticalValue,
			float maxPersistTime, float minPersistTime) {
		super(Y_CREST_STATE_ID, 
//				new AbsoluteStateTransMonitor(criticalValue,(float)0.5,Float.MAX_VALUE),
				new PreStateRelatedABSTransMonitor(criticalValue, (float)0.4,Float.MAX_VALUE),
				maxPersistTime, minPersistTime);
		// TODO Auto-generated constructor stub
	}


	@Override
	public boolean findNewExtremum(float realtimeData) {
		// TODO Auto-generated method stub
		return (realtimeData>stepStatus.getExtremum())||(!stepStatus.extremumIsValidate());
	}


	@Override
	public StepState start(StepState preState, StepInfo stepInfo) {
		// TODO Auto-generated method stub
		//这里主要利用前一个状态的极值等来设定当前状态的分界值等
		((PreStateRelatedABSTransMonitor)stateTransMonitor).start(preState, stepInfo);
		return this;
	}

	/**
	 * 返回分界值，由于对Y轴数据使用绝对分界因此上分界值和下分解值是相等的
	 * @return
	 */
	public float getCriticalValue(){
		return stateTransMonitor.getBottomCriticalValue();
	}
	
	
	
	

}
