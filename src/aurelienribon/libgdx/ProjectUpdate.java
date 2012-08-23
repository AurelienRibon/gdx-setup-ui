package aurelienribon.libgdx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

/**
 * Standalone class used to update an existing libgdx project.
 * Uses a ProjectConfiguration instance as parameter, and provides several
 * methods to update the sub-projects step-by-step.
 *
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class ProjectUpdate {
	private final ProjectConfiguration cfg;

	public ProjectUpdate(ProjectConfiguration cfg) {
		this.cfg = cfg;
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	/**
	 * Selected libraries are inflated from their zip files, and put in the
	 * libs folders of the projects.
	 * @throws IOException
	 */
	public void inflateLibraries() throws IOException {
		File commonPrjLibsDir = new File(cfg.destinationPath, cfg.projectName + "/libs");
		File desktopPrjLibsDir = new File(cfg.destinationPath, cfg.projectName + cfg.desktopSuffix + "/libs");
		File androidPrjLibsDir = new File(cfg.destinationPath, cfg.projectName + cfg.androidSuffix + "/libs");
		File htmlPrjLibsDir = new File(cfg.destinationPath, cfg.projectName + cfg.htmlSuffix + "/war/WEB-INF/lib");
		File dataDir = new File(cfg.destinationPath, cfg.projectName + cfg.androidSuffix + "/assets");

		for (String library : cfg.libs.getNames()) {
			if (!cfg.libs.isUsed(library)) continue;

			InputStream is = new FileInputStream(cfg.libs.getPath(library));
			ZipInputStream zis = new ZipInputStream(is);
			ZipEntry entry;

			LibraryDef def = cfg.libs.getDef(library);

			while ((entry = zis.getNextEntry()) != null) {
				if (entry.isDirectory()) continue;
				String entryName = entry.getName();

				for (String elemName : def.libsCommon)
					if (entryName.endsWith(elemName)) copyEntry(zis, elemName, commonPrjLibsDir);
				for (String elemName : def.libsDesktop)
					if (entryName.endsWith(elemName)) copyEntry(zis, elemName, desktopPrjLibsDir);
				for (String elemName : def.libsAndroid)
					if (entryName.endsWith(elemName)) copyEntry(zis, elemName, androidPrjLibsDir);
				for (String elemName : def.libsHtml)
					if (entryName.endsWith(elemName)) copyEntry(zis, elemName, htmlPrjLibsDir);
				for (String elemName : def.data)
					if (entryName.endsWith(elemName)) copyEntry(zis, elemName, dataDir);
			}

			zis.close();
		}
	}

	/**
	 * Classpaths are configurated according to the selected libraries.
	 * @throws IOException
	 */
	public void configureLibraries() throws IOException {
		List<String> entriesCommon = new ArrayList<String>();
		List<String> entriesDesktop = new ArrayList<String>();
		List<String> entriesAndroid = new ArrayList<String>();
		List<String> entriesHtml = new ArrayList<String>();
		List<String> gwtInherits = new ArrayList<String>();

		for (String library : cfg.libs.getNames()) {
			if (!cfg.libs.isUsed(library)) continue;

			LibraryDef def = cfg.libs.getDef(library);

			for (String file : def.libsCommon) {
				if (!isLibJar(file)) continue;
				String source = getSource(def.libsCommon, file);
				entriesCommon.add(buildClasspathEntry(file, source, "libs/", true));
				entriesAndroid.add(buildClasspathEntry(file, source, "/@{PRJ_COMMON_NAME}/libs/", true));
				entriesHtml.add(buildClasspathEntry(file, source, "/@{PRJ_COMMON_NAME}/libs/", false));
				if (source != null) entriesHtml.add(buildClasspathEntry(source, null, "/@{PRJ_COMMON_NAME}/libs/", false));
			}

			for (String file : def.libsDesktop) {
				if (!isLibJar(file)) continue;
				String source = getSource(def.libsDesktop, file);
				entriesDesktop.add(buildClasspathEntry(file, source, "libs/", false));
			}

			for (String file : def.libsAndroid) {
				if (!isLibJar(file)) continue;
				String source = getSource(def.libsAndroid, file);
				entriesAndroid.add(buildClasspathEntry(file, source, "libs/", true));
			}

			for (String file : def.libsHtml) {
				if (!isLibJar(file)) continue;
				String source = getSource(def.libsHtml, file);
				entriesHtml.add(buildClasspathEntry(file, source, "war/WEB-INF/lib/", false));
				if (source != null) entriesHtml.add(buildClasspathEntry(source, null, "war/WEB-INF/lib/", false));
			}

			if (def.gwtModuleName != null) {
				gwtInherits.add("<inherits name='" + def.gwtModuleName + "' />");
			}
		}

//		templateManager.define("CLASSPATHENTRIES_COMMON", flatten(entriesCommon, "\t", "\n").trim());
//		templateManager.define("CLASSPATHENTRIES_DESKTOP", flatten(entriesDesktop, "\t", "\n").trim());
//		templateManager.define("CLASSPATHENTRIES_ANDROID", flatten(entriesAndroid, "\t", "\n").trim());
//		templateManager.define("CLASSPATHENTRIES_HTML", flatten(entriesHtml, "\t", "\n").trim());
//		templateManager.define("GWT_INHERITS", flatten(gwtInherits, "\t", "\n").trim());
//		templateManager.processOver(new File(tmpDst, "prj-common/.classpath"));
//		templateManager.processOver(new File(tmpDst, "prj-desktop/.classpath"));
//		templateManager.processOver(new File(tmpDst, "prj-android/.classpath"));
//		templateManager.processOver(new File(tmpDst, "prj-html/.classpath"));
//		templateManager.processOver(new File(tmpDst, "prj-html/src/GwtDefinition.gwt.xml"));
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private void copyEntry(ZipInputStream zis, String name, File dst) throws IOException {
		File file = new File(dst, name);
		file.getParentFile().mkdirs();

		OutputStream os = new FileOutputStream(file);
		IOUtils.copy(zis, os);
		os.close();
	}

	private boolean endsWith(String str, String... ends) {
		for (String end : ends) if (str.endsWith(end)) return true;
		return false;
	}

	private boolean isLibJar(String file) {
		if (!file.endsWith(".jar")) return false;
		String name = FilenameUtils.getBaseName(file);
		if (endsWith(name, "-source", "-sources", "-src")) return false;
		return true;
	}

	private String getSource(List<String> files, String file) {
		String path = FilenameUtils.getFullPath(file);
		String name = FilenameUtils.getBaseName(file);
		String ext = FilenameUtils.getExtension(file);

		if (files.contains(path + name + "-source." + ext)) return path + name + "-source." + ext;
		if (files.contains(path + name + "-sources." + ext)) return path + name + "-sources." + ext;
		if (files.contains(path + name + "-src." + ext)) return path + name + "-src." + ext;
		return null;
	}

	private String buildClasspathEntry(String file, String sourceFile, String path, boolean exported) {
		String str = "<classpathentry kind=\"lib\" ";
		if (exported) str += "exported=\"true\" ";
		str += "path=\"" + path + file + "\"";
		if (sourceFile != null) str += " sourcepath=\"" + path + sourceFile + "\"";
		str += "/>";
		return str;
	}

	private String flatten(List<String> strs, String begin, String end) {
		String ret = "";
		for (String str : strs) ret += begin + str + end;
		return ret;
	}
}
