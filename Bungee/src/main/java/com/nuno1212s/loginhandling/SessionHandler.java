package com.nuno1212s.loginhandling;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles player sessions
 */
public class SessionHandler {

    Map<UUID, SessionData> sessions = Collections.synchronizedMap(new HashMap<>());

    private static SessionHandler ins = new SessionHandler();

    public static SessionHandler getIns() {
        return ins;
    }

    public Map<UUID, SessionData> getSessions() {
        return sessions;
    }

    public SessionData getSession(UUID uuid) {
        return sessions.get(uuid);
    }

    public void addSession(SessionData d) {
        sessions.put(d.getPlayerID(), d);
    }

    public void updateSession(UUID u, SessionData d) {
        sessions.put(u, d);
    }

    public void removeSession(UUID u) {
        sessions.remove(u);
    }
}
