package quanta.rxvolley.core;

import com.android.volley.Cache;
import com.android.volley.Network;

public interface Configuration {
    public Cache getCache();
    public Network getNetwork();
}
