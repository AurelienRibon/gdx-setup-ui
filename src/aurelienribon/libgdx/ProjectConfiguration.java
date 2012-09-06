package aurelienribon.libgdx;

import java.util.HashMap;
import java.util.Map;

/**
 * Skeleton for all the parameters related to the configuration of a project.
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class ProjectConfiguration {
	public String projectName = "my-gdx-game";
	public String mainClassName = "MyGdxGame";
	public String packageName = "com.me.mygdxgame";
	public String destinationPath = "";

	public final LibraryManager libs = new LibraryManager();

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

	public static class LibraryManager {
		private final Map<String, Boolean> usages = new HashMap<String, Boolean>();
		private final Map<String, String> paths = new HashMap<String, String>();

		public void setUsage(String name, boolean used) {usages.put(name, used);}
		public void setPath(String name, String path) {paths.put(name, path);}
		public boolean isUsed(String name) {return usages.get(name) == null ? false : usages.get(name);}
		public String getPath(String name) {return paths.get(name);}
	}
}
