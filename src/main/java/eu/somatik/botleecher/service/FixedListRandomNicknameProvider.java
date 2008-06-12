/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.somatik.botleecher.service;

import com.google.inject.Singleton;

/**
 *
 * @author fdb
 */
@Singleton
public class FixedListRandomNicknameProvider implements NicknameProvider {
    private static final String[] NICKS = {
        "spidaboy", "slickerz", "dumpoli", "moeha", "catonia", "pipolipo",
        "omgsize", "toedter", "skyhigh", "rumsound", "mathboy", "shaderz",
        "poppp", "roofly", "ruloman", "seenthis", "tiptopi", "dreamoff",
        "supergaai", "appeltje", "izidor", "tantila", "artbox", "doedoe",
        "almari", "sikaru", "lodinka"
    };
    
    @Override
    public String getNickName() {
        return NICKS[(int) (Math.random() * NICKS.length)];
    }

}
