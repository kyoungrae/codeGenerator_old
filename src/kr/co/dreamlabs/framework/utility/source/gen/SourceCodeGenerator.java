/**
 * ++ dreamlabs Product ++
 */
package kr.co.dreamlabs.framework.utility.source.gen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import kr.co.dreamlabs.framework.common.env.ConfigManager;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <pre>
 * xml 구조의 record 데이터로 변환 해주는 클래스
 * </pre>
 *
 * @since 2012. 7. 31.
 * @version 1.0
 * @author ilhoko
 */
public final class SourceCodeGenerator {
	private List<Document> xmls = new ArrayList<Document>();
	private Document config = null;
	private String encoding = ConfigManager.getProperty("kr.co.dreamlabs.Framework.Automation.Gen.File.Encoding");
	// private String keyfields[] = {"lihoko","jcone","unifiedarchiver"};
	// private String fields[] =
	// {"lihoko","jcone","unifiedarchiver","smarearchiver","voicearchiver","unifiedarchivercentre"};
	// private String beaname = "JCOneBean";

	public SourceCodeGenerator() {

	}

	public void setConfig(File f) throws Exception {
		FileInputStream fis = new FileInputStream(f);
		Utils util = new Utils();
		this.config = util.readXml(fis);
	}

	public void setXml(File f) throws Exception {
		FileInputStream fis = new FileInputStream(f);
		Utils util = new Utils();
		this.xmls.add(util.readXml(fis));
	}

	public void setXmls(File[] f) throws Exception {
		for (int i = 0; i < f.length; i++) {
			FileInputStream fis = new FileInputStream(f[i]);
			Utils util = new Utils();
			this.xmls.add(util.readXml(fis));
		}
	}

	public void setXmls(String[] filenames) throws Exception {
		for (int i = 0; i < filenames.length; i++) {
			System.out.println(i);
			FileInputStream fis = new FileInputStream(new File(filenames[i]));
			Utils util = new Utils();
			this.xmls.add(util.readXml(fis));
		}
	}

	class Utils {
		/**
		 * Read XML as DOM.
		 */
		public Document readXml(InputStream is) throws SAXException, IOException, ParserConfigurationException {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			dbf.setValidating(false);
			dbf.setIgnoringComments(false);
			dbf.setIgnoringElementContentWhitespace(true);
			dbf.setNamespaceAware(true);
			// dbf.setCoalescing(true);
			// dbf.setExpandEntityReferences(true);

			DocumentBuilder db = null;
			db = dbf.newDocumentBuilder();
			db.setEntityResolver(new NullResolver());

			// db.setErrorHandler( new MyErrorHandler());

			return db.parse(is);
		}
	}

	class NullResolver implements EntityResolver {
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
			return new InputSource(new StringReader(""));
		}
	}

	public void generateSourceString() throws Exception {

		if (null == this.config) throw new Exception("Configuration file is not setting");
		if (null == this.xmls) throw new Exception("Source Template XML file is not setting");

		int xml_count = this.xmls.size();

		for (int index_xml = 0; index_xml < xml_count; index_xml++) {
			System.out.print("file index -");
			System.out.println(index_xml);
			// reserved replace
			String conjunction = this.xmls.get(index_xml).getElementsByTagName("conjunction").item(0).getTextContent();
			String ext = this.xmls.get(index_xml).getElementsByTagName("ext").item(0).getTextContent();
			String path = this.xmls.get(index_xml).getElementsByTagName("path").item(0).getTextContent();
			String sourcetype = this.xmls.get(index_xml).getElementsByTagName("sourcetype").item(0).getTextContent();
			String filename = this.xmls.get(index_xml).getElementsByTagName("filename").item(0).getTextContent();
			String genunit = null;
			
			try{
				genunit = this.xmls.get(index_xml).getElementsByTagName("gen-unit").item(0).getTextContent();
			}catch(Exception e) {
				
			}

			NodeList entities = this.config.getElementsByTagName("entity");
			if("entity".equals(genunit)) {
//			    <entity build="yes" desc="월별 급여계획 테이블" name="cc_tx_user_pay_s"
//			    		namespace="cc" scope="" service="servicecc_tx_user_pay_s">
				String mergeSource = "";
				for (int index_entity = 0; index_entity < entities.getLength(); index_entity++) {
					System.out.print("-");
					String source = this.xmls.get(index_xml).getElementsByTagName("source").item(0).getTextContent().trim();
					Node entity = entities.item(index_entity);
					String build = entity.getAttributes().getNamedItem("build").getNodeValue();
					if (!"yes".equals(build.toLowerCase())) continue;
					
					String entity_name = entity.getAttributes().getNamedItem("name").getNodeValue();
					String entity_desc = entity.getAttributes().getNamedItem("desc").getNodeValue();
					String service = entity.getAttributes().getNamedItem("service").getNodeValue();
					String namespace = entity.getAttributes().getNamedItem("namespace").getNodeValue();
					String scope = entity.getAttributes().getNamedItem("scope").getNodeValue();
					System.out.println(index_entity + ":" + scope);
					source = source.replaceAll("#entity_name#", entity_name);
					source = source.replaceAll("#entity_desc#", entity_desc);
					source = source.replaceAll("#service#", service);
					source = source.replaceAll("#sourcetype#", sourcetype);

					source = source.replaceAll("#ENTITY_NAME#", entity_name.toUpperCase());
					source = source.replaceAll("#ENTITY_DESC#", entity_desc.toUpperCase());
					source = source.replaceAll("#SERVICE#", service.toUpperCase());
					source = source.replaceAll("#SOURCETYPE#", sourcetype.toUpperCase());

					source = source.replaceAll("#Entity_name#", replaceFirstCharUpper(entity_name));
					source = source.replaceAll("#Entity_desc#", replaceFirstCharUpper(entity_desc));
					source = source.replaceAll("#Service#", replaceFirstCharUpper(service));
					source = source.replaceAll("#Sourcetype#", replaceFirstCharUpper(sourcetype));		
					
					// definition replace
					int cnt = this.xmls.get(index_xml).getElementsByTagName("definition").item(0).getChildNodes().getLength();
					for (int j = 0; j < cnt; j++) {
						if (Node.ELEMENT_NODE == this.xmls.get(index_xml).getElementsByTagName("definition").item(0).getChildNodes().item(j).getNodeType()) {
							String nodename = this.xmls.get(index_xml).getElementsByTagName("definition").item(0).getChildNodes().item(j).getNodeName();
							String value = this.xmls.get(index_xml).getElementsByTagName(nodename).item(0).getTextContent();
							// source = source.replaceAll("#" + nodename + "#",
							// value);
							source = replaceReservedChar(source, nodename, value);

							source = replaceReservedChar(source, "entity_name", entity_name);
							source = replaceReservedChar(source, "entity_desc", entity_desc);
							source = replaceReservedChar(source, "service", service);
							source = replaceReservedChar(source, "sourcetype", sourcetype);
							source = replaceReservedChar(source, "namespace", namespace);
							source = replaceReservedChar(source, "scope", scope);

						}
					}
					mergeSource = mergeSource + source + "\n";
				}

				FileWriter fstream;
				try {
					String fileName = filename;
					String paTh = path;


					File dir = new File(paTh);
					if (!dir.exists()) dir.mkdirs();
					// fstream = new FileWriter(path + fileName + "." + ext);
					// BufferedWriter out = new BufferedWriter(fstream);

					PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(paTh + fileName + "." + ext), encoding));

					out.write(mergeSource);
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw e;
				}
				
			}else {
				for (int index_entity = 0; index_entity < entities.getLength(); index_entity++) {
					System.out.print("-");
					String source = this.xmls.get(index_xml).getElementsByTagName("source").item(0).getTextContent().trim();

					Node entity = entities.item(index_entity);

					String build = entity.getAttributes().getNamedItem("build").getNodeValue();

					if (!"yes".equals(build.toLowerCase())) continue;

					String entity_name = entity.getAttributes().getNamedItem("name").getNodeValue();
					String entity_desc = entity.getAttributes().getNamedItem("desc").getNodeValue();
					String service = entity.getAttributes().getNamedItem("service").getNodeValue();
					String namespace = entity.getAttributes().getNamedItem("namespace").getNodeValue();
					String scope = entity.getAttributes().getNamedItem("scope").getNodeValue();
					System.out.println(index_entity + ":" + scope);
					source = source.replaceAll("#entity_name#", entity_name);
					source = source.replaceAll("#entity_desc#", entity_desc);
					source = source.replaceAll("#service#", service);
					source = source.replaceAll("#sourcetype#", sourcetype);

					source = source.replaceAll("#ENTITY_NAME#", entity_name.toUpperCase());
					source = source.replaceAll("#ENTITY_DESC#", entity_desc.toUpperCase());
					source = source.replaceAll("#SERVICE#", service.toUpperCase());
					source = source.replaceAll("#SOURCETYPE#", sourcetype.toUpperCase());

					source = source.replaceAll("#Entity_name#", replaceFirstCharUpper(entity_name));
					source = source.replaceAll("#Entity_desc#", replaceFirstCharUpper(entity_desc));
					source = source.replaceAll("#Service#", replaceFirstCharUpper(service));
					source = source.replaceAll("#Sourcetype#", replaceFirstCharUpper(sourcetype));

					while (source.indexOf("#begin#") > 0) {
						System.out.print("-");
						int begin = source.indexOf("#begin#");
						int end = source.indexOf("#end#");

						String loopstr = source.substring(begin + "#begin#".length(), end);
						// System.out.println(loopstr);

						List<String> entity_keyfields = new ArrayList<String>();
						List<Map<String, String>> entity_fields = new ArrayList<Map<String, String>>();

						NodeList fields = entity.getChildNodes();
						for (int k = 0; k < fields.getLength(); k++) {
							if (Node.ELEMENT_NODE == fields.item(k).getNodeType()) {
								Node field = fields.item(k);

								if ("true".equals(field.getAttributes().getNamedItem("keyfield").getNodeValue())) {
									entity_keyfields.add(field.getAttributes().getNamedItem("name").getNodeValue());
								}

								Map<String, String> mp = new HashMap<String, String>();
								mp.put("name", field.getAttributes().getNamedItem("name").getNodeValue());
								mp.put("desc", field.getAttributes().getNamedItem("desc").getNodeValue());
								mp.put("type", field.getAttributes().getNamedItem("type").getNodeValue());
								mp.put("length", field.getAttributes().getNamedItem("length").getNodeValue());

								entity_fields.add(mp);
							}
						}

						if (loopstr.indexOf("#Key_fields#") > 0) {
							int c = entity_keyfields.size();
							String rtn = "";
							for (int s = 0; s < c; s++) {
								if (s < c - 1) {
									rtn += loopstr.replaceAll("#Key_fields#", replaceFirstCharUpper(entity_keyfields.get(s))).replaceAll("#conjunction#", conjunction);
								} else {
									rtn += loopstr.replaceAll("#Key_fields#", replaceFirstCharUpper(entity_keyfields.get(s))).replaceAll("#conjunction#", "");
								}
							}
							StringBuffer sb = new StringBuffer();
							String front = source.substring(0, begin);
							String back = source.substring(end + "#end#".length());
							sb.append(front);
							sb.append(rtn);
							sb.append(back);
							source = sb.toString();
						} else if (loopstr.indexOf("#key_fields#") > 0) {
							int c = entity_keyfields.size();
							String rtn = "";
							for (int s = 0; s < c; s++) {
								if (s < c - 1) {
									rtn += loopstr.replaceAll("#key_fields#", entity_keyfields.get(s)).replaceAll("#conjunction#", conjunction);
								} else {
									rtn += loopstr.replaceAll("#key_fields#", entity_keyfields.get(s)).replaceAll("#conjunction#", "");
								}
							}
							StringBuffer sb = new StringBuffer();
							String front = source.substring(0, begin);
							String back = source.substring(end + "#end#".length());
							sb.append(front);
							sb.append(rtn);
							sb.append(back);
							source = sb.toString();
						} else if (loopstr.indexOf("#field_name#") > 0) {
							int c = entity_fields.size();
							String rtn = "";
							for (int s = 0; s < c; s++) {
								if (s < c - 1) {
									rtn += loopstr.replaceAll("#length#", replaceFirstCharUpper(entity_fields.get(s).get("length")))
											.replaceAll("#Field_name#", replaceFirstCharUpper(entity_fields.get(s).get("name"))).replaceAll("#field_desc#", entity_fields.get(s).get("desc"))
											.replaceAll("#field_name#", entity_fields.get(s).get("name")).replaceAll("#conjunction#", conjunction);
								} else {
									rtn += loopstr.replaceAll("#length#", replaceFirstCharUpper(entity_fields.get(s).get("length")))
											.replaceAll("#Field_name#", replaceFirstCharUpper(entity_fields.get(s).get("name"))).replaceAll("#field_desc#", entity_fields.get(s).get("desc"))
											.replaceAll("#field_name#", entity_fields.get(s).get("name")).replaceAll("#conjunction#", "");
								}
							}
							StringBuffer sb = new StringBuffer();
							String front = source.substring(0, begin);
							String back = source.substring(end + "#end#".length());
							sb.append(front);
							sb.append(rtn);
							sb.append(back);
							source = sb.toString();
						} else if (loopstr.indexOf("#Field_name#") > 0) {
							int c = entity_fields.size();
							String rtn = "";
							for (int s = 0; s < c; s++) {
								if (s < c - 1) {
									rtn += loopstr.replaceAll("#length#", replaceFirstCharUpper(entity_fields.get(s).get("length")))
											.replaceAll("#Field_name#", replaceFirstCharUpper(entity_fields.get(s).get("name"))).replaceAll("#field_desc#", entity_fields.get(s).get("desc"))
											.replaceAll("#field_name#", entity_fields.get(s).get("name")).replaceAll("#conjunction#", conjunction);
								} else {
									rtn += loopstr.replaceAll("#length#", replaceFirstCharUpper(entity_fields.get(s).get("length")))
											.replaceAll("#Field_name#", replaceFirstCharUpper(entity_fields.get(s).get("name"))).replaceAll("#field_desc#", entity_fields.get(s).get("desc"))
											.replaceAll("#field_name#", entity_fields.get(s).get("name")).replaceAll("#conjunction#", "");
								}
							}
							StringBuffer sb = new StringBuffer();
							String front = source.substring(0, begin);
							String back = source.substring(end + "#end#".length());
							sb.append(front);
							sb.append(rtn);
							sb.append(back);
							source = sb.toString();
						}
					}
					// definition replace
					int cnt = this.xmls.get(index_xml).getElementsByTagName("definition").item(0).getChildNodes().getLength();
					for (int j = 0; j < cnt; j++) {
						if (Node.ELEMENT_NODE == this.xmls.get(index_xml).getElementsByTagName("definition").item(0).getChildNodes().item(j).getNodeType()) {
							String nodename = this.xmls.get(index_xml).getElementsByTagName("definition").item(0).getChildNodes().item(j).getNodeName();
							String value = this.xmls.get(index_xml).getElementsByTagName(nodename).item(0).getTextContent();
							// source = source.replaceAll("#" + nodename + "#",
							// value);
							source = replaceReservedChar(source, nodename, value);

							source = replaceReservedChar(source, "entity_name", entity_name);
							source = replaceReservedChar(source, "entity_desc", entity_desc);
							source = replaceReservedChar(source, "service", service);
							source = replaceReservedChar(source, "sourcetype", sourcetype);
							source = replaceReservedChar(source, "namespace", namespace);
							source = replaceReservedChar(source, "scope", scope);

						}
					}
					FileWriter fstream;
					try {
						String fileName = filename;
						String paTh = path;

						fileName = replaceReservedChar(fileName, "entity_name", entity_name);
						fileName = replaceReservedChar(fileName, "entity_desc", entity_desc);
						fileName = replaceReservedChar(fileName, "service", service);
						fileName = replaceReservedChar(fileName, "sourcetype", sourcetype);
						fileName = replaceReservedChar(fileName, "namespace", namespace);
						fileName = replaceReservedChar(fileName, "scope", scope);

						paTh = replaceReservedChar(paTh, "entity_name", entity_name);
						paTh = replaceReservedChar(paTh, "entity_desc", entity_desc);
						paTh = replaceReservedChar(paTh, "service", service);
						paTh = replaceReservedChar(paTh, "sourcetype", sourcetype);
						paTh = replaceReservedChar(paTh, "namespace", namespace);
						paTh = replaceReservedChar(paTh, "scope", scope);

						File dir = new File(paTh);
						if (!dir.exists()) dir.mkdirs();
						// fstream = new FileWriter(path + fileName + "." + ext);
						// BufferedWriter out = new BufferedWriter(fstream);

						PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(paTh + fileName + "." + ext), encoding));

						out.write(source);
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
						throw e;
					}
				}
				
			}
			
		}
	}

	private String replaceReservedChar(String fullstr, String name, String str) {
		String rtnStr = fullstr;
		rtnStr = rtnStr.replaceAll("#" + name + "#", str);
		rtnStr = rtnStr.replaceAll("#" + name.toUpperCase() + "#", str.toUpperCase());
		rtnStr = rtnStr.replaceAll("#" + replaceFirstCharUpper(name) + "#", replaceFirstCharUpper(str));
		return rtnStr;
	}

	private String replaceFirstCharUpper(String str) {
		if ((null == str) || (str.length() < 1)) return str;
		String rStr = str.substring(0, 1);
		rStr = rStr.toUpperCase();
		rStr += str.substring(1);
		return rStr;
	}

	public static void main(String args[]) throws Exception {
		System.out.println("--------------start");
		SourceCodeGenerator gp = new SourceCodeGenerator();

		int cnt = 0;
		String filename = null;
		cnt = Integer.valueOf(ConfigManager.getProperty("kr.co.dreamlabs.Framework.Automation.Gen.Template.Xml.Files.Count"));
		String filelists[] = new String[cnt];
		System.out.println("--------------count");
		System.out.println(cnt);
		for (int i = 0; i < cnt; i++) {
			filename = ConfigManager.getProperty("kr.co.dreamlabs.Framework.Automation.Gen.Template.Xml.Files." + i);
			System.out.println("parsing...");
			System.out.println(filename);
			if (null == filename) break;
			if (!new File(filename).isFile()) break;
			System.out.println(i);
			filelists[i] = new String(filename);
		}

		gp.setConfig(new File(ConfigManager.getProperty("kr.co.dreamlabs.Framework.Automation.Gen.Entity.Xml.File")));
		gp.setXmls(filelists);
		gp.generateSourceString();
		System.out.println("\n--------------end");
	}
}
