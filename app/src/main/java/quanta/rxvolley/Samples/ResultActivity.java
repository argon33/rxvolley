package quanta.rxvolley.Samples;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import quanta.rxvolley.core.LooperScheduler;
import quanta.rxvolley.core.Requests;
import quanta.rxvolley.toolbox.RequestFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

public class ResultActivity extends AppCompatActivity {

    public static final String EXTRA_TYPE = "type";
    public static final int TYPE_RX = 1;
    public static final int TYPE_VOLLEY = 2;
    public static final int TYPE_VOLLEY_SINGLE = 3;
    public static final int TYPE_VOLLEY_FIRST = 4;
    public static final int TYPE_VOLLEY_SEQUENCE = 5;
    public static final int TYPE_VOLLEY_ERROR = 6;

    private TextView mState;
    private TextView mContent;
    private TextView mUnsubscribe;
    private Subscription mSubscription = null;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_result);
        mState = (TextView) findViewById(R.id.state);
        mContent = (TextView) findViewById(R.id.content);
        mUnsubscribe = (TextView) findViewById(R.id.unsubscribe);
        mUnsubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSubscription == null) {
                    Toast.makeText(ResultActivity.this, "no subscription", Toast.LENGTH_SHORT).show();
                } else if (mSubscription.isUnsubscribed()) {
                    Toast.makeText(ResultActivity.this, "has unsubscribed", Toast.LENGTH_SHORT).show();
                } else {
                    mSubscription.unsubscribe();
                }
            }
        });

        mState.setText("start");
        int type = getIntent().getIntExtra(EXTRA_TYPE, TYPE_RX);
        switch (type) {
            case TYPE_VOLLEY:
                mSubscription = testVolley();
                break;
            case TYPE_VOLLEY_SINGLE:
                mSubscription = testRxVolley();
                break;
            case TYPE_VOLLEY_FIRST:
                mSubscription = testVolleyFirst();
                break;
            case TYPE_VOLLEY_SEQUENCE:
                mSubscription = testVolleySequence();
                break;
            case TYPE_VOLLEY_ERROR:
                mSubscription = testVolleyError();
                break;
            default: // TYPE_RX
                mSubscription = testRx();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private Subscription testRx() {
        final String[] texts = "Hello Rx I am RxVolley Done".split(" ");

        return Observable.interval(2000, TimeUnit.MILLISECONDS)
                .take(texts.length)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(final Long index) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mState.setText("next " + System.currentTimeMillis());
                                mContent.setText(texts[index.intValue()]);
                            }
                        });
                    }
                }, new Action1<Throwable>() {

                    @Override
                    public void call(final Throwable throwable) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mState.setText("error");
                                mContent.setText(throwable.toString());
                            }
                        });
                    }
                }, new Action0() {

                    @Override
                    public void call() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mState.setText("completed");
                            }
                        });
                    }
                });
    }

    private Subscription testVolley() {
        final String url = "https://github.com";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mState.setText("completed");
                mContent.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mState.setText("error");
                mContent.setText(error.toString());
            }
        });

        queue.add(request);
        return null;
    }

    private Subscription testRxVolley() {
        final String url = "https://github.com";
        return Requests.build(url, RequestFactory.STRING)
                .put(RequestFactory.PARAM_METHOD, Request.Method.GET)
                .submit()
                .subscribe(new Observer<String>() {

                    @Override
                    public void onCompleted() {
                        mState.setText("completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mState.setText("error");
                        mContent.setText(e.toString());
                    }

                    @Override
                    public void onNext(String content) {
                        mState.setText("next " + System.currentTimeMillis());
                        mContent.setText(content);
                    }
                });
    }

    public Subscription testVolleyFirst() {
        return Observable.just("https://github.com",
                "https://github.com/ReactiveX/RxJava",
                "https://github.com/argon33/rxvolley")
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        return Requests.build(s, RequestFactory.STRING)
                                .put(RequestFactory.PARAM_METHOD, Request.Method.GET)
                                .submit();
                    }
                })
                .subscribe(new Observer<String>() {

                    @Override
                    public void onCompleted() {
                        mState.setText("completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mState.setText("error");
                        mContent.setText(e.toString());
                    }

                    @Override
                    public void onNext(String content) {
                        mState.setText("next " + System.currentTimeMillis());
                        mContent.setText(content);
                    }
                });
    }


    private Subscription testVolleySequence() {
        return Observable.just("https://github.com",
                "https://github.com/ReactiveX/RxJava",
                "https://github.com/argon33/rxvolley")
                .concatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {
                        return Requests.build(s, RequestFactory.STRING)
                                .put(RequestFactory.PARAM_METHOD, Request.Method.GET)
                                .submit()
                                .delay(new Random().nextInt(5), TimeUnit.SECONDS);
                    }
                })
                .observeOn(LooperScheduler.main())
                .subscribe(new Observer<String>() {

                    @Override
                    public void onCompleted() {
                        mState.setText("completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mState.setText("error");
                        mContent.setText(e.toString());
                    }

                    @Override
                    public void onNext(String content) {
                        mState.setText("next " + System.currentTimeMillis());
                        mContent.setText(content);
                    }
                });
    }

    private Subscription testVolleyError() {
        return Observable.just("https://github.com",
                "https://github.com/ReactiveX/RxJava",
                "https://github.com/argon33/rxvolley")
                .concatMap(new Func1<String, Observable<JSONObject>>() {
                    @Override
                    public Observable<JSONObject> call(String s) {
                        return Requests.build(s, RequestFactory.JSON_OBJECT)
                                .put(RequestFactory.PARAM_METHOD, Request.Method.GET)
                                .submit()
                                .delay(new Random().nextInt(5), TimeUnit.SECONDS);
                    }
                })
                .observeOn(LooperScheduler.main())
                .subscribe(new Observer<JSONObject>() {

                    @Override
                    public void onCompleted() {
                        mState.setText("completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mState.setText("error");
                        mContent.setText(e.toString());
                    }

                    @Override
                    public void onNext(JSONObject content) {
                        mState.setText("next " + System.currentTimeMillis());
                        mContent.setText(content.toString());
                    }
                });
    }
}
