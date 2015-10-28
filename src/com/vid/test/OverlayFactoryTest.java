package com.vid.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import com.vid.execute.AppLogger;
import com.vid.overlay.comp.Jcomp.CustomJComponent;
import com.vid.play.CustomOverlay;
import com.vid.play.OverLayGenerator;
import com.vid.test.JDomParserTest.Annotation;
import com.vid.test.JDomParserTest.AnnotationKey;

import uk.co.caprica.vlcj.discovery.NativeDiscovery;

public class OverlayFactoryTest implements Runnable {

	private JDomParserTest test;

	private static Map<Integer, CustomOverlay> overlays;

	private Map<AnnotationKey, Annotation> annotationMap;

	private ArrayList<AnnotationKey> keyArrayList;

	private List<Integer> eventTimeList;

	public OverlayFactoryTest() {
		test = new JDomParserTest();
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
		fillEventList();
		generateOveralys();
	}

	private void fillEventList() {

		for (AnnotationKey key : getKeyArrayList()) {
			if (!eventTimeList.contains(key.startTime))
				eventTimeList.add(key.getStartTime());
			if (!eventTimeList.contains(key.endTime))
				eventTimeList.add(key.getEndTime());
		}

		Collections.sort(eventTimeList);
	}

	private List<CustomJComponent> addJcom(Map<Integer, List<CustomJComponent>> eventCompList, int key, Object value) {
		List<CustomJComponent> list = eventCompList.get(key);
		if (list == null)
			list = new ArrayList<>();
		list.add((CustomJComponent) value);
		return eventCompList.put(key, list);
	}

	private synchronized void generateOveralys() {
		try {
			Map<Integer, List<CustomJComponent>> eventCompMap = new HashMap<Integer, List<CustomJComponent>>();

			for (int i : eventTimeList) {
				for (AnnotationKey key : keyArrayList) {
					if (!key.isChecked) {
						if (key.getEndTime() <= i)
							key.setChecked(true);
						else if (key.startTime <= i && key.endTime >= i) {
							Annotation annotation = annotationMap.get(key);
							addJcom(eventCompMap, i, annotation.getComp());
						}
					}
				}
			}

			createOverLayMap(eventCompMap);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createOverLayMap(Map<Integer, List<CustomJComponent>> eventCompList) {
		Object[] keySet = eventCompList.keySet().toArray();
		for (int i = 0; i < keySet.length - 1; i++) {
			List<CustomJComponent> list = eventCompList.get(keySet[i]);
			overlays.put(((Integer) keySet[i]),
					new CustomOverlay(null, (Integer) keySet[i], (Integer) keySet[i + 1], list));
		}
	}

	public static Map<Integer, CustomOverlay> getOverlays() {
		return overlays;
	}

	public static void setOverlays(Map<Integer, CustomOverlay> overlays) {
		OverlayFactoryTest.overlays = overlays;
	}

	public Map<AnnotationKey, Annotation> getAnnotationMap() {
		return annotationMap;
	}

	public void setAnnotationMap(Map<AnnotationKey, Annotation> annotationMap) {
		this.annotationMap = annotationMap;
	}

	public ArrayList<AnnotationKey> getKeyArrayList() {
		return keyArrayList;
	}

	public void setKeyArrayList(ArrayList<AnnotationKey> keyArrayList) {
		this.keyArrayList = keyArrayList;
	}

	public List<Integer> getEventTimeList() {
		return eventTimeList;
	}

	public void setEventTimeList(List<Integer> eventTimeList) {
		this.eventTimeList = eventTimeList;
	}

	public static void main(String[] args) {
		new NativeDiscovery().discover();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new AppLogger();
				new Thread(new OverlayFactoryTest(), "Generate_Overlay").start();

			}
		});

	}
}