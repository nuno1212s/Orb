package com.nuno1212s.bungee.loginhandler;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/**
 * Session Data
 */
@Data
@AllArgsConstructor
public class SessionData {

    UUID playerID;

    boolean authenticated;

}
