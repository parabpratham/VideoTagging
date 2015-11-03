package com.vid.play.overlay;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import com.vid.commons.Helper;
import com.vid.execute.AppLogger;
import com.vid.log.trace.overlay.OverlayLog;
import com.vid.overlay.comp.Jcomp.CustomJComponent;
import com.vid.overlay.comp.Scomp.CustomStaticComponent;
import com.vid.overlay.comp.master.COMPONENT_TYPE;
import com.vid.overlay.comp.master.SHAPE_TYPE;
import com.vid.test.JDomParserTest;

/**
 * @author pratham
 * 
 *         This class is used to parse the information from the xmlfile and
 *         create a Annotationlist of the present xml files.
 *
 */
public class XMLJDomParser {

	private Map<AnnotationKey, Annotation> annotationsMap;

	private final OverlayLog logger = AppLogger.getOverlayLog();

	public XMLJDomParser() {
		annotationsMap = new HashMap<AnnotationKey, Annotation>();
	}

	/**
	 * @param fileName
	 * @return Map<AnnotationKey, Annotation>
	 * 
	 *         This method is used for reading the annotation from the XML file
	 *         and then generates a Annotation class object which holds the
	 *         start_time, end_time and instance of the actual component
	 *         class(Jcomponent) or informations regarding the parameters in
	 *         case of the static components.
	 * 
	 *         The map generated is consumed by the Overlay_genrated_factory to
	 *         create the Overlays to be displayed at run time
	 * 
	 */

	public Map<AnnotationKey, Annotation> xmlQuery(String fileName) {

		try {

			File inputFile = new File(fileName);
			logger.trace("Fetching elements for :" + fileName);
			SAXBuilder saxBuilder = new SAXBuilder();
			Document document = saxBuilder.build(inputFile);
			logger.trace("Root element :" + document.getRootElement().getName());
			Element classElement = document.getRootElement();
			logger.trace("----------------------------");
			List<Element> videoDetails = classElement.getChildren("video_details");
			Element supercarElement = videoDetails.get(0);
			logger.trace("Current Element :" + supercarElement.getName());
			Element file_name = supercarElement.getChild("file_name");
			logger.trace(file_name.getText().replace("\n", ""));
			Element file_hash = supercarElement.getChild("file_hash");
			logger.trace("file_name : " + file_hash.getText());
			logger.trace("----------------------------");

			List<Element> annotations = classElement.getChildren("annotation");
			for (Element annotation : annotations) {
				logger.trace(" " + annotation.getName());
				String id = annotation.getAttributeValue("id");
				logger.trace(" id " + id);
				String className = annotation.getAttributeValue("type");
				logger.trace(" ClassName " + className);
				String startTime = annotation.getChildTextTrim("starttime");
				logger.trace(" StartTime " + startTime);
				String endTime = annotation.getChildTextTrim("endtime");
				logger.trace(" Endtime " + endTime);
				AnnotationKey key = new AnnotationKey(Integer.parseInt(id), Integer.parseInt(startTime),
						Integer.parseInt(endTime));
				String comp_type = annotation.getChildTextTrim("comp_type");
				logger.trace(" Comp_type " + comp_type);
				Element parameters = annotation.getChild("parameters");

				Element shape_type = annotation.getChild("static_component");
				SHAPE_TYPE shape_TYPE = null;
				if (shape_type != null)
					shape_TYPE = SHAPE_TYPE
							.getFromName(annotation.getChild("static_component").getAttribute("type").getValue());

				Annotation ann = new Annotation(Integer.parseInt(id), Integer.parseInt(startTime),
						Integer.parseInt(endTime), className, parameters, COMPONENT_TYPE.getFromName(comp_type),
						shape_TYPE);
				logger.trace(" Parameters " + parameters);
				logger.trace("----------------------------");

				// Commented as to move to the customoverlay
				// createComponent(parameters, annotation, className, comp_type,
				// id);
				// ann.setComp(component);

				annotationsMap.put(key, ann);
				logger.trace("----------------------------");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return annotationsMap;

	}

	// Old method changed statergy to create the components at runtime
	private Object createComponent(Element parameters, Element annotation, String className, String comp_type,
			String id) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Map<String, Class<?>[]> addToMethodMap = Helper.addToMethodMap(className);
		Object component = Class.forName(className).newInstance();
		if (COMPONENT_TYPE.getFromName(comp_type) == COMPONENT_TYPE.JCOMPONENT) {
			((CustomJComponent) component).setId(id);
			try {
				for (Element parameter : parameters.getChildren()) {

					// Invoke setter methods for the JCOMPONENT
					Method method = component.getClass().getMethod("set" + parameter.getName(),
							addToMethodMap.get("set" + parameter.getName()));
					Class<?>[] pType = method.getParameterTypes();
					for (int i = 0; i < pType.length; i++) {
						Object obj = Helper.parseParameter(pType[i], parameter);
						if (obj != null) {
							method.invoke(component, obj);
						}
					}
				}
				// Invoke the defineParameter methods to set the
				Method method1 = component.getClass().getDeclaredMethod("defineParameter");
				method1.setAccessible(true);
				method1.invoke(component, null);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		} else if (COMPONENT_TYPE.getFromName(comp_type) == COMPONENT_TYPE.SHAPE) {
			Element parameter = annotation.getChild("parameters");
			if (component instanceof CustomStaticComponent) {
				((CustomStaticComponent) component).setParameter(parameter);
				String shape = annotation.getChild("static_component").getAttribute("type").getValue();
				SHAPE_TYPE fromName = SHAPE_TYPE.getFromName(shape);
				((CustomStaticComponent) component).setShape_TYPE(fromName);
				((CustomStaticComponent) component).setParameter(parameter);
				((CustomStaticComponent) component).setId(id);
			}
		}

		return component;
	}

	public class Annotation implements Comparable<Annotation> {

		private int id;
		private int startTime;
		private int endTime;
		private Element parameters;
		private String className;
		private COMPONENT_TYPE component_TYPE;
		private SHAPE_TYPE shape_type;

		public Annotation(int id, int startTime, int endTime, String className, Element parameters,
				COMPONENT_TYPE component_TYPE, SHAPE_TYPE shape_TYPE) {
			this.id = id;
			this.startTime = startTime;
			this.endTime = endTime;
			this.parameters = parameters;
			this.className = className;
			this.shape_type = shape_TYPE;
			this.component_TYPE = component_TYPE;
		}

		@Override
		public int compareTo(Annotation a) {
			return this.startTime - a.startTime;
		}

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

		public COMPONENT_TYPE getComponent_TYPE() {
			return component_TYPE;
		}

		public void setComponent_TYPE(COMPONENT_TYPE component_TYPE) {
			this.component_TYPE = component_TYPE;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public Element getParameters() {
			return parameters;
		}

		public void setParameters(Element parameters) {
			this.parameters = parameters;
		}

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		public SHAPE_TYPE getShape_type() {
			return shape_type;
		}

		public void setShape_type(SHAPE_TYPE shape_type) {
			this.shape_type = shape_type;
		}
	}

	public class AnnotationKey implements Comparable<AnnotationKey> {
		int id;
		int startTime;
		int endTime;
		boolean isChecked = false;

		public AnnotationKey(int id, int startTime, int endTime) {
			this.id = id;
			this.startTime = startTime;
			this.endTime = endTime;
		}

		@Override
		public int compareTo(AnnotationKey a) {
			return this.startTime - a.startTime;
		}

		public boolean equals(Object other) {
			if (!(other instanceof AnnotationKey)) {
				return false;
			}
			AnnotationKey key = (AnnotationKey) other;
			return key.id == id && key.startTime == startTime && key.endTime == endTime;
		}

		public int hashCode() {
			return id * 37 + startTime;
		}

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

		public boolean isChecked() {
			return isChecked;
		}

		public void setChecked(boolean isChecked) {
			this.isChecked = isChecked;
		}
	}

	public Map<AnnotationKey, Annotation> getAnnotationsMap() {
		return annotationsMap;
	}

	public void setAnnotationsMap(Map<AnnotationKey, Annotation> annotationsMap) {
		this.annotationsMap = annotationsMap;
	}

	public static void main(String[] args) {

		// readXml();
		new AppLogger();
		JDomParserTest domParserTest = new JDomParserTest();
		domParserTest.xmlQuery(
				"k:/Install/Study/Programming/SpringWorkspace/VideoPlayer/src/resources/overlay_xml/CustomOverlay.xml");
	}
}