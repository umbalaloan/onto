package com.vn.smartdata.webapp.util;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * The Class ApplicationUtil.
 */
public class ApplicationUtil {
	
	/** The Constant log. */
	private static final Log log = LogFactory.getLog(ApplicationUtil.class);
	
	/** The prop. */
	//private static Properties prop = null;
	
	/**
	 * Gets the application value.
	 *
	 * @param key the key
	 * @return the application value
	 */
	public static String getApplicationValue(String key){
		String value = null;
		
		//if(prop == null){
			try {
				InputStream inputStream = ApplicationUtil.class.getClassLoader().getResourceAsStream("application.properties");
				Properties prop = new Properties();

				prop.load(inputStream);

				value = prop.getProperty(key);
				inputStream.close();
			} catch (Exception e) {
				log.error("getApplicationValue {}", e);
			}
		//} else {
		//	value = prop.getProperty(key);
		//}
		
		return value;
	}
}
