package yuanxz.uestc.smoothingtool;

import java.util.Calendar;
import java.util.Observer;

import yuanxz.uestc.stepstate.y.YStepStateSwitcher;
import yuanxz.uestc.stepstate.z.ZExtendStepStateSwitcher;

public class ExtendSmoothingSchema {
	protected ZExtendStepStateSwitcher stateSwitcher;
//	protected YStepStateSwitcher stateSwitcher;
	protected AverageData yAverageValue;
	protected AverageData zAverageValue;
	
	protected AverageTool yAverageTool;
	protected AverageTool zAverageTool;
	
	public ExtendSmoothingSchema(Observer observer,AverageTool yAverageTool,AverageTool zAverageTool){
		stateSwitcher=new ZExtendStepStateSwitcher();
//		stateSwitcher=new YStepStateSwitcher();
		stateSwitcher.addObserver(observer);
		this.yAverageValue=null;
		this.zAverageValue=null;
		this.yAverageTool=yAverageTool;
		this.zAverageTool=zAverageTool;
	}
	
	/**
	 * 开始计数
	 * @param realtimeData
	 * @param timeStamp
	 */
	public void startCount(float[] realtimeData,Calendar timeStamp){
		doAverage(realtimeData, timeStamp);
		if(averagingValueValidate()){
			stateSwitcher.inputData(
					new float[]{
							yAverageValue.getAverageValue(),
							zAverageValue.getAverageValue()},
					zAverageValue.getAverageTimeStamp());
//			stateSwitcher.inputData(
//					yAverageValue.getAverageValue(),
//					zAverageValue.getAverageTimeStamp());
		}
	}
	
	
	
	/**
	 * 判定平均操作是否完成
	 * @return
	 */
	protected boolean averagingValueValidate(){
		return (yAverageValue!=null&&zAverageValue!=null);
	}
	
	
	/**
	 * 执行平均操作
	 * @param realtimeData
	 * @param timeStamp
	 * @return
	 */
	protected void doAverage(float[] realtimeData,Calendar timeStamp){
		yAverageValue=yAverageTool.doAverage(realtimeData[0], timeStamp);
		zAverageValue=zAverageTool.doAverage(realtimeData[1], timeStamp);
	}
	
}
