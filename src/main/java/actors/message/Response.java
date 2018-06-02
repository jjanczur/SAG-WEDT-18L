package actors.message;


import actors.restaurantResearcher.CommonRestaurant;
import akka.actor.ActorRef;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class Response implements Serializable {

    private static final long serialVersionUID = -4569229744007790163L;

    private ActorRef requester;

    private List<CommonRestaurant> restaurants;

    private List<String> searchingMenus;

    public Response(ActorRef requester, List<CommonRestaurant> restaurants, List<String> searchingMenus) {
        this.requester = requester;
        this.restaurants = restaurants;
        this.searchingMenus = searchingMenus;
    }
}
