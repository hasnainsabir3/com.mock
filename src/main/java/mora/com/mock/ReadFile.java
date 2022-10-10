package mora.com.mock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ReadFile {
	private static final Logger logger = LogManager.getLogger(ReadFile.class);

	public static JSONObject readXmlFile(String extension, String path) {

		File file = new File(path + extension);

		StringBuilder builder = new StringBuilder();
		try (BufferedReader red = new BufferedReader(new FileReader(file));) {
			String out;
			while ((out = red.readLine()) != null) {
				builder.append(out);
			}

			red.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		JSONObject convertedObj = XML.toJSONObject(builder.toString());

		return convertedObj;
	}

	public static JSONObject readJSONFile(String extension, String path) throws Exception {

		File file = new File(path + extension);

		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		String ls = System.getProperty("line.separator");
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(ls);
		}
		// delete the last new line separator
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		reader.close();

		JSONObject convertedObj = new JSONObject(stringBuilder.toString());
		return convertedObj;
	}

	public static JSONObject readSimahInquiryXmlFile(String extension, String path) {

		File file = new File(path + extension);
		JSONObject convertedObj = new JSONObject();
		try {
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			if (doc.hasChildNodes()) {
				JSONArray ddd = printNote_1(doc.getChildNodes());
				convertedObj.put("DATA", ddd);
			}
		} catch (Exception e) {
			logger.error("Exception readSimahInquiryXmlFile = " + e.getMessage());
		}

		return convertedObj;
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

}
