package actors.message;

import actors.restaurantResearcher.CommonRestaurant;
import akka.actor.ActorRef;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Classify implements Serializable {
    private ActorRef requester;
    private List<String> searchingMenus;
    private List<CommonRestaurant> restaurants;
    private static final long serialVersionUID = -6662420828480543859L;

    public Classify(ActorRef requester, List<String> searchingMenus, List<CommonRestaurant> restaurants) {
        this.requester = requester;
        this.searchingMenus = searchingMenus;
        this.restaurants = restaurants;
    }

}
