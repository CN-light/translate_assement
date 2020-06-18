package com.assessment.translate.utils;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class ParameterUtils {
    private static final Logger log = Logger.getLogger(ParameterUtils.class);

    /**
     * 获得不带"-"的UUID
     *
     * @return 不带"-"的UUID
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 获得带"-"的UUID
     *
     * @return 带"-"的UUID
     */
    public static String getUUIDWithSeparator() {
        return UUID.randomUUID().toString();
    }

    /**
     * localDate转为Date(sql)
     *
     * @param date
     * @return Date
     */
    public static Date localDateToDate(LocalDate date) {
        ZonedDateTime zonedDateTime = date.atStartOfDay(ZoneId.systemDefault());
        return new java.sql.Date(Date.from(zonedDateTime.toInstant()).getTime());
    }

    /**
     * 获得当前时间（UTC ISO8601标准)
     *
     * @return 当前时间
     */
    public static String getISO8601Timestamp() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(new SimpleTimeZone(0, "GMT"));
        return df.format(new java.util.Date(System.currentTimeMillis()));
    }

    /**
     * 构建post请求
     *
     * @param uri
     * @param parameters
     * @return post请求
     * @throws URISyntaxException
     * @throws UnsupportedEncodingException
     */
    public static HttpPost buildPost(String uri, HashMap<String, String> parameters) {
        try {
            HttpPost post = new HttpPost(buildURI(uri));
            List<NameValuePair> parametersList = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                parametersList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            post.setEntity(new UrlEncodedFormEntity(parametersList, "UTF-8"));
            return post;
        } catch (URISyntaxException | UnsupportedEncodingException e) {
            log.error(e.toString());
        }
        return null;
    }

    /**
     * 构建get请求
     *
     * @param uri
     * @param parameters
     * @return get请求
     */
    public static HttpGet buildGet(String uri, HashMap<String, String> parameters) {
        URI u = ParameterUtils.buildURI(uri, parameters);
        return (u == null ? null : new HttpGet(u));
    }

    /**
     * 构建不带参数的URI
     *
     * @param uri
     * @return 不带参数的URI
     * @throws URISyntaxException
     */
    private static URI buildURI(String uri) throws URISyntaxException {
        return new URIBuilder(uri).build();
    }

    /**
     * 构建带参数的URI
     *
     * @param uri
     * @param parameters
     * @return 带参数的URI
     * @throws URISyntaxException
     */
    private static URI buildURI(String uri, HashMap<String, String> parameters) {
        try {
            URIBuilder uriBuilder = new URIBuilder(uri);
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                uriBuilder.setParameter(entry.getKey(), entry.getValue());
            }
            return uriBuilder.build();
        } catch (URISyntaxException e) {
            log.error(e.toString());
        }
        return null;
    }
}
