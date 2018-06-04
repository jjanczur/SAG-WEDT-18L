package actors.lunchServer;

import actors.message.ErrorMessage;
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
    private ActorSelection restaurantResearcherSelection = getContext().actorSelection("akka.tcp://ResearcherServer@127.0.0.1:5151/user/Researcher");


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
        log.info("[SUCCESS] Started lunchServer! " + getSelf().path());
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


                for(int i = 0; i<20; i++){
                    List<String> searchingMenus = new ArrayList<String>();
                    Util tools = new Util();
                    for (int z = 0; z < 5; z++) {
                        searchingMenus.add(tools.createRandomMenu(2+i));
                    }

                    Search search = new Search(getSender(),51.490489, -0.167910, 1500,searchingMenus );
                    restaurantResearcherSelection.tell(search, getSelf());
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
                            getSelf().tell(new ErrorMessage(getSender(),"Niepraaidłowa długość lub szerokość geograficzna"), getSelf());
                            return;
                        }

                        List<String> searchingMenus = new ArrayList<String>();
                        for (int i = 2; i < split.length; i++) {

                            if (!split[i].equals(" ")) {
                                searchingMenus.add(split[i]);
                            }

                        }

                        if (lng == null || lat == null) {
                            getSelf().tell(new ErrorMessage(getSender(),"Długość i szerokość geograficzna nie może być pusta"), getSelf());
                            return;
                        }

                        if (searchingMenus.size() == 0) {
                            getSelf().tell(new ErrorMessage(getSender(),"Szukane menu nie może być puste"), getSelf());
                            return;
                        }

                        Search search = new Search(getSender(), lng, lat, 1500, searchingMenus);
                        restaurantResearcherSelection.tell(search, getSelf());

                    } else {
                        getSelf().tell(new ErrorMessage(getSender(),"Zbyt mało danych, lub dane wprowadzono nieprawidłowo."), getSelf());
                    }
                }

            }
        });


        rbuilder.match(Response.class, response -> {

            response.getRequester().tell(response, getSelf());

        });

        rbuilder.match(ErrorMessage.class, error -> {
            error.getRequester().tell(error, getSelf());
        });




        // DEFAULT
        rbuilder.matchAny(o -> log.info(getSelf().path().toString() + " - unknown message type " + o.getClass().getCanonicalName()));

        return rbuilder.build();
    }

}
