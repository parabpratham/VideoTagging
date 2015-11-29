package com.vid.log.trace.overlay;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vid.execute.AppLogger;
import com.vid.log.error.ErrorLog;

public class OverlayLog {

	Logger logger;

	ErrorLog errorLog = AppLogger.getErrorLog();

	public OverlayLog() {
		logger = LogManager.getLogger(OverlayLog.class);
		logger.trace("OverlayLog :: Overlay Messages :: ------------------------------ ");
		logger.trace("---------------------------New Run------------------------------ ");
	}

	public void trace(String msg) {
		logger.trace(msg);
	}

	public void error(String msg) {
		logger.error(msg);
		//errorLog.error(msg);
	}

}
