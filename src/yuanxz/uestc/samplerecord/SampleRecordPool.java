package yuanxz.uestc.samplerecord;

import android.util.Log;

/**
 * @author xingzhong
 * 管理一定数量的SampleRecord，避免频繁分配数组
 * 注意SampleRecord实际是SampleRecordDecrator,由它自己管理自己的应用计数
 */
public class SampleRecordPool {

	public static final String TAG="SampleRecordPool";
	
	public static final int POOL_SIZE=20;//资源池大小
	
	protected SampleRecord[] pool;
	public static SampleRecordPool getInstance(){
		return InstanceClass.instance;
	}
	
	/**
	 * 获取一个资源
	 * @return
	 */
	public SampleRecord get(){
		for(int i=0;i<POOL_SIZE;i++){
			if(pool[i].isRecycle()){
				if(pool[i].rellocate()){//再次尝试是否能够分配成功
					Log.i(TAG,"成功分配一个资源。");
					pool[i].clear();
					return pool[i];
				}
			}
		}
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
	
	
	SampleRecordPool(){
		pool=new SampleRecordDecrator[POOL_SIZE];
		for(int i=0;i<POOL_SIZE;i++){
			pool[i]=new SampleRecordDecrator();//由资源管理自己的占有计数
		}
	}
	
	/**
	 * @author xingzhong
	 * 延迟加载且保证多线程安全
	 */
	private static class InstanceClass{
		private static final SampleRecordPool instance=new SampleRecordPool();
	}
	
}
