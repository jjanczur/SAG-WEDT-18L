package actors.message;

import akka.actor.ActorRef;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ErrorMessage implements Serializable {

    private static final long serialVersionUID = -2854754335549198980L;
    private String msg;
    private ActorRef requester;
    public ErrorMessage(ActorRef requester, String msg)
    {
        this.requester = requester;
        this.msg = msg;
    }

}
