package yuanxz.uestc.stepstate;

import android.util.Log;

public abstract class StateTransMonitor {
	public static final String TAG="StateTransMonitor";
	
	protected float referenceValue;//参考值，有两个用途，或者用来作为绝对分界值，或者用来计算绝对分界值（指定和极值的距离）
	protected float extremValue=Float.NaN;
	protected float upperCriticalValue;//状态之间的分界值(上)
	protected float bottomCriticalValue;
	
	//要求波峰和波谷必须能够超过相应的阈值，否则视为无效的波峰和波谷
	protected float upperThreshold;//波峰必须超过这个值
	protected float bottomThreshold;//波谷必须低于这个值
	
	public StateTransMonitor(float referenceValue,float upperThreshold,float bottomThreshold){
		this.referenceValue=referenceValue;
		this.upperThreshold=upperThreshold;
		this.bottomThreshold=bottomThreshold;
	}
	
	
	/**
	 * 是否可以继续保持当前状态
	 * @param currentValue
	 * @return
	 */
	public boolean retainCurrentState(float currentValue){
		//分界值在极值和当前值之间
		if((bottomCriticalValue>currentValue&&bottomCriticalValue<extremValue)
				||(upperCriticalValue<currentValue&&upperCriticalValue>extremValue)){
			
//			if((bottomCriticalValue>currentValue&&bottomCriticalValue<extremValue)){
//				Log.d(TAG,"峰值："+extremValue+">bottomCriticalValue："+bottomCriticalValue+">当前值："+currentValue+"   状态转移");
//			}else{
//				Log.d(TAG,"当前值："+currentValue+">upperCriticalValue："+upperCriticalValue+">峰值："+extremValue+"   状态转移");
//			}
			
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 * 设定极值
	 */
	public void setExtremumValue(float extremValue){
		this.extremValue=extremValue;
		calculateCriticalValue(extremValue);
	}
	
	/**
	 * 根据极值来计算分界值，并保证分解值的有效性
	 * @param extremValue
	 * @return
	 */
	protected abstract void calculateCriticalValue(float extremValue);
	
	/**
	 * 只重置极值，不重置参考值
	 */
	public void resetInfo(){
		extremValue=Float.NaN;
	}




	public float getReferenceValue() {
		return referenceValue;
	}

	public void setReferenceValue(float referenceValue) {
		this.referenceValue = referenceValue;
	}

	public float getUpperCriticalValue() {
		return upperCriticalValue;
	}

	public void setUpperCriticalValue(float upperCriticalValue) {
		this.upperCriticalValue = upperCriticalValue;
	}

	public float getBottomCriticalValue() {
		return bottomCriticalValue;
	}

	public void setBottomCriticalValue(float bottomCriticalValue) {
		this.bottomCriticalValue = bottomCriticalValue;
	}
	
	/**
	 * 判断目前是否探测到有效的波峰或者波谷
	 * @return
	 */
	public boolean isExtremumValidate(){
		if(!(extremValue>upperThreshold&&extremValue<bottomThreshold)){
			Log.w(TAG,"没有一个有效的峰值！");
			if(extremValue<upperThreshold){
				Log.w(TAG,"极值："+extremValue+"< upperThreshold:"+upperThreshold);
			}else{
				Log.w(TAG,"极值："+extremValue+"> bottomThreshold:"+bottomThreshold);
			}
		}
		return (extremValue>upperThreshold&&extremValue<bottomThreshold);
	}
	
	
}
