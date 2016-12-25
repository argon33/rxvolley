package quanta.rxvolley.core;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;

public final class RequestBuilder<T> {
    static final String TAG = "RequestBuilder";

    private final String url;
    private final Requests.Factory<T> factory;
    private final Map<String, Object> params = new HashMap<String, Object>();

    RequestBuilder(String url, Requests.Factory<T> factory) {
        this.url = url;
        this.factory = factory;
    }

    public RequestBuilder<T> put(String key, Object value) {
        params.put(key, value);
        return this;
    }

    public String getUrl() {
        return url;
    }

    public int getIntParam(String key, int defaultValue) {
        Object v = params.get(key);
        if (v instanceof Integer) {
            return (Integer) v;
        }

        return defaultValue;
    }

    public <F> F getObjParam(String key, F defaultValue) {
        F v = (F) params.get(key);
        return v != null ? v : defaultValue;
    }

    static final class ResponseListener<T> implements Response.Listener<T>, Response.ErrorListener {

        final Subscriber<? super T> subscriber;

        ResponseListener(Subscriber<? super T> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            subscriber.onError(error);
        }

        @Override
        public void onResponse(T response) {
            subscriber.onNext(response);
        }
    }

    static final class SubscriptionListener<T> implements
            RequestQueue.RequestFinishedListener<T>, Subscription {

        final Subscriber<? super T> subscriber;
        final Request<T> request;
        boolean isCompleted;
        boolean isUnsubscribed;

        SubscriptionListener(Subscriber<? super T> subscriber, Request<T> request) {
            this.subscriber = subscriber;
            this.request = request;
        }

        @Override
        public void onRequestFinished(Request<T> request) {
            isCompleted = true;
            subscriber.onCompleted();
            final RequestQueue.RequestFinishedListener<T> listener = this;
            Observable.timer(0, TimeUnit.MILLISECONDS)
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            Requests.queue().removeRequestFinishedListener(listener);
                        }
                    });
        }

        @Override
        public void unsubscribe() {
            isUnsubscribed = true;
            if (isCompleted) {
                request.cancel();
            }
        }

        @Override
        public boolean isUnsubscribed() {
            return isUnsubscribed;
        }
    }


    public Observable<T> submit() {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                ResponseListener<T> l = new ResponseListener(subscriber);
                Request<T> request = factory.create(RequestBuilder.this, l, l);
                SubscriptionListener sl = new SubscriptionListener(subscriber, request);
                subscriber.add(sl);
                RequestQueue queue = Requests.queue();
                queue.addRequestFinishedListener(sl);
                queue.add(request);
            }
        });
    }
}
