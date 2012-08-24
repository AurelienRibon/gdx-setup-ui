package aurelienribon.libgdx.ui;

import aurelienribon.libgdx.ui.panels.AdvancedSettingsPanel;
import aurelienribon.libgdx.ui.panels.ConfigPanel;
import aurelienribon.libgdx.ui.panels.GenerationPanel;
import aurelienribon.libgdx.ui.panels.GoPanel;
import aurelienribon.libgdx.ui.panels.LibraryInfoPanel;
import aurelienribon.libgdx.ui.panels.LibrarySetupPanel;
import aurelienribon.libgdx.ui.panels.ResultPanel;
import aurelienribon.libgdx.ui.panels.SelectionPanel;
import aurelienribon.libgdx.ui.panels.TaskPanel;
import aurelienribon.ui.components.PaintedPanel;
import aurelienribon.ui.css.Style;
import aurelienribon.utils.Animator;
import aurelienribon.utils.HttpUtils.DownloadListener;
import aurelienribon.utils.HttpUtils.DownloadTask;
import aurelienribon.utils.Res;
import aurelienribon.utils.SwingUtils;
import aurelienribon.utils.VersionLabel;
import aurelienribon.utils.slidingpanels.SlidingLayersConfig;
import aurelienribon.utils.slidingpanels.SlidingLayersConfig.Direction;
import aurelienribon.utils.slidingpanels.SlidingLayersPanel;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class MainPanel extends PaintedPanel {
	private final SelectionPanel selectionPanel = new SelectionPanel(this);
	private final ConfigPanel configPanel = new ConfigPanel(this);
	private final VersionLabel versionLabel = new VersionLabel();
	private final LibrarySetupPanel librarySetupPanel = new LibrarySetupPanel(this);
	private final ResultPanel resultPanel = new ResultPanel();
	private final GoPanel goPanel = new GoPanel(this);
	private final TaskPanel taskPanel = new TaskPanel();
	private final AdvancedSettingsPanel advancedSettingsPanel = new AdvancedSettingsPanel();
	private final LibraryInfoPanel libraryInfoPanel = new LibraryInfoPanel(this);
	private final GenerationPanel generationPanel = new GenerationPanel(this);

	private final SlidingLayersPanel panel = new SlidingLayersPanel();

	public MainPanel() {
		SwingUtils.importFont(Res.getStream("fonts/SquareFont.ttf"));
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);

		Style.registerCssClasses(this, ".rootPanel");
		Style.registerCssClasses(selectionPanel, ".groupPanel", "#selectionPanel");
		Style.registerCssClasses(configPanel, ".groupPanel", "#configPanel");
		Style.registerCssClasses(versionLabel, ".versionLabel");
		Style.registerCssClasses(librarySetupPanel, ".groupPanel", "#librarySetupPanel");
		Style.registerCssClasses(resultPanel, ".groupPanel", "#resultPanel");
		Style.registerCssClasses(goPanel, ".groupPanel", "#goPanel");
		Style.registerCssClasses(advancedSettingsPanel, ".groupPanel", "#advancedSettingsPanel");
		Style.registerCssClasses(libraryInfoPanel, ".groupPanel", "#libraryInfoPanel");
		Style.registerCssClasses(generationPanel, ".groupPanel", "#generationPanel");

		Style style = new Style(Res.getUrl("css/style.css"));
		Style.apply(this, style);
		Style.apply(selectionPanel, style);
		Style.apply(configPanel, style);
		Style.apply(versionLabel, style);
		Style.apply(librarySetupPanel, style);
		Style.apply(resultPanel, style);
		Style.apply(goPanel, style);
		Style.apply(taskPanel, style);
		Style.apply(advancedSettingsPanel, style);
		Style.apply(libraryInfoPanel, style);
		Style.apply(generationPanel, style);

		goPanel.init();
		librarySetupPanel.init();

		versionLabel.initAndCheck("3.0.0-alpha", "versions",
			"http://libgdx.badlogicgames.com/nightlies/config/config.txt",
			"http://libgdx.badlogicgames.com/download.html");

		Animator.setTweenManagersCount(2);
		Animator.start();
		panel.setTweenManager(Animator.getTweenManager(0));
		taskPanel.setTweenManager(Animator.getTweenManager(1));

		initConfigurations();
		panel.timeline().pushSet(initCfg).play();

		SwingUtils.addWindowListener(this, new WindowAdapter() {
			@Override public void windowOpened(WindowEvent e) {
				DownloadTask task = Ctx.dlManager.downloadConfigFile();
				task.addListener(configFileDownloadListener);
			}
		});
	}

	private final DownloadListener configFileDownloadListener = new DownloadListener() {
		@Override
		public void onComplete() {
			if (Ctx.testLibUrl != null) Ctx.dlManager.addLibraryUrl("__test_url__", Ctx.testLibUrl);
			if (Ctx.testLibDef != null) Ctx.dlManager.addLibraryDef("__test_def__", Ctx.testLibDef);
			if (Ctx.testLibDef != null) librarySetupPanel.registerLibrary("__test_def__");

			for (String name : Ctx.dlManager.getLibrariesNames()) {
				DownloadTask task = Ctx.dlManager.downloadLibraryDef(name);
				final String libraryName = name;

				task.addListener(new DownloadListener() {
					@Override public void onComplete() {
						Ctx.cfg.libs.add(libraryName, Ctx.dlManager.getLibraryDef(libraryName));
						Ctx.cfg.libs.setUsage(libraryName, libraryName.equals("libgdx"));
						librarySetupPanel.registerLibrary(libraryName);
					}

					@Override
					public void onError(IOException ex) {
						librarySetupPanel.registerLibrary(null);
					}
				});
			}
		}
	};

	// -------------------------------------------------------------------------
	// Configurations
	// -------------------------------------------------------------------------

	private SlidingLayersConfig initCfg, createCfg, updateCfg;
	private SlidingLayersConfig advSettingsCfg;
	private SlidingLayersConfig libraryInfoCfg;
	private SlidingLayersConfig generationCfg;

	private boolean isFirstPanelShown = true;
	private boolean isCreateSetupUsed = true;
	private boolean isGenerationPanelOpen = false;
	private String currentLibraryInfo;

	private void initConfigurations() {
		JLabel logo = new JLabel(Res.getImage("gfx/logo.png"));
		logo.setVerticalAlignment(SwingConstants.TOP);
		logo.setHorizontalAlignment(SwingConstants.LEFT);

		initCfg = new SlidingLayersConfig(panel)
			.row(false, 1).row(true, 30).column(false, 1)
			.beginGrid(0, 0)
				.row(false, 1).row(false, 1).row(false, 1)
				.column(false, 1).column(false, 2).column(false, 1)
				.tile(0, 0, logo)
				.tile(1, 1, selectionPanel)
			.end()
			.tile(1, 0, taskPanel);

		createCfg = new SlidingLayersConfig(panel)
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
			.tile(1, 0, taskPanel);

		advSettingsCfg = new SlidingLayersConfig(panel)
			.row(false, 1).column(false, 1).column(false, 2)
			.beginGrid(0, 0)
				.row(true, configPanel.getPreferredSize().height)
				.row(true, versionLabel.getPreferredSize().height)
				.column(false, 1)
				.tile(0, 0, configPanel)
				.tile(1, 0, versionLabel)
			.end()
			.tile(0, 1, advancedSettingsPanel);

		libraryInfoCfg = new SlidingLayersConfig(panel)
			.row(false, 1).row(true, 30).column(false, 1)
			.beginGrid(0, 0)
				.row(false, 1).column(false, 1).column(false, 2)
				.tile(0, 0, librarySetupPanel)
				.tile(0, 1, libraryInfoPanel)
			.end()
			.tile(1, 0, taskPanel);

		generationCfg = new SlidingLayersConfig(panel)
			.row(false, 1).column(false, 2).column(false, 1)
			.beginGrid(0, 1)
				.row(false, 1)
				.row(true, goPanel.getPreferredSize().height)
				.column(false, 1)
				.tile(0, 0, resultPanel)
				.tile(1, 0, goPanel)
			.end()
			.tile(0, 0, generationPanel);
	}

	public void showCreateSetup() {
		if (isFirstPanelShown) {
			panel.timeline()
				.pushTo(createCfg.clone()
					.hide(Direction.LEFT, configPanel, versionLabel)
					.hide(Direction.UP, librarySetupPanel)
					.hide(Direction.RIGHT, resultPanel, goPanel))
				.setDuration(0.8f)
				.pushTo(createCfg.clone()
					.delayIncr(0.05f, configPanel, librarySetupPanel, resultPanel, goPanel, versionLabel))
				.play();

		} else if (!isCreateSetupUsed) {

		}

		isCreateSetupUsed = true;
		isFirstPanelShown = false;
	}

	public void showUpdateSetup() {
		if (isFirstPanelShown) {
		} else if (isCreateSetupUsed) {
		}

		isCreateSetupUsed = false;
		isFirstPanelShown = false;
	}

	public void showAdvancedSettings() {
		panel.timeline()
			.pushTo(createCfg.clone()
				.hide(Direction.DOWN, taskPanel)
				.hide(Direction.UP, selectionPanel, librarySetupPanel)
				.hide(Direction.RIGHT, resultPanel, goPanel)
				.changeRow(0, configPanel)
				.changeRow(1, versionLabel)
				.delayIncr(0.05f, librarySetupPanel, configPanel, versionLabel, resultPanel, goPanel, taskPanel))
			.pushSet(advSettingsCfg.clone()
				.hide(Direction.RIGHT, advancedSettingsPanel))
			.pushTo(advSettingsCfg)
			.play();
	}

	public void hideAdvancedSettings() {
		panel.timeline()
			.pushTo(advSettingsCfg.clone()
				.hide(Direction.RIGHT, advancedSettingsPanel))
			.pushSet(createCfg.clone()
				.hide(Direction.DOWN, taskPanel)
				.hide(Direction.UP, selectionPanel, librarySetupPanel)
				.hide(Direction.RIGHT, resultPanel, goPanel)
				.changeRow(0, configPanel)
				.changeRow(1, versionLabel))
			.pushTo(createCfg.clone()
				.delayIncr(0.05f, versionLabel, configPanel, selectionPanel, librarySetupPanel, resultPanel, goPanel, taskPanel))
			.play();
	}

	public void showLibraryInfo(String libraryName) {
		if (currentLibraryInfo != null) {
			if (currentLibraryInfo.equals(libraryName)) {
				hideLibraryInfo();
			} else {
				currentLibraryInfo = libraryName;
				libraryInfoPanel.setup(libraryName);
			}
			return;
		}

		currentLibraryInfo = libraryName;
		libraryInfoPanel.setup(libraryName);

		panel.timeline()
			.pushTo(createCfg.clone()
				.hide(Direction.LEFT, selectionPanel, configPanel, versionLabel)
				.hide(Direction.RIGHT, resultPanel, goPanel)
				.delayIncr(0.02f, librarySetupPanel, configPanel, versionLabel, resultPanel, goPanel, versionLabel, taskPanel))
			.pushTo(libraryInfoCfg.clone()
				.hide(Direction.RIGHT, libraryInfoPanel))
			.pushTo(libraryInfoCfg)
			.play();
	}

	public void hideLibraryInfo() {
		currentLibraryInfo = null;

		panel.timeline()
			.pushTo(libraryInfoCfg.clone()
				.hide(Direction.RIGHT, libraryInfoPanel))
			.pushTo(createCfg.clone()
				.hide(Direction.LEFT, selectionPanel, configPanel, versionLabel)
				.hide(Direction.RIGHT, resultPanel, goPanel))
			.pushTo(createCfg.clone()
				.delayIncr(0.02f, librarySetupPanel, configPanel, versionLabel, resultPanel, goPanel, versionLabel, taskPanel))
			.play();
	}

	public void showGenerationPanel() {
		if (isGenerationPanelOpen) {
			hideGenerationPanel();
			return;
		}

		isGenerationPanelOpen = true;

		panel.timeline()
			.pushTo(createCfg.clone()
				.hide(Direction.DOWN, taskPanel, selectionPanel, configPanel, versionLabel, librarySetupPanel)
				.delayIncr(0.05f, librarySetupPanel, versionLabel, configPanel, selectionPanel))
			.pushTo(generationCfg.clone()
				.hide(Direction.UP, generationPanel))
			.pushTo(generationCfg)
			.play();
	}

	public void hideGenerationPanel() {
		isGenerationPanelOpen = false;

		panel.timeline()
			.pushTo(generationCfg.clone()
				.hide(Direction.DOWN, generationPanel))
			.pushTo(createCfg.clone()
				.hide(Direction.UP, selectionPanel, configPanel, versionLabel, librarySetupPanel)
				.hide(Direction.DOWN, taskPanel))
			.pushTo(createCfg.clone()
				.delayIncr(0.05f, versionLabel, configPanel, selectionPanel, librarySetupPanel, taskPanel))
			.play();
	}
}
