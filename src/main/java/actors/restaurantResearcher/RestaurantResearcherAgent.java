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
    private ArrayList restaurantsList;
    private String zomatoApiUserKey = "4972ea7a10293fc07e997364eef03d3d";
    private String googleApiUserKey = "AIzaSyC46JVz8sjO7cLKFytsG9LjuY11BrQu6_w";
    private String keyword = "lunch";
    private int cityId = 61;
    private int restaurantCount = 20;
    private int radius = 1500;
    private double latitude = 51.490489;
    private double longtitude = -0.167910;
    public ZomatoCollection ZC;
    public GoogleCollection GC;
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
        String zomatoRestaurantsJson = new String();

        try {
            URL url = new URL(sURL);
            HttpURLConnection request = (HttpURLConnection)url.openConnection ();
            request.setRequestMethod("GET");
            request.setRequestProperty("user-key", zomatoApiUserKey);
            request.connect();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(request.getInputStream()));
            zomatoRestaurantsJson = in.readLine();
            in.close();
            System.out.println("[INFO] Zomato API data acquired.");
        } catch (IOException e) {
            System.out.println("[INFO] Zomato API not responding.");
            e.printStackTrace();
        }

        Gson gJson = new Gson();
        ZomatoCollection ZC = gJson.fromJson(zomatoRestaurantsJson, ZomatoCollection.class);
        System.out.println("[INFO] Zomato parsing completed");
    }

    private void getGoogleRestaurantsData() {
        String sURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"+
                "location="+latitude+","+longtitude+"&radius="+radius+"&type=restaurant"+
                "&keyword="+keyword+"&key="+googleApiUserKey;
        String googleRestaurantsJson = new String();

        try {
            URL url = new URL(sURL);
            HttpURLConnection request = (HttpURLConnection)url.openConnection ();
            request.setRequestMethod("GET");
            request.connect();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(request.getInputStream()));
            String tmpLine = "";
            while((tmpLine = in.readLine()) != null) {
                googleRestaurantsJson += tmpLine;
            }
            in.close();
            System.out.println("[INFO] Google Places API data acquired.");
        } catch (IOException e) {
            System.out.println("[INFO] Google API not responding.");
            e.printStackTrace();
        }

        Gson gJson = new Gson();
        GoogleCollection GC = gJson.fromJson(googleRestaurantsJson, GoogleCollection.class);
        System.out.println("[INFO] Google parsing completed");
    }
}