package com.nuno1212s.command;

/**
 * Handles registering commands
 */
public interface CommandRegister {

    void registerCommand(String[] aliases, Object commandExecutor);

}
