package yuanxz.uestc.stepstate;

import android.util.Log;


/**
 * @author xingzhong
 * 使用相对值检测状态转移
 * 这里的reference指定的是分界值和极值的距离
 */
public class RelativeStateTransMonitor extends StateTransMonitor{

	public RelativeStateTransMonitor(float referenceValue,float upperThreshold,float bottomThreshold) {
		super(referenceValue, upperThreshold, bottomThreshold);
		// TODO Auto-generated constructor stub
		//不用设置分界阈值，因为这种转换是根据当前值相对于极值的距离来判定的，而不是相对于criticalValue来判定的
	}

	@Override
	protected void calculateCriticalValue(float extremValue) {
		// TODO Auto-generated method stub
//		float sign=extremValue/Math.abs(extremValue);//极值符号
//		float value=(Math.abs(extremValue)-referenceValue);//值
		upperCriticalValue=extremValue+referenceValue;
		bottomCriticalValue=extremValue-referenceValue;
	}

	@Override
	public boolean isExtremumValidate() {
		// TODO Auto-generated method stub
		Log.wtf(TAG, "RelativeStateTransMonitor调用isExtremumValidate一律返回true,应该用retainCurrentState(float)代替！");
		return true;
	}
	
	

}
