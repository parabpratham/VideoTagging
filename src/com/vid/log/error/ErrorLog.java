package com.vid.log.error;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ErrorLog {

	Logger logger;

	public ErrorLog() {
		logger = LogManager.getLogger(ErrorLog.class);
		logger.error("ErrorLog :: ERROR Messages ::-----------------------------------");
		logger.trace("---------------------------New Run------------------------------");
	}
	
	public void error(String msg) {
		logger.error(msg);
	}

}
