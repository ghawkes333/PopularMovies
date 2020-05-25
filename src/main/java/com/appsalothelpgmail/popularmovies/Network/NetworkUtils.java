package com.appsalothelpgmail.popularmovies.Network;

import org.json.JSONObject;

import java.io.IOException;


public class NetworkUtils {
    private final static String TAG = NetworkUtils.class.getSimpleName();
    public static JSONObject OBJECT = null;

    //Taken from https://stackoverflow.com/a/27312494
    //May 5, 2020
    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException | InterruptedException e)          { e.printStackTrace(); }

        return false;
    }


//    public static void callURL(Context context, int id, String param){
//        final JSONObject[] objects = new JSONObject[]{null};
//        String url = url();
////        if(id == -1) {
////            url = TMDbValues.TMDB_BASE_URL + param + TMDbValues.TMDB_API_PARAM + TMDbValues.API_KEY;
////        } else {
////            url = TMDbValues.TMDB_BASE_URL + id + param + TMDbValues.TMDB_API_PARAM + TMDbValues.API_KEY;
////        }
//        if(NetworkUtils.isOnline()) {
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(JsonObjectRequest.Method.GET, url, null, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    OBJECT = response;
////                    objects[0] = response;
//                }
//
//            }, error -> {
//                error.printStackTrace();
//                Log.d(TAG, error.networkResponse.toString());
//            });
//
//            RequestQueue requestQueue = Volley.newRequestQueue(context);
//            requestQueue.add(jsonObjectRequest);
//        } else {
//            Log.w(TAG, "No internet");
//        }
//
////        return objects[0];
//    }
}
