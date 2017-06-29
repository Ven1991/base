package com.webapp.framework.utils.file.upload.bean;

import com.webapp.framework.utils.tools.StringUtil;

public class UploadFileBean
{
  private byte[] buf;
  private String fileExtName;
  private String newFileName;
  private String oldFileName;
  private Long fileLength;
  private String filePath;
  private String fileFullName;
  private String errMsg;
  private String inputName;

  public UploadFileBean(byte[] buf, String oldFileName, Long fileLength)
  {
    String newFileName = StringUtil.getMd5(buf);
    String dir1 = newFileName.substring(0, 2);
    String dir2 = newFileName.substring(2, 4);
    String dir3 = newFileName.substring(4, 6);
    String file = newFileName.substring(6);
    String fileExtName = getExtension(oldFileName);

    this.newFileName = (file + "." + fileExtName);
    this.filePath = ("/" + dir1 + "/" + dir2 + "/" + dir3);
    this.fileFullName = (this.filePath + "/" + this.newFileName);
    this.oldFileName = oldFileName;
    this.fileExtName = fileExtName;
    this.buf = buf;
    this.fileLength = fileLength;
  }

  public UploadFileBean(byte[] buf, String oldFileName, Long fileLength, String inputName) {
    String newFileName = StringUtil.getMd5(buf);
    String dir1 = newFileName.substring(0, 2);
    String dir2 = newFileName.substring(2, 4);
    String dir3 = newFileName.substring(4, 6);
    String file = newFileName.substring(6);
    String fileExtName = getExtension(oldFileName);

    this.newFileName = (file + "." + fileExtName);
    this.filePath = ("/" + dir1 + "/" + dir2 + "/" + dir3);
    this.fileFullName = (this.filePath + "/" + this.newFileName);
    this.oldFileName = oldFileName;
    this.fileExtName = fileExtName;
    this.buf = buf;
    this.fileLength = fileLength;
    this.inputName = inputName;
  }
  public UploadFileBean(String errmsg, String inputName) {
    this.errMsg = errmsg;
    this.inputName = inputName;
  }

  public String getInputName() {
    return this.inputName;
  }
  public void setInputName(String inputName) {
    this.inputName = inputName;
  }
  public byte[] getBuf() {
    return this.buf;
  }

  public void setBuf(byte[] buf) {
    this.buf = buf;
  }

  public String getFileExtName() {
    return this.fileExtName;
  }

  public void setFileExtName(String fileExtName) {
    this.fileExtName = fileExtName;
  }

  public String getNewFileName() {
    return this.newFileName;
  }

  public void setNewFileName(String newFileName) {
    this.newFileName = newFileName;
  }

  public Long getFileLength() {
    return this.fileLength;
  }

  public void setFileLength(Long fileLength) {
    this.fileLength = fileLength;
  }

  public String getFilePath() {
    return this.filePath;
  }

  public String getFileFullName() {
    return this.fileFullName;
  }

  public String getErrMsg() {
    return this.errMsg;
  }

  public void setErrMsg(String errMsg) {
    this.errMsg = errMsg;
  }
  public String getOldFileName() {
    return this.oldFileName;
  }
  public void setOldFileName(String oldFileName) {
    this.oldFileName = oldFileName;
  }

  private static String getExtension(String fileName)
  {
    return fileName.substring(fileName.lastIndexOf(".") + 1);
  }
}