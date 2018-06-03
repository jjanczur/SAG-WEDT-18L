package actors.lunchServer;

import actors.menuClassifier.MenuClassifierAgent;
import actors.message.*;

import actors.message.Search;
import actors.restaurantResearcher.Restaurant;
import actors.restaurantResearcher.ZomatoCollection;
import actors.restaurantResearcher.CommonRestaurant;

import actors.restaurantResearcher.RestaurantResearcherAgent;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import java.util.ArrayList;
import java.util.List;

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


        ActorSystem system = ActorSystem.create("Actorsystem");

        final ActorRef server = system.actorOf(LunchServerAgent.props(), "Server");

        final ActorRef classifyActor = system.actorOf(MenuClassifierAgent.props(), "Calssifier");

        final ActorRef research = system.actorOf(RestaurantResearcherAgent.props(), "Researcher");

        server.tell("test",ActorRef.noSender());

        server.tell("51.490489; -0.165645646321;Roasted fish and chips;coffe; ",ActorRef.noSender());



    }
}

