package aurelienribon.libgdx.ui.panels;

import aurelienribon.libgdx.Helper;
import aurelienribon.libgdx.LibraryDef;
import aurelienribon.libgdx.ui.Ctx;
import aurelienribon.libgdx.ui.MainPanel;
import aurelienribon.ui.css.Style;
import aurelienribon.utils.notifications.AutoListModel;
import aurelienribon.utils.notifications.ObservableList;
import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class ClasspathsPanel extends javax.swing.JPanel {
	private final ObservableList<EntryPath> coreClasspath = new ObservableList<EntryPath>();
	private final ObservableList<EntryPath> androidClasspath = new ObservableList<EntryPath>();
	private final ObservableList<EntryPath> desktopClasspath = new ObservableList<EntryPath>();
	private final ObservableList<EntryPath> htmlClasspath = new ObservableList<EntryPath>();

    public ClasspathsPanel(final MainPanel mainPanel) {
        initComponents();

		Style.registerCssClasses(jScrollPane2, ".frame");
		Style.registerCssClasses(jScrollPane6, ".frame");
		Style.registerCssClasses(jScrollPane4, ".frame");
		Style.registerCssClasses(jScrollPane5, ".frame");
		Style.registerCssClasses(jScrollPane7, ".frame");
		Style.registerCssClasses(paintedPanel1, ".optionGroupPanel");

		coreList.setModel(new AutoListModel<EntryPath>(coreClasspath));
		androidList.setModel(new AutoListModel<EntryPath>(androidClasspath));
		desktopList.setModel(new AutoListModel<EntryPath>(desktopClasspath));
		htmlList.setModel(new AutoListModel<EntryPath>(htmlClasspath));

		coreList.setCellRenderer(listCellRenderer);
		androidList.setCellRenderer(listCellRenderer);
		desktopList.setCellRenderer(listCellRenderer);
		htmlList.setCellRenderer(listCellRenderer);

		Ctx.listeners.add(new Ctx.Listener() {
			@Override public void cfgUpdateChanged() {update();}
		});
    }

	private void update() {
		coreClasspath.clear();
		androidClasspath.clear();
		desktopClasspath.clear();
		htmlClasspath.clear();

		Map<String, List<EntryPath>> paths = new HashMap<String, List<EntryPath>>();
		paths.put(Helper.getCorePrjPath(Ctx.cfgUpdate), coreClasspath);
		paths.put(Helper.getAndroidPrjPath(Ctx.cfgUpdate), androidClasspath);
		paths.put(Helper.getDesktopPrjPath(Ctx.cfgUpdate), desktopClasspath);
		paths.put(Helper.getHtmlPrjPath(Ctx.cfgUpdate), htmlClasspath);

		for (String path : paths.keySet()) {
			File dir = new File(path);
			if (dir.isDirectory()) {
				File classpathFile = new File(dir, ".classpath");
				if (classpathFile.isFile()) populateClasspath(classpathFile, paths.get(path));
			}
		}

		List<EntryPath> newCoreClasspath = new ArrayList<EntryPath>();
		List<EntryPath> newDesktopClasspath = new ArrayList<EntryPath>();
		List<EntryPath> newAndroidClasspath = new ArrayList<EntryPath>();
		List<EntryPath> newHtmlClasspath = new ArrayList<EntryPath>();
		List<String> newGwtModules = new ArrayList<String>();

		String corePrjName = Helper.getCorePrjName(Ctx.cfgUpdate);

		for (String library : Ctx.cfgUpdate.libraries) {
			LibraryDef def = Ctx.libs.getDef(library);

			for (String file : def.libsCommon) {
				if (!isLibJar(file)) continue;
				String source = getSource(def.libsCommon, file);
				newCoreClasspath.add(new EntryPath("libs/", file, source, true));
				newAndroidClasspath.add(new EntryPath("/" + corePrjName + "/libs/", file, source, true));
				newHtmlClasspath.add(new EntryPath("/" + corePrjName + "/libs/", file, source, false));
				if (source != null) newHtmlClasspath.add(new EntryPath("/" + corePrjName + "/libs/", source, null, false));
			}

			for (String file : def.libsDesktop) {
				if (!isLibJar(file)) continue;
				String source = getSource(def.libsDesktop, file);
				newDesktopClasspath.add(new EntryPath("libs/", file, source, false));
			}

			for (String file : def.libsAndroid) {
				if (!isLibJar(file)) continue;
				String source = getSource(def.libsAndroid, file);
				newAndroidClasspath.add(new EntryPath("libs/", file, source, true));
			}

			for (String file : def.libsHtml) {
				if (!isLibJar(file)) continue;
				String source = getSource(def.libsHtml, file);
				newHtmlClasspath.add(new EntryPath("war/WEB-INF/lib/", file, source, false));
				if (source != null) newHtmlClasspath.add(new EntryPath("war/WEB-INF/lib/", source, null, false));
			}

			if (def.gwtModuleName != null) {
				newGwtModules.add(def.gwtModuleName);
			}
		}

		for (EntryPath e : coreClasspath) e.testOverwritten(newCoreClasspath);
		for (EntryPath e : androidClasspath) e.testOverwritten(newAndroidClasspath);
		for (EntryPath e : desktopClasspath) e.testOverwritten(newDesktopClasspath);
		for (EntryPath e : htmlClasspath) e.testOverwritten(newHtmlClasspath);

		for (EntryPath e : newCoreClasspath) if (e.testAdded(coreClasspath)) coreClasspath.add(e);
		for (EntryPath e : newAndroidClasspath) if (e.testAdded(androidClasspath)) androidClasspath.add(e);
		for (EntryPath e : newDesktopClasspath) if (e.testAdded(desktopClasspath)) desktopClasspath.add(e);
		for (EntryPath e : newHtmlClasspath) if (e.testAdded(htmlClasspath)) htmlClasspath.add(e);

		for (List<EntryPath> classpath : paths.values()) {
			Collections.sort(classpath, new Comparator<EntryPath>() {
				@Override
				public int compare(EntryPath o1, EntryPath o2) {
					if (o1.path.startsWith("/") && !o2.path.startsWith("/")) return 1;
					if (!o1.path.startsWith("/") && o2.path.startsWith("/")) return -1;
					return o1.path.compareTo(o2.path);
				}
			});
		}
	}

	private void populateClasspath(File classpathFile, List<EntryPath> classpath) {
		try {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setNamespaceAware(true);
			Document doc = domFactory.newDocumentBuilder().parse(classpathFile);
			XPath xpath = XPathFactory.newInstance().newXPath();

			NodeList nodes = (NodeList) xpath
				.compile("classpath/classpathentry[@kind='lib']")
				.evaluate(doc, XPathConstants.NODESET);

			for (int i=0; i<nodes.getLength(); i++) {
				if (nodes.item(i).getAttributes().getNamedItem("path") == null) continue;
				String path = nodes.item(i).getAttributes().getNamedItem("path").getNodeValue();
				String sourcepath = nodes.item(i).getAttributes().getNamedItem("sourcepath") != null
					? nodes.item(i).getAttributes().getNamedItem("sourcepath").getNodeValue() : null;
				boolean exported = nodes.item(i).getAttributes().getNamedItem("exported") != null
					? nodes.item(i).getAttributes().getNamedItem("exported").getNodeValue().equalsIgnoreCase("true") : false;

				classpath.add(new EntryPath(path, sourcepath, exported));
			}

		} catch (ParserConfigurationException ex) {
		} catch (SAXException ex) {
		} catch (IOException ex) {
		} catch (XPathExpressionException ex) {
		}
	}

	private boolean isLibJar(String file) {
		if (!file.endsWith(".jar")) return false;
		String name = FilenameUtils.getBaseName(file);
		if (endsWith(name, "-source", "-sources", "-src")) return false;
		return true;
	}

	private boolean endsWith(String str, String... ends) {
		for (String end : ends) if (str.endsWith(end)) return true;
		return false;
	}

	private String getSource(List<String> files, String file) {
		String path = FilenameUtils.getFullPath(file);
		String name = FilenameUtils.getBaseName(file);
		String ext = FilenameUtils.getExtension(file);

		if (files.contains(path + name + "-source." + ext)) return path + name + "-source." + ext;
		if (files.contains(path + name + "-sources." + ext)) return path + name + "-sources." + ext;
		if (files.contains(path + name + "-src." + ext)) return path + name + "-src." + ext;
		return null;
	}

	private String buildClasspathEntry(String file, String sourceFile, String path, boolean exported) {
		String str = "<classpathentry kind=\"lib\" ";
		if (exported) str += "exported=\"true\" ";
		str += "path=\"" + path + file + "\"";
		if (sourceFile != null) str += " sourcepath=\"" + path + sourceFile + "\"";
		str += "/>";
		return str;
	}

	// -------------------------------------------------------------------------
	// List renderer
	// -------------------------------------------------------------------------

	private final ListCellRenderer listCellRenderer = new DefaultListCellRenderer() {
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			EntryPath entryPath = (EntryPath) value;

			label.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
			label.setText(entryPath.path);

			if (entryPath.overwritten) {
				label.setForeground(new Color(0x3D5277));
			} else if (entryPath.added) {
				label.setForeground(new Color(0x008800));
			} else {
				label.setForeground(new Color(0xD1B40F));
			}

			return label;
		}
	};

	// -------------------------------------------------------------------------
	// Inner Classes
	// -------------------------------------------------------------------------

	private static class EntryPath {
		public String path;
		public String sourcepath;
		public boolean exported;
		public boolean overwritten = false;
		public boolean added = false;

		public EntryPath(String path, String sourcepath, boolean exported) {
			this.path = path;
			this.sourcepath = sourcepath;
			this.exported = exported;
		}

		public EntryPath(String preffix, String path, String sourcepath, boolean exported) {
			this.path = preffix + path;
			this.sourcepath = sourcepath != null ? preffix + sourcepath : null;
			this.exported = exported;
		}

		public void testOverwritten(List<EntryPath> entries) {
			for (EntryPath e : entries) {
				if (e.path.equals(path)) {
					overwritten = true;
					return;
				}
			}
		}

		public boolean testAdded(List<EntryPath> entries) {
			for (EntryPath e : entries) {
				if (e.path.equals(path)) {
					return false;
				}
			}
			added = true;
			return true;
		}
	}

	// -------------------------------------------------------------------------
	// Generated stuff
	// -------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cancelBtn = new javax.swing.JButton();
        validateBtn = new javax.swing.JButton();
        paintedPanel1 = new aurelienribon.ui.components.PaintedPanel();
        jLabel2 = new javax.swing.JLabel();
        deleteBtn = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        coreList = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        htmlList = new javax.swing.JList();
        jLabel6 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        androidList = new javax.swing.JList();
        jLabel5 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        desktopList = new javax.swing.JList();
        jLabel4 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        gwtList = new javax.swing.JList();
        jLabel7 = new javax.swing.JLabel();

        cancelBtn.setText("Cancel");

        validateBtn.setText("Validate");

        jLabel2.setText("<html><b>Legend</b>\n<br/>\n<font color=\"#3D5277\"><b>Blue</b></font> is an element that will be updated, <font color=\"#008800\"><b>green</b></font> is a new element (you selected a new library), and <font color=\"#D1B40F\"><b>orange</b></font> is an element that is not updated, or that is unknown.\n<br/><br/>\nPlease review your classpaths before proceeding. Specifically, you should look at the orange entries, and remove those that are not needed in your project. When updating a project, some libraries may have changed their names, leaving old entries undesirable.");
        jLabel2.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout paintedPanel1Layout = new javax.swing.GroupLayout(paintedPanel1);
        paintedPanel1.setLayout(paintedPanel1Layout);
        paintedPanel1Layout.setHorizontalGroup(
            paintedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paintedPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        paintedPanel1Layout.setVerticalGroup(
            paintedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paintedPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap())
        );

        deleteBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/gfx/ic_delete.png"))); // NOI18N
        deleteBtn.setText("Delete selected element");

        jPanel7.setLayout(new java.awt.GridLayout(1, 2, 10, 0));

        jPanel6.setLayout(new java.awt.GridLayout(2, 1, 0, 10));

        jPanel1.setOpaque(false);

        coreList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(coreList);

        jLabel1.setText("Core project classpath");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addContainerGap(170, Short.MAX_VALUE))
            .addComponent(jScrollPane2)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE))
        );

        jPanel6.add(jPanel1);

        jPanel4.setOpaque(false);

        htmlList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane6.setViewportView(htmlList);

        jLabel6.setText("Html project classpath");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addContainerGap())
            .addComponent(jScrollPane6)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel6)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE))
        );

        jPanel6.add(jPanel4);

        jPanel7.add(jPanel6);

        jPanel5.setOpaque(false);
        jPanel5.setLayout(new java.awt.GridLayout(3, 1, 0, 10));

        jPanel3.setOpaque(false);

        androidList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane5.setViewportView(androidList);

        jLabel5.setText("Android project classpath");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addContainerGap())
            .addComponent(jScrollPane5)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel3);

        jPanel2.setOpaque(false);

        desktopList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane4.setViewportView(desktopList);

        jLabel4.setText("Desktop project classpath");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addContainerGap())
            .addComponent(jScrollPane4)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel2);

        jPanel8.setOpaque(false);

        gwtList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane7.setViewportView(gwtList);

        jLabel7.setText("GWT modules");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addGap(210, 213, Short.MAX_VALUE))
            .addComponent(jScrollPane7)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel8);

        jPanel7.add(jPanel5);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(paintedPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(deleteBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(validateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelBtn))
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelBtn, validateBtn});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelBtn)
                    .addComponent(validateBtn)
                    .addComponent(deleteBtn))
                .addGap(18, 18, 18)
                .addComponent(paintedPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList androidList;
    private javax.swing.JButton cancelBtn;
    private javax.swing.JList coreList;
    private javax.swing.JButton deleteBtn;
    private javax.swing.JList desktopList;
    private javax.swing.JList gwtList;
    private javax.swing.JList htmlList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private aurelienribon.ui.components.PaintedPanel paintedPanel1;
    private javax.swing.JButton validateBtn;
    // End of variables declaration//GEN-END:variables

}
