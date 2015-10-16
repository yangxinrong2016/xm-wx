package com.imxiaomai.wxplatform.util;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public final class Xml {

        /* ------------------------- gen xml ------------------------- */

	final String tag;
	final StringBuilder attrs = new StringBuilder();
	final StringBuilder content = new StringBuilder();

	public Xml(String tag) {
		this.tag = tag;
	}

	public Xml add(String tagName, Object value) {
		content.append(String.format("<%s>%s</%s>", tagName, EscapeUtil.escapeXml(value), tagName));
		return this;
	}
	public Xml attr(String attrName, Object value) {
		if (value != null)
			attrs.append(String.format(" %s='%s'", attrName, EscapeUtil.escapeXml(value)));
		return this;
	}
	public Xml append(Object str) {
		content.append(str == null ? "" : String.valueOf(str));
		return this;
	}

	public String toString() {
		return String.format("<%s%s>%s</%s>", tag, attrs, content, tag);
	}
	public String toDoc(String charset) {
		return String.format(XML_DECLARATION, charset) + this.toString();
	}
	public String toDoc() {
		return toDoc("GBK");
	}
	static final String XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"%s\"?>";



        /* ------------------------- build xml ------------------------- */

	public static Document parse(String xml) throws DocumentException, IOException, XmlPullParserException {
		return new _FixedXPP3Reader().read(new StringReader(xml));
	}
	public static ParsedXml of(String xml) throws DocumentException, IOException, XmlPullParserException {
		return new ParsedXml(parse(xml));
	}

	public static String textNotNull(Element elem, String xpath) {
		if(StringUtils.isNotBlank(text(elem, xpath))){
			return text(elem, xpath).trim();
		}
		return "";
	}
	public static String text(Element elem, String xpath) {
		String[] paths = xpath.split("/");
		for (int i = 0; i < paths.length - 1; i++) {
			if (!paths[i].isEmpty() && elem != null)
				elem = elem.element(paths[i]);
		}
		if (elem == null)
			return null;
		String lastPath = paths[paths.length - 1];
		if (lastPath.isEmpty())
			return elem.getTextTrim();
		if (lastPath.startsWith("@"))
			return elem.attributeValue(lastPath.substring(1));
		return elem.elementText(lastPath);
	}

	public static final class ParsedXml {
		public ParsedXml(Document doc) {
			this.doc = doc;
			this.root = doc != null ? doc.getRootElement() : null;
		}
		public final Document doc;
		public final Element root;

		public boolean isEmpty() {
			return root == null;
		}

		public String textNotNull(String xpath) {
			return Xml.textNotNull(root, xpath);
		}
		public String text(String xpath) {
			return Xml.text(root, xpath);
		}

		public Element elem(String xpath) {
			Element elem = this.root;
			for (String path : xpath.split("/")) {
				if (elem != null)
					elem = elem.element(path);
			}
			return elem;
		}
		@SuppressWarnings("unchecked")
		public List<Element> elems(String xpath) {
			List<Element> src = new ArrayList<Element>(Arrays.asList(this.root));
			List<Element> trg = new ArrayList<Element>();
			for (String path : xpath.split("/")) {
				for (Element elem : src)
					trg.addAll(elem.elements(path));
				List<Element> swap = src;
				src = trg;
				trg = swap;
				trg.clear();
			}
			return src;
		}
	}



        /* ------------------------- misc ------------------------- */

	public static String prettyPrint(String xml) {
		if (xml == null)
			return "!!! Empty Xml !!!";

		Document doc;
		try {
			doc = new _FixedXPP3Reader().read(new StringReader(xml));
		} catch (Exception e) {
			return "!!! Parse Xml Error !!!\n" + e + "\n\n\n" + xml;
		}

		try {
			return _prettyPrint(doc);
		} catch (Exception e) {
			return "!!! Format Xml Error !!!\n" + e + "\n\n\n" + xml;
		}
	}
	public static String prettyPrint(Document doc) {
		if (doc == null)
			return "!!! Empty Doc !!!";
		try {
			return _prettyPrint(doc);
		} catch (Exception e) {
			return "!!! Format Doc Error !!!\n" + e;
		}
	}
	static String _prettyPrint(Document doc) throws Exception {
		OutputFormat format = OutputFormat.createPrettyPrint();
		StringWriter out = new StringWriter();
		new XMLWriter(out, format).write(doc);
		return out.toString();
	}
	public static String prettyPrint(Element elem) {
		if (elem == null)
			return "!!! Empty Elem !!!";
		try {
			return _prettyPrint(elem);
		} catch (Exception e) {
			return "!!! Format Elem Error !!!\n" + e;
		}
	}
	private static String _prettyPrint(Element elem) throws Exception {
		OutputFormat format = OutputFormat.createPrettyPrint();
		StringWriter out = new StringWriter();
		new XMLWriter(out, format).write(elem);
		return out.toString();
	}


    /* ------------------------- demo ------------------------- */

	public static void main(String[] args) throws Exception {
		String xml = testSimpleGen();
		testParse(xml);
		// test pretty print
		xml = testComplexGen();
	}

	private static String testSimpleGen() {
		return new Xml("root").add("one", 1).add("two", "II").append("<three />")
				.append("<a>&lt;hello&gt;</a><b><b1 attr='1'>:)</b1></b>").toDoc();
	}

	private static void testParse(String xml) throws DocumentException, IOException, XmlPullParserException {
		ParsedXml doc = Xml.of(xml);

		Element e = doc.elem("b/b1");
	}

	private static String testComplexGen() {
		return new Xml("root").add("tag1", "simpleVal").append(new Xml("tag2")).toDoc();
	}

}