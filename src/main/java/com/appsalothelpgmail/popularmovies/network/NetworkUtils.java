package com.appsalothelpgmail.popularmovies.network;

import java.io.IOException;


public class NetworkUtils {
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
}
