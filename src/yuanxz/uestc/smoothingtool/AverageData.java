package yuanxz.uestc.smoothingtool;

import java.util.Calendar;

/**
 * @author yuanxz
 * 平均后的值和时间戳的封装
 */
public class AverageData {

	float averageValue;
	Calendar averageTimeStamp;
	public AverageData(float averageValue, Calendar averageTimeStamp) {
		super();
		this.averageValue = averageValue;
		this.averageTimeStamp = averageTimeStamp;
	}
	public AverageData() {
		super();
		// TODO Auto-generated constructor stub
	}
	public float getAverageValue() {
		return averageValue;
	}
	public void setAverageValue(float averageValue) {
		this.averageValue = averageValue;
	}
	public Calendar getAverageTimeStamp() {
		return averageTimeStamp;
	}
	public void setAverageTimeStamp(Calendar averageTimeStamp) {
		this.averageTimeStamp = averageTimeStamp;
	}
	
	
}
