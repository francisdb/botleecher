/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.somatik.botleecher.service;


import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fdb
 */
public class NicknameProviderTest {

    /**
     * Test of generateRandomNick method, of class NicknameProviderImpl.
     */
    @Test
    public void testGenerateRandomNick() {
        NicknameProvider instance = new NicknameProviderImpl();
        String result = instance.generateRandomNick();
        assertFalse("Generated nickname is NULL", result == null);
        assertTrue("Generated nickname length == 0", result.length() > 0);
    }

}