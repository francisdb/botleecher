/*
 * botPanel.java
 *
 * Created on February 22, 2007, 10:15 PM
 */

package eu.somatik.botleecher.gui;

import eu.somatik.botleecher.*;
import eu.somatik.botleecher.model.Pack;
import eu.somatik.botleecher.model.PackStatus;
import eu.somatik.botleecher.model.PackTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.jibble.pircbot.User;

/**
 *
 * @author  francisdb
 */
public class BotPanel extends javax.swing.JPanel{
    
    private final User user;
    private final BotLeecher botLeecher;
    private Timer updater;
    
    /** Creates new form botPanel
     * @param botLeecher
     * @param user
     */
    public BotPanel( BotLeecher botLeecher, final User user) {
        initComponents();
        this.user = user;
        this.botLeecher = botLeecher;
        this.botLeecher.addListener(new BotListener(){
            @Override
            public void packListLoaded(final List<Pack> packList) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        packTable.setModel(new PackTableModel(packList));
                    }
                });  
            }
        });
        this.packSpinner.setValue(2);
        
        updater = new Timer(500, new UpdateListerner());
        updater.setCoalesce(true);
        updater.start();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        botScrollPane = new javax.swing.JScrollPane();
        botTextPane = new javax.swing.JTextPane();
        packSpinner = new javax.swing.JSpinner();
        startButton = new javax.swing.JButton();
        transferStatusBar = new javax.swing.JProgressBar();
        jScrollPane1 = new javax.swing.JScrollPane();
        packTable = new javax.swing.JTable();
        refreshButton = new javax.swing.JButton();

        botScrollPane.setViewportView(botTextPane);

        packSpinner.setValue(1);

        startButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/control_play.png"))); // NOI18N
        startButton.setText("Start");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        packTable.setAutoCreateRowSorter(true);
        packTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        packTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                packTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(packTable);

        refreshButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/arrow_refresh.png"))); // NOI18N
        refreshButton.setText("Refresh");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(refreshButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(startButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(packSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(transferStatusBar, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                    .addComponent(botScrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startButton)
                    .addComponent(packSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(refreshButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(botScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(transferStatusBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startButtonActionPerformed
    startButton.setEnabled(false);
    
    botLeecher.setCounter((Integer)packSpinner.getValue());
    botLeecher.startLeeching();
}//GEN-LAST:event_startButtonActionPerformed

private void packTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_packTableMouseClicked
    if(evt.getClickCount() == 2){
        int row = packTable.rowAtPoint(evt.getPoint());
        int modelRow = packTable.convertRowIndexToModel(row);
        PackTableModel model = (PackTableModel)packTable.getModel();
        Pack pack = model.getPack(modelRow);
        if(pack.getStatus() == PackStatus.AVAILABLE){
            botLeecher.queuePack(pack);
        }else{
            System.err.println("Pack already queued or downloaded");
        }
    }
}//GEN-LAST:event_packTableMouseClicked

private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
    botLeecher.requestPackList();
}//GEN-LAST:event_refreshButtonActionPerformed

private class UpdateListerner implements ActionListener {
    private final NumberFormat formatter;
    
    public UpdateListerner() {
        formatter = NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(0);
    }
    
        @Override
    public void actionPerformed(ActionEvent e) {
        if (botLeecher.getCurrentTransfer() != null) {
            packSpinner.setValue(botLeecher.getCounter());
            transferStatusBar.setMaximum((int) botLeecher.getCurrentTransfer()
                    .getSize());
            transferStatusBar.setValue((int) botLeecher.getCurrentTransfer()
                    .getProgress());
            transferStatusBar.setString((int) botLeecher.getCurrentTransfer()
                    .getProgressPercentage() +
                    " %");
            botTextPane.setText(
                    botLeecher.getDescription()+"\n"+
                    botLeecher.getCurrentTransfer().getFile().getName() +
                    "\n" +
                    (int) (botLeecher.getCurrentTransfer().getTransferRate() / 1024) +
                    "Kbps \n" + "Last notice: " +
                    botLeecher.getLastNotice());
            
            //String percentage = formatter.format(botLeecher.getCurrentTransfer()
            //        .getProgressPercentage());
            //                setTitle(percentage + "% " +
            //                    botLeecher.getCurrentTransfer().getFile().getName());
        } else {
            transferStatusBar.setValue(0);
            botTextPane.setText(botLeecher.getDescription()+"\n"+"no transfer");
        }
    }
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane botScrollPane;
    private javax.swing.JTextPane botTextPane;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner packSpinner;
    private javax.swing.JTable packTable;
    private javax.swing.JButton refreshButton;
    private javax.swing.JButton startButton;
    private javax.swing.JProgressBar transferStatusBar;
    // End of variables declaration//GEN-END:variables
    
}
