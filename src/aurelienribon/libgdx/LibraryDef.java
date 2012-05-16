package aurelienribon.libgdx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class LibraryDef {
	public final String name;
	public final String description;
	public final String homepage;
	public final String gwtModuleName;
	public final String stableVersion;
	public final String stableUrl;
	public final String latestUrl;
	public final List<String> libsCommon;
	public final List<String> libsDesktop;
	public final List<String> libsAndroid;
	public final List<String> libsHtml;

	public String path = null;
	public boolean isUsed = false;

	public LibraryDef(String content) {
		this.name = parseBlock(content, "name");
		this.description = parseBlock(content, "description").replaceAll("\\s+", " ");
		this.homepage = parseBlock(content, "homepage");
		this.gwtModuleName = parseBlock(content, "gwt");
		this.stableVersion = parseBlock(content, "stable-version");
		this.stableUrl = parseBlock(content, "stable-url");
		this.latestUrl = parseBlock(content, "latest-url");
		this.libsCommon = parseBlockAsList(content, "libs-common");
		this.libsDesktop = parseBlockAsList(content, "libs-desktop");
		this.libsAndroid = parseBlockAsList(content, "libs-android");
		this.libsHtml = parseBlockAsList(content, "libs-html");
	}

	private String parseBlock(String input, String name) {
		Matcher m = Pattern.compile("\\[" + name + "\\](.*?)(\\[|$)", Pattern.DOTALL).matcher(input);
		if (m.find()) return m.group(1).trim();
		return null;
	}

	private List<String> parseBlockAsList(String input, String name) {
		Matcher m = Pattern.compile("\\[" + name + "\\](.*?)(\\[|$)", Pattern.DOTALL).matcher(input);
		if (m.find()) {
			String str = m.group(1).trim();
			List<String> lines = new ArrayList<String>(Arrays.asList(str.split("\n")));
			for (int i=lines.size()-1; i>=0; i--) {
				String line = lines.get(i).trim();
				lines.set(i, line);
				if (line.equals("")) lines.remove(i);
			}
			return Collections.unmodifiableList(lines);
		}
		return Collections.unmodifiableList(new ArrayList<String>());
	}
}
