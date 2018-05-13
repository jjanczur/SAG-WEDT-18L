package actors.message;

import java.io.Serializable;

/**
 * Komunikat wysyłany przez LunchSerwer do użytkownika, jeśli w systemie nie ma aktualnie żadnych uruchomionych agentów.
 */
public class NoAgents implements Serializable {
    private static final long serialVersionUID = 4830948398450520L;
}
