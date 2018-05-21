package yuanxz.uestc.samplerecord;

import java.util.Arrays;
import java.util.Iterator;

import android.util.Log;

public class SampleRecord implements Iterable{
	
	public static final String TAG="SampleRecord";
	
	public static final int RECORD_SIZE=100;//一般情况，sensor_delay_game为50Hz，100大小足够
	private int sampleCount=0;
	private float[] samples=new float[RECORD_SIZE];
	private int index=0;
	private volatile boolean isRecycle=true;
	
	
	public SampleRecord(){
		Arrays.fill(samples, 0);
	}
	
	/**
	 * 增加样本
	 * @param sampleData
	 */
	public void addSample(float sampleData){
		if(index<RECORD_SIZE){
			samples[index++]=sampleData;
			sampleCount++;
		}else{
			System.err.println("sample out of bound!");
		}
	}
	
	/**
	 * 是否可以拿来利用
	 * @return
	 */
	public synchronized boolean isRecycle(){
		if(index==0&&sampleCount==0&&isRecycle){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 将这个对象重新分配
	 * @return 如果没有占用，则分配成功，否则分配失败
	 */
	public synchronized boolean rellocate(){
		if(isRecycle){
			isRecycle=false;
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 释放资源
	 */
	public synchronized void recycle(){
		Log.i(TAG,"成功回收利用！");
		clear();
		isRecycle=true;
	}
	
	/**
	 * 清除数据
	 */
	public synchronized void clear(){
		sampleCount=0;
		index=0;
		Arrays.fill(samples, 0);
	}
	
	/**
	 * @return 返回样本个数
	 */
	public int size(){
		return sampleCount;
	}
	
	@Override
	public Iterator<Float> iterator() {
		// TODO Auto-generated method stub
		return new recordIterator();
	}
	
	private class recordIterator implements Iterator<Float>{
		
		recordIterator() {
			// TODO Auto-generated constructor stub
			index=0;//保证从开始进行迭代
		}
		
		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			if(index<sampleCount){
				return true;
			}else{
				return false;
			}
		}

		@Override
		public Float next() {
			// TODO Auto-generated method stub
			return samples[index++];
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			System.out.println("不支持此操作！");
		}
		
	}
}
