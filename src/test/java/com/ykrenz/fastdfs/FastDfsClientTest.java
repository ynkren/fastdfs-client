package com.ykrenz.fastdfs;


import com.ykrenz.fastdfs.event.UploadProgressListener;
import com.ykrenz.fastdfs.model.DownloadFileRequest;
import com.ykrenz.fastdfs.model.MetaDataRequest;
import com.ykrenz.fastdfs.model.ThumbImage;
import com.ykrenz.fastdfs.model.UploadFileRequest;
import com.ykrenz.fastdfs.model.UploadImageRequest;
import com.ykrenz.fastdfs.model.UploadSalveFileRequest;
import com.ykrenz.fastdfs.model.fdfs.ImageStorePath;
import com.ykrenz.fastdfs.model.fdfs.MetaData;
import com.ykrenz.fastdfs.model.fdfs.StorePath;
import com.ykrenz.fastdfs.model.proto.storage.DownloadByteArray;
import com.ykrenz.fastdfs.model.proto.storage.DownloadFileWriter;
import com.ykrenz.fastdfs.model.proto.storage.DownloadOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * 文件基础操作测试演示
 *
 * @author tobato
 */
public class FastDfsClientTest extends BaseClientTest {

    protected static Logger LOGGER = LoggerFactory.getLogger(FastDfsClientTest.class);

    /**
     * 基本文件上传操作测试
     *
     * @throws IOException
     */
    @Test
    public void uploadFile() throws IOException {
        LOGGER.debug("##上传文件..##");
        RandomTextFile file = new RandomTextFile();
//        File file = new File(testFilePath);
//        FileInputStream stream = new FileInputStream(file);
        UploadFileRequest fileRequest = UploadFileRequest.builder()
                .stream(file.getInputStream(), file.getFileSize(), file.getFileExtName())
//                .stream(stream, file.length(), "exe")
                .metaData("key1", "value1")
                .metaData("key2", "value2")
                .listener(new UploadProgressListener() {
                    @Override
                    public void start() {
                        LOGGER.debug("开始上传...文件总大小={}", totalBytes);
                    }

                    @Override
                    public void uploading() {
                        LOGGER.debug("上传中 上传进度为" + percent());
                    }

                    @Override
                    public void completed() {
                        LOGGER.debug("上传完成...");
                    }

                    @Override
                    public void failed() {
                        LOGGER.debug("上传失败...已经上传的字节数={}", bytesWritten);
                    }
                })
                .build();
        StorePath storePath = fastDFS.uploadFile(fileRequest);
        assertNotNull(storePath);
        LOGGER.info("上传文件 result={}", storePath);
        LOGGER.info("上传文件 webPath={}", storePath.getWebPath());
        LOGGER.info("上传文件 downLoadPath={}", storePath.getDownLoadPath("1.txt"));
        LOGGER.info("上传文件 downLoadPath2={}", storePath.getDownLoadPath("name", "1.txt"));
        delete(storePath);
        assertNull(queryFile(storePath));
    }

    @Test
    public void uploadSalveFile() throws IOException {
        LOGGER.debug("##上传文件..##");
        RandomTextFile file = new RandomTextFile();
//        File file = new File(testFilePath);
        UploadFileRequest fileRequest = UploadFileRequest.builder()
                .stream(file.getInputStream(), file.getFileSize(), file.getFileExtName())
//                .file(file)
                .metaData("key1", "value1")
                .build();
        StorePath storePath = fastDFS.uploadFile(fileRequest);

        UploadSalveFileRequest salveFileRequest = UploadSalveFileRequest.builder()
                .masterPath(storePath.getPath())
                .stream(file.getInputStream(), file.getFileSize(), file.getFileExtName())
//                .file(file)
                .prefix("aaa")
                .metaData("salvekey", "salvevalue")
                .build();
        StorePath slaveFile = fastDFS.uploadSlaveFile(salveFileRequest);
        assertNotNull(storePath);
        assertNotNull(slaveFile);
        LOGGER.info("上传文件 result={} slaveFile={}", storePath, slaveFile);
        LOGGER.info("上传文件 webPath={} webPath={}", storePath.getWebPath(), slaveFile.getWebPath());
        LOGGER.info("上传文件 downLoadPath={} downLoadPath={}", storePath.getDownLoadPath("1.txt"), slaveFile.getDownLoadPath("1.txt"));
        LOGGER.info("上传文件 downLoadPath2={} downLoadPath2={}",
                storePath.getDownLoadPath("name", "1.txt"),
                slaveFile.getDownLoadPath("name", "1.txt"));
        delete(storePath);
        delete(slaveFile);
        assertNull(queryFile(storePath));
        assertNull(queryFile(slaveFile));
    }

    @Test
    public void deleteFileInfo() throws IOException {
        StorePath storePath = uploadRandomFile();
        assertNotNull(storePath);
        delete(storePath);
        delete(storePath);
        assertNull(queryFile(storePath));
    }

    @Test
    public void downLoadTest() {
        StorePath storePath = uploadRandomFile();
        DownloadFileRequest request = DownloadFileRequest.builder()
                .groupName(storePath.getGroup())
                .path(storePath.getPath())
//                .fileSize(2)
                .build();
        fastDFS.downloadFile(request, new DownloadFileWriter("tmp/tmp1.txt"));
        byte[] bytes = fastDFS.downloadFile(request, new DownloadByteArray());
        try {
            LOGGER.info(IOUtils.toString(bytes, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (OutputStream ous = FileUtils.openOutputStream(new File("tmp/test.txt"))) {
            fastDFS.downloadFile(request, new DownloadOutputStream(ous));
        } catch (IOException e) {
            e.printStackTrace();
        }
        delete(storePath);
    }

    @Test
    public void uploadImageTest() throws IOException {
        File file = getFile();
        Set<MetaData> metaData = new HashSet<>();
        metaData.add(new MetaData("a", "a"));
        UploadImageRequest request = UploadImageRequest.builder()
                .file(file)
                .thumbImage(new ThumbImage(150, 150))
                .build();
        ImageStorePath imageStorePath = fastDFS.uploadImage(request);
        LOGGER.info("img={}", imageStorePath.getImg());
        LOGGER.info("thumbs={}", imageStorePath.getThumbs());
        delete(imageStorePath.getImg());
        for (StorePath path : imageStorePath.getThumbs()) {
            delete(path);
        }
    }

    @Test
    public void createThumbImageTest() throws IOException {
        File file = getFile();
        Set<MetaData> metaData = new HashSet<>();
        metaData.add(new MetaData("a", "a"));
        UploadImageRequest request = UploadImageRequest.builder()
                .file(file)
                .thumbImage(new ThumbImage(150, 150), metaData)
                .build();
        StorePath thumbImage = fastDFS.createThumbImage(request);
        LOGGER.info("thumbImage={}", thumbImage);
        delete(thumbImage);

        UploadImageRequest request2 = UploadImageRequest.builder()
                .file(file)
                .thumbImage(new ThumbImage(150, 150), metaData)
                .thumbImage(new ThumbImage(100, 100), metaData)
                .thumbImage(new ThumbImage(0.5), metaData)
                .build();
        List<StorePath> thumbImages = fastDFS.createThumbImages(request2);
        LOGGER.info("thumbImages={}", thumbImages);
        for (StorePath path : thumbImages) {
            delete(path);
        }
    }


    @Test
    public void uploadMetadata() throws IOException {
        LOGGER.debug("##上传文件..##");
        RandomTextFile file = new RandomTextFile();
        UploadFileRequest fileRequest = UploadFileRequest.builder()
                .stream(file.getInputStream(), file.getFileSize(), file.getFileExtName())
                .metaData("key1", "value1")
                .metaData("key2", "value2")
                .build();
        StorePath storePath = fastDFS.uploadFile(fileRequest);

        UploadSalveFileRequest salveFileRequest = UploadSalveFileRequest.builder()
                .masterPath(storePath.getPath())
                .stream(file.getInputStream(), file.getFileSize(), file.getFileExtName())
                .prefix("aaa")
                .metaData("salvekey1", "salvevalue1")
                .metaData("salvekey2", "salvevalue2")
                .build();
        StorePath slaveFile = fastDFS.uploadSlaveFile(salveFileRequest);
        assertNotNull(storePath);
        assertNotNull(slaveFile);

        Set<MetaData> metaData = getMetaData(storePath);
        assertEquals(2, metaData.size());
        assertTrue(metaData.contains(new MetaData("key1", "value1")));
        assertTrue(metaData.contains(new MetaData("key2", "value2")));

        Set<MetaData> metaData2 = getMetaData(slaveFile);
        assertEquals(2, metaData2.size());
        assertTrue(metaData2.contains(new MetaData("salvekey1", "salvevalue1")));
        assertTrue(metaData2.contains(new MetaData("salvekey2", "salvevalue2")));

        MetaDataRequest metaDataRequest = MetaDataRequest.builder()
                .metaData("key1", "newvalue1")
                .metaData("key2", "newvalue2")
                .groupName(storePath.getGroup())
                .path(storePath.getPath())
                .build();
        fastDFS.mergeMetadata(metaDataRequest);

        Set<MetaData> newMeta = getMetaData(storePath);
        assertEquals(2, newMeta.size());
        assertTrue(newMeta.contains(new MetaData("key1", "newvalue1")));
        assertTrue(newMeta.contains(new MetaData("key2", "newvalue2")));


        MetaDataRequest metaDataRequesto = MetaDataRequest.builder()
                .metaData("keyo", "valueo")
                .groupName(storePath.getGroup())
                .path(storePath.getPath())
                .build();
        fastDFS.overwriteMetadata(metaDataRequesto);

        Set<MetaData> oMeta = getMetaData(storePath);
        assertEquals(1, oMeta.size());
        assertTrue(oMeta.contains(new MetaData("keyo", "valueo")));


        MetaDataRequest metaDataRequesto2 = MetaDataRequest.builder()
                .metaData("skeyo", "svalueo")
                .groupName(slaveFile.getGroup())
                .path(slaveFile.getPath())
                .build();
        fastDFS.overwriteMetadata(metaDataRequesto2);

        Set<MetaData> soMeta = getMetaData(slaveFile);
        assertEquals(1, soMeta.size());
        assertTrue(soMeta.contains(new MetaData("skeyo", "svalueo")));


        delete(storePath);
        delete(slaveFile);
        assertNull(queryFile(storePath));
        assertNull(queryFile(slaveFile));
    }
}
