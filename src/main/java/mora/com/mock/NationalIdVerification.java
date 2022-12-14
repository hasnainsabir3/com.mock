package mora.com.mock;

import com.dbp.core.error.DBPApplicationException;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.konylabs.middleware.api.OperationData;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.exceptions.MiddlewareException;
import com.mora.util.ErrorCode;
import com.mora.util.Utilities;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class NationalIdVerification implements JavaService2 {
	private static final Logger logger = LogManager.getLogger(NationalIdVerification.class);

	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		boolean istemp = false;
		Result result = new Result();
		JSONObject JsonResponse = null;
		String res = "";
		Map<String, String> customerResponse = new HashMap<>();
		if (request.getParameter("NationalID").toString().equals("")) {
			result = ErrorCode.ERR_66008.updateResultObject(result);
		} else if (request.getParameter("Mobile").toString().equals("")) {
			result = ErrorCode.ERR_66009.updateResultObject(result);
		} else if (request.getParameter("DOB").toString().equals("")) {
			result = ErrorCode.ERR_66009.updateResultObject(result);
		} else if (request.getParameter("LoanPurpose").toString().equals("")) {
			result = ErrorCode.ERR_660010.updateResultObject(result);
		} else if (request.getParameter("IBAN").toString().equals("")) {
			result = ErrorCode.ERR_660011.updateResultObject(result);
		} else if (request.getParameter("ChargeCode").toString().equals("")) {
			result = ErrorCode.ERR_660011.updateResultObject(result);
		} else {
			JSONObject JresponseEnroll = new JSONObject();
			JresponseEnroll = enrollCustomer(request, response);
			if (JresponseEnroll.optString("ResponseCode").equals("00")) {
				try {
					HashMap<String, Object> imap = new HashMap<>();
					imap.put("$filter", "Ssn eq " + request.getParameter("NationalID"));
					OperationData serviceData = request.getServicesManager().getOperationDataBuilder()
							.withServiceId("DBXDBServices").withOperationId("dbxdb_customer_get").build();
					res = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices")
							.withOperationId("dbxdb_customer_get").withRequestParameters(imap).build().getResponse();
					JsonResponse = new JSONObject(res);
					if (JsonResponse.getJSONArray("customer").length() > 0) {
						result = ErrorCode.ERR_660012.updateResultObject(result);
					} else {
						createCitizenVerifyStatus(request.getParameter("Mobile"), request.getParameter("NationalID"));
						JSONObject jobj = verifyMobileOwner(request, response);
						if (jobj.has("isOwnerVerify")) {
							if (jobj.optString("isOwnerVerify").equals("false"))
								result = ErrorCode.ERR_660013.updateResultObject(result);
						} else if (jobj.has("isOwnerVerifyError")) {
							if (jobj.optString("isOwnerVerifyError").equals("true"))
								result = ErrorCode.ERR_660014.updateResultObject(result);
						} else if (jobj.has("isCitizenSuccess")) {
							if (jobj.optString("isCitizenSuccess").equals("false")) {
								String str;
								switch ((str = jobj.optString("errorCode").toString()).hashCode()) {
								case 49:
									if (!str.equals("1"))
										break;
									result = ErrorCode.ERR_660015.updateResultObject(result);
									return result;
								case 50:
									if (!str.equals("2"))
										break;
									result = ErrorCode.ERR_660016.updateResultObject(result);
									return result;
								case 51:
									if (!str.equals("3"))
										break;
									result = ErrorCode.ERR_660017.updateResultObject(result);
									return result;
								case 53:
									if (!str.equals("5"))
										break;
									result = ErrorCode.ERR_660018.updateResultObject(result);
									return result;
								case 54:
									if (!str.equals("6"))
										break;
									result.addParam("ResponseCode", ErrorCode.ERR_660019.toString());
									result.addParam("Message", ErrorCode.ERR_660019.getErrorMessage());
									return result;
								case 55:
									if (!str.equals("7"))
										break;
									result.addParam("ResponseCode", ErrorCode.ERR_660020.toString());
									result.addParam("Message", ErrorCode.ERR_660020.getErrorMessage());
									return result;
								case 56:
									if (!str.equals("8"))
										break;
									result.addParam("ResponseCode", ErrorCode.ERR_660019.toString());
									result.addParam("Message", ErrorCode.ERR_660019.getErrorMessage());
									return result;
								case 57:
									if (!str.equals("9"))
										break;
									result.addParam("ResponseCode", ErrorCode.ERR_660019.toString());
									result.addParam("Message", ErrorCode.ERR_660019.getErrorMessage());
									return result;
								case 1567:
									if (!str.equals("10"))
										break;
									result.addParam("ResponseCode", ErrorCode.ERR_660021.toString());
									result.addParam("Message", ErrorCode.ERR_660021.getErrorMessage());
									return result;
								}
								result.addParam("ResponseCode", ErrorCode.ERR_660021.toString());
								result.addParam("Message", ErrorCode.ERR_660021.getErrorMessage());
							} else if (jobj.optString("customerStatus").equals("SID_CANCELLED")) {
								result.addParam("ResponseCode", ErrorCode.ERR_660022.toString());
								result.addParam("Message", ErrorCode.ERR_660022.getErrorMessage());
							} else if (jobj.optString("customerStatus").equals("SID_CUS_ACTIVE")) {
								result.addParam("ResponseCode", ErrorCode.ERR_60000.toString());
								result.addParam("Message", ErrorCode.ERR_60000.getErrorMessage());
							} else if (jobj.optString("customerStatus").equals("SID_CUS_SUSPENDED")) {
								result.addParam("ResponseCode", ErrorCode.ERR_660023.toString());
								result.addParam("Message", ErrorCode.ERR_660023.getErrorMessage());
							}
						} else if (jobj.has("isCitizenExc") && jobj.optString("isCitizenExc").equals("true")) {
							result.addParam("ResponseCode", ErrorCode.ERR_660021.toString());
							result.addParam("Message", String.valueOf(ErrorCode.ERR_660021.getErrorMessage()) + " "
									+ jobj.optString("message"));
						}
					}
				} catch (Exception e) {
					result.addParam("ResponseCode", ErrorCode.ERR_660021.toString());
				}
			} else {
				result.addParam("ResponseCode", ErrorCode.ERR_66004.toString());
				String msg = "";
				if (JresponseEnroll.optString("ResponseCode") == "20")
					msg = ErrorCode.ERR_660018.getErrorMessage();
				if (JresponseEnroll.optString("ResponseCode") == "10")
					msg = "Record not inserted";
				if (JresponseEnroll.optString("ResponseCode") == "-11")
					msg = ErrorCode.ERR_660014.getErrorMessage();
				result.addParam("Error", " " + msg);
			}
		}
		return result;
	}

	private JSONObject verifyMobileOwner(DataControllerRequest request, DataControllerResponse response) {
		String res = "";
		JSONObject JsonResponse = null;
		JSONObject JsonIdentity = null;
		boolean flag = false;
		HashMap<String, Object> imap = new HashMap<>();
		imap.put("id", request.getParameter("NationalID"));
		imap.put("mobileNumber", request.getParameter("Mobile"));
		HashMap<String, Object> imapHeader = new HashMap<>();
		imapHeader.put("app-id", "c445edda");
		imapHeader.put("app-key", "3a171d308e025b6d7a46e93ad7b0bbb3");
		imapHeader.put("SERVICE_KEY", "9f5786c8-640c-4390-bde3-b952ef397145");
		imapHeader.put("content-type", "application/json");
		try {
			res = DBPServiceExecutorBuilder.builder().withServiceId("MobileOwnerVerificationAPI")
					.withOperationId("VerifyMobileNumber").withRequestParameters(imap).withRequestHeaders(imapHeader)
					.build().getResponse();
			String apiHost = "MobileOwnerVerification";
			String requestJson = (new ObjectMapper()).writeValueAsString(imapHeader);
			if (res != null)
				try {
					auditLogData(request, response, requestJson, res, apiHost);
				} catch (DBPApplicationException | MiddlewareException e) {
					e.printStackTrace();
				}
			JsonResponse = new JSONObject(res);
			if (JsonResponse.has("isOwner")) {
				if (JsonResponse.getBoolean("isOwner")) {
					if (request.getParameter("NationalID").startsWith("1")) {
						JsonIdentity = callYakeenCitizen(request, response);
					} else if (request.getParameter("NationalID").startsWith("2")) {
						JsonIdentity = callYakeenAlien(request, response);
					}
				} else {
					flag = false;
					JsonIdentity = new JSONObject();
					JsonIdentity.put("isOwnerVerify", "false");
				}
			} else if (JsonResponse.has("errmsg")) {
				JsonIdentity = new JSONObject();
				JsonIdentity.put("isOwnerVerifyError", "true");
				JsonIdentity.put("OvEmessage", JsonResponse.optString("message").toString());
			}
		} catch (Exception e) {
			JsonIdentity = new JSONObject();
			JsonIdentity.put("isOwnerVerifyError", "true");
			JsonIdentity.put("OvEmessage", e.getMessage());
		}
		return JsonIdentity;
	}

	private JSONObject callYakeenCitizen(DataControllerRequest request, DataControllerResponse response) {
		boolean flag = false;
		String res = "";
		String operation = "";
		String errorCode = "";
		JSONObject JsonResponse = null;
		JSONObject JsonResult = new JSONObject();
		JSONObject JsonCitizenError = new JSONObject();
		if (request.getParameter("ChargeCode").toString().toUpperCase().equals("PROD")) {
			operation = "getCitizenInfoOrch";
		} else {
			operation = "getCitizenInfoOrchMock";
		}
		HashMap<String, Object> imap = new HashMap<>();
		imap.put("nin", request.getParameter("NationalID"));
		imap.put("dateOfBirth", request.getParameter("DOB"));
		imap.put("referenceNumber", "");
		imap.put("password", "Ejarah@76511");
		imap.put("userName", "USR_Ejarah_PROD");
		imap.put("chargeCode", "PROD");
		imap.put("Mobile", request.getParameter("Mobile"));
		try {
			res = DBPServiceExecutorBuilder.builder().withServiceId("YakeenOrch").withOperationId(operation)
					.withRequestParameters(imap).build().getResponse();
			String apiHost = "YAKEEN_ORCH";
			String requestJson = (new ObjectMapper()).writeValueAsString(imap);
			if (res != null)
				try {
					auditLogData(request, response, requestJson, res, apiHost);
				} catch (DBPApplicationException | MiddlewareException e) {
					e.printStackTrace();
				}
			JsonResponse = new JSONObject(res);
			if (JsonResponse.has("faultdetail")) {
				JsonCitizenError = Utilities.xmlParser(JsonResponse.optString("faultdetail"));
				errorCode = JsonCitizenError.getJSONObject("commonErrorObject").optString("ErrorCode").toString();
				JsonResult.put("isCitizenSuccess", "false");
				JsonResult.put("errorCode", errorCode);
				updateCitizenVerifyStatus(request.getParameter("Mobile"), "Failed");
			} else {
				HashMap<String, Object> imapDb = new HashMap<>();
				imapDb.put("NationalID", request.getParameter("NationalID"));
				imapDb.put("Mobile", request.getParameter("Mobile"));
				imapDb.put("IBAN", request.getParameter("IBAN"));
				String resDB = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices")
						.withOperationId("dbxdb_updatecustomer_citizeninfo").withRequestParameters(imapDb).build()
						.getResponse();
				JsonResponse = new JSONObject(resDB);
				if (JsonResponse.getJSONArray("records").length() > 0) {
					String saveCitizenRes = JsonResponse.getJSONArray("records").getJSONObject(0)
							.optString("Status_id");
					JsonResult.put("customerStatus", saveCitizenRes);
					JsonResult.put("isCitizenSuccess", "true");
					updateCitizenVerifyStatus(request.getParameter("Mobile"), "Passed");
				}
			}
		} catch (Exception e) {
			JsonResult.put("message", e.getMessage());
			JsonResult.put("isCitizenExc", "true");
		}
		return JsonResult;
	}

	private JSONObject callYakeenCitizenForCore(DataControllerRequest request, DataControllerResponse response)
			throws JsonProcessingException {
		boolean flag = false;
		String res = "";
		String operation = "";
		String errorCode = "";
		JSONObject JsonResponse = null;
		JSONObject JsonResult = new JSONObject();
		JSONObject JsonCitizenError = new JSONObject();
		HashMap<String, Object> imap = new HashMap<>();
		imap.put("nin", request.getParameter("NationalID"));
		imap.put("dateOfBirth", request.getParameter("DOB"));
		imap.put("referenceNumber", "");
		imap.put("password", "Ejarah@76511");
		imap.put("userName", "USR_Ejarah_PROD");
		imap.put("chargeCode", "PROD");
		imap.put("Mobile", request.getParameter("Mobile"));
		try {
			res = DBPServiceExecutorBuilder.builder().withServiceId("YakeenSoapAPI").withOperationId("getCitizenInfo")
					.withRequestParameters(imap).build().getResponse();
			String apiHost = "YAKEEN_CITIZEN";
			String requestJson = (new ObjectMapper()).writeValueAsString(imap);
			if (res != null)
				try {
					auditLogData(request, response, requestJson, res, apiHost);
				} catch (DBPApplicationException | MiddlewareException e) {
					e.printStackTrace();
				}
			JsonResponse = new JSONObject(res);
		} catch (DBPApplicationException e) {
			e.printStackTrace();
		}
		return JsonResponse;
	}

	private JSONObject callYakeenAlien(DataControllerRequest request, DataControllerResponse response) {
		boolean flag = false;
		String res = "";
		String operation = "";
		String errorCode = "";
		JSONObject JsonResponse = null;
		JSONObject JsonResult = new JSONObject();
		JSONObject JsonCitizenError = new JSONObject();
		if (request.getParameter("ChargeCode").toString().toUpperCase().equals("PROD")) {
			operation = "getAlienInfoByIqama";
		} else {
			operation = "getAlienInfoByIqamaMock";
		}
		HashMap<String, Object> imap = new HashMap<>();
		imap.put("iqamaNumber", request.getParameter("NationalID"));
		imap.put("dateOfBirth", request.getParameter("DOB"));
		imap.put("referenceNumber", "");
		imap.put("password", "Ejarah@76511");
		imap.put("userName", "USR_Ejarah_PROD");
		imap.put("chargeCode", "PROD");
		imap.put("Mobile", request.getParameter("Mobile"));
		try {
			res = DBPServiceExecutorBuilder.builder().withServiceId("YakeenOrch").withOperationId(operation)
					.withRequestParameters(imap).build().getResponse();
			String apiHost = "YAKEEN_API";
			String requestJson = (new ObjectMapper()).writeValueAsString(imap);
			if (res != null)
				try {
					auditLogData(request, response, requestJson, res, apiHost);
				} catch (DBPApplicationException | MiddlewareException e) {
					e.printStackTrace();
				}
			JsonResponse = new JSONObject(res);
			if (JsonResponse.has("faultdetail")) {
				JsonCitizenError = Utilities.xmltojson(JsonResponse.optString("faultdetail"));
				errorCode = JsonCitizenError.getJSONObject("Yakeen4EjarahFault").getJSONObject("commonErrorObject")
						.getJSONObject("ErrorCode").toString();
				JsonResult.put("isCitizenSuccess", "false");
				JsonResult.put("errorCode", errorCode);
				updateIqamaVerifyStatus(request.getParameter("Mobile"), "Failed");
			} else {
				HashMap<String, Object> imapDb = new HashMap<>();
				imapDb.put("NationalID", request.getParameter("NationalID"));
				imapDb.put("Mobile", request.getParameter("Mobile"));
				imapDb.put("IBAN", request.getParameter("IBAN"));
				imapDb.put("LoanPurpose", request.getParameter("LoanPurpose"));
				String resDB = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices")
						.withOperationId("dbxdb_updatecustomer_iqamainfo").withRequestParameters(imapDb).build()
						.getResponse();
				JsonResponse = new JSONObject(resDB);
				if (JsonResponse.getJSONArray("records").length() > 0) {
					String saveCitizenRes = JsonResponse.getJSONArray("records").getJSONObject(0)
							.optString("Status_id");
					JsonResult.put("customerStatus", saveCitizenRes);
					JsonResult.put("isCitizenSuccess", "true");
					updateIqamaVerifyStatus(request.getParameter("Mobile"), "Passed");
					JsonResponse = new JSONObject();
					JsonResult = callYakeenAlienStatus(request, response);
				}
			}
		} catch (Exception e) {
			JsonResult.put("message", e.getMessage());
			JsonResult.put("isCitizenExc", "true");
		}
		return JsonResult;
	}

	private JSONObject callYakeenAlienForCore(DataControllerRequest request, DataControllerResponse response)
			throws JsonProcessingException {
		boolean flag = false;
		String res = "";
		String operation = "";
		String errorCode = "";
		JSONObject JsonResponse = null;
		HashMap<String, Object> imap = new HashMap<>();
		imap.put("iqamaNumber", request.getParameter("NationalID"));
		imap.put("dateOfBirth", request.getParameter("DOB"));
		imap.put("referenceNumber", "");
		imap.put("password", "Ejarah@76511");
		imap.put("userName", "USR_Ejarah_PROD");
		imap.put("chargeCode", "PROD");
		imap.put("Mobile", request.getParameter("Mobile"));
		try {
			res = DBPServiceExecutorBuilder.builder().withServiceId("YakeenSoapAPI")
					.withOperationId("getAlienInfoByIqama").withRequestParameters(imap).build().getResponse();
			String apiHost = "YAKEEN_ALIEN";
			String requestJson = (new ObjectMapper()).writeValueAsString(imap);
			if (res != null)
				try {
					auditLogData(request, response, requestJson, res, apiHost);
				} catch (DBPApplicationException | MiddlewareException e) {
					e.printStackTrace();
				}
			JsonResponse = new JSONObject(res);
		} catch (DBPApplicationException e) {
			e.printStackTrace();
		}
		return JsonResponse;
	}

	private JSONObject callYakeenAddressForCore(DataControllerRequest request, DataControllerResponse response)
			throws JsonProcessingException {
		boolean flag = false;
		String res = "";
		String operation = "";
		String errorCode = "";
		JSONObject JsonResponse = null;
		HashMap<String, Object> imap = new HashMap<>();
		imap.put("iqamaNumber", request.getParameter("NationalID"));
		imap.put("dateOfBirth", request.getParameter("DOB"));
		imap.put("referenceNumber", "");
		imap.put("password", "Ejarah@76511");
		imap.put("userName", "USR_Ejarah_PROD");
		imap.put("chargeCode", "PROD");
		imap.put("Mobile", request.getParameter("Mobile"));
		try {
			res = DBPServiceExecutorBuilder.builder().withServiceId("YakeenSoapAPI")
					.withOperationId("getCitizenAddressInfo").withRequestParameters(imap).build().getResponse();
			String apiHost = "YAKEEN_ADDY";
			String requestJson = (new ObjectMapper()).writeValueAsString(imap);
			if (res != null)
				try {
					auditLogData(request, response, requestJson, res, apiHost);
				} catch (DBPApplicationException | MiddlewareException e) {
					e.printStackTrace();
				}
			JsonResponse = new JSONObject(res);
		} catch (DBPApplicationException e) {
			e.printStackTrace();
		}
		return JsonResponse;
	}

	private JSONObject callYakeenTwoForCore(DataControllerRequest request, DataControllerResponse response)
			throws JsonProcessingException {
		boolean flag = false;
		String res = "";
		String operation = "";
		String errorCode = "";
		JSONObject JsonResponse = null;
		HashMap<String, Object> imap = new HashMap<>();
		imap.put("nin", request.getParameter("NationalID"));
		imap.put("dateOfBirth", request.getParameter("DOB"));
		imap.put("referenceNumber", "");
		imap.put("password", "Ejarah@76511");
		imap.put("userName", "USR_Ejarah_PROD");
		imap.put("chargeCode", "PROD");
		try {
			res = DBPServiceExecutorBuilder.builder().withServiceId("YakeenSoapApiTwo")
					.withOperationId("getCitizenInfoTwo").withRequestParameters(imap).build().getResponse();
			String apiHost = "YAKEEN_TWO";
			String requestJson = (new ObjectMapper()).writeValueAsString(imap);
			if (res != null)
				try {
					auditLogData(request, response, requestJson, res, apiHost);
				} catch (DBPApplicationException | MiddlewareException e) {
					e.printStackTrace();
				}
			JsonResponse = new JSONObject(res);
		} catch (DBPApplicationException e) {
			e.printStackTrace();
		}
		return JsonResponse;
	}

	private JSONObject callYakeenAlienStatus(DataControllerRequest request, DataControllerResponse response) {
		boolean flag = false;
		String res = "";
		String operation = "";
		String errorCode = "";
		JSONObject JsonResponse = null;
		JSONObject JsonResult = new JSONObject();
		JSONObject JsonCitizenError = new JSONObject();
		if (request.getParameter("ChargeCode").toString().toUpperCase().equals("PROD")) {
			operation = "getAlienInfoByIqamaAndStatusOrch";
		} else {
			operation = "getAlienInfoByIqamaAndStatusOrchMock";
		}
		HashMap<String, Object> imap = new HashMap<>();
		imap.put("iqamaNumber", request.getParameter("NationalID"));
		imap.put("dateOfBirth", request.getParameter("DOB"));
		imap.put("referenceNumber", "");
		imap.put("password", "Ejarah@76511");
		imap.put("userName", "USR_Ejarah_PROD");
		imap.put("chargeCode", "PROD");
		imap.put("Mobile", request.getParameter("Mobile"));
		try {
			res = DBPServiceExecutorBuilder.builder().withServiceId("YakeenOrch").withOperationId(operation)
					.withRequestParameters(imap).build().getResponse();
			String apiHost = "YAKEEN_ALIEN";
			String requestJson = (new ObjectMapper()).writeValueAsString(imap);
			if (res != null)
				try {
					auditLogData(request, response, requestJson, res, apiHost);
				} catch (DBPApplicationException | MiddlewareException e) {
					e.printStackTrace();
				}
			JsonResponse = new JSONObject(res);
			if (JsonResponse.has("faultdetail")) {
				JsonCitizenError = Utilities.xmltojson(JsonResponse.optString("faultdetail"));
				errorCode = JsonCitizenError.getJSONObject("Yakeen4EjarahFault").getJSONObject("commonErrorObject")
						.getJSONObject("ErrorCode").toString();
				JsonResult.put("isCitizenSuccess", "false");
				JsonResult.put("errorCode", errorCode);
				updateIqamaStVerifyStatus(request.getParameter("Mobile"), "Failed");
			} else {
				HashMap<String, Object> imapDb = new HashMap<>();
				imapDb.put("NationalID", request.getParameter("NationalID"));
				imapDb.put("Mobile", request.getParameter("Mobile"));
				imapDb.put("IBAN", request.getParameter("IBAN"));
				String resDB = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices")
						.withOperationId("dbxdb_updatecustomer_iqamastatus").withRequestParameters(imapDb).build()
						.getResponse();
				JsonResponse = new JSONObject(resDB);
				if (JsonResponse.getJSONArray("records").length() > 0) {
					String saveCitizenRes = JsonResponse.getJSONArray("records").getJSONObject(0)
							.optString("Status_id");
					JsonResult.put("customerStatus", saveCitizenRes);
					JsonResult.put("isCitizenSuccess", "true");
					updateIqamaStVerifyStatus(request.getParameter("Mobile"), "Passed");
				}
			}
		} catch (Exception e) {
			JsonResult.put("message", e.getMessage());
			JsonResult.put("isCitizenExc", "true");
		}
		return JsonResult;
	}

	private void updateMobileVerifyStatus(String mobileNumber, String status) {
		String res = "";
		HashMap<String, Object> imap = new HashMap<>();
		imap.put("Mobile", mobileNumber);
		imap.put("ResponseStatus", status);
		try {
			res = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices")
					.withOperationId("dbxdb_updatecus_mobverify").withRequestParameters(imap).build().getResponse();
		} catch (Exception e) {
			String errorMsg = "Error in NationalIdVerification, updateMobileVerifyStatus : " + e.toString();
			logger.error(errorMsg);
		}
	}

	private void updateCitizenVerifyStatus(String mobileNumber, String status) {
		String res = "";
		HashMap<String, Object> imap = new HashMap<>();
		imap.put("Mobile", mobileNumber);
		imap.put("ResponseStatus", status);
		try {
			res = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices")
					.withOperationId("dbxdb_updatecus_citizenverify").withRequestParameters(imap).build().getResponse();
		} catch (Exception e) {
			String errorMsg = "Error in NationalIdVerification, updateCitizenVerifyStatus : " + e.toString();
			logger.error(errorMsg);
		}
	}

	private void updateIqamaVerifyStatus(String mobileNumber, String status) {
		String res = "";
		HashMap<String, Object> imap = new HashMap<>();
		imap.put("Mobile", mobileNumber);
		imap.put("ResponseStatus", status);
		try {
			res = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices")
					.withOperationId("dbxdb_updatecus_iqamaverify").withRequestParameters(imap).build().getResponse();
		} catch (Exception e) {
			String errorMsg = "Error in NationalIdVerification, updateIqamaVerifyStatus : " + e.toString();
			logger.error(errorMsg);
		}
	}

	private void updateIqamaStVerifyStatus(String mobileNumber, String status) {
		String res = "";
		HashMap<String, Object> imap = new HashMap<>();
		imap.put("Mobile", mobileNumber);
		imap.put("ResponseStatus", status);
		try {
			res = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices")
					.withOperationId("dbxdb_updatecus_iqamaverifystatus").withRequestParameters(imap).build()
					.getResponse();
		} catch (Exception e) {
			String errorMsg = "Error in NationalIdVerification, updateIqamaStVerifyStatus : " + e.toString();
			logger.error(errorMsg);
		}
	}

	private void createCitizenVerifyStatus(String mobileNumber, String nationalID) {
		String res = "";
		HashMap<String, Object> imap = new HashMap<>();
		imap.put("Mobile", mobileNumber);
		imap.put("IqamaNumber", nationalID);
		try {
			res = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices")
					.withOperationId("dbxdb_customerverify_status_create").withRequestParameters(imap).build()
					.getResponse();
		} catch (Exception e) {
			String errorMsg = "Error in NationalIdVerification, createCitizenVerifyStatus : " + e.toString();
			logger.error(errorMsg);
		}
	}

	private JSONObject enrollCustomer(DataControllerRequest request, DataControllerResponse response) throws Exception {
		boolean istemp = false;
		String tempRegID = "";
		String nationalID = request.getParameter("NationalID");
		Result result = new Result();
		JSONObject JsonResponse = null;
		JSONObject jsonSend = null;
		String res = "";
		Map<String, String> customerResponse = new HashMap<>();
		try {
			HashMap<String, Object> imap = new HashMap<>();
			imap.put("Mobile", request.getParameter("Mobile"));
			OperationData serviceData = request.getServicesManager().getOperationDataBuilder()
					.withServiceId("DBXDBServices").withOperationId("dbxdb_customer_get").build();
			res = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices")
					.withOperationId("dbxdb_gettempRegisterUser").withRequestParameters(imap).build().getResponse();
			JsonResponse = new JSONObject(res);
			if (JsonResponse.getJSONArray("records").length() > 0) {
				Random rand = new Random();
				int upperbound = 99999;
				int int_random = rand.nextInt(upperbound);
				JSONArray records = JsonResponse.getJSONArray("records");
				JSONObject jObj = records.getJSONObject(0);
				HashMap<String, Object> imapReq = new HashMap<>();
				imapReq.put("Stan", Integer.valueOf(int_random));
				imapReq.put("UserName", nationalID);
				imapReq.put("Password", jObj.optString("Password"));
				imapReq.put("CreatedBy", "SYSTEM");
				imapReq.put("ModifiedBy", "SYSTEM");
				imapReq.put("FirstName", "");
				imapReq.put("LastName", "");
				imapReq.put("MiddleName", "");
				imapReq.put("DateOfBirth", jObj.optString("DateOfBirth"));
				imapReq.put("Ssn", nationalID);
				imapReq.put("CustomerType_id", "TYPE_ID_PROSPECT");
				imapReq.put("coreCustomerId", "000");
				imapReq.put("Type_id", "PHONE,EMAIL");
				imapReq.put("Value", String.valueOf(request.getParameter("Mobile")) + "," + jObj.optString("Email"));
				tempRegID = jObj.optString("id");
				String createRes = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices")
						.withOperationId("dbxdb_sp_createcustomer").withRequestParameters(imapReq).build()
						.getResponse();
				JSONObject JsonResponseCreateCus = new JSONObject(createRes);
				if (JsonResponseCreateCus.getJSONArray("records").length() > 0) {
					logger.error("Customer stored procedure executed ===>");
					String cusId = JsonResponseCreateCus.getJSONArray("records").getJSONObject(0)
							.optString("@MaxCustomerId");
					try {
						if (createCoreProspect(request, response, cusId)) {
							result = ErrorCode.ERR_660078.updateResultObject(result);
							logger.error("PRospect customer created 24334=====>");
						} else {
							logger.error("Prospect create failed");
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					jsonSend = new JSONObject();
					jsonSend.put("ResponseCode", "00");
				
					HashMap<String, Object> imapdltReq = new HashMap<>();
					imapdltReq.put("UserName", request.getParameter("Mobile"));
					try {
						String str1 = DBPServiceExecutorBuilder.builder().withServiceId("DBXDBServices")
								.withOperationId("dbxdb_sp_delete_tempregistration").withRequestParameters(imapdltReq)
								.build().getResponse();
					}catch(Exception e) {
						logger.error("Exception dbxdb_sp_delete_tempregistration = " + e.getMessage());
					}
					
					logger.error("Check jsonSend = " + jsonSend);
				} else {
					jsonSend = new JSONObject();
					jsonSend.put("ResponseCode", "10");
				}
			} else {
				jsonSend = new JSONObject();
				jsonSend.put("ResponseCode", "20");
			}
		} catch (Exception e) {
			jsonSend = new JSONObject();
			jsonSend.put("ResponseCode", "-11");
		}
		return jsonSend;
	}

	public boolean createCoreProspect(DataControllerRequest request, DataControllerResponse response, String cusID) {
		boolean checkStatus = false;

		try {
			HashMap<String, Object> prospectParam = new HashMap<>();
			String res = null;
			String prosRes = null;
			JSONObject jsonResponse = null;
			JSONObject jsonResponseTwo = null;
			if (request.getParameter("NationalID").startsWith("1")) {
				jsonResponse = callYakeenCitizenForCore(request, response);
				jsonResponseTwo = callYakeenTwoForCore(request, response);
				logger.error("calling yakeenforcore");
			} else if (request.getParameter("NationalID").startsWith("2")) {
				jsonResponse = callYakeenAlienForCore(request, response);
			}
			if (jsonResponse != null) {
				logger.error("Calling prospect customer = " + jsonResponse);
				logger.error("Calling prospect customer jsonResponseTwo = " + jsonResponseTwo);
				String dateH = jsonResponse.optString("dateOfBirthG");
				String dateHij = jsonResponse.optString("dateOfBirthH");
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
				DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		//		DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("yyyymmdd");
		//		dateHij = LocalDate.parse(dateHij, formatter).format(formatter3);
				prospectParam.put("dateOfBirth", LocalDate.parse(dateH, formatter).format(formatter2));
				prospectParam.put("displayName", jsonResponse.optString("englishFirstName"));
				prospectParam.put("customerName", String.valueOf(jsonResponse.optString("englishFirstName")) + " "
						+ jsonResponse.optString("englishLastName"));
				prospectParam.put("phoneNumber", request.getParameter("Mobile"));
				prospectParam.put("customerMnemonic", getMnemonic());
				prospectParam.put("gender", getGenderName(jsonResponseTwo.optString("gender")));
				prospectParam.put("title", getSalutation(jsonResponseTwo.optString("gender")));
				prospectParam.put("maritalStatus", getMaritalStatus(jsonResponseTwo.optString("socialStatusCode")));
				prospectParam.put("lastName", jsonResponse.optString("englishLastName"));
				prospectParam.put("givenName", jsonResponse.optString("englishFirstName"));
				prospectParam.put("street", "");
				prospectParam.put("address", "");
				logger.error("dateHijMfb", dateFormatter(jsonResponse.optString("dateOfBirthH")));
				logger.error("dateMfb", dateFormatter(jsonResponse.optString("dateOfBirthG")));
				prospectParam.put("dateHijMfb", dateFormatter(jsonResponse.optString("dateOfBirthH")));
				prospectParam.put("dateMfb", dateFormatter(jsonResponse.optString("dateOfBirthG")));
				prospectParam.put("firstNmEnMfb", jsonResponse.optString("englishFirstName"));
				prospectParam.put("secNmEnMfb", jsonResponse.optString("englishSecondName"));
				prospectParam.put("thrdNmEnMfb", jsonResponse.optString("englishThirdName"));
				prospectParam.put("lastNmEnMfb", jsonResponse.optString("englishLastName"));
				prospectParam.put("firstNmArMfb", jsonResponse.optString("firstName"));
				prospectParam.put("fathNmArMfb", jsonResponse.optString("fatherName"));
				prospectParam.put("grndNmArMfb", jsonResponse.optString("grandFatherName"));
				prospectParam.put("lastNmArMfb", jsonResponse.optString("familyName"));
				prospectParam.put("noOfVehclMfb", jsonResponse.optString("totalVehicles"));
				prospectParam.put("idExpiryMfb", jsonResponse.optString("idExpiryDate"));
				prospectParam.put("maritalStatus", getMaritalStatus(jsonResponseTwo.optString("socialStatusCode")));
				logger.error("input params for prospect customer", prospectParam.toString());
				prosRes = DBPServiceExecutorBuilder.builder().withServiceId("MoraT24Service")
						.withOperationId("ProspectCustomer").withRequestParameters(prospectParam).build().getResponse();
				JSONObject JsonResponse = new JSONObject(prosRes);
				if (JsonResponse.optString("status").equalsIgnoreCase("success")) {
					String partyId = JsonResponse.optString("id");
					logger.error("T24 prospect created");
					HashMap<String, Object> cusUpdate = new HashMap<>();
					cusUpdate.put("id", cusID);
					String firstName = jsonResponse.optString("englishFirstName");
					cusUpdate.put("FirstName", firstName);
					String secondName = jsonResponse.optString("englishSecondName");
					cusUpdate.put("MiddleName", secondName);
					String lastName = jsonResponse.optString("englishLastName");
					cusUpdate.put("LastName", lastName);
					String englishFullName = jsonResponse.optString("englishFirstName") + " "
							+ jsonResponse.optString("englishSecondName") + " "
							+ jsonResponse.optString("englishLastName");
					String arabicFullName = jsonResponse.optString("firstName") + " "
							+ jsonResponse.optString("fatherName") + " " + jsonResponse.optString("grandFatherName")
							+ " " + jsonResponse.optString("familyName");
					cusUpdate.put("DateOfBirth", jsonResponse.optString("dateOfBirthG"));
					cusUpdate.put("Gender", jsonResponseTwo.optString("gender"));
					cusUpdate.put("IDExpiryDate", jsonResponseTwo.optString("idExpiryDate"));
					cusUpdate.put("FullName", englishFullName);
					cusUpdate.put("ArFullName", arabicFullName);
					cusUpdate.put("partyId", partyId);
					HashMap<String, Object> tempupdate = new HashMap<>();
					String requestJson = (new ObjectMapper()).writeValueAsString(prospectParam);
					tempupdate.put("partyId", partyId);
					res = DBPServiceExecutorBuilder.builder().withServiceId("DBMoraServices")
							.withOperationId("customerProfileUpdate").withRequestParameters(cusUpdate).build()
							.getResponse();
					if (res != null) {
						logger.error("customer profiled updated");
						// return true;
						checkStatus = true;
					}
				} else if (JsonResponse.optString("status").equalsIgnoreCase("failure")) {
					logger.error("T24 prospect not created failure");
					// return false;
					checkStatus = false;
				} else {
					logger.error("T24 prospect not created enter ine else");
				}
			}

		} catch (Exception e) {
			logger.error("Exception in create prospect = " + e.getMessage());
		}
		return checkStatus;
	}

	private String dateFormatter(String dateN) {
		String convertdate = "";

		String dtformat = "dd-MM-yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(dtformat);
		try {

			Date dt = sdf.parse(dateN);
			SimpleDateFormat sdf2 = new SimpleDateFormat("ddMMyyyy");
			convertdate = sdf2.format(dt);
		} catch (Exception e) {
			logger.error("Date formatter in exceptin = " + e.getMessage());
		}
		logger.error("dateFormatter = " + convertdate);
		return convertdate;
	}

	private String getMaritalStatus(String socCode) {
		if (socCode.equals("0"))
			return "SINGLE";
		return "MARRIED";
	}

	private String getGenderName(String gender) {
		if (gender.equals("M"))
			return "MALE";
		return "FEMALE";
	}

	private String getSalutation(String gender) {
		if (gender.equals("M"))
			return "MR";
		return "MRS";
	}

	public String getMnemonic() {
		String mnemonic = "";
		Random random = new Random();
		int num = random.nextInt(100000);
		String formatted = String.format("%05d", new Object[] { Integer.valueOf(num) });
		mnemonic = "C" + formatted;
		return mnemonic;
	}

	public boolean updateCustomer(DataControllerRequest request, DataControllerResponse response) {
		return false;
	}

	public boolean auditLogData(DataControllerRequest request, DataControllerResponse response, String req, String res,
			String apiHost) throws DBPApplicationException, MiddlewareException {
		UUID uuid = UUID.randomUUID();
		String uuidAsString = uuid.toString();
		String cusId = request.getParameter("NationalID");
		String logResponse = null;
		String channelDevice = "Mobile";
		String ipAddress = request.getRemoteAddr();
		HashMap<String, Object> logdataRequestMap = new HashMap<>();
		logdataRequestMap.put("id", uuidAsString);
		logdataRequestMap.put("Customer_id", cusId);
		logdataRequestMap.put("Application_id", "");
		logdataRequestMap.put("channelDevice", channelDevice);
		logdataRequestMap.put("apihost", apiHost);
		logdataRequestMap.put("request_payload", req);
		logdataRequestMap.put("reponse_payload", res);
		logdataRequestMap.put("ipAddress", ipAddress);
		logResponse = DBPServiceExecutorBuilder.builder().withServiceId("DBMoraServices")
				.withOperationId("dbxlogs_auditlog_create").withRequestParameters(logdataRequestMap).build()
				.getResponse();
		if (logResponse != null && logResponse.length() > 0)
			return true;
		return false;
	}
}
