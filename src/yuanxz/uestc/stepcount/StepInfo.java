package yuanxz.uestc.stepcount;

import java.util.Calendar;

import yuanxz.uestc.distancecalculatorschema.CalculateSchema;
import yuanxz.uestc.samplerecord.SampleRecord;
import yuanxz.uestc.samplerecord.SampleRecordPool;

public abstract class StepInfo {

	int stepNum;//这步是第多少步
	float zeroReferenceValue;//零位置参考值
	//一步的起始和结束时间以及其中2、3阶段加速度的极值
	Calendar stepStartTime;
	Calendar stepFinishTime;
	float maxAcceleration;
	float minAcceleration;
	
	SampleRecord zSampleRecord;//此步中z轴样本记录
	SampleRecord ySampleRecord;//y轴数据
	
	public StepInfo(Calendar stepStartTime, Calendar stepFinishTime,
			float maxAcceleration, float minAcceleration) {
		allocRes();
		this.stepStartTime = stepStartTime;
		this.stepFinishTime = stepFinishTime;
		this.maxAcceleration = maxAcceleration;
		this.minAcceleration = minAcceleration;
	}

	public StepInfo(){
		allocRes();
	}

	
	
	public float getZeroReferenceValue() {
		return zeroReferenceValue;
	}

	public void setZeroReferenceValue(float zeroReferenceValue) {
		this.zeroReferenceValue = zeroReferenceValue;
	}

	public Calendar getStepStartTime() {
		return stepStartTime;
	}

	public void setStepStartTime(Calendar stepStartTime) {
		this.stepStartTime = stepStartTime;
	}

	public Calendar getStepFinishTime() {
		return stepFinishTime;
	}

	public void setStepFinishTime(Calendar stepFinishTime) {
		this.stepFinishTime = stepFinishTime;
	}

	public float getMaxAcceleration() {
		return maxAcceleration;
	}

	public void setMaxAcceleration(float maxAcceleration) {
		this.maxAcceleration = maxAcceleration;
	}

	public float getMinAcceleration() {
		return minAcceleration;
	}

	public void setMinAcceleration(float minAcceleration) {
		this.minAcceleration = minAcceleration;
	}

	public int getStepNum() {
		return stepNum;
	}

	public void setStepNum(int stepNum) {
		this.stepNum = stepNum;
	}

	public SampleRecord getZSampleRecord() {
		return zSampleRecord;
	}

	public SampleRecord getYSampleRecord(){
		return ySampleRecord;
	}
	
	/**
	 * 记录数据
	 * @param sampleData
	 */
	public void recordZSample(float sampleData){
		zSampleRecord.addSample(sampleData);
	}
	
	/**
	 * 记录y轴数据
	 * @param sampleData
	 */
	public void recordYSample(float sampleData){
		ySampleRecord.addSample(sampleData);
	}
	
	/**
	 * 记录y轴数据和z轴数据
	 * @param sampleData
	 */
	public void recordSamples(float[] sampleData){
		ySampleRecord.addSample(sampleData[0]);
		zSampleRecord.addSample(sampleData[1]);
	}
	
	/**
	 * 释放资源，以便重新利用一定记得调用
	 */
	public void releaseRes(){
		zSampleRecord.recycle();
		ySampleRecord.recycle();
	}

	/**
	 * 清除数据记录
	 */
	public void clear(){
		zeroReferenceValue=0;
		stepNum=0;
		stepStartTime=null;
		stepFinishTime=null;
		maxAcceleration=-Float.MAX_VALUE;
		minAcceleration=Float.MAX_VALUE;
		zSampleRecord.clear();
		ySampleRecord.clear();
	}
	
	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		zSampleRecord.recycle();
		ySampleRecord.recycle();
		super.finalize();
	}
	
	/**
	 * 申请内存资源
	 */
	protected abstract void allocRes();
	
//	/**
//	 * 需要使用这个资源的线程登记请求
//	 */
//	public abstract StepInfo registerRequest();
	
	/**
	 * 添加要用到这个StepInfo的计算Schema（主要用于MP版本）
	 */
	public abstract void addSchema(CalculateSchema[] schemas);
	
	
}
