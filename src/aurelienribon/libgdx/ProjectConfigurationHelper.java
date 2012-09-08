package aurelienribon.libgdx;

import org.apache.commons.io.FilenameUtils;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class ProjectConfigurationHelper {
	public static String getCommonPrjName(ProjectConfiguration cfg) {
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

	public static String getCommonPrjPath(ProjectConfiguration cfg) {
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
}
