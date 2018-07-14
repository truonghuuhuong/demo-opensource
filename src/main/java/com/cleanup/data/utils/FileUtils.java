package com.cleanup.data.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The FileUtils class.
 *
 */
public class FileUtils {

	/** The logger. */
	private static Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);
	
	/** The properties utils. */
	private static final PropertiesUtils properiesUtil = PropertiesUtils.getInstance();

	/** Offset hours default is 24 hours if null. */
	private static final int OFFSET_HOURS = 24; // default 24 hours
	
    /** The constant SimpleDateFormat. */
    private static final DateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");    
    
	/** The properties offset. */
	private static String offset = properiesUtil.getPropertyValue("apg.clean.data.offset");
		
	/**
	 * Inits the path array.
	 *
	 * @return the JSON array
	 */
	private static JSONArray initPathArray() {
		JSONArray pathArray = new JSONArray();
		try {
			String path = properiesUtil.getPropertyValue("apg.clean.data.path");
			if(StringUtils.isBlank(path)) {
				LOGGER.error("Path must be not empty!");				
			} else {
				pathArray = new JSONArray(path);
			}
		} catch (JSONException e) {
			LOGGER.error(e.getMessage());
		}
		return pathArray;
	}
	
	public static FileTime getCreationTime(File file) throws IOException {
	    Path p = Paths.get(file.getAbsolutePath());
	    BasicFileAttributes view = Files.getFileAttributeView(p, BasicFileAttributeView.class).readAttributes();
	    FileTime fileTime = view.creationTime();
	    return fileTime;
	}
	
	public static void deleteFilesOlder() throws JSONException, IOException {
		JSONArray pathArray = initPathArray();
		long createTime;
		int count;
		
		if (null != pathArray && pathArray.length() > 0) {
			String urls = null;
			String extensions = null; 
			JSONObject path = null;
			
			int offsetHours = 0;
			if (StringUtils.isNotBlank(offset) && StringUtils.isNumeric(offset)) {
				offset = offset.trim();
				offsetHours = Integer.parseInt(offset);
			}
			if (offsetHours == 0) {
				offsetHours = OFFSET_HOURS;
			}
			long purgeTime = System.currentTimeMillis() - ((long) offsetHours * 60 * 60 * 1000);
	
			for (int i = 0; i < pathArray.length(); i++) {
				LOGGER.info("");
				LOGGER.info("Path: {}", i + 1 );
				path = pathArray.getJSONObject(i);			
				try {
					urls = path.getString("url");
					extensions = path.getString("extensions");
				} catch (JSONException e) {
					LOGGER.error(e.getMessage().replace("JSONObject", "Properties "));
				}
	
				LOGGER.info("URL: \"{}\" - extensions:{}", urls, extensions);
				final StringBuilder extension = new StringBuilder();
				if (null != extensions) {
					extensions = extensions.trim();
					extensions = extensions.replace(';', '|');				
					extension.append("([^\\s]+(\\.(?i)(");
					extension.append(extensions);
					extension.append("))$)");
				
					if (StringUtils.isNotBlank(urls)) {
						final File directory = new File(urls.trim());
						if (directory.exists() && directory.isDirectory()) {
							if (null == directory.listFiles()){
								LOGGER.error("\t- You don't currently have permission to access this folder.");
							} else {
								File[] listFiles = directory.listFiles();
								count = 0;
								if (StringUtils.isNotBlank(extensions)) {
									listFiles = directory.listFiles(new FilenameFilter() {
										public boolean accept(File dir, String name) {
											return name.matches(extension.toString());
										}
									});
								}
								for (File file : listFiles) {
									createTime = getCreationTime(file).toMillis();
									if (createTime < purgeTime) {
										if (file.delete()) {
	                                        LOGGER.info("\t- {} - {}" , file.getName(), sdfDate.format(createTime));
											count++;
										} else {
											LOGGER.info("\t- Cannot delete file {}, you need permission to perform this action!", file.getName());
										}
									}
								}
								LOGGER.info("Number of file(s) deleted: {} and the remaining files: {}", count , listFiles.length - count);
							}
						} else {
							LOGGER.error("URL \"{}\" does not exist.", urls);
						}
					}else {
						LOGGER.error("URL must be not empty!");
					}
				}
			}
		}
	}
}
