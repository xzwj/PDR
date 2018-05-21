package yuanxz.uestc.message;

/**
 * @author xingzhong
 * 用于统一信息管理
 */
public class StepCountMessage {
	/**
	 * 开始计步
	 */
	public static final int MSG_START_STEP_COUNT=0x80;
	/**
	 * 表示对一步分析计步完成
	 */
	public static final int MSG_ONE_STEP=0x81;
	/**
	 * 表示计步模块检测到一步
	 */
	public static final int MSG_STEP_COUNT=0x82;
	/**
	 * 计步停止
	 */
	public static final int MSG_STOP_STEP_COUNT = 0x83;
}
