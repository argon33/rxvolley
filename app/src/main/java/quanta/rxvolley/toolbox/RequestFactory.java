package quanta.rxvolley.toolbox;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import quanta.rxvolley.core.RequestBuilder;
import quanta.rxvolley.core.Requests;

public final class RequestFactory {

    /** int */
    public static final String PARAM_METHOD = "method";


    public static final Requests.Factory<String> STRING = new Requests.Factory<String>() {

        @Override
        public Request<String> create(RequestBuilder<String> builder, Response.Listener<String> l, Response.ErrorListener el) {
            int method = builder.getIntParam(PARAM_METHOD, Request.Method.GET);
            return new StringRequest(method, builder.getUrl(), l, el);
        }
    };

    /** int */
    public static final String BITMAP_WIDTH = "width";
    /** int */
    public static final String BITMAP_HEIGHT = "height";
    /** Bitmap.Config */
    public static final String BITMAP_CONFIG = "config";
    /** ImageView.ScaleType */
    public static final String BITMAP_SCALE_TYPE = "scale_type";
    public static final Requests.Factory<Bitmap> BITMAP = new Requests.Factory<Bitmap>() {

        @Override
        public Request<Bitmap> create(RequestBuilder<Bitmap> builder, Response.Listener<Bitmap> l, Response.ErrorListener el) {
            int maxWidth = builder.getIntParam(BITMAP_WIDTH, 0);
            int maxHeight = builder.getIntParam(BITMAP_HEIGHT, 0);
            Bitmap.Config config = builder.getObjParam(BITMAP_CONFIG, null);
            ImageView.ScaleType scaleType = builder.getObjParam(BITMAP_SCALE_TYPE, null);

            return new ImageRequest(builder.getUrl(), l, maxWidth, maxHeight, scaleType, config, el);
        }
    };

    /** JSONObject */
    public static final String JSON_OBJECT_BODY = "json_object_body";

    public static final Requests.Factory<JSONObject> JSON_OBJECT = new Requests.Factory<JSONObject>() {
        @Override
        public Request<JSONObject> create(RequestBuilder<JSONObject> builder, Response.Listener<JSONObject> l, Response.ErrorListener el) {
            int method = builder.getIntParam(PARAM_METHOD, Request.Method.GET);
            JSONObject body = builder.getObjParam(JSON_OBJECT_BODY, null);

            return new JsonObjectRequest(method, builder.getUrl(), body, l, el);
        }
    };


    /** JSONArray */
    public static final String JSON_ARRAY_BODY = "json_array_body";
    public static final Requests.Factory<JSONArray> JSON_ARRAY = new Requests.Factory<JSONArray>() {
        @Override
        public Request<JSONArray> create(RequestBuilder<JSONArray> builder, Response.Listener<JSONArray> l, Response.ErrorListener el) {
            int method = builder.getIntParam(PARAM_METHOD, Request.Method.GET);
            JSONArray body = builder.getObjParam(JSON_ARRAY_BODY, null);

            return new JsonArrayRequest(method, builder.getUrl(), body, l, el);
        }
    };

}
