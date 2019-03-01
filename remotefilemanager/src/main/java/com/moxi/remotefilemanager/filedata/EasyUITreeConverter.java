package com.moxi.remotefilemanager.filedata;

/**
 * EasyUI的树节点转换器接口
 * 
 * @author Administrator
 *
 */
public interface EasyUITreeConverter {

	/**
	 * 将对象转换成树节点实体
	 * @param obj 源数据
	 * @return 转换后的树型数据
	 */
	public EasyUITreeNode convert(Object obj);
}