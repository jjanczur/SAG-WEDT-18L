package actors.menuClassifier;

import actors.menuClassifier.classifier.Algorithm;
import actors.menuClassifier.classifier.RestaurantClassifierWrapper;
import actors.message.Classify;
import actors.message.Response;
import actors.restaurantResearcher.RestaurantCollection;
import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

public class MenuClassifierAgent extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        ReceiveBuilder rbuilder = ReceiveBuilder.create();

        rbuilder.match(Classify.class, classify -> {
            if (classify.getSearchingMenu() != null && classify.getSearchingMenu().length() > 0) {

                RestaurantCollection restaurantC = classifyRestaurants(classify.getRestaurantC(), classify.getSearchingMenu());
                getSender().tell(new Response(classify.getRequester(), restaurantC), getSelf());
                log.info("Restaurant classification completed");

            } else {
                System.out.println("[WARN] The message string does not specify the data source.");
            }

        });

        // DEFAULT
        rbuilder.matchAny(o -> log.info(getSelf().path().toString() + " - unknown message type " + o.getClass().getCanonicalName()));

        return rbuilder.build();
    }

    public static Props props() {
        return Props.create(MenuClassifierAgent.class);
    }


    public RestaurantCollection classifyRestaurants(RestaurantCollection restaurants, String searchingMenu) {

        Algorithm alg = new Algorithm(restaurants, searchingMenu);
        alg.classifyRestaurants();

        RestaurantCollection restaurantCollection = new RestaurantCollection();

        for (RestaurantClassifierWrapper restaurantCW : alg.getRestaurantsCW()) {
            restaurantCollection.restaurants.add(restaurantCW.getRestaurant());
        }

        return restaurantCollection;

    }


}