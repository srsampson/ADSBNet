/**
 * GUI.java
 */
package adsbnet;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;

/**
 * A Class to display a GUI window.
 *
 * @author Steve Sampson, January 2020
 */
public final class GUI extends JFrame {

    private static final long TIMEOUT = 1000L;		// 1 second update on PU display
    private static final long RATE = 500L;              // .5 second
    //
    private final ZuluMillis zulu = new ZuluMillis();
    //
    private final Timer timer1;
    private final TimerTask task1;
    private final Timer timer2;
    private final TimerTask task2;
    //
    private final KineticParse process;
    //
    private long messageType1;
    private long messageType2;
    private long messageType3;
    private long messageType4;
    private long messageType5;
    private long messageType6;
    private long messageType7;
    //
    private long trackCount;

    public GUI(KineticParse c) {
        this.process = c;
        
        initComponents();
        
        trackCount =
                messageType1 =
                messageType2 =
                messageType3 =
                messageType4 =
                messageType5 =
                messageType6 =
                messageType7 = 0L;
        
        timer1 = new Timer();
        task1 = new UpdateTable();
        timer1.scheduleAtFixedRate(task1, 0L, TIMEOUT);

        task2 = new UpdateCounters();
        timer2 = new Timer();
        timer2.scheduleAtFixedRate(task2, 10L, RATE);
    }

    public void close() {
        timer1.cancel();
        timer2.cancel();
    }
    
    public void updateCountersDisplay() {
        type1Count.setText((new StringBuilder()).append("").append(messageType1).toString());
        type2Count.setText((new StringBuilder()).append("").append(messageType2).toString());
        type3Count.setText((new StringBuilder()).append("").append(messageType3).toString());
        type4Count.setText((new StringBuilder()).append("").append(messageType4).toString());
        type5Count.setText((new StringBuilder()).append("").append(messageType5).toString());
        type6Count.setText((new StringBuilder()).append("").append(messageType6).toString());
        type7count.setText((new StringBuilder()).append("").append(messageType7).toString());
        trackCounter.setText((new StringBuilder()).append("").append(trackCount).toString());
    }

    public void incType1() {
        messageType1++;
    }

    public void incType2() {
        messageType2++;
    }

    public void incType3() {
        messageType3++;
    }

    public void incType4() {
        messageType4++;
    }

    public void incType5() {
        messageType5++;
    }

    public void incType6() {
        messageType6++;
    }

    public void incType7() {
        messageType7++;
    }
    
    public void setTrackCount(long val) {
        trackCount = val;
    }

    private class UpdateCounters extends TimerTask {

        @Override
        public void run() {
            updateCountersDisplay();
            Thread.yield();
        }
    }

    class UpdateTable extends TimerTask {

        @Override
        public void run() {
            long now;

            /*
             * Due to values being deleted in another thread, the collection
             * count may become bogus.  Not to worry, just ignore the
             * exceptions.
             */

            now = zulu.getUTCTime() / 1000L;   // seconds

            try {
                CopyOnWriteArrayList<HeartBeat> sites = process.getBeatHashTable();
                int row = 0;

                DefaultTableModel ttm = (DefaultTableModel) jTable1.getModel();
                ttm.setRowCount(process.getBeatReportsSize());

                for (HeartBeat site : sites) {
                    ttm.setValueAt(" " + site.getStationName(), row, 0);
                    ttm.setValueAt(site.getBeatCount(), row, 1);
                    ttm.setValueAt(site.getTrackCount(), row, 2);
                    ttm.setValueAt("  " + site.getStationIP(), row, 3);

                    // Get time in seconds since last update
                    long time = site.getUpdateTime() / 1000L;      // sec
                    ttm.setValueAt((int) (now - time), row, 4);
                    time = site.getDiffTime() / 1000L;  // sec
                    ttm.setValueAt(time, row, 5);

                    row++;
                }

                ttm.fireTableDataChanged();     // keep sorted
            } catch (Exception e) {
            }

            Thread.yield();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        clearCounterButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        type1Count = new javax.swing.JLabel();
        type2Count = new javax.swing.JLabel();
        type3Count = new javax.swing.JLabel();
        type4Count = new javax.swing.JLabel();
        type5Count = new javax.swing.JLabel();
        type6Count = new javax.swing.JLabel();
        type7count = new javax.swing.JLabel();
        trackCounter = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        airborne = new javax.swing.JLabel();
        velocity = new javax.swing.JLabel();
        altitude = new javax.swing.JLabel();
        squawk = new javax.swing.JLabel();
        tracks = new javax.swing.JLabel();
        air2air = new javax.swing.JLabel();
        surface = new javax.swing.JLabel();
        callsign = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ADSNet 1.90");
        setBounds(new java.awt.Rectangle(300, 300, 0, 0));
        setResizable(false);

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel13.setText("Clear Counters");

        clearCounterButton.setText("RESET");
        clearCounterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearCounterButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(clearCounterButton)
                .addGap(28, 28, 28))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(clearCounterButton))
                .addGap(29, 29, 29))
        );

        type1Count.setBackground(new java.awt.Color(255, 255, 255));
        type1Count.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        type1Count.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        type1Count.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        type1Count.setOpaque(true);

        type2Count.setBackground(new java.awt.Color(255, 255, 255));
        type2Count.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        type2Count.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        type2Count.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        type2Count.setOpaque(true);

        type3Count.setBackground(new java.awt.Color(255, 255, 255));
        type3Count.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        type3Count.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        type3Count.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        type3Count.setOpaque(true);

        type4Count.setBackground(new java.awt.Color(255, 255, 255));
        type4Count.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        type4Count.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        type4Count.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        type4Count.setOpaque(true);

        type5Count.setBackground(new java.awt.Color(255, 255, 255));
        type5Count.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        type5Count.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        type5Count.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        type5Count.setOpaque(true);

        type6Count.setBackground(new java.awt.Color(255, 255, 255));
        type6Count.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        type6Count.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        type6Count.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        type6Count.setOpaque(true);

        type7count.setBackground(new java.awt.Color(255, 255, 255));
        type7count.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        type7count.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        type7count.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        type7count.setOpaque(true);

        trackCounter.setBackground(new java.awt.Color(255, 255, 255));
        trackCounter.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        trackCounter.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        trackCounter.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        trackCounter.setOpaque(true);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(type1Count, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(type2Count, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(type3Count, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(type4Count, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(type5Count, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(type6Count, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(type7count, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(trackCounter, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(type1Count, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(type2Count, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(type3Count, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(type4Count, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(type5Count, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(type6Count, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(type7count, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(trackCounter, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(42, Short.MAX_VALUE))
        );

        airborne.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        airborne.setText("Airborne");

        velocity.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        velocity.setText("Velocity");

        altitude.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        altitude.setText("Altitude");

        squawk.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        squawk.setText("Squawk");

        tracks.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        tracks.setText("Tracks");

        air2air.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        air2air.setText("AirToAir");

        surface.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        surface.setText("Surface");

        callsign.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        callsign.setText("Callsign");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tracks)
                    .addComponent(airborne)
                    .addComponent(velocity)
                    .addComponent(air2air)
                    .addComponent(surface)
                    .addComponent(callsign)
                    .addComponent(squawk)
                    .addComponent(altitude))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(callsign)
                .addGap(18, 18, 18)
                .addComponent(surface)
                .addGap(18, 18, 18)
                .addComponent(airborne)
                .addGap(18, 18, 18)
                .addComponent(velocity)
                .addGap(18, 18, 18)
                .addComponent(altitude)
                .addGap(18, 18, 18)
                .addComponent(squawk)
                .addGap(18, 18, 18)
                .addComponent(air2air)
                .addGap(18, 18, 18)
                .addComponent(tracks)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        airborne.getAccessibleContext().setAccessibleParent(jPanel1);
        velocity.getAccessibleContext().setAccessibleParent(jPanel1);
        altitude.getAccessibleContext().setAccessibleParent(jPanel1);
        squawk.getAccessibleContext().setAccessibleParent(jPanel1);
        surface.getAccessibleContext().setAccessibleParent(jPanel1);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel3.getAccessibleContext().setAccessibleParent(jPanel4);
        jPanel2.getAccessibleContext().setAccessibleParent(jPanel4);
        jPanel1.getAccessibleContext().setAccessibleParent(jPanel4);

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null}
            },
            new String [] {
                "Station", "Beats Heard", "System Tracks", "IP Address", "Last Heard", "Time Diff"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Long.class, java.lang.Long.class, java.lang.String.class, java.lang.Integer.class, java.lang.Long.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        jTable1.setAutoscrolls(false);
        jTable1.setGridColor(new java.awt.Color(204, 204, 204));
        jTable1.setRowSelectionAllowed(false);
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(510, Short.MAX_VALUE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(235, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 506, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void clearCounterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearCounterButtonActionPerformed
        messageType1 =
                messageType2 =
                messageType3 =
                messageType4 =
                messageType5 =
                messageType6 =
                messageType7 = 0L;

        updateCountersDisplay();
    }//GEN-LAST:event_clearCounterButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel air2air;
    private javax.swing.JLabel airborne;
    private javax.swing.JLabel altitude;
    private javax.swing.JLabel callsign;
    private javax.swing.JButton clearCounterButton;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel squawk;
    private javax.swing.JLabel surface;
    private javax.swing.JLabel trackCounter;
    private javax.swing.JLabel tracks;
    private javax.swing.JLabel type1Count;
    private javax.swing.JLabel type2Count;
    private javax.swing.JLabel type3Count;
    private javax.swing.JLabel type4Count;
    private javax.swing.JLabel type5Count;
    private javax.swing.JLabel type6Count;
    private javax.swing.JLabel type7count;
    private javax.swing.JLabel velocity;
    // End of variables declaration//GEN-END:variables
}
