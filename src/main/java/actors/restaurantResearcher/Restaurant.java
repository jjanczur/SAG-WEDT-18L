
package actors.restaurantResearcher;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Setter
@Getter
public class Restaurant {

    @SerializedName("restaurant")
    @Expose
    public RestaurantZomato restaurant;

    public String dailyMenu;

    public Restaurant(RestaurantZomato restaurant) {
        this.restaurant = restaurant;
    }

    public Restaurant(String dailyMenu) {
        this.dailyMenu = dailyMenu;
    }

}
