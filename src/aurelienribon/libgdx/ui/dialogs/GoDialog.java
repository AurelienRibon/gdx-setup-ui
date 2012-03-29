package aurelienribon.libgdx.ui.dialogs;

import aurelienribon.libgdx.ProjectConfiguration;
import aurelienribon.libgdx.ProjectSetup;
import aurelienribon.libgdx.ui.AppContext;
import aurelienribon.ui.css.Style;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import res.Res;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class GoDialog extends javax.swing.JDialog {
	private final ProjectSetup setup;

    public GoDialog(JFrame parent) {
        super(parent, true);
        initComponents();

		Style.registerCssClasses(rootPanel, ".rootPanel");
		Style.registerCssClasses(title1, ".titleLabel");
		Style.registerCssClasses(title2, ".titleLabel");
		Style.registerCssClasses(importQuestion, ".questionLabel");
		Style.registerCssClasses(fixHtmlQuestion, ".questionLabel");
		Style.apply(getContentPane(), new Style(Res.class.getResource("style.css")));

		importQuestion.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(MouseEvent e) {
				ImportHelpDialog dialog = new ImportHelpDialog(null);
				dialog.setLocationRelativeTo(GoDialog.this);
				dialog.pack();
				dialog.setVisible(true);
			}
		});

		fixHtmlQuestion.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(MouseEvent e) {
				FixHtmlHelpDialog dialog = new FixHtmlHelpDialog(null);
				dialog.setLocationRelativeTo(GoDialog.this);
				dialog.pack();
				dialog.setVisible(true);
			}
		});

		step1.setVisible(false);
		step2.setVisible(false);
		step3.setVisible(false);
		done.setVisible(false);
		title2.setVisible(false);
		importQuestion.setVisible(false);
		fixHtmlQuestion.setVisible(false);

		ProjectConfiguration cfg = AppContext.inst().getConfig();
		setup = new ProjectSetup(cfg);

		new Thread(new Runnable() {
			@Override public void run() {
				try {
					SwingUtilities.invokeLater(new Runnable() {@Override public void run() {step1.setVisible(true);}});
					setup.inflateProjects();
					SwingUtilities.invokeLater(new Runnable() {@Override public void run() {step2.setVisible(true);}});
					setup.inflateLibraries();
					SwingUtilities.invokeLater(new Runnable() {@Override public void run() {step3.setVisible(true);}});
					setup.copy();
					SwingUtilities.invokeLater(new Runnable() {@Override public void run() {done.setVisible(true);}});
					SwingUtilities.invokeLater(new Runnable() {@Override public void run() {title2.setVisible(true);}});
					SwingUtilities.invokeLater(new Runnable() {@Override public void run() {importQuestion.setVisible(true);}});
					SwingUtilities.invokeLater(new Runnable() {@Override public void run() {fixHtmlQuestion.setVisible(true);}});
				} catch (final IOException ex) {
					SwingUtilities.invokeLater(new Runnable() {@Override public void run() {
						JOptionPane.showMessageDialog(GoDialog.this, ex.getMessage());
					}});
				}
			}
		}).start();
    }

	// -------------------------------------------------------------------------
	// Generated stuff
	// -------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rootPanel = new aurelienribon.ui.components.PaintedPanel();
        title1 = new javax.swing.JLabel();
        step1 = new javax.swing.JLabel();
        step2 = new javax.swing.JLabel();
        step3 = new javax.swing.JLabel();
        done = new javax.swing.JLabel();
        title2 = new javax.swing.JLabel();
        importQuestion = new javax.swing.JLabel();
        fixHtmlQuestion = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Generation");

        title1.setText("Generation in progress");

        step1.setText("Decompressing projects...");

        step2.setText("Decompressing libraries...");

        step3.setText("Copying projects...");

        done.setText("Done!");

        title2.setText("Frequently Asked Questions");

        importQuestion.setText("How do I import the projects into eclipse?");

        fixHtmlQuestion.setText("How do I fix the \"gwt-servlet not found\" error in my html project?");

        javax.swing.GroupLayout rootPanelLayout = new javax.swing.GroupLayout(rootPanel);
        rootPanel.setLayout(rootPanelLayout);
        rootPanelLayout.setHorizontalGroup(
            rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rootPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(title1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(rootPanelLayout.createSequentialGroup()
                        .addGroup(rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(step1)
                            .addComponent(step2)
                            .addComponent(step3)
                            .addComponent(done)
                            .addComponent(importQuestion)
                            .addComponent(fixHtmlQuestion))
                        .addGap(0, 144, Short.MAX_VALUE))
                    .addComponent(title2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        rootPanelLayout.setVerticalGroup(
            rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rootPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(title1)
                .addGap(18, 18, 18)
                .addComponent(step1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(step2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(step3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(done)
                .addGap(18, 18, 18)
                .addComponent(title2)
                .addGap(18, 18, 18)
                .addComponent(importQuestion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fixHtmlQuestion)
                .addContainerGap(125, Short.MAX_VALUE))
        );

        getContentPane().add(rootPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel done;
    private javax.swing.JLabel fixHtmlQuestion;
    private javax.swing.JLabel importQuestion;
    private aurelienribon.ui.components.PaintedPanel rootPanel;
    private javax.swing.JLabel step1;
    private javax.swing.JLabel step2;
    private javax.swing.JLabel step3;
    private javax.swing.JLabel title1;
    private javax.swing.JLabel title2;
    // End of variables declaration//GEN-END:variables

}
