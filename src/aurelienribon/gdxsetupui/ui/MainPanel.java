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
import aurelienribon.gdxsetupui.ui.panels.TaskPanel;
import aurelienribon.slidinglayout.SLAnimator;
import aurelienribon.slidinglayout.SLConfig;
import aurelienribon.slidinglayout.SLKeyframe;
import aurelienribon.slidinglayout.SLPanel;
import static aurelienribon.slidinglayout.SLSide.*;
import aurelienribon.ui.components.Button;
import aurelienribon.ui.components.PaintedPanel;
import aurelienribon.ui.css.Style;
import aurelienribon.utils.HttpUtils.DownloadListener;
import aurelienribon.utils.HttpUtils.DownloadTask;
import aurelienribon.utils.Res;
import aurelienribon.utils.SwingUtils;
import aurelienribon.utils.VersionLabel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.JLabel;
import org.apache.commons.io.IOUtils;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class MainPanel extends PaintedPanel {
	// Panels
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

	// Start panel components
	private final JLabel startLogoLabel = new JLabel(Res.getImage("gfx/logo.png"));
	private final JLabel startQuestionLabel = new JLabel("<html>Do you want to create"
		+ " a new project, or to update the libraries of an existing one?");
	private final Button startSetupBtn = new Button() {{setText("Create");}};
	private final Button startUpdateBtn = new Button() {{setText("Update");}};

	// Misc components
	private final Button changeModeBtn = new Button() {{setText("Change mode");}};

	// SlidingLayout
	private final SLPanel rootPanel = new SLPanel();
	private final float transitionDuration = 0.5f;
	private final int gap = 10;

	public MainPanel() {
		SwingUtils.importFont(Res.getStream("fonts/SquareFont.ttf"));
		setLayout(new BorderLayout());
		add(rootPanel, BorderLayout.CENTER);

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

		startSetupBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				showSetupView();
			}
		});

		startUpdateBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				showUpdateView();
			}
		});

		changeModeBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				showInitView();
			}
		});

		versionLabel.initAndCheck("3.0.0-beta", "versions",
			"http://libgdx.badlogicgames.com/nightlies/config/config.txt",
			"http://libgdx.badlogicgames.com/download.html");

		initStyle();
		initConfigurations();
		rootPanel.initialize(initCfg);

		SLAnimator.start();
		rootPanel.setTweenManager(SLAnimator.createTweenManager());
		taskPanel.setTweenManager(SLAnimator.createTweenManager());

		SwingUtils.addWindowListener(this, new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				DownloadTask task = Ctx.libs.downloadConfigFile();
				task.addListener(configFileDownloadListener);
				rootPanel.repaint();
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
	// Style
	// -------------------------------------------------------------------------

	private void initStyle() {
		Style.registerCssClasses(this, ".rootPanel");
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
		Style.registerCssClasses(startQuestionLabel, ".startQuestionLabel");
		Style.registerCssClasses(startSetupBtn, ".startButton");
		Style.registerCssClasses(startUpdateBtn, ".startButton");
		Style.registerCssClasses(changeModeBtn, ".bold");

		Component[] targets = new Component[] {
			this, configSetupPanel, configUpdatePanel, versionLabel,
			librarySelectionPanel, previewPanel, goPanel, taskPanel, advancedSettingsPanel,
			libraryInfoPanel, classpathsPanel, processSetupPanel, processUpdatePanel,
			startQuestionLabel, startSetupBtn, startUpdateBtn, changeModeBtn
		};

		Style style = new Style(Res.getUrl("css/style.css"));
		for (Component target : targets) Style.apply(target, style);
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
				.row(startLogoLabel.getPreferredSize().height)
				.row(1f)
				.col(1f)
				.col(startLogoLabel.getPreferredSize().width)
				.col(1f)
				.place(0, 1, startLogoLabel)
				.beginGrid(1, 1)
					.row(1f).row(50).row(80).row(1f)
					.col(1f).col(4.5f).col(1f)
					.place(1, 1, startQuestionLabel)
					.beginGrid(2, 1)
						.row(1f)
						.col(1f).col(1f)
						.place(0, 0, startSetupBtn)
						.place(0, 1, startUpdateBtn)
					.endGrid()
				.endGrid()
			.endGrid()
			.place(1, 0, taskPanel);

		setupCfg = new SLConfig(rootPanel)
			.gap(gap, gap)
			.row(1f).row(30).col(1f)
			.beginGrid(0, 0)
				.row(1f).col(1f).col(1f).col(1f)
				.beginGrid(0, 0)
					.row(configSetupPanel.getPreferredSize().height)
					.row(versionLabel.getPreferredSize().height)
					.col(1f)
					.place(0, 0, configSetupPanel)
					.place(1, 0, versionLabel)
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
			.beginGrid(1, 0)
				.row(1f).col(100).col(1f)
				.place(0, 0, changeModeBtn)
				.place(0, 1, taskPanel)
			.endGrid();

		updateCfg = new SLConfig(rootPanel)
			.gap(gap, gap)
			.row(1f).row(30).col(1f)
			.beginGrid(0, 0)
				.row(1f).col(1f).col(1f).col(1f)
				.beginGrid(0, 0)
					.row(configUpdatePanel.getPreferredSize().height)
					.row(versionLabel.getPreferredSize().height)
					.col(1f)
					.place(0, 0, configUpdatePanel)
					.place(1, 0, versionLabel)
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
			.beginGrid(1, 0)
				.row(1f).col(100).col(1f)
				.place(0, 0, changeModeBtn)
				.place(0, 1, taskPanel)
			.endGrid();

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
				.col(1f)
				.place(0, 0, configSetupPanel)
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
				.col(1f)
				.place(0, 0, configUpdatePanel)
			.endGrid()
			.place(0, 1, advancedSettingsPanel);

		updateGenerationCfg = new SLConfig(rootPanel)
			.gap(gap, gap)
			.row(1f).col(2f).col(1f)
			.place(0, 0, classpathsPanel)
			.place(0, 1, processUpdatePanel);
	}

	public void showSetupView() {
		Ctx.mode = Ctx.Mode.SETUP;
		Ctx.fireModeChangedChanged();

		rootPanel.createTransition()
			.push(new SLKeyframe(setupCfg, transitionDuration)
				.setStartSideForNewCmps(RIGHT)
				.setStartSide(LEFT, changeModeBtn)
				.setEndSideForOldCmps(LEFT))
			.play();
	}

	public void showUpdateView() {
		Ctx.mode = Ctx.Mode.UPDATE;
		Ctx.fireModeChangedChanged();

		rootPanel.createTransition()
			.push(new SLKeyframe(updateCfg, transitionDuration)
				.setStartSideForNewCmps(RIGHT)
				.setStartSide(LEFT, changeModeBtn)
				.setEndSideForOldCmps(LEFT))
			.play();
	}

	public void showInitView() {
		Ctx.mode = Ctx.Mode.INIT;
		Ctx.fireModeChangedChanged();

		rootPanel.createTransition()
			.push(new SLKeyframe(initCfg, transitionDuration)
				.setStartSideForNewCmps(LEFT)
				.setEndSideForOldCmps(RIGHT)
				.setEndSide(LEFT, changeModeBtn))
			.play();
	}

	public boolean showAdvancedSettings() {
		switch (Ctx.mode) {
			case SETUP:
				return rootPanel.createTransition()
					.push(new SLKeyframe(setupAdvSettingsCfg, transitionDuration)
						.setEndSideForOldCmps(BOTTOM)
						.setStartSideForNewCmps(TOP))
					.play();

			case UPDATE:
				return rootPanel.createTransition()
					.push(new SLKeyframe(updateAdvSettingsCfg, transitionDuration)
						.setEndSideForOldCmps(BOTTOM)
						.setStartSideForNewCmps(TOP))
					.play();
		}

		return false;
	}

	public boolean hideAdvancedSettings() {
		switch (Ctx.mode) {
			case SETUP:
				return rootPanel.createTransition()
					.push(new SLKeyframe(setupCfg, transitionDuration)
						.setEndSideForOldCmps(TOP)
						.setStartSideForNewCmps(BOTTOM))
					.play();

			case UPDATE:
				return rootPanel.createTransition()
					.push(new SLKeyframe(updateCfg, transitionDuration)
						.setEndSideForOldCmps(TOP)
						.setStartSideForNewCmps(BOTTOM))
					.play();
		}

		return false;
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
						.setEndSide(LEFT, configSetupPanel, versionLabel, changeModeBtn)
						.setEndSide(RIGHT, previewPanel, goPanel)
						.setStartSide(TOP, libraryInfoPanel)
						.setDelay(transitionDuration, libraryInfoPanel))
					.play();
				break;

			case UPDATE:
				rootPanel.createTransition()
					.push(new SLKeyframe(libraryInfoCfg, transitionDuration)
						.setEndSide(LEFT, configUpdatePanel, versionLabel, changeModeBtn)
						.setEndSide(RIGHT, goPanel)
						.setStartSide(TOP, libraryInfoPanel)
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
						.setEndSide(RIGHT, libraryInfoPanel)
						.setStartSide(RIGHT, previewPanel, goPanel)
						.setStartSide(LEFT, configSetupPanel, versionLabel, changeModeBtn)
						.setDelay(transitionDuration, previewPanel, goPanel))
					.play();
				break;

			case UPDATE:
				rootPanel.createTransition()
					.push(new SLKeyframe(updateCfg, transitionDuration)
						.setEndSide(RIGHT, libraryInfoPanel)
						.setStartSide(RIGHT, goPanel)
						.setStartSide(LEFT, configUpdatePanel, versionLabel, changeModeBtn)
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
				.setEndSide(TOP, configSetupPanel, versionLabel, librarySelectionPanel)
				.setEndSide(BOTTOM, taskPanel, changeModeBtn, goPanel)
				.setStartSide(BOTTOM, processSetupPanel))
			.play();
	}

	public void hideGenerationCreatePanel() {
		isProcessSetupPanelOpen = false;

		rootPanel.createTransition()
			.push(new SLKeyframe(setupCfg, transitionDuration)
				.setEndSide(BOTTOM, processSetupPanel)
				.setStartSide(TOP, configSetupPanel, versionLabel, librarySelectionPanel)
				.setStartSide(BOTTOM, taskPanel, changeModeBtn, goPanel))
			.play();
	}

	public void showGenerationUpdatePanel() {
		rootPanel.createTransition()
			.push(new SLKeyframe(updateGenerationCfg, transitionDuration)
				.setEndSide(TOP, configUpdatePanel, versionLabel, librarySelectionPanel)
				.setEndSide(BOTTOM, taskPanel, changeModeBtn, goPanel)
				.setStartSide(BOTTOM, classpathsPanel)
				.setStartSide(TOP, processUpdatePanel))
			.play();
	}

	public void hideGenerationUpdatePanel() {
		rootPanel.createTransition()
			.push(new SLKeyframe(updateCfg, transitionDuration)
				.setEndSide(BOTTOM, classpathsPanel)
				.setEndSide(TOP, processUpdatePanel)
				.setStartSide(TOP, configUpdatePanel, versionLabel, librarySelectionPanel)
				.setStartSide(BOTTOM, taskPanel, changeModeBtn, goPanel))
			.play();
	}
}
