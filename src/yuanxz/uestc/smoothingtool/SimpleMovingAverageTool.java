package yuanxz.uestc.smoothingtool;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Observer;

import yuanxz.uestc.stepstate.z.ZExtendStepStateSwitcher;

public class SimpleMovingAverageTool extends AverageTool{
	
	private float[] averageSmoothValue;
	
	private int currentSize=0;
	
	private float sumTemp=0;
	
	
	public SimpleMovingAverageTool(int windowSize){
		super(windowSize);
		averageSmoothValue=new float[windowSize];
		Arrays.fill(averageSmoothValue, 0);
	}
	
	/**
	 * 平滑
	 * @param data
	 * @return
	 */
	public float averageSmooth(float data){
		updateSmoothValue(data);
		return smooth();
	}
	
	/**
	 * 用来平均的数是否初始化完毕
	 * @return
	 */
	private boolean isSmoothValueInited(){
		return (windowSize==currentSize);
	}
	
	/**
	 * 更新用来平均的数组，淘汰老数据，插入新数据。若数据不够，则不淘汰
	 * @param newValue
	 */
	private void updateSmoothValue(float newValue){
		if(!isSmoothValueInited()){//数据不够
			averageSmoothValue[currentSize++]=newValue;
			sumTemp+=newValue;
		}else{
			sumTemp-=averageSmoothValue[0];
			//更新
			for(int i=1;i<windowSize;i++){
				averageSmoothValue[i-1]=averageSmoothValue[i];
			}
			averageSmoothValue[windowSize-1]=newValue;
			
			sumTemp+=newValue;
		}
	}
	
	/**
	 * 简单求平均
	 * @return
	 */
	private float smooth(){
		return sumTemp/currentSize;
	}



	@Override
	public AverageData doAverage(float realtimeValue, Calendar timeStamp) {
		// TODO Auto-generated method stub
		return new AverageData(averageSmooth(realtimeValue), timeStamp);
	}

	
}
