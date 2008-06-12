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
public class NicknameProviderImplTest {

    /**
     * Test of getNickName method, of class FixedListRandomNicknameProvider.
     */
    @Test
    public void testGenerateRandomNick() {
        NicknameProvider instance = new FixedListRandomNicknameProvider();
        String result = instance.getNickName();
        assertFalse("Generated nickname is NULL", result == null);
        assertTrue("Generated nickname length == 0", result.length() > 0);
    }

}