package yuanxz.uestc.stepstate;

import java.util.Calendar;

import yuanxz.uestc.stepcount.StepInfo;


import android.content.Loader.ForceLoadContentObserver;

public abstract class StepState {
	public static final int ABSOLUTE_REFERENCE_VALUE=0;//绝对参考值
	
	protected float maxPersistTime;//状态最长持续时间
	protected float minPersistTime;//最短持续时间（滤除抖动）
	
	private int stateID;
	
	protected StepStatus stepStatus;
	
	protected boolean isStateValidate;//标志当前状态是否得到过理想的极值和持续过理想的时间间隔
	
	protected StateTransMonitor stateTransMonitor;//状态转换检测
	protected float preStateExtremumValue;//上一个状态的极值
	
	
	public StepState(int stateID,
			StateTransMonitor stateTransMonitor,
			float maxPersistTime,float minPersistTime){
		this.stateID=stateID;
		this.maxPersistTime=maxPersistTime;
		this.minPersistTime=minPersistTime;
//		resetExtremum();
		stepStatus=new StepStatus(stateID);
		isStateValidate=false;
		this.stateTransMonitor=stateTransMonitor;
	}
	
	/**
	 * 主要是在开始这个状态之前进行一些处理，如根据上一个状态值来设定分界值等
	 * @param preState
	 * @param stepInfo
	 * @return
	 */
	public abstract StepState start(StepState preState,StepInfo stepInfo);
	
	
	
	
	public int getStateID(){
		return stateID;
	}
	
	public void setStateID(int stateID){
		this.stateID=stateID;
	}
	
	
	/**
	 * 得到极值
	 * @return
	 */
	public float getExtremum() {
		return stepStatus.getExtremum();
	}

	
	/**
	 * 得到极值时间
	 * @return
	 */
	public Calendar getExtremumTimeStamp() {
		return stepStatus.getExtremumTimeStamp();
	}


	public Calendar getStateBeginTime() {
		return stepStatus.getStateBeginTime();
	}

	public void setStateBeginTime(Calendar stateBeginTime) {
		stepStatus.setStateBeginTime(stateBeginTime);
	}
	
	public Calendar getStateFinishTime() {
		return stepStatus.getStateFinishTime();
	}

	public void setStateFinishTime(Calendar stateFinishTime) {
		stepStatus.setStateFinishTime(stateFinishTime);
	}
	
	public StepStatus getStepStatus() {
		return stepStatus;
	}


	/**
	 * 录入数据，主要是看是否能够更新极值及参考值等
	 * @param realtimeData
	 * @param timestamp
	 */
	public void inputData(float realtimeData,Calendar timestamp){
		findExtremumData(realtimeData, timestamp);
	}
	
	
	
	//////////////////////////////////////////////////////////////////////////////
	/**
	 * 重置当前状态
	 * 只重置极值和时间戳及本状态是否有效的标识，不重置状态分割的判断标准
	 */
	public void reset(){
		stepStatus.reset();
		isStateValidate=false;
	}
	
	
	/**
	 * 判断一个状态是否持续时间过长或者过短，以确定当前是否处于静止状态
	 * @param currentTime
	 * @return
	 */
	boolean isPersistTimeValidate(Calendar currentTime){
		Calendar stateBeginTime=stepStatus.getStateBeginTime();
		if(stateBeginTime!=null){
			long interval=Math.abs(currentTime.getTimeInMillis()-stateBeginTime.getTimeInMillis());
			if((interval>=maxPersistTime)||interval<=minPersistTime){
				isStateValidate=false;
				return false;
			}else{
				isStateValidate=true;
				return true;
			}
		}else{
			stateBeginTime=currentTime;
			isStateValidate=false;
			return true;
		}
	}
	
	/**
	 * 判断当前数据是否对应于当前状态，如果对应于当前状态，则不进行状态转移，否则考虑转移到下一个状态
	 * @param realtimeData
	 * @return
	 */
	public boolean isDataValidate(float realtimeData) {
		// TODO Auto-generated method stub
//		if(isReferenceValueValide()){
//			float difference=Math.abs(realtimeData-referenceValue);
//			if(difference>threshold){
//				return false;
//			}else{
//				return true;
//			}
//		}else{
//			//还没有参考值
//			return true;
//		}
		return stateTransMonitor.retainCurrentState(realtimeData);
	}
	
	/**
	 * 波峰和波谷值是否合法
	 * @return
	 */
	public boolean isExtremumValidate(){
		return stateTransMonitor.isExtremumValidate();
	}
	
	/**
	 * 当前状态是否是有效状态
	 * @return
	 */
	public boolean isStateValidate(){
		return isStateValidate;
	}
	
	
	
	/**
	 * 当前值对应于当前状态的情况下，寻找当前状态的极值。
	 * @param realtimeData
	 * @param timestamp
	 */
	void findExtremumData(float realtimeData,Calendar timestamp){
		if(findNewExtremum(realtimeData)){
			stepStatus.setExtremum(realtimeData);
			stepStatus.setExtremumTimeStamp(timestamp);
			stateTransMonitor.setExtremumValue(realtimeData);
		}
	}
	
	
	/**
	 * 可以继续保持当前状态，条件为
	 * 值未超出阈值且时间间隔没有超出上限
	 * @param realtimeData
	 * @param timeStamp
	 * @return
	 */
	public boolean canRtainCurrentState(float realtimeData,Calendar timeStamp){
		Calendar stateBeginTime=stepStatus.getStateBeginTime();
		long interval=Math.abs(timeStamp.getTimeInMillis()-stateBeginTime.getTimeInMillis());
		return ((interval<maxPersistTime)&&isDataValidate(realtimeData));
	}
	
	/**
	 * 可以转移至下一个状态，条件为：
	 * 值超出且时间间隔合法且具有合法的波峰和波谷值
	 * @param realtimeData
	 * @param timeStamp
	 * @return
	 */
	public boolean canTransiteToNextState(float realtimeData,Calendar timeStamp){
		return (
				(!isDataValidate(realtimeData))
				&&isPersistTimeValidate(timeStamp)
				&&isExtremumValidate());
	}
	
	/**
	 * 可以转移至最初状态，条件为：
	 * 时间间隔不合法或者值超出阈值但是波峰波谷值非法
	 * @param realtimeData
	 * @param timeStamp
	 * @return
	 */
	public boolean canBackTheIdleState(float realtimeData,Calendar timeStamp){
		return (!isPersistTimeValidate(timeStamp)||(!isDataValidate(realtimeData)&&!isExtremumValidate()));
	}
	
	/**
	 * 新的极值
	 * @param realtimeData
	 * @return
	 */
	protected abstract boolean findNewExtremum(float realtimeData);
	
	
	/**
	 * 设置参考值（距离峰值的距离或者绝对分界值）
	 * @param value
	 */
	 public void setNextStateReferenceValue(float value){
		stateTransMonitor.setReferenceValue(value);
	 }
	
	
	/**
	 * 返回本状态分界值
	 * @return
	 */
	public abstract float getCriticalValue();
}
