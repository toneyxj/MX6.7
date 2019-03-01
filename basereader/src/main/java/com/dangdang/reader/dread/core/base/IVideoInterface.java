package com.dangdang.reader.dread.core.base;

/**
 * @author luxu
 */
public interface IVideoInterface {

    boolean changeVideoOrientation();

    void resetVedioView();

    void resetVedioViewWithOutOrientation();

    boolean isVideoShow();

    boolean isVideoLandscape();


}
