package yuanxz.uestc.stepstate;

import java.util.Iterator;

import android.util.Log;

import yuanxz.uestc.samplerecord.SampleRecord;
import yuanxz.uestc.stepcount.StepInfo;
import yuanxz.uestc.stepstate.y.YCrestState;
import yuanxz.uestc.stepstate.z.ZIdleStepState;

/**
 * @author xingzhong
 * 此类使用绝对的分界值，但是这个分界值依赖于上一个状态
 */
public class PreStateRelatedABSTransMonitor extends StateTransMonitor{
	private static final String TAG="PreStateRelatedABSTransMonitor";
	
	private StepStatus preStatus;
	
	//原始的波峰波谷限定值
	private float originalUpperThreshold;
	private float  originalBottomThreshold;
	
	public PreStateRelatedABSTransMonitor(float referenceValue,
			float upperThreshold, float bottomThreshold) {
		super(referenceValue, upperThreshold, bottomThreshold);
		// TODO Auto-generated constructor stub
		upperCriticalValue=referenceValue;
		bottomCriticalValue=referenceValue;
		originalBottomThreshold=bottomThreshold;
		originalUpperThreshold=upperThreshold;
	}

	@Override
	protected void calculateCriticalValue(float extremValue) {
		// TODO Auto-generated method stub
		//如果不是从空闲状态转换而来，则需要结合极值来不断确定分界值
		if(preStatus.getId()!=ZIdleStepState.IDLE_STATE_ID){
			float criticalValue=(preStatus.getExtremum()+extremValue)/2;
//			Log.d(TAG,"extremValue="+extremValue+" criticalValue="+criticalValue);
			setCriticalValue(criticalValue);
		}
	}
	
	
	/**
	 * 开始监控，根据上一个状态来设置分界值等
	 * @param preState
	 * @param stepInfo
	 */
	public void start(StepState preState,StepInfo stepInfo){
		try {
			this.preStatus=(StepStatus) preState.getStepStatus().clone();
			//如果从空闲状态直接转换而来，则可以确定分界值，否则要结合本状态的极值来确定
			float criticalValue;
			if(preStatus.getId()==ZIdleStepState.IDLE_STATE_ID){//
				criticalValue=calculateAverageValue(stepInfo);
				setCriticalValue(criticalValue);
				Log.i(TAG,"上一个空闲状态的零参考值为："+criticalValue);
			}else{
				criticalValue=preState.getCriticalValue();
				if(preState.getStateID()==YCrestState.Y_CREST_STATE_ID){
					Log.i(TAG,"上一个波峰状态的零参考值为："+criticalValue);
				}else{
					Log.i(TAG,"上一个波谷状态的零参考值为："+criticalValue);
				}
			}
			//根据上一个状态的零参考值来设置当前状态的上下限
			//重新设置阈值(根据上一个状态)
			upperThreshold=criticalValue+originalUpperThreshold;
			Log.d(TAG,"upperThreshold="+criticalValue+"+"+originalUpperThreshold);
			bottomThreshold=criticalValue+originalBottomThreshold;
			Log.d(TAG,"bottomThreshold="+criticalValue+"+"+originalBottomThreshold);
			
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG,"状态拷贝出错！");
		}
	}

	/**
	 * 求记录的平均值
	 * @param sampleRecord
	 * @return
	 */
	private float calculateAverageValue(StepInfo stepInfo){
		
		float sum=0;
		SampleRecord sampleRecord=stepInfo.getYSampleRecord();//这里直接取Y数据欠妥
		Iterator<Float> it=sampleRecord.iterator();
		while(it.hasNext()){
			sum+=it.next();
		}
		return sum/(sampleRecord.size());
	}
	
	
	@Override
	public void setReferenceValue(float referenceValue) {
		super.setReferenceValue(referenceValue);
		setCriticalValue(referenceValue);//referenceValue就是CriticalValue
	}
	
	public void setCriticalValue(float criticalValue){
		super.setUpperCriticalValue(criticalValue);
		super.setBottomCriticalValue(criticalValue);
	}
}
