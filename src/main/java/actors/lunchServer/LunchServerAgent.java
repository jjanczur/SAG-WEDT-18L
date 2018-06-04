package actors.lunchServer;

import actors.message.Response;
import actors.message.Search;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import org.apache.log4j.Logger;
import utils.Util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Agent serwera systemu. Zbiera informacje nt. uruchomionych w systemie agentow oraz przyjmuje zapytania od requesterów i rozsyła je do restaurantResearcher'ów.
 */
public class LunchServerAgent extends AbstractActor {


    LinkedList<ActorRef> menuResearchers;
    //private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private static final Logger log = Logger.getLogger(LunchServerAgent.class);

    // Getting the other actor
    private ActorSelection selection = getContext().actorSelection("akka.tcp://ClientServer@127.0.0.1:5555/user/Client");


    /**
     * Generator agenta jako aktora Akki {@link Props}.
     *
     * @return
     */
    public static Props props() {
        return Props.create(LunchServerAgent.class, () -> new LunchServerAgent());
    }

    /**
     * Default constructor.
     */
    public LunchServerAgent() {
        menuResearchers = new LinkedList<>();
        log.info("[SUCCESS] Started lunchServer!");
    }

    /**
     * Prztwarzanie komunikatów:
     *
     * @return Receiver komunikatów.
     */
    public Receive createReceive() {
        ReceiveBuilder rbuilder = ReceiveBuilder.create();


        rbuilder.match(String.class, searchingData -> {
            if (searchingData.equals("test")) {

                /*String searchingMenu1 = "Roasted fish and chips and sauce and cucumber. Coffe, water.";
                String searchingMenu2 = "Lemon-sesame salsa. Whisky, lamb, fresh potato";
                String searchingMenu3 = "Fruit Plate Served With Cottage Cheese";
                String searchingMenu4 = "Broiled Mutton chop omelet with kidneys. White wine";
                String searchingMenu5 = "Charcoal Broiled Filet Mignon. Baked Potato or French Fried Potatoes.";*/
                List<String> searchingMenus = new ArrayList<String>();
                Util tools = new Util();
                for (int z = 0; z < 25; z++) {
                    searchingMenus.add(tools.createRandomMenu(2));
                }
                /*searchingMenus.add(searchingMenu1);
                searchingMenus.add(searchingMenu2);
                searchingMenus.add(searchingMenu3);
                searchingMenus.add(searchingMenu4);
                searchingMenus.add(searchingMenu5);*/


                for (int i = 0; i < searchingMenus.size(); i = i + 5) {
                    Search search = new Search(51.490489, -0.167910, 1500, searchingMenus.subList(i, i + 4));
                    getContext().actorSelection("//Actorsystem/user/ResRouter").tell(search, getSelf());
                }

            } else {
                if (searchingData != null && searchingData.length() > 0) {
                    log.info("Server resived message from: " + getSender());

                    String[] split = searchingData.split(";");

                    if (split.length >= 3) {
                        Double lng = null;
                        Double lat = null;

                        try {
                            lng = Double.valueOf(split[0]);
                            lat = Double.valueOf(split[1]);
                        } catch (NumberFormatException exception) {
                            getSelf().tell(new Error("Niepraaidłowa długość lub szerokość geograficzna"), getSelf());
                            return;
                        }

                        List<String> searchingMenus = new ArrayList<String>();
                        for (int i = 2; i < split.length; i++) {

                            if (!split[i].equals(" ")) {
                                searchingMenus.add(split[i]);
                            }

                        }

                        if (lng == null || lat == null) {
                            getSelf().tell(new Error("Długość i szerokość geograficzna nie może być pusta"), getSelf());
                            return;
                        }

                        if (searchingMenus.size() == 0) {
                            getSelf().tell(new Error("Szukane menu nie może być puste"), getSelf());
                            return;
                        }

                        Search search = new Search(lng, lat, 1500, searchingMenus);
                        getContext().actorSelection("../Researcher").tell(search, getSelf());

                    } else {
                        getSelf().tell(new Error("Zbyt mało danych, lub dane wprowadzono nieprawidłowo."), getSelf());
                    }
                }

            }
        });


        rbuilder.match(Response.class, response -> {
/*            if (response.getRestaurants() != null && response.getRestaurants().size() > 0) {
                log.info("Server resived message from: " + getSender());

                log.info("\tSearching menus: ");
                for(String searchingMenu : response.getSearchingMenus()){
                    log.info("\t\t"+searchingMenu);
                }

                log.info("\tFound restaurants: ");
                for (CommonRestaurant restaurant : response.getRestaurants()) {
                    log.info("\t\t" + restaurant.getName());
                    log.debug(restaurant.toString());
                }

            }*/

            selection.tell(response, getSelf());

        });

        rbuilder.match(Error.class, error -> {
            selection.tell(error, getSelf());
        });

        // DEFAULT
        rbuilder.matchAny(o -> log.info(getSelf().path().toString() + " - unknown message type " + o.getClass().getCanonicalName()));

        return rbuilder.build();
    }

}
