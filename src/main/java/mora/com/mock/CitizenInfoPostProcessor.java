package mora.com.mock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;
import org.json.XML;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.session.Session;

/**
 * Hello world!
 *
 */
public class CitizenInfoPostProcessor implements DataPostProcessor2 {
	@Override
	public Object execute(Result backResult, DataControllerRequest request, DataControllerResponse arg2) throws Exception {
		// TODO Auto-generated method stub
		
		String nin = request.getParameter("nin");
		String filePath = "/home/mora/MockFiles/GetCitizenInfo/" + nin + "_Ilm_RSP";
		
		JSONObject jsonObject = ReadFile.readXmlFile(".XML", filePath);
		JSONObject mainObj = jsonObject.getJSONObject("getCitizenInfoResponse").getJSONObject("CitizenInfoResult");

		JSONObject resultObject = new JSONObject();
		
		// make result object according to the fabric output
		// setting values in session and check the session
		Session session = request.getSession();
		Set<String> keySet = mainObj.keySet();

		for (String key : keySet) {
			resultObject.put(key, mainObj.opt(key).toString());
			if(session !=null)
				session.setAttribute(key, mainObj.opt(key));
		}
		resultObject.put("opstatus", 0);
		resultObject.put("httpStatusCode", 200);

		resultObject.put("ns1:getCitizenInfoResponse", new JSONObject());
		

		Result result = new Result();
		result = JSONToResult.convert(resultObject.toString());

		return result;
	}

}
