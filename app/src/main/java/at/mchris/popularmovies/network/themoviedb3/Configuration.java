package at.mchris.popularmovies.network.themoviedb3;

import java.util.List;

/**
 * Simple parcelable POD object, which represents "the movie db configuration".
 */
public class Configuration {

    private static class Images {
        public String baseUrl;
        public String secureBaseUrl;
        public List<String> backdropSizes;
        public List<String> logoSizes;
        public List<String> posterSizes;
        public List<String> profileSizes;
        public List<String> stillSizes;
    }

    private Images images;
    private List<String> changeKeys;

    public String getBaseUrl() { return images.baseUrl; }
    public String getSecureBaseUrl() { return images.secureBaseUrl; }
    public List<String> getPosterSizes() { return images.posterSizes; }
}
