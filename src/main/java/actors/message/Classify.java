package actors.message;

import actors.restaurantResearcher.RestaurantCollection;
import akka.actor.ActorRef;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class Classify implements Serializable {
    private ActorRef requester;
    private String searchingMenu;
    private RestaurantCollection restaurantC;
    private static final long serialVersionUID = -6662420828480543859L;

    public Classify(ActorRef requester, String searchingMenu, RestaurantCollection restaurantC){
        this.requester = requester;
        this.searchingMenu = searchingMenu;
        this.restaurantC = restaurantC;
    }


}
