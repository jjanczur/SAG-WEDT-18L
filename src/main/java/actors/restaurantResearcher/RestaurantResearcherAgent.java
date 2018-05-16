package actors.restaurantResearcher;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.google.gson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RestaurantResearcherAgent extends AbstractActor {
    //variables
    ArrayList restaurantsList;
    String apiUserKey = "4972ea7a10293fc07e997364eef03d3d";
    int cityId = 61;
    int restaurantCount = 10;
    int radius = 1500;
    double latitude = 51.490489;
    double longtitude = -0.167910;

    @Override
    public Receive createReceive() {
        ReceiveBuilder rbuilder = ReceiveBuilder.create();

        rbuilder.match(String.class, s -> {
            System.out.println("Received message: " + s.toLowerCase());
            if(s.contains("zomato"))
                getZomatoRestaurantsData();
            else if(s.contains("google"))
                getGoogleRestaurantsData();
            else
                System.out.println("[WARN] The message string does not specify the data source.");
        });

        return rbuilder.build();
    }

    public static Props props(){
        return Props.create(RestaurantResearcherAgent.class);
    }

    private void getZomatoRestaurantsData() {
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
            System.out.println("[INFO] Zomato API not responding.");
            e.printStackTrace();
        }

        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject)jsonParser.parse(allRestaurantsJson);
        /*todo: Make conversion from json list restaurant class directly */
        JsonArray jsonArr = jsonObject.getAsJsonArray("restaurant");

        Gson gJson = new Gson();
        restaurantsList = new ArrayList<Restaurant>();

        for(int i=0; i<jsonArr.size();i++) {
            restaurantsList.add(gJson.fromJson(jsonArr.get(i).toString(), Restaurant.class));
        }
        System.out.println(restaurantsList.get(0));

        System.out.println("[INFO] Zomato data processing finished succesfully.");
    }

    private void getGoogleRestaurantsData() {
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
    }

}