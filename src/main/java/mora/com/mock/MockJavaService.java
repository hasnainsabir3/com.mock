package mora.com.mock;

import java.util.ArrayList;

import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

public class MockJavaService implements JavaService2 {
	private static final Logger logger = LogManager.getLogger(MockJavaService.class);

	@Override
	public Object invoke(String arg0, Object[] arg1, DataControllerRequest request, DataControllerResponse arg3)
			throws Exception {


		List<NameValuePair> urlParameters = new ArrayList<>();
		urlParameters.add(new BasicNameValuePair("grant_type", "client_credentials"));
		urlParameters.add(new BasicNameValuePair("scope", "read write"));

		HttpResponse response = postWithFormData("https://sandbox.nafith.sa/api/oauth/token/", urlParameters);
		HttpEntity entity = response.getEntity();
		String responseString = EntityUtils.toString(entity);
		JSONObject responseObject = new JSONObject(responseString);

		logger.error("MockJavaService  responseObject= " + responseObject);

		Result result = new Result();

		result = JSONToResult.convert(responseString);
		return result;
	}

	private static RequestConfig requestConfig = RequestConfig.custom().build();

	public HttpResponse postWithFormData(String url, List<NameValuePair> params) {
		try {
			logger.error("MockJavaService  postWithFormData enter ");
			
			HttpClient httpClient = HttpClients
		            .custom()
		            .setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build())
		            .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
		            .build();

			HttpPost request = new HttpPost(url);

			request.setHeader("Content-Type", "application/x-www-form-urlencoded");
			request.setHeader("Authorization",
					"Basic  TFd2WEhFQjFvbGZPeDBJOGVOWElrQlNRSFI2bDBRQmlTR1hvdWVmYjpOSXZxYU92S0RVMFE4NkdnMG1jVEFlQmNPRThRSFlNREVWOHBzbzA0dWpiZVA4bEszTWpoaDM3VVpjQ0lVc0dNbUdxZW51NzJJVGpHU0xncnVuV1A4OGxvclZ0Y1plZE5aZVVYbHJ5Mll1SlF2ekt6ZW5malVIdmN5UjlvYjJXeA==");
			// adding the form data
			request.setEntity(new UrlEncodedFormEntity(params));
			return httpClient.execute(request);
		} catch (Exception e) {
			logger.error("MockJavaService  postWithFormData exception= " + e.getMessage());
			return null;
		}

	}

}
