package eu.somatik.botleecher;

import org.jibble.pircbot.*;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;


/**
 *
 * @author francisdb
 */
public class IrcConnection extends PircBot {
    private static final String[] NICKS = {
        "spidaboy", "slickerz", "dumpoli", "moeha", "catonia", "pipolipo",
        "omgsize", "toedter", "skyhigh", "rumsound", "mathboy", "shaderz",
        "poppp", "roofly", "ruloman", "seenthis", "tiptopi", "dreamoff",
        "supergaai", "appeltje", "izidor", "tantila", "artbox", "doedoe",
        "almari", "sikaru", "lodinka"
    };
    
    private final List<IrcConnectionListener> listeners;
    private PropertyChangeSupport propertyChangeSupport;
    
    private Map<String,BotLeecher> leechers;
    
    /** Creates a new instance of Main */
    public IrcConnection() {
        super();
        
        this.leechers = Collections.synchronizedMap(new HashMap<String,BotLeecher>());
        this.listeners = new Vector<IrcConnectionListener>();
        this.setLogin(createRandomNick());
        this.setName(createRandomNick());
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
    
    private String createRandomNick() {
        return NICKS[(int) (Math.random() * NICKS.length)];
    }
    
    /**
     *
     * @param channel
     * @param sender
     * @param login
     * @param hostname
     * @param message
     */
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
    public void onNotice(String sourceNick, String sourceLogin,
            String sourceHostname, String target, String notice) {
        BotLeecher leecher = leechers.get(sourceNick);
        if(leecher != null){
            leechers.get(sourceNick).onNotice(sourceNick, sourceLogin, sourceHostname, target, notice);
        }
    }
    
    /**
     *
     * @param transfer
     */
    public void onIncomingFileTransfer(DccFileTransfer transfer) {
        leechers.get(transfer.getNick()).onIncomingFileTransfer(transfer);
    }
    
    /**
     *
     * @param transfer
     * @param ex
     */
    protected void onFileTransferFinished(DccFileTransfer transfer, Exception ex) {
        leechers.get(transfer.getNick()).onFileTransferFinished(transfer, ex);
    }
    
    /**
     *
     * @param channel
     * @param users
     */
    protected void onUserList(String channel, User[] users) {
        Arrays.sort(users, new UserComparator());
        for (IrcConnectionListener listener : listeners) {
            listener.userListLoaded(channel, users);
        }
    }
    
    private class UserComparator implements Comparator<User>{
        public int compare(User o1, User o2) {
            return o1.getNick().compareToIgnoreCase(o2.getNick());
        }
    }

    
    /**
     *
     */
    protected void onDisconnect() {
        System.out.println("DISCONNECT:\tDisconnected from server");
    }
}
