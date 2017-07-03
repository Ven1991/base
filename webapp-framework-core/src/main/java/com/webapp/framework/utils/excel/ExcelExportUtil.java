package com.webapp.framework.utils.excel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.webapp.framework.utils.file.utils.FileUtil;

public class ExcelExportUtil
{
  private static final Logger LOGGER = Logger.getLogger(ExcelExportUtil.class);
  public static final String XLSX = ".xlsx";
  public static final String ZIP = ".zip";
  public static final String DATA_SCOPE_TITLE_DATA = "A";
  public static final String DATA_SCOPE_DATA = "B";
  public static final String CELL_TYPE_STRING = "S";
  public static final String CELL_TYPE_NUMBER = "N";
  private static final String ENCODING_UTF_8 = "UTF-8";
  private Workbook workbook = null;
  private static ComparatorRow comparatorRow = null;
  private static ComparatorCell comparatorCell = null;
  private Map<String, Integer> shardStringsIndexMap = null;
  private List<String> shardStringsList = null;
  private Map<String, List<File>> xmlFileMap = null;

  private ExcelExportUtil()
  {
    comparatorRow = new ComparatorRow();
    comparatorCell = new ComparatorCell();

    this.shardStringsIndexMap = new HashMap();
    this.shardStringsList = new ArrayList();
  }

  public static ExcelExportUtil getInstance()
  {
    ExcelExportUtil excelExportUtil = new ExcelExportUtil();
    return excelExportUtil;
  }

  public void createWorkbook(String filePath, String fileName, String creator)
  {
    this.workbook = new Workbook();
    this.workbook.setFilePath(filePath);
    this.workbook.setFileName(fileName);
    if (AssertUtil.isVal(creator))
      this.workbook.getPoiWorkbook().getProperties().getCoreProperties().setCreator(creator);
  }

  public XSSFCellStyle createPoiStyle(String styleKey)
  {
    XSSFCellStyle poiStyle = this.workbook.getPoiWorkbook().createCellStyle();
    this.workbook.getPoiStyleMap().put(styleKey, poiStyle);
    return poiStyle;
  }

  public void createDefaultStyles()
  {
    XSSFCellStyle titleStyle = createPoiStyle("title");
    XSSFFont titleFont = createPoiFont();
    titleFont.setBoldweight((short)700);
    titleFont.setFontHeightInPoints((short)12);
    titleStyle.setFont(titleFont);
    titleStyle.setAlignment((short)2);
    titleStyle.setVerticalAlignment((short)1);
    titleStyle.setWrapText(true);
    titleStyle.setBorderBottom((short)1);
    titleStyle.setBorderLeft((short)1);
    titleStyle.setBorderRight((short)1);
    titleStyle.setBorderTop((short)1);

    XSSFCellStyle leftStyle = createPoiStyle("left");
    XSSFFont leftFont = createPoiFont();
    leftFont.setFontHeightInPoints((short)10);
    leftStyle.setFont(leftFont);
    leftStyle.setAlignment((short)1);
    leftStyle.setVerticalAlignment((short)1);
    leftStyle.setWrapText(true);
    leftStyle.setBorderBottom((short)1);
    leftStyle.setBorderLeft((short)1);
    leftStyle.setBorderRight((short)1);
    leftStyle.setBorderTop((short)1);

    XSSFCellStyle centerStyle = createPoiStyle("center");
    XSSFFont centerFont = createPoiFont();
    centerFont.setFontHeightInPoints((short)10);
    centerStyle.setFont(centerFont);
    centerStyle.setAlignment((short)2);
    centerStyle.setVerticalAlignment((short)1);
    centerStyle.setWrapText(true);
    centerStyle.setBorderBottom((short)1);
    centerStyle.setBorderLeft((short)1);
    centerStyle.setBorderRight((short)1);
    centerStyle.setBorderTop((short)1);

    XSSFCellStyle rightStyle = createPoiStyle("right");
    XSSFFont rightFont = createPoiFont();
    rightFont.setFontHeightInPoints((short)10);
    rightStyle.setFont(rightFont);
    rightStyle.setAlignment((short)3);
    rightStyle.setVerticalAlignment((short)1);
    rightStyle.setWrapText(true);
    rightStyle.setBorderBottom((short)1);
    rightStyle.setBorderLeft((short)1);
    rightStyle.setBorderRight((short)1);
    rightStyle.setBorderTop((short)1);

    for (int index = 0; index <= 10; index++) {
      XSSFCellStyle numberStyle = createPoiStyle(String.valueOf(index));
      XSSFFont numberFont = createPoiFont();

      numberFont.setFontHeightInPoints((short)10);
      numberStyle.setFont(numberFont);
      numberStyle.setAlignment((short)3);
      numberStyle.setVerticalAlignment((short)1);
      numberStyle.setWrapText(true);
      numberStyle.setBorderBottom((short)1);
      numberStyle.setBorderLeft((short)1);
      numberStyle.setBorderRight((short)1);
      numberStyle.setBorderTop((short)1);
      XSSFDataFormat df = createPoiFormat();
      String format = "";
      if (index == 0)
        format = "#0";
      else {
        format = "#0." + String.format(new StringBuilder().append("%0").append(index).append("d").toString(), new Object[] { Integer.valueOf(0) });
      }
      numberStyle.setDataFormat(df.getFormat(format));
    }
  }

  public XSSFFont createPoiFont()
  {
    XSSFFont poiFont = this.workbook.getPoiWorkbook().createFont();
    return poiFont;
  }

  public XSSFDataFormat createPoiFormat()
  {
    XSSFDataFormat poiFormat = this.workbook.getPoiWorkbook().createDataFormat();
    return poiFormat;
  }

  public Sheet createSheet(String sheetName)
  {
    Sheet sheet = new Sheet();
    sheet.setSheetName(sheetName);
    this.workbook.getSheetList().add(sheet);
    return sheet;
  }

  public void setColumnWidth(Sheet sheet, int columnIndex, int columnWidth)
  {
    sheet.getColumnWidthMap().put(Integer.valueOf(columnIndex), Integer.valueOf(columnWidth));
  }

  public void setRowHeight(Sheet sheet, int rowIndex, int rowHeight)
  {
    Row row = sheet.getRow(rowIndex);
    if (AssertUtil.isInval(row)) {
      row = createRow(sheet, rowIndex);
    }
    row.setRowHeight(rowHeight);
  }

  public void addMerge(Sheet sheet, int rowIndexBegin, int rowIndexEnd, int cellIndexBegin, int cellIndexEnd)
  {
    Merge merge = new Merge();
    merge.setRowIndexBegin(rowIndexBegin);
    merge.setRowIndexEnd(rowIndexEnd);
    merge.setCellIndexBegin(cellIndexBegin);
    merge.setCellIndexEnd(cellIndexEnd);
    sheet.getMergeList().add(merge);
  }

  public Row createRow(Sheet sheet, int rowIndex)
  {
    Row row = (Row)sheet.getRowMap().get(Integer.valueOf(rowIndex));
    if (AssertUtil.isVal(row)) {
      return row;
    }
    row = new Row();
    row.setRowIndex(rowIndex);
    sheet.getRowList().add(row);
    sheet.getRowMap().put(Integer.valueOf(rowIndex), row);
    row.setRowHeight(25);
    return row;
  }

  public void addCell(Row row, int cellIndex, String cellValue, String cellType, String styleKey)
  {
    Cell cell = (Cell)row.getCellMap().get(Integer.valueOf(cellIndex));
    if (AssertUtil.isInval(cell)) {
      cell = new Cell();
      cell.setRow(row);
      cell.setCellIndex(cellIndex);
      row.getCellList().add(cell);
      row.getCellMap().put(Integer.valueOf(cellIndex), cell);
    }
    cell.setCellValue(cellValue);
    cell.setCellType(cellType);
    cell.setStyleKey(styleKey);

    if (("S".equals(cellType)) && 
      (!this.shardStringsIndexMap.containsKey(cellValue))) {
      this.shardStringsList.add(cellValue);
      this.shardStringsIndexMap.put(cellValue, Integer.valueOf(this.shardStringsList.size() - 1));
    }
  }

  public void generateExcel()
    throws Exception
  {
    File templateFile = null;
    this.xmlFileMap = new HashMap();
    try {
      String realFilePath = "D:/" + this.workbook.getFilePath();
      String fileFullName = realFilePath + "/" + this.workbook.getFileName() + ".xlsx";
      boolean exists = new File(realFilePath).exists();
      if (!exists) {
        new File(realFilePath).mkdirs();
      }

      templateFile = new File(getUUID() + "_template.xlsx");
      templateFile.createNewFile();
      List sheetList = this.workbook.getSheetList();
      createPoiSheet(templateFile, sheetList);

      createSheetXML(sheetList);

      createSharedStringsXML();

      substitute(templateFile, this.xmlFileMap, fileFullName);
    } catch (Exception e) {
      LOGGER.error("ExcelExportUtil.generateExcel()", e);
      throw e;
    } finally {
      delTempFile(templateFile, this.xmlFileMap);
      this.workbook = null;
    }
  }

  private void createPoiSheet(File templateFile, List<Sheet> sheetList)
    throws IOException
  {
    FileOutputStream templateOS = new FileOutputStream(templateFile);
    for (int sheetIndex = 0; (AssertUtil.isVal(sheetList)) && (sheetIndex < sheetList.size()); sheetIndex++) {
      this.workbook.getPoiWorkbook().createSheet(((Sheet)sheetList.get(sheetIndex)).getSheetName());
    }
    this.workbook.getPoiWorkbook().write(templateOS);
    templateOS.flush();
    templateOS.close();
  }

  private void createSheetXML(List<Sheet> sheetList)
    throws IOException
  {
    for (int sheetIndex = 0; (AssertUtil.isVal(sheetList)) && (sheetIndex < sheetList.size()); sheetIndex++) {
      Sheet sheet = (Sheet)sheetList.get(sheetIndex);

      fillSheet(sheet);
      Collections.sort(sheet.getRowList(), comparatorRow);

      XSSFSheet poiSheet = this.workbook.getPoiWorkbook().getSheetAt(sheetIndex);
      String sheetRef = poiSheet.getPackagePart().getPartName().getName();
      sheet.setSheetRef(sheetRef.substring(1));
      List sheetFileList = new ArrayList();
      this.xmlFileMap.put(sheetRef.substring(1), sheetFileList);

      XMLWriterManager manager = new XMLWriterManager(sheet);
      for (int i = 0; ; i++) { manager.getClass(); if (i >= 10) break;
        sheetFileList.add(File.createTempFile("sheet", ".xml"));
      }
      manager.excuteStartSheetWriter();
    }
  }

  private void createSheetColsXML(SheetWriter sheetWriter, Sheet sheet) throws IOException{
    Map columnWidthMap = sheet.getColumnWidthMap();
    if ((AssertUtil.isVal(columnWidthMap)) && (!columnWidthMap.isEmpty())) {
      Set<Integer> columnIndexSet = columnWidthMap.keySet();
      sheetWriter.beginCols();
      for (Integer columnIndex : columnIndexSet) {
        sheetWriter.createCols(columnIndex.intValue(), ((Integer)columnWidthMap.get(columnIndex)).intValue());
      }
      sheetWriter.endCols();
    }
  }

  private void multithreadCreateRowXML(int thisThreadNum, int threadCount, Sheet sheet, List<Row> rowList)
    throws IOException
  {
    List sheetFileList = (List)this.xmlFileMap.get(sheet.getSheetRef());
    File sheetFile = (File)sheetFileList.get(thisThreadNum - 1);

    Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sheetFile), "UTF-8"));
    SheetWriter sheetWriter = new SheetWriter(writer);
    RowWriter rowWriter = new RowWriter(writer);
    CellWriter cellWriter = new CellWriter(writer);

    if (thisThreadNum == 1) {
      sheetWriter.beginWorksheet();

      createSheetColsXML(sheetWriter, sheet);

      sheetWriter.beginSheetData();
    }
    int rowListSize = AssertUtil.isVal(rowList) ? rowList.size() : 0;
    for (int rowIndex = 0; rowIndex < rowListSize; rowIndex++) {
      Row row = (Row)rowList.get(rowIndex);
      List cellList = row.getCellList();
      Collections.sort(cellList, comparatorCell);
      rowWriter.insertRow(row.getRowIndex(), row.getRowHeight());
      int cellListSize = cellList.size();
      for (int columnIndex = 0; columnIndex < cellListSize; columnIndex++) {
        Cell cell = (Cell)cellList.get(columnIndex);
        String cellValue = cell.getCellValue();
        String styleKey = cell.getStyleKey();
        String cellType = cell.getCellType();
        int cellIndex = cell.getCellIndex();

        XSSFCellStyle poiStyle = (XSSFCellStyle)this.workbook.getPoiStyleMap().get(styleKey);
        int poiStyleIndex = AssertUtil.isVal(poiStyle) ? poiStyle.getIndex() : -1;

        if ("S".equals(cellType))
          cellWriter.createCell(cell.getRow().getRowIndex(), cellIndex, cellValue, ((Integer)this.shardStringsIndexMap.get(cellValue)).intValue(), poiStyleIndex);
        else if ("N".equals(cellType)) {
          cellWriter.createCell(cell.getRow().getRowIndex(), cellIndex, AssertUtil.getDecimal(cellValue).doubleValue(), poiStyleIndex);
        }
      }
      rowWriter.endRow();
    }

    if (thisThreadNum == threadCount) {
      sheetWriter.endSheetData();

      createMergeXML(writer, sheet);

      sheetWriter.endWorksheet();
    }

    writer.flush();
    writer.close();
  }

  private void createMergeXML(Writer writer, Sheet sheet)
    throws IOException
  {
    List<Merge> mergeList = sheet.getMergeList();
    MergeCellWriter mregeWriter = new MergeCellWriter(writer);
    if (AssertUtil.isVal(mergeList)) {
      mregeWriter.beginMerge(mergeList.size());
      for (Merge merge : mergeList) {
        mregeWriter.merge(merge.getRowIndexBegin(), merge.getRowIndexEnd(), merge.getCellIndexBegin(), merge.getCellIndexEnd());
      }
      mregeWriter.endMerge();
    }
  }

  private void createSharedStringsXML()
    throws IOException
  {
    List sharedStringsFileList = new ArrayList();
    this.xmlFileMap.put("xl/sharedStrings.xml", sharedStringsFileList);

//    XMLWriterManager manager = new XMLWriterManager(this.shardStringsList);
    XMLWriterManager manager = new XMLWriterManager();

    for (int i = 0; ; i++) { manager.getClass(); if (i >= 10) break;
      sharedStringsFileList.add(File.createTempFile("shardStrings", ".xml"));
    }
    manager.excuteStartSharedStringWriter();
  }

  private void multithreadCreateSharedStringsXML(int thisThreadNum, int threadCount, List<String> sharedStringsList)
    throws IOException
  {
    List sharedStringsFileList = (List)this.xmlFileMap.get("xl/sharedStrings.xml");
    File sharedStringsFile = (File)sharedStringsFileList.get(thisThreadNum - 1);

    Writer stringWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sharedStringsFile), "UTF-8"));
    SharedStringWriter sharedStringWriter = new SharedStringWriter(stringWriter);

    if (thisThreadNum == 1) {
      sharedStringWriter.beginSharedString();
    }
    for (int index = 0; (AssertUtil.isVal(sharedStringsList)) && (index < sharedStringsList.size()); index++) {
      sharedStringWriter.createSharedString((String)sharedStringsList.get(index));
    }

    if (thisThreadNum == threadCount) {
      sharedStringWriter.endSharedString();
    }
    stringWriter.flush();
    stringWriter.close();
  }

  private void delTempFile(File templateFile, Map<String, List<File>> xmlFileMap)
  {
    System.gc();
    if ((templateFile.isFile()) && (templateFile.exists())) {
      templateFile.delete();
    }
    if (AssertUtil.isVal(xmlFileMap)) {
      Set<String> xmlFileNameSet = xmlFileMap.keySet();
      for (String xmlFileName : xmlFileNameSet) {
        List<File> xmlFileList = (List)xmlFileMap.get(xmlFileName);
        for (File xmlFile : xmlFileList)
          if ((xmlFile != null) && (xmlFile.isFile()) && (xmlFile.exists()))
            xmlFile.delete();
      }
    }
  }

  private void substitute(File templateFile, Map<String, List<File>> xmlFileMap, String fileFullName)
    throws IOException
  {
    FileOutputStream targetFileOutputStream = new FileOutputStream(fileFullName);
    ZipFile zipFile = new ZipFile(templateFile);
    ZipOutputStream zos = new ZipOutputStream(targetFileOutputStream);

    Set<String> xmlFileNameSet = xmlFileMap.keySet();
    Enumeration en = zipFile.entries();
    ZipEntry ze;
    while (en.hasMoreElements()) {
      ze = (ZipEntry)en.nextElement();
      if (!xmlFileNameSet.contains(ze.getName())) {
        zos.putNextEntry(new ZipEntry(ze.getName()));
        InputStream is = zipFile.getInputStream(ze);
        copyStream(is, zos);
        is.close();
      }

    }

    for (String xmlFileName : xmlFileNameSet) {
      zos.putNextEntry(new ZipEntry(xmlFileName));
      List<File> xmlFileList = (List)xmlFileMap.get(xmlFileName);
      for (File xmlFile : xmlFileList) {
        InputStream is = new FileInputStream(xmlFile);
        copyStream(is, zos);
        is.close();
      }
    }
    zos.close();
    targetFileOutputStream.close();
  }

  private void copyStream(InputStream in, OutputStream out)
    throws IOException
  {
    byte[] chunk = new byte[1024];
    int count;
    while ((count = in.read(chunk)) >= 0)
      out.write(chunk, 0, count);
  }

  private void fillSheet(Sheet sheet)
  {
    List mergeList = sheet.getMergeList();
    Merge merge = null;
    for (int i = 0; i < mergeList.size(); i++) {
      merge = (Merge)mergeList.get(i);
      for (int rowIndex = merge.getRowIndexBegin(); rowIndex <= merge.getRowIndexEnd(); rowIndex++) {
        Row row = sheet.getRow(rowIndex);
        for (int cellIndex = merge.getCellIndexBegin(); cellIndex <= merge.getCellIndexEnd(); cellIndex++) {
          if (AssertUtil.isInval(row)) {
            row = createRow(sheet, rowIndex);
          }
          if (AssertUtil.isInval(row.getCell(cellIndex)))
            addCell(row, cellIndex, "", "S", "left");
        }
      }
    }
  }

  private static String changeToLetter(int columnNo)
  {
    String letter = "";
    int div = columnNo / 26;
    int mod = columnNo % 26;

    if (columnNo <= 26) {
      letter = (char)(columnNo + 64) + "";
    } else {
      if (mod == 0) {
        mod = 26;
        div -= 1;
      }
      letter = (char)(div + 64) + "" + (char)(mod + 64) + "";
    }
    return letter;
  }

  private static final synchronized String getUUID()
  {
    return UUID.randomUUID().toString().replace("-", "");
  }

  private void substitute(File templateFile, Map<String, List<File>> xmlFileMap, OutputStream outstream) throws IOException
  {
    ZipFile zipFile = new ZipFile(templateFile);
    ZipOutputStream zos = new ZipOutputStream(outstream);
    Set<String> xmlFileNameSet = xmlFileMap.keySet();
    Enumeration en = zipFile.entries();
    ZipEntry ze;
    while (en.hasMoreElements()) {
      ze = (ZipEntry)en.nextElement();
      if (!xmlFileNameSet.contains(ze.getName())) {
        zos.putNextEntry(new ZipEntry(ze.getName()));
        InputStream is = zipFile.getInputStream(ze);
        copyStream(is, zos);
        is.close();
      }

    }

    for (String xmlFileName : xmlFileNameSet) {
      zos.putNextEntry(new ZipEntry(xmlFileName));
      List<File> xmlFileList = (List)xmlFileMap.get(xmlFileName);
      for (File xmlFile : xmlFileList) {
        InputStream is = new FileInputStream(xmlFile);
        copyStream(is, zos);
        is.close();
      }
    }
    zos.close();
  }

  public void generateExcelNew(OutputStream outStream) throws Exception {
    File templateFile = null;
    this.xmlFileMap = new HashMap();
    try
    {
      String path = FileUtil.getWebAppRealPath() + "/excelTemp/";
      File tmp = new File(path);
      if (!tmp.exists()) {
        tmp.mkdir();
      }
      templateFile = new File(path + getUUID() + "_template.xlsx");
      templateFile.createNewFile();
      List sheetList = this.workbook.getSheetList();
      createPoiSheet(templateFile, sheetList);

      createSheetXML(sheetList);

      createSharedStringsXML();

      substitute(templateFile, this.xmlFileMap, outStream);
    } catch (Exception e) {
      LOGGER.error("ExcelExportUtil.generateExcel()", e);
      throw e;
    } finally {
      delTempFile(templateFile, this.xmlFileMap);
      this.workbook = null;
    }
  }

  public class XMLWriterThread
    implements Runnable
  {
    private int thisThreadNum;
    private int threadCount;
    private Sheet sheet;
    private List<Row> rowList;
    private List<String> sheardStringsList;
    private boolean isFinish;

    public XMLWriterThread(int thisThreadNum, Sheet sheet, List<Row> rowList)
    {
      this.thisThreadNum = thisThreadNum;
//      this.threadCount = threadCount;
      this.sheet = sheet;
      this.rowList = rowList;
    }

    public XMLWriterThread(int thisThreadNum, List<String> sheardStringsList)
    {
      this.thisThreadNum = thisThreadNum;
//      this.threadCount = threadCount;
      this.sheardStringsList = sheardStringsList;
    }

    public void run() {
      try {
        if (this.rowList != null)
        {
          ExcelExportUtil.this.multithreadCreateRowXML(this.thisThreadNum, this.threadCount, this.sheet, this.rowList);
        }

        if (this.sheardStringsList != null)
        {
          ExcelExportUtil.this.multithreadCreateSharedStringsXML(this.thisThreadNum, this.threadCount, this.sheardStringsList);
        }

        this.isFinish = true;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    public boolean isFinish() {
      return this.isFinish;
    }
  }

  public class XMLWriterManager
  {
    public final int MAXTHREADCOUNT = 10;
    private int threadCount;
    private Sheet sheet;
    private List<Row> rowList;
    private List<String> sharedStringsList;
    private List<ExcelExportUtil.XMLWriterThread> XMLWriterThreadList = new ArrayList();

    public XMLWriterManager(Sheet sheet)
    {
      this.sheet = sheet;
    }

    public XMLWriterManager()
    {
      this.sharedStringsList = sharedStringsList;
    }

    public void excuteStartSheetWriter()
    {
      startSheetWriter();
      checkIsFinish();
    }

    public void excuteStartSharedStringWriter()
    {
      startSharedStringWriter();
      checkIsFinish();
    }

    private void checkIsFinish()
    {
      while (true)
      {
        boolean finish = true;

        for (ExcelExportUtil.XMLWriterThread thread : this.XMLWriterThreadList) {
          if (!thread.isFinish()) {
            finish = false;
            break;
          }
        }

        if (finish) {
          break;
        }
        try
        {
          Thread.sleep(100L);
        } catch (InterruptedException localInterruptedException1) {
        }
      }
    }

    private void startSheetWriter() {
      this.rowList = this.sheet.getRowList();
      int rowSize = this.rowList.size();

      this.threadCount = (rowSize / 20 + (rowSize % 20 > 0 ? 1 : 0));

      this.threadCount = (this.threadCount >= 10 ? 10 : this.threadCount);

      ExcelExportUtil.LOGGER.info("sheetName:" + this.sheet.getSheetName() + ", startSheetWriter 开始， 共并发执行" + this.threadCount + "个线程");

      List perThreadRowList = null;

      int perThreadRowCount = this.threadCount > 0 ? rowSize / this.threadCount : 0;
      int mod = this.threadCount > 0 ? rowSize % this.threadCount : 0;
      for (int i = 0; i < this.threadCount; i++) {
        if (i == this.threadCount - 1)
          perThreadRowList = this.rowList.subList(i * perThreadRowCount, perThreadRowCount * (i + 1) + mod);
        else {
          perThreadRowList = this.rowList.subList(i * perThreadRowCount, perThreadRowCount * (i + 1));
        }
//        ExcelExportUtil.XMLWriterThread XMLWriterThread = new ExcelExportUtil.XMLWriterThread(ExcelExportUtil.this, i + 1, this.threadCount, this.sheet, perThreadRowList);
        ExcelExportUtil.XMLWriterThread XMLWriterThread = new ExcelExportUtil.XMLWriterThread(i + 1, perThreadRowList);
        new Thread(XMLWriterThread).start();
        this.XMLWriterThreadList.add(XMLWriterThread);
      }
    }

    private void startSharedStringWriter() {
      int sharedStringsSize = this.sharedStringsList.size();

      this.threadCount = (sharedStringsSize / 20 + (sharedStringsSize % 20 > 0 ? 1 : 0));

      this.threadCount = (this.threadCount >= 10 ? 10 : this.threadCount);

      ExcelExportUtil.LOGGER.info("startSharedStringWriter 开始， 共并发执行" + this.threadCount + "个线程");

      List perThreadTextList = null;

      int perThreadTextCount = this.threadCount > 0 ? sharedStringsSize / this.threadCount : 0;
      int mod = this.threadCount > 0 ? sharedStringsSize % this.threadCount : 0;
      for (int i = 0; i < this.threadCount; i++) {
        if (i == this.threadCount - 1)
          perThreadTextList = this.sharedStringsList.subList(i * perThreadTextCount, perThreadTextCount * (i + 1) + mod);
        else {
          perThreadTextList = this.sharedStringsList.subList(i * perThreadTextCount, perThreadTextCount * (i + 1));
        }
//        ExcelExportUtil.XMLWriterThread XMLWriterThread = new ExcelExportUtil.XMLWriterThread(ExcelExportUtil.this, i + 1, this.threadCount, perThreadTextList);
        ExcelExportUtil.XMLWriterThread XMLWriterThread = new ExcelExportUtil.XMLWriterThread(i + 1, perThreadTextList);
        new Thread(XMLWriterThread).start();
        this.XMLWriterThreadList.add(XMLWriterThread);
      }
    }
  }

  private static class SharedStringWriter extends ExcelExportUtil.XMLWriter
  {
    private static final String[] xmlCode = new String[256];

    public SharedStringWriter(Writer _out)
    {
      super(_out);
    }

    public void beginSharedString() throws IOException {
      this._out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      this._out.write("<sst count=\"490\" uniqueCount=\"182\" xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">");
    }

    public void endSharedString() throws IOException {
      this._out.write("</sst>");
    }

    public void createSharedString(String content) throws IOException {
      this._out.write("<si><t>" + encode(content) + "</t></si>");
    }

    public static String encode(String string) throws UnsupportedEncodingException {
      if (string == null) {
        return "";
      }
      int n = string.length();

      StringBuffer buffer = new StringBuffer();
      for (int i = 0; i < n; i++) {
        char character = string.charAt(i);
        try {
          String xmlchar = xmlCode[character];
          if (xmlchar == null)
            buffer.append(character);
          else
            buffer.append(xmlCode[character]);
        }
        catch (ArrayIndexOutOfBoundsException aioobe) {
          buffer.append(character);
        }
      }
      return buffer.toString();
    }

    static
    {
      xmlCode[39] = "'";
      xmlCode[34] = "\"";
      xmlCode[38] = "&amp;";
      xmlCode[60] = "&lt;";
      xmlCode[62] = "&gt;";
    }
  }

  private static class MergeCellWriter extends ExcelExportUtil.XMLWriter
  {
    public MergeCellWriter(Writer _out)
    {
      super(_out);
    }

    public void beginMerge(int count) throws IOException {
      this._out.write("<mergeCells count=\"" + count + "\">");
    }

    public void merge(int rowNoBegin, int rowNoEnd, int columnNoBegin, int columnNoEnd) throws IOException {
      String range = new CellRangeAddress(rowNoBegin, rowNoEnd, columnNoBegin, columnNoEnd).formatAsString();
      this._out.write("<mergeCell ref=\"" + range + "\" />");
    }

    public void endMerge() throws IOException {
      this._out.write("</mergeCells>");
    }
  }

  private static class CellWriter extends ExcelExportUtil.XMLWriter
  {
    public CellWriter(Writer _out)
    {
      super(_out);
    }

    public void createCell(int rowIndex, int columnIndex, String value, int textIndex, int styleIndex) throws IOException {
      String ref = ExcelExportUtil.changeToLetter(columnIndex + 1) + (rowIndex + 1);
      this._out.write("<c r=\"" + ref + "\"");
      if (styleIndex != -1) {
        this._out.write(" s=\"" + styleIndex + "\"");
      }
      if (AssertUtil.isVal(value)) {
        this._out.write(" t=\"s\"");
        this._out.write(">");
        this._out.write("<v>" + textIndex + "</v>");
        this._out.write("</c>");
      } else {
        this._out.write("/>");
      }
    }

    public void createCell(int rowIndex, int columnIndex, double value, int styleIndex) throws IOException {
      String ref = ExcelExportUtil.changeToLetter(columnIndex + 1) + (rowIndex + 1);
      this._out.write("<c r=\"" + ref + "\"");
      if (AssertUtil.isVal(Double.valueOf(value))) {
        this._out.write(" t=\"n\"");
      }
      if (styleIndex != -1) {
        this._out.write(" s=\"" + styleIndex + "\"");
      }
      this._out.write(">");
      if (AssertUtil.isVal(Double.valueOf(value))) {
        this._out.write("<v>" + value + "</v>");
      }
      this._out.write("</c>");
    }
  }

  private static class RowWriter extends ExcelExportUtil.XMLWriter
  {
    public RowWriter(Writer _out)
    {
      super(_out);
    }

    public void insertRow(int rownum, int rowHeight) throws IOException {
      this._out.write("<row r=\"" + (rownum + 1) + "\" ");
      if (rowHeight > 0) {
        this._out.write("ht=\"" + rowHeight + "\" customHeight=\"1\"");
      }
      this._out.write(">" + LINE_SEPARATOR);
    }

    public void endRow() throws IOException {
      this._out.write("</row>" + LINE_SEPARATOR);
    }
  }

  private static class SheetWriter extends ExcelExportUtil.XMLWriter
  {
    public SheetWriter(Writer _out)
    {
      super(_out);
    }

    public void beginWorksheet() throws IOException {
      this._out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      this._out.write("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">");
    }

    public void endWorksheet() throws IOException {
      this._out.write("</worksheet>");
    }

    public void beginSheetData() throws IOException {
      this._out.write("<sheetData>" + LINE_SEPARATOR);
    }

    public void endSheetData() throws IOException {
      this._out.write("</sheetData>");
    }

    public void beginCols() throws IOException {
      this._out.write("<cols>");
    }

    public void endCols() throws IOException {
      this._out.write("</cols>");
    }

    public void createCols(int columnIndex, int columnWidth) throws IOException {
      this._out.write("<col min=\"" + columnIndex + "\" max=\"" + columnIndex + "\" ");
      this._out.write("width=\"" + columnWidth + "\" ");
      this._out.write("bestFit=\"1\" customWidth=\"1\" ");
      this._out.write("/>");
    }
  }

  private static class XMLWriter
  {
    public final Writer _out;
    public static String LINE_SEPARATOR = System.getProperty("line.separator");

    public XMLWriter(Writer out) {
      this._out = out;
    }
  }

  private class ComparatorCell
    implements Comparator<Cell>
  {
    private ComparatorCell()
    {
    }

    public int compare(Cell cell0, Cell cell1)
    {
      return cell0.getCellIndex() - cell1.getCellIndex();
    }
  }

  private class ComparatorRow implements Comparator<Row>
  {
    private ComparatorRow()
    {
    }

    public int compare(Row row0, Row row1)
    {
      return row0.getRowIndex() - row1.getRowIndex();
    }
  }
}