package com.webapp.framework.utils.excel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sheet
{
  private String sheetName = "";

  private String sheetRef = "";

  private List<Row> rowList = new ArrayList();

  private Map<Integer, Row> rowMap = new HashMap();

  private List<Merge> mergeList = new ArrayList();

  private Map<Integer, Integer> columnWidthMap = new HashMap();

  public String getSheetName()
  {
    return this.sheetName;
  }

  public void setSheetName(String sheetName)
  {
    this.sheetName = sheetName;
  }

  public List<Row> getRowList()
  {
    return this.rowList;
  }

  public Map<Integer, Row> getRowMap()
  {
    return this.rowMap;
  }

  public List<Merge> getMergeList()
  {
    return this.mergeList;
  }

  public Map<Integer, Integer> getColumnWidthMap()
  {
    return this.columnWidthMap;
  }

  public Row getRow(int rowIndex)
  {
    return (Row)this.rowMap.get(Integer.valueOf(rowIndex));
  }

  protected String getSheetRef()
  {
    return this.sheetRef;
  }

  protected void setSheetRef(String sheetRef)
  {
    this.sheetRef = sheetRef;
  }
}