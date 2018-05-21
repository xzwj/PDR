package yuanxz.uestc.smoothingtool;

import java.util.Calendar;

public abstract class AverageTool {
	
	protected int windowSize;
	
	public AverageTool(int windowsize){
		this.windowSize=windowsize;
	}
	
	
	/**
	 * 执行平均操作,平均操作完成返回平均后的值和时间戳的封装，否则返回null
	 * @param realtimeValue
	 * @param timeStamp
	 * @return
	 */
	public abstract AverageData doAverage(float realtimeValue,Calendar timeStamp);
	
}
