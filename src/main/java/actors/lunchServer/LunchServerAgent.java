package actors.lunchServer;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

import java.util.LinkedList;

/**
 * Agent serwera systemu. Zbiera informacje nt. uruchomionych w systemie agentow oraz przyjmuje zapytania od requesterów i rozsyła je do restaurantResearcher'ów.
 */
public class LunchServerAgent extends AbstractActor {


    LinkedList<ActorRef> menuResearchers;
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    /**
     * Generator agenta jako aktora Akki {@link Props}.
     * @return
     */
    static Props props() {
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
     * @return Receiver komunikatów.
     */
    public Receive createReceive() {
        ReceiveBuilder rbuilder = ReceiveBuilder.create();


        // DEFAULT
        rbuilder.matchAny(o -> log.info(getSelf().path().toString() + " - unknown message type " + o.getClass().getCanonicalName()));

        return rbuilder.build();
    }

}
