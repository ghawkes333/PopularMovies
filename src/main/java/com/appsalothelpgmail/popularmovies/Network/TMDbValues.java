package com.appsalothelpgmail.popularmovies.Network;


import com.appsalothelpgmail.popularmovies.BuildConfig;

public class TMDbValues {
    public final static String API_KEY = BuildConfig.API_KEY;

    private final static String TMDB_BASE_IMAGE_URL = "https://image.tmdb.org/t/p/";
    private final static String TMDB_SIZE = "w185";
    public final static String TMDB_IMAGE_URL = TMDB_BASE_IMAGE_URL + TMDB_SIZE;


    public final static String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/";
    public final static String TMDB_POPULAR = "popular";
    public final static String TMDB_TOP_RATED = "top_rated";
    public final static String TMDB_API_PARAM = "?api_key=";
    public final static String TMDB_VIDEO_PARAM = "/videos";
    public final static String TMDB_REVIEWS_PARAM = "/reviews";



    public final static String TMDB_RESPONSE_MOVIE_ID = "id";
    public final static String TMDB_RESPONSE_TITLE = "original_title";
    public final static String TMDB_RESPONSE_VOTE_AVERAGE = "vote_average";
    public final static String TMDB_RESPONSE_RELEASE_DATE = "release_date";
    public final static String TMDB_RESPONSE_PLOT = "overview";
    public final static String TMDB_RESPONSE_MOVIE_IMAGE_PATH = "poster_path";
    public final static String TMDB_RESPONSE_RESULTS = "results";
    public final static String TMDB_RESPONSE_KEY = "key";
    public final static String TMDB_RESPONSE_SITE = "site";
    public final static String TMDB_RESPONSE_CONTENT = "content";

    public final static String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";
}
