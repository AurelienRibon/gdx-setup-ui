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
	private final LibraryManager libs;

	public ProjectUpdate(ProjectConfiguration cfg, LibraryManager libs) {
		this.cfg = cfg;
		this.libs = libs;
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
		File commonPrjLibsDir = new File(getCommonPrjPath() + "/libs");
		File desktopPrjLibsDir = new File(getDesktopPrjPath() + "/libs");
		File androidPrjLibsDir = new File(getAndroidPrjPath() + "/libs");
		File htmlPrjLibsDir = new File(getHtmlPrjPath() + "/war/WEB-INF/lib");
		File dataDir = new File(getAndroidPrjPath() + "/assets");

		for (String library : cfg.libraries) {
			InputStream is = new FileInputStream(cfg.librariesZipPaths.get(library));
			ZipInputStream zis = new ZipInputStream(is);
			ZipEntry entry;

			LibraryDef def = libs.getDef(library);

			while ((entry = zis.getNextEntry()) != null) {
				if (entry.isDirectory()) continue;
				String entryName = entry.getName();

				for (String elemName : def.libsCommon)
					if (entryName.endsWith(elemName)) copyEntry(zis, elemName, commonPrjLibsDir);

				if (cfg.isDesktopIncluded) {
					for (String elemName : def.libsDesktop)
						if (entryName.endsWith(elemName)) copyEntry(zis, elemName, desktopPrjLibsDir);
				}

				if (cfg.isAndroidIncluded) {
					for (String elemName : def.libsAndroid)
						if (entryName.endsWith(elemName)) copyEntry(zis, elemName, androidPrjLibsDir);
					for (String elemName : def.data)
						if (entryName.endsWith(elemName)) copyEntry(zis, elemName, dataDir);
				}

				if (cfg.isHtmlIncluded) {
					for (String elemName : def.libsHtml)
						if (entryName.endsWith(elemName)) copyEntry(zis, elemName, htmlPrjLibsDir);
				}
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

		for (String library : cfg.libraries) {
			LibraryDef def = libs.getDef(library);

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

		templateManager.define("CLASSPATHENTRIES_COMMON", flatten(entriesCommon, "\t", "\n").trim());
		templateManager.define("CLASSPATHENTRIES_DESKTOP", flatten(entriesDesktop, "\t", "\n").trim());
		templateManager.define("CLASSPATHENTRIES_ANDROID", flatten(entriesAndroid, "\t", "\n").trim());
		templateManager.define("CLASSPATHENTRIES_HTML", flatten(entriesHtml, "\t", "\n").trim());
		templateManager.define("GWT_INHERITS", flatten(gwtInherits, "\t", "\n").trim());
		templateManager.processOver(new File(tmpDst, "prj-common/.classpath"));
		templateManager.processOver(new File(tmpDst, "prj-desktop/.classpath"));
		templateManager.processOver(new File(tmpDst, "prj-android/.classpath"));
		templateManager.processOver(new File(tmpDst, "prj-html/.classpath"));
		templateManager.processOver(new File(tmpDst, "prj-html/src/GwtDefinition.gwt.xml"));
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private String getCommonPrjPath() {return cfg.destinationPath + "/" + cfg.projectName + cfg.suffixCommon;}
	private String getDesktopPrjPath() {return cfg.destinationPath + "/" + cfg.projectName + cfg.suffixDesktop;}
	private String getAndroidPrjPath() {return cfg.destinationPath + "/" + cfg.projectName + cfg.suffixAndroid;}
	private String getHtmlPrjPath() {return cfg.destinationPath + "/" + cfg.projectName + cfg.suffixHtml;}

	private void copyEntry(ZipInputStream zis, String name, File dst) throws IOException {
		File file = new File(dst, name);
		file.getParentFile().mkdirs();

		OutputStream os = new FileOutputStream(file);
		IOUtils.copy(zis, os);
		os.close();
	}
}
