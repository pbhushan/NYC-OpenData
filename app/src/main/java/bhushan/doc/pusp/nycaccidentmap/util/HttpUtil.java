package bhushan.doc.pusp.nycaccidentmap.util;

import android.content.Context;
import android.net.http.HttpResponseCache;
import android.util.Log;

import java.io.File;

/**
 * Created by PBhushan on 2/28/2015.
 * Util class for handling global HTTP configuration.
 */
public class HttpUtil {

    public static void enableHttpResponseCache(Context context) {
        try {
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            File cacheDir = context.getExternalCacheDir();
            File httpCacheDir = new File(cacheDir, "http");
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (Exception httpResponseCacheNotAvailable) {
            Log.w("HttpUtil", "HTTP response cache is unavailable.", httpResponseCacheNotAvailable);

        }
    }

    public static void flushCacheToDisk() {
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
    }


}
