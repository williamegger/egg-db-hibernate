package com.egg.code;

public class Bean {

	private Integer beanId;
	private String name;
	private String remark;
	private Boolean deleted;
	private Integer createMan;
	private Long createDate;
	private Integer lastMan;
	private Long lastDate;

	public Bean() {
	}

	public Integer getBeanId() {
		return beanId;
	}

	public void setBeanId(Integer beanId) {
		this.beanId = beanId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Integer getCreateMan() {
		return createMan;
	}

	public void setCreateMan(Integer createMan) {
		this.createMan = createMan;
	}

	public Long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Long createDate) {
		this.createDate = createDate;
	}

	public Integer getLastMan() {
		return lastMan;
	}

	public void setLastMan(Integer lastMan) {
		this.lastMan = lastMan;
	}

	public Long getLastDate() {
		return lastDate;
	}

	public void setLastDate(Long lastDate) {
		this.lastDate = lastDate;
	}

}
