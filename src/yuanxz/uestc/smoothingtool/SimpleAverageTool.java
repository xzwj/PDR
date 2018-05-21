package yuanxz.uestc.smoothingtool;

import java.util.Calendar;
import java.util.Observer;

import yuanxz.uestc.stepstate.z.ZExtendStepStateSwitcher;

public class SimpleAverageTool extends AverageTool{

	private int averageTimes;
	private float averageValueTemp;
	private Calendar averageTimeTemp;
	
	public SimpleAverageTool(int windowSize){
		super(windowSize);
		init();
	}
	
	@Override
	public AverageData doAverage(float realtimeValue, Calendar timeStamp) {
		// TODO Auto-generated method stub
		calculateAverageValue(realtimeValue, timeStamp);
		if(averageValueCalculateCompleted()){
			return new AverageData(averageValueTemp, averageTimeTemp);
		}else{
			return null;
		}
	}

	/**
	 * 初始化
	 * @param observer
	 */
	private void init(){
		averageTimeTemp=Calendar.getInstance();
		averageTimeTemp.clear();
		averageValueTemp=0;
		averageTimes=0;
	}
	
	/**
	 * 计算平均值
	 * @param currentValue
	 * @param currentTime
	 */
	private void calculateAverageValue(float currentValue,Calendar currentTime){
		if(!averageValueCalculateCompleted()){
			//平均值
			averageValueTemp=((averageValueTemp*averageTimes)+currentValue)/(averageTimes+1);
			averageTimes++;
			if(averageValueCalculateCompleted()){
				//平均时间
				long timeInMillis=averageTimeTemp.getTimeInMillis()+currentTime.getTimeInMillis();
				averageTimeTemp.setTimeInMillis(timeInMillis/2);
			}
		}else{
			//初始设定
			averageTimes=1;
			averageValueTemp=currentValue;
			averageTimeTemp.clear();
			averageTimeTemp=currentTime;
		}
	}
	
	
	/**
	 * 是否平均了指定的次数
	 * @return
	 */
	private boolean averageValueCalculateCompleted(){
		if(averageTimes<windowSize){
			return false;
		}else{
			return true;
		}
	}
}
