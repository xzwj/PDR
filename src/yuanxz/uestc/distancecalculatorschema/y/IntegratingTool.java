package yuanxz.uestc.distancecalculatorschema.y;

import java.util.Arrays;
import java.util.Iterator;

import android.util.Log;
import yuanxz.uestc.samplerecord.SampleRecord;
import yuanxz.uestc.stepcount.StepInfo;

public class IntegratingTool {

	public static final String TAG="IntegratingTool";
	
	private static float[] result;
	
	static{
		result=new float[SampleRecord.RECORD_SIZE];
	}
	
	/**
	 * 进行一次积分运算
	 * @param initValue
	 * @param sampleFrequency
	 * @param stepInfo
	 * @return
	 */
	public static float[] integrate(float initValue,int sampleFrequency,StepInfo stepInfo){
		//积分运算中的相邻两个积分值
		float valueTemp1;
		float valueTemp2;
		int size=0;//积分的中间结果数量
		
		SampleRecord sampleRecord=stepInfo.getYSampleRecord();
		float zeroReference=stepInfo.getZeroReferenceValue();
		
		Arrays.fill(result, 0);//reset
		
		if(sampleRecord.size()<2){
			Log.e(TAG, "Ysize="+sampleRecord.size()+",stepInfo数据太少！");
			Log.e(TAG,"Zsize="+stepInfo.getZSampleRecord().size());
			//debug
			StringBuilder sb=new StringBuilder();
			sb.append("data:\n");
			Iterator<Float> it=sampleRecord.iterator();
			while(it.hasNext()){
				sb.append(it.next()+",");
			}
			sb.append('\n');
			Log.d(TAG, sb.toString());
			
		}else{
			float interval=getInterval(sampleFrequency);
			Iterator<Float> it=sampleRecord.iterator();
			valueTemp1=it.next();
			valueTemp2=it.next();
			
			//利用微元法进行积分，
			result[0]=particleCalculate(initValue,valueTemp1, valueTemp2, interval,zeroReference);
			size=1;
			while(it.hasNext()){
				valueTemp1=valueTemp2;
				valueTemp2=it.next();
				result[size]=particleCalculate(result[size-1], valueTemp1, valueTemp2, interval,zeroReference);
				size++;
			}
		}
		
		return result;
	}
	
	/**
	 * @param initValue
	 * @param sampleFrequency
	 * @param sampleRecord
	 * @return
	 */
	public static float[] integrate(float initValue,int sampleFrequency,SampleRecord sampleRecord,float zeroReferenceValue){
		//积分运算中的相邻两个积分值
		float valueTemp1;
		float valueTemp2;
		int size=0;//积分的中间结果数量
		Arrays.fill(result, 0);//reset
		
		if(sampleRecord.size()<2){
			Log.e(TAG, "size="+sampleRecord.size()+",sampleRecord数据太少！");
			//debug
			StringBuilder sb=new StringBuilder();
			sb.append("data:\n");
			Iterator<Float> it=sampleRecord.iterator();
			while(it.hasNext()){
				sb.append(it.next()+",");
			}
			sb.append('\n');
			Log.d(TAG, sb.toString());
			
		}else{
			float interval=getInterval(sampleFrequency);
			Iterator<Float> it=sampleRecord.iterator();
			valueTemp1=it.next();
			valueTemp2=it.next();
			
			//利用微元法进行积分，
			result[0]=particleCalculate(initValue,valueTemp1, valueTemp2, interval,zeroReferenceValue);
			size=1;
			while(it.hasNext()){
				valueTemp1=valueTemp2;
				valueTemp2=it.next();
				result[size]=particleCalculate(result[size-1], valueTemp1, valueTemp2, interval,zeroReferenceValue);
				size++;
			}
		}
		
		return result;	
	}
	
	
	
	/**
	 * 进行一次积分运算
	 * @param initValue
	 * @param sampleFrequency
	 * @param stepInfo
	 * @return
	 */
	public static float integrate(float initValue,int sampleFrequency,float[] data,float zeroReferenceValue){
		//积分运算中的相邻两个积分值
		float valueTemp1;
		float valueTemp2;
		float result=0;
		if(data!=null){
			if(data.length<2){
				System.out.println("stepInfo数据太少！");
			}else{
				float interval=getInterval(sampleFrequency);
				valueTemp1=data[0];
				valueTemp2=data[1];
				
				//利用微元法进行积分，
				result=particleCalculate(initValue,valueTemp1, valueTemp2, interval,zeroReferenceValue);
				for(int i=2;i<data.length;i++){
					valueTemp1=valueTemp2;
					valueTemp2=data[i];
					result=particleCalculate(result, valueTemp1, valueTemp2, interval,zeroReferenceValue);
				}
			}
			Log.i(TAG,"result="+result);
		}else{
			Log.e(TAG,"data==null");
		}
		return result;
	}
	
	
	
	/**
	 * 利用微元法进行一次微元积分（已经考虑正负值）
	 * @param value1
	 * @param value2
	 * @return
	 */
	private static float particleCalculate(float initValue,float value1,float value2,float interval,float zeroReferenceValue){
//		return (value1*interval+((value2-value1)*interval/2));
		value1-=zeroReferenceValue;
		value2-=zeroReferenceValue;
		return (initValue+(value1+(value2-value1)/2)*interval);
	}
	
	
	
	/**
	 * 根据采样频率计算采样时间间隔
	 * @return
	 */
	private static float getInterval(int sampleFrequency){
		if(sampleFrequency>0){
			return (float)1/(float)sampleFrequency;
		}else{
			Log.e(TAG,"sampleFrequency<0!!!!!");
			return 0;
		}
	}
	
	
}
