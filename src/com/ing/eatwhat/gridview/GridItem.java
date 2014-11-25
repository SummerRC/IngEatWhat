package com.ing.eatwhat.gridview;

public class GridItem {
	private String name;
	private String path;
	private int section;

	public GridItem(String name, String path) {
		super();
		this.name = name;
		this.path = path;
	}
	
	public String getname() {
		return name;
	}
	public void setname(String name) {
		this.name = name;
	}
	public String getpath() {
		return path;
	}
	public void setpath(String path) {
		this.path = path;
	}
	
	public int getSection() {
		return section;
	}

	public void setSection(int section) {
		this.section = section;
	}

}
