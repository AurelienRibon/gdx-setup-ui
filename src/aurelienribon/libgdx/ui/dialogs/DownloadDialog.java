package aurelienribon.libgdx.ui.dialogs;

import aurelienribon.ui.css.Style;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.commons.io.FileUtils;
import res.Res;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class DownloadDialog extends javax.swing.JDialog {
	private final Callback callback;
	private final String input;
	private final String output;
	private boolean run = false;

	public DownloadDialog(JFrame parent, Callback callback, String input, String output) {
		super(parent, true);

		this.callback = callback;
		this.input = input;
		this.output = output;

		initComponents();
		nameLabel.setText(input);
		countLabel.setText("...");

		addWindowListener(new WindowAdapter() {
			@Override public void windowClosing(WindowEvent e) {
				run = false;
			}
		});

		try {
			new File(output).getCanonicalFile().getParentFile().mkdirs();
		} catch (IOException ex) {
		}

		downloadAsync(input, output);

		Style.registerCssClasses(rootPanel, ".rootPanel");
		Style.apply(getContentPane(), new Style(Res.class.getResource("style.css")));
	}

	public interface Callback {
		public void completed();
		public void canceled();
	}

	private void downloadAsync(final String in, final String out) {
		Thread th = new Thread(new Runnable() {@Override public void run() {download();}});
		th.start();
	}

	private void download() {
		run = true;

		OutputStream os = null;
		InputStream is = null;

		try {
			URL url = new URL(input);
			URLConnection connection = url.openConnection();
			connection.connect();

			is = new BufferedInputStream(url.openStream());
			os = new FileOutputStream(output + ".tmp");

			byte[] data = new byte[1024];
			long length = connection.getContentLengthLong();
			long total = 0;

			int count;
			while (run && (count = is.read(data)) != -1) {
                total += count;
                os.write(data, 0, count);
				setProgress(total, length);
            }

		} catch (MalformedURLException ex) {
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage());
			run = false;
		} finally {
			end(is, os);
		}
	}

	private void setProgress(long total, long length) {
		progressBar.setValue((int) Math.round((double) total / length * 100));
		countLabel.setText((total/1024) + " / " + (length/1024));
	}

	private void end(InputStream is, OutputStream os) {
		if (os != null) try {os.flush(); os.close();} catch (IOException ex1) {}
		if (is != null) try {is.close();} catch (IOException ex1) {}
		dispose();

		if (run == true) {
			try {
				FileUtils.moveFile(new File(output + ".tmp"), new File(output));
			} catch (IOException ex) {
			}
			callback.completed();
		} else {
			File file = new File(output + ".tmp");
			file.delete();
			callback.canceled();
		}
	}

	// -------------------------------------------------------------------------
	// Generated stuff
	// -------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rootPanel = new aurelienribon.ui.components.PaintedPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        countLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Download in progress...");
        setResizable(false);

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/res/gfx/ic64_download.png"))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("Downloading: ");

        countLabel.setText("xxx");

        jLabel2.setText("KiloBytes downloaded: ");

        nameLabel.setText("xxx");

        javax.swing.GroupLayout rootPanelLayout = new javax.swing.GroupLayout(rootPanel);
        rootPanel.setLayout(rootPanelLayout);
        rootPanelLayout.setHorizontalGroup(
            rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rootPanelLayout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(rootPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(countLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(rootPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 471, Short.MAX_VALUE))
                .addContainerGap())
        );
        rootPanelLayout.setVerticalGroup(
            rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(rootPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(countLabel))
                .addContainerGap())
        );

        getContentPane().add(rootPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel countLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JProgressBar progressBar;
    private aurelienribon.ui.components.PaintedPanel rootPanel;
    // End of variables declaration//GEN-END:variables

}
