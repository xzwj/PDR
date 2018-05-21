package yuanxz.uestc.stepcount;

import java.util.Calendar;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import android.util.Log;
import yuanxz.uestc.distancecalculatorschema.CalculateSchema;
import yuanxz.uestc.samplerecord.SampleRecord;
import yuanxz.uestc.samplerecord.SampleRecordPoolMP;

/**
 * @author yuanxz
 * 多线程版本
 */
/**
 * @author xingzhong
 * 由它统一管理资源的应用计数
 */
public class StepInfoMP extends StepInfo{

	
	private static final String TAG="StepInfoMP"	;
	
	private volatile AtomicInteger requestThreadNum;//准备请求这个资源的线程数量
	private volatile AtomicInteger referenceTime;//引用计数
	private CopyOnWriteArrayList<Long> referenceThread;
	
	public StepInfoMP() {
		// TODO Auto-generated constructor stub
		requestThreadNum=new AtomicInteger(0);
		referenceTime=new AtomicInteger(0);
		referenceThread=new CopyOnWriteArrayList<Long>();
	}

	public StepInfoMP(Calendar stepStartTime, Calendar stepFinishTime,
			float maxAcceleration, float minAcceleration) {
		super(stepStartTime, stepFinishTime, maxAcceleration, minAcceleration);
		// TODO Auto-generated constructor stub
		requestThreadNum=new AtomicInteger(0);
		referenceTime=new AtomicInteger(0);
		referenceThread=new CopyOnWriteArrayList<Long>();
	}

	@Override
	public SampleRecord getZSampleRecord() {
		// TODO Auto-generated method stub
		increaseReferenceTime();
		return super.getZSampleRecord();
	}

	@Override
	public SampleRecord getYSampleRecord() {
		// TODO Auto-generated method stub
		increaseReferenceTime();
		return super.getYSampleRecord();
	}


	@Override
	public void releaseRes() {
		// TODO Auto-generated method stub
	    Log.i(TAG,"====>before decrease:referenceTime = "+referenceTime+" requestThreadnum = "+ requestThreadNum);
		decreaseReferenceTime();
		if((referenceTime.intValue()<=0&&requestThreadNum.intValue()<=0)
//				&&(
//						(!stepInfo.getYSampleRecord().isRecycle())
//						&&(!stepInfo.getZSampleRecord().isRecycle()))
						){//没有引用了，可以回收资源以便重复利用
			Log.i(TAG,"referenceTime = "+referenceTime+" requestThreadnum = "+ requestThreadNum +"stepInfo开始回收利用");
			super.releaseRes();
		}
	}

	/**
	 * 增加引用计数
	 */
	private void increaseReferenceTime(){
		long threadID=Thread.currentThread().getId();
		if(!referenceThread.contains(threadID)){
			referenceThread.add(threadID);
			referenceTime.addAndGet(1);
			if(referenceTime.intValue()>requestThreadNum.intValue()){
				Log.w(TAG,"实际引用次数大于登记的请求次数！");
			}
		}else{
			Log.w(TAG,"同一个线程对这个资源多次引用只记一次");
		}
		
	}
	
	/**
	 * 如果当前线程对此资源占用过，则解除占用并减少引用计数
	 */
	private void decreaseReferenceTime(){
		long threadID=Thread.currentThread().getId();
		if(referenceThread.contains(threadID)){
			referenceThread.remove(threadID);
			referenceTime.decrementAndGet();
			requestThreadNum.decrementAndGet();
			if(referenceTime.intValue()<0||requestThreadNum.intValue()<0){
				Log.w(TAG, "引用计数和请求计数释放错误！");
			}
		}else{
			Log.w(TAG, "decreaseReferenceTime:这个线程没有引用过这个资源！");
		}
	}

	@Override
	protected void allocRes() {
		// TODO Auto-generated method stub
		zSampleRecord=SampleRecordPoolMP.getInstance().get();
		ySampleRecord=SampleRecordPoolMP.getInstance().get();
//		zSampleRecord=new SampleRecord();
//		ySampleRecord=new SampleRecord();
	}

//	@Override
//	public StepInfo registerRequest() {
//		// TODO Auto-generated method stub
//		requestThreadNum.addAndGet(1);
//		Log.i(TAG,"====> registerRequest: requestThreadNum"+ requestThreadNum);
//		return this;
//	}

    @Override
    public void addSchema(CalculateSchema[] schemas) {
        requestThreadNum.getAndAdd(schemas.length);
        for(int i=0;i<schemas.length;i++){
            schemas[i].addTask(this);
        }
    }
	
	
	
	
}
