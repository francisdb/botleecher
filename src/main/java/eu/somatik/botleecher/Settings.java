/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.somatik.botleecher;

import java.io.File;
import java.io.FileInputStream;
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
            File userHome = new File(System.getProperty("user.home"));
            File newFolder = new File(userHome,"downloads");
            if(!newFolder.exists()){
                newFolder.mkdir();
            }
            configFile.setProperty(PROP_SAVEFOLDER, newFolder.getAbsolutePath());
            saveConfig(configFile);
        }
        return saveFolder;
    }
    
    private String getConfigFilePath(){
        String path = System.getProperty("user.home");
        path += File.separator + CONFIG;
        return path;
    }

    private Properties loadConfig() {
        Properties configFile = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(getConfigFilePath());
            configFile.load(fis);
        } catch (IOException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.INFO, "properties file not found, generating new one");
            createConfig();
        } finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException ex) {
                    Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
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
            writer = new FileWriter(getConfigFilePath());
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
