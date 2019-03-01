package com.dangdang.reader.dread.core.base;

import android.graphics.Point;
import android.graphics.Rect;

import com.dangdang.reader.dread.core.base.IEpubPageView.DrawingType;
import com.dangdang.reader.dread.core.base.IReaderController.DDirection;
import com.dangdang.reader.dread.core.base.IReaderController.DPageIndex;

public interface IReaderWidget {

	/**
	 * 设置开始移动x,y和方向
	 */
	public abstract void startManualScrolling(int x, int y, DDirection direction);

	/**
	 * 设置移动中x,y
	 */
	public abstract boolean scrollManuallyTo(int x, int y);

	/**
	 * 设置移动中x,y 和 移动速度
	 */
	public abstract boolean startAnimatedScrolling(int x, int y, int speed, boolean sourceAnim);

	/**
	 *
	 */
	public abstract boolean scrollRelease(int x, int y, boolean fast);

	/**
	 * @param start
	 * @param end
	 * @param rects
	 * @return
	 */
	public abstract int doDrawing(DPageIndex pageIndex, DrawingType type, DrawPoint start, DrawPoint end, DrawPoint current, Rect[] rects, int drawLineColor);


	public abstract int drawFinish(DrawingType type, DrawPoint current, boolean isPrev, boolean isNext);


	public void setAdapter(IPageAdapter adapter);

	/**
	 * 重绘
	 */
	public void repaint();

	/**
	 * 重绘当前页，选择重绘上一页\下一页
	 * @param prevPaint
	 * @param nextPaint
	 */
	public void repaintSync(boolean prevPaint, boolean nextPaint);

	public void repaintFooter();

	public void reset();

	public void clear();


	public static class DrawPoint {

		/**
		 * 是否画Cursor(跨页笔记时需要)
		 */
		private boolean isDraw = true;
		private Point point;

		public DrawPoint(){
		}

		public DrawPoint(boolean isDraw, Point point) {
			super();
			this.isDraw = isDraw;
			this.point = point;
		}

		public boolean isDraw() {
			return isDraw;
		}
		public void setDraw(boolean isDraw) {
			this.isDraw = isDraw;
		}
		public Point getPoint() {
			return point;
		}
		public void setPoint(Point point) {
			this.point = point;
		}

		public boolean isNull(){
			return point == null;
		}
		
	}
	
}
