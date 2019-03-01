package com.dangdang.reader.personal.domain;

import java.util.List;


public class GroupItem {

	public GroupType type;
	public List<ShelfBook> list;
	public boolean isNew = false;
	
	public GroupItem(GroupType type, List<ShelfBook> list){
		this.type = type;
		this.list = list;
	}
}
