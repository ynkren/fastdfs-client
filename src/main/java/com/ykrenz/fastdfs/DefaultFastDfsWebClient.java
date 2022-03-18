package com.ykrenz.fastdfs;

import com.ykrenz.fastdfs.config.HttpConfiguration;
import com.ykrenz.fastdfs.model.FastDfsWeb;
import com.ykrenz.fastdfs.model.fdfs.WebServerLocator;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author ykren
 * @date 2022/3/15
 */
public class DefaultFastDfsWebClient implements FastDfsWebClient {

    private final HttpConfiguration configuration;

    /**
     * web url
     */
    private final WebServerLocator webServerLocator;
    /**
     * web路径是否包含Group
     * 关联mod_fastdfs.conf url_have_group_name
     */
    private final boolean urlHaveGroup;
    /**
     * 是否开启http防盗链
     * 关联http.config http.anti_steal.check_token
     */
    private final boolean httpAntiStealToken;
    /**
     * http防盗链 secretKey
     * 关联http.config http.anti_steal.secret_key
     */
    private final String secretKey;
    /**
     * 字符集
     */
    private final Charset secretKeyCharset;

    public DefaultFastDfsWebClient(HttpConfiguration configuration) {
        this.configuration = configuration;
        this.webServerLocator = new WebServerLocator(configuration.getWebServers());
        this.urlHaveGroup = configuration.isUrlHaveGroup();
        this.httpAntiStealToken = configuration.isHttpAntiStealToken();
        this.secretKey = configuration.getSecretKey();

        if (configuration.getSecretKeyCharset() != null) {
            this.secretKeyCharset = Charset.forName(configuration.getSecretKeyCharset());
        } else {
            secretKeyCharset = StandardCharsets.UTF_8;
        }
    }

    public HttpConfiguration getConfiguration() {
        return configuration;
    }

    public WebServerLocator getWebServerLocator() {
        return webServerLocator;
    }

    @Override
    public String accessUrl(String groupName, String path) {
        return FastDfsWeb.builder(path,
                webServerLocator.getWebUrl())
                .haveGroupName(urlHaveGroup)
                .groupName(groupName)
                .httpAntiStealToken(httpAntiStealToken)
                .secretKey(secretKey)
                .secretKeyCharset(secretKeyCharset)
                .build().accessUrl();
    }

    @Override
    public String downLoadUrl(String groupName, String path, String downLoadName) {
        return FastDfsWeb.builder(path,
                webServerLocator.getWebUrl())
                .haveGroupName(urlHaveGroup)
                .groupName(groupName)
                .httpAntiStealToken(httpAntiStealToken)
                .secretKey(secretKey)
                .secretKeyCharset(secretKeyCharset)
                .build().downLoadUrl(downLoadName);
    }

    @Override
    public String downLoadUrl(String groupName, String path, String urlArgName, String downLoadName) {
        return FastDfsWeb.builder(path,
                webServerLocator.getWebUrl())
                .haveGroupName(urlHaveGroup)
                .groupName(groupName)
                .httpAntiStealToken(httpAntiStealToken)
                .secretKey(secretKey)
                .secretKeyCharset(secretKeyCharset)
                .build().downLoadUrl(urlArgName, downLoadName);
    }

}
