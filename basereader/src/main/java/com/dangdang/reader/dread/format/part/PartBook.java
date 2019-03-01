package com.dangdang.reader.dread.format.part;

import com.dangdang.reader.dread.format.Book;
import com.dangdang.reader.dread.format.Chapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 原创书的实体
 * @author Yhyu
 */
public class PartBook extends Book {

    private static final long serialVersionUID = 1L;

    private List<BaseNavPoint> volumeList; // 卷列表

    public PartBook() {
        super();
        printLog("PartBookPartBookPartBook");
    }
    /**
     * 根据章节id获取章节
     *
     * @param id
     * @return
     */
    public PartChapter getChapterById(int id) {
        PartChapter ret = null;
        for (Chapter chapter : getChapterList()) {
            PartChapter partChapter = (PartChapter) chapter;
            if (partChapter.getId() == id) {
                return partChapter;
            }
        }
        return ret;
    }

    @Override
    public BaseNavPoint getNavPoint(Chapter chapter) {
        final List<BaseNavPoint> nPList = getNavPointList();
        if (nPList == null || chapter == null) {
            return null;
        }
        PartChapter partChapter = (PartChapter) chapter;
        PartNavPoint partNavPointP = null;
        for (BaseNavPoint n : nPList) {
            if (n instanceof PartVolumeNavPoint)
                continue;
            PartNavPoint tempPoint = (PartNavPoint) n;
            if (partChapter.id == tempPoint.getChapterId() || partChapter.getPath().equals(tempPoint.fullSrc)) {
                partNavPointP = tempPoint;
                break;
            }
        }
        return partNavPointP;
    }

    @Override
    public List<BaseNavPoint> getAllNavPointList() {
        return getNavPointList();
    }

    public List<BaseNavPoint> getAllNavPointAndVolumeList() {
        List<BaseNavPoint> temp = getNavPointList();
        List<BaseNavPoint> list=null;
        if (temp != null) {
            list = new ArrayList<BaseNavPoint>();
            list.addAll(temp);
            Collections.copy(list, temp);
            if (volumeList != null && volumeList.size() > 1) {
                int position = 0;
                for (int i = 0; i < volumeList.size(); i++) {
                    PartVolumeNavPoint volume = (PartVolumeNavPoint) volumeList.get(i);
                    list.add(position, volume);
                    position = position + 1 + volume.getCount();
                }
            }
        }
        return list;
    }

    public void setVolumeList(List<BaseNavPoint> volumeList) {
        this.volumeList = volumeList;
    }

    @Override
    public BaseNavPoint getNavPoint(int pageIndexInBook) {
        return super.getNavPoint(pageIndexInBook);
    }

    public static class PartNavPoint extends BaseNavPoint {

        public PartNavPoint() {
            super();
        }

        private int isFree;

        public int getIsFree() {
            return isFree;
        }

        public void setIsFree(int isFree) {
            this.isFree = isFree;
        }

        protected int chapterId;// 章节id,对于原创书，可能作为书签笔记中的index

        public int getChapterId() {
            return chapterId;
        }

        public void setChapterId(int chapterId) {
            this.chapterId = chapterId;
        }
    }

    public static class PartVolumeNavPoint extends BaseNavPoint {
        private String volumeId;
        private int count;

        public String getVolumeId() {
            return volumeId;
        }

        public void setVolumeId(String volumeId) {
            this.volumeId = volumeId;
        }


        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}
