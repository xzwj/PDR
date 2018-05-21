package yuanxz.uestc.samplerecord;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.util.Log;


/**
 * @author xingzhong
 * 多线程版本
 * 主要用来协助多个读线程管理SampleRecord中数据的生命周期，只会有一个写线程，不用管理写线程的同步问题
 */
public class SampleRecordDecrator extends SampleRecord {

	private static final String TAG="SampleRecordDecrator"	;
	
	private volatile AtomicInteger referenceTime;//引用计数
	
	private SampleRecord sampleRecord;
	
	private CopyOnWriteArrayList<Long> referenceThread;
	
	SampleRecordDecrator(SampleRecord sampleRecord){
		this.sampleRecord=sampleRecord;
		referenceTime=new AtomicInteger(0);
		referenceThread=new CopyOnWriteArrayList<Long>();
	}
	
	SampleRecordDecrator() {
		sampleRecord=new SampleRecord();
		// TODO Auto-generated constructor stub
		referenceTime=new AtomicInteger(0);
		referenceThread=new CopyOnWriteArrayList<Long>();
	}

	@Override
	public void addSample(float sampleData) {
		// TODO Auto-generated method stub
		sampleRecord.addSample(sampleData);
	}

	@Override
	public boolean isRecycle() {
		// TODO Auto-generated method stub
		return sampleRecord.isRecycle();
	}

	@Override
	public synchronized boolean rellocate() {
		// TODO Auto-generated method stub
		return sampleRecord.rellocate();
	}

	@Override
	public synchronized void recycle() {
		// TODO Auto-generated method stub
		decreaseReferenceTime();
		if((referenceTime.intValue()==0)&&(!isRecycle())){//没有引用了，可以回收资源以便重复利用
			sampleRecord.recycle();
		}
	}

	@Override
	public synchronized void clear() {
		// TODO Auto-generated method stub
		sampleRecord.clear();
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return sampleRecord.size();
	}

	//读线程主要是为了迭代这里面的数据，所以，一旦线程调用iterator，就将这个数据结构的引用
	//增加1
	@Override
	public Iterator<Float> iterator() {
		// TODO Auto-generated method stub
		increaseReferenceTime();
		
		return sampleRecord.iterator();
	}
	
	/**
	 * 增加引用计数
	 */
	private void increaseReferenceTime(){
		long threadID=Thread.currentThread().getId();
		if(!referenceThread.contains(threadID)){
			referenceThread.add(threadID);
			referenceTime.addAndGet(1);
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
		}else{
			Log.w(TAG, "decreaseReferenceTime:这个线程没有引用过这个资源！");
		}
	}
}
