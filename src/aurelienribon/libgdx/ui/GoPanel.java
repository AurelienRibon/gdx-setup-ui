package aurelienribon.libgdx.ui;

import aurelienribon.libgdx.ProjectConfiguration;
import aurelienribon.libgdx.ProjectSetup;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class GoPanel extends javax.swing.JPanel {
    public GoPanel() {
        initComponents();
		AppContext.inst().addListener(new AppContext.Listener() {@Override public void configChanged() {update();}});

		goBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(GoPanel.this);
				ProjectConfiguration cfg = AppContext.inst().getConfig();

				try {
					ProjectSetup setup = new ProjectSetup(cfg);
					setup.inflateProjects();
					setup.inflateLibrary();
					setup.copy();
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(frame, ex.getMessage());
				}
			}
		});

		update();
    }

	private void update() {
		goBtn.setEnabled(AppContext.inst().getConfig().isValid());
	}

	// -------------------------------------------------------------------------
	// Generated stuff
	// -------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        goBtn = new javax.swing.JButton();

        goBtn.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        goBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/gfx/ic_run.png"))); // NOI18N
        goBtn.setText("Generate projects");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(goBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(goBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton goBtn;
    // End of variables declaration//GEN-END:variables

}
