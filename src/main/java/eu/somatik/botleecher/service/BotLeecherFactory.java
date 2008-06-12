package eu.somatik.botleecher.service;

import eu.somatik.botleecher.BotLeecher;
import eu.somatik.botleecher.IrcConnection;
import org.jibble.pircbot.User;

/**
 *
 * @author fdb
 */
public interface BotLeecherFactory {
    
    /**
     * Creates a botleecher
     * @param user
     * @param settings
     * @param botListener
     * @return
     */
    BotLeecher getBotLeecher(User user, IrcConnection connection);


}
