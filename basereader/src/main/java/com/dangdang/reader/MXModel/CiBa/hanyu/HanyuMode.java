package com.dangdang.reader.MXModel.CiBa.hanyu;

import com.mx.mxbase.utils.StringUtils;

/**
 * Created by xj on 2018/7/4.
 */

public class HanyuMode {
    public String err_no;
    public String err_msg;
    public Chinese chinese;


    public String getHanyuValue(String value) {
        if (chinese == null) {
            return value;
        } else {
            try {
                StringBuilder builder = new StringBuilder();
                int size = value.length();
                if (size==1||(chinese.cy==null&&chinese.ci==null)) {//字
                    Zi zi = chinese.zi.get(0);
                    builder.append(zi.hanzi + "\t[" + zi.pinyin + "]");
                    builder.append("\n");
                    builder.append("[部 首] " + zi.bushou);
                    builder.append("\t\t[笔 画] " + zi.bihua);
                    builder.append("\n\n");
                    if (!StringUtils.isNull(zi.jieshi)) {
                        builder.append(zi.jieshi);
                        builder.append("\n");
                    }
                    if (!StringUtils.isNull(zi.nixu)) {
                        builder.append("[ 逆  序 ] " + zi.nixu);
                        builder.append("\n");
                    }
                    if (!StringUtils.isNull(zi.english)) {
                        builder.append("[ 英  文 ] " + zi.english);
                        builder.append("\n");
                    }
                    if (!StringUtils.isNull(zi.ciyu)) {
                        builder.append("[ 词  组 ] " + zi.ciyu);
                        builder.append("\n");
                    }
                    if (!StringUtils.isNull(zi.zaozi)) {
                        builder.append("[ 造  字  ] " + zi.zaozi);
                        builder.append("\n");
                    }
                    if (!StringUtils.isNull(zi.fanti)) {
                        builder.append("[ 繁  体 ] " + zi.fanti);
                        builder.append("\n");
                    }

                } else if (size == 2||(chinese.cy==null&&chinese.ci!=null)) {//词
                    Ci ci=chinese.ci;
                    builder.append( ci.ciyu + "\t[" + ci.pinyin + "]");
                    builder.append("\n\n");
                    if (!StringUtils.isNull(ci.jieshi)) {
                        builder.append("[ 释  义 ] "+ci.jieshi);
                        builder.append("\n");
                    }
                    if (!StringUtils.isNull(ci.goucheng)) {
                        builder.append("[ 构  成 ] " + ci.goucheng);
                        builder.append("\n");
                    }
                    if (!StringUtils.isNull(ci.tongyi)) {
                        builder.append("[同意词] " + ci.tongyi);
                        builder.append("\n");
                    }
                    if (!StringUtils.isNull(ci.liju)) {
                        builder.append("[ 列  句 ] " + ci.liju);
                        builder.append("\n");
                    }

                }else {
                    CY cy=chinese.cy;
                    if (cy==null)return value;
                    builder.append( cy.chengyu + "\t[" + cy.pinyin + "]");
                    builder.append("\n\n");
                    if (!StringUtils.isNull(cy.shiyi)) {
                        builder.append("[ 释  义 ]"+cy.shiyi);
                        builder.append("\n");
                    }
                    if (!StringUtils.isNull(cy.yuchu)) {
                        builder.append("[ 语  出 ] " + cy.yuchu);
                        builder.append("\n");
                    }
                    if (!StringUtils.isNull(cy.zhengyin)) {
                        builder.append("[ 正  音 ] " + cy.zhengyin);
                        builder.append("\n");
                    }
                    if (!StringUtils.isNull(cy.bianxing)) {
                        builder.append("[ 辨  形 ] " + cy.bianxing);
                        builder.append("\n");
                    }
                    if (!StringUtils.isNull(cy.jiegou)) {
                        builder.append("[ 构  成 ] " + cy.jiegou);
                        builder.append("\n");
                    }
                    if (!StringUtils.isNull(cy.english)) {
                        builder.append("[ 英  语 ] " + cy.english);
                        builder.append("\n");
                    }
                    if (!StringUtils.isNull(cy.jinyi)) {
                        builder.append("[近义词] " + cy.jinyi);
                        builder.append("\n");
                    }
                    if (!StringUtils.isNull(cy.fanyi)) {
                        builder.append("[反义词] " + cy.fanyi);
                        builder.append("\n");
                    }
                    if (!StringUtils.isNull(cy.yongfa)) {
                        builder.append("[ 用  法 ] " + cy.yongfa);
                        builder.append("\n");
                    }
                    if (!StringUtils.isNull(cy.bianxing)) {
                        builder.append("[ 辨  析 ] " + cy.bianxing);
                        builder.append("\n");
                    }
                    if (!StringUtils.isNull(cy.liju)) {
                        builder.append("[ 例  句 ] " + cy.liju);
                        builder.append("\n");
                    }
                }
                return builder.toString();
            } catch (Exception e) {
                return value;
            }
        }
    }

    @Override
    public String toString() {

        return "HanyuMode{" +
                "err_no='" + err_no + '\'' +
                ", err_msg='" + err_msg + '\'' +
                ", chinese=" + chinese +
                '}';
    }
}
