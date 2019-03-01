package com.moxi.remotefilemanager.model;

import java.util.Date;

/**
 * 平台系统资源（菜单，按钮）
 * 
 * @author ZHANGWEI
 *
 */
public class Resource {
	private Long id;		//编号
	private String name;	//文件名称（文本）
	private String url;		//文件地址
	private String target;	//资源打开目标
	private String iconcls;	//资源图标类名
	private Integer orderno;//资源排序编号
	private Integer type;	//是否是文件或文件夹类型：0-文件夹，1-文件
	private Long pid;		//资源父级ID
	private Date createtime;//资源创建时间
	private Integer state;	//资源状态：0-可见,1-隐藏
	
	
	/**
	 * 无参数构造方法
	 */
	public Resource() {
	}
	/**
	 * 传入系统资源编号的构造方法
	 * @param id 资源ID
	 */
	public Resource(Long id) {
		this.id = id;
	}
	
	/**
	 * 编号
	 * @return 编号
	 */
	public Long getId() {
		return id;
	}
	/**
	 * 编号
	 * @param id 编号
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 资源名称（文本）
	 * @return 资源名称（文本）
	 */
	public String getName() {
		return name;
	}
	/**
	 * 资源名称（文本）
	 * @param name 资源名称（文本）
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 资源链接地址
	 * @return 资源链接地址
	 */
	public String getUrl() {
		return url;
	}
	/**
	 * 资源链接地址
	 * @param url 资源链接地址
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	/**
	 * 资源打开目标
	 * @return 资源打开目标
	 */
	public String getTarget() {
		return target;
	}
	/**
	 * 资源打开目标
	 * @param target 资源打开目标
	 */
	public void setTarget(String target) {
		this.target = target;
	}
	/**
	 * 资源图标类名
	 * @return 资源图标类名
	 */
	public String getIconcls() {
		return iconcls;
	}
	/**
	 * 资源图标类名
	 * @param iconcls 资源图标类名
	 */
	public void setIconcls(String iconcls) {
		this.iconcls = iconcls;
	}
	/**
	 * 资源排序编号
	 * @return 资源排序编号
	 */
	public Integer getOrderno() {
		return orderno;
	}
	/**
	 * 资源排序编号
	 * @param orderno 资源排序编号
	 */
	public void setOrderno(Integer orderno) {
		this.orderno = orderno;
	}
	/**
	 * 资源类型：0-菜单，1-按钮
	 * @return 资源类型：0-菜单，1-按钮
	 */
	public Integer getType() {
		return type;
	}
	/**
	 * 资源类型：0-菜单，1-按钮
	 * @param type 资源类型：0-菜单，1-按钮
	 */
	public void setType(Integer type) {
		this.type = type;
	}
	/**
	 * 资源父级ID
	 * @return 资源父级ID
	 */
	public Long getPid() {
		return pid;
	}
	/**
	 * 资源父级ID
	 * @param pid 资源父级ID
	 */
	public void setPid(Long pid) {
		this.pid = pid;
	}
	/**
	 * 资源创建时间
	 * @return 资源创建时间
	 */
	public Date getCreatetime() {
		return createtime;
	}
	/**
	 * 资源创建时间
	 * @param createtime 资源创建时间
	 */
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	/**
	 * 资源状态：0-可见,1-隐藏
	 * @return 资源状态：0-可见,1-隐藏
	 */
	public Integer getState() {
		return state;
	}
	/**
	 * 资源状态：0-可见,1-隐藏
	 * @param state 资源状态：0-可见,1-隐藏
	 */
	public void setState(Integer state) {
		this.state = state;
	}
	
	/**
	 * 获取对象的哈希码
	 */
	@Override
	public int hashCode() {
		return id.hashCode();
	}
	
	/**
	 * 判断两个系统资源对象是否相等的方法
	 * @param obj 资源对象
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj==null) {
			return false;
		}
		
		if (!(obj instanceof Resource)) {
			return false;
		}
		
		Resource res = (Resource) obj;
		if (res.getId()!=null && res.getId().equals(id)) {
			return true;
		}
		else {
			return false;
		}
	}
}