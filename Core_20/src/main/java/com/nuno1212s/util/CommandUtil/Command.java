package com.nuno1212s.util.CommandUtil;

/**
 * Command interface
 */
public interface Command <T> {

    String[] names();

    String usage();

    void execute(T player, String[] args);

}
