package actors.restaurantResearcher;

import com.google.gson.annotations.SerializedName;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class ZomatoCollection {
    @SerializedName("restaurants")
    public List<Restaurant> restaurants;

    public ZomatoCollection(){
        restaurants = new ArrayList<>();
    }
    public int getRestaurantId(int index){
        if(index > restaurants.size() || index < 0){
            System.out.println("[WARN] Index exceeds the restaurant collection.");
            return 0;
        }
        else
            return parseInt(restaurants.get(index).restaurant.getId());
    }
    public String getRestaurantDailyMenu(int index){
        if(index > restaurants.size() || index < 0){
            System.out.println("[WARN] Index exceeds the restaurant collection.");
            return "";
        }
        else{
            if(restaurants.get(index).dailyMenu != null)
                return restaurants.get(index).dailyMenu;
            else {
                System.out.println("[INFO] Restaurant " +
                        getRestaurantId(index) +
                        " does not have daily menu posted on Zomato");
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
