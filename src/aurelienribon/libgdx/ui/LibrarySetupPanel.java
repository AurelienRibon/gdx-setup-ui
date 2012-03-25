package aurelienribon.libgdx.ui;

import aurelienribon.libgdx.ProjectConfiguration;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class LibrarySetupPanel extends javax.swing.JPanel {
	private final ProjectConfiguration cfg = AppContext.inst().getConfig();
	private File selectedFile;

    public LibrarySetupPanel() {
        initComponents();

		browseBtn.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {browse();}});
		getStableBtn.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {getStable();}});
		getNightliesBtn.addActionListener(new ActionListener() {@Override public void actionPerformed(ActionEvent e) {getNightlies();}});

		for (File file : new File(".").listFiles()) {
			if (file.isFile() && file.getName().equals("libgdx-nightly-latest.zip")) select(file);
			else if (file.isFile() && file.getName().equals("libgdx-0.9.2.zip")) select(file);
		}
    }

	private void browse() {
		String path = selectedFile != null ? selectedFile.getPath() : ".";
		JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

		JFileChooser chooser = new JFileChooser(new File(path));
		chooser.setFileFilter(new FileNameExtensionFilter("Zip files (*.zip)", "zip"));
		chooser.setDialogTitle("Please select the libgdx zip archive");

		if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			select(chooser.getSelectedFile());
		}
	}

	private void getStable() {
		String input = "http://libgdx.googlecode.com/files/libgdx-0.9.2.zip";
		String output = "libgdx-0.9.2.zip";
		getFile(input, output);
	}

	private void getNightlies() {
		String input = "http://libgdx.badlogicgames.com/nightlies/libgdx-nightly-latest.zip";
		String output = "libgdx-nightly-latest.zip";
		getFile(input, output);
	}

	private void getFile(String input, final String output) {
		final File zipFile = new File(output);

		DownloadDialog.Callback callback = new DownloadDialog.Callback() {
			@Override public void completed() {select(zipFile);}
			@Override public void canceled() {}
		};

		JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
		DownloadDialog dialog = new DownloadDialog(frame, callback, input, output);
		dialog.setLocationRelativeTo(frame);
		dialog.setVisible(true);
	}

	private void select(File zipFile) {
		selectedFile = zipFile;
		try {pathField.setText(zipFile.getCanonicalPath());} catch (IOException ex) {}
		cfg.setLibraryPath(zipFile.getPath());
		AppContext.inst().fireConfigChangedEvent();
	}

	// -------------------------------------------------------------------------
	// Generated stuff
	// -------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        pathField = new javax.swing.JTextField();
        browseBtn = new javax.swing.JButton();
        getStableBtn = new javax.swing.JButton();
        getNightliesBtn = new javax.swing.JButton();

        jLabel1.setText("Path");

        pathField.setEditable(false);

        browseBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/gfx/ic_browse.png"))); // NOI18N

        getStableBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/gfx/ic_download.png"))); // NOI18N
        getStableBtn.setText("Get stable");

        getNightliesBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/gfx/ic_download.png"))); // NOI18N
        getNightliesBtn.setText("Get nightlies");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pathField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browseBtn))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(getStableBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(getNightliesBtn)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(pathField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addComponent(browseBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(getStableBtn)
                    .addComponent(getNightliesBtn))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {browseBtn, jLabel1, pathField});

    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseBtn;
    private javax.swing.JButton getNightliesBtn;
    private javax.swing.JButton getStableBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField pathField;
    // End of variables declaration//GEN-END:variables

}
