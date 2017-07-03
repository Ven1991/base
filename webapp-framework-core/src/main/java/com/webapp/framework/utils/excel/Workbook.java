package com.webapp.framework.utils.excel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Workbook
{
  private String filePath = "";

  private String fileName = "";

  private XSSFWorkbook poiWorkbook = new XSSFWorkbook();

  private Map<String, XSSFCellStyle> poiStyleMap = new HashMap();

  private List<Sheet> sheetList = new ArrayList();

  public String getFilePath()
  {
    return this.filePath;
  }

  public void setFilePath(String filePath)
  {
    this.filePath = filePath;
  }

  public String getFileName()
  {
    return this.fileName;
  }

  public void setFileName(String fileName)
  {
    this.fileName = fileName;
  }

  public XSSFWorkbook getPoiWorkbook()
  {
    return this.poiWorkbook;
  }

  public Map<String, XSSFCellStyle> getPoiStyleMap()
  {
    return this.poiStyleMap;
  }

  public List<Sheet> getSheetList()
  {
    return this.sheetList;
  }
}