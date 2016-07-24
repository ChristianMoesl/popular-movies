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

    public String getBaseUrl() {
        return images.baseUrl;
    }

    public String getSecureBaseUrl() {
        return images.secureBaseUrl;
    }

    public List<String> getBackdropSizes() {
        return images.backdropSizes;
    }

    public List<String> getLogoSizes() {
        return images.logoSizes;
    }

    public List<String> getPosterSizes() {
        return images.posterSizes;
    }

    public List<String> getProfileSizes() {
        return images.profileSizes;
    }

    public List<String> getStillSizes() {
        return images.stillSizes;
    }

    public List<String> getChangeKeys() {
        return changeKeys;
    }
}
