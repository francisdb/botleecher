/*
 * To change this template, choose JarImageLoader | Templates
 * and open the template in the editor.
 */

package eu.somatik.botleecher.service;

import javax.swing.ImageIcon;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author fdb
 */
public class JarImageLoaderTest {


    /**
     * Test of loadImageIcon method, of class JarImageLoader.
     */
    @Test
    public void testLoadImageIcon() {
        String path = "/icons/cancel.png";
        String description = "cancel icon";
        ImageLoader imageLoader = new JarImageLoader();
        ImageIcon result = imageLoader.loadImageIcon(path, description);
        assertNotNull(result);
    }

}