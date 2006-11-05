package eu.somatik.botleecher;

/*
 * mainFrame.java
 *
 * Created on 7 januari 2005, 23:07
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
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
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import org.jibble.pircbot.*;


/**
 *
 * @author  fdb
 */
public class BotLeecher extends JFrame {
    
    private JButton btnConnect;
    private JButton btnStart;
    private JLabel lblStartAt;
    private JLabel lblTrasnfer;
    private JList lstNicknames;
    private JProgressBar pbTransfer;
    private JSpinner sStartAt;
    private JTextField txtServer;
    private JTextField txtChannel;
    private JTextArea txtLog;

    
    private static BotConnection botLeecher;
    
    
    /** Creates new form mainFrame */
    public BotLeecher() {        
        botLeecher = new BotConnection();
        initComponents();
    }
    
    private void initComponents() {//GEN-BEGIN:initComponents
        this.setTitle("BotLeecher (somatik.be)");
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
        //txtServer.setText("204.92.73.10");
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

        lblTrasnfer = new JLabel();
        lblTrasnfer.setText("no transfer");
        lblTrasnfer.setPreferredSize(new Dimension(800,100));
        lblTrasnfer.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblTrasnfer.setBorder(new javax.swing.border.EtchedBorder());
        
        JSplitPane splitPaneStatusLog = new JSplitPane(JSplitPane.VERTICAL_SPLIT,lblTrasnfer,scrollLog);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,scrollNicks,splitPaneStatusLog);
        this.getContentPane().add(splitPane, BorderLayout.CENTER);
       
        PrintStream oldStream = System.out;
		PrintStream aPrintStream  = new PrintStream(new FilteredStream(oldStream));
	    System.setOut(aPrintStream); // catches System.out messages
        System.setErr(aPrintStream); // catches error messages
        
        this.pack();
    }
   
    
    private class ActionConnect extends AbstractAction{
        BotLeecher parent;
        public ActionConnect(BotLeecher parent) {
            super("Connect");
            this.parent=parent;
        }
        
	    public void actionPerformed(ActionEvent e) {
            try{
                botLeecher.connect(txtServer.getText());
                botLeecher.joinChannel(txtChannel.getText());
                while (botLeecher.getChannels().length==0)
                    Thread.sleep(100);
                //wait for nicklist
                Thread.sleep(500);
                btnConnect.setEnabled(false);
                txtChannel.setEnabled(false);
                txtServer.setEnabled(false);
            }catch(Exception ex){
                JOptionPane.showMessageDialog(parent,ex.getLocalizedMessage(),"error",JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
            User[] users = botLeecher.getUsers(txtChannel.getText());
            Arrays.sort(users,new UserComparator());
            lstNicknames.setListData(users);  
            lstNicknames.addListSelectionListener(new myListSelectionListener());
	    }
    }
    
    private class ActionStart extends AbstractAction{
        private BotLeecher parent;
    
        public ActionStart(BotLeecher parent) {
            super("Start");
            this.parent = parent;
        }
        
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setMultiSelectionEnabled(false);
            fileChooser.setDialogTitle("Where do you want to save the downloaded files?");
            fileChooser.showDialog(parent,"select");
            
            botLeecher.setSavePath(fileChooser.getSelectedFile().getPath()+File.separator);
            lstNicknames.setEnabled(false);
            btnStart.setEnabled(false);
            botLeecher.setCounter(Integer.parseInt(sStartAt.getValue().toString()));
            
            User selectedUser = (User)lstNicknames.getSelectedValue();
            botLeecher.leechBot(selectedUser.getNick());
            new UpdateThread();            
        }
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

        new BotLeecher().setVisible(true);
    }
    
    
    private  class UpdateThread implements Runnable {
        private boolean running;
        private Thread myThread;
        
        public UpdateThread(){
            running = true;
            myThread=new Thread(this);
            myThread.start();
        }
        
        public void run() {
            //System.out.println("de run");
            while (running == true){
                if (botLeecher.getCurrentTransfer()!=null){
                    sStartAt.setValue(new Integer(botLeecher.getCounter()));
                    pbTransfer.setMaximum((int)botLeecher.getCurrentTransfer().getSize());
                    pbTransfer.setValue((int)botLeecher.getCurrentTransfer().getProgress());
                    pbTransfer.setString((int)botLeecher.getCurrentTransfer().getProgressPercentage()+" %");
                    lblTrasnfer.setText("<html>"+botLeecher.getCurrentTransfer().getFile().getName()+"<br>"
                            + (int)(botLeecher.getCurrentTransfer().getTransferRate()/1024)+"Kbps <br>"
                            + "Last notice: "+botLeecher.getLastMessage()
                            +"</html>");
                    
                    NumberFormat formatter = NumberFormat.getInstance();
                    formatter.setMaximumFractionDigits(0);
                    String percentage = formatter.format(botLeecher.getCurrentTransfer().getProgressPercentage());
                    setTitle(percentage+"% "+ botLeecher.getCurrentTransfer().getFile().getName());
                } else {
                    pbTransfer.setValue(0);
                    lblTrasnfer.setText("no transfer");
                }
                try{
                    Thread.sleep(100);
                } catch (Exception e){
                    System.out.println(e);
                }
            }
        }
        
        public void stop(){
            running = false;
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
	
    /**
	 * @author francis
	 *
	 * Class that will replace the System.out
	 * Writes to the old stream and to our debug window
	 */
    class FilteredStream extends FilterOutputStream {
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
    
    public class DocumentSizeFilter extends DocumentFilter {
        int maxCharacters;
        boolean DEBUG = false;

        public DocumentSizeFilter(int maxChars) {
            maxCharacters = maxChars;
        }

        public void insertString(FilterBypass fb, int offs,
                                 String str, javax.swing.text.AttributeSet a)
            throws BadLocationException {
            if (DEBUG) {
                System.out.println("in DocumentSizeFilter's insertString method");
            }

            //This rejects the entire insertion if it would make
            //the contents too long. Another option would be
            //to truncate the inserted string so the contents
            //would be exactly maxCharacters in length.
            if ((fb.getDocument().getLength() + str.length()) <= maxCharacters)
                super.insertString(fb, offs, str, a);
            else
                Toolkit.getDefaultToolkit().beep();
        }
        
        public void replace(FilterBypass fb, int offs,
                            int length, 
                            String str, AttributeSet a)
            throws BadLocationException {
            if (DEBUG) {
                System.out.println("in DocumentSizeFilter's replace method");
            }
            //This rejects the entire replacement if it would make
            //the contents too long. Another option would be
            //to truncate the replacement string so the contents
            //would be exactly maxCharacters in length.
            if ((fb.getDocument().getLength() + str.length()
                 - length) <= maxCharacters)
                super.replace(fb, offs, length, str, a);
            else
                Toolkit.getDefaultToolkit().beep();
        }

    }

}




