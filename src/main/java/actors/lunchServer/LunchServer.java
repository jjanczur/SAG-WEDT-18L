package actors.lunchServer;

import actors.menuClassifier.MenuClassifierAgent;
import actors.message.Classify;
import actors.restaurantResearcher.Restaurant;
import actors.restaurantResearcher.ZomatoCollection;
import actors.restaurantResearcher.RestaurantResearcherAgent;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
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
        final ActorRef research = system.actorOf(RestaurantResearcherAgent.props(), "Researcher");
        research.tell("get-restaurants", ActorRef.noSender());


        final ActorRef classifyActor = system.actorOf(MenuClassifierAgent.props(), "Calssifier");

        String searchingMenu = "roasted fish and chips and sauce and cucumber";
        ZomatoCollection restaurantsCollection = new ZomatoCollection();

        restaurantsCollection.restaurants.add(new Restaurant("roasted fish and chips"));
        restaurantsCollection.restaurants.add(new Restaurant("Gravlax in lemon-sesame salsa with fresh coriander, chilli and cucumber"));
        restaurantsCollection.restaurants.add(new Restaurant(searchingMenu));

        Classify classify = new Classify();
        //classify.setSearchingMenu(searchingMenu);
        //classify.setRestaurantC(restaurantsCollection);

        classifyActor.tell(classify, ActorRef.noSender());

        //system.actorOf(LunchServerAgent.props());

    }
}

