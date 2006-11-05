package eu.somatik.botleecher;

/**
 *
 * Created on 7 januari 2005, 23:07
 *
 * @author francisdb
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import org.jdesktop.swingworker.SwingWorker;

import org.jibble.pircbot.*;

public class BotLeecher extends JFrame {
    
    private JButton btnConnect;
    private JButton btnStart;
    private JLabel lblStartAt;
    private JLabel transferLabel;
    private JList lstNicknames;
    private JProgressBar pbTransfer;
    private JSpinner sStartAt;
    private JTextField txtServer;
    private JTextField txtChannel;
    private JTextArea txtLog;
    
    private Timer updater;
    
    private static BotConnection botLeecher;
    
    
    /** Creates new form mainFrame */
    public BotLeecher() {
        botLeecher = new BotConnection();
        initComponents();
        
    }
    
    private void initComponents() {
        this.setTitle("BotLeecher");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        
        btnConnect = new JButton(new ActionConnect(this));
        btnStart = new JButton(new ActionStart(this));
        btnStart.setEnabled(false);
        
        List<String> items = new ArrayList<String>();
        items.add("Connect first");
        lstNicknames = new JList(items.toArray());
        lstNicknames.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        //lstNicknames.setVisibleRowCount(10);
        
        JScrollPane scrollNicks = new JScrollPane(lstNicknames);
        scrollNicks.setPreferredSize(new Dimension(150,500));
        
        pbTransfer = new JProgressBar();
        
        lblStartAt = new JLabel();
        lblStartAt.setText("Start at nr:");
        sStartAt = new JSpinner();
        sStartAt.setValue(new Integer(1));
        txtServer = new JTextField(20);
        txtServer.setText("irc.efnet.net");
        txtChannel = new JTextField(15);
        txtChannel.setText("#dnbmp3");
        txtLog = new JTextArea("Connect first");
        txtLog.setEditable(false);
        JScrollPane scrollLog = new JScrollPane(txtLog);
        //AbstractDocument pDoc=(AbstractDocument)txtLog.getDocument();
        //pDoc.setDocumentFilter(new DocumentSizeFilter(1000));
        
        this.getContentPane().setLayout(new BorderLayout());
        
        JPanel panelButtons = new JPanel(new FlowLayout());
        
        panelButtons.add(txtServer);
        panelButtons.add(txtChannel);
        panelButtons.add(btnConnect);
        panelButtons.add(lblStartAt);
        panelButtons.add(sStartAt);
        panelButtons.add(btnStart);
        
        this.getContentPane().add(panelButtons,BorderLayout.NORTH);
        
        pbTransfer.setStringPainted(true);
        getContentPane().add(pbTransfer, BorderLayout.SOUTH);
        
        transferLabel = new JLabel();
        transferLabel.setText("no transfer");
        transferLabel.setPreferredSize(new Dimension(800,100));
        transferLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        transferLabel.setBorder(new javax.swing.border.EtchedBorder());
        
        JSplitPane splitPaneStatusLog = new JSplitPane(JSplitPane.VERTICAL_SPLIT,transferLabel,scrollLog);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,scrollNicks,splitPaneStatusLog);
        this.getContentPane().add(splitPane, BorderLayout.CENTER);
        
        PrintStream oldStream = System.out;
        PrintStream aPrintStream  = new PrintStream(new FilteredStream(oldStream));
        System.setOut(aPrintStream); // catches System.out messages
        System.setErr(aPrintStream); // catches error messages
        
        this.pack();
    }
    
    
    private void connect(){
        new ConnectWorker().execute();
    }
    
    private void start(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setDialogTitle("Where do you want to save the downloaded files?");
        fileChooser.showDialog(this,"select");
        
        botLeecher.setSavePath(fileChooser.getSelectedFile().getPath()+File.separator);
        lstNicknames.setEnabled(false);
        btnStart.setEnabled(false);
        botLeecher.setCounter(Integer.parseInt(sStartAt.getValue().toString()));
        
        User selectedUser = (User)lstNicknames.getSelectedValue();
        botLeecher.leechBot(selectedUser.getNick());
        updater = new Timer(100,new UpdateListerner());
        updater.setCoalesce(true);
        updater.start();
    }
    
    private void stop(){
        updater.stop();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
        try{
            UIManager.LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();
            for (int i=0;i<lookAndFeels.length;i++){
                System.out.println("Available lookAndFeel: "+lookAndFeels[i].getClassName());
            }
            System.out.println("Setting lookAndFeel: "+UIManager.getSystemLookAndFeelClassName());
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception ex){
            ex.printStackTrace();
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new BotLeecher().setVisible(true);
            }
        });
    }
    
    private class ConnectWorker extends SwingWorker<User[],String>{
        private final String server;
        private final String channel;
        public ConnectWorker(){
            server = txtServer.getText();
            channel = txtChannel.getText();
            transferLabel.setText("Connecting to " + server);
        }
        
        protected User[] doInBackground() throws Exception {
            botLeecher.connect(server);
            publish("Joining channel " + channel);
            botLeecher.joinChannel(channel);
             publish("Loading channel list ");
            while (botLeecher.getChannels().length == 0){
                Thread.sleep(100);
            }
             publish("Loading user list ");
            while (botLeecher.getUsers(channel).length <= 1){
                Thread.sleep(100);
            }
            return botLeecher.getUsers(channel);
        }
        
        protected void process(List<String> messages) {
           for(String msg:messages){
            transferLabel.setText(msg);
           }
        }
        
        protected void done() {
            try {
                lstNicknames.setListData(get());
                btnConnect.setEnabled(false);
                txtChannel.setEnabled(false);
                txtServer.setEnabled(false);
            } catch (ExecutionException ex) {
                JOptionPane.showMessageDialog(BotLeecher.this,ex.getCause().getMessage(),"error",JOptionPane.ERROR_MESSAGE);
                ex.getCause().printStackTrace();
            } catch (InterruptedException ex) {
                JOptionPane.showMessageDialog(BotLeecher.this,ex.getMessage(),"error",JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
            lstNicknames.addListSelectionListener(new myListSelectionListener());
        }
        
    }
    
    
    private  class UpdateListerner implements ActionListener {
        private final NumberFormat formatter;
        public UpdateListerner(){
            formatter = NumberFormat.getInstance();
            formatter.setMaximumFractionDigits(0);
        }
        
        public void actionPerformed(ActionEvent e) {
            if (botLeecher.getCurrentTransfer()!=null){
                sStartAt.setValue(new Integer(botLeecher.getCounter()));
                pbTransfer.setMaximum((int)botLeecher.getCurrentTransfer().getSize());
                pbTransfer.setValue((int)botLeecher.getCurrentTransfer().getProgress());
                pbTransfer.setString((int)botLeecher.getCurrentTransfer().getProgressPercentage()+" %");
                transferLabel.setText("<html>"+botLeecher.getCurrentTransfer().getFile().getName()+"<br>"
                        + (int)(botLeecher.getCurrentTransfer().getTransferRate()/1024)+"Kbps <br>"
                        + "Last notice: "+botLeecher.getLastMessage()
                        +"</html>");
                
                
                
                String percentage = formatter.format(botLeecher.getCurrentTransfer().getProgressPercentage());
                setTitle(percentage+"% "+ botLeecher.getCurrentTransfer().getFile().getName());
            } else {
                pbTransfer.setValue(0);
                transferLabel.setText("no transfer");
            }
        }
        
        
    }
    
    private class UserComparator implements Comparator<User>{
        public int compare(User o1, User o2) {
            return o1.getNick().compareToIgnoreCase(o2.getNick());
        }
    }
    
    private class myListSelectionListener implements ListSelectionListener{
        public void valueChanged(ListSelectionEvent e) {
            if(lstNicknames.getSelectedIndices().length == 1){
                btnStart.setEnabled(true);
            }else{
                btnStart.setEnabled(false);
            }
            
        }
    }
    
    
    private class ActionConnect extends AbstractAction{
        public ActionConnect(BotLeecher parent) {
            super("Connect");
        }
        
        public void actionPerformed(ActionEvent e) {
            connect();
        }
    }
    
    private class ActionStart extends AbstractAction{
        private BotLeecher parent;
        
        public ActionStart(BotLeecher parent) {
            super("Start");
            this.parent = parent;
        }
        
        public void actionPerformed(ActionEvent e) {
            start();
        }
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
        
        public void write(byte b[]) throws IOException {
            String aString = new String(b);
            txtLog.append(aString);
            txtLog.setCaretPosition( txtLog.getDocument().getLength() );
            oldStream.write(b);
        }
        
        public void write(byte b[], int off, int len) throws IOException {
            String aString = new String(b , off , len);
            txtLog.append(aString);
            txtLog.setCaretPosition( txtLog.getDocument().getLength() );
            oldStream.write(b,off,len);
        }
        
    }
    
}




