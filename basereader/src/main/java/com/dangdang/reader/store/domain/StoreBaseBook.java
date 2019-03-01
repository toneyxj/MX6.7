package com.dangdang.reader.store.domain;

import com.dangdang.reader.common.domain.BaseBook;

/**
 * 书城书籍基类数据结构
 * Created by xiaruri on 2015/5/25.
 */
public class StoreBaseBook extends BaseBook{

    private static final long serialVersionUID = 1L;

    private int wordCnt;                    // 字数
    private int bookReviewCount;            // 评论数
    private int isStore;                    // 是否收藏
    private float score;                    // 星级数
    private String subTitle;                // 二级标题
    private String recommandWords;          // 小编推荐语
    private String authorHeadPic;           // 作者头像地址
    private String authorId;                // 作者id
    private String authorIntroduction;      // 作者介绍
    private String isbn;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getWordCnt() {
        return wordCnt;
    }

    public void setWordCnt(int wordCnt) {
        this.wordCnt = wordCnt;
    }

    public int getBookReviewCount() {
        return bookReviewCount;
    }

    public void setBookReviewCount(int bookReviewCount) {
        this.bookReviewCount = bookReviewCount;
    }

    public int getIsStore() {
        return isStore;
    }

    public void setIsStore(int isStore) {
        this.isStore = isStore;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getRecommandWords() {
        return recommandWords;
    }

    public void setRecommandWords(String recommandWords) {
        this.recommandWords = recommandWords;
    }

    public String getAuthorHeadPic() {
        return authorHeadPic;
    }

    public void setAuthorHeadPic(String authorHeadPic) {
        this.authorHeadPic = authorHeadPic;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorIntroduction() {
        return authorIntroduction;
    }

    public void setAuthorIntroduction(String authorIntroduction) {
        this.authorIntroduction = authorIntroduction;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

}
