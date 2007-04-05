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
import org.jibble.pircbot.DccFileTransfer;
import org.jibble.pircbot.User;

/**
 *
 * @author francisdb
 */
public class BotLeecher {
    
    private User user;
    
    private String savePath;
    
    private IrcConnection connection;
    
    private boolean leeching;
    
    private int counter = 1;
    
    private DccFileTransfer curentTransfer;
    
    private String lastNotice;
    
    /** 
     * Creates a new instance of BotLeecher 
     * @param user 
     * @param connection 
     */
    public BotLeecher(User user, IrcConnection connection) {
        this.user = user;
        this.connection = connection;
        this.leeching = false;
        this.lastNotice = "";
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
            System.out.println("DONE LEECHING BOT "+user.getNick());
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
        System.out.println("FINISHED:\t Transfer finished");
        
        if (ex != null) {
            System.out.println(ex.getClass().getName() + " -> " +
                    ex.getMessage());
        }
        
        requestNext();
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
        System.out.println("saving to " + savePath+ " for bot "+user.getNick());
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
        connection.sendMessage(user.getNick(), "XDCC SEND " + counter);
    }
    
    
}
