package com.assessment.translate.utils.concurrent;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.util.concurrent.*;

public class TranslateThreadPool extends ThreadPoolExecutor {
    private final HttpContext context;
    private final PoolingHttpClientConnectionManager cm;
    private final CloseableHttpClient httpClient;
    private final Logger log = Logger.getLogger(TranslateThreadPool.class);

    public TranslateThreadPool(int corePoolSize, int maxPoolSize, int queueCapacity) {
        super(corePoolSize, maxPoolSize, 0, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(queueCapacity));
        setRejectedExecutionHandler((r, executor) -> log.info(r.toString() + " is discarded"));
        context = new BasicHttpContext();
        cm = new PoolingHttpClientConnectionManager();
        httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();
        log.info("thread pool start");
    }

    @Override
    protected void terminated() {
        log.info("thread pool terminated");
    }

    public <T extends HttpRequestBase> JSONObject translate(T method) {
        try {
            return submit(() -> {
                try (CloseableHttpResponse response = httpClient.execute(method, context)) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        return JSONObject.parseObject(EntityUtils.toString(entity));
                    }
                } catch (Exception e) {
                    log.error(e.toString());
                }
                return null;
                /*最多等待5s，否则抛出超时异常*/
            }).get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error(e.toString());
        }
        return null;
    }

    public JSONObject submitTask(Callable<JSONObject> callable) {
        try {
            /*最多等待5s，否则抛出超时异常*/
            return submit(callable).get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error(e.toString());
        }
        return null;
    }
}
