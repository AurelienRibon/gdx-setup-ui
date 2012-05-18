
import aurelienribon.libgdx.ui.Ctx;
import aurelienribon.libgdx.ui.MainPanel;
import aurelienribon.ui.components.ArStyle;
import aurelienribon.ui.css.swing.SwingStyle;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class Main {
	public static void main(String[] args) {
		parseArgs(args);

		SwingUtilities.invokeLater(new Runnable() {
			@Override public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException ex) {
				} catch (InstantiationException ex) {
				} catch (IllegalAccessException ex) {
				} catch (UnsupportedLookAndFeelException ex) {
				}

				SwingStyle.init();
				ArStyle.init();

				JFrame frame = new JFrame("LibGDX Project Setup");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setContentPane(new MainPanel());
				frame.setSize(1000, 550);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	private static void parseArgs(String[] args) {
		for (int i=0; i<args.length; i++) {
			if (args[i].equals("-test") && i<args.length) {
				try {
					Ctx.testUrl = new URL(args[i+1]);
				} catch (MalformedURLException ex) {
					System.err.println("[warning] Test url is malformed");
				}
			}
		}
	}
}
