package aurelienribon.libgdx.ui;

import aurelienribon.ui.components.PaintedPanel;
import aurelienribon.ui.css.Style;
import aurelienribon.utils.Res;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class MainPanel3 extends PaintedPanel {
    public MainPanel3() {
		try {
			Font font1 = Font.createFont(Font.TRUETYPE_FONT, Res.getStream("fonts/SquareFont.ttf"));
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font1);
		} catch (FontFormatException ex) {
		} catch (IOException ex) {
		}

        initComponents();

		Style.registerCssClasses(this, ".rootPanel");
		Style.registerCssClasses(librarySetupPanel, ".groupPanel", "#librarySetupPanel");
		Style.registerCssClasses(configPanel, ".groupPanel", "#configPanel");
		Style.registerCssClasses(selectionPanel, ".groupPanel", "#selectionPanel");
		Style.registerCssClasses(resultPanel, ".groupPanel", "#resultPanel");
		Style.registerCssClasses(goPanel, ".groupPanel", "#goPanel");
		Style.registerCssClasses(versionLabel, ".versionLabel");
		Style.apply(this, new Style(Res.getUrl("css/style.css")));

		librarySetupPanel.init();
		goPanel.init();

		versionLabel.initAndCheck("2.0.2", "versions",
			"http://libgdx.badlogicgames.com/nightlies/config/config.txt",
			"http://libgdx.badlogicgames.com/download.html");
    }

	// -------------------------------------------------------------------------
	// Generated stuff
	// -------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        groupsWrapper = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        configPanel = new aurelienribon.libgdx.ui.panels.ConfigPanel();
        versionLabel = new aurelienribon.utils.VersionLabel();
        selectionPanel = new aurelienribon.libgdx.ui.panels.SelectionPanel();
        jPanel3 = new javax.swing.JPanel();
        librarySetupPanel = new aurelienribon.libgdx.ui.panels.LibrarySetupPanel();
        jPanel1 = new javax.swing.JPanel();
        goPanel = new aurelienribon.libgdx.ui.panels.GoPanel();
        resultPanel = new aurelienribon.libgdx.ui.panels.ResultPanel();

        setLayout(new java.awt.BorderLayout());

        groupsWrapper.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));
        groupsWrapper.setOpaque(false);
        groupsWrapper.setLayout(new java.awt.GridLayout(1, 0, 15, 0));

        jPanel4.setOpaque(false);

        versionLabel.setText("versionLabel1");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(configPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
            .addComponent(versionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(selectionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addComponent(selectionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(configPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(versionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        groupsWrapper.add(jPanel4);

        jPanel3.setOpaque(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(librarySetupPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(librarySetupPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)
        );

        groupsWrapper.add(jPanel3);

        jPanel1.setOpaque(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(resultPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
            .addComponent(goPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(resultPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(goPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        groupsWrapper.add(jPanel1);

        add(groupsWrapper, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private aurelienribon.libgdx.ui.panels.ConfigPanel configPanel;
    private aurelienribon.libgdx.ui.panels.GoPanel goPanel;
    private javax.swing.JPanel groupsWrapper;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private aurelienribon.libgdx.ui.panels.LibrarySetupPanel librarySetupPanel;
    private aurelienribon.libgdx.ui.panels.ResultPanel resultPanel;
    private aurelienribon.libgdx.ui.panels.SelectionPanel selectionPanel;
    private aurelienribon.utils.VersionLabel versionLabel;
    // End of variables declaration//GEN-END:variables

}
