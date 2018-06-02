package actors.message;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class Error implements Serializable {

    private static final long serialVersionUID = -2854754335549198980L;
    private String msg;

    public Error(String msg) {
        this.msg = msg;
    }

}
