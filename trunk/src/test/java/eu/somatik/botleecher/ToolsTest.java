/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.somatik.botleecher;

import javax.swing.ImageIcon;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fdb
 */
public class ToolsTest {

    public ToolsTest() {
    }

    /**
     * Test of createImageIcon method, of class Tools.
     */
    @Test
    public void testCreateImageIcon() {
        String path = "/icons/cancel.png";
        String description = "cancel icon";
        ImageIcon expResult = null;
        ImageIcon result = Tools.createImageIcon(path, description);
        assertNotNull(result);
    }

}