/*
 * BotMediator.java
 *
 * Created on February 22, 2007, 10:21 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package eu.somatik.botleecher.gui;

import eu.somatik.botleecher.*;
import java.awt.Cursor;
import org.jibble.pircbot.User;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;


/**
 *
 * @author francisdb
 */
public class BotMediator implements IrcConnectionListener {
    private IrcConnection icrConnection;
    private LeecherFrame leecherFrame;
    
    /** Creates a new instance of BotMediator */
    public BotMediator() {
        icrConnection = new eu.somatik.botleecher.IrcConnection();
        icrConnection.addBotListener(this);
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                leecherFrame = new LeecherFrame(BotMediator.this);
                // center
                leecherFrame.setLocationRelativeTo(null);
                leecherFrame.setVisible(true);
            }
        });
        
        PrintStream oldStream = System.out;
        PrintStream aPrintStream  = new PrintStream(new FilteredStream(oldStream));
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
            ex.printStackTrace();
        }
        
        new BotMediator();
    }
    
    public void userListLoaded(final String channel, final  User[] users) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                leecherFrame.setUsers(users);
            }
        });
    }    
    
    private void writeToLog(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                leecherFrame.writeToLog(message);
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
        
        protected String doInBackground() throws Exception {
            icrConnection.connect(server);
            icrConnection.joinChannel(channel);
            return null;
        }
        
        protected void done() {
            try {
                get();
            } catch (ExecutionException ex) {
                leecherFrame.setContolsActivated(true);
                leecherFrame.showException(ex.getCause().getClass().getSimpleName()+": "+ex.getCause().getMessage());
                ex.getCause().printStackTrace();
            } catch (InterruptedException ex) {
                leecherFrame.setContolsActivated(true);
                leecherFrame.showException(ex.getClass().getSimpleName()+": "+ex.getMessage());
                ex.printStackTrace();
            }finally{
                leecherFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }
    }
    
    /**
     * Todo refactor, this should stay private
     */
    protected IrcConnection getIcrConnection(){
        return icrConnection;
    }
    
    /**
     * @author francisdb
     *
     * Class that will replace the System.out
     * Writes to the old stream and to our debug window
     */
    private class FilteredStream extends FilterOutputStream {
        private PrintStream oldStream;
        
        public FilteredStream(PrintStream oldStream) {
            super(new ByteArrayOutputStream());
            this.oldStream = oldStream;
        }
        
        public void write(byte[] b) throws IOException {
            String aString = new String(b);
            writeToLog(aString);
            oldStream.write(b);
        }
        
        public void write(byte[] b, int off, int len) throws IOException {
            String aString = new String(b, off, len);
            writeToLog(aString);
            oldStream.write(b, off, len);
        }
    }

    @Override
    public void disconnected() {
        leecherFrame.setContolsActivated(true);
    }
}
