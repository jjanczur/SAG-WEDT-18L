package actors.message;

import actors.restaurantResearcher.RestaurantCollection;
import akka.actor.ActorRef;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class Response implements Serializable {

    private static final long serialVersionUID = -4569229744007790163L;

    private ActorRef requester;

    private RestaurantCollection restaurantCollection;

    public Response(ActorRef requester, RestaurantCollection restaurantCollection) {
        this.requester = requester;
        this.restaurantCollection = restaurantCollection;
    }
}
