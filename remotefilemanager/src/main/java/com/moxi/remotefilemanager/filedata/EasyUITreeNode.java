package com.moxi.remotefilemanager.filedata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EasyUI的Tree组件实体
 * 
 * @author Administrator
 *
 */
public class EasyUITreeNode {
	private String id;
	private String text;
	private String iconCls;
	private String url;
	private String parentId;
	private String state;
	private Boolean checked;
	private Map<String, Object> attributes = new HashMap<String, Object>();
	private List<EasyUITreeNode> children = new ArrayList<EasyUITreeNode>();

	/**
	 * 节点ID
	 * @return 节点ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 节点ID
	 * @param id 节点ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 节点文本
	 * @return 节点文本
	 */
	public String getText() {
		return text;
	}

	/**
	 * 节点文本
	 * @param text 节点文本
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * 节点图标
	 * @return 节点图标
	 */
	public String getIconCls() {
		return iconCls;
	}

	/**
	 * 节点图标
	 * @param iconCls 节点图标
	 */
	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

	/**
	 * 节点链接地址
	 * @return 节点链接地址
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * 节点链接地址
	 * @param url 节点链接地址
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 节点状态 'open'或'closed'
	 * @return 节点状态 'open'或'closed'
	 */
	public String getState() {
		return state;
	}

	/**
	 * 节点状态 'open'或'closed'
	 * @param state 节点状态 'open'或'closed'
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * 该节点是否被选中
	 * @return 该节点是否被选中
	 */
	public Boolean getChecked() {
		return checked;
	}

	/**
	 * 该节点是否被选中
	 * @param checked 该节点是否被选中
	 */
	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	/**
	 * 节点的自定义属性
	 * @return 节点的自定义属性
	 */
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	/**
	 * 节点的自定义属性
	 * @param attributes 节点的自定义属性
	 */
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	/**
	 * 子节点集合
	 * @return 子节点集合
	 */
	public List<EasyUITreeNode> getChildren() {
		return children;
	}

	/**
	 * 子节点集合
	 * @param children 子节点集合
	 */
	public void setChildren(List<EasyUITreeNode> children) {
		this.children = children;
	}

	/**
	 * 父节点ID
	 * @return 父节点ID
	 */
	public String getParentId() {
		return parentId;
	}

	/**
	 * 父节点ID
	 * @param parentId 父节点ID
	 */
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
}