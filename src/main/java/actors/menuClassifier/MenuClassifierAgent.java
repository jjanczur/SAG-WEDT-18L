package actors.menuClassifier;

import actors.menuClassifier.classifier.Algorithm;
import actors.menuClassifier.classifier.RestaurantClassifierWrapper;
import actors.message.Classify;
import actors.message.Response;
import actors.restaurantResearcher.CommonRestaurant;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuClassifierAgent extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public MenuClassifierAgent() {
        log.info("[SUCCESS] Started MenuClassifierAgent!");
    }

    @Override
    public Receive createReceive() {
        ReceiveBuilder rbuilder = ReceiveBuilder.create();

        rbuilder.match(Classify.class, classify -> {
            if (classify.getSearchingMenus() != null && classify.getSearchingMenus().size() > 0) {


                List<CommonRestaurant> restaurants = classifyRestaurants(classify.getRestaurants(), classify.getSearchingMenus());
                //getSender().tell(new Response(classify.getRequester(), restaurants), getSelf());
                ActorRef as = getSender();
                getSender().tell(new Response(classify.getRequester(), restaurants), getSelf());
                log.info("Restaurant classification completed");

            } else {
                log.info("[WARN] The message string does not specify the data source.");
            }

        });

        // DEFAULT
        rbuilder.matchAny(o -> log.info(getSelf().path().toString() + " - unknown message type " + o.getClass().getCanonicalName()));

        return rbuilder.build();
    }

    public static Props props() {
        return Props.create(MenuClassifierAgent.class);
    }


    public List<CommonRestaurant> classifyRestaurants(List<CommonRestaurant> restaurants, List<String> searchingMenus) {

        Algorithm alg = new Algorithm(restaurants, searchingMenus);
        alg.classifyRestaurants();

        List<CommonRestaurant> sortedRestaurants = new ArrayList<CommonRestaurant>();

        for (Map.Entry<Double, RestaurantClassifierWrapper> entry : alg.getRanking().entrySet()) {
            sortedRestaurants.add(entry.getValue().getRestaurant());
        }
/*
        for (RestaurantClassifierWrapper restaurantCW : alg.getRestaurantsCW()) {
            sortedRestaurants.add(restaurantCW.getRestaurant());
        }
*/

        return sortedRestaurants;

    }


}