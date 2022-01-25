package com.ykren.fastdfs;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * 测试用随机字符文件
 *
 * @author tobato
 */
public class RandomTextFile {

    private String text;

    private InputStream inputStream;

    private long fileSize;

    private String fileExtName = "txt";

    public RandomTextFile() {
        this.text = RandomStringUtils.random(30, "762830abdcefghijklmnopqrstuvwxyz0991822-");
        this.fileSize = text.length();
    }

    public RandomTextFile(String text) {
        this.text = text;
        this.fileSize = text.length();
    }

    public String getText() {
        return text;
    }

    public InputStream getInputStream() {
        this.inputStream = new ByteArrayInputStream(text.getBytes());
        return inputStream;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getFileExtName() {
        return fileExtName;
    }

    public void setFileExtName(String fileExtName) {
        this.fileExtName = fileExtName;
    }
}
