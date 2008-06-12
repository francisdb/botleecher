package eu.somatik.botleecher.service;

import com.google.inject.Singleton;
import eu.somatik.botleecher.*;
import javax.swing.ImageIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fdb
 */
@Singleton
public class JarImageLoader implements ImageLoader {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BotLeecher.class);

    /** Returns an ImageIcon, or null if the path was invalid.
     * @param path 
     * @param description
     * @return
     */
    @Override
    public ImageIcon loadImageIcon(String path, String description) {
        java.net.URL imgURL = JarImageLoader.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            LOGGER.error("Couldn't find file: " + path);
            return null;
        }
    }
}
