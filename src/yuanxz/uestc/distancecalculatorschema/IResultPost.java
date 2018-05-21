package yuanxz.uestc.distancecalculatorschema;

public interface IResultPost {
	/**
	 * 传递计算结果
	 * @param result
	 * @param currentStep
	 */
	public void postResult(double result,int currentStep);
}
