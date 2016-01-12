package com.baoyz.swipemenuexpandablelistview.test;

import java.util.List;

public class TestBean {

	private String name;
	private List<TestBean> childList;
	
	public TestBean(String name, List<TestBean> childList) {
		super();
		this.name = name;
		this.childList = childList;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<TestBean> getChildList() {
		return childList;
	}
	public void setChileList(List<TestBean> childList) {
		this.childList = childList;
	}
}
