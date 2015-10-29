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
import com.vid.overlay.comp.master.COMPONENT_TYPE;
import com.vid.play.overlay.XMLJDomParser.Annotation;
import com.vid.play.overlay.XMLJDomParser.AnnotationKey;

public class OverlayFactory implements Runnable {

	private XMLJDomParser test;

	private Map<AnnotationKey, Annotation> annotationMap;

	private ArrayList<AnnotationKey> keyArrayList;

	private List<Integer> eventTimeList;

	private static Map<Integer, CustomOverlay> overlays;

	private static final OverlayLog logger = AppLogger.getOverlayLog();

	public OverlayFactory() {
		test = new XMLJDomParser();
		keyArrayList = new ArrayList<AnnotationKey>();
		eventTimeList = new ArrayList<Integer>();
		overlays = OverLayGenerator.getOverlays();
	}

	@Override
	public void run() {
		annotationMap = test.xmlQuery(
				"k:/Install/Study/Programming/SpringWorkspace/VideoPlayer/src/resources/overlay_xml/CustomOverlay.xml");
		keyArrayList.addAll(annotationMap.keySet());
		Collections.sort(keyArrayList);
		logger.trace("AnnotationMap generated and keys sorted " + annotationMap.size());
		fillEventList();
		generateOveralys();
		OverLayGenerator.setCurrent_overlays(overlays);
		// Current_overlays = new HashMap<>(overlays);
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
						else if (key.getStartTime() <= i && key.getEndTime() >= i) {
							Annotation annotation = annotationMap.get(key);
							if (annotation.getComponent_TYPE() == COMPONENT_TYPE.JCOMPONENT) {
								addJcom(eventJCompMap, i, annotation.getComp());
								addScom(eventSCompMap, i, null);
							} else {
								addJcom(eventJCompMap, i, null);
								addScom(eventSCompMap, i, annotation.getComp());
							}
						}
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
			overlays.put((eventTimeList.get(i)),
					new CustomOverlay(null, eventTimeList.get(i), eventTimeList.get(i + 1), jList, sList));
		}
		logger.trace("Generated overlays count : " + overlays.size());
	}

	public ArrayList<AnnotationKey> getKeyArrayList() {
		return keyArrayList;
	}

}
