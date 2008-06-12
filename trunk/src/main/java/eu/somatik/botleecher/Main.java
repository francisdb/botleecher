/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.somatik.botleecher;

import com.google.inject.Guice;
import com.google.inject.Injector;
import eu.somatik.botleecher.gui.BotMediator;
import eu.somatik.botleecher.module.BotLeecherModule;

/**
 *
 * @author fdb
 */
public class Main {

    public Main() {
    }
    
    public static void main(String[] args){
        Injector injector = Guice.createInjector(new BotLeecherModule());
        BotMediator mediator = injector.getInstance(BotMediator.class);
        mediator.start();
    }

}
