package actors.restaurantResearcher;

import com.google.gson.annotations.SerializedName;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ZomatoCollection {
    @SerializedName("restaurants")
    public List<Restaurant> restaurants;

    public ZomatoCollection(){
        restaurants = new ArrayList<>();
    }
}
