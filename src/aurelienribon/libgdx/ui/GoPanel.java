package aurelienribon.libgdx.ui;

import aurelienribon.libgdx.ui.dialogs.GoDialog;
import aurelienribon.ui.css.Style;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class GoPanel extends javax.swing.JPanel {
    public GoPanel() {
        initComponents();
		Style.registerCssClasses(headerPanel, ".header");
		Style.registerCssClasses(numberLabel, ".headerNumber");

		AppContext.inst().addListener(new AppContext.Listener() {@Override public void configChanged() {update();}});

		goBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(GoPanel.this);
				GoDialog dialog = new GoDialog(frame);
				dialog.setLocationRelativeTo(frame);
				dialog.setVisible(true);
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

        jPanel1 = new javax.swing.JPanel();
        goBtn = new javax.swing.JButton();
        headerPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        numberLabel = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setOpaque(false);

        goBtn.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        goBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/gfx/ic_run.png"))); // NOI18N
        goBtn.setText("Generate projects");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(goBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(goBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        add(jPanel1, java.awt.BorderLayout.CENTER);

        jLabel4.setText("<html> Ready to go?");
        jLabel4.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        numberLabel.setText("4");

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, headerPanelLayout.createSequentialGroup()
                .addComponent(numberLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE))
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(numberLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        add(headerPanel, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton goBtn;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel numberLabel;
    // End of variables declaration//GEN-END:variables

}
