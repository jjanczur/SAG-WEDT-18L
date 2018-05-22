
package actors.restaurantResearcher;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Restaurant {

    @SerializedName("restaurant")
    @Expose
    private RestaurantZomato restaurant;

    public RestaurantZomato getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(RestaurantZomato restaurant) {
        this.restaurant = restaurant;
    }

}
