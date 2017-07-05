package com.webapp.framework.utils.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import com.webapp.framework.utils.tools.Format;
import com.webapp.framework.utils.tools.StringUtil;

public class ExcelUtil {
	private String dataFormat = "m/d/yy h:mm";

	private Map<HSSFWorkbook, HSSFCellStyle> dateStyleMaps = new HashMap();
	private String[] heanders;
	private String[] beannames;
	private int columnWidth = 16;
	private int rowHeight = 25;
	private boolean columnWidthFlag = false;
	private Map<Integer, Integer> columnWidthMap = null;

	public ExcelUtil() {
	}

	public ExcelUtil(String[] heanders) {
		this.heanders = heanders;
	}

	public HSSFWorkbook doExportXLS(List dateList, String sheetname, boolean isEntity) throws IOException {
		HSSFWorkbook wb = new HSSFWorkbook();
		if (dateList.size() > 32767) {
			createXLSEntityBulk(wb, dateList);
		} else {
			HSSFSheet sheet = wb.createSheet(sheetname);
			createXLSHeader(sheet);
			if (isEntity)
				createXLSEntity(wb, sheet, dateList);
			else {
				createXLS(wb, sheet, dateList);
			}
		}

		this.dateStyleMaps.clear();
		return wb;
	}

	public HSSFWorkbook doExportXLS(List dateList, String sheetname, boolean isEntity, File file) throws IOException {
		POIFSFileSystem poif = new POIFSFileSystem(new FileInputStream(file));
		HSSFWorkbook wb = new HSSFWorkbook(poif);
		if (dateList.size() > 32767) {
			createXLSEntityBulk(wb, dateList);
		} else {
			HSSFSheet sheet = wb.createSheet(sheetname);
			createXLSHeader(sheet);
			if (isEntity)
				createXLSEntity(wb, sheet, dateList);
			else {
				createXLS(wb, sheet, dateList);
			}
		}

		this.dateStyleMaps.clear();
		return wb;
	}

	public void createXLSHeader(HSSFSheet sheet) {
		for (int i = 0; i < this.heanders.length; i++)
			setStringValue(sheet, 0, (short) i, this.heanders[i]);
	}

	public void createXLS(HSSFWorkbook wb, HSSFSheet sheet, List<Object[]> dateList) {
		for (int i = 1; i <= dateList.size(); i++) {
			Object[] object = (Object[]) dateList.get(i - 1);
			for (int j = 0; j < object.length; j++)
				doSetCell(wb, sheet, (short) i, (short) j, object[j]);
		}
	}

	public void createXLSEntity(HSSFWorkbook wb, HSSFSheet sheet, List<Object> dateList) {
		for (int i = 1; i <= dateList.size(); i++) {
			Object bean = dateList.get(i - 1);
			for (int j = 0; j < this.beannames.length; j++) {
				BeanWrapper bw = new BeanWrapperImpl(bean);
				doSetCell(wb, sheet, (short) i, (short) j, bw.getPropertyValue(this.beannames[j]));
			}
		}
	}

	public HSSFWorkbook createXLSEntityBulk(HSSFWorkbook wb, List<Object> dateList) {
		int sublistIndex = 0;
		int perSheetMaxSize = 32767;
		int sheetindex = 1;

		while (sublistIndex < dateList.size()) {
			List subList = dateList.subList(sublistIndex, dateList.size());

			HSSFSheet sheet = wb.createSheet("" + sheetindex);

			createXLSHeader(sheet);
			long row = 1L;
			for (int i = 1; i <= subList.size(); i++) {
				sublistIndex++;

				Object bean = subList.get(i - 1);

				for (int j = 0; j < this.beannames.length; j++) {
					BeanWrapper bw = new BeanWrapperImpl(bean);
					doSetCell(wb, sheet, (short) i, (short) j, bw.getPropertyValue(this.beannames[j]));
				}

				row += 1L;
				if (row > perSheetMaxSize) {
					break;
				}
			}

			sheetindex++;
		}

		this.dateStyleMaps.clear();
		return wb;
	}

	public void doSetCell(HSSFWorkbook wb, HSSFSheet sheet, int rowNum, int colNum, Object value) {
		if (value != null)
			if ((value instanceof Number)) {
				setDoubleValue(sheet, rowNum, colNum, Double.valueOf(value.toString()));
			} else if ((value instanceof String)) {
				setStringValue(sheet, rowNum, colNum, value.toString());
			} else if ((value instanceof Date)) {
				HSSFCellStyle dateStyle = null;
				if (this.dateStyleMaps.containsKey(wb)) {
					dateStyle = (HSSFCellStyle) this.dateStyleMaps.get(wb);
				} else {
					dateStyle = wb.createCellStyle();
					this.dateStyleMaps.put(wb, dateStyle);
				}
				setDateValue(sheet, dateStyle, rowNum, colNum, (Date) value);
			}
	}

	public void setDoubleValue(HSSFSheet sheet, int rowNum, int colNum, Double value) {
		HSSFCell cell = getMyCell(sheet, rowNum, colNum);
		cell.setCellType(0);
		cell.setCellValue(value.doubleValue());
	}

	public void setDateValue(HSSFSheet sheet, HSSFCellStyle dateStyle, int rowNum, int colNum, Date value) {
		HSSFCell cell = getMyCell(sheet, rowNum, colNum);

		dateStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(this.dataFormat));
		cell.setCellStyle(dateStyle);
		cell.setCellValue(value);
	}

	public void setStringValue(HSSFSheet sheet, int rowNum, int colNum, String value) {
		HSSFCell cell = getMyCell(sheet, rowNum, colNum);

		cell.setCellType(1);
		HSSFRichTextString str = new HSSFRichTextString(value);
		cell.setCellValue(str);
	}

	private HSSFCell getMyCell(HSSFSheet sheet, int rowNum, int colNum) {
		HSSFRow row = sheet.getRow(rowNum);
		if (null == row) {
			row = sheet.createRow(rowNum);
		}
		HSSFCell cell = row.getCell((short) colNum);
		if (null == cell) {
			cell = row.createCell((short) colNum);
		}
		return cell;
	}

	public void createUploadXLSEntity(HSSFWorkbook wb, HSSFSheet sheet, List<Object> dateList) {
		for (int i = 2; i <= dateList.size() + 1; i++) {
			Object bean = dateList.get(i - 2);
			for (int j = 2; j <= this.beannames.length + 1; j++) {
				getMyCell(sheet, (short) i, 1).setCellValue(i - 1);
				sheet.getRow(i).getCell((short) 0);
				BeanWrapper bw = new BeanWrapperImpl(bean);
				doSetCell(wb, sheet, (short) i, (short) j, bw.getPropertyValue(this.beannames[(j - 2)]));
			}
		}
	}

	public String[] getBeannames() {
		return this.beannames;
	}

	public void setBeannames(String[] beannames) {
		this.beannames = beannames;
	}

	public String getDataFormat() {
		return this.dataFormat;
	}

	public void setDataFormat(String dataFormat) {
		this.dataFormat = dataFormat;
	}

	public void doExportXLSNew(List dateList, String sheetname, boolean isEntity, OutputStream outStream) {
		ExcelExportUtil excelExport = ExcelExportUtil.getInstance();

		excelExport.createDefaultStyles();
		Sheet sheet = excelExport.createSheet(sheetname);

		Row row = excelExport.createRow(sheet, 0);
		int currow = 0;
		row = excelExport.createRow(sheet, currow);
		for (int columnIndex = 0; columnIndex < this.heanders.length; columnIndex++) {
			String cellValue = "";
			String cellType = "";
			String styleKey = "";
			cellValue = this.heanders[columnIndex];
			cellType = "S";
			styleKey = "title";
			setHeaderWidth(excelExport, sheet, columnIndex, cellValue, true);
			excelExport.addCell(row, columnIndex, cellValue, cellType, styleKey);
		}

		for (int i = 1; i <= dateList.size(); i++) {
			Object bean = dateList.get(i - 1);
			row = excelExport.createRow(sheet, i);
			for (int columnIndex = 0; columnIndex < this.beannames.length; columnIndex++) {
				BeanWrapper bw = new BeanWrapperImpl(bean);
				String cellValue = "";
				String cellType = "";
				String styleKey = "";
				String[] colname = this.beannames[columnIndex].split(":");

				Object obj = bw.getPropertyValue(colname[0]);
				if ((obj instanceof Number)) {
					cellValue = obj.toString();
					styleKey = "0";
					if (colname.length > 1) {
						styleKey = colname[1];
					} else if ((colname[0].contains("amt")) || (colname[0].contains("price"))
							|| (colname[0].contains("AMT")) || (colname[0].contains("PRICE")))
						styleKey = "2";
					else if (colname[0].contains("sl2")) {
						styleKey = "3";
					}

					cellType = "N";
				} else if ((obj instanceof String)) {
					cellValue = obj.toString();
					cellType = "S";
					styleKey = "left";
				} else if ((obj instanceof Date)) {
					cellValue = Format.format("yyyy-MM-dd", (Date) obj);
					if ((colname.length > 1) && ("datetime".equals(colname[1]))) {
						cellValue = Format.format("yyyy-MM-dd HH:mm", (Date) obj);
					}

					cellType = "S";
					styleKey = "center";
				} else {
					cellValue = "";
					cellType = "S";
					styleKey = "left";
				}

				setRowAutoHeight(excelExport, sheet, i, columnIndex, cellValue);
				excelExport.addCell(row, columnIndex, cellValue, cellType, styleKey);
			}
		}
		try {
			excelExport.generateExcelNew(outStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setColumnWidth(ExcelExportUtil excelExport, Sheet sheet, int columnIndex, String cellValue) {
		int width = cellValue.getBytes().length;
		if (this.columnWidthFlag) {
			this.columnWidthMap = sheet.getColumnWidthMap();
		}
		int oldColumnWidth = this.columnWidth;
		if (null != this.columnWidthMap) {
			oldColumnWidth = ((Integer) this.columnWidthMap.get(Integer.valueOf(columnIndex + 1))).intValue();
		}
		if (width > oldColumnWidth) {
			excelExport.setColumnWidth(sheet, columnIndex + 1, width);
			this.columnWidthFlag = true;
		} else {
			this.columnWidthFlag = false;
		}
	}

	public int getExcelCellAutoHeight(String cellValue, int fontCountInline) {
		int defaultRowHeight = this.rowHeight;
		float defaultCount = 0.0F;
		for (int i = 0; i < cellValue.length(); i++) {
			float ff = getregex(cellValue.substring(i, i + 1));
			defaultCount += ff;
		}
		int heigt = ((int) (defaultCount / fontCountInline) + 1) * defaultRowHeight;
		return heigt;
	}

	public int getExcelCellAutoWidth(String cellValue) {
		float defaultCount = 0.0F;
		for (int i = 0; i < cellValue.length(); i++) {
			float ff = getregex(cellValue.substring(i, i + 1));
			defaultCount += ff;
		}
		int width = (int) Math.ceil(defaultCount);
		return width;
	}

	public static float getregex(String charStr) {
		if (charStr == " ") {
			return 0.5F;
		}

		if (Pattern.compile("^[A-Za-z0-9]+$").matcher(charStr).matches()) {
			return 0.5F;
		}

		if (Pattern.compile("[u4e00-u9fa5]+$").matcher(charStr).matches()) {
			return 1.0F;
		}

		if (Pattern.compile("[^x00-xff]").matcher(charStr).matches()) {
			return 1.0F;
		}
		return 0.5F;
	}

	public void setRowAutoHeight(ExcelExportUtil excelExport, Sheet sheet, int rowIndex, int columnIndex,
			String cellValue) {
		Integer oldRowHeight = Integer.valueOf(sheet.getRow(rowIndex).getRowHeight());
		Map map = sheet.getColumnWidthMap();
		int fontCountInline = 8;
		if ((null != map) && (!map.isEmpty())) {
			fontCountInline = ((Integer) map.get(Integer.valueOf(columnIndex + 1))).intValue();
		}
		int newRowHeight = getExcelCellAutoHeight(cellValue + "", fontCountInline);
		if (newRowHeight < this.rowHeight) {
			newRowHeight = this.rowHeight;
		}
		if (null != oldRowHeight) {
			if (newRowHeight > oldRowHeight.intValue())
				excelExport.setRowHeight(sheet, rowIndex, newRowHeight);
		} else
			excelExport.setRowHeight(sheet, rowIndex, newRowHeight);
	}

	public void setHeaderWidth(ExcelExportUtil excelExport, Sheet sheet, int columnIndex, String headerValue,
			boolean flag) {
		int width = this.columnWidth;
		if (StringUtil.isNotNull(headerValue)) {
			int headerWidth = headerValue.getBytes().length * 13 / 10;
			if (headerWidth > width) {
				width = headerWidth;
			}
		}
		if (flag) {
			this.columnWidthMap = sheet.getColumnWidthMap();
			if ((null != this.columnWidthMap) && (!this.columnWidthMap.isEmpty())
					&& (this.columnWidthMap.size() == this.heanders.length)) {
				int oldColumnWidth = ((Integer) this.columnWidthMap.get(Integer.valueOf(columnIndex + 1))).intValue();
				if (oldColumnWidth > width) {
					width = oldColumnWidth;
				}
			}
		}
		excelExport.setColumnWidth(sheet, columnIndex + 1, width);
		this.columnWidthFlag = true;
	}
}