package com.webapp.framework.utils.tools.bean;

public class FileInfo
{
  private String fileName;
  private String filePath;
  private Long fileLength;
  private String fileExtName;

  public String getFileName()
  {
    return this.fileName;
  }
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }
  public String getFilePath() {
    return this.filePath;
  }
  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }
  public Long getFileLength() {
    return this.fileLength;
  }
  public void setFileLength(Long fileLength) {
    this.fileLength = fileLength;
  }
  public String getFileExtName() {
    return this.fileExtName;
  }
  public void setFileExtName(String fileExtName) {
    this.fileExtName = fileExtName;
  }
}