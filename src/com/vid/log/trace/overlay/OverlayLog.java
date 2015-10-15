package com.vid.log.trace.overlay;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OverlayLog {

	Logger logger;

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
	}

}
