package com.dangdang.reader.utils;

import android.text.TextUtils;

import com.dangdang.zframework.log.LogM;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yhyu on 2015/6/15.
 */
public class ImageConfig {
    public static final String IMAGE_SIZE_AA = "210X300";
    private static final String IMAGE_SIZE_AA_FLAG = "aa";

    public static final String IMAGE_SIZE_BB = "182X260";
    private static final String IMAGE_SIZE_BB_FLAG = "bb";

    public static final String IMAGE_SIZE_CC = "168X240";
    private static final String IMAGE_SIZE_CC_FLAG = "cc";

    public static final String IMAGE_SIZE_DD = "126X180";
    private static final String IMAGE_SIZE_DD_FLAG = "dd";

    public static final String IMAGE_SIZE_EE = "112X160";
    private static final String IMAGE_SIZE_EE_FLAG = "ee";

    public static final String IMAGE_SIZE_FF = "98X140";
    private static final String IMAGE_SIZE_FF_FLAG = "ff";

    public static final String IMAGE_SIZE_GG = "84X120";
    private static final String IMAGE_SIZE_GG_FLAG = "gg";

    public static final String IMAGE_SIZE_HH = "70X100";
    private static final String IMAGE_SIZE_HH_FLAG = "hh";

    public static final String IMAGE_SIZE_II = "280X400";
    private static final String IMAGE_SIZE_II_FLAG = "ii";

    public static final String IMAGE_SIZE_JJ = "252X360";
    private static final String IMAGE_SIZE_JJ_FLAG = "jj";

    public static final String IMAGE_SIZE_KK = "154X220";
    private static final String IMAGE_SIZE_KK_FLAG = "kk";

    public static final String IMAGE_SIZE_LL = "140X200";
    private static final String IMAGE_SIZE_LL_FLAG = "ll";
    private static List<String> SIZES = new ArrayList<String>();
    private static List<String> FLAGS = new ArrayList<String>();

    static {
        SIZES.add(IMAGE_SIZE_AA);
        FLAGS.add(IMAGE_SIZE_AA_FLAG);

        SIZES.add(IMAGE_SIZE_BB);
        FLAGS.add(IMAGE_SIZE_BB_FLAG);

        SIZES.add(IMAGE_SIZE_CC);
        FLAGS.add(IMAGE_SIZE_CC_FLAG);

        SIZES.add(IMAGE_SIZE_DD);
        FLAGS.add(IMAGE_SIZE_DD_FLAG);

        SIZES.add(IMAGE_SIZE_EE);
        FLAGS.add(IMAGE_SIZE_EE_FLAG);

        SIZES.add(IMAGE_SIZE_FF);
        FLAGS.add(IMAGE_SIZE_FF_FLAG);

        SIZES.add(IMAGE_SIZE_GG);
        FLAGS.add(IMAGE_SIZE_GG_FLAG);

        SIZES.add(IMAGE_SIZE_HH);
        FLAGS.add(IMAGE_SIZE_HH_FLAG);

        SIZES.add(IMAGE_SIZE_II);
        FLAGS.add(IMAGE_SIZE_II_FLAG);

        SIZES.add(IMAGE_SIZE_JJ);
        FLAGS.add(IMAGE_SIZE_JJ_FLAG);

        SIZES.add(IMAGE_SIZE_KK);
        FLAGS.add(IMAGE_SIZE_KK_FLAG);

        SIZES.add(IMAGE_SIZE_LL);
        FLAGS.add(IMAGE_SIZE_LL_FLAG);
    }

    public static String getBookCoverBySize(String url, String size) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(size))
            return url;
        int index = SIZES.indexOf(size);
        if (index < 0)
            return url;

        String flag = FLAGS.get(index);
        String regex = "_[\\w]{0,9}_cover";

        url = url.replaceFirst(regex, "_" + flag + "_cover");
        return url;
    }

    public static final String PAPER_IMAGE_SIZE_B = "200X200";
    public static final String PAPER_IMAGE_SIZE_W = "350X350";
    public static final String PAPER_IMAGE_SIZE_E = "500X500";
    private static final String PAPER_IMAGE_SIZE_B_FLAG = "b";
    private static final String PAPER_IMAGE_SIZE_W_FLAG = "w";
    private static final String PAPER_IMAGE_SIZE_E_FLAG = "e";
    private static List<String> PAPER_IMAGE_SIZES = new ArrayList<String>();
    private static List<String> PAPER_IAMGE_FLAGS = new ArrayList<String>();

    static {
        PAPER_IMAGE_SIZES.add(PAPER_IMAGE_SIZE_B);
        PAPER_IAMGE_FLAGS.add(PAPER_IMAGE_SIZE_B_FLAG);

        PAPER_IMAGE_SIZES.add(PAPER_IMAGE_SIZE_W);
        PAPER_IAMGE_FLAGS.add(PAPER_IMAGE_SIZE_W_FLAG);

        PAPER_IMAGE_SIZES.add(PAPER_IMAGE_SIZE_E);
        PAPER_IAMGE_FLAGS.add(PAPER_IMAGE_SIZE_E_FLAG);
    }


    /**
     * 根据尺寸获取对应的纸书封面尺寸
     * @param url               // 纸书封面url，如：http://img38.ddimg.cn/49/19/20881228-1_b_2.jpg
     * @param size              // 尺寸，如：200X200
     * @return
     */
    public static String getPaperBookCoverBySize(String url, String size){
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(size)){
            return url;
        }

        int index = PAPER_IMAGE_SIZES.indexOf(size);
        if (index < 0){
            return url;
        }

        String flag = PAPER_IAMGE_FLAGS.get(index);
        String regex = "_[\\w]";

        url = url.replaceFirst(regex, "_" + flag);

        return url;
    }

}
