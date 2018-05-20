package actors.lunchServer;
import actors.restaurantResearcher.RestaurantResearcherAgent;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
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
     * @param args Parametry CLI.
     */
    public static void main( String[] args )
    {
        /*todo: Ogarnąć po co jest ten cfg2 - skąd w ogóle bierze plik server.conf ????(może jakiś domyśłny - nie mylić z /conf/server.conf)*/

        /*
        * ustawienia serwera
        */
        //Props.create(RestaurantResearcherAgent.class);
        //Config cfg = ConfigFactory.load("conf/server");
       // Config cfg2 = ConfigFactory.parseFile(new File("server.conf")).withFallback(cfg);


        ActorSystem system = ActorSystem.create("Actorsystem");
        final ActorRef research = system.actorOf(RestaurantResearcherAgent.props(), "Researcher");
        research.tell("get-zomato", ActorRef.noSender());

        //system.actorOf(LunchServerAgent.props());
    }
}

