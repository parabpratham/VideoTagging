package com.vid.log.trace.overlay;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JComponentLog {

	Logger logger;

	public JComponentLog() {
		logger = LogManager.getLogger(JComponentLog.class);
		logger.trace("JComponentLog :: JComponentLog Messages :: ------------------------------ ");
		logger.trace("---------------------------New Run------------------------------ ");
	}

	public void trace(String msg) {
		logger.trace(msg);
	}

	public void error(String msg) {
		logger.error(msg);
	}

}
