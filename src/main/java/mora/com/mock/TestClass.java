package mora.com.mock;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class TestClass {

	static String[] NON_FINANCIAL_PRODUCTS = new String[] { "MBL", "LND", "DAT", "NET" };

	public static void main(String[] args) throws Exception {
		
		String test = "نشيط";
		

		if (test.equals("نشيط"))
			System.out.println("Yes");
		else
			System.out.println("No");
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public static void readXmlFile(String extension, String path) {
		File file = new File(path + extension);

		try {
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			if (doc.hasChildNodes()) {
				JSONArray ddd = printNote_1(doc.getChildNodes());
				System.out.println("ddd ::::: " + ddd);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		// JSONObject convertedObj = XML.toJSONObject(builder.toString());

		// return convertedObj;

	}

	private static JSONArray printNote_1(NodeList nodeList) {
		JSONArray dataArr = new JSONArray();
		JSONObject dataObject = new JSONObject();
		for (int count = 0; count < nodeList.getLength(); count++) {
			Node tempNode = nodeList.item(count);
			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
				if (tempNode.hasChildNodes() && tempNode.getChildNodes().getLength() > 1) {
					JSONArray temArr = printNote_1(tempNode.getChildNodes());
					if (dataObject.has(tempNode.getNodeName())) {
						dataObject.getJSONArray(tempNode.getNodeName()).put(temArr.getJSONObject(0));
					} else {
						dataObject.put(tempNode.getNodeName(), temArr);
					}
				} else {
					dataObject.put(tempNode.getNodeName(), tempNode.getTextContent());
				}
			}
		}
		dataArr.put(dataObject);
		return dataArr;
	}

	public static String convertString(String data) {

		data = data.replaceAll("-", "");

		StringBuilder sb = new StringBuilder(data);
		String returnStr = sb.reverse().toString();
		return returnStr;

	}

	public static void convertIntoJSONObject(String extension, String path) {
		File file = new File(path + extension);
		XmlMapper xmlMapper = new XmlMapper();

		try {
			String xml = FileUtils.readFileToString(file, Charset.defaultCharset());
			JsonNode node = xmlMapper.readTree(xml.getBytes());

			ObjectMapper jsonMapper = new ObjectMapper();

			String json = jsonMapper.writeValueAsString(node);

			System.out.println(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
