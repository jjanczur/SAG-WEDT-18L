package actors.restaurantResearcher;

import com.google.gson.annotations.SerializedName;
import org.apache.log4j.Logger;
import utils.Util;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class ZomatoCollection {
    private static final Logger log = Logger.getLogger(ZomatoCollection.class);

    @SerializedName("restaurants")
    public List<Restaurant> restaurants;
    public Util util = new Util();
    private int randMenuLines = 150;

    public ZomatoCollection(){
        restaurants = new ArrayList<>();
    }
    public int getRestaurantId(int index){
        if(index > restaurants.size() || index < 0){
            log.warn("[WARN] Index exceeds the restaurant collection.");
            return 0;
        }
        else
            return parseInt(restaurants.get(index).restaurant.getId());
    }
    public String getRestaurantDailyMenu(int index){
        if(index > restaurants.size() || index < 0){
            log.warn("[WARN] Index exceeds the restaurant collection.");
            return "";
        }
        else{
            if(restaurants.get(index).dailyMenu != null )
                return restaurants.get(index).dailyMenu;
            else {
                log.info("[INFO] Restaurant " +
                        getRestaurantId(index) +
                        " does not have daily menu posted on Zomato.");
                return "";
            }
        }
    }
    public CommonRestaurant generateCommon(int index){
        return new CommonRestaurant(getRestaurantId(index), "Zomato",
                getRestaurantDailyMenu(index), restaurants.get(index).restaurant.getName().toLowerCase(),
                restaurants.get(index).restaurant.getLocation().getAddress(),
                restaurants.get(index).restaurant.getUrl());
    }
}
