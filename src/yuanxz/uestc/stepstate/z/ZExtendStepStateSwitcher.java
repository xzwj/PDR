package yuanxz.uestc.stepstate.z;

import java.util.Calendar;
import java.util.Observable;

import yuanxz.uestc.stepcount.StepInfo;
import yuanxz.uestc.stepcount.StepInfoMP;
import yuanxz.uestc.stepstate.StepState;

/**
 * @author xingzhong
 * 同时记录了Z轴数据和Y轴数据
 */
public class ZExtendStepStateSwitcher extends Observable{
	
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
	
	private StepInfo stepInfo;
	private int step;//用来标记计步的数量
	
	public ZExtendStepStateSwitcher(){
		idleStepState=new ZIdleStepState((float)1, IDLE_STATE_MAX_PERSIST_TIME, IDLE_STATE_MIN_PERSIST_TIME);
		crestStepState=new ZCrestState(3, CREST_STATE_MAX_PERSIST_TIME, CREST_STATE_MIN_PERSIST_TIME);
		troughStepState=new ZTroughState(3, TROUGH_STATE_MAX_PERSIST_TIME, TROUGH_STATE_MIN_PERSIST_TIME);
		
		setCurrentState(idleStepState);
		stepInfo=new StepInfoMP();
		step=0;
	}
	
	/**
	 * 接收传感器数值输入
	 * @param realtimeData
	 * @param timeStamp
	 */
	public void inputData(float[] realtimeData,Calendar timeStamp){
		stepInfo.recordSamples(realtimeData);//记录样本数据
		
		float zRealtimeData=realtimeData[1];
		currentState.inputData(zRealtimeData, timeStamp);
		if(currentState.canRtainCurrentState(zRealtimeData, timeStamp)){
//			System.out.println(currentState.getStateID()+":保持当前状态");
//			currentState.findExtremumData(zRealtimeData, timeStamp);
		}else if(currentState.canTransiteToNextState(zRealtimeData, timeStamp)){
//			System.out.println(currentState.getStateID()+":转移至下一个状态");
			nextState(currentState, zRealtimeData);
		}else if(currentState.canBackTheIdleState(zRealtimeData, timeStamp)){
//			System.out.println(currentState.getStateID()+":转移至空闲状态");
			backTheFirstState();
		}else{
			backTheFirstState();
			System.err.println("状态判断错误！");
		}
		
	}
	
	/**
	 * 获取当前状态
	 * @return
	 */
	public StepState getCurrentState(){
		return currentState;
	}
	
	
	/**
	 * 回到状态机的第一个状态
	 * @return
	 */
	private StepState backTheFirstState(){
		resetAllStepState();
		setCurrentState(idleStepState);
		stepInfo.clear();
		return currentState;
	}
	
	/**
	 * 进入此函数一定表示该进行状态转移了
	 * @param currentState
	 * @param realtimeData
	 * @return
	 */
	private StepState nextState(StepState currentState,float realtimeData){
		if(currentState.isDataValidate(realtimeData)){
			System.err.println("非法进入nextState函数。");
			return null;
		}
		
		switch (currentState.getStateID()) {
		case ZIdleStepState.IDLE_STATE_ID:
			crestStepState.start(currentState, stepInfo);
			setCurrentState(crestStepState);
			stepInfo.clear();
			break;
		case ZCrestState.CREST_STATE_ID:
			troughStepState.start(currentState, stepInfo);
			setCurrentState(troughStepState);
			break;
		case ZTroughState.TROUGH_STATE_ID:
			crestStepState.start(currentState, stepInfo);
			detectNewStep();//判断是否是有效的一步
			setCurrentState(crestStepState);
			break;
		default:
			System.err.println("状态转移出现错误！");
			break;
		}
		return currentState;
	}
	
	/**
	 * 检测是否出现有效的波峰和波谷值,只在波谷时调用
	 */
	private void detectNewStep(){
		//有效的一步
		if(crestStepState.isStateValidate()&&troughStepState.isStateValidate()){
			recordOneStepInfo();
			setChanged();
			notifyObservers(stepInfo);
			stepInfo=new StepInfoMP();//记录新的一步
		}
		resetAllStepState();
	}
	
	
	/**
	 * 在一步完整的情况下，记录一步中的有用信息
	 */
	private void recordOneStepInfo(){
		stepInfo.setMaxAcceleration(crestStepState.getExtremum());
		stepInfo.setMinAcceleration(troughStepState.getExtremum());
		stepInfo.setStepStartTime(crestStepState.getStateBeginTime());
		stepInfo.setStepFinishTime(troughStepState.getStateFinishTime());
		stepInfo.setZeroReferenceValue((crestStepState.getCriticalValue()+troughStepState.getCriticalValue())/2);
		stepInfo.setStepNum(++step);
	}
	
	/**
	 * 设置currentState,主要规范其中的各个阶段的开始结束时间设置
	 * @param nextState
	 */
	private void setCurrentState(StepState nextState){
		Calendar currentTime=Calendar.getInstance();
		if(currentState!=null){
//			System.out.println(((currentTime.getTimeInMillis())%1e6)+": "+currentState.getStateID()+" -->"+nextState.getStateID());
			currentState.setStateFinishTime(currentTime);
		}
		nextState.setStateBeginTime(currentTime);
		currentState=nextState;
	}
	
	/**
	 * 重置四个状态的所有信息
	 */
	private void resetAllStepState(){
		idleStepState.reset();
		crestStepState.reset();
		troughStepState.reset();
	}
}
