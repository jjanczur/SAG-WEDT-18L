package actors.message;

import akka.actor.ActorRef;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Search implements Serializable {
    private static final long serialVersionUID = -6392849246692016285L;
    private int radius; // = 1500;
    private double latitude; // = 51.490489;
    private double longtitude; // = -0.167910;
    private List<String> searchingMenus;
    private ActorRef requester;

    public Search(ActorRef requester, double lng, double lat, int rad, List<String> searchingMenus) {
        this.requester = requester;
        this.radius = rad;
        this.longtitude = lng;
        this.latitude = lat;
        this.searchingMenus = searchingMenus;
    }

}
