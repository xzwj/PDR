package yuanxz.uestc.distancecalculatorschema;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import yuanxz.uestc.stepcount.StepInfo;

public abstract class CalculateSchema implements Runnable{
	public static final int QUEUE_SIZE=10;

	private ExecutorService exec;
	private BlockingQueue<StepInfo> tasks;
	private IResultPost resultPoster;//推送计算结果
	
	public CalculateSchema(IResultPost resultPoster){
		tasks=new ArrayBlockingQueue<StepInfo>(QUEUE_SIZE);
		exec=Executors.newCachedThreadPool();
		exec.execute(this);
		this.resultPoster=resultPoster;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!Thread.currentThread().isInterrupted()){
			synchronized (this) {
				while(tasks.isEmpty()){
					try {
						this.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						//会自动清除interrupt标记
					}
				}
				StepInfo currentTask=tasks.poll();
				double result=calculateStepLen(currentTask);
				resultPoster.postResult(result,currentTask.getStepNum());//推送计算结果
				//由于存在这个线程将资源使用完了其他线程还没有开始请求资源的情况，故这里主动放弃一次cpu
//				Thread.currentThread().yield();//主动放弃cpu
				currentTask.releaseRes();//释放资源
			}
		}
	}

	/**
	 * 添加计算任务
	 * @param stepInfo
	 */
	public void addTask(StepInfo stepInfo){
		tasks.add(stepInfo);
		synchronized (this) {
			this.notifyAll();
		}
	}
	
	/**
	 * 停止线程
	 */
	public void stop(){
		exec.shutdownNow();
		exec=null;
	}
	
	
	/**
	 * 计算步长
	 * @param stepInfo
	 * @return
	 */
	public abstract double calculateStepLen(StepInfo stepInfo);
}
