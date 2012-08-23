package aurelienribon.utils.slidingpanels;

import java.awt.Component;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Aurelien Ribon | http://www.aurelienribon.com/
 */
public class SlidingLayersConfig {
	public static enum Direction {UP, DOWN, LEFT, RIGHT}

	private final SlidingLayersPanel panel;
	private final Map<Component, Tile> tiles = new HashMap<Component, Tile>();
	private final Map<Direction, List<Tile>> hiddenTiles = new EnumMap<Direction, List<Tile>>(Direction.class);
	private Grid rootGrid = new Grid(null), currentGrid = rootGrid;
	private int hgap = 10, vgap = 10;

	public SlidingLayersConfig(SlidingLayersPanel panel) {
		this.panel = panel;
		for (Direction dir : Direction.values())
			hiddenTiles.put(dir, new ArrayList<Tile>());
	}

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	public SlidingLayersConfig column(boolean fixed, int width) {
		Column column = new Column();
		column.fixedWidth = fixed;
		column.relWidth = width;
		column.w = width;
		currentGrid.cols.add(column);
		return this;
	}

	public SlidingLayersConfig row(boolean fixed, int height) {
		Row row = new Row();
		row.fixedHeight = fixed;
		row.relHeight = height;
		row.h = height;
		currentGrid.rows.add(row);
		return this;
	}

	public SlidingLayersConfig beginGrid(int row, int col) {
		Grid grid = new Grid(currentGrid);
		grid.row = row;
		grid.col = col;
		currentGrid.tiles.add(grid);
		currentGrid = grid;
		return this;
	}

	public SlidingLayersConfig end() {
		currentGrid = currentGrid.parent;
		return this;
	}

	public SlidingLayersConfig tile(int row, int col, Component cmp) {
		Tile tile = new Tile();
		tile.row = row;
		tile.col = col;
		currentGrid.tiles.add(tile);
		tiles.put(cmp, tile);
		return this;
	}

	public SlidingLayersConfig hide(Direction dir, Component... cmps) {
		for (Component cmp : cmps)
			if (!hiddenTiles.get(dir).contains(getTile(cmp)))
				hiddenTiles.get(dir).add(getTile(cmp));
		return this;
	}

	public SlidingLayersConfig delay(float delay, Component... cmps) {
		for (Component cmp : cmps) getTile(cmp).delay = delay;
		return this;
	}

	public SlidingLayersConfig delayIncr(float delay, Component... cmps) {
		float delayIncr = 0;
		for (Component cmp : cmps) getTile(cmp).delay = (delayIncr += delay);
		return this;
	}

	public SlidingLayersConfig hgap(int gap) {
		hgap = gap;
		return this;
	}

	public SlidingLayersConfig vgap(int gap) {
		vgap = gap;
		return this;
	}

	@Override
	public SlidingLayersConfig clone() {
		SlidingLayersConfig cfg = new SlidingLayersConfig(panel);
		Map<Tile, Tile> tilesMap = new HashMap<Tile, Tile>();

		for (Component cmp : tiles.keySet()) {
			Tile t = tiles.get(cmp);
			Tile tt = new Tile();
			tt.row = t.row;
			tt.col = t.col;
			tt.delay = t.delay;
			tt.x = t.x;
			tt.y = t.y;
			tt.w = t.w;
			tt.h = t.h;
			cfg.tiles.put(cmp, tt);
			tilesMap.put(t, tt);
		}

		for (Direction dir : hiddenTiles.keySet()) {
			for (Tile t : hiddenTiles.get(dir)) {
				cfg.hiddenTiles.get(dir).add(tilesMap.get(t));
			}
		}

		cfg.rootGrid = clone(rootGrid, null, tilesMap);
		cfg.hgap = hgap;
		cfg.vgap = vgap;
		return cfg;
	}

	private Grid clone(Grid g, Grid parent, Map<Tile, Tile> tilesMap) {
		Grid gg = new Grid(parent);
		gg.row = g.row;
		gg.col = g.col;
		gg.x = g.x;
		gg.y = g.y;
		gg.w = g.w;
		gg.h = g.h;

		for (Row r : g.rows) {
			Row rr = new Row();
			rr.fixedHeight = r.fixedHeight;
			rr.relHeight = r.relHeight;
			rr.h = r.h;
			gg.rows.add(rr);
		}

		for (Column c : g.cols) {
			Column cc = new Column();
			cc.fixedWidth = c.fixedWidth;
			cc.relWidth = c.relWidth;
			cc.w = c.w;
			gg.cols.add(cc);
		}

		for (Tile t : g.tiles) {
			if (t instanceof Grid) {
				gg.tiles.add(clone((Grid) t, gg, tilesMap));
			} else  {
				gg.tiles.add(tilesMap.get(t));
			}
		}

		return gg;
	}

	// -------------------------------------------------------------------------
	// Package API & helpers
	// -------------------------------------------------------------------------

	private static class Grid extends Tile {
		public final Grid parent;
		public final List<Row> rows = new ArrayList<Row>();
		public final List<Column> cols = new ArrayList<Column>();
		public final List<Tile> tiles = new ArrayList<Tile>();
		public Grid(Grid parent) {this.parent = parent;}
	}

	private static class Row {
		public boolean fixedHeight;
		public float relHeight;
		public int h;
	}

	private static class Column {
		public boolean fixedWidth;
		public float relWidth;
		public int w;
	}

	static class Tile {
		public int row, col;
		public float delay;
		public int x, y, w, h;
	}

	Set<Component> getComponents() {
		return tiles.keySet();
	}

	Tile getTile(Component cmp) {
		return tiles.get(cmp);
	}

	void placeAndRoute() {
		rootGrid.x = hgap;
		rootGrid.y = vgap;
		rootGrid.w = panel.getWidth()-hgap*2;
		rootGrid.h = panel.getHeight()-vgap*2;
		placeAndRoute(rootGrid);
	}

	private void placeAndRoute(Grid grid) {
		// Place rows

		float totalRelHeight = 0;
		int totalHeight = grid.h - vgap * (grid.rows.size() - 1);

		for (Row r : grid.rows) {
			if (r.fixedHeight) totalHeight -= r.h;
			else totalRelHeight += r.relHeight;
		}

		for (Row r : grid.rows) {
			if (!r.fixedHeight) r.h = (int) (totalHeight * r.relHeight / totalRelHeight);
		}

		// Place columns

		float totalRelWidth = 0;
		int totalWidth = grid.w - hgap * (grid.cols.size() - 1);

		for (Column c : grid.cols) {
			if (c.fixedWidth) totalWidth -= c.w;
			else totalRelWidth += c.relWidth;
		}

		for (Column c : grid.cols) {
			if (!c.fixedWidth) c.w = (int) (totalWidth * c.relWidth / totalRelWidth);
		}

		// Place tiles

		int x = grid.x, y = grid.y;

		for (int iRow=0; iRow<grid.rows.size(); iRow++) {
			for (int iCol=0; iCol<grid.cols.size(); iCol++) {
				for (Tile t : grid.tiles) {
					if (t.row != iRow || t.col != iCol) continue;
					t.x = x;
					t.y = y;
					t.w = grid.cols.get(t.col).w;
					t.h = grid.rows.get(t.row).h;
					if (t instanceof Grid) placeAndRoute((Grid) t);
				}
				x += grid.cols.get(iCol).w + hgap;
			}
			x = grid.x;
			y += grid.rows.get(iRow).h + vgap;
		}

		// Offset hidden tiles

		for (Direction dir : Direction.values()) {
			List<Tile> ts = hiddenTiles.get(dir);
			if (ts.isEmpty()) continue;

			switch (dir) {
				case UP:
					int maxY = ts.get(0).y + ts.get(0).h;
					for (Tile t : ts) maxY = Math.max(maxY, t.y + t.h);
					for (Tile t : ts) t.y -= maxY;
					break;

				case DOWN:
					int minY = ts.get(0).y;
					for (Tile t : ts) minY = Math.min(minY, t.y);
					for (Tile t : ts) t.y += panel.getHeight() - minY;
					break;

				case LEFT:
					int maxX = ts.get(0).x + ts.get(0).w;
					for (Tile t : ts) maxX = Math.max(maxX, t.x + t.w);
					for (Tile t : ts) t.x -= maxX;
					break;

				case RIGHT:
					int minX = ts.get(0).x;
					for (Tile t : ts) minX = Math.min(minX, t.x);
					for (Tile t : ts) t.x += panel.getWidth() - minX;
					break;
			}
		}
	}
}
