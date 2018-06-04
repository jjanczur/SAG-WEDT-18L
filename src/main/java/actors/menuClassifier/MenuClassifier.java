package actors.menuClassifier;

import akka.actor.ActorSystem;
import akka.routing.FromConfig;
import akka.routing.RoundRobinPool;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;

public class MenuClassifier {

    public static void main(String[] args) {

        int pool = 1;

        Config cfg = ConfigFactory.load("classifier");

        Config cfg2 = ConfigFactory.parseFile(new File("server.conf")).withFallback(cfg);

        ActorSystem system = ActorSystem.create("ClassifierServer", cfg2);

        //system.actorOf(new RoundRobinPool(pool).props(MenuClassifierAgent.props()), "Classifier");
        system.actorOf(FromConfig.getInstance().props(MenuClassifierAgent.props()), "Classifier");

    }
}
