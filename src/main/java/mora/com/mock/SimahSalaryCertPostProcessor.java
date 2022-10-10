package mora.com.mock;

import java.util.Set;

import org.json.JSONObject;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.session.Session;

public class SimahSalaryCertPostProcessor implements DataPostProcessor2 {

	@Override
	public Object execute(Result backResult, DataControllerRequest request, DataControllerResponse arg2)
			throws Exception {
		// TODO Auto-generated method stub

		String nin = request.getParameter("idNumber");
		String filePath = "/home/mora/MockFiles/SimahSalaryCert/" + nin;

		JSONObject jsonObject = ReadFile.readJSONFile(".json", filePath);
		JSONObject resultObject = mappingIntoSingleJSONObj(jsonObject.getJSONObject("data"), request);

		Result result = new Result();
		result = JSONToResult.convert(resultObject.toString());
		return result;
	}

	public static JSONObject mappingIntoSingleJSONObj(JSONObject obj, DataControllerRequest request) {
		JSONObject mainObj = new JSONObject();
		if (!obj.isNull("governmentSector")) {
			JSONObject pvtObj = obj.optJSONArray("governmentSector").getJSONObject(0);

			Set<String> keySet = pvtObj.keySet();
			Session session = request.getSession();
			for (String key : keySet) {
				Set<String> keyInnerSet = pvtObj.getJSONObject(key).keySet();
				if (keyInnerSet.size() > 0) {
					for (String innerKey : keyInnerSet) {
						System.out.println(innerKey);
						mainObj.put(innerKey, pvtObj.optJSONObject(key).optString(innerKey));
						if(session !=null)
							session.setAttribute(innerKey, pvtObj.optJSONObject(key).optString(innerKey));
					}
				}
			}

			Double govSal = Double.parseDouble(mainObj.optString("totalAllownces")) 
					+ Double.parseDouble(mainObj.optString("basicSalary"))
					- Double.parseDouble(mainObj.getString("totalDeductions"));
			mainObj.put("govSalary", govSal.toString());
			session.setAttribute("govSalary", govSal.toString());

		} else {
			JSONObject pvtObj = (JSONObject) obj.optJSONObject("privateSector").optJSONArray("employmentStatusInfo")
					.get(0);

			Double totalSalary = Double.parseDouble(pvtObj.optString("otherAllowance"))
					+ Double.parseDouble(pvtObj.optString("basicWage"))
					+ Double.parseDouble(pvtObj.optString("housingAllowance"));

			Set<String> keySet = pvtObj.keySet();
			Session session = request.getSession();
			for (String key : keySet) {
				mainObj.put(key, pvtObj.opt(key));
				if(session !=null)
					session.setAttribute(key, pvtObj.opt(key));
			}
			mainObj.put("privateTotalSalary", totalSalary.toString());
			session.setAttribute("privateTotalSalary", totalSalary.toString());
		}
		
		mainObj.put("opstatus", 0);
		mainObj.put("httpStatusCode", 200);
		
		return mainObj;
	}

}
