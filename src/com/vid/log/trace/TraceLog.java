package com.vid.log.trace;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TraceLog {

	Logger logger;

	public TraceLog() {
		logger = LogManager.getLogger(TraceLog.class);
		logger.error("TraceLog :: Trace Messages ::");
	}

	public void trace(String msg) {
		logger.trace(msg);
	}

	public void debug(String msg) {
		logger.debug(msg);
	}

}
