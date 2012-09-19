package aurelienribon.gdxsetupui.ui;

import aurelienribon.gdxsetupui.LibraryDef;
import aurelienribon.gdxsetupui.ui.panels.AdvancedSettingsPanel;
import aurelienribon.gdxsetupui.ui.panels.ClasspathsPanel;
import aurelienribon.gdxsetupui.ui.panels.ConfigSetupPanel;
import aurelienribon.gdxsetupui.ui.panels.ConfigUpdatePanel;
import aurelienribon.gdxsetupui.ui.panels.GoPanel;
import aurelienribon.gdxsetupui.ui.panels.LibraryInfoPanel;
import aurelienribon.gdxsetupui.ui.panels.LibrarySelectionPanel;
import aurelienribon.gdxsetupui.ui.panels.PreviewPanel;
import aurelienribon.gdxsetupui.ui.panels.ProcessSetupPanel;
import aurelienribon.gdxsetupui.ui.panels.ProcessUpdatePanel;
import aurelienribon.gdxsetupui.ui.panels.SelectionPanel;
import aurelienribon.gdxsetupui.ui.panels.TaskPanel;
import aurelienribon.slidinglayout.SLAnimator;
import aurelienribon.slidinglayout.SLConfig;
import aurelienribon.slidinglayout.SLKeyframe;
import aurelienribon.slidinglayout.SLPanel;
import aurelienribon.slidinglayout.SLSide;
import aurelienribon.ui.components.PaintedPanel;
import aurelienribon.ui.css.Style;
import aurelienribon.utils.HttpUtils.DownloadListener;
import aurelienribon.utils.HttpUtils.DownloadTask;
import aurelienribon.utils.Res;
import aurelienribon.utils.SwingUtils;
import aurelienribon.utils.VersionLabel;
import java.awt.BorderLayout;
import java.awt.Component;
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
	private final SLPanel rootPanel = new SLPanel();
	private final JLabel logoLabel = new JLabel(Res.getImage("gfx/logo.png"));
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

	private final float transitionDuration = 0.5f;
	private final int gap = 10;

	public MainPanel() {
		SwingUtils.importFont(Res.getStream("fonts/SquareFont.ttf"));
		setLayout(new BorderLayout());
		add(rootPanel, BorderLayout.CENTER);

		logoLabel.setVerticalAlignment(SwingConstants.TOP);
		logoLabel.setHorizontalAlignment(SwingConstants.LEFT);

		Style.registerCssClasses(this, ".rootPanel");
		Style.registerCssClasses(selectionPanel, ".groupPanel", "#selectionPanel");
		Style.registerCssClasses(configSetupPanel, ".groupPanel", "#configSetupPanel");
		Style.registerCssClasses(configUpdatePanel, ".groupPanel", "#configUpdatePanel");
		Style.registerCssClasses(versionLabel, ".versionLabel");
		Style.registerCssClasses(librarySelectionPanel, ".groupPanel", "#librarySelectionPanel");
		Style.registerCssClasses(previewPanel, ".groupPanel", "#previewPanel");
		Style.registerCssClasses(goPanel, ".groupPanel", "#goPanel");
		Style.registerCssClasses(advancedSettingsPanel, ".groupPanel", "#advancedSettingsPanel");
		Style.registerCssClasses(libraryInfoPanel, ".groupPanel", "#libraryInfoPanel");
		Style.registerCssClasses(classpathsPanel, ".groupPanel", "#classpathsPanel");
		Style.registerCssClasses(processSetupPanel, ".groupPanel", "#processSetupPanel");
		Style.registerCssClasses(processUpdatePanel, ".groupPanel", "#processUpdatePanel");

		Component[] targets = new Component[] {
			this, selectionPanel, configSetupPanel, configUpdatePanel, versionLabel,
			librarySelectionPanel, previewPanel, goPanel, taskPanel, advancedSettingsPanel,
			libraryInfoPanel, classpathsPanel, processSetupPanel, processUpdatePanel
		};

		Style style = new Style(Res.getUrl("css/style.css"));
		for (Component target : targets) Style.apply(target, style);

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

		SLAnimator.start();
		rootPanel.setTweenManager(SLAnimator.createTweenManager());
		taskPanel.setTweenManager(SLAnimator.createTweenManager());

		initConfigurations();
		rootPanel.initialize(initCfg);

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

	private SLConfig initCfg, setupCfg, updateCfg;
	private SLConfig libraryInfoCfg;
	private SLConfig setupAdvSettingsCfg;
	private SLConfig setupGenerationCfg;
	private SLConfig updateAdvSettingsCfg;
	private SLConfig updateGenerationCfg;

	private boolean isProcessSetupPanelOpen = false;
	private String currentLibraryInfo;

	private void initConfigurations() {
		initCfg = new SLConfig(rootPanel)
			.gap(gap, gap)
			.row(1f).row(30).col(1f)
			.beginGrid(0, 0)
				.row(1f)
				.row(selectionPanel.getPreferredSize().height)
				.row(versionLabel.getPreferredSize().height)
				.row(1f)
				.col(1f).col(1f).col(1f)
				.place(0, 0, logoLabel)
				.place(1, 1, selectionPanel)
				.place(2, 1, versionLabel)
			.endGrid()
			.place(1, 0, taskPanel);

		setupCfg = new SLConfig(rootPanel)
			.gap(gap, gap)
			.row(1f).row(30).col(1f)
			.beginGrid(0, 0)
				.row(1f).col(1f).col(1f).col(1f)
				.beginGrid(0, 0)
					.row(selectionPanel.getPreferredSize().height)
					.row(configSetupPanel.getPreferredSize().height)
					.row(versionLabel.getPreferredSize().height)
					.col(1f)
					.place(0, 0, selectionPanel)
					.place(1, 0, configSetupPanel)
					.place(2, 0, versionLabel)
				.endGrid()
				.beginGrid(0, 1)
					.row(1f)
					.col(1f)
					.place(0, 0, librarySelectionPanel)
				.endGrid()
				.beginGrid(0, 2)
					.row(1f)
					.row(goPanel.getPreferredSize().height)
					.col(1f)
					.place(0, 0, previewPanel)
					.place(1, 0, goPanel)
				.endGrid()
			.endGrid()
			.place(1, 0, taskPanel);

		updateCfg = new SLConfig(rootPanel)
			.gap(gap, gap)
			.row(1f).row(30).col(1f)
			.beginGrid(0, 0)
				.row(1f).col(1f).col(1f).col(1f)
				.beginGrid(0, 0)
					.row(selectionPanel.getPreferredSize().height)
					.row(configUpdatePanel.getPreferredSize().height)
					.row(versionLabel.getPreferredSize().height)
					.col(1f)
					.place(0, 0, selectionPanel)
					.place(1, 0, configUpdatePanel)
					.place(2, 0, versionLabel)
				.endGrid()
				.beginGrid(0, 1)
					.row(1f)
					.col(1f)
					.place(0, 0, librarySelectionPanel)
				.endGrid()
				.beginGrid(0, 2)
					.row(goPanel.getPreferredSize().height)
					.col(1f)
					.place(0, 0, goPanel)
				.endGrid()
			.endGrid()
			.place(1, 0, taskPanel);

		libraryInfoCfg = new SLConfig(rootPanel)
			.gap(gap, gap)
			.row(1f).row(30).col(1f)
			.beginGrid(0, 0)
				.row(1f).col(1f).col(2f)
				.place(0, 0, librarySelectionPanel)
				.place(0, 1, libraryInfoPanel)
			.endGrid()
			.place(1, 0, taskPanel);

		setupAdvSettingsCfg = new SLConfig(rootPanel)
			.gap(gap, gap)
			.row(1f).col(1f).col(2f)
			.beginGrid(0, 0)
				.row(configSetupPanel.getPreferredSize().height)
				.row(versionLabel.getPreferredSize().height)
				.col(1f)
				.place(0, 0, configSetupPanel)
				.place(1, 0, versionLabel)
			.endGrid()
			.place(0, 1, advancedSettingsPanel);

		setupGenerationCfg = new SLConfig(rootPanel)
			.gap(gap, gap)
			.row(1f).col(2f).col(1f)
			.beginGrid(0, 1)
				.row(1f).col(1f)
				.place(0, 0, previewPanel)
			.endGrid()
			.place(0, 0, processSetupPanel);

		updateAdvSettingsCfg = new SLConfig(rootPanel)
			.gap(gap, gap)
			.row(1f).col(1f).col(2f)
			.beginGrid(0, 0)
				.row(configUpdatePanel.getPreferredSize().height)
				.row(versionLabel.getPreferredSize().height)
				.col(1f)
				.place(0, 0, configUpdatePanel)
				.place(1, 0, versionLabel)
			.endGrid()
			.place(0, 1, advancedSettingsPanel);

		updateGenerationCfg = new SLConfig(rootPanel)
			.gap(gap, gap)
			.row(1f).col(2f).col(1f)
			.place(0, 0, classpathsPanel)
			.place(0, 1, processUpdatePanel);
	}

	public void showCreateSetup() {
		switch (Ctx.mode) {
			case INIT:
				Ctx.mode = Ctx.Mode.SETUP;
				Ctx.fireModeChangedChanged();

				rootPanel.createTransition()
					.push(new SLKeyframe(setupCfg, transitionDuration)
						.setStartSide(SLSide.LEFT, configSetupPanel)
						.setStartSide(SLSide.TOP, librarySelectionPanel)
						.setStartSide(SLSide.RIGHT, previewPanel, goPanel)
						.setEndSide(SLSide.TOP, logoLabel)
						.setDelay(transitionDuration, configSetupPanel, librarySelectionPanel))
					.play();
				break;

			case UPDATE:
				Ctx.mode = Ctx.Mode.SETUP;
				Ctx.fireModeChangedChanged();

				rootPanel.createTransition()
					.push(new SLKeyframe(setupCfg, transitionDuration)
						.setEndSide(SLSide.LEFT, configUpdatePanel)
						.setStartSide(SLSide.TOP, previewPanel)
						.setStartSide(SLSide.LEFT, configSetupPanel)
						.setDelay(transitionDuration, configSetupPanel))
					.play();
				break;
		}
	}

	public void showUpdateSetup() {
		switch (Ctx.mode) {
			case INIT:
				Ctx.mode = Ctx.Mode.UPDATE;
				Ctx.fireModeChangedChanged();

				rootPanel.createTransition()
					.push(new SLKeyframe(updateCfg, transitionDuration)
						.setStartSide(SLSide.LEFT, configUpdatePanel)
						.setStartSide(SLSide.TOP, librarySelectionPanel)
						.setStartSide(SLSide.RIGHT, goPanel)
						.setEndSide(SLSide.TOP, logoLabel)
						.setDelay(transitionDuration, configUpdatePanel, librarySelectionPanel))
					.play();
				break;

			case SETUP:
				Ctx.mode = Ctx.Mode.UPDATE;
				Ctx.fireModeChangedChanged();

				rootPanel.createTransition()
					.push(new SLKeyframe(updateCfg, transitionDuration)
						.setEndSide(SLSide.TOP, previewPanel)
						.setEndSide(SLSide.LEFT, configSetupPanel)
						.setStartSide(SLSide.LEFT, configUpdatePanel)
						.setDelay(transitionDuration, versionLabel, configUpdatePanel))
					.play();
				break;
		}
	}

	public void showAdvancedSettings() {
		switch (Ctx.mode) {
			case SETUP:
				rootPanel.createTransition()
					.push(new SLKeyframe(setupAdvSettingsCfg, transitionDuration)
						.setEndSide(SLSide.BOTTOM, taskPanel)
						.setEndSide(SLSide.TOP, selectionPanel)
						.setEndSide(SLSide.RIGHT, librarySelectionPanel, previewPanel, goPanel)
						.setStartSide(SLSide.RIGHT, advancedSettingsPanel)
						.setDelay(transitionDuration, advancedSettingsPanel))
					.play();
				break;

			case UPDATE:
				rootPanel.createTransition()
					.push(new SLKeyframe(updateAdvSettingsCfg, transitionDuration)
						.setEndSide(SLSide.BOTTOM, taskPanel)
						.setEndSide(SLSide.TOP, selectionPanel)
						.setEndSide(SLSide.RIGHT, librarySelectionPanel, goPanel)
						.setStartSide(SLSide.RIGHT, advancedSettingsPanel)
						.setDelay(transitionDuration, advancedSettingsPanel))
					.play();
				break;
		}
	}

	public void hideAdvancedSettings() {
		switch (Ctx.mode) {
			case SETUP:
				rootPanel.createTransition()
					.push(new SLKeyframe(setupCfg, transitionDuration)
						.setEndSide(SLSide.RIGHT, advancedSettingsPanel)
						.setStartSide(SLSide.RIGHT, librarySelectionPanel, previewPanel, goPanel)
						.setStartSide(SLSide.BOTTOM, taskPanel)
						.setStartSide(SLSide.TOP, selectionPanel)
						.setDelay(transitionDuration, librarySelectionPanel, previewPanel, goPanel, taskPanel))
					.play();
				break;

			case UPDATE:
				rootPanel.createTransition()
					.push(new SLKeyframe(updateCfg, transitionDuration)
						.setEndSide(SLSide.RIGHT, advancedSettingsPanel)
						.setStartSide(SLSide.RIGHT, librarySelectionPanel, goPanel)
						.setStartSide(SLSide.BOTTOM, taskPanel)
						.setStartSide(SLSide.TOP, selectionPanel)
						.setDelay(transitionDuration, librarySelectionPanel, goPanel, taskPanel))
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
				rootPanel.createTransition()
					.push(new SLKeyframe(libraryInfoCfg, transitionDuration)
						.setEndSide(SLSide.LEFT, selectionPanel, configSetupPanel, versionLabel)
						.setEndSide(SLSide.RIGHT, previewPanel, goPanel)
						.setStartSide(SLSide.RIGHT, libraryInfoPanel)
						.setDelay(transitionDuration, libraryInfoPanel))
					.play();
				break;

			case UPDATE:
				rootPanel.createTransition()
					.push(new SLKeyframe(libraryInfoCfg, transitionDuration)
						.setEndSide(SLSide.LEFT, selectionPanel, configUpdatePanel, versionLabel)
						.setEndSide(SLSide.RIGHT, goPanel)
						.setStartSide(SLSide.RIGHT, libraryInfoPanel)
						.setDelay(transitionDuration, libraryInfoPanel))
					.play();
				break;
		}
	}

	public void hideLibraryInfo() {
		currentLibraryInfo = null;

		switch (Ctx.mode) {
			case SETUP:
				rootPanel.createTransition()
					.push(new SLKeyframe(setupCfg, transitionDuration)
						.setEndSide(SLSide.RIGHT, libraryInfoPanel)
						.setStartSide(SLSide.RIGHT, previewPanel, goPanel)
						.setStartSide(SLSide.LEFT, selectionPanel, configSetupPanel, versionLabel)
						.setDelay(transitionDuration, previewPanel, goPanel))
					.play();
				break;

			case UPDATE:
				rootPanel.createTransition()
					.push(new SLKeyframe(updateCfg, transitionDuration)
						.setEndSide(SLSide.RIGHT, libraryInfoPanel)
						.setStartSide(SLSide.RIGHT, goPanel)
						.setStartSide(SLSide.LEFT, selectionPanel, configUpdatePanel, versionLabel)
						.setDelay(transitionDuration, goPanel))
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

		rootPanel.createTransition()
			.push(new SLKeyframe(setupGenerationCfg, transitionDuration)
				.setEndSide(SLSide.TOP, selectionPanel, configSetupPanel, versionLabel, librarySelectionPanel)
				.setEndSide(SLSide.BOTTOM, taskPanel, goPanel)
				.setStartSide(SLSide.BOTTOM, processSetupPanel))
			.play();
	}

	public void hideGenerationCreatePanel() {
		isProcessSetupPanelOpen = false;

		rootPanel.createTransition()
			.push(new SLKeyframe(setupCfg, transitionDuration)
				.setEndSide(SLSide.BOTTOM, processSetupPanel)
				.setStartSide(SLSide.TOP, selectionPanel, configSetupPanel, versionLabel, librarySelectionPanel)
				.setStartSide(SLSide.BOTTOM, taskPanel, goPanel))
			.play();
	}

	public void showGenerationUpdatePanel() {
		rootPanel.createTransition()
			.push(new SLKeyframe(updateGenerationCfg, transitionDuration)
				.setEndSide(SLSide.TOP, selectionPanel, configUpdatePanel, versionLabel, librarySelectionPanel)
				.setEndSide(SLSide.BOTTOM, taskPanel, goPanel)
				.setStartSide(SLSide.BOTTOM, classpathsPanel)
				.setStartSide(SLSide.TOP, processUpdatePanel))
			.play();
	}

	public void hideGenerationUpdatePanel() {
		rootPanel.createTransition()
			.push(new SLKeyframe(updateCfg, transitionDuration)
				.setEndSide(SLSide.BOTTOM, classpathsPanel)
				.setEndSide(SLSide.TOP, processUpdatePanel)
				.setStartSide(SLSide.TOP, selectionPanel, configUpdatePanel, versionLabel, librarySelectionPanel)
				.setStartSide(SLSide.BOTTOM, taskPanel, goPanel))
			.play();
	}
}
