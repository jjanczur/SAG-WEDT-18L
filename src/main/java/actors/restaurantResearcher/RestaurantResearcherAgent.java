package actors.restaurantResearcher;

import actors.menuResearcher.Restaurant;
import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
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

public class RestaurantResearcherAgent extends AbstractLoggingActor {
    //variables
    ArrayList restaurantsListJson;
    ArrayList restaurantsList;
    String apiUserKey = "4972ea7a10293fc07e997364eef03d3d";
    int cityId = 61;
    int restaurantCount = 10;
    int radius = 1500;
    double latitude = 51.490489;
    double longtitude = -0.167910;

    public void onReceive(Object message) {

    }
    public static Props props(){
        return Props.create(RestaurantResearcherAgent.class);
    }
    @Override
    public Receive createReceive() {
        return null;
    }

    public void getRestaurantsData() {
        String sURL = "https://developers.zomato.com/api/v2.1/search?entity_id="+cityId
                +"&entity_type=city&q=lunch&count="+restaurantCount
                +"&lat="+latitude+"&lon="+longtitude+"&radius="+radius+"&sort=rating&order=desc";
        String allRestaurantsJson = new String();

        try {
            URL url = new URL(sURL);
            HttpURLConnection request = (HttpURLConnection)url.openConnection ();
            request.setRequestMethod("GET");
            request.setRequestProperty("user-key", apiUserKey);
            request.connect();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(request.getInputStream()));
            allRestaurantsJson = in.readLine();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject)jsonParser.parse(allRestaurantsJson);
        /*todo: Make conversion from json list restaurant class directly */
        JsonArray jsonArr = jsonObject.getAsJsonArray("restaurants");
        Gson gJson = new Gson();
        restaurantsListJson = gJson.fromJson(jsonArr, ArrayList.class);
        restaurantsList = new ArrayList<Restaurant>();
        for(int i=0;i<restaurantsListJson.size();i++){
            restaurantsList.set(i,gJson.fromJson((String)restaurantsListJson.get(i), Restaurant.class));
        }
    }

}