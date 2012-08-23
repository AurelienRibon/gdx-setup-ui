package aurelienribon.utils.slidingpanels;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.utils.slidingpanels.SlidingLayersConfig.Tile;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class SlidingLayersPanel extends JLayeredPane {
	private SlidingLayersConfig[] timeline;
	private int timelineIdx;
	private Callback timelineCallback;
	private float timelineDuration = 0.7f;
	private Timer resizeTimer;

	public static interface Callback {
		public void done();
	}

	public SlidingLayersPanel() {
		addComponentListener(new ComponentAdapter() {
			private boolean firstResize = true;

			@Override
			public void componentResized(ComponentEvent e) {
				if (timeline != null && firstResize) {
					SlidingLayersConfig cfg = timeline[timelineIdx];
					cfg.placeAndRoute();
					place();
				} else if (timeline != null) {
					if (resizeTimer != null) resizeTimer.stop();
					resizeTimer = new Timer(300, new ActionListener() {
						@Override public void actionPerformed(ActionEvent e) {
							SlidingLayersConfig cfg = timeline[timelineIdx];
							cfg.placeAndRoute();
							tween(false);
						}
					});
					resizeTimer.setRepeats(false);
					resizeTimer.start();
				}

				firstResize = false;
			}
		});
	}

	public void force(SlidingLayersConfig cfg) {
		timeline = new SlidingLayersConfig[] {cfg};
		timelineIdx = 0;
		go(false);
	}

	public void play(SlidingLayersConfig... cfgs) {
		timeline = cfgs;
		timelineIdx = 0;
		go(true);
	}

	public void playCallback(Callback callback) {
		timelineCallback = callback;
	}

	public void playDuration(float duration) {
		timelineDuration = duration;
	}

	// -------------------------------------------------------------------------
	// Helpers
	// -------------------------------------------------------------------------

	private void go(boolean animate) {
		SlidingLayersConfig cfg = timeline[timelineIdx];

		removeAll();
		for (Component c : cfg.getComponents()) add(c, new Integer(1));

		cfg.placeAndRoute();
		if (animate) tween(true); else place();
	}

	private void tween(boolean useDelays) {
		tweenManager.killAll();

		Timeline tl = Timeline.createParallel();
		SlidingLayersConfig cfg = timeline[timelineIdx];

		for (Component c : cfg.getComponents()) {
			Tile t = cfg.getTile(c);
			tl.push(Tween.to(c, JComponentAccessor.XYWH, timelineDuration)
				.target(t.x, t.y, t.w, t.h)
				.delay(useDelays ? t.delay : 0)
			);
		}

		tl.setCallback(new TweenCallback() {
			@Override public void onEvent(int type, BaseTween<?> source) {
				if (timelineIdx < timeline.length-1) {
					timelineIdx++;
					go(true);
				} else if (timelineCallback != null) {
					timelineCallback.done();
					timelineCallback = null;
				}
			}
		});

		tl.start(tweenManager);
	}

	private void place() {
		SlidingLayersConfig cfg = timeline[timelineIdx];

		for (Component c : cfg.getComponents()) {
			Tile t = cfg.getTile(c);
			c.setBounds(t.x, t.y, t.w, t.h);
		}
	}

	// -------------------------------------------------------------------------
	// Animator
	// -------------------------------------------------------------------------

	private static final TweenManager tweenManager = new TweenManager();
	private static boolean running = false;

	static {
		Tween.registerAccessor(JComponent.class, new JComponentAccessor());
		Tween.setCombinedAttributesLimit(4);
	}

	public static void start() {
		running = true;

		Runnable runnable = new Runnable() {@Override public void run() {
			long lastMillis = System.currentTimeMillis();

			while (running) {
				try {Thread.sleep(10);} catch (InterruptedException ex) {}

				long newMillis = System.currentTimeMillis();
				final float delta = (newMillis - lastMillis) / 1000f;

				SwingUtilities.invokeLater(new Runnable() {	@Override public void run() {
					tweenManager.update(delta);
				}});

				lastMillis = newMillis;
			}
		}};

		new Thread(runnable).start();
	}

	public static void stop() {
		running = false;
	}

	private static class JComponentAccessor implements TweenAccessor<JComponent> {
		public static final int XYWH = 1;

		@Override
		public int getValues(JComponent target, int tweenType, float[] returnValues) {
			switch (tweenType) {
				case XYWH:
					returnValues[0] = target.getX();
					returnValues[1] = target.getY();
					returnValues[2] = target.getWidth();
					returnValues[3] = target.getHeight();
					return 4;

				default: assert false; return 0;
			}
		}

		@Override
		public void setValues(JComponent target, int tweenType, float[] newValues) {
			switch (tweenType) {
				case XYWH:
					target.setBounds(Math.round(newValues[0]), Math.round(newValues[1]), Math.round(newValues[2]), Math.round(newValues[3]));
					target.revalidate();
					break;

				default: assert false;
			}
		}
	}
}
