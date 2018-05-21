package actors.restaurantResearcher;

import com.google.gson.annotations.SerializedName;
import java.net.URL;
import java.util.List;

public class RestaurantCollection {
    @SerializedName("restaurants")
    public List<Restaurant> restaurants;
}
