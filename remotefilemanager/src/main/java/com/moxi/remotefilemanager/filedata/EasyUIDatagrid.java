package com.moxi.remotefilemanager.filedata;

import java.util.List;

/**
 * EasyUI的Datagrid组件的分页数据实体
 * 
 * @author Administrator
 *
 */
public class EasyUIDatagrid {
	private long total;
	private List<? extends Object> rows;

	/**
	 * 总记录数
	 * @return 总记录数
	 */
	public long getTotal() {
		return total;
	}

	/**
	 * 总记录数
	 * @param total 总记录数
	 */
	public void setTotal(long total) {
		this.total = total;
	}
	/**
	 * 当前页的数据
	 * @return 当前页的数据
	 */
	public List<? extends Object> getRows() {
		return rows;
	}
	/**
	 * 当前页的数据
	 * @param rows 当前页的数据
	 */
	public void setRows(List<? extends Object> rows) {
		this.rows = rows;
	}
}
