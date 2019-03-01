package com.moxi.biji.intf;

import java.util.List;

/**
 * 文字内容生成器
 * Created by Administrator on 2019/3/1.
 */
public interface ContentBuilderInterface<Content> {
    String getContent(List<Content> list,Object bj);
}
