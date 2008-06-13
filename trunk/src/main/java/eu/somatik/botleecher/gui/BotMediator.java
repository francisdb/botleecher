/*
 * BotMediator.java
 *
 * Created on February 22, 2007, 10:21 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package eu.somatik.botleecher.gui;

import com.google.inject.Inject;
import eu.somatik.botleecher.tools.TextWriter;
import eu.somatik.botleecher.tools.DualOutputStream;
import eu.somatik.botleecher.*;
import eu.somatik.botleecher.service.ImageLoader;
import java.awt.Cursor;
import org.jibble.pircbot.User;

import java.io.PrintStream;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
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
    private ImageLoader imageLoader;

    
    /** Creates a new instance of BotMediator */
    @Inject
    public BotMediator(IrcConnection ircConnection, ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
        icrConnection = ircConnection;
        icrConnection.addBotListener(this);
        leecherFrame = null;
    }
    
    public void start(){
        initLookAndFeel();
        redirectOutputStreams();
        startGuiOnEDT();
    }

    private void startGuiOnEDT() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                leecherFrame = new LeecherFrame(BotMediator.this, imageLoader);
                // center
                leecherFrame.setLocationRelativeTo(null);
                leecherFrame.setVisible(true);
            }
        });
    }
    
    private void redirectOutputStreams(){
        PrintStream oldStream = System.out;
        PrintStream aPrintStream = new PrintStream(new DualOutputStream(oldStream, this));
        System.setOut(aPrintStream); // catches System.out messages
        System.setErr(aPrintStream); // catches error messages
    }
    
    private void initLookAndFeel(){
        try {
            UIManager.LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();

            for (int i = 0; i < lookAndFeels.length; i++) {
                LOGGER.debug("Available lookAndFeel: " +
                        lookAndFeels[i].getClassName());
            }

            LOGGER.info("Setting lookAndFeel: " +
                    UIManager.getSystemLookAndFeelClassName());
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (IllegalAccessException ex) {
            LOGGER.error("Could not set system look and feel", ex);
        } catch (UnsupportedLookAndFeelException ex) {
            LOGGER.error("Could not set system look and feel", ex);
        } catch(ClassNotFoundException ex){
            LOGGER.error("Could not set system look and feel", ex);
        }catch(InstantiationException ex){
            LOGGER.error("Could not set system look and feel", ex);
        }
    }

    @Override
    public void userListLoaded(final String channel, final User[] users) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                leecherFrame.setUsers(users);
            }
        });
    }

    @Override
    public void writeText(final String text) {
        if(leecherFrame != null){
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    leecherFrame.writeToLog(text);
                }
            });
        }else{
            LOGGER.info(text);
        }
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
                leecherFrame.showException(ex.getCause().getClass().getSimpleName() + ": " + ex.getCause().getMessage());
                LOGGER.error("Could not connect & join channel", ex.getCause());
            } catch (InterruptedException ex) {
                leecherFrame.setContolsActivated(true);
                leecherFrame.showException(ex.getClass().getSimpleName() + ": " + ex.getMessage());
                LOGGER.error("Connect worker interrupted", ex);
            } finally {
                leecherFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }
    }

    /**
     * Todo refactor, this should stay private
     * @return 
     */
    protected IrcConnection getIcrConnection() {
        return icrConnection;
    }

    @Override
    public void disconnected() {
        leecherFrame.setContolsActivated(true);
    }
}
