package quanta.rxvolley.core;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

import java.io.File;

public final class Requests {
    static final String TAG = "Requests";

    public static interface Factory<T> {
        public Request<T> create(RequestBuilder<T> builder, Response.Listener<T> l, Response.ErrorListener el);
    }

    public static <T> RequestBuilder<T> build(String url, Factory<T> factory) {
        return new RequestBuilder<T>(url, factory);
    }

    public static synchronized void setup(Configuration config) {
        if (QUEUE != null) {
            throw new IllegalStateException("config must be setup before initializing queue");
        }

        CONFIG = config;
    }

    public static synchronized RequestQueue queue() {
        if (QUEUE == null) {
            if (CONFIG == null) {
                CONFIG = new DefaultConfiguration();
            }

            QUEUE = new RequestQueue(CONFIG.getCache(), CONFIG.getNetwork());
            QUEUE.start();
        }

        return QUEUE;
    }

    private static RequestQueue QUEUE = null;
    private static Configuration CONFIG = null;
    static final class DefaultConfiguration implements Configuration {

        @Override
        public Cache getCache() {
            return new DiskBasedCache(new File("/sdcard/temp/"));
        }

        @Override
        public Network getNetwork() {
            return new BasicNetwork(new HurlStack());
        }
    }
}
