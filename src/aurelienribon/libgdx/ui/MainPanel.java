package aurelienribon.libgdx.ui;

import aurelienribon.libgdx.ui.panels.LibrarySetupPanel;
import aurelienribon.libgdx.ui.panels.SelectionPanel;
import aurelienribon.libgdx.ui.panels.ConfigPanel;
import aurelienribon.libgdx.ui.panels.ResultPanel;
import aurelienribon.libgdx.ui.panels.GoPanel;
import aurelienribon.libgdx.ui.panels.TaskPanel;
import aurelienribon.ui.components.PaintedPanel;
import aurelienribon.ui.css.Style;
import aurelienribon.utils.Res;
import aurelienribon.utils.SwingUtils;
import aurelienribon.utils.VersionLabel;
import aurelienribon.utils.slidingpanels.SlidingLayersConfig;
import aurelienribon.utils.slidingpanels.SlidingLayersConfig.Direction;
import aurelienribon.utils.slidingpanels.SlidingLayersPanel;
import aurelienribon.utils.slidingpanels.SlidingLayersPanel.Callback;
import java.awt.BorderLayout;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class MainPanel extends PaintedPanel {
	private final SelectionPanel selectionPanel = new SelectionPanel();
	private final ConfigPanel configPanel = new ConfigPanel();
	private final VersionLabel versionLabel = new VersionLabel();
	private final LibrarySetupPanel librarySetupPanel = new LibrarySetupPanel();
	private final ResultPanel resultPanel = new ResultPanel();
	private final GoPanel goPanel = new GoPanel();
	private final TaskPanel taskPanel = new TaskPanel();

	private final SlidingLayersPanel panel = new SlidingLayersPanel();
	private final SlidingLayersConfig cfg0;
	private final SlidingLayersConfig cfg1;
	private final SlidingLayersConfig cfg2;
	private final SlidingLayersConfig cfg3;

	public MainPanel() {
		SlidingLayersPanel.start();
		SwingUtils.importFont(Res.getStream("fonts/SquareFont.ttf"));
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);

		Style style = new Style(Res.getUrl("css/style.css"));
		Style.registerCssClasses(this, ".rootPanel");
		Style.registerCssClasses(selectionPanel, ".groupPanel", "#selectionPanel");
		Style.registerCssClasses(configPanel, ".groupPanel", "#configPanel");
		Style.registerCssClasses(versionLabel, ".versionLabel");
		Style.registerCssClasses(librarySetupPanel, ".groupPanel", "#librarySetupPanel");
		Style.registerCssClasses(resultPanel, ".groupPanel", "#resultPanel");
		Style.registerCssClasses(goPanel, ".groupPanel", "#goPanel");
		Style.apply(this, style);
		Style.apply(selectionPanel, style);
		Style.apply(configPanel, style);
		Style.apply(versionLabel, style);
		Style.apply(librarySetupPanel, style);
		Style.apply(resultPanel, style);
		Style.apply(goPanel, style);

		versionLabel.initAndCheck("2.0.2", "versions",
			"http://libgdx.badlogicgames.com/nightlies/config/config.txt",
			"http://libgdx.badlogicgames.com/download.html");

		cfg0 = new SlidingLayersConfig(panel)
			.hgap(300).vgap(150)
			.row(false, 1).column(false, 1)
			.tile(0, 0, selectionPanel);

		cfg1 = new SlidingLayersConfig(panel)
			.row(true, selectionPanel.getPreferredSize().height)
			.column(false, 1).column(false, 1).column(false, 1)
			.tile(0, 0, selectionPanel);

		cfg2 = new SlidingLayersConfig(panel)
			.row(false, 1).row(true, 30).column(false, 1)
			.beginGrid(0, 0)
				.row(false, 1).column(false, 1).column(false, 1).column(false, 1)
				.beginGrid(0, 0)
					.row(true, selectionPanel.getPreferredSize().height)
					.row(true, configPanel.getPreferredSize().height)
					.row(true, versionLabel.getPreferredSize().height)
					.column(false, 1)
					.tile(0, 0, selectionPanel)
					.tile(1, 0, configPanel)
					.tile(2, 0, versionLabel)
				.end()
				.beginGrid(0, 1)
					.row(false, 1)
					.column(false, 1)
					.tile(0, 0, librarySetupPanel)
				.end()
				.beginGrid(0, 2)
					.row(false, 1)
					.row(true, goPanel.getPreferredSize().height)
					.column(false, 1)
					.tile(0, 0, resultPanel)
					.tile(1, 0, goPanel)
				.end()
			.end()
			.tile(1, 0, taskPanel)
			.delayIncr(0.1f, configPanel, librarySetupPanel, resultPanel, goPanel, versionLabel, taskPanel);

		cfg3 = cfg2.clone()
			.hide(Direction.LEFT, configPanel, versionLabel)
			.hide(Direction.DOWN, taskPanel)
			.hide(Direction.UP, librarySetupPanel)
			.hide(Direction.RIGHT, resultPanel, goPanel);

		panel.force(cfg0);
		panel.playDuration(0.5f);

		selectionPanel.setCreateRunnable(new Runnable() {@Override public void run() {
			panel.playCallback(new Callback() {
				@Override public void done() {
					panel.force(cfg3);
					panel.play(cfg2);
				}
			});
			panel.play(cfg1);
		}});

		selectionPanel.setUpdateRunnable(new Runnable() {@Override public void run() {

		}});
	}
}
