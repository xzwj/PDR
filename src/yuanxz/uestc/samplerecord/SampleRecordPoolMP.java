package yuanxz.uestc.samplerecord;

import android.util.Log;

/**
 * @author xingzhong
 * 注意这里管理的是SampleRecord,这个类没有管理引用计数的能力，调用者必须自己管理计数，否则，只要一个线程调用releaseRes,这个资源就被释放了
 */
public class SampleRecordPoolMP{
	public static final String TAG="SampleRecordPoolMP";
	public static final int POOL_SIZE=40;//资源池大小
	
	protected SampleRecord[] pool;
	
	public static SampleRecordPoolMP getInstance(){
		return InstanceClass.instance;
	}
	
	/**
	 * 获取一个资源
	 * @return
	 */
	public SampleRecord get(){
		int j=0;
		do{
			for(int i=0;i<POOL_SIZE;i++){
				if(pool[i].isRecycle()){
					if(pool[i].rellocate()){//再次尝试是否能够分配成功
						Log.i(TAG,"成功分配一个资源。");
						pool[i].clear();
						return pool[i];
					}
				}
			}
		}while(j++>3);
		Log.e(TAG,"资源池全被占用，无法再分配资源！");
		return null;
	}
	
	/**
	 * 释放资源
	 */
	public void releaseRes(){
		pool=null;
		System.gc();
	}
	
	
	private SampleRecordPoolMP() {
		// TODO Auto-generated constructor stub
		pool=new SampleRecord[POOL_SIZE];
		for(int i=0;i<POOL_SIZE;i++){
			pool[i]=new SampleRecord();
		}
	}

	/**
	 * @author xingzhong
	 * 延迟加载且保证多线程安全
	 */
	private static class InstanceClass{
		private static final SampleRecordPoolMP instance=new SampleRecordPoolMP();
	}
	
}
