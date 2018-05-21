package yuanxz.uestc.stepstate.y;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Observable;

import android.util.Log;


import yuanxz.uestc.samplerecord.SampleRecord;
import yuanxz.uestc.stepcount.StepInfo;
import yuanxz.uestc.stepcount.StepInfoSP;
import yuanxz.uestc.stepstate.StepState;
import yuanxz.uestc.stepstate.StepStateSwitcher;
import yuanxz.uestc.stepstate.z.ZCrestState;
import yuanxz.uestc.stepstate.z.ZIdleStepState;
import yuanxz.uestc.stepstate.z.ZTroughState;

public class YStepStateSwitcher extends StepStateSwitcher{
private static final String TAG="YStepStateSwitcher"	;
	
	public static final float IDLE_STATE_MAX_PERSIST_TIME=300;
	public static final float CREST_STATE_MAX_PERSIST_TIME=1000;//500->1000
	public static final float TROUGH_STATE_MAX_PERSIST_TIME=1000;//500->1000
	
	public static final float IDLE_STATE_MIN_PERSIST_TIME=-1;
	public static final float CREST_STATE_MIN_PERSIST_TIME=50;
	public static final float TROUGH_STATE_MIN_PERSIST_TIME=50;
	
	
	protected StepState currentState;
	protected StepState idleStepState;
	protected StepState crestStepState;
	protected StepState troughStepState;
	
	public YStepStateSwitcher(){
		super(
				new ZIdleStepState((float)0.3, IDLE_STATE_MAX_PERSIST_TIME, IDLE_STATE_MIN_PERSIST_TIME), 
				new YCrestState(0, CREST_STATE_MAX_PERSIST_TIME, CREST_STATE_MIN_PERSIST_TIME), 
				new YTroughState(0, TROUGH_STATE_MAX_PERSIST_TIME, TROUGH_STATE_MIN_PERSIST_TIME));
	}

	@Override
	protected void recordSample(float realtimeData) {
		// TODO Auto-generated method stub
		stepInfo.recordYSample(realtimeData);
	}

	
}
