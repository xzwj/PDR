package yuanxz.uestc.stepstate;

import android.location.Criteria;

/**
 * @author xingzhong
 * 这里的referenceValue是绝对的状态分界值
 */
public class AbsoluteStateTransMonitor extends StateTransMonitor {

	public AbsoluteStateTransMonitor(float referenceValue,float upperThreshold,float bottomThreshold) {
		super(referenceValue, upperThreshold, bottomThreshold);
		// TODO Auto-generated constructor stub
		upperCriticalValue=referenceValue;
		bottomCriticalValue=referenceValue;
		//根据分界值来作为零参考，设置上下峰值必须到达的阈值
		upperThreshold+=referenceValue;
		bottomThreshold+=referenceValue;
	}

	@Override
	protected void calculateCriticalValue(float extremValue) {
		// TODO Auto-generated method stub
		//referenceValue就是CriticalValue
//		upperCriticalValue=referenceValue;
//		bottomCriticalValue=referenceValue;
	}

	public void setCriticalValue(float criticalValue){
		super.setUpperCriticalValue(criticalValue);
		super.setBottomCriticalValue(criticalValue);
	}

	@Override
	public void setReferenceValue(float referenceValue) {
		// TODO Auto-generated method stub
		super.setReferenceValue(referenceValue);
		setCriticalValue(referenceValue);//referenceValue就是CriticalValue
	}
	
	
}
