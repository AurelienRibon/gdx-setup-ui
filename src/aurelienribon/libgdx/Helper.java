package aurelienribon.libgdx;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class Helper {
	private static final DocumentBuilderFactory domFactory;
	private static final XPath xpath;

	static {
		domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		xpath = XPathFactory.newInstance().newXPath();
	}

	// -------------------------------------------------------------------------
	// Project Configuration
	// -------------------------------------------------------------------------

	public static String getCorePrjName(ProjectConfiguration cfg) {
		return cfg.projectName + cfg.suffixCommon;
	}

	public static String getDesktopPrjName(ProjectConfiguration cfg) {
		return cfg.projectName + cfg.suffixDesktop;
	}

	public static String getAndroidPrjName(ProjectConfiguration cfg) {
		return cfg.projectName + cfg.suffixAndroid;
	}

	public static String getHtmlPrjName(ProjectConfiguration cfg) {
		return cfg.projectName + cfg.suffixHtml;
	}

	public static String getCorePrjPath(ProjectConfiguration cfg) {
		return FilenameUtils.normalize(cfg.destinationPath + "/" + cfg.projectName + cfg.suffixCommon + "/", true);
	}

	public static String getDesktopPrjPath(ProjectConfiguration cfg) {
		return FilenameUtils.normalize(cfg.destinationPath + "/" + cfg.projectName + cfg.suffixDesktop + "/", true);
	}

	public static String getAndroidPrjPath(ProjectConfiguration cfg) {
		return FilenameUtils.normalize(cfg.destinationPath + "/" + cfg.projectName + cfg.suffixAndroid + "/", true);
	}

	public static String getHtmlPrjPath(ProjectConfiguration cfg) {
		return FilenameUtils.normalize(cfg.destinationPath + "/" + cfg.projectName + cfg.suffixHtml + "/", true);
	}

	// -------------------------------------------------------------------------
	// Classpath
	// -------------------------------------------------------------------------

	public static boolean isFileRuntimeJar(String filename) {
		if (!filename.endsWith(".jar")) return false;
		String name = FilenameUtils.getBaseName(filename);
		if (endsWith(name, "-source", "-sources", "-src")) return false;
		return true;
	}

	public static boolean endsWith(String str, String... ends) {
		for (String end : ends) if (str.endsWith(end)) return true;
		return false;
	}

	public static String getSource(List<String> files, String file) {
		String path = FilenameUtils.getFullPath(file);
		String name = FilenameUtils.getBaseName(file);
		String ext = FilenameUtils.getExtension(file);

		if (files.contains(path + name + "-source." + ext)) return path + name + "-source." + ext;
		if (files.contains(path + name + "-sources." + ext)) return path + name + "-sources." + ext;
		if (files.contains(path + name + "-src." + ext)) return path + name + "-src." + ext;
		return null;
	}

	public static List<ClasspathEntry> getCoreClasspathEntries(ProjectConfiguration cfg, LibraryManager libs) {
		List<ClasspathEntry> classpath = new ArrayList<ClasspathEntry>();

		for (String library : cfg.libraries) {
			LibraryDef def = libs.getDef(library);

			for (String file : def.libsCommon) {
				if (!isFileRuntimeJar(file)) continue;
				String source = getSource(def.libsCommon, file);
				classpath.add(new ClasspathEntry("libs/", file, source, true));
			}
		}

		return classpath;
	}

	public static List<ClasspathEntry> getAndroidClasspathEntries(ProjectConfiguration cfg, LibraryManager libs) {
		List<ClasspathEntry> classpath = new ArrayList<ClasspathEntry>();
		String corePrjName = getCorePrjName(cfg);

		for (String library : cfg.libraries) {
			LibraryDef def = libs.getDef(library);

			for (String file : def.libsCommon) {
				if (!isFileRuntimeJar(file)) continue;
				String source = getSource(def.libsCommon, file);
				classpath.add(new ClasspathEntry("/" + corePrjName + "/libs/", file, source, true));
			}

			for (String file : def.libsAndroid) {
				if (!isFileRuntimeJar(file)) continue;
				String source = getSource(def.libsAndroid, file);
				classpath.add(new ClasspathEntry("libs/", file, source, true));
			}
		}

		return classpath;
	}

	public static List<ClasspathEntry> getDesktopClasspathEntries(ProjectConfiguration cfg, LibraryManager libs) {
		List<ClasspathEntry> classpath = new ArrayList<ClasspathEntry>();

		for (String library : cfg.libraries) {
			LibraryDef def = libs.getDef(library);

			for (String file : def.libsDesktop) {
				if (!isFileRuntimeJar(file)) continue;
				String source = getSource(def.libsDesktop, file);
				classpath.add(new ClasspathEntry("libs/", file, source, false));
			}
		}

		return classpath;
	}

	public static List<ClasspathEntry> getHtmlClasspathEntries(ProjectConfiguration cfg, LibraryManager libs) {
		List<ClasspathEntry> classpath = new ArrayList<ClasspathEntry>();
		String corePrjName = getCorePrjName(cfg);

		for (String library : cfg.libraries) {
			LibraryDef def = libs.getDef(library);

			for (String file : def.libsCommon) {
				if (!isFileRuntimeJar(file)) continue;
				String source = getSource(def.libsCommon, file);
				classpath.add(new ClasspathEntry("/" + corePrjName + "/libs/", file, source, false));
				if (source != null) classpath.add(new ClasspathEntry("/" + corePrjName + "/libs/", source, null, false));
			}

			for (String file : def.libsHtml) {
				if (!isFileRuntimeJar(file)) continue;
				String source = getSource(def.libsHtml, file);
				classpath.add(new ClasspathEntry("war/WEB-INF/lib/", file, source, false));
				if (source != null) classpath.add(new ClasspathEntry("war/WEB-INF/lib/", source, null, false));
			}
		}

		return classpath;
	}

	public static List<GwtModule> getGwtModules(ProjectConfiguration cfg, LibraryManager libs) {
		List<GwtModule> newGwtModules = new ArrayList<GwtModule>();

		for (String library : cfg.libraries) {
			LibraryDef def = libs.getDef(library);

			if (def.gwtModuleName != null) {
				newGwtModules.add(new GwtModule(def.gwtModuleName));
			}
		}

		return newGwtModules;
	}

	public static List<ClasspathEntry> getClasspathEntries(File classpathFile) {
		List<ClasspathEntry> classpath = new ArrayList<ClasspathEntry>();

		try {
			Document doc = domFactory.newDocumentBuilder().parse(classpathFile);

			NodeList nodes = (NodeList) xpath
				.compile("classpath/classpathentry[@kind='lib' and @path]")
				.evaluate(doc, XPathConstants.NODESET);

			for (int i=0; i<nodes.getLength(); i++) {
				String path = nodes.item(i).getAttributes().getNamedItem("path").getNodeValue();
				String sourcepath = nodes.item(i).getAttributes().getNamedItem("sourcepath") != null
					? nodes.item(i).getAttributes().getNamedItem("sourcepath").getNodeValue() : null;
				boolean exported = nodes.item(i).getAttributes().getNamedItem("exported") != null
					? nodes.item(i).getAttributes().getNamedItem("exported").getNodeValue().equalsIgnoreCase("true") : false;

				classpath.add(new ClasspathEntry(path, sourcepath, exported));
			}

		} catch (ParserConfigurationException ex) {
		} catch (SAXException ex) {
		} catch (IOException ex) {
		} catch (XPathExpressionException ex) {
		}

		return classpath;
	}

	public static List<GwtModule> getGwtModules(File modulesFile) {
		List<GwtModule> modules = new ArrayList<GwtModule>();

		try {
			Document doc = domFactory.newDocumentBuilder().parse(modulesFile);

			NodeList nodes = (NodeList) xpath
				.compile("module/inherits[@name]")
				.evaluate(doc, XPathConstants.NODESET);

			for (int i=0; i<nodes.getLength(); i++) {
				String name = nodes.item(i).getAttributes().getNamedItem("name").getNodeValue();
				modules.add(new GwtModule(name));
			}

		} catch (ParserConfigurationException ex) {
		} catch (SAXException ex) {
		} catch (IOException ex) {
		} catch (XPathExpressionException ex) {
		}

		return modules;
	}

	public static class ClasspathEntry implements Comparable<ClasspathEntry> {
		public String path;
		public String sourcepath;
		public boolean exported;
		public boolean overwritten = false;
		public boolean added = false;

		public ClasspathEntry(String path, String sourcepath, boolean exported) {
			this.path = path;
			this.sourcepath = sourcepath;
			this.exported = exported;
		}

		public ClasspathEntry(String preffix, String path, String sourcepath, boolean exported) {
			this.path = preffix + path;
			this.sourcepath = sourcepath != null ? preffix + sourcepath : null;
			this.exported = exported;
		}

		public void testOverwritten(List<ClasspathEntry> entries) {
			for (ClasspathEntry e : entries) if (e.path.equals(path)) {overwritten = true; return;}
		}

		public boolean testAdded(List<ClasspathEntry> entries) {
			for (ClasspathEntry e : entries) if (e.path.equals(path)) return false;
			added = true;
			return true;
		}

		@Override
		public int compareTo(ClasspathEntry o) {
			if (path.startsWith("/") && !o.path.startsWith("/")) return 1;
			if (!path.startsWith("/") && o.path.startsWith("/")) return -1;
			return path.compareTo(o.path);
		}
	}

	public static class GwtModule implements Comparable<GwtModule> {
		public String name;
		public boolean overwritten = false;
		public boolean added = false;

		public GwtModule(String name) {
			this.name = name;
		}

		public void testOverwritten(List<GwtModule> entries) {
			for (GwtModule e : entries) if (e.name.equals(name)) {overwritten = true; return;}
		}

		public boolean testAdded(List<GwtModule> entries) {
			for (GwtModule e : entries) if (e.name.equals(name)) return false;
			added = true;
			return true;
		}

		@Override
		public int compareTo(GwtModule o) {
			return name.compareTo(o.name);
		}
	}
}
