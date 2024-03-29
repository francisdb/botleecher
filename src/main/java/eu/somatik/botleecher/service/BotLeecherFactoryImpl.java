/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.somatik.botleecher.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.somatik.botleecher.BotLeecher;
import eu.somatik.botleecher.IrcConnection;
import org.jibble.pircbot.User;

/**
 *
 * @author fdb
 */
@Singleton
public class BotLeecherFactoryImpl implements BotLeecherFactory{
    private final Settings settings;
    private final PackListReader packListReader;

    @Inject
    public BotLeecherFactoryImpl(final Settings settings, final PackListReader packListReader) {
        this.settings = settings;
        this.packListReader = packListReader;
    }

    @Override
    public BotLeecher getBotLeecher(User user, IrcConnection connection) {
        return new BotLeecher(user, connection, settings, packListReader);
    }

}
