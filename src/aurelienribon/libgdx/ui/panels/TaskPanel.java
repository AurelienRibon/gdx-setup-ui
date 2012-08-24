package aurelienribon.libgdx.ui.panels;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.ui.css.Style;
import aurelienribon.utils.Animator;
import aurelienribon.utils.HttpUtils;
import aurelienribon.utils.HttpUtils.DownloadListener;
import aurelienribon.utils.HttpUtils.DownloadTask;
import aurelienribon.utils.Res;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class TaskPanel extends JPanel {
	private final List<Tile> tiles = new ArrayList<Tile>();
	private TweenManager tweenManager;

	public TaskPanel() {
		setLayout(null);
		setPreferredSize(new Dimension(50, 30));
		Style.registerCssClasses(this, ".taskBar");

		HttpUtils.addListener(new HttpUtils.Listener() {
			@Override public void newDownload(DownloadTask task) {
				addDownloadTile(task);
			}
		});
	}

	public void setTweenManager(TweenManager tweenManager) {
		this.tweenManager = tweenManager;
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private void addDownloadTile(DownloadTask task) {
		final DownloadTile tile = new DownloadTile(task);
		tile.setLocation(getNextTileX() + getWidth(), 2);

		Tween.to(tile, Animator.JComponentAccessor.X, 2)
			.target(getNextTileX())
			.ease(Quad.OUT)
			.start(tweenManager);

		task.addListener(new DownloadListener() {
			@Override public void onUpdate(int length, int totalLength) {tile.setCurrentSize(length, totalLength);}
			@Override public void onComplete() {tile.setToComplete(); removeTile(tile, true);}
			@Override public void onError(IOException ex) {tile.setToError("IOException: " + ex.getMessage());}
		});

		add(tile);
		tiles.add(tile);
	}

	private int getNextTileX() {
		return 2 + 202*tiles.size();
	}

	private int getTileX(Tile tile) {
		int idx = tiles.indexOf(tile);
		return 2 + 202*idx;
	}

	private void removeTile(Tile tile, boolean useDelay) {
		Tween.to(tile, Animator.JComponentAccessor.Y, 0.3f)
			.targetRelative(50)
			.ease(Quad.IN)
			.delay(useDelay ? 3.5f : 0)
			.setCallback(removeTileCallback)
			.setUserData(tile)
			.start(tweenManager);
	}

	private final TweenCallback removeTileCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			tiles.remove((Tile) source.getUserData());

			for (Tile tile : tiles) {
				tweenManager.killTarget(tile, Animator.JComponentAccessor.X);
				Tween.to(tile, Animator.JComponentAccessor.X, 2)
					.target(getTileX(tile))
					.ease(Quad.OUT)
					.start(tweenManager);
			}
		}
	};

	// -------------------------------------------------------------------------

	private class Tile extends JPanel {
	}

	private class DownloadTile extends Tile {
		private final JLabel logoLabel = new JLabel();
		private final JLabel titleLabel = new JLabel();
		private final JLabel stateLabel = new JLabel();
		private final JLabel cancelLabel = new JLabel(Res.getImage("gfx/ic_cancel.png"));

		public DownloadTile(final DownloadTask task) {
			setBackground(Color.LIGHT_GRAY);
			setLayout(null);
			setSize(200, 26);

			if (task.getTag().startsWith("Master")) logoLabel.setIcon(Res.getImage("gfx/ic24_cog.png"));
			else if (task.getTag().startsWith("Def")) logoLabel.setIcon(Res.getImage("gfx/ic24_file.png"));
			else if (task.getTag().startsWith("Version")) logoLabel.setIcon(Res.getImage("gfx/ic24_cog.png"));
			else logoLabel.setIcon(Res.getImage("gfx/ic24_download.png"));

			logoLabel.setBounds(0, 1, 24, 24);
			titleLabel.setText(task.getTag());
			titleLabel.setVerticalAlignment(SwingConstants.TOP);
			titleLabel.setBounds(29, 0, getWidth()-30, getHeight());
			stateLabel.setText("0 / ?? (?%)");
			stateLabel.setVerticalAlignment(SwingConstants.BOTTOM);
			stateLabel.setBounds(29, 0, getWidth()-30, getHeight());
			cancelLabel.setBounds(199-16, 0, 16, 16);

			add(logoLabel);
			add(titleLabel);
			add(stateLabel);
			add(cancelLabel);

			cancelLabel.addMouseListener(new MouseAdapter() {
				@Override public void mousePressed(MouseEvent e) {
					cancelLabel.setIcon(null);
					removeTile(DownloadTile.this, false);
					task.stop();
				}
			});
		}

		public void setCurrentSize(int currentSize, int totalSize) {
			int percent = totalSize > 0 ? (int) (100 * ((float)currentSize / totalSize)) : -1;

			if (percent >= 0) {
				stateLabel.setText(currentSize + " / " + totalSize + " (" + percent + "%)");
			} else {
				stateLabel.setText(currentSize + " / ?? (?%)");
			}
		}

		public void setToError(String msg) {
			setBackground(Color.red);
			setToolTipText(msg);
		}

		public void setToComplete() {
			cancelLabel.setIcon(null);
		}
	}
}
