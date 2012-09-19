package aurelienribon.gdxsetupui.ui.panels;

import aurelienribon.gdxsetupui.ui.MainPanel;
import aurelienribon.ui.css.Style;
import aurelienribon.utils.SwingUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class SelectionPanel extends javax.swing.JPanel {
    public SelectionPanel(final MainPanel mainPanel) {
        initComponents();

		Style.registerCssClasses(headerPanel, ".header");
		Style.registerCssClasses(numberLabel, ".headerNumber");

		setupBtn.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			mainPanel.showCreateSetup();
		}});

		updateBtn.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {
			mainPanel.showUpdateSetup();
		}});

		SwingUtils.addWindowListener(this, new WindowAdapter() {
			@Override public void windowOpened(WindowEvent e) {
				setupBtn.requestFocusInWindow();
			}
		});
    }

	// -------------------------------------------------------------------------
	// Generated stuff
	// -------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        headerPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        numberLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        setupBtn = new javax.swing.JButton();
        updateBtn = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jLabel4.setText("<html> Do you want to create a new project, or update the libraries of an existing project?");
        jLabel4.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        numberLabel.setText("1");

        javax.swing.GroupLayout headerPanelLayout = new javax.swing.GroupLayout(headerPanel);
        headerPanel.setLayout(headerPanelLayout);
        headerPanelLayout.setHorizontalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, headerPanelLayout.createSequentialGroup()
                .addComponent(numberLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE))
        );
        headerPanelLayout.setVerticalGroup(
            headerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(numberLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(headerPanelLayout.createSequentialGroup()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        add(headerPanel, java.awt.BorderLayout.NORTH);

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        setupBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/gfx/ic_add.png"))); // NOI18N
        setupBtn.setText("Create new project");
        jPanel1.add(setupBtn);

        updateBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/gfx/ic_update.png"))); // NOI18N
        updateBtn.setText("Update existing project");
        jPanel1.add(updateBtn);

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel headerPanel;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel numberLabel;
    private javax.swing.JButton setupBtn;
    private javax.swing.JButton updateBtn;
    // End of variables declaration//GEN-END:variables

}
