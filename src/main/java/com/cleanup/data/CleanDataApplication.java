package com.cleanup.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.cleanup.data.utils.PropertiesUtils;

@SpringBootApplication
@ComponentScan("com.dtv.apg.cleanup.data")
public class CleanDataApplication {

	private static final PropertiesUtils properties = PropertiesUtils.getInstance();

	public static void main(String[] args) {
		if(null != properties && null != properties.getPropertyValue("apg.clean.data.port")) {
			String newPort = properties.getPropertyValue("apg.clean.data.port");
			//new port from config.properties is set here
			System.getProperties().put( "server.port", Integer.parseInt(newPort));  
		}
		SpringApplication.run(CleanDataApplication.class, args); 
	}
}
