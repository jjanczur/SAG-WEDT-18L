package actors.client;

import actors.message.Response;
import actors.restaurantResearcher.CommonRestaurant;
import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

public class ClientActor extends AbstractActor {

    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    // Getting the other actor
    private ActorSelection selection = getContext().actorSelection("akka.tcp://LunchServer@127.0.0.1:5150/user/Server");

    public Receive createReceive() {
        ReceiveBuilder rbuilder = ReceiveBuilder.create();


        rbuilder.match(String.class, searchingData -> {
            if (searchingData.equals("test")) {
                selection.tell("test", getSelf());
            } else {
                selection.tell(searchingData, getSelf());
            }
        });


        rbuilder.match(Response.class, response -> {
            if (response.getRestaurants() != null && response.getRestaurants().size() > 0) {
                log.info("Server resived message from: " + getSender());

                log.info("\tSearching menus: ");
                for (String searchingMenu : response.getSearchingMenus()) {
                    log.info("\t\t" + searchingMenu);
                }

                log.info("\tFound restaurants: ");
                for (CommonRestaurant restaurant : response.getRestaurants()) {
                    log.info("\t\t" + restaurant.getName());
                    log.debug(restaurant.toString());
                }

            }

        });

        rbuilder.match(Error.class, error -> {
            log.info("ERROR");
            log.info("Komunikat bledu: " + error.getMessage());
        });

        // DEFAULT
        rbuilder.matchAny(o -> log.info(getSelf().path().toString() + " - unknown message type " + o.getClass().getCanonicalName()));

        return rbuilder.build();
    }
}
