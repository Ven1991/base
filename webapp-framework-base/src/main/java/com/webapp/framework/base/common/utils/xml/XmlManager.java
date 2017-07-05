package com.webapp.framework.base.common.utils.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlManager {
	private static Logger log = LogManager.getLogger(XmlManager.class);

	public static Document readstr2xml(String str) throws IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		Document doc;
		try {
			db = dbf.newDocumentBuilder();
			doc = db.parse(new ByteArrayInputStream(str.getBytes()));
		} catch (Exception pce) {
			log.error(pce.getMessage(), pce);
			log.info(str);
			return null;
		}
		return doc;
	}

	public static Document readstr2xml(String str, String charset) throws IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		Document doc;
		try {
			db = dbf.newDocumentBuilder();
			doc = db.parse(new ByteArrayInputStream(str.getBytes(charset)));
		} catch (Exception pce) {

			log.error(pce.getMessage(), pce);
			log.info(str);
			return null;
		}
		// Document doc;
		return doc;
	}

	public static Document readXMLFile(File file) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;

		db = dbf.newDocumentBuilder();

		if (!file.exists()) {
			throw new Exception("获取文件失败");
		}
		Document doc = db.parse(file);

		return doc;
	}

	public static Document readXMLFile(InputStream is) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		return db.parse(is);
	}

	public static Document readXMLFile(String inFile) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;

		db = dbf.newDocumentBuilder();

		File file = new File(inFile);
		if (!file.exists()) {
			throw new Exception("获取文件失败[" + inFile + "]");
		}
		Document doc = db.parse(file);

		return doc;
	}

	public static String getAttribValue(Node node, String inStrName) {
		String strRet = null;
		String strName = "";

		if (node == null)
			return null;
		NamedNodeMap attribs = node.getAttributes();
		if (null == attribs)
			return null;
		for (int i = 0; i < attribs.getLength(); i++) {
			Node attribNode = attribs.item(i);
			strName = attribNode.getNodeName();
			if (strName.trim().equals(inStrName)) {
				return attribNode.getNodeValue();
			}
		}
		return strRet;
	}

	public static String getElementValue(Node node) throws Exception {
		if (node != null) {
			Node grandChild = node.getFirstChild();
			if (grandChild != null) {
				if (grandChild.getNodeValue() != null) {
					return grandChild.getNodeValue();
				}
			}
			return null;
		}

		return null;
	}

	public static String getChildElementValue(Node node, String subTagName) {
		String returnString = "";
		if (node != null) {
			NodeList children = node.getChildNodes();
			if (children == null)
				return null;
			for (int innerLoop = 0; innerLoop < children.getLength(); innerLoop++) {
				Node child = children.item(innerLoop);

				if ((child != null) && (child.getNodeName() != null) && (child.getNodeName().equals(subTagName))) {
					Node grandChild = child.getFirstChild();
					if (grandChild == null)
						return "";
					if (grandChild.getNodeValue() != null) {
						returnString = grandChild.getNodeValue();
						return returnString;
					}
				}
			}
		}
		return returnString;
	}

	public static void SaveXmlFile(Document doc, String filename) {
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			DOMSource source = new DOMSource(doc);
			transformer.setOutputProperty("encoding", "GB2312");
			transformer.setOutputProperty("indent", "yes");
			PrintWriter pw = new PrintWriter(new FileOutputStream(filename));
			StreamResult result = new StreamResult(pw);
			transformer.transform(source, result);
		} catch (TransformerException mye) {
			mye.printStackTrace();
		} catch (IOException exp) {
			exp.printStackTrace();
		}
	}

	public static String getXmlKeyValue(String xml, String key) {
		if (null == xml)
			return null;
		if (xml.indexOf(key) < 0)
			return null;
		String startTag = "<" + key + ">";
		String endTag = "</" + key + ">";
		return xml.substring(xml.indexOf(startTag) + startTag.length(), xml.indexOf(endTag));
	}

	public static String getXmlData(String name, String value) {
		if (null == value)
			value = "";

		return "<" + name + "><![CDATA[" + value + "]]></" + name + ">";
	}
}