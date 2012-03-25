package aurelienribon.libgdx;

import aurelienribon.utils.TemplateManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import res.Res;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class ProjectSetup {
	private final ProjectConfiguration cfg;
	private final File tmpDst = new File("tmp", "projects");
	private final TemplateManager templateManager = new TemplateManager();

	public ProjectSetup(ProjectConfiguration cfg) {
		this.cfg = cfg;

		templateManager.define("PACKAGE_NAME", cfg.getPackageName());
		templateManager.define("PRJ_NAME_COMMON", cfg.getCommonPrjName());
		if (cfg.isDesktopIncluded()) templateManager.define("PRJ_NAME_DESKTOP", cfg.getDesktopPrjName());
		if (cfg.isAndroidIncluded()) templateManager.define("PRJ_NAME_ANDROID", cfg.getAndroidPrjName());
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public void inflateProjects() throws IOException {
		FileUtils.forceMkdir(tmpDst);
		FileUtils.cleanDirectory(tmpDst);

		InputStream is = Res.getStream("projects.zip");
		ZipInputStream zis = new ZipInputStream(is);
		ZipEntry entry;

		while ((entry = zis.getNextEntry()) != null) {
			File file = new File(tmpDst, entry.getName());
			if (entry.isDirectory()) {
				FileUtils.forceMkdir(file);
			} else {
				OutputStream os = new FileOutputStream(file);
				IOUtils.copy(zis, os);
				os.close();
			}
		}

		zis.close();
		postProcessInflate();
	}

	public void inflateLibrary() throws IOException {
		InputStream is = new FileInputStream(cfg.getLibraryPath());
		ZipInputStream zis = new ZipInputStream(is);
		ZipEntry entry;

		File commonPrjLibsDir = new File(new File(tmpDst, cfg.getCommonPrjName()), "libs");
		File desktopPrjLibsDir = new File(new File(tmpDst, cfg.getDesktopPrjName()), "libs");
		File androidPrjLibsDir = new File(new File(tmpDst, cfg.getAndroidPrjName()), "libs");

		while ((entry = zis.getNextEntry()) != null) {
			if (entry.isDirectory()) continue;

			String name = FilenameUtils.getName(entry.getName());
			if (name.equals("gdx.jar")) copyEntry(zis, entry, commonPrjLibsDir);
			if (name.equals("gdx-sources.jar")) copyEntry(zis, entry, commonPrjLibsDir);
			if (name.equals("gdx-natives.jar")) copyEntry(zis, entry, desktopPrjLibsDir);
			if (name.equals("gdx-backend-lwjgl.jar")) copyEntry(zis, entry, desktopPrjLibsDir);
			if (name.equals("gdx-backend-lwjgl-sources.jar")) copyEntry(zis, entry, desktopPrjLibsDir);
			if (name.equals("gdx-backend-lwjgl-natives.jar")) copyEntry(zis, entry, desktopPrjLibsDir);
			if (name.equals("gdx-backend-android.jar")) copyEntry(zis, entry, androidPrjLibsDir);
			if (name.equals("gdx-backend-android-sources.jar")) copyEntry(zis, entry, androidPrjLibsDir);
			if (entry.getName().contains("armeabi")) copyEntry(zis, entry, androidPrjLibsDir);
		}

		zis.close();
	}

	public void copy() throws IOException {
		File src = new File(tmpDst, cfg.getCommonPrjName());
		File dst = new File(cfg.getDestinationPath());
		FileUtils.copyDirectoryToDirectory(src, dst);

		if (cfg.isDesktopIncluded()) {
			src = new File(tmpDst, cfg.getDesktopPrjName());
			FileUtils.copyDirectoryToDirectory(src, dst);
		}

		if (cfg.isAndroidIncluded()) {
			src = new File(tmpDst, cfg.getAndroidPrjName());
			FileUtils.copyDirectoryToDirectory(src, dst);
		}
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private void postProcessInflate() throws IOException {
		File src = new File(tmpDst, "prj");
		File dst = new File(tmpDst, cfg.getCommonPrjName());
		templateDir(src);
		FileUtils.moveDirectory(src, dst);

		if (cfg.isDesktopIncluded()) {
			src = new File(tmpDst, "prj-desktop");
			dst = new File(tmpDst, cfg.getDesktopPrjName());
			templateDir(src);
			FileUtils.moveDirectory(src, dst);
		}

		if (cfg.isAndroidIncluded()) {
			src = new File(tmpDst, "prj-android");
			dst = new File(tmpDst, cfg.getAndroidPrjName());
			templateDir(src);

			String path1 = FilenameUtils.normalize("src/MainActivity.java");
			String path2 = FilenameUtils.normalize("src/" + cfg.getPackageName().replaceAll("\\.", "/") + "/MainActivity.java");
			FileUtils.moveFile(new File(src, path1), new File(src, path2));
			FileUtils.moveDirectory(src, dst);
		}
	}

	private void templateDir(File dir) throws IOException {
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				templateDir(file);
			} else {
				templateManager.processOver(file);
			}
		}
	}

	private void copyEntry(ZipInputStream zis, ZipEntry entry, File dst) throws IOException {
		File file = new File(dst, entry.getName());
		file.getParentFile().mkdirs();

		OutputStream os = new FileOutputStream(file);
		IOUtils.copy(zis, os);
		os.close();
	}
}
