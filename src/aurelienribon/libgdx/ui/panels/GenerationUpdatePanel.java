package aurelienribon.libgdx.ui.panels;

import aurelienribon.libgdx.ui.MainPanel;
import aurelienribon.ui.css.Style;
import javax.swing.SwingUtilities;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class GenerationUpdatePanel extends javax.swing.JPanel {
    public GenerationUpdatePanel(final MainPanel mainPanel) {
        initComponents();

		Style.registerCssClasses(jScrollPane1, ".frame");
		Style.registerCssClasses(progressArea, ".progressArea");
    }

	public void generate() {
		progressArea.setText("");

//		final ProjectSetup setup = new ProjectSetup(Ctx.cfgCreate, Ctx.libs);
//
//		new Thread(new Runnable() {
//			@Override public void run() {
//				try {
//					report("Decompressing projects...");
//					setup.inflateProjects();
//					report(" done\nDecompressing libraries...");
//					setup.inflateLibraries();
//					report(" done\nConfiguring libraries...");
//					setup.configureLibraries();
//					report(" done\nPost-processing files...");
//					setup.postProcess();
//					report(" done\nCopying projects...");
//					setup.copy();
//					report(" done\nCleaning...");
//					setup.clean();
//					report(" done\nAll done!");
//				} catch (final IOException ex) {
//					report("\n[error] " + ex.getMessage());
//					report("\nCleaning...");
//					setup.clean();
//					report("done");
//				}
//			}
//		}).start();
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

        progressArea.setEditable(false);
        progressArea.setColumns(20);
        progressArea.setRows(5);
        jScrollPane1.setViewportView(progressArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea progressArea;
    // End of variables declaration//GEN-END:variables

}
