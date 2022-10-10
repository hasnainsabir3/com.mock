package mora.com.mock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.json.JSONObject;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;

public class SImahInquiryPostProcessor implements DataPostProcessor2 {
	private static final Logger logger = LogManager.getLogger(SImahInquiryPostProcessor.class);

	@Override
	public Object execute(Result backResult, DataControllerRequest request, DataControllerResponse arg2)
			throws Exception {
		// TODO Auto-generated method stub

		String nin = request.getParameter("CID2");

		logger.error("Simah Enquiry response nin= " + nin);

		String filePath = "/home/mora/MockFiles/SimahInquiry/" + nin + "_RESPONSE";
		
		JSONObject jsonObject = ReadFile.readSimahInquiryXmlFile(".XML", filePath);
		jsonObject.put("opstatus", 0);
		jsonObject.put("httpStatusCode", 200);

		Result result = new Result();
		result = JSONToResult.convert(jsonObject.toString());

		return result;
	}
	
	
	
	
	

}
