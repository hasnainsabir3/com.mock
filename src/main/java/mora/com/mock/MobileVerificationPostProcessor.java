package mora.com.mock;

import org.json.JSONObject;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.convertions.ResultToJSON;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;

public class MobileVerificationPostProcessor implements DataPostProcessor2 {

	@Override
	public Object execute(Result backendResult, DataControllerRequest request, DataControllerResponse arg2) throws Exception {
		// TODO Auto-generated method stub
		String id = request.getParameter("id");
		String mobileNum = request.getParameter("mobileNumber");
		
		
		//String jsonStr = ResultToJSON.convert(backendResult);
		
		
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("referenceNumber", "3fbd6405-ffbf-43da-af9e-7fd183f815a4");
		jsonObject.put("isOwner", true);
		jsonObject.put("mobile", mobileNum);
		jsonObject.put("id", id);
		jsonObject.put("opstatus", 0);
		jsonObject.put("httpStatusCode", 200);
		
		Result result = new Result();
		
		result = JSONToResult.convert(jsonObject.toString());
		
		return result;
	}

}
