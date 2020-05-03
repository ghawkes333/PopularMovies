package com.appsalothelpgmail.popularmovies.Utils;

public class TMDbValues {
    public final static String API_KEY = "c901c825d26b844b00e2edbd97ffe72d";

    private final static String TMDB_BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    private final static String TMDB_SIZE = "w185";
    public final static String TMDB_IMAGE_URL = TMDB_BASE_IMAGE_URL + TMDB_SIZE;


    public final static String TMDB_BASE_URL = "http://api.themoviedb.org/3/movie/";
    public final static String TMDB_POPULAR = "popular";
    public final static String TMDB_TOP_RATED = "top_rated";
    public final static String TMDB_API_PARAM = "?api_key=";


    public final static String TMDB_RESPONSE_MOVIE_ID = "id";
    public final static String TMDB_RESPONSE_TITLE = "original_title";
    public final static String TMDB_RESPONSE_VOTE_AVERAGE = "vote_average";
    public final static String TMDB_RESPONSE_RELEASE_DATE = "release_date";
    public final static String TMDB_RESPONSE_PLOT = "overview";
    public final static String TMDB_RESPONSE_MOVIE_IMAGE_PATH = "poster_path";
    public final static String TMDB_RESPONSE_RESULTS = "results";
}
