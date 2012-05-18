package aurelienribon.libgdx;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class ProjectConfiguration {
	public String projectName = "my-gdx-game";
	public String mainClassName = "MyGdxGame";
	public String packageName = "com.me.mygdxgame";
	public String destinationPath = "";

	public final Map<String, LibraryDef> libraries = new HashMap<String, LibraryDef>();

	public boolean isDesktopIncluded = true;
	public boolean isAndroidIncluded = true;
	public boolean isHtmlIncluded = true;
	public String commonSuffix = "";
	public String desktopSuffix = "-desktop";
	public String androidSuffix = "-android";
	public String htmlSuffix = "-html";
	public String androidMinSdkVersion = "5";
	public String androidTargetSdkVersion = "15";
	public String androidMaxSdkVersion = "";

	// -------------------------------------------------------------------------

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
		if (packageName.endsWith(".")) return false;
		if (mainClassName.trim().equals("")) return false;

		for (String libraryName : libraries.keySet()) {
			if (!isLibraryValid(libraryName)) return false;
		}

		return true;
	}

	public boolean isLibraryValid(String libraryName) {
		if (!libraries.get(libraryName).isUsed) return true;
		String path = libraries.get(libraryName).path;
		if (path == null) return false;
		if (!path.endsWith(".zip")) return false;
		if (!new File(path).isFile()) return false;
		return true;
	}

	public String getErrorMessage() {
		if (projectName.trim().equals("")) return "Project name is not set.";
		if (packageName.trim().equals("")) return "Package name is not set.";
		if (packageName.endsWith(".")) return "Package name ends with a dot.";
		if (mainClassName.trim().equals("")) return "Main class name is not set.";

		for (String libraryName : libraries.keySet()) {
			if (!isLibraryValid(libraryName))
				return "At least one selected library has a missing or invalid archive.";
		}

		return "No error found";
	}
}
