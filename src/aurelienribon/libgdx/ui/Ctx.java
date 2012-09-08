package aurelienribon.libgdx.ui;

import aurelienribon.libgdx.LibraryDef;
import aurelienribon.libgdx.LibraryManager;
import aurelienribon.libgdx.ProjectConfiguration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class Ctx {
	public static enum Mode {CREATE, UPDATE}
	public static Mode mode = Mode.CREATE;

	public static final ProjectConfiguration cfgCreate = new ProjectConfiguration();
	public static final ProjectConfiguration cfgUpdate = new ProjectConfiguration();
	public static final List<Listener> listeners = new CopyOnWriteArrayList<Listener>();
	public static final LibraryManager libs = new LibraryManager("http://libgdx.googlecode.com/svn/trunk/extensions/gdx-setup-ui/config/config.txt");
	public static String testLibUrl = null;
	public static LibraryDef testLibDef = null;

	// -------------------------------------------------------------------------
	// Events
	// -------------------------------------------------------------------------

	public static class Listener {
		public void modeChanged() {}
		public void cfgCreateChanged() {}
		public void cfgUpdateChanged() {}
	}

	public static void fireModeChangedChanged() {for (Listener l : listeners) l.modeChanged();}
	public static void fireCfgCreateChanged() {for (Listener l : listeners) l.cfgCreateChanged();}
	public static void fireCfgUpdateChanged() {for (Listener l : listeners) l.cfgUpdateChanged();}
}
