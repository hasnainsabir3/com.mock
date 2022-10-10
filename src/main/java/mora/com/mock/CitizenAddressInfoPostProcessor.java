package mora.com.mock;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;

public class CitizenAddressInfoPostProcessor implements DataPostProcessor2{

	private static final Logger logger = LogManager.getLogger(CitizenAddressInfoPostProcessor.class);
	
	
	@Override
	public Object execute(Result backResult, DataControllerRequest request, DataControllerResponse arg2) throws Exception {
		// TODO Auto-generated method stub
		
		String nin = request.getParameter("nin");
		String filePath = "/home/mora/MockFiles/GetCitizenAddressInfo/" + nin + "_NA_RSP";
		
		JSONObject jsonObject = ReadFile.readXmlFile(".XML", filePath);
		JSONObject mainObj = jsonObject.getJSONObject("getCitizenAddressInfoResponse").getJSONObject("CitizenAddressInfoResult");

		logger.error("CitizenAddressInfoPostProcessor = " + jsonObject);
		
		Set<String> keySet = mainObj.keySet();

		JSONObject resultObject = new JSONObject();
		for (String key : keySet) {
			resultObject.put(key, mainObj.opt(key).toString());
		}
		resultObject.put("opstatus", 0);
		resultObject.put("httpStatusCode", 200);
		resultObject.put("CitizenAddressInfoResult", mainObj);
		resultObject.put("ns1:getCitizenAddressInfoResponse", jsonObject.getJSONObject("getCitizenAddressInfoResponse"));
		
		logger.error("CitizenAddressInfoPostProcessor  resultObject= " + resultObject);
		
		Result result = new Result();
		result = JSONToResult.convert(resultObject.toString());

		return result;
	}

}
