package com.github.binarywang.wxvehicle.service.impl;

import com.github.binarywang.wxvehicle.bean.WxProfitSharingApiData;
import com.github.binarywang.wxvehicle.exception.WxProfitSharingException;
import jodd.util.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * <pre>
 * 微信支付请求实现类，apache httpclient实现.
 * Created by Binary Wang on 2016/7/28.
 * </pre>
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
public class WxProfitSharingServiceApacheHttpImpl extends com.github.binarywang.wxvehicle.service.impl.BaseWxProfitSharingServiceImpl {
  @Override
  public byte[] postForBytes(String url, String requestStr, boolean useKey) throws WxProfitSharingException {
    try {
      HttpClientBuilder httpClientBuilder = createHttpClientBuilder(useKey);
      HttpPost httpPost = this.createHttpPost(url, requestStr);
      try (CloseableHttpClient httpclient = httpClientBuilder.build()) {
        try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
          final byte[] bytes = EntityUtils.toByteArray(response.getEntity());
          final String responseData = Base64.encodeToString(bytes);
          this.log.info("\n【请求地址】：{}\n【请求数据】：{}\n【响应数据(Base64编码后)】：{}", url, requestStr, responseData);
          wxApiData.set(new WxProfitSharingApiData(url, requestStr, responseData, null));
          return bytes;
        }
      } finally {
        httpPost.releaseConnection();
      }
    } catch (Exception e) {
      this.log.error("\n【请求地址】：{}\n【请求数据】：{}\n【异常信息】：{}", url, requestStr, e.getMessage());
      wxApiData.set(new WxProfitSharingApiData(url, requestStr, null, e.getMessage()));
      throw new WxProfitSharingException(e.getMessage(), e);
    }
  }

  @Override
  public String post(String url, String requestStr, boolean useKey) throws WxProfitSharingException {
    try {
      HttpClientBuilder httpClientBuilder = this.createHttpClientBuilder(useKey);
      HttpPost httpPost = this.createHttpPost(url, requestStr);
      try (CloseableHttpClient httpclient = httpClientBuilder.build()) {
        try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
          String responseString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
          this.log.info("\n【请求地址】：{}\n【请求数据】：{}\n【响应数据】：{}", url, requestStr, responseString);
          wxApiData.set(new WxProfitSharingApiData(url, requestStr, responseString, null));
          return responseString;
        }
      } finally {
        httpPost.releaseConnection();
      }
    } catch (Exception e) {
      this.log.error("\n【请求地址】：{}\n【请求数据】：{}\n【异常信息】：{}", url, requestStr, e.getMessage());
      wxApiData.set(new WxProfitSharingApiData(url, requestStr, null, e.getMessage()));
      throw new WxProfitSharingException(e.getMessage(), e);
    }
  }


  private StringEntity createEntry(String requestStr) {
    try {
      return new StringEntity(new String(requestStr.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
    } catch (UnsupportedEncodingException e) {
      //cannot happen
      this.log.error(e.getMessage(),e);
      return null;
    }
  }

  private HttpClientBuilder createHttpClientBuilder(boolean useKey) throws WxProfitSharingException {
    HttpClientBuilder httpClientBuilder = HttpClients.custom();
    if (useKey) {
      this.setKey(httpClientBuilder);
    }

    if (StringUtils.isNotBlank(this.getConfig().getHttpProxyHost())
      && this.getConfig().getHttpProxyPort() > 0) {
      // 使用代理服务器 需要用户认证的代理服务器
      CredentialsProvider provider = new BasicCredentialsProvider();
      provider.setCredentials(
        new AuthScope(this.getConfig().getHttpProxyHost(), this.getConfig().getHttpProxyPort()),
        new UsernamePasswordCredentials(this.getConfig().getHttpProxyUsername(), this.getConfig().getHttpProxyPassword()));
      httpClientBuilder.setDefaultCredentialsProvider(provider);
    }
    return httpClientBuilder;
  }

  private HttpPost createHttpPost(String url, String requestStr) {
    HttpPost httpPost = new HttpPost(url);
    httpPost.setEntity(this.createEntry(requestStr));

    httpPost.setConfig(RequestConfig.custom()
      .setConnectionRequestTimeout(this.getConfig().getHttpConnectionTimeout())
      .setConnectTimeout(this.getConfig().getHttpConnectionTimeout())
      .setSocketTimeout(this.getConfig().getHttpTimeout())
      .build());

    return httpPost;
  }

  private void setKey(HttpClientBuilder httpClientBuilder) throws WxProfitSharingException {
    SSLContext sslContext = this.getConfig().getSslContext();
    if (null == sslContext) {
      sslContext = this.getConfig().initSSLContext();
    }

    SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
      new String[]{"TLSv1"}, null, new DefaultHostnameVerifier());
    httpClientBuilder.setSSLSocketFactory(sslsf);
  }

}