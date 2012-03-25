package aurelienribon.libgdx.ui;

import aurelienribon.libgdx.ProjectConfiguration;
import java.awt.Component;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import org.apache.commons.io.FilenameUtils;
import res.Res;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class ResultTree extends JTree {
	private final ProjectConfiguration cfg = AppContext.inst().getConfig();
	private final Map<String, DefaultMutableTreeNode> nodes = new TreeMap<String, DefaultMutableTreeNode>();
	private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();

	public ResultTree() {
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setRootVisible(false);
		setShowsRootHandles(true);
		setCellRenderer(treeCellRenderer);

		AppContext.inst().addListener(new AppContext.Listener() {
			@Override public void configChanged() {
				update();
			}
		});

		build();
		update();
	}

	private void build() {
		try {
			ZipInputStream zis = new ZipInputStream(Res.getStream("projects.zip"));
			ZipEntry entry;

			while ((entry = zis.getNextEntry()) != null) {
				String name = entry.getName();
				name = entry.isDirectory() ? "#DIR#" + name : name;
				name = entry.isDirectory() ? name.substring(0, name.length()-1) : name;

				DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
				nodes.put(name, node);
			}

			zis.close();

			for (String name : nodes.keySet()) {
				String pName = name.startsWith("#DIR#") ? name : "#DIR#" + name;
				pName = FilenameUtils.getPath(pName);
				pName = pName.endsWith("/") ? pName.substring(0, pName.length()-1) : pName;

				DefaultMutableTreeNode node = nodes.get(name);
				DefaultMutableTreeNode pNode = nodes.get(pName);

				if (pNode != null) pNode.add(node);
				else rootNode.add(node);
			}

		} catch (IOException ex) {
		}
	}

	private void update() {
		DefaultMutableTreeNode commonPrjNode = nodes.get("#DIR#prj");
		DefaultMutableTreeNode desktopPrjNode = nodes.get("#DIR#prj-desktop");
		DefaultMutableTreeNode androidPrjNode = nodes.get("#DIR#prj-android");

		rootNode.removeAllChildren();
		rootNode.add(commonPrjNode);
		if (cfg.isDesktopIncluded()) rootNode.add(desktopPrjNode);
		if (cfg.isAndroidIncluded()) rootNode.add(androidPrjNode);

		DefaultMutableTreeNode androidSrcNode = nodes.get("#DIR#prj-android/src");
		DefaultMutableTreeNode androidSrcActivityNode = nodes.get("prj-android/src/MainActivity.java");
		DefaultMutableTreeNode previousNode = androidSrcNode;

		androidSrcNode.removeAllChildren();
		if (!cfg.getPackageName().trim().equals("")) {
			String[] paths = cfg.getPackageName().split("\\.");
			for (String path : paths) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode("#DIR#prj-android/src/" + path);
				previousNode.add(node);
				previousNode = node;
			}
			previousNode.add(androidSrcActivityNode);
		}

		setModel(new DefaultTreeModel(rootNode));
	}

	private final TreeCellRenderer treeCellRenderer = new DefaultTreeCellRenderer() {
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			if (node.getUserObject() instanceof String) {
				String name = (String) node.getUserObject();
				boolean isDir = name.startsWith("#DIR#");
				name = name.replaceFirst("#DIR#", "");

				if (isDir && name.equals("prj")) name = cfg.getCommonPrjName();
				if (isDir && name.equals("prj-desktop")) name = cfg.getDesktopPrjName();
				if (isDir && name.equals("prj-android")) name = cfg.getAndroidPrjName();

				label.setText(FilenameUtils.getName(name));
				label.setIcon(isDir ? Res.getImage("ic_folder.png") : Res.getImage("ic_file.png"));
			}

			return label;
		}
	};
}
