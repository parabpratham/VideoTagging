package com.vid.execute;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vid.log.error.ErrorLog;
import com.vid.log.trace.TraceLog;
import com.vid.log.trace.overlay.OverlayLog;

public class AppLogger {

	static TraceLog traceLog;

	static ErrorLog errorLog;

	static OverlayLog overlayLog;

	public AppLogger() {
		System.setProperty("log4j.configurationFile",
				"file:K:/Install/Study/Programming/SpringWorkspace/VideoPlayer/src/log4j2.xml");
		traceLog = new TraceLog();
		overlayLog = new OverlayLog();
		errorLog = new ErrorLog();
	}

	public static Logger getLogger() {
		return LogManager.getRootLogger();
	}

	public static TraceLog getTraceLog() {
		return traceLog;
	}

	public static ErrorLog getErrorLog() {
		return errorLog;
	}

	public static OverlayLog getOverlayLog() {
		return overlayLog;
	}

	public static void main(String[] args) {
		System.setProperty("log4j.configurationFile",
				"file:K:/Install/Study/Programming/SpringWorkspace/VideoPlayer/src/log4j2.xml");
		AppLogger appLogger = new AppLogger();
		appLogger.getErrorLog().error("hi");
		appLogger.getOverlayLog().error("bye");
		appLogger.getTraceLog().debug("hh");
	}

}
