package yuanxz.uestc.stepstate.z;

import java.util.Calendar;
import java.util.Observer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import yuanxz.uestc.smoothingtool.ExtendSmoothingSchema;
import yuanxz.uestc.smoothingtool.SimpleMovingAverageTool;
import yuanxz.uestc.smoothingtool.SmoothingSchema;
import yuanxz.uestc.stepstate.StepStateSwitcher;
import android.util.Log;

public class ZStepCounter implements Runnable{
public static final String TAG="StepCounter";
	
	private SmoothingSchema smoothingSchema;
//	private ExtendSmoothingSchema smoothingSchema;
	
	private ZStepStateSwitcher stateSwitcher;
	
	private BlockingQueue<TimeStampData> dataQueue=
			new ArrayBlockingQueue<TimeStampData>(10, true);
	private ExecutorService exe;
	/**
	 * 需要一个观察者来接收一步的通知
	 * @param observer
	 */
	public ZStepCounter(Observer observer){
		StepStateSwitcher switcher=new ZStepStateSwitcher();
		switcher.addObserver(observer);
		smoothingSchema=new SmoothingSchema(switcher, new SimpleMovingAverageTool(5));
//		smoothingSchema=new ExtendSmoothingSchema(observer,
//				new SimpleMovingAverageTool(5), new SimpleMovingAverageTool(5));
		stateSwitcher=new ZStepStateSwitcher();
		stateSwitcher.addObserver(observer);

		//线程开始运行
		exe=Executors.newCachedThreadPool();
		exe.execute(this);
		
	}

	public void startCount(float realtimeData, Calendar timeStamp){
//		smoothingSchema.startCount(realtimeData[1], timeStamp);
//		smoothingSchema.startCount(realtimeData, timeStamp);
		dataQueue.offer(new TimeStampData(realtimeData, timeStamp));
	}

	/**
	 * 结束
	 */
	public void stopCount(){
		smoothingSchema.stopCount();
//		exe.shutdownNow();
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!Thread.currentThread().isInterrupted()){
			try {
				TimeStampData data=dataQueue.take();
				//debug
//				Log.d(TAG,"获取到一个计算请求");
				smoothingSchema.startCount(data.realtimeData, data.timestamp);
//				Log.d(TAG,"计算完成");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
		}
		Log.i(TAG,"计算线程退出");
	}
	
	
	private class TimeStampData{
		float realtimeData;
		Calendar timestamp;
		public TimeStampData(float realtimeData,Calendar timestamp){
			this.realtimeData=realtimeData;
			this.timestamp=timestamp;
		}
		
		
	}
}
