package at.mchris.popularmovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Contains all utils for basic network access with the volley framework.
 */
public class NetworkUtils {

    private static volatile NetworkUtils instance;

    private final RequestQueue requestQueue;

    private NetworkUtils(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static NetworkUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (NetworkUtils.class) {
                if (instance == null) {
                    instance = new NetworkUtils(context);
                }
            }
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
         getRequestQueue().add(req);
    }
}
