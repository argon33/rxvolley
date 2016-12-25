# rxvolley
Calling volley in a more elegant way by taking the advantage of RxJava.
It depends on the jars of volley and RxJava, instead of the source.

Sample: 
Requests.build(url, RequestFactory.STRING)
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
