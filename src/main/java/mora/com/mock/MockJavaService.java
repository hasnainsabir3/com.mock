package mora.com.mock;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.konylabs.middleware.api.OperationData;
import com.konylabs.middleware.api.ServiceRequest;
import com.konylabs.middleware.api.ServicesManager;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;

public class MockJavaService implements JavaService2{
	private static final Logger logger = LogManager.getLogger(MockJavaService.class);
	@Override
	public Object invoke(String arg0, Object[] arg1, DataControllerRequest request, DataControllerResponse arg3)
			throws Exception {


		Result result = new Result();
		String id = request.getParameter("CID2");


		HashMap<String, Object> imap = new HashMap<>();
		imap.put("CID2", id);

        
		logger.error("res = " + id);
        String res = DBPServiceExecutorBuilder.builder().withServiceId("testSimService")
        		.withOperationId("testsimInquir")
        		.withRequestParameters(imap)
        		.build().getResponse();    

        logger.error("res = " + res);
		result = JSONToResult.convert(res);
		
		
		
		
		String checkStr = (result.getParamValueByName("PCNMFE") != null)? result.getParamValueByName("SC_SCORE"):"Hasnin";
			logger.error("checkStr = " + checkStr);
		
		
		
		
		
		
		
		
		
		return result;
	}

}
