/**
 * 
 * DIRECTV PROPRIETARY
 * Copyright� 2014 DIRECTV, INC.
 * UNPUBLISHED WORK
 * ALL RIGHTS RESERVED
 * 
 * This software is the confidential and proprietary information of
 * DIRECTV, Inc. ("Proprietary Information").  Any use, reproduction, 
 * distribution or disclosure of the software or Proprietary Information, 
 * in whole or in part, must comply with the terms of the license 
 * agreement, nondisclosure agreement or contract entered into with 
 * DIRECTV providing access to this software.
 */
package com.cleanup.data.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The CloseUtils class.
 *
 */
public class CloseUtils {
	
	private static Logger LOGGER = LoggerFactory.getLogger(CloseUtils.class);

	private CloseUtils() {

	}

	public static void close(Object... objects) {
		for (Object object : objects) {
			try {
				if (object != null) {
					if (object instanceof PreparedStatement) {
						((PreparedStatement) object).close();
					} else if (object instanceof ResultSet) {
						((ResultSet) object).close();
					} else if (object instanceof Connection) {
						((Connection) object).close();
					} else if (object instanceof AutoCloseable) {
						((AutoCloseable) object).close();
					}
				}
			} catch (Exception ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		}
	}
}
