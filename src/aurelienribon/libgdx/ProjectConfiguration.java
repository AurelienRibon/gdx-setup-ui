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

	private String rawProjectName = "My LibGDX Game !!";
	private String projectName = "MyLibGDXGame";
	private String directoryName = "my-libgdx-game";
	private String packageName = "com.me.mygame";
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

	public String getRawProjectName() {
		return rawProjectName;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getDirectoryName() {
		return directoryName;
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

	public void setRawProjectName(String rawProjectName) {
		this.rawProjectName = rawProjectName;
		this.projectName = rawProjectName.replaceAll("\\s+", "");

		if (projectName.length() > 0) {
			projectName = projectName.substring(0, 1).toUpperCase() + projectName.substring(1);
		}

		String chars = ",;:!ù*^$?./§%µ¨£¤&é\"'(-è_çà)=~#{[|`\\^@]}<>²";
		for (char c : chars.toCharArray()) projectName = projectName.replace("" + c, "");
	}

	public void setDirectoryName(String directoryName) {
		this.directoryName = directoryName;
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
		return directoryName + commonSuffix;
	}

	public String getDesktopPrjName() {
		return directoryName + desktopSuffix;
	}

	public String getAndroidPrjName() {
		return directoryName + androidSuffix;
	}

	public String getHtmlPrjName() {
		return directoryName + htmlSuffix;
	}

	public boolean isValid() {
		if (directoryName.trim().equals("")) return false;
		if (projectName.trim().equals("")) return false;
		if (packageName.trim().equals("")) return false;

		for (String libraryName : libraries) {
			String path = getLibraryPath(libraryName);
			if (path == null) return false;
			if (!path.endsWith(".zip")) return false;
			if (!new File(path).isFile()) return false;
		}

		return true;
	}
}
