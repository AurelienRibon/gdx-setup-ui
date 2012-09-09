package aurelienribon.libgdx.ui;

import aurelienribon.libgdx.LibraryDef;
import aurelienribon.libgdx.ui.panels.AdvancedSettingsPanel;
import aurelienribon.libgdx.ui.panels.ClasspathsPanel;
import aurelienribon.libgdx.ui.panels.ConfigSetupPanel;
import aurelienribon.libgdx.ui.panels.ConfigUpdatePanel;
import aurelienribon.libgdx.ui.panels.GoPanel;
import aurelienribon.libgdx.ui.panels.LibraryInfoPanel;
import aurelienribon.libgdx.ui.panels.LibrarySelectionPanel;
import aurelienribon.libgdx.ui.panels.PreviewPanel;
import aurelienribon.libgdx.ui.panels.ProcessSetupPanel;
import aurelienribon.libgdx.ui.panels.ProcessUpdatePanel;
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
import org.apache.commons.io.IOUtils;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class MainPanel extends PaintedPanel {
	private final SelectionPanel selectionPanel = new SelectionPanel(this);
	private final ConfigSetupPanel configSetupPanel = new ConfigSetupPanel(this);
	private final ConfigUpdatePanel configUpdatePanel = new ConfigUpdatePanel(this);
	private final VersionLabel versionLabel = new VersionLabel();
	private final LibrarySelectionPanel librarySelectionPanel = new LibrarySelectionPanel(this);
	private final PreviewPanel previewPanel = new PreviewPanel();
	private final GoPanel goPanel = new GoPanel(this);
	private final TaskPanel taskPanel = new TaskPanel();
	private final AdvancedSettingsPanel advancedSettingsPanel = new AdvancedSettingsPanel();
	private final LibraryInfoPanel libraryInfoPanel = new LibraryInfoPanel(this);
	private final ClasspathsPanel classpathsPanel = new ClasspathsPanel(this);
	private final ProcessSetupPanel processSetupPanel = new ProcessSetupPanel(this);
	private final ProcessUpdatePanel processUpdatePanel = new ProcessUpdatePanel(this);

	private final SlidingLayersPanel panel = new SlidingLayersPanel();

	public MainPanel() {
		SwingUtils.importFont(Res.getStream("fonts/SquareFont.ttf"));
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);

		Style.registerCssClasses(this, ".rootPanel");
		Style.registerCssClasses(selectionPanel, ".groupPanel", "#selectionPanel");
		Style.registerCssClasses(configSetupPanel, ".groupPanel", "#configSetupPanel");
		Style.registerCssClasses(configUpdatePanel, ".groupPanel", "#configUpdatePanel");
		Style.registerCssClasses(versionLabel, ".versionLabel");
		Style.registerCssClasses(librarySelectionPanel, ".groupPanel", "#librarySetupPanel");
		Style.registerCssClasses(previewPanel, ".groupPanel", "#previewPanel");
		Style.registerCssClasses(goPanel, ".groupPanel", "#goPanel");
		Style.registerCssClasses(advancedSettingsPanel, ".groupPanel", "#advancedSettingsPanel");
		Style.registerCssClasses(libraryInfoPanel, ".groupPanel", "#libraryInfoPanel");
		Style.registerCssClasses(classpathsPanel, ".groupPanel", "#classpathsPanel");
		Style.registerCssClasses(processSetupPanel, ".groupPanel", "#processSetupPanel");
		Style.registerCssClasses(processUpdatePanel, ".groupPanel", "#processUpdatePanel");

		Object[] targets = new Object[] {
			this, selectionPanel, configSetupPanel, configUpdatePanel, versionLabel,
			librarySelectionPanel, previewPanel, goPanel, taskPanel, advancedSettingsPanel,
			libraryInfoPanel, classpathsPanel, processSetupPanel, processUpdatePanel
		};

		Style style = new Style(Res.getUrl("css/style.css"));
		for (Object t : targets) Style.apply(t, style);

		try {
			String rawDef = IOUtils.toString(Res.getStream("libgdx.txt"));
			LibraryDef def = new LibraryDef(rawDef);
			Ctx.libs.addDef("libgdx", def);
			Ctx.cfgSetup.libraries.add("libgdx");
			Ctx.cfgUpdate.libraries.add("libgdx");
			librarySelectionPanel.initializeLibgdx();
		} catch (IOException ex) {
			assert false;
		}

		versionLabel.initAndCheck("3.0.0-beta", "versions",
			"http://libgdx.badlogicgames.com/nightlies/config/config.txt",
			"http://libgdx.badlogicgames.com/download.html");

		Animator.setTweenManagersCount(2);
		Animator.start();
		panel.setTweenManager(Animator.getTweenManager(0));
		taskPanel.setTweenManager(Animator.getTweenManager(1));

		initConfigurations();
		panel.timeline().pushSet(initCfg).play();

		SwingUtils.addWindowListener(this, new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				DownloadTask task = Ctx.libs.downloadConfigFile();
				task.addListener(configFileDownloadListener);
			}

			@Override
			public void windowClosing(WindowEvent e) {
				Ctx.libs.cleanUpDownloads();
			}
		});
	}

	private final DownloadListener configFileDownloadListener = new DownloadListener() {
		@Override
		public void onComplete() {
			if (Ctx.testLibUrl != null) Ctx.libs.addUrl("__test_url__", Ctx.testLibUrl);
			if (Ctx.testLibDef != null) Ctx.libs.addDef("__test_def__", Ctx.testLibDef);
			if (Ctx.testLibDef != null) librarySelectionPanel.registerLibrary("__test_def__");

			for (String name : Ctx.libs.getNames()) {
				DownloadTask task = Ctx.libs.downloadDef(name);
				final String libraryName = name;

				task.addListener(new DownloadListener() {
					@Override public void onComplete() {
						librarySelectionPanel.registerLibrary(libraryName);
					}

					@Override
					public void onError(IOException ex) {
						librarySelectionPanel.registerLibrary(null);
					}
				});
			}
		}
	};

	public void launchUpdateProcess() {
		processUpdatePanel.launch();
	}

	// -------------------------------------------------------------------------
	// Configurations
	// -------------------------------------------------------------------------

	private SlidingLayersConfig initCfg, setupCfg, updateCfg;
	private SlidingLayersConfig libraryInfoCfg;
	private SlidingLayersConfig setupAdvSettingsCfg;
	private SlidingLayersConfig setupGenerationCfg;
	private SlidingLayersConfig updateAdvSettingsCfg;
	private SlidingLayersConfig updateGenerationCfg;

	private boolean isProcessSetupPanelOpen = false;
	private String currentLibraryInfo;

	private void initConfigurations() {
		JLabel logo = new JLabel(Res.getImage("gfx/logo.png"));
		logo.setVerticalAlignment(SwingConstants.TOP);
		logo.setHorizontalAlignment(SwingConstants.LEFT);

		initCfg = new SlidingLayersConfig(panel)
			.row(false, 1).row(true, 30).column(false, 1)
			.beginGrid(0, 0)
				.row(false, 1)
				.row(true, selectionPanel.getPreferredSize().height)
				.row(true, versionLabel.getPreferredSize().height)
				.row(false, 1)
				.column(false, 1).column(false, 1).column(false, 1)
				.tile(0, 0, logo)
				.tile(1, 1, selectionPanel)
				.tile(2, 1, versionLabel)
			.end()
			.tile(1, 0, taskPanel);

		setupCfg = new SlidingLayersConfig(panel)
			.row(false, 1).row(true, 30).column(false, 1)
			.beginGrid(0, 0)
				.row(false, 1).column(false, 1).column(false, 1).column(false, 1)
				.beginGrid(0, 0)
					.row(true, selectionPanel.getPreferredSize().height)
					.row(true, configSetupPanel.getPreferredSize().height)
					.row(true, versionLabel.getPreferredSize().height)
					.column(false, 1)
					.tile(0, 0, selectionPanel)
					.tile(1, 0, configSetupPanel)
					.tile(2, 0, versionLabel)
				.end()
				.beginGrid(0, 1)
					.row(false, 1)
					.column(false, 1)
					.tile(0, 0, librarySelectionPanel)
				.end()
				.beginGrid(0, 2)
					.row(false, 1)
					.row(true, goPanel.getPreferredSize().height)
					.column(false, 1)
					.tile(0, 0, previewPanel)
					.tile(1, 0, goPanel)
				.end()
			.end()
			.tile(1, 0, taskPanel);

		updateCfg = new SlidingLayersConfig(panel)
			.row(false, 1).row(true, 30).column(false, 1)
			.beginGrid(0, 0)
				.row(false, 1).column(false, 1).column(false, 1).column(false, 1)
				.beginGrid(0, 0)
					.row(true, selectionPanel.getPreferredSize().height)
					.row(true, configUpdatePanel.getPreferredSize().height)
					.row(true, versionLabel.getPreferredSize().height)
					.column(false, 1)
					.tile(0, 0, selectionPanel)
					.tile(1, 0, configUpdatePanel)
					.tile(2, 0, versionLabel)
				.end()
				.beginGrid(0, 1)
					.row(false, 1)
					.column(false, 1)
					.tile(0, 0, librarySelectionPanel)
				.end()
				.beginGrid(0, 2)
					.row(true, goPanel.getPreferredSize().height)
					.column(false, 1)
					.tile(0, 0, goPanel)
				.end()
			.end()
			.tile(1, 0, taskPanel);

		libraryInfoCfg = new SlidingLayersConfig(panel)
			.row(false, 1).row(true, 30).column(false, 1)
			.beginGrid(0, 0)
				.row(false, 1).column(false, 1).column(false, 2)
				.tile(0, 0, librarySelectionPanel)
				.tile(0, 1, libraryInfoPanel)
			.end()
			.tile(1, 0, taskPanel);

		setupAdvSettingsCfg = new SlidingLayersConfig(panel)
			.row(false, 1).column(false, 1).column(false, 2)
			.beginGrid(0, 0)
				.row(true, configSetupPanel.getPreferredSize().height)
				.row(true, versionLabel.getPreferredSize().height)
				.column(false, 1)
				.tile(0, 0, configSetupPanel)
				.tile(1, 0, versionLabel)
			.end()
			.tile(0, 1, advancedSettingsPanel);

		setupGenerationCfg = new SlidingLayersConfig(panel)
			.row(false, 1).column(false, 2).column(false, 1)
			.beginGrid(0, 1)
				.row(false, 1)
				.row(true, goPanel.getPreferredSize().height)
				.column(false, 1)
				.tile(0, 0, previewPanel)
				.tile(1, 0, goPanel)
			.end()
			.tile(0, 0, processSetupPanel);

		updateAdvSettingsCfg = new SlidingLayersConfig(panel)
			.row(false, 1).column(false, 1).column(false, 2)
			.beginGrid(0, 0)
				.row(true, configUpdatePanel.getPreferredSize().height)
				.row(true, versionLabel.getPreferredSize().height)
				.column(false, 1)
				.tile(0, 0, configUpdatePanel)
				.tile(1, 0, versionLabel)
			.end()
			.tile(0, 1, advancedSettingsPanel);

		updateGenerationCfg = new SlidingLayersConfig(panel)
			.row(false, 1).column(false, 2).column(false, 1)
			.tile(0, 0, classpathsPanel)
			.tile(0, 1, processUpdatePanel);
	}

	public void showCreateSetup() {
		switch (Ctx.mode) {
			case INIT:
				Ctx.mode = Ctx.Mode.SETUP;
				Ctx.fireModeChangedChanged();

				panel.timeline()
					.pushTo(new SlidingLayersConfig(panel)
						.column(false, 1).column(false, 1).column(false, 1)
						.row(true, selectionPanel.getPreferredSize().height)
						.row(true, configSetupPanel.getPreferredSize().height)
						.row(true, versionLabel.getPreferredSize().height)
						.tile(0, 0, selectionPanel)
						.tile(2, 0, versionLabel)
						.delay(0.15f, versionLabel))
					.pushSet(setupCfg.clone()
						.hide(Direction.LEFT, configSetupPanel)
						.hide(Direction.UP, librarySelectionPanel)
						.hide(Direction.RIGHT, previewPanel, goPanel))
					.pushTo(setupCfg.clone()
						.delayIncr(0.05f, configSetupPanel, librarySelectionPanel, previewPanel, goPanel))
					.play();
				break;

			case UPDATE:
				Ctx.mode = Ctx.Mode.SETUP;
				Ctx.fireModeChangedChanged();

				panel.timeline()
					.pushTo(updateCfg.clone()
						.hide(Direction.LEFT, configUpdatePanel))
					.pushTo(setupCfg.clone()
						.hide(Direction.RIGHT, previewPanel)
						.hide(Direction.LEFT, configSetupPanel))
					.pushTo(setupCfg)
					.play();
				break;
		}
	}

	public void showUpdateSetup() {
		switch (Ctx.mode) {
			case INIT:
				Ctx.mode = Ctx.Mode.UPDATE;
				Ctx.fireModeChangedChanged();

				panel.timeline()
					.pushTo(new SlidingLayersConfig(panel)
						.column(false, 1).column(false, 1).column(false, 1)
						.row(true, selectionPanel.getPreferredSize().height)
						.row(true, configUpdatePanel.getPreferredSize().height)
						.row(true, versionLabel.getPreferredSize().height)
						.tile(0, 0, selectionPanel)
						.tile(2, 0, versionLabel)
						.delay(0.15f, versionLabel))
					.pushSet(updateCfg.clone()
						.hide(Direction.LEFT, configUpdatePanel)
						.hide(Direction.UP, librarySelectionPanel)
						.hide(Direction.RIGHT, goPanel))
					.pushTo(updateCfg.clone()
						.delayIncr(0.05f, configUpdatePanel, librarySelectionPanel, goPanel))
					.play();
				break;

			case SETUP:
				Ctx.mode = Ctx.Mode.UPDATE;
				Ctx.fireModeChangedChanged();

				panel.timeline()
					.pushTo(setupCfg.clone()
						.hide(Direction.RIGHT, previewPanel)
						.hide(Direction.LEFT, configSetupPanel))
					.pushTo(updateCfg.clone()
						.hide(Direction.LEFT, configUpdatePanel))
					.pushTo(updateCfg)
					.play();
				break;
		}
	}

	public void showAdvancedSettings() {
		switch (Ctx.mode) {
			case SETUP:
				panel.timeline()
					.pushTo(setupCfg.clone()
						.hide(Direction.DOWN, taskPanel)
						.hide(Direction.UP, selectionPanel, librarySelectionPanel)
						.hide(Direction.RIGHT, previewPanel, goPanel)
						.changeRow(0, configSetupPanel)
						.changeRow(1, versionLabel)
						.delayIncr(0.05f, librarySelectionPanel, configSetupPanel, versionLabel, previewPanel, goPanel, taskPanel))
					.pushSet(setupAdvSettingsCfg.clone()
						.hide(Direction.RIGHT, advancedSettingsPanel))
					.pushTo(setupAdvSettingsCfg)
					.play();
				break;

			case UPDATE:
				panel.timeline()
					.pushTo(updateCfg.clone()
						.hide(Direction.DOWN, taskPanel)
						.hide(Direction.UP, selectionPanel, librarySelectionPanel)
						.hide(Direction.RIGHT, goPanel)
						.changeRow(0, configUpdatePanel)
						.changeRow(1, versionLabel)
						.delayIncr(0.05f, librarySelectionPanel, configUpdatePanel, versionLabel, goPanel, taskPanel))
					.pushSet(updateAdvSettingsCfg.clone()
						.hide(Direction.RIGHT, advancedSettingsPanel))
					.pushTo(updateAdvSettingsCfg)
					.play();
				break;
		}
	}

	public void hideAdvancedSettings() {
		switch (Ctx.mode) {
			case SETUP:
				panel.timeline()
					.pushTo(setupAdvSettingsCfg.clone()
						.hide(Direction.RIGHT, advancedSettingsPanel))
					.pushSet(setupCfg.clone()
						.hide(Direction.DOWN, taskPanel)
						.hide(Direction.UP, selectionPanel, librarySelectionPanel)
						.hide(Direction.RIGHT, previewPanel, goPanel)
						.changeRow(0, configSetupPanel)
						.changeRow(1, versionLabel))
					.pushTo(setupCfg.clone()
						.delayIncr(0.05f, versionLabel, configSetupPanel, selectionPanel, librarySelectionPanel, previewPanel, goPanel, taskPanel))
					.play();
				break;

			case UPDATE:
				panel.timeline()
					.pushTo(updateAdvSettingsCfg.clone()
						.hide(Direction.RIGHT, advancedSettingsPanel))
					.pushSet(updateCfg.clone()
						.hide(Direction.DOWN, taskPanel)
						.hide(Direction.UP, selectionPanel, librarySelectionPanel)
						.hide(Direction.RIGHT, goPanel)
						.changeRow(0, configUpdatePanel)
						.changeRow(1, versionLabel))
					.pushTo(updateCfg.clone()
						.delayIncr(0.05f, versionLabel, configUpdatePanel, selectionPanel, librarySelectionPanel, goPanel, taskPanel))
					.play();
				break;
		}
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

		switch (Ctx.mode) {
			case SETUP:
				panel.timeline()
					.pushTo(setupCfg.clone()
						.hide(Direction.LEFT, selectionPanel, configSetupPanel, versionLabel)
						.hide(Direction.RIGHT, previewPanel, goPanel)
						.delayIncr(0.02f, librarySelectionPanel, configSetupPanel, versionLabel, previewPanel, goPanel, versionLabel, taskPanel))
					.pushTo(libraryInfoCfg.clone()
						.hide(Direction.RIGHT, libraryInfoPanel))
					.pushTo(libraryInfoCfg)
					.play();
				break;

			case UPDATE:
				panel.timeline()
					.pushTo(updateCfg.clone()
						.hide(Direction.LEFT, selectionPanel, configUpdatePanel, versionLabel)
						.hide(Direction.RIGHT, goPanel)
						.delayIncr(0.02f, librarySelectionPanel, configUpdatePanel, versionLabel, goPanel, versionLabel, taskPanel))
					.pushTo(libraryInfoCfg.clone()
						.hide(Direction.RIGHT, libraryInfoPanel))
					.pushTo(libraryInfoCfg)
					.play();
				break;
		}
	}

	public void hideLibraryInfo() {
		currentLibraryInfo = null;

		switch (Ctx.mode) {
			case SETUP:
				panel.timeline()
					.pushTo(libraryInfoCfg.clone()
						.hide(Direction.RIGHT, libraryInfoPanel))
					.pushTo(setupCfg.clone()
						.hide(Direction.LEFT, selectionPanel, configSetupPanel, versionLabel)
						.hide(Direction.RIGHT, previewPanel, goPanel))
					.pushTo(setupCfg.clone()
						.delayIncr(0.02f, librarySelectionPanel, configSetupPanel, versionLabel, previewPanel, goPanel, versionLabel, taskPanel))
					.play();
				break;

			case UPDATE:
				panel.timeline()
					.pushTo(libraryInfoCfg.clone()
						.hide(Direction.RIGHT, libraryInfoPanel))
					.pushTo(updateCfg.clone()
						.hide(Direction.LEFT, selectionPanel, configUpdatePanel, versionLabel)
						.hide(Direction.RIGHT, goPanel))
					.pushTo(updateCfg.clone()
						.delayIncr(0.02f, librarySelectionPanel, configUpdatePanel, versionLabel, goPanel, versionLabel, taskPanel))
					.play();
				break;
		}
	}

	public void showGenerationCreatePanel() {
		if (isProcessSetupPanelOpen) {
			hideGenerationCreatePanel();
			return;
		}

		isProcessSetupPanelOpen = true;

		panel.timeline()
			.pushTo(setupCfg.clone()
				.hide(Direction.DOWN, taskPanel, selectionPanel, configSetupPanel, versionLabel, librarySelectionPanel)
				.delayIncr(0.05f, librarySelectionPanel, versionLabel, configSetupPanel, selectionPanel))
			.pushTo(setupGenerationCfg.clone()
				.hide(Direction.UP, processSetupPanel))
			.pushTo(setupGenerationCfg)
			.play();
	}

	public void hideGenerationCreatePanel() {
		isProcessSetupPanelOpen = false;

		panel.timeline()
			.pushTo(setupGenerationCfg.clone()
				.hide(Direction.DOWN, processSetupPanel))
			.pushTo(setupCfg.clone()
				.hide(Direction.UP, selectionPanel, configSetupPanel, versionLabel, librarySelectionPanel)
				.hide(Direction.DOWN, taskPanel))
			.pushTo(setupCfg.clone()
				.delayIncr(0.05f, versionLabel, configSetupPanel, selectionPanel, librarySelectionPanel, taskPanel))
			.play();
	}

	public void showGenerationUpdatePanel() {
		panel.timeline()
			.pushTo(updateCfg.clone()
				.hide(Direction.DOWN, taskPanel, selectionPanel, configUpdatePanel, versionLabel, librarySelectionPanel)
				.hide(Direction.UP, goPanel)
				.delayIncr(0.05f, taskPanel, librarySelectionPanel, versionLabel, configUpdatePanel, selectionPanel, goPanel))
			.pushTo(updateGenerationCfg.clone()
				.hide(Direction.UP, classpathsPanel)
				.hide(Direction.DOWN, processUpdatePanel))
			.pushTo(updateGenerationCfg)
			.play();
	}

	public void hideGenerationUpdatePanel() {
		panel.timeline()
			.pushTo(updateGenerationCfg.clone()
				.hide(Direction.UP, classpathsPanel)
				.hide(Direction.DOWN, processUpdatePanel))
			.pushTo(updateCfg.clone()
				.hide(Direction.DOWN, taskPanel, selectionPanel, configUpdatePanel, versionLabel, librarySelectionPanel)
				.hide(Direction.UP, goPanel))
			.pushTo(updateCfg
				.delayIncr(0.05f, librarySelectionPanel, selectionPanel, configUpdatePanel, versionLabel, goPanel, taskPanel))
			.play();
	}
}
