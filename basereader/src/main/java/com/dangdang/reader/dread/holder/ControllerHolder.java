package com.dangdang.reader.dread.holder;

import android.content.Context;
import android.graphics.Rect;

import com.dangdang.reader.dread.config.ReadConfig;
import com.dangdang.reader.dread.jni.BaseJniWarp.ElementIndex;
import com.dangdang.zframework.log.LogM;
import com.dangdang.zframework.utils.DRUiUtility;

public class ControllerHolder {

	private final static int CrossPageTime = 1000;//ms
	private final static int MinHandRange = 50;
	private final static int MaxCrossPage = 1;
	
	private Rect leftTopRect;
	private Rect rightBottomRect;
	private Rect topRect;
	private Rect bottomRect;

	private Rect leftTopCrossRect;
	private Rect rightBottomCrossRect;
	
	private int startPageIndexInChapter = - 1;
	private int endPageIndexInChapter = -1;
	private ElementIndex[] pressEmtIndexes;

	private long mStartCrossTime = 0; 
	private boolean initStartCrossTime = false;
	
	public ControllerHolder(Context context) {
		init(context);
	}
	
	private void init(Context context) {

		final ReadConfig readConfig = ReadConfig.getConfig();
		final int screenHeight = readConfig.getReadHeight();
		final int screenWidth = readConfig.getReadWidth();
		final float density = DRUiUtility.getDensity();

		final int realRange = (int) (MinHandRange * density);
		final int marginLeft = (int) ReadConfig.getConfig().getPaddingLeft();
		final int marginTop = (int) ReadConfig.getConfig().getPaddingTop(context);
		final int marginBottom = (int) ReadConfig.getConfig().getPaddingButtom();
		leftTopRect = new Rect();
		leftTopRect.left = 0;
		leftTopRect.top = 0;
		leftTopRect.right = marginLeft;
		leftTopRect.bottom = marginTop;

		rightBottomRect = new Rect();
		rightBottomRect.left = screenWidth - marginLeft;
		rightBottomRect.top = screenHeight - marginBottom;
		rightBottomRect.right = screenWidth;
		rightBottomRect.bottom = screenHeight;

		leftTopCrossRect = new Rect();
		leftTopCrossRect.right = realRange;
		leftTopCrossRect.bottom = realRange;

		rightBottomCrossRect = new Rect();
		rightBottomCrossRect.left = screenWidth - realRange;
		rightBottomCrossRect.top = screenHeight - realRange;
		rightBottomCrossRect.right = screenWidth;
		rightBottomCrossRect.bottom = screenHeight;

		topRect = new Rect();
		topRect.left = 0;
		topRect.top = 0;
		topRect.right = screenWidth;
		topRect.bottom = realRange;

		bottomRect = new Rect();
		bottomRect.left = 0;
		bottomRect.top = screenHeight - topRect.bottom;
		bottomRect.right = screenWidth;
		bottomRect.bottom = screenHeight;
		
	}
	
	public void reInit(Context context){
		init(context);
	}

	public boolean isHorizontalCross(int x, int y) {

		boolean isCross = false;
		isCross = rightBottomCrossRect.contains(x, y) || leftTopCrossRect.contains(x, y);

		return isCross;
	}

	public boolean isVerticalCross(int x, int y) {

		return topRect.contains(x, y) || bottomRect.contains(x, y);
	}

	/**
	 * @param anmType
	 * @param x
	 * @param y
	 *            y坐标相对于页
	 * @return
	 */
	/*public boolean isPrepareCrossPage(AnimType anmType, int x, int y) {

		boolean isCross = false;
		if (anmType == AnimType.Vertical) {
			isCross = isVerticalCross(x, y);
		} else {
			isCross = isHorizontalCross(x, y);
		}

		return isCross;
	}*/
	
	/**
	 * @param anmType
	 * @param x
	 * @param y
	 *            相对于页
	 * @return
	 */
	/*public boolean isTopArea(AnimType anmType, int x, int y) {

		boolean isPrev = false;
		if (anmType == AnimType.Vertical) {
			isPrev = topRect.contains(x, y);
		} else {
			isPrev = leftTopRect.contains(x, y);
		}
		return isPrev;
	}*/

	/**
	 * @param anmType
	 * @param x
	 * @param y
	 *            相对于页
	 * @return
	 */
	/*public boolean isBottomArea(AnimType anmType, int x, int y) {
		boolean isNext = false;
		if (anmType == AnimType.Vertical) {
			isNext = bottomRect.contains(x, y);
		} else {
			isNext = rightBottomRect.contains(x, y);
		}
		return isNext;
	}*/

	/**
	 * int[0] = 左上 start int[1] = 右下 end int[z][0]: x坐标 int[z][1]: y坐标
	 * 
	 * @param isLeftTop
	 * @return
	 */
	public int[][] getStartAndEndCoords() {

		final int[][] coords = new int[2][2];
		final int[] leftTop = { leftTopRect.right, leftTopRect.bottom };
		final int[] rightBottom = { rightBottomRect.left, rightBottomRect.top };
		/*
		 * final int lsize = eList.size(); if(eList != null && lsize > 0){
		 * //final BaseElement firstE = eList.get(0); final BaseElement lastE =
		 * eList.get(lsize - 1);
		 * 
		 * //leftTop[0] = (int) firstE.getStartX(); //leftTop[1] = (int)
		 * firstE.getStartY(); rightBottom[0] = (int) lastE.getStartX();
		 * rightBottom[1] = (int) lastE.getStartY(); }
		 */
		coords[0] = leftTop;
		coords[1] = rightBottom;

		return coords;
	}

	public int getTopRectCenterY(){
		return topRect.centerY();
	}
	
	public int getBottomRectCenterY(){
		return bottomRect.centerY();
	}

	/*public int getStartPageIndexInChapter() {
		return startPageIndexInChapter;
	}*/

	public void setStartPageIndexInChapter(int startPageIndexInChapter) {
		this.startPageIndexInChapter = startPageIndexInChapter;
	}

	public int getEndPageIndexInChapter() {
		return endPageIndexInChapter;
	}

	public void setEndPageIndexInChapter(int endPageIndexInChapter) {
		this.endPageIndexInChapter = endPageIndexInChapter;
	}
	
	public void setPressEmtIndexes(ElementIndex[] pressEmtIndexes) {
		this.pressEmtIndexes = pressEmtIndexes;
	}
	
	public ElementIndex[] getPressEmtIndexes(){
		return pressEmtIndexes;
	}

	public boolean isMaxCross(){
		if(startPageIndexInChapter != -1 && endPageIndexInChapter != -1){
			return Math.abs(endPageIndexInChapter - startPageIndexInChapter) >= MaxCrossPage;
		}
		return false;
	}
	
	public boolean isForward(){
		return endPageIndexInChapter > startPageIndexInChapter;
	}
	
	public void resetNoteRecord(){
		startPageIndexInChapter = -1;
		endPageIndexInChapter = -1;
	}
	
	public void initStartCrossTime(long time){
		//printLog(" crossTime initStartCrossTime " + initStartCrossTime + ",cross=" + mStartCrossTime + ",now=" + time);
		if(!initStartCrossTime){
			initStartCrossTime = true;
			mStartCrossTime = time;
		}
	}
	
	public void resetStartCrossTime(){
		initStartCrossTime = false;
		//printLog(" crossTime resetStartCrossTime ");
	}
	
	public boolean canCrossPage(long nowTime){
		//printLog(" crossTime canCrossPage " + nowTime + "," + mStartCrossTime);
		return (nowTime - mStartCrossTime) >= CrossPageTime;
	}
	
	private void printLog(String log){
		LogM.i(getClass().getSimpleName(), log);
	}
	
}
