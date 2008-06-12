/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.somatik.botleecher.module;

import com.google.inject.AbstractModule;
import eu.somatik.botleecher.service.PackListReader;
import eu.somatik.botleecher.service.PackListReaderImpl;
import eu.somatik.botleecher.service.BotLeecherFactory;
import eu.somatik.botleecher.service.BotLeecherFactoryImpl;
import eu.somatik.botleecher.service.ImageLoader;
import eu.somatik.botleecher.service.JarImageLoader;
import eu.somatik.botleecher.service.NicknameProvider;
import eu.somatik.botleecher.service.FixedListRandomNicknameProvider;
import eu.somatik.botleecher.service.Settings;
import eu.somatik.botleecher.service.SettingsImpl;

/**
 * Guice configuration module
 * @author fdb
 */
public class BotLeecherModule extends AbstractModule{

    @Override
    protected void configure() {
        bind(Settings.class).to(SettingsImpl.class);
        bind(NicknameProvider.class).to(FixedListRandomNicknameProvider.class);
        bind(ImageLoader.class).to(JarImageLoader.class);
        bind(BotLeecherFactory.class).to(BotLeecherFactoryImpl.class);
        bind(PackListReader.class).to(PackListReaderImpl.class);
    }

}
