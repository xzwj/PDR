package yuanxz.uestc.smoothingtool;

import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;

import yuanxz.uestc.stepstate.StepStateSwitcher;
import yuanxz.uestc.stepstate.z.ZExtendStepStateSwitcher;
import yuanxz.uestc.stepstate.z.ZStepStateSwitcher;

public class SmoothingSchema {
	
	protected StepStateSwitcher stateSwitcher;
	
	protected AverageData averagedValue;
	
	protected AverageTool averageTool;
	
	public SmoothingSchema(StepStateSwitcher stateSwitcher,AverageTool averageTool){
		this.stateSwitcher=stateSwitcher;
		this.averagedValue=null;
		this.averageTool=averageTool;
	}
	
	/**
	 * 开始计数
	 * @param realtimeData
	 * @param timeStamp
	 */
	public void startCount(float realtimeData,Calendar timeStamp){
		averagedValue=doAverage(realtimeData, timeStamp);
		if(averagingValueValidate()){
			stateSwitcher.inputData(averagedValue.getAverageValue(),averagedValue.getAverageTimeStamp());
		}
	}
	
	/**
	 * 停止计数
	 */
	public void stopCount()
	{
		stateSwitcher.setstep(0);
	}
	/**
	 * 判定平均操作是否完成
	 * @return
	 */
	protected boolean averagingValueValidate(){
		return averagedValue!=null;
	}
	
	
	/**
	 * 执行平均操作
	 * @param realtimeData
	 * @param timeStamp
	 * @return
	 */
	protected AverageData doAverage(float realtimeData,Calendar timeStamp){
		return (averagedValue=averageTool.doAverage(realtimeData, timeStamp));
	}
	
}
