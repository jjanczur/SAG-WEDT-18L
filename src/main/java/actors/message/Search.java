package actors.message;

import actors.restaurantResearcher.CommonRestaurant;
import actors.restaurantResearcher.GoogleCollection;
import actors.restaurantResearcher.ZomatoCollection;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Search implements Serializable {
    private int radius; // = 1500;
    private double latitude; // = 51.490489;
    private double longtitude; // = -0.167910;

    public Search(double lng, double lat, int rad){
        this.radius = rad;
        this.longtitude = lng;
        this.latitude = lat;
    }

}
