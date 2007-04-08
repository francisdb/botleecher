/*
 * BotLeecher.java
 *
 * Created on April 5, 2007, 4:35 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package eu.somatik.botleecher;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import org.jibble.pircbot.DccFileTransfer;
import org.jibble.pircbot.User;

/**
 *
 * @author francisdb
 */
public class BotLeecher {
    
    private User botUser;
    
    private String description;
    
    private String savePath;
    
    private IrcConnection connection;
    
    private boolean leeching;
    
    private boolean listRequested;
    
    private int counter = 1;
    
    private DccFileTransfer curentTransfer;
    
    private String lastNotice;
    
    private File listFile;
    
    private List<BotListener> listeners;
   
    
    
    /**
     * Creates a new instance of BotLeecher
     * @param user
     * @param connection
     */
    public BotLeecher(User user, IrcConnection connection) {
        this.botUser = user;
        this.connection = connection;
        this.leeching = false;
        this.listRequested = false;
        this.lastNotice = "";
        this.description = "";
        this.listeners = new Vector<BotListener>();
        requestPackList();
    }
    
    private void requestPackList(){
        listRequested = true;
        connection.sendMessage(botUser.getNick(), "XDCC SEND 1");
    }
    
    private void requestNext() {
        counter++;
        
        if (leeching) {
            requestNextPack();
        } else {
            curentTransfer = null;
        }
    }
    
    
    /**
     *
     * @param transfer
     */
    public void onIncomingFileTransfer(DccFileTransfer transfer) {
        
        if(listRequested){
            try         {
                listFile = java.io.File.createTempFile("list", botUser.getNick());
                listFile.deleteOnExit();
                transfer.receive(listFile, false);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }else{
            curentTransfer = transfer;
            
            File saveFile = new File(savePath + transfer.getFile().getName());
            System.out.println("INCOMING:\t" + transfer.getFile().toString() + " " +
                    transfer.getSize() + " bytes");
            
            //if file exists cut one 8bytes off to make transfer go on
            if (saveFile.exists() && (transfer.getSize() == saveFile.length())) {
                System.out.println("EXISTS:\t try to close connection");
                transfer.close();
                requestNext();
                
                //FileImageInputStream fis = new FileInputStream
            } else {
                System.out.println("SAVING TO:\t" + saveFile.toString());
                transfer.receive(saveFile, true);
            }
        }
        
    }
    
    /**
     * 
     * @param listener 
     */
    public void addListener(BotListener listener){
        listeners.add(listener);
    }
    
    /**
     *
     * @param sourceNick
     * @param sourceLogin
     * @param sourceHostname
     * @param target
     * @param notice
     */
    public void onNotice(String sourceNick, String sourceLogin,
            String sourceHostname, String target, String notice) {
        if (notice.contains("Invalid Pack Number")) {
            System.out.println("DONE LEECHING BOT "+botUser.getNick());
            leeching = false;
        }
        
        if (notice.contains("point greater")) {
            System.out.println("EXISTS:\t try to close connection");
            
            curentTransfer.close();
            //this.sendMessage(botName,"XDCC remove");
            requestNext();
        }
        
        lastNotice = notice;
    }
    
    
    /**
     *
     * @param transfer
     * @param ex
     */
    public void onFileTransferFinished(DccFileTransfer transfer, Exception ex) {
        if (ex != null) {
            System.out.println(ex.getClass().getName() + " -> " + ex.getMessage());
        }
        if(listRequested){
            System.out.println("LIST:\t List received for "+transfer.getNick());
            listRequested = false;
            PackListReader reader = new PackListReader(listFile);
            for(String message:reader.getMessages()){
                this.description  += message + "\n";
            }
            for(BotListener listener:listeners){
                listener.packListLoaded(reader.getPacks());
            }
        }else{
            System.out.println("FINISHED:\t Transfer finished for "+transfer.getFile().getName());
            requestNext();
        }
    }
    
    /**
     *
     * @return
     */
    public boolean isLeeching() {
        return leeching;
    }
    
    /**
     *
     * @param connection
     */
    public void setConnection(IrcConnection connection) {
        this.connection = connection;
    }
    
    /**
     *
     * @return
     */
    public IrcConnection getConnection() {
        return connection;
    }
    /**
     *
     * @param savePath
     */
    public void setSavePath(String savePath) {
        this.savePath = savePath;
        System.out.println("saving to " + savePath+ " for bot "+botUser.getNick());
    }
    
    /**
     *
     * @param counter
     */
    public void setCounter(int counter) {
        this.counter = counter;
    }
    
    /**
     *
     * @return
     */
    public int getCounter() {
        return this.counter;
    }
    
    /**
     *
     * @return
     */
    public DccFileTransfer getCurrentTransfer() {
        return curentTransfer;
    }
    
    /**
     *
     * @return
     */
    public String getLastNotice() {
        return lastNotice;
    }
    
    /**
     * 
     * @return 
     */
    public String getDescription() {
        return description;
    }

    
    
    /**
     *
     */
    public void start(){
        leeching = true;
        requestNextPack();
    }
    
    /**
     *
     *
     */
    private void requestNextPack() {
        connection.sendMessage(botUser.getNick(), "XDCC SEND " + counter);
    }
    
    
}
