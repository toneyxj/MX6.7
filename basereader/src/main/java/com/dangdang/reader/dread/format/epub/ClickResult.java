package com.dangdang.reader.dread.format.epub;

import android.graphics.Rect;

public class ClickResult {

    private ClickType mType = ClickType.None;

    public ClickResult() {
    }

    public ClickResult(ClickType type) {
        mType = type;
    }

    public ClickType getType() {
        return mType;
    }

    public void setType(ClickType mType) {
        this.mType = mType;
    }

    public boolean isPicNormal() {
        return mType.isPicNormal();
    }

    public boolean isInnerNote() {
        return mType.isInnerNote();
    }

    public boolean isClick() {
        return mType.isClick();
    }

    public boolean isPicDesc() {
        return mType.isPicDesc();
    }

    public boolean isOther() {
        return mType.isOther();
    }

    public boolean isToBrowser() {
        return mType.isToBrowser();
    }

    public boolean isAudio() {
        return mType.isAudio();
    }

    public boolean isVideo() {
        return mType.isVideo();
    }

    public boolean isPicFull() {
        return mType.isPicFull();
    }

    public boolean isPicSmall() {
        return mType.isPicSmall();
    }
    public boolean isConlink() {
        return mType.isConlink();
    }
    public boolean isPicAcrossPage() {
        return mType.isPicAcrossPage();
    }
    public boolean isPicGif() {
        return mType.isPicGif();
    }
    public static class ImageClickResult extends ClickResult {

        private String imgPath;
        private String imgDesc;
        private Rect imgRect;
		private int imgBgColor;

        public String getImgPath() {
            return imgPath;
        }

        public void setImgPath(String imgPath) {
            this.imgPath = imgPath;
        }

        public String getImgDesc() {
            return imgDesc;
        }

        public void setImgDesc(String imgDesc) {
            this.imgDesc = imgDesc;
        }

        public Rect getImgRect() {
            return imgRect;
        }

        public void setImgRect(Rect imgRect) {
            this.imgRect = imgRect;
        }
		public int getImgBgColor() {
			return imgBgColor;
		}

		public void setImgBgColor(int imgBgColor) {
			this.imgBgColor = imgBgColor;
		}

    }

    public static class InnerLabelClickResult extends ClickResult {

        private String labelContent;
        private Rect imgRect;

        public String getLabelContent() {
            return labelContent;
        }

        public void setLabelContent(String labelContent) {
            this.labelContent = labelContent;
        }

        public Rect getImgRect() {
            return imgRect;
        }

        public void setImgRect(Rect imgRect) {
            this.imgRect = imgRect;
        }
    }

    public static class ToBrowserClickResult extends ClickResult {

        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }

    public static class InnerGotoClickResult extends ClickResult {

        private InnerGotoType gotoType = InnerGotoType.AT_NONE;// 锚点类型
        private String href;// 锚点跳转地址,如果是空，表示当前页锚点
        private String anchor;// 锚点ID
        private int pageIndex; // 锚点对应的页码

        public InnerGotoType getGotoType() {
            return gotoType;
        }

        public void setGotoType(InnerGotoType gotoType) {
            this.gotoType = gotoType;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public String getAnchor() {
            return anchor;
        }

        public void setAnchor(String anchor) {
            this.anchor = anchor;
        }

        public int getPageIndex() {
            return pageIndex;
        }

        public void setPageIndex(int pageIndex) {
            this.pageIndex = pageIndex;
        }

        public boolean isBookInner() {
            return gotoType.isBookInner();
        }

        public boolean isToBrowser() {
            return gotoType.isToBrowser();
        }


    }

    public static class AudioClickResult extends ClickResult {

        private String path;
        private Rect imgRect;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public Rect getImgRect() {
            return imgRect;
        }

        public void setImgRect(Rect imgRect) {
            this.imgRect = imgRect;
        }

    }

    public enum ClickType {

        None, // 默认图
        Text,
        /**
         * 独占图
         */
        PicNormal, PicInLine,

        PicDesc,
        /**
         * 注释
         */
        InnerNote,
        /**
         * 全屏图
         */
        PicFull,
        /**
         * 封面图
         */
       PicFrontCover, PicGallary, PicSign,PIC_SMALL,CONLINK , Other, Audio, Video, PIC_ACROSS_PAGE,PIC_GIF,Url, InteractiveTable, InteractiveCode;

        public final static int Type_None = 0;
        public final static int Type_Text = 1; // 文本
        public final static int Type_PicNormal = 2; // 独占图
        public final static int Type_PicInLine = 3; // 随文图
        public final static int Type_PicDesc = 4; // 图说
        public final static int Type_InnerNote = 5; // 注释
        public final static int Type_PicFull = 6; // 全屏图
        public final static int Type_PicFrontCover = 7; // 封面
        public final static int Type_PicGallary = 8;//
        public final static int Type_PicSign = 9;
        public final static int Type_PIC_SMALL = 10;// 小图.
        public final static int Type_CONLINK = 11;// 地址跳转
        public final static int Type_Audio = 12;// 音频
        public final static int Type_Video = 13;// 视频
        public final static int Type_PIC_ACROSS_PAGE = 14;// 跨页图
        public final static int Type_PIC_GIF = 15;// GIF
		
		public final static int Type_InteractiveTable = 21;
		public final static int Type_InteractiveCode = 22;
		

        public static ClickType convert(int clickType) {

            ClickType cType = None;
            switch (clickType) {
                case Type_Text:
                    cType = Text;
                    break;
                case Type_PicNormal:
                    cType = PicNormal;
                    break;
                case Type_PicInLine:
                    cType = PicInLine;
                    break;
                case Type_PicDesc:
                    cType = PicDesc;
                    break;
                case Type_InnerNote:
                    cType = InnerNote;
                    break;
                case Type_PicFull:
                    cType = PicFull;
                    break;
                case Type_PicFrontCover:
                    cType = PicFrontCover;
                    break;
                case Type_PicGallary:
                    cType = PicGallary;
                    break;
                case Type_PicSign:
                    cType = PicSign;
                    break;
                case Type_PIC_SMALL:
                    cType = PIC_SMALL;
                    break;
                case Type_CONLINK:
                    cType = CONLINK;
                    break;
                case Type_Audio:
                    cType = Audio;
                    break;
                case Type_Video:
                    cType = Video;
                    break;
                case Type_PIC_ACROSS_PAGE:
                    cType = PIC_ACROSS_PAGE;
                    break;
                case Type_PIC_GIF:
                    cType = PIC_GIF;
                    break;
            /*
			 * case Type_Url: cType = Url; break;
			 */
            }

            return cType;
        }

        public boolean isClick() {
            return isPicNormal() || isPicDesc();
        }

        public boolean isPicNormal() {
            return this == PicNormal;
        }

        public boolean isPicDesc() {
            return this == PicDesc;
        }

        public boolean isInnerNote() {
            return this == InnerNote;
        }

        public boolean isPicFull() {
            return this == PicFull;
        }

        public boolean isOther() {
            return this == Other;
        }

        public boolean isToBrowser() {
            return this == Url;
        }

        public boolean isAudio() {
            return this == Audio;
        }

        public boolean isVideo() {
            return this == Video;
        }
        public boolean isPicSmall() {
            return this == PIC_SMALL;
        }
        public boolean isConlink() {
            return this == CONLINK;
        }
        public boolean isPicAcrossPage() {
            return this == PIC_ACROSS_PAGE;
        }
        public boolean isPicGif() {
            return this == PIC_GIF;
        }
    }

    public static enum InnerGotoType {

        /**
         * typedef enum tagAType { AT_NONE, // 无跳转 AT_HTTP, // 网页 AT_INNER, //
         * 本章内跳转 AT_OUTER, // 其他章跳转头 AT_OUTER_TAG, // 其他章跳转锚点 AT_MAILTO, // mail
         * } ATYPE;
         */

        AT_NONE, // 无跳转
        AT_HTTP, // 网页
        AT_INNER, // 本章内跳转
        AT_OUTER, // 其他章跳转头
        AT_OUTER_TAG, // 其他章跳转锚点
        AT_MAILTO;

        public final static int TYPE_AT_NONE = 0;
        public final static int TYPE_AT_HTTP = 1;
        public final static int TYPE_AT_INNER = 2;
        public final static int TYPE_AT_OUTER = 3;
        public final static int TYPE_AT_OUTER_TAG = 4;
        public final static int TYPE_AT_MAILTO = 5;

        public static InnerGotoType convert(int gotoType) {

            InnerGotoType igType = AT_NONE;
            switch (gotoType) {
                case TYPE_AT_HTTP:
                    igType = AT_HTTP;
                    break;
                case TYPE_AT_INNER:
                    igType = AT_INNER;
                    break;
                case TYPE_AT_OUTER:
                    igType = AT_OUTER;
                    break;
                case TYPE_AT_OUTER_TAG:
                    igType = AT_OUTER_TAG;
                    break;
                case TYPE_AT_MAILTO:
                    igType = AT_MAILTO;
                    break;
            }

            return igType;
        }

        /**
         * 是否书内跳转
         *
         * @return
         */
        public boolean isBookInner() {
            return isInner() || isOuter() || isOuterTag();
        }

        public boolean isToBrowser() {
            return isHttp();
        }

        public boolean isNone() {
            return this == AT_NONE;
        }

        public boolean isHttp() {
            return this == AT_HTTP;
        }

        public boolean isInner() {
            return this == AT_INNER;
        }

        public boolean isOuter() {
            return this == AT_OUTER;
        }

        public boolean isOuterTag() {
            return this == AT_OUTER_TAG;
        }

        public boolean isMailTo() {
            return this == AT_MAILTO;
        }
    }

}
