package aurelienribon.libgdx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

	public final List<String> libraries = new ArrayList<String>();
	public final Map<String, String> librariesZipPaths = new HashMap<String, String>();

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
}
