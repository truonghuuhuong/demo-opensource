package com.cleanup.data.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This class allow get value of properties in file config.properties.
 *
 */
@Component
public final class PropertiesUtils {
	/** The log. */
	private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesUtils.class);
	
	/** The config file name. */
	private final String configFileName = "config.properties";
	
	/** The config file. */
	private final File configFile = new File(configFileName);

	/** The instance. */
	private static PropertiesUtils instance = null;
	
	private Map<String, String> defaultValueMap = initDefaultValueMap();
	
	/** The prop. */
	private Properties prop;

	/**
	 * Constructor.
	 */
	private PropertiesUtils() {
		prop = new Properties();
		InputStream is = null;
		try {
			is = getConfigFileInputStream();
			prop.load(is);
		} catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			CloseUtils.close(is);
		}
	}
	
	private Map<String, String> initDefaultValueMap() {
		Map<String, String> defaultValueMap = new HashMap<String, String>();
		return defaultValueMap;
	}

	public static synchronized void loadProperties() {
		instance = new PropertiesUtils();
	}

	/**
	 * Get instance of PropertiesUtils.
	 * 
	 * @return instance of PropertiesUtils
	 */
	public static synchronized PropertiesUtils getInstance() {
		if (instance == null) {
			instance = new PropertiesUtils();
		}
		return instance;
	}

	/**
	 * Get property value by property key.
	 *
	 * @param propertyKey the property key
	 * @return value of property
	 */
	public String getPropertyValue(String propertyKey) {
		String result = prop.getProperty(propertyKey);
		if (result == null) {
			LOGGER.warn("Property {} does not exist. Retrieve default value.", propertyKey);
			result = defaultValueMap.get(propertyKey);
		}
		return result;
	}
	
	public boolean propertyValueExists(String propertyKey) {
		return prop.getProperty(propertyKey) != null;
	}
	
	/**
	 * @param propertyKey
	 * @param defaultValue
	 * @return
	 */
	public String getPropertyValue(String propertyKey, String defaultValue) {
		return prop.getProperty(propertyKey, defaultValue);
	}
	
	public InputStream getConfigFileInputStream() throws FileNotFoundException {
		InputStream is;
		LOGGER.info("Load config file for PropertiesUtils at: {}", configFile.getAbsolutePath());
		if (!configFile.exists()) {
			LOGGER.warn("Property file doesn't exist. Load from resource.");
			is = this.getClass().getClassLoader().getResourceAsStream(configFileName);
		} else {
			is = new FileInputStream(configFile);
		}
		return is;
	}
	
	public static void setNull(){
		instance = null;
	}
}
