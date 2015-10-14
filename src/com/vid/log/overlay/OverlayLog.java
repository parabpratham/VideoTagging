package com.vid.log.overlay;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OverlayLog {

	Logger logger;

	public OverlayLog() {
		logger = LogManager.getLogger(OverlayLog.class);
		logger.trace("OverlayLog :: Overlay Messages ::");
	}

	public void trace(String msg) {
		logger.trace(msg);
	}

	public void error(String msg) {
		logger.error(msg);
	}

}
