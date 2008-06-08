package eu.somatik.botleecher;

import eu.somatik.botleecher.service.NicknameProviderImpl;
import org.jibble.pircbot.*;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author francisdb
 */
public class IrcConnection extends PircBot {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BotLeecher.class);

    
    private final List<IrcConnectionListener> listeners;
    private PropertyChangeSupport propertyChangeSupport;
    
    private Map<String,BotLeecher> leechers;
    
    /** Creates a new instance of Main */
    public IrcConnection() {
        super();
        
        NicknameProviderImpl nickProvider = new NicknameProviderImpl();
        
        this.leechers = Collections.synchronizedMap(new HashMap<String,BotLeecher>());
        this.listeners = new Vector<IrcConnectionListener>();
        this.setLogin(nickProvider.generateRandomNick());
        this.setName(nickProvider.generateRandomNick());
        this.setFinger(nickProvider.generateRandomNick());
        this.setVersion("xxx");
        this.setAutoNickChange(true);
        this.setVerbose(true);
    }
    
    /**
     *
     *
     * @param user 
     * @return 
     */
    public BotLeecher makeLeecher(User user) {
        BotLeecher leecher = new BotLeecher(user, this);
        leecher.start();
        leechers.put(user.getNick(),leecher);
        return leecher;
    }
    
    /**
     * 
     * @param botName 
     * @return 
     */
    public BotLeecher getBotLeecher(String botName){
        return leechers.get(botName);
    }
    
    public void shutdown(){
        LOGGER.info("Shutting down all leechers");
        for(BotLeecher leecher:leechers.values()){
            leecher.shutdown();
        }
        disconnect();
    }
    
    /**
     *
     * @param listener
     */
    public void addBotListener(IrcConnectionListener listener) {
        listeners.add(listener);
    }
    
    
    /**
     *
     * @param listener
     */
    public void removeBotListener(IrcConnectionListener listener) {
        listeners.remove(listener);
    }
    
   
    
    /**
     *
     * @param channel
     * @param sender
     * @param login
     * @param hostname
     * @param message
     */
    @Override
    public void onMessage(String channel, String sender, String login,
            String hostname, String message) {
        if (message.equalsIgnoreCase("time")) {
            String time = new java.util.Date().toString();
            sendMessage(channel, sender + ": The time is now " + time);
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
    @Override
    public void onNotice(String sourceNick, String sourceLogin,
            String sourceHostname, String target, String notice) {
        BotLeecher leecher = leechers.get(sourceNick);
        if(leecher != null){
            leecher.onNotice(sourceNick, sourceLogin, sourceHostname, target, notice);
        }
    }
    
    /**
     *
     * @param transfer
     */
    @Override
    public void onIncomingFileTransfer(DccFileTransfer transfer) {
        leechers.get(transfer.getNick()).onIncomingFileTransfer(transfer);
    }
    
    /**
     *
     * @param transfer
     * @param ex
     */
    @Override
    protected void onFileTransferFinished(DccFileTransfer transfer, Exception ex) {
        leechers.get(transfer.getNick()).onFileTransferFinished(transfer, ex);
    }
    
    /**
     *
     * @param channel
     * @param users
     */
    @Override
    protected void onUserList(String channel, User[] users) {
        Arrays.sort(users, new UserComparator());
        for (IrcConnectionListener listener : listeners) {
            listener.userListLoaded(channel, users);
        }
    }
    
    private class UserComparator implements Comparator<User>{
        @Override
        public int compare(User o1, User o2) {
            return o1.getNick().compareToIgnoreCase(o2.getNick());
        }
    }

    
    /**
     *
     */
    @Override
    protected void onDisconnect() {
        System.out.println("DISCONNECT:\tDisconnected from server");
        for (IrcConnectionListener listener : listeners) {
            listener.disconnected();
        }
    }
}
