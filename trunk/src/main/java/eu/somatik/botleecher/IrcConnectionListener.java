package eu.somatik.botleecher;

import org.jibble.pircbot.User;


/**
 * 
 * @author francisdb
 */
public interface IrcConnectionListener {
    /**
     * 
     * @param channel 
     * @param users 
     */
    void userListLoaded(String channel, User[] users);
    
    void disconnected();
}