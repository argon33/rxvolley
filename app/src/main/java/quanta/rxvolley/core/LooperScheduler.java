package quanta.rxvolley.core;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.concurrent.TimeUnit;

import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

public final class LooperScheduler extends Scheduler {
    static final String TAG = "LooperScheduler";

    static final class LooperAction implements Runnable, Subscription {
        private final Action0 mAction;
        private final Handler mHandler;
        private boolean mUnsubscribed = false;

        LooperAction(Action0 action, Handler handler) {
            mAction = action;
            mHandler = handler;
        }

        @Override
        public void run() {
            if (!mUnsubscribed) {
                mAction.call();
            }
        }

        @Override
        public void unsubscribe() {
            mUnsubscribed = true;
            mHandler.removeCallbacks(this);
        }

        @Override
        public boolean isUnsubscribed() {
            return mUnsubscribed;
        }
    }



    static final class LooperWorker extends Worker {
        private final Handler mHandler;
        private boolean mUnsubscribed = false;

        LooperWorker(Looper looper) {
            mHandler = new Handler(looper);
        }

        @Override
        public Subscription schedule(Action0 action) {
            return schedule(action, 0, TimeUnit.MILLISECONDS);
        }

        @Override
        public Subscription schedule(Action0 action, long delayTime, TimeUnit unit) {
            if (mUnsubscribed) {
                return Subscriptions.unsubscribed();
            }

            LooperAction la = new LooperAction(action, mHandler);
            Message msg = Message.obtain(mHandler, la);
            msg.obj = this;
            mHandler.sendMessageDelayed(msg, unit.toMillis(delayTime));

            if (mUnsubscribed) {
                la.unsubscribe();
            }

            return la;
        }

        @Override
        public void unsubscribe() {
            mUnsubscribed = true;
            mHandler.removeCallbacksAndMessages(this);
        }

        @Override
        public boolean isUnsubscribed() {
            return mUnsubscribed;
        }
    }

    private final Looper mLooper;
    private LooperScheduler(Looper looper) {
        mLooper = looper;
    }

    @Override
    public Worker createWorker() {
        return new LooperWorker(mLooper);
    }

    public static final Scheduler newScheduler(Looper looper) {
        return new LooperScheduler(looper);
    }

    public static final Scheduler main() {
        return new LooperScheduler(Looper.getMainLooper());
    }

    public static final Scheduler my() {
        return new LooperScheduler(Looper.myLooper());
    }
}
