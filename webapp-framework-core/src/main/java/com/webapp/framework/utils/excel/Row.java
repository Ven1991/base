package com.webapp.framework.utils.excel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Row {
	private int rowIndex = 0;

	private int rowHeight = 0;

	private List<Cell> cellList = new ArrayList();

	private Map<Integer, Cell> cellMap = new HashMap();

	public int getRowIndex() {
		return this.rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public int getRowHeight() {
		return this.rowHeight;
	}

	public void setRowHeight(int rowHeight) {
		this.rowHeight = rowHeight;
	}

	public List<Cell> getCellList() {
		return this.cellList;
	}

	protected Map<Integer, Cell> getCellMap() {
		return this.cellMap;
	}

	public Cell getCell(int cellIndex) {
		return (Cell) this.cellMap.get(Integer.valueOf(cellIndex));
	}
}