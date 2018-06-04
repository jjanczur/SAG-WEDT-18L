package actors.client;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;

public class Client {

    public static void main(String[] args) {


        Config cfg = ConfigFactory.load("client");

        Config cfg2 = ConfigFactory.parseFile(new File("server.conf")).withFallback(cfg);


        // Creating environment
        ActorSystem system = ActorSystem.create("ClientServer", cfg2);

        // Client actor
        ActorRef client = system.actorOf(Props.create(ClientActor.class), "Client");

        client.tell("test", ActorRef.noSender());

        client.tell("51.490489; -0.165645646321;Roasted fish and chips;coffe; ", ActorRef.noSender());

    }
}

