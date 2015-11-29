package com.vid.matroska;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ebml.BinaryElement;
import org.ebml.UTF8StringElement;
import org.ebml.io.FileDataSource;
import org.ebml.matroska.MatroskaFile;
import org.ebml.matroska.MatroskaFileAttachment;

import com.vid.execute.AppLogger;
import com.vid.log.trace.TraceLog;

public class MatroskaContainer {

	private String filePath;

	private MatroskaFile videoFile;

	private TraceLog traceLog = AppLogger.getTraceLog();

	private Map<UTF8StringElement, MatroskaFileAttachment> attachmentMap;

	private UTF8StringElement overlayFile = null;

	public MatroskaContainer() {
	}

	public MatroskaContainer(String filePath) {
		attachmentMap = new HashMap<>();
		setFilePath(filePath);
		try {
			FileDataSource ioDS = new FileDataSource(getFilePath());
			videoFile = new MatroskaFile(ioDS);
			if (videoFile != null) {
				videoFile.readFile();
				attachmentMap = videoFile.getAttachmentMap();
				for (UTF8StringElement fileName : attachmentMap.keySet()) {
					if (fileName.getValue().contains("Overlay")) {
						overlayFile = fileName;
					}
				}
			}

		} catch (Exception e) {
			traceLog.error(e.getMessage());
			e.printStackTrace();
		}

	}

	public BinaryElement getFileData(UTF8StringElement fileName) {

		MatroskaFileAttachment fileAttachment = attachmentMap.get(fileName);
		if (fileAttachment == null)
			return null;
		else
			return fileAttachment.getFileData();

	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public MatroskaFile getVideoFile() {
		return videoFile;
	}

	public void setVideoFile(MatroskaFile videoFile) {
		this.videoFile = videoFile;
	}

	public UTF8StringElement getOverlayFile() {
		return overlayFile;
	}

	public Map<UTF8StringElement, MatroskaFileAttachment> getAttachmentMap() {
		return attachmentMap;
	}

	public void setOverlayFile(UTF8StringElement overlayFile) {
		this.overlayFile = overlayFile;
	}

	Map<String, byte[]> fileDataMap = new HashMap<>();

	public byte[] getDataFile(String fileName) {

		byte[] fileData = fileDataMap.get(fileName);
		if (fileData != null)
			return fileData;

		Set<UTF8StringElement> keySet = attachmentMap.keySet();
		for (UTF8StringElement key : keySet) {
			if (key.getValue().equalsIgnoreCase(fileName)) {
				fileData = attachmentMap.get(key).getFileData().getData().array();
				fileDataMap.put(fileName, fileData);
				break;
			}
		}
		return fileData;
	}
}
