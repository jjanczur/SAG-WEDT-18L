package actors.restaurantResearcher;

import actors.menuClassifier.MenuClassifierAgent;
import actors.message.Classify;
import actors.message.Search;
import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import utils.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RestaurantResearcherAgent extends AbstractActor {
    private static final Logger log = Logger.getLogger(RestaurantResearcherAgent.class);
    private ActorSelection restaurantClassifierSelection = getContext().actorSelection("akka.tcp://ClassifierServer@127.0.0.1:5152/user/Classifier");
    //variables
    private ArrayList restaurantsList;
    private String zomatoApiUserKey = "159b5e120894ad6cae76c3a568b19732";
    private String googleApiUserKey = "AIzaSyC46JVz8sjO7cLKFytsG9LjuY11BrQu6_w";
    //https://developers.facebook.com/tools/explorer/?method=GET&path=me%3Ffields%3Did%2Cname&version=v3.0
    private String facebookApiUserKey = "EAACEdEose0cBADHZCXfsjoEIR4WdKZBaPYGQXvSuIX0TJesjWmxLPxzHqnvsxZBZBIVaygt2KGw7xnzODHZCP3cDaO0ixfjGmhZAZB2kxznrCCZBnphdJkBpErcJZCKSZBqsHIHFZCDJH3oYtZAtG1se4243EVrjhBDdLPWnZA6yjapPifgEFSSpLRH2D4BNa3Rd5XnPC3ZBNxRq0UPAZDZD";

    private String keyword = "lunch";
    private int restaurantCount = 50;
    private int randMenuLines = 150;
    public ZomatoCollection ZC;
    public GoogleCollection GC;
    public FacebookCollection FC;
    public List<CommonRestaurant> CommonRestaurantList;
    public RestaurantResearcherAgent() {
        log.info("[SUCCESS] Started RestaurantResearcherAgent! "+getSelf().path());
    }
    @Override
    public Receive createReceive() {
        ReceiveBuilder rbuilder = ReceiveBuilder.create();

        rbuilder.match(Search.class, s -> {
            if ((s.getLatitude() != 0.0) && (s.getLongtitude() != 0.0)) {
                getZomatoRestaurantsData(s.getLongtitude(), s.getLatitude(), s.getRadius());
                getGoogleRestaurantsData(s.getLongtitude(), s.getLatitude(), s.getRadius());
                unifyRestaurants();

                Classify classify = new Classify();
                classify.setSearchingMenus(s.getSearchingMenus());
                classify.setRestaurants(this.CommonRestaurantList);
                classify.setRequester(s.getRequester());

                restaurantClassifierSelection.tell(classify, getSelf());
            }
        });

        return rbuilder.build();
    }

    public static Props props() {
        return Props.create(RestaurantResearcherAgent.class);
    }

    private void getZomatoRestaurantsData(double latitude, double longtitude, int radius) {
        String sURL = "https://developers.zomato.com/api/v2.1/search?"
                + "&entity_type=city&q=lunch&count=" + restaurantCount
                + "&lat=" + latitude + "&lon=" + longtitude + "&radius=" + radius + "&sort=rating&order=desc";
        String zomatoRestaurantsJson = new String();

        try {
            URL url = new URL(sURL);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.setRequestMethod("GET");
            request.setRequestProperty("user-key", zomatoApiUserKey);
            request.connect();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(request.getInputStream()));
            zomatoRestaurantsJson = in.readLine();
            in.close();
            log.info("[INFO] Zomato API data acquired. "+getSelf().path());
        } catch (IOException e) {
            log.info("[INFO] Zomato API not responding. "+getSelf().path());
            e.printStackTrace();
        }

        Gson gJson = new Gson();
        ZC = gJson.fromJson(zomatoRestaurantsJson, ZomatoCollection.class);
        Util util = new Util();
        for (Restaurant restaurant : ZC.restaurants) {
            if (restaurant.getDailyMenu() == null || restaurant.getDailyMenu().equals("")) {
                restaurant.setDailyMenu(util.createRandomMenu(randMenuLines));
            }

        }

        log.info("[INFO] Zomato parsing completed. "+getSelf().path());
    }

    private void getGoogleRestaurantsData(double latitude, double longtitude, int radius) {
        String sURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                "location=" + latitude + "," + longtitude + "&radius=" + radius + "&type=restaurant" +
                "&keyword=" + keyword + "&key=" + googleApiUserKey;
        String googleRestaurantsJson = new String();

        try {
            URL url = new URL(sURL);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.setRequestMethod("GET");
            request.connect();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(request.getInputStream()));
            String tmpLine = "";
            while ((tmpLine = in.readLine()) != null) {
                googleRestaurantsJson += tmpLine;
            }
            in.close();
            log.info("[INFO] Google Places API data acquired. "+getSelf().path());
        } catch (IOException e) {
            log.info("[INFO] Google API not responding. "+getSelf().path());
            e.printStackTrace();
        }

        Gson gJson = new Gson();
        GC = gJson.fromJson(googleRestaurantsJson, GoogleCollection.class);
        log.info("[INFO] Google parsing completed. "+getSelf().path());
    }


    private void getFacebookRestaurantsData(double latitude, double longtitude, int radius) {

        //        String sURL = "https://graph.facebook.com/v3.0/search?center=51.490489,-0.167910&distance=1500&limit=50/feed&q=restaurant&type=place?access_token=EAACEdEose0cBAKVAmtvZBfYsT1SMDMrX1Yr0e2JFINOF7IaZCLHxtQkYIRRH4DwsvhnbAp5YWZB4L0ul9zLYMmIu3bUOyX4B3w8uOhObjWFAZCYmxzyvZAfBDLqVEaNonudEz5cmXEsddPIYhYa5qoX3RrSXJAU4mFjXsoZCgWsW0cUdjkHnzPQHBBe4rgupHRwUfxtlbnFwZDZD";

        String sURL = "https://graph.facebook.com/v3.0/search?center=" + latitude + ","
                + longtitude + "&distance=" + radius +
                "&limit=50/feed&q=restaurant&type=place" + "&access_token=" + facebookApiUserKey;

        String facebookRestaurantsJson;

        facebookRestaurantsJson = getJsonResponse(sURL);

        Gson gJson = new Gson();
        FC = gJson.fromJson(facebookRestaurantsJson, FacebookCollection.class);
        log.info("Facebook parsing completed. "+getSelf().path());
    }


    private String getJsonResponse(String sURL) {
        String facebookRestaurantsJson = null;
        try {
            URL url = new URL(sURL);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.setRequestMethod("GET");
            request.connect();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(request.getInputStream()));
            String tmpLine;
            while ((tmpLine = in.readLine()) != null) {
                facebookRestaurantsJson += tmpLine;
            }
            in.close();
            log.info(" Facebook Places API data acquired."+getSelf().path());
        } catch (IOException e) {
            log.error("Facebook API not responding. Message: " + e.getMessage(), e);
            e.printStackTrace();
        }
        if (facebookRestaurantsJson == null) {
            log.warn("Facebook response is null");
        }
        return facebookRestaurantsJson;
    }


    private void unifyRestaurants() {
        Util util = new Util();
        CommonRestaurantList = new ArrayList<CommonRestaurant>();
        for (int i = 0; i < ZC.restaurants.size(); i++) {
            CommonRestaurant tmp_zomato = ZC.generateCommon(i);
            CommonRestaurant tmp_google = GC.generateCommon(i);

            //Sprawdzam czy na pewno nie ma pustych menu i dodaje randomy jak jakieś są
            if (tmp_zomato.dailyMenu == null || tmp_zomato.dailyMenu.equals("")) {
                tmp_zomato.setDailyMenu(util.createRandomMenu(randMenuLines));
            }
            if (tmp_google.dailyMenu == null || tmp_google.dailyMenu.equals("")) {
                tmp_google.setDailyMenu(util.createRandomMenu(randMenuLines));
            }

            CommonRestaurantList.add(tmp_zomato);

        }
        log.info("number of restaurants: " + CommonRestaurantList.size());
        log.info("[INFO] Zomato CR list creation completed. "+getSelf().path());
    }
}