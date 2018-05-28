package actors.message;


import actors.restaurantResearcher.CommonRestaurant;
import akka.actor.ActorRef;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class Response implements Serializable {

    private static final long serialVersionUID = -4569229744007790163L;

    private ActorRef requester;

    private List<CommonRestaurant> restaurants;

    public Response(ActorRef requester, List<CommonRestaurant> restaurants) {
        this.requester = requester;
        this.restaurants = restaurants;
    }
}
