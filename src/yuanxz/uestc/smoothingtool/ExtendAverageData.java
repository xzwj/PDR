package yuanxz.uestc.smoothingtool;

import java.util.Calendar;

public class ExtendAverageData {
	private float zAverageValue;
	private float yAverageValue;
	private Calendar averageTimeStamp;
	public ExtendAverageData() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ExtendAverageData(float zAverageValue, float yAverageValue,
			Calendar averageTimeStamp) {
		super();
		this.zAverageValue = zAverageValue;
		this.yAverageValue = yAverageValue;
		this.averageTimeStamp = averageTimeStamp;
	}
	public float getzAverageValue() {
		return zAverageValue;
	}
	public void setzAverageValue(float zAverageValue) {
		this.zAverageValue = zAverageValue;
	}
	public float getyAverageValue() {
		return yAverageValue;
	}
	public void setyAverageValue(float yAverageValue) {
		this.yAverageValue = yAverageValue;
	}
	public Calendar getAverageTimeStamp() {
		return averageTimeStamp;
	}
	public void setAverageTimeStamp(Calendar averageTimeStamp) {
		this.averageTimeStamp = averageTimeStamp;
	}
	
}
