package yuanxz.uestc.stepstate.z;

import java.util.Calendar;
import java.util.Observable;

import android.R.id;
import android.util.Log;

import yuanxz.uestc.stepcount.StepInfo;
import yuanxz.uestc.stepcount.StepInfoSP;
import yuanxz.uestc.stepstate.StepState;
import yuanxz.uestc.stepstate.StepStateSwitcher;

/**
 * @author xingzhong
 * 只记录了Z轴数据，前期用来检测行走步数的
 */
public class ZStepStateSwitcher extends StepStateSwitcher{
	
	private static final String TAG="StepStateSwitcher"	;
	
	public static final float IDLE_STATE_MAX_PERSIST_TIME=300;
	public static final float CREST_STATE_MAX_PERSIST_TIME=1000;//500->1000
	public static final float TROUGH_STATE_MAX_PERSIST_TIME=1000;//500->1000
	
	public static final float IDLE_STATE_MIN_PERSIST_TIME=-1;
	public static final float CREST_STATE_MIN_PERSIST_TIME=50;
	public static final float TROUGH_STATE_MIN_PERSIST_TIME=50;
	
	public ZStepStateSwitcher(){
		super(
				new ZIdleStepState((float)1, IDLE_STATE_MAX_PERSIST_TIME, IDLE_STATE_MIN_PERSIST_TIME), 
				new ZCrestState(3, CREST_STATE_MAX_PERSIST_TIME, CREST_STATE_MIN_PERSIST_TIME),
				new ZTroughState(3, TROUGH_STATE_MAX_PERSIST_TIME, TROUGH_STATE_MIN_PERSIST_TIME));
	}

	@Override
	protected void recordSample(float realtimeData) {
		// TODO Auto-generated method stub
		stepInfo.recordZSample(realtimeData);
	}

	
}
