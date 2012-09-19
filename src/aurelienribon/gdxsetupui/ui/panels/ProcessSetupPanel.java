package aurelienribon.gdxsetupui.ui.panels;

import aurelienribon.gdxsetupui.ProjectSetup;
import aurelienribon.gdxsetupui.ui.Ctx;
import aurelienribon.gdxsetupui.ui.MainPanel;
import aurelienribon.gdxsetupui.ui.dialogs.HelpFixHtmlDialog;
import aurelienribon.gdxsetupui.ui.dialogs.HelpImportDialog;
import aurelienribon.ui.css.Style;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.SwingUtilities;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class ProcessSetupPanel extends javax.swing.JPanel {
    public ProcessSetupPanel(final MainPanel mainPanel) {
        initComponents();

		Style.registerCssClasses(jScrollPane1, ".frame");
		Style.registerCssClasses(importQuestion, ".linkLabel");
		Style.registerCssClasses(fixHtmlQuestion, ".linkLabel");
		Style.registerCssClasses(paintedPanel1, ".optionGroupPanel");
		Style.registerCssClasses(progressArea, ".progressArea");

		importQuestion.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		fixHtmlQuestion.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		startBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				generate();
			}
		});

		backBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				mainPanel.hideGenerationCreatePanel();
			}
		});

		importQuestion.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(MouseEvent e) {
				HelpImportDialog dialog = new HelpImportDialog(null);
				dialog.setLocationRelativeTo(ProcessSetupPanel.this);
				dialog.pack();
				dialog.setVisible(true);
			}
		});

		fixHtmlQuestion.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(MouseEvent e) {
				HelpFixHtmlDialog dialog = new HelpFixHtmlDialog(null);
				dialog.setLocationRelativeTo(ProcessSetupPanel.this);
				dialog.pack();
				dialog.setVisible(true);
			}
		});
    }

	private void generate() {
		progressArea.setText("");

		final ProjectSetup setup = new ProjectSetup(Ctx.cfgSetup, Ctx.libs);

		new Thread(new Runnable() {
			@Override public void run() {
				try {
					report("Decompressing projects...");
					setup.inflateProjects();
					report(" done\nDecompressing libraries...");
					setup.inflateLibraries();
					report(" done\nConfiguring libraries...");
					setup.configureLibraries();
					report(" done\nPost-processing files...");
					setup.postProcess();
					report(" done\nCopying projects...");
					setup.copy();
					report(" done\nCleaning...");
					setup.clean();
					report(" done\nAll done!");
				} catch (final IOException ex) {
					report("\n[error] " + ex.getMessage());
					report("\nCleaning...");
					setup.clean();
					report("done");
				}
			}
		}).start();
	}

	private void report(final String txt) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override public void run() {
				progressArea.append(txt);
			}
		});
	}

	// -------------------------------------------------------------------------
	// Generated stuff
	// -------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        progressArea = new javax.swing.JTextArea();
        paintedPanel1 = new aurelienribon.ui.components.PaintedPanel();
        importQuestion = new javax.swing.JLabel();
        fixHtmlQuestion = new javax.swing.JLabel();
        startBtn = new javax.swing.JButton();
        backBtn = new javax.swing.JButton();

        progressArea.setEditable(false);
        progressArea.setColumns(20);
        progressArea.setRows(5);
        jScrollPane1.setViewportView(progressArea);

        paintedPanel1.setOpaque(false);

        importQuestion.setText("How do I import the projects into eclipse?");

        fixHtmlQuestion.setText("How do I fix the \"gwt-servlet not found\" error in my html project?");

        javax.swing.GroupLayout paintedPanel1Layout = new javax.swing.GroupLayout(paintedPanel1);
        paintedPanel1.setLayout(paintedPanel1Layout);
        paintedPanel1Layout.setHorizontalGroup(
            paintedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paintedPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(paintedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(importQuestion)
                    .addComponent(fixHtmlQuestion))
                .addContainerGap(57, Short.MAX_VALUE))
        );
        paintedPanel1Layout.setVerticalGroup(
            paintedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(paintedPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(importQuestion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fixHtmlQuestion)
                .addContainerGap())
        );

        startBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/gfx/ic_ok.png"))); // NOI18N
        startBtn.setText("Generate!");

        backBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/gfx/ic_back.png"))); // NOI18N
        backBtn.setText("Go back");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(paintedPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(startBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(backBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {backBtn, startBtn});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startBtn)
                    .addComponent(backBtn))
                .addGap(18, 18, 18)
                .addComponent(paintedPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backBtn;
    private javax.swing.JLabel fixHtmlQuestion;
    private javax.swing.JLabel importQuestion;
    private javax.swing.JScrollPane jScrollPane1;
    private aurelienribon.ui.components.PaintedPanel paintedPanel1;
    private javax.swing.JTextArea progressArea;
    private javax.swing.JButton startBtn;
    // End of variables declaration//GEN-END:variables

}
