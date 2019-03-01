package com.dangdang.reader.dread.config;

import java.util.HashSet;

public enum PageType {

    /**
     * 普通页
     */
    Common(),
    /**
     * 全屏图/封面图/版式设置的全屏图
     */
    FullScreen(),
    FrontCover(),
    /**
     * 超出页眉即没有页眉
     */
    NoHeader(),
    /**
     * 超出页脚即没有页脚
     */
    NoFooter(),
    /**
     * gallery效果
     */
    Gallery(),
    /**
     * 音频
     */
    Audio(),
    /**
     * 视频
     */
    Video(),
    CodeInteractive(),
    TableInteractive(),
    AcrossPage();

	/*public final int value;

	PageType(int v){
		this.value = v;
	};
	
	public final int getValue(){
		return value;
	}*/

    /**
     * typedef enum tagPageType
     * {
     * PAGETYPE_NORMAL = 0,
     * PAGETYPE_FULLSCREEN = 1,
     * PAGETYPE_FRONTCOVER = 2,
     * PAGETYPE_BLEED_UP = 4,
     * PAGETYPE_BLEED_DOWN = 8,
     * PAGETYPE_GALLERY = 16,
     * PAGETYPE_AUDIO_AUTOPLAY = 32,
     * PAGETYPE_VIDEO_AUTOPLAY = 64,
     * PAGETYPE_CODE_INTERACTIVE = 128,
     * PAGETYPE_TABLE_INTERACTIVE = 256,
     * }PageType;
     */
    public final static int Type_Common = 0;
    public final static int Type_FullScreen = 1;
    public final static int Type_FrontCover = 2;
    public final static int Type_NoHeader = 4;
    public final static int Type_NoFooter = 8;
    public final static int Type_Gallery = 16;
    public final static int Type_Audio = 32;
    public final static int Type_Video = 64;
    public final static int Type_CodeInteractive = 128;
    public final static int Type_TableInteractive = 256;
    public final static int Type_AcrossPage = 512;

    public static HashSet<PageType> convert(int pageType) {
        HashSet<PageType> type = new HashSet<PageType>();
        if ((pageType & Type_Common) == Type_Common) {
            type.add(Common);
        }
        if ((pageType & Type_FullScreen) == Type_FullScreen) {
            type.add(FullScreen);
        }
        if ((pageType & Type_FrontCover) == Type_FrontCover) {
            type.add(FrontCover);
        }
        if ((pageType & Type_NoHeader) == Type_NoHeader) {
            type.add(NoHeader);
        }
        if ((pageType & Type_NoFooter) == Type_NoFooter) {
            type.add(NoFooter);
        }
        if ((pageType & Type_Gallery) == Type_Gallery) {
            type.add(Gallery);
        }
        if ((pageType & Type_Audio) == Type_Audio) {
            type.add(Audio);
        }
        if ((pageType & Type_Video) == Type_Video) {
            type.add(Video);
        }
        if ((pageType & Type_CodeInteractive) == Type_CodeInteractive) {
            type.add(CodeInteractive);
        }
        if ((pageType & Type_TableInteractive) == Type_TableInteractive) {
            type.add(TableInteractive);
        }
        if ((pageType & Type_AcrossPage) == Type_AcrossPage) {
            type.add(AcrossPage);
        }
        return type;
    }

    public static boolean isNoHeader(HashSet<PageType> pageType) {
        if (pageType == null || pageType.size() == 0)
            return false;
        return pageType.contains(FullScreen) || pageType.contains(FrontCover) || pageType.contains(AcrossPage) || pageType.contains(NoHeader);
    }

    public static boolean isNoFooter(HashSet<PageType> pageType) {
        if (pageType == null || pageType.size() == 0)
            return false;
        return pageType.contains(FullScreen) || pageType.contains(FrontCover) || pageType.contains(AcrossPage) || pageType.contains(NoFooter);
    }

    public static boolean isGallary(HashSet<PageType> pageType) {
        if (pageType == null || pageType.size() == 0)
            return false;

        return pageType.contains(Gallery);
    }

}
