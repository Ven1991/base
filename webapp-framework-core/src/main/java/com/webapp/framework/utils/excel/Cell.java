package com.webapp.framework.utils.excel;

public class Cell
{
  private int cellIndex = 0;

  private String cellValue = "";

  private String styleKey = "";

  private String cellType = "";
  private Row row;

  public int getCellIndex()
  {
    return this.cellIndex;
  }

  public void setCellIndex(int cellIndex)
  {
    this.cellIndex = cellIndex;
  }

  public String getCellValue()
  {
    return this.cellValue;
  }

  public void setCellValue(String cellValue)
  {
    this.cellValue = (cellValue == null ? "" : cellValue);
  }

  public String getStyleKey()
  {
    return this.styleKey;
  }

  public void setStyleKey(String styleKey)
  {
    this.styleKey = styleKey;
  }

  public String getCellType()
  {
    return this.cellType;
  }

  public void setCellType(String cellType)
  {
    this.cellType = cellType;
  }

  public Row getRow()
  {
    return this.row;
  }

  public void setRow(Row row)
  {
    this.row = row;
  }
}