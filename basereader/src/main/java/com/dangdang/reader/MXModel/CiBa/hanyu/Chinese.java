package com.dangdang.reader.MXModel.CiBa.hanyu;

import java.util.List;

/**
 * Created by xj on 2018/7/4.
 */

public class Chinese {
    //字
    public List<Zi> zi;
    //词
    public Ci ci;
    //成语
    public CY cy;

    @Override
    public String toString() {
        return "Chinese{" +
                "zi=" + zi +
                ", ci=" + ci +
                ", cy=" + cy +
                '}';
    }
}
