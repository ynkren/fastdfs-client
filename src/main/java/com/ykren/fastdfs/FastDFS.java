package com.ykren.fastdfs;

import com.ykren.fastdfs.model.*;
import com.ykren.fastdfs.model.fdfs.FileInfo;
import com.ykren.fastdfs.model.fdfs.MetaData;
import com.ykren.fastdfs.model.fdfs.StorePath;

import java.util.Set;

/**
 * FastDFS文件存储客户端
 *
 * @author ykren
 */
public interface FastDFS {

    /**
     * 获取trackerClient
     *
     * @return
     */
    TrackerClient trackerClient();

    /**
     * 关闭客户端
     */
    void close();

    /**
     * 上传一般文件
     *
     * @param request
     * @return
     */
    StorePath uploadFile(UploadFileRequest request);

    /**
     * 上传从文件
     *
     * @param request
     * @return
     */
    StorePath uploadSlaveFile(UploadSalveFileRequest request);

    /**
     * 获取文件元信息
     *
     * @param request
     * @return
     */
    Set<MetaData> getMetadata(MetaDataInfoRequest request);

    /**
     * 修改文件元信息（覆盖）
     *
     * @param request
     */
    void overwriteMetadata(MetaDataRequest request);

    /**
     * 修改文件元信息（合并）
     *
     * @param request
     */
    void mergeMetadata(MetaDataRequest request);

    /**
     * 查看文件的信息
     *
     * @param request
     * @return
     */
    FileInfo queryFileInfo(FileInfoRequest request);

    /**
     * 删除文件
     *
     * @param request
     */
    void deleteFile(FileInfoRequest request);

    /**
     * 下载文件
     *
     * @param request
     * @return
     */
    <T> T downloadFile(DownloadFileRequest<T> request);

    /**
     * 上传支持断点续传的文件
     *
     * @param request
     * @return
     */
    StorePath uploadAppenderFile(UploadFileRequest request);

    /**
     * 断点续传文件
     *
     * @param request
     */
    void appendFile(AppendFileRequest request);

    /**
     * 修改续传文件的内容
     *
     * @param request
     */
    void modifyFile(ModifyFileRequest request);

    /**
     * 清除续传类型文件的内容
     *
     * @param request
     */
    void truncateFile(TruncateFileRequest request);

    /**
     * appender类型文件改名为普通文件 V6.02及以上版本
     *
     * @param request
     * @return
     */
    StorePath regenerateAppenderFile(RegenerateAppenderFileRequest request);

    /**
     * 初始化分片上传
     *
     * @param request group
     * @return StorePath
     */
    StorePath initMultipartUpload(InitMultipartUploadRequest request);

    /**
     * 上传分片
     *
     * @param request
     */
    void uploadPart(UploadMultipartPartRequest request);

    /**
     * 完成分片上传 V6.02及以上版本 5.x版本调用uploadPart完毕即可认为成功
     *
     * @param request
     */
    StorePath completeMultipartUpload(CompleteMultipartRequest request);

    /**
     * 终止分片上传
     *
     * @param request
     */
    void abortMultipartUpload(AbortMultipartRequest request);

//    List<FilePartInfo> listParts(String groupName, String path);
}
