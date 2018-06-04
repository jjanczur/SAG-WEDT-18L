package actors.lunchServer;

import actors.menuClassifier.MenuClassifierAgent;
import actors.restaurantResearcher.RestaurantResearcherAgent;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;

/**
 * Klasa uruchamiająca serwer systemu. Serwer zbiera informacje nt. uruchomionych w
 * systemie agentów oraz przyjmuje zapytania od użytkownika i rozsyła je do RestaurantResearcher.
 */

public class LunchServer {

    /**
     * Server entry point.
     *
     * @param args Parametry CLI.
     */
    public static void main(String[] args) {


        Config cfg = ConfigFactory.load("lunchServer");

        Config cfg2 = ConfigFactory.parseFile(new File("server.conf")).withFallback(cfg);

        ActorSystem system = ActorSystem.create("LunchServer", cfg2);

        system.actorOf(LunchServerAgent.props(), "Server");

/*        final ActorRef server = system.actorOf(LunchServerAgent.props(), "Server");*/

        final ActorRef classifyActor = system.actorOf(MenuClassifierAgent.props(), "Calssifier");

        final ActorRef research = system.actorOf(RestaurantResearcherAgent.props(), "Researcher");

    /*    server.tell("test",ActorRef.noSender());

        server.tell("51.490489; -0.165645646321;Roasted fish and chips;coffe; ",ActorRef.noSender());

        server.tell("51.490489; -0.17645646321;Roasted fish and chips; ",ActorRef.noSender());*/
    }
}

