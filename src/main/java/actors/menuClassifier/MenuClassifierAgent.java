package actors.menuClassifier;

import actors.menuClassifier.classifier.Algorithm;
import actors.menuClassifier.classifier.RestaurantClassifierWrapper;
import actors.message.Classify;
import actors.message.ErrorMessage;
import actors.message.Response;
import actors.restaurantResearcher.CommonRestaurant;
import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuClassifierAgent extends AbstractActor {

    private static final Logger log = Logger.getLogger(MenuClassifierAgent.class);

    public MenuClassifierAgent() {
        log.info("[SUCCESS] Started MenuClassifierAgent! " +getSelf().path());
    }

    // Getting the other actor
    private ActorSelection lunchServerSelection = getContext().actorSelection("akka.tcp://LunchServer@127.0.0.1:5150/user/Server");

    @Override
    public Receive createReceive() {
        ReceiveBuilder rbuilder = ReceiveBuilder.create();

        rbuilder.match(Classify.class, classify -> {
            if (classify.getSearchingMenus() != null && classify.getSearchingMenus().size() > 0) {


                List<CommonRestaurant> restaurants = classifyRestaurants(classify.getRestaurants(), classify.getSearchingMenus());

                lunchServerSelection.tell(new Response(classify.getRequester(), restaurants, classify.getSearchingMenus()), getSelf());
                log.info("Restaurant classification completed. "+getSelf().path());

            } else {
                log.info("The message string does not specify the data source.");
                lunchServerSelection.tell(new ErrorMessage(classify.getRequester(), "The message string does not specify the data source."),getSelf());
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

        return sortedRestaurants;

    }


}