package aurelienribon.libgdx.ui;

import aurelienribon.libgdx.ProjectConfiguration;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class ConfigPanel extends javax.swing.JPanel {
	private final ProjectConfiguration cfg = AppContext.inst().getConfig();

    public ConfigPanel() {
        initComponents();

		nameField.setText(cfg.getProjectName());
		packageField.setText(cfg.getPackageName());

		try {
			File destDir = new File(cfg.getDestinationPath());
			destinationField.setText(destDir.getCanonicalPath());
		} catch (IOException ex) {
		}

		nameField.addMouseListener(mouseListener);
		packageField.addMouseListener(mouseListener);

		nameField.addKeyListener(keyListener);
		packageField.addKeyListener(keyListener);
		browseBtn.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {browse();}});

		genDesktopPrjChk.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {update();}});
		genAndroidPrjChk.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {update();}});

		update();

		nameField.requestFocusInWindow();
		nameField.selectAll();
    }

	private void browse() {
		String path = cfg.getDestinationPath();
		JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

		JFileChooser chooser = new JFileChooser(new File(path));
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("Select the destination folder");

		if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			destinationField.setText(chooser.getSelectedFile().getPath());
			update();
		}
	}

	private void update() {
		cfg.setProjectName(nameField.getText());
		cfg.setPackageName(packageField.getText());
		cfg.setDestinationPath(destinationField.getText());
		cfg.setDesktopIncluded(genDesktopPrjChk.isSelected());
		cfg.setAndroidIncluded(genAndroidPrjChk.isSelected());
		AppContext.inst().fireConfigChangedEvent();
	}

	private final KeyListener keyListener = new KeyAdapter() {
		@Override
		public void keyReleased(KeyEvent e) {
			update();
		}
	};

	private final MouseListener mouseListener = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			JTextField field = (JTextField) e.getSource();
			if (!field.isFocusOwner()) field.selectAll();
		}
	};

	// -------------------------------------------------------------------------
	// Generated stuff
	// -------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        packageField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        destinationField = new javax.swing.JTextField();
        genCommonPrjChk = new javax.swing.JCheckBox();
        genDesktopPrjChk = new javax.swing.JCheckBox();
        genAndroidPrjChk = new javax.swing.JCheckBox();
        browseBtn = new javax.swing.JButton();

        jLabel1.setText("Name");

        jLabel2.setText("Package");

        jLabel3.setText("Destination");

        destinationField.setEditable(false);

        genCommonPrjChk.setSelected(true);
        genCommonPrjChk.setText("generate common project (required)");
        genCommonPrjChk.setEnabled(false);

        genDesktopPrjChk.setSelected(true);
        genDesktopPrjChk.setText("generate desktop project");

        genAndroidPrjChk.setSelected(true);
        genAndroidPrjChk.setText("generate android project");

        browseBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/gfx/ic_browse.png"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(destinationField, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browseBtn))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nameField)
                            .addComponent(packageField)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(genAndroidPrjChk)
                            .addComponent(genDesktopPrjChk)
                            .addComponent(genCommonPrjChk))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(packageField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(destinationField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseBtn))
                .addGap(18, 18, 18)
                .addComponent(genCommonPrjChk)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(genDesktopPrjChk)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(genAndroidPrjChk)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {browseBtn, destinationField, jLabel1, jLabel2, jLabel3, nameField, packageField});

    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseBtn;
    private javax.swing.JTextField destinationField;
    private javax.swing.JCheckBox genAndroidPrjChk;
    private javax.swing.JCheckBox genCommonPrjChk;
    private javax.swing.JCheckBox genDesktopPrjChk;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField nameField;
    private javax.swing.JTextField packageField;
    // End of variables declaration//GEN-END:variables

}
