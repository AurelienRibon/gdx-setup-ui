package aurelienribon.libgdx;

import aurelienribon.utils.HttpUtils;
import aurelienribon.utils.HttpUtils.DownloadListener;
import aurelienribon.utils.HttpUtils.DownloadTask;
import aurelienribon.utils.ParseUtils;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The download manager job is to retrieve the master configuration file,
 * and to download each library definition file. It maintains a collection of
 * definition files and urls.
 *
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class DownloadManager {
	private final String configUrl;
	private final List<String> libraries = new ArrayList<String>();
	private final Map<String, String> librariesUrls = new HashMap<String, String>();
	private final Map<String, LibraryDef> librariesDefs = new HashMap<String, LibraryDef>();

	public DownloadManager(String configUrl) {
		this.configUrl = configUrl;
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	/**
	 * Asynchronously downloads the master config file and parses it to build
	 * the list of available libraries.
	 */
	public DownloadTask downloadConfigFile() {
		libraries.clear();
		librariesUrls.clear();
		librariesDefs.clear();

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		DownloadTask task =  HttpUtils.downloadAsync(configUrl, output, "Master config file");

		task.addListener(new DownloadListener() {
			@Override public void onComplete() {parseLibraries(output.toString());}
		});

		return task;
	}

	/**
	 * Asynchronously downloads the library definition file corresponding
	 * to the given name.
	 */
	public DownloadTask downloadLibraryDef(final String name) {
		if (!librariesUrls.containsKey(name)) return null;

		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		DownloadTask task =  HttpUtils.downloadAsync(librariesUrls.get(name), output, "Def '" + name + "'");

		task.addListener(new DownloadListener() {
			@Override public void onComplete() {librariesDefs.put(name, new LibraryDef(output.toString()));}
		});

		return task;
	}

	/**
	 * Manually adds a library definition file url. Used mostly for testing
	 * a library.
	 */
	public void addLibraryUrl(String name, String url) {
		libraries.add(name);
		librariesUrls.put(name, url);
	}

	/**
	 * Manually adds a library definition file. Used mostly for testing a
	 * library.
	 */
	public void addLibraryDef(String name, LibraryDef def) {
		libraries.add(name);
		librariesDefs.put(name, def);
	}

	public String getConfigUrl() {return configUrl;}
	public List<String> getLibrariesNames() {return Collections.unmodifiableList(libraries);}
	public String getLibraryUrl(String name) {return librariesUrls.get(name);}
	public LibraryDef getLibraryDef(String name) {return librariesDefs.get(name);}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private void parseLibraries(String str) {
		List<String> lines = ParseUtils.parseBlockAsList(str, "libraries");

		for (String line : lines) {
			String[] parts = line.split("=", 2);
			if (parts.length != 2) continue;

			String name = parts[0].trim();
			libraries.add(name);
			librariesUrls.put(name, parts[1].trim());
		}
	}
}
