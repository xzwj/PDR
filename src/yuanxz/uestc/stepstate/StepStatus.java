package yuanxz.uestc.stepstate;


import java.text.SimpleDateFormat;
import java.util.Calendar;

public class StepStatus implements Cloneable{
	protected int id;
	protected float extremum=Float.NaN;//极值
	protected Calendar extremumTimeStamp=null;//极值时间
	protected Calendar stateBeginTime=null;//状态开始时间
	protected Calendar stateFinishTime=null;
	
	public StepStatus(int id){
		this.id=id;
	}
	
	public StepStatus(int id,float extremum, Calendar extremumTimeStamp,
			Calendar stateBeginTime, Calendar stateFinishTime) {
		super();
		this.id=id;
		this.extremum = extremum;
		this.extremumTimeStamp = extremumTimeStamp;
		this.stateBeginTime = stateBeginTime;
		this.stateFinishTime = stateFinishTime;
	}
	
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getExtremum() {
		return extremum;
	}
	public void setExtremum(float extremum) {
		this.extremum = extremum;
	}
	public Calendar getExtremumTimeStamp() {
		return extremumTimeStamp;
	}
	public void setExtremumTimeStamp(Calendar extremumTimeStamp) {
		this.extremumTimeStamp = extremumTimeStamp;
	}
	public Calendar getStateBeginTime() {
		return stateBeginTime;
	}
	public void setStateBeginTime(Calendar stateBeginTime) {
		this.stateBeginTime = stateBeginTime;
	}
	public Calendar getStateFinishTime() {
		return stateFinishTime;
	}
	public void setStateFinishTime(Calendar stateFinishTime) {
		this.stateFinishTime = stateFinishTime;
	}
	
	/**
	 * 是否是有效的极值（非NaN)
	 * @return
	 */
	public boolean extremumIsValidate(){
		return !Float.isNaN(extremum);
	}
	
	public void reset(){
		extremumTimeStamp=null;
		stateBeginTime=null;
		stateFinishTime=null;
		extremum=Float.NaN;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		StringBuilder sb=new StringBuilder("StepStatus:");
		sb.append("id:"+id+", ");
		sb.append("extremum:"+extremum+", ");
		sb.append("extremumTime:"+sdf.format(extremumTimeStamp.getTime())+", ");
		sb.append("StartTime:"+sdf.format(stateBeginTime.getTime())+", ");
		sb.append("FinishTime:"+sdf.format(stateFinishTime.getTime())+"\n");
		return sb.toString();
	}
	
	
	
	
}
