import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jacek on 19.02.2017.
 */
public class BaseConnection {
    public static final String TITLE_URL = "http://www.omdbapi.com/?s=";
    public static final String ID_URL = "http://www.omdbapi.com/?i=";
    public static final String MOVIE_TYPE = "&type=movie";
    public static final String SERIES_TYPE = "&type=series";
    public static final String EPISODE_TYPE = "&type=episode";
    public static final String ALL_TYPE = "";
    public static final int PAGE_NONE = -1;

    public static JSONArray jsonArraySearchResult;
    public static int totalResults;
    public static int pages;
    public static int actualPage = 1;



    public static JSONObject makeRequest(String what, String quest, String type, int page, int season) throws IOException {
        JSONObject jsonSearchResult = null;
        String stringPage, stringSeason;
        if(page == PAGE_NONE) stringPage = "";
        else stringPage = "&page="+page;

        if(season == PAGE_NONE) stringSeason = "";
        else stringSeason = "&season="+season;
        String getURL = what + quest + type + stringPage + stringSeason;
        getURL = getURL.replaceAll(" ","%20");
        URL obj = new URL(getURL);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            jsonSearchResult = new JSONObject(response.toString());
            if(jsonSearchResult.getString("Response").equals("True")) {
                if(jsonSearchResult.has("Search")) {
                    jsonArraySearchResult = jsonSearchResult.getJSONArray("Search");
                    totalResults = Integer.parseInt(jsonSearchResult.getString("totalResults"));
                }
            } else {
                Lo.g(jsonSearchResult.getString("Error"));
                totalResults = 0;
            }
        } else {
            Lo.g("GET Request not worked");
        }
        return jsonSearchResult;
    }

}