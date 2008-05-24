/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.somatik.botleecher.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author fdb
 */
public class SettingsImpl implements Settings {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsImpl.class);

    private static final String CONFIG_FILE_NAME = "botleecher.properties";
    
    private static final String PROP_SAVEFOLDER = "savefolder";

    @Override
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
        path += File.separator + CONFIG_FILE_NAME;
        return path;
    }

    private Properties loadConfig() {
        Properties configFile = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(getConfigFilePath());
            configFile.load(fis);
        } catch (IOException ex) {
            LOGGER.error("properties file not found, generating new one",ex);
            createConfig();
        } finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException ex) {
                    LOGGER.error("Could not close the FileInputStream", ex);
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
        } catch (IOException ex) {
            LOGGER.error("Error writing properties to file", ex);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    LOGGER.error("Could not close the FileWriter", ex);
                }
            }
        }
    }
}
