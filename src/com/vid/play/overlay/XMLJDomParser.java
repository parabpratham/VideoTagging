package com.vid.play.overlay;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import com.vid.commons.Fonts;
import com.vid.commons.Helper;
import com.vid.commons.SupportedColors;
import com.vid.execute.AppLogger;
import com.vid.log.trace.overlay.OverlayLog;
import com.vid.overlay.comp.Jcomp.SpeechBubble.SpeechBubbleImageType;
import com.vid.overlay.comp.Jcomp.SpotLight.Link_type;
import com.vid.overlay.comp.Scomp.CustomStaticComponent;
import com.vid.overlay.comp.master.COMPONENT_TYPE;
import com.vid.overlay.comp.master.SHAPE_TYPE;
import com.vid.test.JDomParserTest;

/**
 * @author hp
 * 
 *         This class is used to parse the information from the xmlfile and
 *         create a Annotationlist of the present xml files.
 *
 */
public class XMLJDomParser {

	private Map<String, Map<String, Class<?>[]>> classMethods;
	private Map<AnnotationKey, Annotation> annotationsMap;

	private final OverlayLog logger = AppLogger.getOverlayLog();

	public XMLJDomParser() {
		classMethods = new HashMap<>();
		annotationsMap = new HashMap<AnnotationKey, Annotation>();
	}

	public static void main(String[] args) {

		// readXml();
		new AppLogger();
		JDomParserTest domParserTest = new JDomParserTest();
		domParserTest.xmlQuery(
				"k:/Install/Study/Programming/SpringWorkspace/VideoPlayer/src/resources/overlay_xml/CustomOverlay.xml");
	}

	/**
	 * @param ClassName
	 * @return MethodParameterMap Map<MethodName, Parameters : Class<?>[]>
	 * 
	 *         <p>
	 *         A sort of cache for the CustomComponent classes setter
	 *         metthods.Stores the method name against the parameters for
	 *         invoking the methods later.
	 *         </p>
	 */
	private Map<String, Class<?>[]> addToMethodMap(String className) {

		if (getClassMethods().get(className) == null) {
			// System.out.println(className);
			logger.trace(className);
			Map<String, Class<?>[]> methodParameterMap = new HashMap<String, Class<?>[]>();
			try {
				Method[] methods = Class.forName(className).newInstance().getClass().getMethods();
				for (Method method : methods) {
					if (method.getName().startsWith("set")) {
						methodParameterMap.put(method.getName(), method.getParameterTypes());
						/*
						 * logger.trace( + " " + getClassMethods() + " " +
						 * method.getName());
						 */
					}
				}
				getClassMethods().put(className, methodParameterMap);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		return getClassMethods().get(className);
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

	/**
	 * @param fileName
	 * @return
	 */
	/**
	 * @param fileName
	 * @return
	 */
	public Map<AnnotationKey, Annotation> xmlQuery(String fileName) {

		try {

			File inputFile = new File(fileName);
			logger.trace("Fetching elements for :" + fileName);
			SAXBuilder saxBuilder = new SAXBuilder();
			Document document = saxBuilder.build(inputFile);
			// Systm.out.println("Root element :" +
			// document.getRootElement().getName());
			logger.trace("Root element :" + document.getRootElement().getName());
			Element classElement = document.getRootElement();
			// System.out.println("----------------------------");
			logger.trace("----------------------------");
			List<Element> videoDetails = classElement.getChildren("video_details");
			Element supercarElement = videoDetails.get(0);
			// System.out.println("\nCurrent Element :" +
			// supercarElement.getName());
			logger.trace("Current Element :" + supercarElement.getName());
			Element file_name = supercarElement.getChild("file_name");
			// System.out.println("file_name : " +
			// file_name.getText().replace("\n", ""));
			logger.trace(file_name.getText().replace("\n", ""));
			Element file_hash = supercarElement.getChild("file_hash");
			// System.out.println("file_name : " + file_hash.getText());
			logger.trace("file_name : " + file_hash.getText());
			// System.out.println("----------------------------");
			logger.trace("----------------------------");

			List<Element> annotations = classElement.getChildren("annotation");
			for (Element annotation : annotations) {
				// System.out.println(annotation.getName());
				logger.trace(" " + annotation.getName());
				String id = annotation.getAttributeValue("id");
				// System.out.println("id " + id);
				logger.trace(" id " + id);
				String className = annotation.getAttributeValue("type");
				// System.out.println("ClassName " + className);
				logger.trace(" ClassName " + className);
				String startTime = annotation.getChildTextTrim("starttime");
				// System.out.println("startTime " + startTime);
				logger.trace(" StartTime " + startTime);
				String endTime = annotation.getChildTextTrim("endtime");
				// System.out.println("endtime " + endTime);
				logger.trace(" Endtime " + endTime);
				AnnotationKey key = new AnnotationKey(Integer.parseInt(id), Integer.parseInt(startTime),
						Integer.parseInt(endTime));
				String comp_type = annotation.getChildTextTrim("comp_type");
				// System.out.println("comp_type " + comp_type);
				logger.trace(" Comp_type " + comp_type);
				Element parameters = annotation.getChild("parameters");
				Annotation ann = new Annotation(Integer.parseInt(startTime), Integer.parseInt(endTime), className,
						parameters, COMPONENT_TYPE.getFromName(comp_type));
				// System.out.println("parameters " + parameters);
				logger.trace(" Parameters " + parameters);
				// System.out.println("----------------------------");
				logger.trace("----------------------------");
				// System.out.println("----------------------------");
				logger.trace("----------------------------");

				Map<String, Class<?>[]> addToMethodMap = addToMethodMap(className);
				if (parameters == null)
					continue;

				Object component = Class.forName(className).newInstance();
				if (COMPONENT_TYPE.getFromName(comp_type) == COMPONENT_TYPE.JCOMPONENT) {
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
									// System.out.println(component.getClass().getMethod("get"
									// + parameter.getName(), null)
									// .invoke(component, null));
								}
							}
						}
						// Invoke the defineParameter methods to set the
						// remaining
						// parameters for each component
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
						// System.out.println(shape);
						SHAPE_TYPE fromName = SHAPE_TYPE.getFromName(shape);
						((CustomStaticComponent) component).setShape_TYPE(fromName);
						((CustomStaticComponent) component).setParameter(parameter);
						((CustomStaticComponent) component)
								.setId(annotation.getChild("static_component").getAttribute("id").getValue());
					}
				}

				ann.setComp(component);
				annotationsMap.put(key, ann);
				// System.out.println("----------------------------");
				logger.trace("----------------------------");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return annotationsMap;

	}

	/**
	 * @param Class<?>
	 *            pType
	 * @param XMLElement
	 *            parameter
	 * @return InstanceOfTheComponent
	 * 
	 *         Depending upon the component_type generates the appropriate
	 *         instance of the parameters to be passed for invoking the setter
	 *         methods
	 * 
	 */
	private Object parseParameter(Class<?> pType, Element parameter) {

		if (parameter.getTextTrim().equalsIgnoreCase(""))
			return null;

		// System.out.print(pType.getCanonicalName() + " " + parameter.getName()
		// + " " + parameter.getTextTrim() + " ");
		Object newInstance = null;
		try {

			if (pType.getCanonicalName().equalsIgnoreCase(Integer.class.getCanonicalName())) {
				newInstance = Integer.parseInt(parameter.getTextTrim());
			} else if (pType.getCanonicalName().equalsIgnoreCase(String.class.getCanonicalName())) {
				newInstance = parameter.getTextTrim();
			} else if (pType.getCanonicalName().equalsIgnoreCase(Color.class.getCanonicalName())) {
				String[] colorDetails = parameter.getTextTrim().split(",");
				Color color;
				try {
					Field field = Class.forName(Color.class.getCanonicalName()).getField(colorDetails[0]);
					color = (Color) field.get(null);
				} catch (Exception e) {
					color = null; // Not defined
					logger.error("Color not defined " + e.getMessage());
				}
				newInstance = new SupportedColors(color, Integer.parseInt(colorDetails[1]));
			} else if (pType.getCanonicalName().equalsIgnoreCase(Font.class.getCanonicalName())) {

				if (parameter.getTextTrim() == null || parameter.getTextTrim().equalsIgnoreCase(""))
					newInstance = Fonts.getAppFont();
				else
					newInstance = Fonts.getFont(parameter.getTextTrim(), Font.PLAIN, 18);
			} else if (pType.getCanonicalName().equalsIgnoreCase(Image.class.getCanonicalName())) {
				newInstance = new ImageIcon(getClass().getResource(parameter.getTextTrim())).getImage();
			} else if (pType.getCanonicalName().equalsIgnoreCase(Link_type.class.getCanonicalName())) {
				newInstance = Link_type.getFromName(parameter.getTextTrim());
			} else if (pType.getCanonicalName().equalsIgnoreCase(SpeechBubbleImageType.class.getCanonicalName())) {
				newInstance = SpeechBubbleImageType.getFromName(parameter.getTextTrim());
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}

		return newInstance;
	}

	public class Annotation implements Comparable<Annotation> {

		int startTime;
		int endTime;
		Element parameters;
		String className;
		Object comp;
		COMPONENT_TYPE component_TYPE;

		public Annotation(int startTime, int endTime, String className, Element parameters,
				COMPONENT_TYPE component_TYPE) {
			this.startTime = startTime;
			this.endTime = endTime;
			this.parameters = parameters;
			this.className = className;
			this.component_TYPE = component_TYPE;
		}

		@Override
		public int compareTo(Annotation a) {
			return this.startTime - a.startTime;
		}

		public Object getComp() {
			return comp;
		}

		public void setComp(Object comp) {
			this.comp = comp;
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

	public Map<String, Map<String, Class<?>[]>> getClassMethods() {
		return classMethods;
	}

	public void setClassMethods(Map<String, Map<String, Class<?>[]>> classMethods) {
		this.classMethods = classMethods;
	}

	public Map<AnnotationKey, Annotation> getAnnotationsMap() {
		return annotationsMap;
	}

	public void setAnnotationsMap(Map<AnnotationKey, Annotation> annotationsMap) {
		this.annotationsMap = annotationsMap;
	}

}
/*
 * List<Element> supercarList = classElement.getChildren("supercars");
 * System.out.println("----------------------------");
 *
 * for (int temp = 0; temp < supercarList.size(); temp++) { Element
 * supercarElement = supercarList.get(temp); System.out.println(
 * "\nCurrent Element :" + supercarElement.getName()); Attribute attribute =
 * supercarElement.getAttribute("company"); System.out.println("company : " +
 * attribute.getValue() ); List<Element> carNameList =
 * supercarElement.getChildren("carname"); for (int count = 0; count <
 * carNameList.size(); count++) { Element carElement = carNameList.get(count);
 * System.out.print("car name : "); System.out.println(carElement.getText());
 * System.out.print("car type : "); Attribute typeAttribute =
 * carElement.getAttribute("type"); if(typeAttribute !=null)
 * System.out.println(typeAttribute.getValue()); else{ System.out.println(""); }
 * } }
 * 
 * private static void readXml() { try {
 * 
 * File inputFile = new
 * File("/root/workspace/XMLParser/src/resources/overlay_xml/CustomOverlay.xml")
 * ;
 * 
 * SAXBuilder saxBuilder = new SAXBuilder();
 * 
 * Document document = saxBuilder.build(inputFile);
 * 
 * System.out.println("Root element :" + document.getRootElement().getName());
 * 
 * Element classElement = document.getRootElement();
 * 
 * List<Element> studentList = classElement.getChildren();
 * System.out.println("----------------------------");
 * 
 * for (int temp = 0; temp < studentList.size(); temp++) { Element student =
 * studentList.get(temp); System.out.println("\nCurrent Element :" +
 * student.getName()); Attribute attribute = student.getAttribute("rollno");
 * System.out.println("Student roll no : " + attribute.getValue());
 * System.out.println("First Name : " +
 * student.getChild("firstname").getText()); System.out.println("Last Name : " +
 * student.getChild("lastname").getText()); System.out.println("Nick Name : " +
 * student.getChild("nickname").getText()); System.out.println("Marks : " +
 * student.getChild("marks").getText()); } } catch (JDOMException e) {
 * e.printStackTrace(); } catch (IOException ioe) { ioe.printStackTrace(); } }
 * 
 */