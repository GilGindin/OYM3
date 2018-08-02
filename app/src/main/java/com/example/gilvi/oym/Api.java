package com.example.gilvi.oym;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.StringUtils;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/* this class handles the calls to youtube API. 'calls' mean calling (using) the functions the Youtube API exposes.
   youtube API has several classes & functions you can use to get and work with data from youtube.
   samples: https://developers.google.com/youtube/v3/code_samples/java*/
public class Api {

    // every call to youtube API need to contain an identifier to identify who make the call, this field will hold that identifier
    public static GoogleAccountCredential mCredential;
    private static int selectedTop;
    private static String[] db;
    private static String genre;

    /* this function starts the whole process of calling the API.
     it create a new MakeRequestTask, that contain all the data of our API call (which function to call,
     what to do with the videos data we get etc. immediately after creating the object it calls its
     'execute' function. that fires that request to youtube API with the parameters stored in te object*/
    public static void callApi(ResponseListener listener, int numSelected, String selectedGenre) {
        if (selectedGenre.equals("Jazz")) {
            db = Consts.TOP_100_JAZZ_ARTISTS;
        }
        else if (selectedGenre.equals("Global Combined")) {
            db = Consts.TOP_5000_COMBINED_ARTISTS;
        }
        else {
            db = Consts.TOP_ARTISTS;
        }
        genre = selectedGenre;
        selectedTop = numSelected;
        new MakeRequestTask(mCredential, listener).execute();
    }

    /**
     * when getting data from youtube, we send and receive data over the internet.
     * when doing so we must use what is called 'asynchronous task'.this class is designed to perform stuff like internet calls
     * without blocking the UI.
     * here's the asynchronous task that handles the YouTube Data API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    private static class MakeRequestTask extends AsyncTask<Void, Void, List<Video>> {
        private com.google.api.services.youtube.YouTube mService = null;
        private Exception mLastError = null;
        ResponseListener responseListener;

        MakeRequestTask(GoogleAccountCredential credential, ResponseListener listener) {
            this.responseListener = listener;
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.youtube.YouTube.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("OYM")
                    .build();
        }

        /**
         * Background task to call YouTube Data API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<Video> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch data from youtube by keyword
         * the example here: https://developers.google.com/youtube/v3/code_samples/java
         **/
        private List<Video> getDataFromApi() throws IOException {
            List<Video> videos = new ArrayList<Video>();
            YouTube.Search.List searchList = mService.search().list("id,snippet");
            String top = "";
            String[] selectedArtists = Arrays.copyOfRange(db, selectedTop, Math.min(selectedTop + 10, db.length));
            String[] formatted = new String[10];
            for (int i=0; i<10; i++) {
                formatted[i] = "\"" + selectedArtists[i] + "\"";
            }
            top = TextUtils.join("|", formatted);
            if (genre == "Jazz") {
                top += " official -cover -live -transcription";
            }
            else {
                top += " \"official video\" -cover -live -remix -karaoke -mix -parody -teaser -paparazzi -trailer -lyrics -reaction -zumba";
            }
            searchList.setQ(top); // the setQ params is the search term you look on youtube
            searchList.setType("video");
            searchList.setRelevanceLanguage("en");
            searchList.setTopicId(genre == "Jazz" ? "/m/03_d0" : "/m/064t9"); // see https://developers.google.com/youtube/v3/docs/search/list - search for topicId
            searchList.setOrder("relevance");
//            DateTime dt = new DateTime("2018-01-05T00:00:00Z");
//            searchList.setPublishedAfter(dt);
            searchList.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
            searchList.setMaxResults(50L);

            SearchListResponse response =  searchList.execute();
            List<SearchResult> items = response.getItems();
            if (items != null) {
                videos = this.itemsToVideos(items);
            }

            return videos;
        }

        private List<Video> itemsToVideos(List<SearchResult> items) {
            List<Video> videos = new ArrayList<Video>();
            for (SearchResult item : items) {
                SearchResultSnippet snippet  = item.getSnippet();
                Video v = new Video(snippet.getTitle(), snippet.getTitle(), snippet.getThumbnails().getDefault().getUrl(), item.getId().getVideoId());
                videos.add(v);
            }
            return videos;
        }


        @Override
        protected void onPreExecute() {

        }

        protected void onPostExecute(List<Video> output) {
            this.responseListener.onFinished(output);
        }

        @Override
        protected void onCancelled() {
            if (mLastError != null) {
                //alert the error
                return;
            }
        }
    }
}
