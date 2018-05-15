package actors.menuResearcher;

import akka.actor.AbstractActor;
import com.google.gson.Gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MenuResearcherAgent extends AbstractActor {
    //variables
    ArrayList restaurants;

    //methods
    @Override
    public Receive createReceive() {
        return null;
    }

    public void getData() {
        String sURL = "https://developers.zomato.com/api/v2.1/search?entity_id=61&entity_type=city&q=lunch&count=10&lat=%2051.490489&lon=-0.167910&radius=1500&sort=rating&order=desc"; //just a string
        String restaurantsJson;
        URL url = new URL(sURL);

        try {
            HttpURLConnection request = (HttpURLConnection)url.openConnection ();
            request.setRequestMethod("GET");
            request.setRequestProperty("user-key", "4972ea7a10293fc07e997364eef03d3d");
            request.connect();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(request.getInputStream()));
            restaurantsJson = in.readLine();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject)jsonParser.parse(restaurantsJson);

        JsonArray jsonArr = jsonObject.getAsJsonArray("restaurants");
        //jsonArr.
        Gson gJson = new Gson();
        restaurants = gJson.fromJson(jsonArr, ArrayList.class);
    }
}