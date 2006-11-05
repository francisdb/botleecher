package eu.somatik.botleecher;


import java.io.File;

import org.jibble.pircbot.*;
/**
 *
 * @author fdb
 */




public class BotConnection extends PircBot {
    
    private static final String[] NICKS={"spidaboy","slickerz","dumpoli","moeha","catonia","pipolipo","omgsize",
            "toedter","skyhigh","rumsound","mathboy","shaderz","poppp","roofly","ruloman","seenthis","tiptopi",
            "dreamoff","supergaai","appeltje","izidor","tantila","artbox","doedoe","almari","sikaru","lodinka"};
    
    private int counter=1;
    private String botName;
    private boolean finished=false;
    private DccFileTransfer curentTransfer;
    private String lastMessage;
    private String savePath=null;
    
    /** Creates a new instance of Main */
    public BotConnection() {
        this.setName(getRandomNick());
        this.setAutoNickChange(true);
        this.setVerbose(true);
    }
    
    private String getRandomNick(){
        return NICKS[(int)(Math.random()*NICKS.length)];
    }
    
    public void onMessage(String channel, String sender,
           String login, String hostname, String message) {
        if (message.equalsIgnoreCase("time")) {
            String time = new java.util.Date().toString();
            sendMessage(channel, sender + ": The time is now " + time);
        }
    }
    
    public void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice) {
        if (notice.contains("Invalid Pack Number")){
            finished = true;
        }
        if(notice.contains("point greater")){
            System.out.println("EXISTS:\t try to close connection");
            curentTransfer.close();
            //this.sendMessage(botName,"XDCC remove");
            requestNext();
        }
        lastMessage=notice;
    }
    
    public void onIncomingFileTransfer(DccFileTransfer transfer) {
        curentTransfer = transfer;
        File saveFile = new File(savePath+transfer.getFile().getName());
        System.out.println("INCOMING:\t" + transfer.getFile().toString() + " " + transfer.getSize() + " bytes");
        //if file exists cut one 8bytes off to make transfer go on
        
        if (saveFile.exists() && transfer.getSize()==saveFile.length()){
            System.out.println("EXISTS:\t try to close connection");
            transfer.close();
            requestNext();
            //FileImageInputStream fis = new FileInputStream
        }else{
            System.out.println("SAVING TO:\t"+saveFile.toString());
            transfer.receive(saveFile, true);
        }
    }
    
    protected void onFileTransferFinished(DccFileTransfer tansfer, Exception ex) {
        System.out.println("FINISHED:\t Transfer finished");
        if (ex!=null)
            System.out.println(ex.getClass().getName()+" -> "+ex.getMessage());
        requestNext();
      
    }
    
    private void requestNext(){
        counter++;
        if(!finished)
            this.leechBot(botName);
        else
            curentTransfer=null;  
    }
    
    public void leechBot(String botName){
        this.botName=botName;
        this.sendMessage(botName,"XDCC SEND "+counter);
    }
    
    public DccFileTransfer getCurrentTransfer(){
        return curentTransfer;
    }
    
    public void setCounter(int counter){
        this.counter=counter;
    }
    
    public int getCounter(){
        return this.counter;
    }
    
    public String getLastMessage(){
        return lastMessage;
    }
    
    public void setSavePath(String savePath){
        this.savePath = savePath;
        System.out.println("saving to "+savePath);
    }
        
    protected void onDisconnect() {
        //JOptionPane.showMessageDialog(parent,"Disconnected from server");
        System.out.println("DISCONNECT:\tDisconnected from server");
    }
    
}
