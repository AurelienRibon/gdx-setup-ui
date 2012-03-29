package aurelienribon.libgdx;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class ProjectConfiguration {
	private final List<String> libraries = new ArrayList<String>();
	private final Map<String, LibraryDef> libraryDefs = new HashMap<String, LibraryDef>();
	private final Map<String, String> libraryPaths = new HashMap<String, String>();

	private String projectName = "my-libgdx-project";
	private String packageName = "my.libgdx.project";
	private String destinationPath = "";
	private boolean desktopIncluded = true;
	private boolean androidIncluded = true;
	private boolean htmlIncluded = true;
	private String commonSuffix = "";
	private String desktopSuffix = "-desktop";
	private String androidSuffix = "-android";
	private String htmlSuffix = "-html";

	// -------------------------------------------------------------------------

	public List<String> getLibraryNames() {
		return Collections.unmodifiableList(libraries);
	}

	public String getLibraryPath(String libraryName) {
		return libraryPaths.get(libraryName);
	}

	public LibraryDef getLibraryDef(String libraryName) {
		return libraryDefs.get(libraryName);
	}

	public void registerLibrary(String name, LibraryDef def) {
		libraries.add(name);
		libraryDefs.put(name, def);
	}

	public void setLibraryPath(String libraryName, String libraryPath) {
		libraryPaths.put(libraryName, libraryPath);
	}

	// -------------------------------------------------------------------------

	public String getProjectName() {
		return projectName;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getDestinationPath() {
		return destinationPath;
	}

	public boolean isDesktopIncluded() {
		return desktopIncluded;
	}

	public boolean isAndroidIncluded() {
		return androidIncluded;
	}

	public boolean isHtmlIncluded() {
		return htmlIncluded;
	}

	public String getCommonSuffix() {
		return commonSuffix;
	}

	public String getDesktopSuffix() {
		return desktopSuffix;
	}

	public String getAndroidSuffix() {
		return androidSuffix;
	}

	public String getHtmlSuffix() {
		return htmlSuffix;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void setDestinationPath(String destinationPath) {
		this.destinationPath = destinationPath;
	}

	public void setDesktopIncluded(boolean desktopIncluded) {
		this.desktopIncluded = desktopIncluded;
	}

	public void setAndroidIncluded(boolean androidIncluded) {
		this.androidIncluded = androidIncluded;
	}

	public void setHtmlIncluded(boolean htmlIncluded) {
		this.htmlIncluded = htmlIncluded;
	}

	public void setCommonSuffix(String commonSuffix) {
		this.commonSuffix = commonSuffix;
	}

	public void setDesktopSuffix(String desktopSuffix) {
		this.desktopSuffix = desktopSuffix;
	}

	public void setAndroidSuffix(String androidSuffix) {
		this.androidSuffix = androidSuffix;
	}

	public String getCommonPrjName() {
		return projectName + commonSuffix;
	}

	public String getDesktopPrjName() {
		return projectName + desktopSuffix;
	}

	public String getAndroidPrjName() {
		return projectName + androidSuffix;
	}

	public String getHtmlPrjName() {
		return projectName + htmlSuffix;
	}

	public boolean isValid() {
		if (projectName.trim().equals("")) return false;
		if (packageName.trim().equals("")) return false;

		for (String path : libraryPaths.values()) {
			if (!path.endsWith(".zip")) return false;
			if (!new File(path).isFile()) return false;
		}

		return true;
	}
}
