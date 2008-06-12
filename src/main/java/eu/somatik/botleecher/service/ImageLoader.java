
package eu.somatik.botleecher.service;

import javax.swing.ImageIcon;

/**
 * Loads images
 * @author fdb
 */
public interface ImageLoader {
    
    ImageIcon loadImageIcon(String path, String description);
}
