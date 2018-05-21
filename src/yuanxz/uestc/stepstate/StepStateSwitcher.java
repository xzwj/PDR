package yuanxz.uestc.stepstate;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Observable;

import yuanxz.uestc.samplerecord.SampleRecord;
import yuanxz.uestc.stepcount.StepInfo;
import yuanxz.uestc.stepcount.StepInfoMP;
import yuanxz.uestc.stepcount.StepInfoSP;
import yuanxz.uestc.stepstate.z.ZCrestState;
import yuanxz.uestc.stepstate.z.ZIdleStepState;
import yuanxz.uestc.stepstate.z.ZTroughState;

public abstract class StepStateSwitcher extends Observable{
private static final String TAG="StepStateSwitcher"	;
	
	protected StepState currentState;
	protected StepState idleStepState;
	protected StepState crestStepState;
	protected StepState troughStepState;
	
	protected StepInfo stepInfo;
	private int step;//用来标记计步的数量
	
	public StepStateSwitcher(StepState idleStepState,StepState crestStepState,StepState troughStepState){
		this.idleStepState=idleStepState;
		this.crestStepState=crestStepState;
		this.troughStepState=troughStepState;
		
		setCurrentState(idleStepState,Calendar.getInstance());//空闲状态的开始时间不重要
		stepInfo=new StepInfoMP();
		step=0;
	}
	
	public void setstep(int step)
	{
		this.step = step;
	}
	/**
	 * 接收传感器数值输入
	 * @param realtimeData
	 * @param timeStamp
	 */
	public void inputData(float realtimeData,Calendar timeStamp){
		recordSample(realtimeData);//记录样本数据
		currentState.inputData(realtimeData, timeStamp);
		if(currentState.canRtainCurrentState(realtimeData, timeStamp)){
//			System.out.println(currentState.getStateID()+":保持当前状态");
//			currentState.findExtremumData(realtimeData, timeStamp);
		}else if(currentState.canTransiteToNextState(realtimeData, timeStamp)){
//			System.out.println(currentState.getStateID()+":转移至下一个状态");
			nextState(currentState, realtimeData,timeStamp);
		}else if(currentState.canBackTheIdleState(realtimeData, timeStamp)){
//			System.out.println(currentState.getStateID()+":转移至空闲状态");
			backTheFirstState();
		}else{
			System.err.println("状态判断错误！");
		}
		
	}
	
	/**
	 * 记录数据
	 * @param realtimeData
	 * @param timestamp
	 */
	protected abstract void recordSample(float realtimeData);
	
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
		setCurrentState(idleStepState,Calendar.getInstance());
		stepInfo.clear();
		return currentState;
	}
	
	/**
	 * 进入此函数一定表示该进行状态转移了
	 * @param currentState
	 * @param realtimeData
	 * @return
	 */
	private StepState nextState(StepState currentState,float realtimeData,Calendar timeStamp){
		if(currentState.isDataValidate(realtimeData)){
			System.err.println("非法进入nextState函数。");
			return null;
		}
		
		switch (currentState.getStateID()) {
		case ZIdleStepState.IDLE_STATE_ID:
//			nextStatePreprocess(currentState, crestStepState, stepInfo);
			crestStepState.start(currentState, stepInfo);//先后次序不要变
			stepInfo.clear();//只记录有效状态的数据
			setCurrentState(crestStepState,timeStamp);
			break;
		case ZCrestState.CREST_STATE_ID:
//			nextStatePreprocess(currentState, troughStepState, stepInfo);
			troughStepState.start(currentState, stepInfo);
			setCurrentState(troughStepState,timeStamp);
			break;
		case ZTroughState.TROUGH_STATE_ID:
//			nextStatePreprocess(currentState, crestStepState, stepInfo);
			crestStepState.start(currentState, stepInfo);
			detectNewStep(timeStamp);//判断是否是有效的一步
//			setCurrentState(crestStepState,timeStamp);//被转移到detectNewStep中
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
	private void detectNewStep(Calendar timeStamp){
		//有效的一步
		if(crestStepState.isStateValidate()&&troughStepState.isStateValidate()){
			troughStepState.setStateFinishTime(timeStamp);
			recordOneStepInfo();
			setChanged();
			notifyObservers(stepInfo);
			
			//debug
//			Log.d(TAG,"stepinfo size="+stepInfo.);
			
			stepInfo=new StepInfoMP();//记录新的一步
		}
		resetAllStepState();
		setCurrentState(crestStepState,timeStamp);
	}
	
	
	/**
	 * 在一步完整的情况下，记录一步中的有用信息
	 */
	private void recordOneStepInfo(){
		stepInfo.setMaxAcceleration(crestStepState.getExtremum());
		stepInfo.setMinAcceleration(troughStepState.getExtremum());
		stepInfo.setStepStartTime(crestStepState.getStateBeginTime());
		stepInfo.setStepFinishTime(troughStepState.getStateFinishTime());
		//设置零参考值
//		stepInfo.setZeroReferenceValue((crestStepState.getCriticalValue()+troughStepState.getCriticalValue())/2);
		stepInfo.setZeroReferenceValue((crestStepState.getExtremum()+troughStepState.getExtremum())/2);
		stepInfo.setStepNum(++step);
	}
	
	/**
	 * 设置currentState,主要规范其中的各个阶段的开始结束时间设置
	 * @param nextState
	 */
	private void setCurrentState(StepState nextState,Calendar currentTime){
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
	
	
	/**
	 * 数据预处理
	 * @param currentState
	 * @param nextSate
	 * @param stepInfo
	 */
//	protected abstract void nextStatePreprocess(StepState currentState,StepState nextSate,StepInfo stepInfo);
}
