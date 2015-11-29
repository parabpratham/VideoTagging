package com.vid.play.overlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vid.execute.AppLogger;
import com.vid.log.trace.overlay.OverlayLog;
import com.vid.matroska.MatroskaContainer;
import com.vid.play.overlay.XMLJDomParser.Annotation;
import com.vid.play.overlay.XMLJDomParser.AnnotationKey;

public class OverlayFactory implements Runnable {

	private XMLJDomParser xmlParser;

	private Map<AnnotationKey, Annotation> annotationMap;

	private static Map<Integer, CustomOverlayMarker> overlayMarkerMap;

	private ArrayList<AnnotationKey> keyArrayList;

	private List<Integer> eventTimeList;

	private static OverlayLog logger = AppLogger.getOverlayLog();

	public OverlayFactory() {
		xmlParser = new XMLJDomParser();
		keyArrayList = new ArrayList<AnnotationKey>();
		eventTimeList = new ArrayList<Integer>();
		overlayMarkerMap = new HashMap<>();
		setupOverlays();
	}

	public OverlayFactory(MatroskaContainer container) {
		setContainer(container);
		xmlParser = new XMLJDomParser();
		keyArrayList = new ArrayList<AnnotationKey>();
		eventTimeList = new ArrayList<Integer>();
		overlayMarkerMap = new HashMap<>();
		setupOverlays();
	}

	@Override
	public void run() {
		setupOverlays();
	}

	public static MatroskaContainer container;

	public void setupOverlays() {

		annotationMap = xmlParser.xmlQuery(container);

		if (annotationMap != null) {
			keyArrayList.addAll(annotationMap.keySet());
			Collections.sort(keyArrayList);
			if (logger == null)
				logger = AppLogger.getOverlayLog();
			logger.trace("AnnotationMap generated and keys sorted " + annotationMap.size());
			fillEventList();
			generateOveralysList();
		}
	}

	private void fillEventList() {

		for (AnnotationKey key : getKeyArrayList()) {
			if (!eventTimeList.contains(key.getStartTime()))
				eventTimeList.add(key.getStartTime());
			if (!eventTimeList.contains(key.getEndTime()))
				eventTimeList.add(key.getEndTime());
		}
		Collections.sort(eventTimeList);
		logger.trace("EventTimeList generated");
	}

	private void generateOveralysList() {
		for (int overlayGenerationTime : eventTimeList) {
			for (AnnotationKey key : keyArrayList) {
				if (key.getEndTime() <= overlayGenerationTime)
					key.setChecked(true);
				if (key.getStartTime() > overlayGenerationTime)
					break;
				else if (key.getStartTime() <= overlayGenerationTime && key.getEndTime() >= overlayGenerationTime) {
					CustomOverlayMarker marker;
					if (overlayMarkerMap.get(overlayGenerationTime) == null) {
						marker = new CustomOverlayMarker();
						marker.setStartTime(key.getStartTime());
						marker.setEndTime(key.getEndTime());
					} else
						marker = overlayMarkerMap.get(overlayGenerationTime);
					marker.addOverlayMarker(overlayGenerationTime, annotationMap.get(key));
					overlayMarkerMap.put(overlayGenerationTime, marker);
				}
			}
		}

	}

	public ArrayList<AnnotationKey> getKeyArrayList() {
		return keyArrayList;
	}

	public class CustomOverlayMarker {

		int id;

		int startTime;

		int endTime;

		List<Annotation> annotations;

		public int getStartTime() {
			return startTime;
		}

		public void setStartTime(int startTime) {
			this.startTime = startTime;
		}

		public int getEndTime() {
			return endTime;
		}

		public void setEndTime(int endTime) {
			this.endTime = endTime;
		}

		public List<Annotation> getAnnotations() {
			return annotations;
		}

		public void setAnnotations(List<Annotation> annotations) {
			this.annotations = annotations;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public void addOverlayMarker(int id, Annotation annotation) {
			if (annotations == null)
				annotations = new ArrayList<>();
			annotations.add(annotation);
		}

	}

	public static Map<Integer, CustomOverlayMarker> getOverlayMarkerMap() {
		return overlayMarkerMap;
	}

	public static void setOverlayMarkerMap(Map<Integer, CustomOverlayMarker> overlayMarkerMap) {
		OverlayFactory.overlayMarkerMap = overlayMarkerMap;
	}

	public static MatroskaContainer getContainer() {
		return container;
	}

	public static void setContainer(MatroskaContainer container) {
		OverlayFactory.container = container;
	}

}
