package at.mchris.popularmovies.network.themoviedb3;

import com.android.volley.*;
import com.android.volley.Request;

import org.apache.commons.codec.StringEncoder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by chris_000 on 18.07.2016.
 */
public class JsonRequestBuilder<T> {

    protected String path = "";
    protected Map<String, String> parameters = new HashMap<>();
    protected Response.Listener listener;
    protected Response.ErrorListener errorListener;
    protected Class<T> clazz;

    JsonRequestBuilder() {
    }

    public JsonRequestBuilder<T> setClass(Class<T> clazz) {
        this.clazz = clazz;
        return this;
    }

    public JsonRequestBuilder<T> setResponseListener(Response.Listener listener) {
        this.listener = listener;
        return this;
    }

    public JsonRequestBuilder<T> setErrorListener(Response.ErrorListener errorListener) {
        this.errorListener = errorListener;
        return this;
    }

    public JsonRequestBuilder<T> setPath(String text) {
        path = text;
        return this;
    }

    public JsonRequestBuilder<T> appendToPath(String text) {
        path = path.concat(text);
        return this;
    }

    public JsonRequestBuilder<T> putParameter(String key, String value) {
        parameters.put(key, value);
        return this;
    }

    protected void appendParameters(StringBuilder sb) {
        if (parameters.size() > 0) {
            sb.append('?');
            Iterator<String> keyIt = parameters.keySet().iterator();
            Iterator<String> valueit = parameters.values().iterator();
            sb.append(keyIt.next())
                    .append('=').append(valueit.next());
            while(keyIt.hasNext()) {
                sb.append('&');
                sb.append(keyIt.next());
                sb.append('=');
                sb.append(valueit.next());
            }
        }
    }

    public JsonRequest<T> build() {
        StringBuilder sb = new StringBuilder();
        sb.append(path);
        appendParameters(sb);
        return new JsonRequest<>(Request.Method.GET, sb.toString(), null, listener, errorListener, clazz);
    }
}