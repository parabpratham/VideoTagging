package com.vid.test;

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
import com.vid.commons.SupportedColors;
import com.vid.overlay.comp.Jcomp.SpeechBubble.SpeechBubbleImageType;
import com.vid.overlay.comp.Jcomp.SpotLight.Link_type;
import com.vid.overlay.comp.master.COMPONENT_TYPE;

public class JDomParserTest {

	private Map<String, Map<String, Class<?>[]>> classMethods;
	private Map<AnnotationKey, Annotation> annotationsMap;
	
	public JDomParserTest() {
		classMethods = new HashMap<>();
		annotationsMap = new HashMap<AnnotationKey, Annotation>();
	}

	public static void main(String[] args) {

		// readXml();
		JDomParserTest domParserTest = new JDomParserTest();
		domParserTest.xmlQuery(
				"k:/Install/Study/Programming/SpringWorkspace/VideoPlayer/src/resources/overlay_xml/CustomOverlay.xml");
	}

	private Map<String, Class<?>[]> addToMethodMap(String className) {

		if (getClassMethods().get(className) == null) {
			System.out.println(className);
			Map<String, Class<?>[]> methodParameterMap = new HashMap<String, Class<?>[]>();
			try {
				Method[] methods = Class.forName(className).newInstance().getClass().getMethods();
				for (Method method : methods) {
					if (method.getName().startsWith("set")) {
						methodParameterMap.put(method.getName(), method.getParameterTypes());
					}
				}
				getClassMethods().put(className, methodParameterMap);
			} catch (Exception e) {
			}
		}
		return getClassMethods().get(className);
	}

	public Map<AnnotationKey, Annotation> xmlQuery(String fileName) {

		try {

			File inputFile = new File(fileName);

			SAXBuilder saxBuilder = new SAXBuilder();
			Document document = saxBuilder.build(inputFile);
			System.out.println("Root element :" + document.getRootElement().getName());
			Element classElement = document.getRootElement();
			System.out.println("----------------------------");

			List<Element> videoDetails = classElement.getChildren("video_details");
			Element supercarElement = videoDetails.get(0);
			System.out.println("\nCurrent Element :" + supercarElement.getName());
			Element file_name = supercarElement.getChild("file_name");
			System.out.println("file_name : " + file_name.getText().replace("\n", ""));
			Element file_hash = supercarElement.getChild("file_hash");
			System.out.println("file_name : " + file_hash.getText());
			System.out.println("----------------------------");

			List<Element> annotations = classElement.getChildren("annotation");
			for (Element annotation : annotations) {
				System.out.println(annotation.getName());
				String id = annotation.getAttributeValue("id");
				System.out.println("id " + id);
				String className = annotation.getAttributeValue("type");
				System.out.println("ClassName " + className);
				String startTime = annotation.getChildTextTrim("starttime");
				System.out.println("startTime " + startTime);
				String endTime = annotation.getChildTextTrim("endtime");
				System.out.println("endtime " + endTime);
				AnnotationKey key = new AnnotationKey(Integer.parseInt(id), Integer.parseInt(startTime),
						Integer.parseInt(endTime));
				String comp_type = annotation.getChildTextTrim("comp_type");
				System.out.println("comp_type " + comp_type);
				Element parameters = annotation.getChild("parameters");
				Annotation ann = new Annotation(Integer.parseInt(startTime), Integer.parseInt(endTime), className,
						parameters, COMPONENT_TYPE.getFromName(comp_type));
				System.out.println("parameters " + parameters);
				System.out.println("----------------------------");

				Map<String, Class<?>[]> addToMethodMap = addToMethodMap(className);

				System.out.println("----------------------------");

				if (parameters == null)
					continue;
				Object component = Class.forName(className).newInstance();
				for (Element parameter : parameters.getChildren()) {
					if (COMPONENT_TYPE.getFromName(comp_type) == COMPONENT_TYPE.JCOMPONENT) {
						try {
							Method method = component.getClass().getMethod("set" + parameter.getName(),
									addToMethodMap.get("set" + parameter.getName()));
							System.out.print(method.getName() + " ");
							Class<?>[] pType = method.getParameterTypes();
							for (int i = 0; i < pType.length; i++) {
								Object obj = parseParameter(pType[i], parameter);
								if (obj != null) {
									method.invoke(component, obj);
									System.out.println(component.getClass().getMethod("get" + parameter.getName(), null)
											.invoke(component, null));
								}
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				try {
					Method method = component.getClass().getDeclaredMethod("defineParameter");
					method.setAccessible(true);
					method.invoke(component, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
				ann.setComp(component);
				annotationsMap.put(key, ann);
				System.out.println("----------------------------");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return annotationsMap;
	}

	private Object parseParameter(Class<?> pType, Element parameter) {

		if (parameter.getTextTrim().equalsIgnoreCase(""))
			return null;

		System.out.print(pType.getCanonicalName() + " " + parameter.getName() + " " + parameter.getTextTrim() + " ");
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