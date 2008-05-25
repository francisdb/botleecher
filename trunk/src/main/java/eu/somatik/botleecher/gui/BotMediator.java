/*
 * BotMediator.java
 *
 * Created on February 22, 2007, 10:21 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package eu.somatik.botleecher.gui;

import eu.somatik.botleecher.tools.TextWriter;
import eu.somatik.botleecher.tools.DualOutputStream;
import eu.somatik.botleecher.*;
import java.awt.Cursor;
import org.jibble.pircbot.User;

import java.io.PrintStream;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author francisdb
 */
public class BotMediator implements IrcConnectionListener, TextWriter {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BotMediator.class);
    
    private IrcConnection icrConnection;
    private LeecherFrame leecherFrame;
    
    /** Creates a new instance of BotMediator */
    public BotMediator() {
        icrConnection = new eu.somatik.botleecher.IrcConnection();
        icrConnection.addBotListener(this);
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                leecherFrame = new LeecherFrame(BotMediator.this);
                // center
                leecherFrame.setLocationRelativeTo(null);
                leecherFrame.setVisible(true);
            }
        });
        
        PrintStream oldStream = System.out;
        PrintStream aPrintStream  = new PrintStream(new DualOutputStream(oldStream, this));
        System.setOut(aPrintStream); // catches System.out messages
        System.setErr(aPrintStream); // catches error messages
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            UIManager.LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();
            
            for (int i = 0; i < lookAndFeels.length; i++) {
                System.out.println("Available lookAndFeel: " +
                        lookAndFeels[i].getClassName());
            }
            
            System.out.println("Setting lookAndFeel: " +
                    UIManager.getSystemLookAndFeelClassName());
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            LOGGER.error("Could not set system look and feel",ex);
        }
        
        new BotMediator();
    }
    
    @Override
    public void userListLoaded(final String channel, final  User[] users) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                leecherFrame.setUsers(users);
            }
        });
    }

    @Override
    public void writeText(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                leecherFrame.writeToLog(text);
            }
        });
    }
    
    
    /**
     * Connects to the irc network
     */
    protected void connect() {
        new ConnectWorker().execute();
    }
    
    
    
    private class ConnectWorker extends SwingWorker<String, String> {
        private final String server;
        private final String channel;
        
        public ConnectWorker() {
            leecherFrame.setContolsActivated(false);
            server = leecherFrame.getServer();
            channel = leecherFrame.getRoom();
            leecherFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            // TODO show hourglass to user
        }
        
        @Override
        protected String doInBackground() throws Exception {
            icrConnection.connect(server);
            icrConnection.joinChannel(channel);
            return null;
        }
        
        @Override
        protected void done() {
            try {
                get();
            } catch (ExecutionException ex) {
                leecherFrame.setContolsActivated(true);
                leecherFrame.showException(ex.getCause().getClass().getSimpleName()+": "+ex.getCause().getMessage());
                LOGGER.error("Could not connect & join channel",ex.getCause());
            } catch (InterruptedException ex) {
                leecherFrame.setContolsActivated(true);
                leecherFrame.showException(ex.getClass().getSimpleName()+": "+ex.getMessage());
                LOGGER.error("Connect worker interrupted", ex);
            }finally{
                leecherFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }
    }
    
    /**
     * Todo refactor, this should stay private
     * @return 
     */
    protected IrcConnection getIcrConnection(){
        return icrConnection;
    }
    

    @Override
    public void disconnected() {
        leecherFrame.setContolsActivated(true);
    }
}
