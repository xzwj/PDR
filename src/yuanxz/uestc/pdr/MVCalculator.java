package yuanxz.uestc.pdr;


/**
 * @author yuanxz
 * 均值和方差计算，针对数据不是一下完全给全，而是一个个给出的情况，使用递推的方法来计算
 */
public class MVCalculator {

	private float meanTemp=0;//均值
	private float varianceTemp=0;//方差
	private int k=0;//样本数量
	private float eSquareTemp=0;//E(x2),用于计算方差
	
	/**
	 * 新增加一个
	 * @param data
	 */
	public void inputData(float data){
		++k;
		calculateMean(data);
		calculateVariance(data);
	}
	
	/**
	 * 计算均值
	 * @param data
	 */
	private void calculateMean(float data){
		meanTemp=((meanTemp*(k-1))+data)/(k);
	}
	
	/**
	 * 计算方差，注意需要先计算均值
	 * @param data
	 */
	private void calculateVariance(float data){
		eSquareTemp=(float) ((eSquareTemp*(k-1)+Math.pow(data, 2))/k);
		varianceTemp=(float) (eSquareTemp-Math.pow(meanTemp, 2));
	}

	public float getMean() {
		return meanTemp;
	}

	public float getVariance() {
		return varianceTemp;
	}

	public int getK() {
		return k;
	}
	
	
}
