package yuanxz.uestc.stepcount;

import yuanxz.uestc.distancecalculatorschema.CalculateSchema;
import yuanxz.uestc.samplerecord.SampleRecordPool;

public class StepInfoSP extends StepInfo{

	@Override
	protected void allocRes() {
		// TODO Auto-generated method stub
		zSampleRecord=SampleRecordPool.getInstance().get();
		ySampleRecord=SampleRecordPool.getInstance().get();
	}


    @Override
    public void addSchema(CalculateSchema[] schemas) {
        // TODO Auto-generated method stub
        
    }

}
