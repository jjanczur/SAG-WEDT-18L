package actors.restaurantResearcher;


import actors.menuClassifier.MenuClassifierAgent;
import akka.actor.ActorSystem;
import akka.routing.RoundRobinPool;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;

/*
* Głównym zadaniem tego aktora jest wyszukiwanie restauracji
* za pomocą API serwisu Zomato. API to udostępnia możliwość
* stosowania do 1000 zapytań na dobę i można specyfikować w
* nim lokalizację, typ kuchni, popularność itd. Ma ono tą przewagę
* nad szukaniem danych w innych źródłach, że pozwala na wyszukiwanie
* restauracji o określonym poziomie popularności/jakości jedzenia.
 * Dodatkowo, w odpowiedzi na zapytanie, może zwracać adresy do stron
 * na Facebooku czy Twitterze.*/
public class RestaurantResearcher {

    public static void main(String[] args) {

        int pool = 5;

        Config cfg = ConfigFactory.load("researcher");

        Config cfg2 = ConfigFactory.parseFile(new File("server.conf")).withFallback(cfg);

        ActorSystem system = ActorSystem.create("ResearcherServer", cfg2);

        system.actorOf(new RoundRobinPool(pool).props(RestaurantResearcherAgent.props()), "Researcher");

    }

}
