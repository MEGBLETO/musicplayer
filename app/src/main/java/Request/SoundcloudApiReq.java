package Request;

import android.util.Log;

import com.android.developer.arslan.advancemusicplayer.config;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import Model.Song;

public class SoundcloudApiReq {

    public interface DeezerInterface {
        void onSuccess(ArrayList<Song> songs);

        void onError(String message);
    }


    private RequestQueue queue;
    private static final String URL = "https://api.deezer.com/search/artist/?q=eminem&index=0&limit=2&output=json";

    private static final String TAG = "APP";

    public SoundcloudApiReq(RequestQueue queue) {
        this.queue = queue;
    }

    public void getSongList(final DeezerInterface callback) {

            final JSONObject request = new JSONObject(Method.GET, URL, null ,new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "onResponse: " + response);

                    ArrayList<Song> songs = new ArrayList<>();
                    /*if (response.length() > 0){*/
                            try {
                                JSONArray jsonArray = response.getJSONArray("datas");

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject songObject = response.getJSONObject(i);

                                    long id = songObject.getLong("id");
                                    String title = songObject.getString("title");
                                    String getSongUrl = songObject.getString("link");
                                    long getNumbAlbum = songObject.getLong("nb_album");
                                    String getSingerPlaylist = songObject.getString("tracklist");
                                    String artist = songObject.getString("name");

                                    Song song = new Song(id, title, artist, getSongUrl, getNumbAlbum, getSingerPlaylist);
                                }

                                } catch(JSONException e){
                                    Log.d(TAG, "onResponse: " + e.getMessage());
                                    callback.onError("une erreur s'est produite");
                                    e.printStackTrace();
                                }

                        callback.onSuccess(songs);
                    }
                    /*else{
                        callback.onError("Aucune chanson trouvee");
                    }*/

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: " + error.getMessage());

                    callback.onError("une erreur s'est produite");
                }
            });

            queue.add(request);
     }

}