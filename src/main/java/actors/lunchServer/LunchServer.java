package actors.lunchServer;

import actors.menuClassifier.MenuClassifierAgent;
import actors.message.Classify;
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
        /*todo: Ogarnąć po co jest ten cfg2 - skąd w ogóle bierze plik server.conf ????(może jakiś domyśłny - nie mylić z /conf/server.conf)*/

        /*
         * ustawienia serwera
         */
        //Props.create(RestaurantResearcherAgent.class);
        //Config cfg = ConfigFactory.load("conf/server");
        // Config cfg2 = ConfigFactory.parseFile(new File("server.conf")).withFallback(cfg);

        ActorSystem system = ActorSystem.create("Actorsystem");


        final ActorRef server = system.actorOf(LunchServerAgent.props(), "Server");


        final ActorRef research = system.actorOf(RestaurantResearcherAgent.props(), "Researcher");
        research.tell("get-restaurants", ActorRef.noSender());


        // test klasyfiaktora menu
        final ActorRef classifyActor = system.actorOf(MenuClassifierAgent.props(), "Calssifier");

        String searchingMenu1 = "Roasted fish and chips and sauce and cucumber.";
        String searchingMenu2 = "Lemon-sesame salsa.";
        List<String> searchingMenus = new ArrayList<String>();
        searchingMenus.add(searchingMenu1);
        searchingMenus.add(searchingMenu2);


        List<CommonRestaurant> restaurantsCollection = new ArrayList<CommonRestaurant>();


        restaurantsCollection.add(new CommonRestaurant(1, "zomato", "Roasted fish and chips.",
                "Nad zapracowanym Jackiem", "Plac Szymiego 23/147"));

        restaurantsCollection.add(new CommonRestaurant(2, "zomato", "Gravlax in lemon-sesame salsa with " +
                "fresh coriander, chilli and cucumber.",
                "U fajnego Pana Szymona", "Plac Jackowsikiego"));

        restaurantsCollection.add(new CommonRestaurant("1", "google", searchingMenu1 + " " + searchingMenu2,
                "Pod potężnym Dominikiem", "Plac Wielkiego Dzika 21/37"));


        Classify classify = new Classify();
        classify.setSearchingMenus(searchingMenus);
        classify.setRestaurants(restaurantsCollection);

        classifyActor.tell(classify, server);


    }
}

