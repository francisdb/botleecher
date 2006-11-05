package eu.somatik.botleecher;

import org.jibble.pircbot.User;


public interface BotListener {
    void userListLoaded(String channel, User[] users);
}