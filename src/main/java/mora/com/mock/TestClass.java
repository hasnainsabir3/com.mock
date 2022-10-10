package mora.com.mock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.chrono.HijrahChronology;
import java.time.chrono.HijrahDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.chrono.IslamicChronology;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.helpers.DefaultHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class TestClass {

	public static void main(String[] args) throws Exception {

		
		//convertIntoJSONObject(".XML", filePath);
		/*
		 * JSONObject mainObj =
		 * jsonObject.getJSONObject("getCitizenAddressInfoResponse").getJSONObject(
		 * "CitizenAddressInfoResult") .getJSONObject("addressListList");
		 * 
		 * System.out.println(jsonObject.getJSONObject("getCitizenAddressInfoResponse").
		 * getJSONObject("CitizenAddressInfoResult")); Set<String> keySet =
		 * mainObj.keySet();
		 * 
		 * JSONObject resultObj = new JSONObject(); JSONObject addressObject = new
		 * JSONObject(); for (String key : keySet) { addressObject.put(key,
		 * mainObj.opt(key).toString()); } resultObj.put("httpStatusCode", 200);
		 * resultObj.put("opstatus", 0); resultObj.put("addressListList",
		 * addressObject); resultObj.put("CitizenAddressInfoResult", new
		 * JSONObject().put("logId",
		 * jsonObject.getJSONObject("getCitizenAddressInfoResponse").getJSONObject(
		 * "CitizenAddressInfoResult").get("logId")));
		 * resultObj.put("ns1:getCitizenAddressInfoResponse", new JSONObject());
		 * resultObj.put("soap:Body", new JSONObject());
		 */

		// System.out.println(resultObj);

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
			String xml = FileUtils.readFileToString(file,Charset.defaultCharset());
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
