/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.somatik.botleecher;

import javax.swing.ImageIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fdb
 */
public class Tools {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BotLeecher.class);

    /** Returns an ImageIcon, or null if the path was invalid.
     * @param path 
     * @param description
     * @return
     */
    public static ImageIcon createImageIcon(String path, String description) {
        java.net.URL imgURL = Tools.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            LOGGER.error("Couldn't find file: " + path);
            return null;
        }
    }
}
