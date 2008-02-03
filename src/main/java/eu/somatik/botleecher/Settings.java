/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.somatik.botleecher;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fdb
 */
public class Settings {

    private static final String CONFIG = "botleecher.properties";
    
    private static final String PROP_SAVEFOLDER = "savefolder";

    public File getSaveFolder() {
        File saveFolder = null;
        Properties configFile = loadConfig();
        String folder = configFile.getProperty(PROP_SAVEFOLDER);
        if (folder != null) {
            saveFolder = new File(folder);
        }else{
            //TODO ask user?
        }
        return saveFolder;
    }

    private Properties loadConfig() {
        Properties configFile = new Properties();
        try {
            configFile.load(this.getClass().getClassLoader().getResourceAsStream(CONFIG));
        } catch (IOException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.INFO, "properties file not found, generating new one", ex);
            createConfig();
        }
        return configFile;
    }

    private void createConfig(){
        Properties configFile = new Properties();
        saveConfig(configFile);
    }
    
    private void saveConfig(Properties configFile) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(CONFIG);
            configFile.store(writer, "botleecher configuration file");
        } catch (IOException ex1) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex1);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex1) {
                    Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
    }
}
