package aurelienribon.libgdx.ui;

import aurelienribon.libgdx.LibraryDef;
import aurelienribon.libgdx.ui.panels.AdvancedSettingsPanel;
import aurelienribon.libgdx.ui.panels.ClasspathsPanel;
import aurelienribon.libgdx.ui.panels.ConfigCreatePanel;
import aurelienribon.libgdx.ui.panels.ConfigUpdatePanel;
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
import org.apache.commons.io.IOUtils;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class MainPanel extends PaintedPanel {
	private final SelectionPanel selectionPanel = new SelectionPanel(this);
	private final ConfigCreatePanel configCreatePanel = new ConfigCreatePanel(this);
	private final ConfigUpdatePanel configUpdatePanel = new ConfigUpdatePanel(this);
	private final VersionLabel versionLabel = new VersionLabel();
	private final LibrarySetupPanel librarySetupPanel = new LibrarySetupPanel(this);
	private final ResultPanel resultPanel = new ResultPanel();
	private final GoPanel goPanel = new GoPanel(this);
	private final TaskPanel taskPanel = new TaskPanel();
	private final AdvancedSettingsPanel advancedSettingsPanel = new AdvancedSettingsPanel();
	private final LibraryInfoPanel libraryInfoPanel = new LibraryInfoPanel(this);
	private final ClasspathsPanel classpathsPanel = new ClasspathsPanel(this);
	private final GenerationPanel generationPanel = new GenerationPanel(this);

	private final SlidingLayersPanel panel = new SlidingLayersPanel();

	public MainPanel() {
		SwingUtils.importFont(Res.getStream("fonts/SquareFont.ttf"));
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);

		Style.registerCssClasses(this, ".rootPanel");
		Style.registerCssClasses(selectionPanel, ".groupPanel", "#selectionPanel");
		Style.registerCssClasses(configCreatePanel, ".groupPanel", "#configCreatePanel");
		Style.registerCssClasses(configUpdatePanel, ".groupPanel", "#configUpdatePanel");
		Style.registerCssClasses(versionLabel, ".versionLabel");
		Style.registerCssClasses(librarySetupPanel, ".groupPanel", "#librarySetupPanel");
		Style.registerCssClasses(resultPanel, ".groupPanel", "#resultPanel");
		Style.registerCssClasses(goPanel, ".groupPanel", "#goPanel");
		Style.registerCssClasses(advancedSettingsPanel, ".groupPanel", "#advancedSettingsPanel");
		Style.registerCssClasses(libraryInfoPanel, ".groupPanel", "#libraryInfoPanel");
		Style.registerCssClasses(classpathsPanel, ".groupPanel", "#classpathsPanel");
		Style.registerCssClasses(generationPanel, ".groupPanel", "#generationPanel");

		Style style = new Style(Res.getUrl("css/style.css"));
		Style.apply(this, style);
		Style.apply(selectionPanel, style);
		Style.apply(configCreatePanel, style);
		Style.apply(configUpdatePanel, style);
		Style.apply(versionLabel, style);
		Style.apply(librarySetupPanel, style);
		Style.apply(resultPanel, style);
		Style.apply(goPanel, style);
		Style.apply(taskPanel, style);
		Style.apply(advancedSettingsPanel, style);
		Style.apply(libraryInfoPanel, style);
		Style.apply(classpathsPanel, style);
		Style.apply(generationPanel, style);

		try {
			String rawDef = IOUtils.toString(Res.getStream("libgdx.txt"));
			LibraryDef def = new LibraryDef(rawDef);
			Ctx.libs.addDef("libgdx", def);
			Ctx.cfgCreate.libraries.add("libgdx");
			Ctx.cfgUpdate.libraries.add("libgdx");
		} catch (IOException ex) {
			assert false;
		}

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
				DownloadTask task = Ctx.libs.downloadConfigFile();
				task.addListener(configFileDownloadListener);
			}
		});
	}

	private final DownloadListener configFileDownloadListener = new DownloadListener() {
		@Override
		public void onComplete() {
			if (Ctx.testLibUrl != null) Ctx.libs.addUrl("__test_url__", Ctx.testLibUrl);
			if (Ctx.testLibDef != null) Ctx.libs.addDef("__test_def__", Ctx.testLibDef);
			if (Ctx.testLibDef != null) librarySetupPanel.registerLibrary("__test_def__");

			for (String name : Ctx.libs.getNames()) {
				DownloadTask task = Ctx.libs.downloadDef(name);
				final String libraryName = name;

				task.addListener(new DownloadListener() {
					@Override public void onComplete() {
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
	private SlidingLayersConfig createAdvSettingsCfg;
	private SlidingLayersConfig createLibraryInfoCfg;
	private SlidingLayersConfig createGenerationCfg;
	private SlidingLayersConfig updateClasspathsCfg;

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
				.row(false, 10).row(false, 15).row(true, versionLabel.getPreferredSize().height).row(false, 10)
				.column(false, 1).column(false, 2).column(false, 1)
				.tile(0, 0, logo)
				.tile(1, 1, selectionPanel)
				.tile(2, 1, versionLabel)
			.end()
			.tile(1, 0, taskPanel);

		createCfg = new SlidingLayersConfig(panel)
			.row(false, 1).row(true, 30).column(false, 1)
			.beginGrid(0, 0)
				.row(false, 1).column(false, 1).column(false, 1).column(false, 1)
				.beginGrid(0, 0)
					.row(true, selectionPanel.getPreferredSize().height)
					.row(true, configCreatePanel.getPreferredSize().height)
					.row(true, versionLabel.getPreferredSize().height)
					.column(false, 1)
					.tile(0, 0, selectionPanel)
					.tile(1, 0, configCreatePanel)
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
					.tile(0, 0, librarySetupPanel)
				.end()
				.beginGrid(0, 2)
					.row(true, goPanel.getPreferredSize().height)
					.column(false, 1)
					.tile(0, 0, goPanel)
				.end()
			.end()
			.tile(1, 0, taskPanel);

		createAdvSettingsCfg = new SlidingLayersConfig(panel)
			.row(false, 1).column(false, 1).column(false, 2)
			.beginGrid(0, 0)
				.row(true, configCreatePanel.getPreferredSize().height)
				.row(true, versionLabel.getPreferredSize().height)
				.column(false, 1)
				.tile(0, 0, configCreatePanel)
				.tile(1, 0, versionLabel)
			.end()
			.tile(0, 1, advancedSettingsPanel);

		createLibraryInfoCfg = new SlidingLayersConfig(panel)
			.row(false, 1).row(true, 30).column(false, 1)
			.beginGrid(0, 0)
				.row(false, 1).column(false, 1).column(false, 2)
				.tile(0, 0, librarySetupPanel)
				.tile(0, 1, libraryInfoPanel)
			.end()
			.tile(1, 0, taskPanel);

		createGenerationCfg = new SlidingLayersConfig(panel)
			.row(false, 1).column(false, 2).column(false, 1)
			.beginGrid(0, 1)
				.row(false, 1)
				.row(true, goPanel.getPreferredSize().height)
				.column(false, 1)
				.tile(0, 0, resultPanel)
				.tile(1, 0, goPanel)
			.end()
			.tile(0, 0, generationPanel);

		updateClasspathsCfg = new SlidingLayersConfig(panel)
			.row(false, 1).column(false, 2).column(false, 1)
			.beginGrid(0, 1)
				.row(true, goPanel.getPreferredSize().height)
				.column(false, 1)
				.tile(0, 0, goPanel)
			.end()
			.tile(0, 0, classpathsPanel);
	}

	public void showCreateSetup() {
		if (isFirstPanelShown) {
			Ctx.mode = Ctx.Mode.CREATE;
			Ctx.fireModeChangedChanged();

			panel.timeline()
				.pushTo(new SlidingLayersConfig(panel)
					.column(false, 1).column(false, 1).column(false, 1)
					.row(true, selectionPanel.getPreferredSize().height)
					.row(true, configCreatePanel.getPreferredSize().height)
					.row(true, versionLabel.getPreferredSize().height)
					.tile(0, 0, selectionPanel)
					.tile(2, 0, versionLabel)
					.delay(0.15f, versionLabel))
				.pushSet(createCfg.clone()
					.hide(Direction.LEFT, configCreatePanel)
					.hide(Direction.UP, librarySetupPanel)
					.hide(Direction.RIGHT, resultPanel, goPanel))
				.pushTo(createCfg.clone()
					.delayIncr(0.05f, configCreatePanel, librarySetupPanel, resultPanel, goPanel))
				.play();

		} else if (!isCreateSetupUsed) {
			Ctx.mode = Ctx.Mode.CREATE;
			Ctx.fireModeChangedChanged();

			panel.timeline()
				.pushTo(updateCfg.clone()
					.hide(Direction.LEFT, configUpdatePanel))
				.pushTo(createCfg.clone()
					.hide(Direction.RIGHT, resultPanel)
					.hide(Direction.LEFT, configCreatePanel))
				.pushTo(createCfg)
				.play();
		}

		isCreateSetupUsed = true;
		isFirstPanelShown = false;
	}

	public void showUpdateSetup() {
		if (isFirstPanelShown) {
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
					.hide(Direction.UP, librarySetupPanel)
					.hide(Direction.RIGHT, goPanel))
				.pushTo(updateCfg.clone()
					.delayIncr(0.05f, configUpdatePanel, librarySetupPanel, goPanel))
				.play();

		} else if (isCreateSetupUsed) {
			Ctx.mode = Ctx.Mode.UPDATE;
			Ctx.fireModeChangedChanged();

			panel.timeline()
				.pushTo(createCfg.clone()
					.hide(Direction.RIGHT, resultPanel)
					.hide(Direction.LEFT, configCreatePanel))
				.pushTo(updateCfg.clone()
					.hide(Direction.LEFT, configUpdatePanel))
				.pushTo(updateCfg)
				.play();
		}

		isCreateSetupUsed = false;
		isFirstPanelShown = false;
	}

	public void showAdvancedSettings() {
		if (isCreateSetupUsed) {
			panel.timeline()
				.pushTo(createCfg.clone()
					.hide(Direction.DOWN, taskPanel)
					.hide(Direction.UP, selectionPanel, librarySetupPanel)
					.hide(Direction.RIGHT, resultPanel, goPanel)
					.changeRow(0, configCreatePanel)
					.changeRow(1, versionLabel)
					.delayIncr(0.05f, librarySetupPanel, configCreatePanel, versionLabel, resultPanel, goPanel, taskPanel))
				.pushSet(createAdvSettingsCfg.clone()
					.hide(Direction.RIGHT, advancedSettingsPanel))
				.pushTo(createAdvSettingsCfg)
				.play();
		} else {

		}
	}

	public void hideAdvancedSettings() {
		if (isCreateSetupUsed) {
			panel.timeline()
				.pushTo(createAdvSettingsCfg.clone()
					.hide(Direction.RIGHT, advancedSettingsPanel))
				.pushSet(createCfg.clone()
					.hide(Direction.DOWN, taskPanel)
					.hide(Direction.UP, selectionPanel, librarySetupPanel)
					.hide(Direction.RIGHT, resultPanel, goPanel)
					.changeRow(0, configCreatePanel)
					.changeRow(1, versionLabel))
				.pushTo(createCfg.clone()
					.delayIncr(0.05f, versionLabel, configCreatePanel, selectionPanel, librarySetupPanel, resultPanel, goPanel, taskPanel))
				.play();
		} else {

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

		if (isCreateSetupUsed) {
			currentLibraryInfo = libraryName;
			libraryInfoPanel.setup(libraryName);

			panel.timeline()
				.pushTo(createCfg.clone()
					.hide(Direction.LEFT, selectionPanel, configCreatePanel, versionLabel)
					.hide(Direction.RIGHT, resultPanel, goPanel)
					.delayIncr(0.02f, librarySetupPanel, configCreatePanel, versionLabel, resultPanel, goPanel, versionLabel, taskPanel))
				.pushTo(createLibraryInfoCfg.clone()
					.hide(Direction.RIGHT, libraryInfoPanel))
				.pushTo(createLibraryInfoCfg)
				.play();
		} else {

		}
	}

	public void hideLibraryInfo() {
		currentLibraryInfo = null;

		if (isCreateSetupUsed) {
			panel.timeline()
				.pushTo(createLibraryInfoCfg.clone()
					.hide(Direction.RIGHT, libraryInfoPanel))
				.pushTo(createCfg.clone()
					.hide(Direction.LEFT, selectionPanel, configCreatePanel, versionLabel)
					.hide(Direction.RIGHT, resultPanel, goPanel))
				.pushTo(createCfg.clone()
					.delayIncr(0.02f, librarySetupPanel, configCreatePanel, versionLabel, resultPanel, goPanel, versionLabel, taskPanel))
				.play();
		} else {

		}
	}

	public void showGenerationPanel() {
		if (isGenerationPanelOpen) {
			hideGenerationPanel();
			return;
		}

		isGenerationPanelOpen = true;

		panel.timeline()
			.pushTo(createCfg.clone()
				.hide(Direction.DOWN, taskPanel, selectionPanel, configCreatePanel, versionLabel, librarySetupPanel)
				.delayIncr(0.05f, librarySetupPanel, versionLabel, configCreatePanel, selectionPanel))
			.pushTo(createGenerationCfg.clone()
				.hide(Direction.UP, generationPanel))
			.pushTo(createGenerationCfg)
			.play();
	}

	public void hideGenerationPanel() {
		isGenerationPanelOpen = false;

		panel.timeline()
			.pushTo(createGenerationCfg.clone()
				.hide(Direction.DOWN, generationPanel))
			.pushTo(createCfg.clone()
				.hide(Direction.UP, selectionPanel, configCreatePanel, versionLabel, librarySetupPanel)
				.hide(Direction.DOWN, taskPanel))
			.pushTo(createCfg.clone()
				.delayIncr(0.05f, versionLabel, configCreatePanel, selectionPanel, librarySetupPanel, taskPanel))
			.play();
	}

	public void showClasspathsPanel() {
		panel.timeline()
			.pushTo(createCfg.clone()
				.hide(Direction.DOWN, taskPanel, selectionPanel, configCreatePanel, versionLabel, librarySetupPanel)
				.delayIncr(0.05f, librarySetupPanel, versionLabel, configCreatePanel, selectionPanel))
			.pushTo(updateClasspathsCfg.clone()
				.hide(Direction.UP, classpathsPanel))
			.pushTo(updateClasspathsCfg)
			.play();
	}

	public void hideClasspathsPanel() {
		
	}
}
