/**
 * ++ dreamlabs Product ++
 */
package kr.co.dreamlabs.framework.utility.source.gen;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
 
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import kr.co.dreamlabs.framework.common.env.ConfigManager;
import kr.co.dreamlabs.framework.utility.source.gen.entity.EntityHandler;

/**
 * <pre>
 * EntityXmlGenerator
 * </pre>
 * 
 * @since 2013. 7. 2.
 * @version 1.0
 * @author 고일호(ilho.ko@gmail.com)
 * @see
 *
 */
public class EntityXmlGenerator {
	public static final String _root = "entities";
	public static final String _entity = "entity";
	public static final String _field = "field";

	 public static Map changeToLowerMapKey(Map map){
		   Map<String, Object> origin = map;
		   Map<String, Object> temp = new HashMap<String, Object>();   
		  
		   Set<String> set = origin.keySet();
		   Iterator<String> e = set.iterator();

		   while(e.hasNext()){
		     String key = e.next();
		     Object value = (Object) origin.get(key);

		     temp.put(key.toUpperCase(), value);
		   }

		   origin = null;
		  return temp;
		 }
	 
	public static void main(String args[]) throws Exception {
		EntityHandler handler = new EntityHandler();
		List list = handler.select();
		int cnt = list.size();
		if (0 < cnt) {
			// 1. Create XML Document
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = null;
			Document dom = null;

			try {
				docBuilder = documentFactory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				throw e;
			} catch (Exception ex) {
				throw ex;
			}
			dom = docBuilder.newDocument();

			Element xmlroot = dom.createElement(_root);

			String tablename = "";
			String oldtablename = "";
			Element xmlentity = null;
			for (int i = 0; i < cnt; i++) {
				Map mp = (Map) list.get(i);
				System.out.println(mp );
				mp = changeToLowerMapKey(mp);
				System.out.println(mp );
				tablename = mp.get("TABLE_NAME").toString().toLowerCase();
				
				if (!tablename.equals(oldtablename)) {
					xmlentity = dom.createElement(_entity);
					xmlentity.setAttribute("build", "yes");
					xmlentity.setAttribute("name", tablename);
					xmlentity.setAttribute("service", "service" + tablename);
					xmlentity.setAttribute("desc", nvl(mp.get("TABLE_COMMENTS"), " ").toString());
					xmlentity.setAttribute("namespace", ConfigManager.getProperty("kr.co.dreamlabs.Framework.Automation.Gen.namespace"));
					//xmlentity.setAttribute("scope", "");
					xmlentity.setAttribute("scope", ConfigManager.getProperty("kr.co.dreamlabs.Framework.Automation.Gen.scope"));
					xmlroot.appendChild(xmlentity);
				}
				System.out.println(mp.get("TABLE_NAME"));
				System.out.println(mp.get("COLUMN_NAME"));
				System.out.println(mp.get("COLUMN_KEY"));
				System.out.println(mp.get("COLUMN_LENGTH"));
				System.out.println(mp.get("COLUMN_TYPE"));
				System.out.println(mp.get("COLUMN_NULLABLE"));
				System.out.println(mp.get("COLUMN_DEFAULT"));
				System.out.println(mp.get("COLUMN_COMMENTS"));
				System.out.println("---------");

				Element xmlfield = dom.createElement(_field);
				xmlfield.setAttribute("name", mp.get("COLUMN_NAME").toString().toLowerCase());
				xmlfield.setAttribute("keyfield", mp.get("COLUMN_KEY").toString());
				xmlfield.setAttribute("length", mp.get("COLUMN_LENGTH").toString());
				xmlfield.setAttribute("type", mp.get("COLUMN_TYPE").toString());
				xmlfield.setAttribute("nullable", mp.get("COLUMN_NULLABLE").toString());
				xmlfield.setAttribute("default", nvl(mp.get("COLUMN_DEFAULT"),"").toString());
				xmlfield.setAttribute("desc", nvl(mp.get("COLUMN_COMMENTS"), " ").toString());
				xmlentity.appendChild(xmlfield);

				oldtablename = tablename;
				System.out.println(mp);
			}

			dom.appendChild(xmlroot);

			OutputFormat outputformat = new OutputFormat();
			outputformat.setEncoding("utf-8");
			outputformat.setIndent(4);
			outputformat.setIndenting(true);
			outputformat.setPreserveSpace(false);

			XMLSerializer serializer = new XMLSerializer();

			StringWriter writer = new StringWriter();
			serializer.setOutputFormat(outputformat);
			serializer.setOutputCharStream(writer);
			serializer.asDOMSerializer();
			serializer.serialize(dom.getDocumentElement());
			String rtn = new String(writer.toString());
			System.out.println(rtn);

			// FileWriter fstream;
			try {

				// File dir = new
				// File("/Users/ilhoko/Documents/workspace/KIH.DEV.SrcGen/config");
				// if(!dir.exists())dir.mkdirs();
				// fstream = new FileWriter(
				// ConfigManager.getProperty("kr.co.dreamlabs.Framework.Automation.Gen.Entity.Xml.File"));
				FileOutputStream fstream = new FileOutputStream(ConfigManager.getProperty("kr.co.dreamlabs.Framework.Automation.Gen.Entity.Xml.File"));
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fstream, ConfigManager.getProperty("kr.co.dreamlabs.Framework.Automation.Gen.File.Encoding")));			
				
				out.write(rtn);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			}
		}
	}

	private static Object nvl(Object obj, Object destobj) {
		if (null == obj) return destobj;
		return obj;
	}
}
