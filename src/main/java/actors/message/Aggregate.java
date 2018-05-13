package actors.message;

import java.io.Serializable;

/**
 * Komunikat wysyłany przed zamknięciem przez program Requestera do swojego agenta. Agent requestera po otrzymaniu tego komunikatu, agreguje otrzymane do tej pory wyniki klasyfikacji, wybierając najbardziej prawdopodobną klasę.
 */
public class Aggregate implements Serializable {
    private static final long serialVersionUID = 7483949478932L;
}