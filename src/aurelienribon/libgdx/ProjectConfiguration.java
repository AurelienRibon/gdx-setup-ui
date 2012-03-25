package aurelienribon.libgdx;

import java.io.File;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class ProjectConfiguration {
	private String projectName = "my-libgdx-project";
	private String packageName = "my.libgdx.project";
	private String destinationPath = "";
	private String libraryPath = "";
	private boolean desktopIncluded = true;
	private boolean androidIncluded = true;
	private String commonSuffix = "";
	private String desktopSuffix = "-desktop";
	private String androidSuffix = "-android";

	public String getProjectName() {
		return projectName;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getDestinationPath() {
		return destinationPath;
	}

	public String getLibraryPath() {
		return libraryPath;
	}

	public boolean isDesktopIncluded() {
		return desktopIncluded;
	}

	public boolean isAndroidIncluded() {
		return androidIncluded;
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

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void setDestinationPath(String destinationPath) {
		this.destinationPath = destinationPath;
	}

	public void setLibraryPath(String libraryPath) {
		this.libraryPath = libraryPath;
	}

	public void setDesktopIncluded(boolean desktopIncluded) {
		this.desktopIncluded = desktopIncluded;
	}

	public void setAndroidIncluded(boolean androidIncluded) {
		this.androidIncluded = androidIncluded;
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

	public boolean isValid() {
		boolean isPrjNameValid = !projectName.trim().equals("");
		boolean isPckNameValid = !packageName.trim().equals("");
		boolean isLibZip = libraryPath.endsWith(".zip");
		boolean isLibFile = new File(libraryPath).isFile();

		return isPrjNameValid && isPckNameValid && isLibZip && isLibFile;
	}
}
