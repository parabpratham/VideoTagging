package com.vid.play.overlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vid.execute.AppLogger;
import com.vid.log.trace.overlay.OverlayLog;
import com.vid.overlay.comp.Jcomp.CustomJComponent;
import com.vid.overlay.comp.Scomp.CustomStaticComponent;
import com.vid.play.overlay.XMLJDomParser.Annotation;
import com.vid.play.overlay.XMLJDomParser.AnnotationKey;

public class OverlayFactory implements Runnable {

	private XMLJDomParser test;

	private Map<AnnotationKey, Annotation> annotationMap;

	private static Map<Integer, CustomOverlayMarker> overlayMarkerMap;

	private ArrayList<AnnotationKey> keyArrayList;

	private List<Integer> eventTimeList;
	
	private final String xmlPath = "k:/Install/Study/Programming/SpringWorkspace/VideoPlayer/src/resources/overlay_xml/CustomOverlay.xml";

	private static OverlayLog logger = AppLogger.getOverlayLog();

	public OverlayFactory() {
		test = new XMLJDomParser();
		keyArrayList = new ArrayList<AnnotationKey>();
		eventTimeList = new ArrayList<Integer>();
		// overlays = OverLayGenerator.getOverlays();
		overlayMarkerMap = new HashMap<>();
		run();
	}

	@Override
	public void run() {
		annotationMap = test.xmlQuery(xmlPath);
		keyArrayList.addAll(annotationMap.keySet());
		Collections.sort(keyArrayList);
		if (logger == null)
			logger = AppLogger.getOverlayLog();
		logger.trace("AnnotationMap generated and keys sorted " + annotationMap.size());
		fillEventList();
		generateOveralysList();
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

	private List<CustomJComponent> addJcom(Map<Integer, List<CustomJComponent>> eventCompList, int key, Object value) {
		List<CustomJComponent> list = eventCompList.get(key);
		if (list == null)
			list = new ArrayList<>();
		list.add((CustomJComponent) value);
		return eventCompList.put(key, list);
	}

	private List<CustomStaticComponent> addScom(Map<Integer, List<CustomStaticComponent>> eventCompList, int key,
			Object value) {
		List<CustomStaticComponent> list = eventCompList.get(key);
		if (list == null)
			list = new ArrayList<>();
		list.add((CustomStaticComponent) value);
		return eventCompList.put(key, list);
	}

	
	private void generateOveralys() {
		try {
			Map<Integer, List<CustomJComponent>> eventJCompMap = new HashMap<Integer, List<CustomJComponent>>();
			Map<Integer, List<CustomStaticComponent>> eventSCompMap = new HashMap<Integer, List<CustomStaticComponent>>();
			logger.trace("GenerateOverlays started");
			for (int i : eventTimeList) {
				for (AnnotationKey key : keyArrayList) {
					if (!key.isChecked()) {
						if (key.getEndTime() <= i)
							key.setChecked(true);
						if (key.getStartTime() > i)
							break;
//						else if (key.getStartTime() <= i && key.getEndTime() >= i) {
//							Annotation annotation = annotationMap.get(key);
//							
//							if (annotation.getComponent_TYPE() == COMPONENT_TYPE.JCOMPONENT) {
//								logger.trace(
//										i + " " + key.startTime + " " + key.endTime + " Found J " + annotation.getId());
//								addJcom(eventJCompMap, i, annotation.getComp());
//								addScom(eventSCompMap, i, null);
//							} else if (annotation.getComponent_TYPE() == COMPONENT_TYPE.SHAPE) {
//								addJcom(eventJCompMap, i, null);
//								logger.trace(
//										i + " " + key.startTime + " " + key.endTime + " Found S " + annotation.getId());
//								addScom(eventSCompMap, i, annotation.getComp());
//							}
//						}
					}
				}
			}
			logger.trace("GenerateOverlays : generated " + eventJCompMap.size() + " , " + eventSCompMap.size()
					+ " overlays");
			createOverLayMap(eventJCompMap, eventSCompMap);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createOverLayMap(Map<Integer, List<CustomJComponent>> eventJCompList,
			Map<Integer, List<CustomStaticComponent>> eventSCompList) {
		logger.trace("Generating overlays at time events " + eventTimeList.size());
		for (int i = 0; i < eventTimeList.size() - 1; i++) {
			List<CustomJComponent> jList = eventJCompList.get(eventTimeList.get(i));
			List<CustomStaticComponent> sList = eventSCompList.get(eventTimeList.get(i));
			logger.trace(i + " " + eventTimeList.get(i) + " " + eventTimeList.get(i + 1));
			//overlays.put((eventTimeList.get(i)),
				//	new CustomOverlayTest(null, eventTimeList.get(i), eventTimeList.get(i + 1), jList, sList));
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

}
