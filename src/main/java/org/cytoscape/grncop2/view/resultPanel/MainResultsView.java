package org.cytoscape.grncop2.view.resultPanel;

import java.awt.Component;
import java.io.File;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.grncop2.controller.NetworkController;
import org.cytoscape.grncop2.controller.ResultPanelController;
import org.cytoscape.grncop2.controller.actions.LoadResultAction;
import org.cytoscape.grncop2.controller.tasks.ApplyVisualStyleTask;
import org.cytoscape.grncop2.controller.tasks.ExportResultsTask;
import org.cytoscape.grncop2.controller.tasks.ShowDisconnectedNodesTask;
import org.cytoscape.grncop2.controller.tasks.UpdateFiltersTask;
import org.cytoscape.grncop2.controller.tasks.UpdateTimeLagFilterTask;
import org.cytoscape.grncop2.controller.utils.CySwing;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;

/**
 * @license Apache License V2 <http://www.apache.org/licenses/LICENSE-2.0.html>
 * @author Juan José Díaz Montaña
 */
public class MainResultsView extends javax.swing.JPanel implements CytoPanelComponent {
    private final TaskManager taskManager;
    private final ResultPanelController resultPanelController;
    private final NetworkController network;
    private Integer lag = 0;
    private final int maxLag;
    
    /**
     * Creates new form MainResultsView
     */
    public MainResultsView(TaskManager taskManager, ResultPanelController resultPanelController) {
        this.taskManager = taskManager;
        this.resultPanelController = resultPanelController;
        this.network = resultPanelController.getNetwork();
        this.maxLag = this.resultPanelController.getResult().getGRNs()[0].length - 1;
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rcaLabel = new javax.swing.JLabel();
        rcaSlider = new javax.swing.JSlider();
        rcaTextField = new javax.swing.JTextField();
        accuracyLabel = new javax.swing.JLabel();
        accuracySlider = new javax.swing.JSlider();
        accuracyTextField = new javax.swing.JTextField();
        coverageLabel = new javax.swing.JLabel();
        coverageSlider = new javax.swing.JSlider();
        coverageTextField = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        saveResultsButton = new javax.swing.JButton();
        applyFiltersButton = new javax.swing.JButton();
        prevWindowButton = new javax.swing.JButton();
        lagTextField = new javax.swing.JTextField();
        nextWindowButton = new javax.swing.JButton();
        showAllToggleButton = new javax.swing.JToggleButton();
        jSeparator3 = new javax.swing.JSeparator();
        showDisconnectedNodesCheckBox = new javax.swing.JCheckBox();
        closeResultsButton = new javax.swing.JButton();

        rcaLabel.setText("RCA");
        rcaLabel.setToolTipText("Rule Consensus Accuracy (RCA) threhold specifies the minimum proportion of datasets in which a rule must predict well");

        rcaSlider.setToolTipText("Rule Consensus Accuracy (RCA) threhold specifies the minimum proportion of datasets in which a rule must predict well");
        rcaSlider.setValue(95);

        rcaTextField.setText("" + rcaSlider.getValue());
        rcaTextField.setToolTipText("Rule Consensus Accuracy (RCA) threhold specifies the minimum proportion of datasets in which a rule must predict well");
        rcaTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                rcaTextFieldFocusLost(evt);
            }
        });
        rcaTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rcaTextFieldActionPerformed(evt);
            }
        });

        accuracyLabel.setText("Accuracy");
        accuracyLabel.setToolTipText("Accuracy acts as a cut off for rules that do not predict well based on the calculated score");

        accuracySlider.setToolTipText("Accuracy acts as a cut off for rules that do not predict well based on the calculated score");
        accuracySlider.setValue(95);

        accuracyTextField.setText("" + accuracySlider.getValue());
        accuracyTextField.setToolTipText("Accuracy acts as a cut off for rules that do not predict well based on the calculated score");
        accuracyTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                accuracyTextFieldFocusLost(evt);
            }
        });
        accuracyTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                accuracyTextFieldActionPerformed(evt);
            }
        });

        coverageLabel.setText("Coverage");
        coverageLabel.setToolTipText("Sample Coverage Percentage (SCP) specifies the minimum TP (TN) count in relation to the number of samples to avoid rules with high accuracy but a small sample size");

        coverageSlider.setToolTipText("Sample Coverage Percentage (SCP) specifies the minimum TP (TN) count in relation to the number of samples to avoid rules with high accuracy but a small sample size");
        coverageSlider.setValue(95);

        coverageTextField.setText("" + coverageSlider.getValue());
        coverageTextField.setToolTipText("Sample Coverage Percentage (SCP) specifies the minimum TP (TN) count in relation to the number of samples to avoid rules with high accuracy but a small sample size");
        coverageTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                coverageTextFieldFocusLost(evt);
            }
        });
        coverageTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                coverageTextFieldActionPerformed(evt);
            }
        });

        saveResultsButton.setText("Save results");
        saveResultsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveResultsButtonActionPerformed(evt);
            }
        });

        applyFiltersButton.setText("Apply filters");
        applyFiltersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyFiltersButtonActionPerformed(evt);
            }
        });

        prevWindowButton.setText("<");
        prevWindowButton.setEnabled(false);
        prevWindowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prevWindowButtonActionPerformed(evt);
            }
        });

        lagTextField.setText(Integer.toString(lag));
        lagTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lagTextFieldActionPerformed(evt);
            }
        });

        nextWindowButton.setText(">");
        nextWindowButton.setEnabled(lag < maxLag);
        nextWindowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextWindowButtonActionPerformed(evt);
            }
        });

        showAllToggleButton.setText("Show all");
        showAllToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showAllToggleButtonActionPerformed(evt);
            }
        });

        showDisconnectedNodesCheckBox.setText("Show disconnected nodes");
        showDisconnectedNodesCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        showDisconnectedNodesCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showDisconnectedNodesCheckBoxActionPerformed(evt);
            }
        });

        closeResultsButton.setText("Close results");
        closeResultsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeResultsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator3)
                    .addComponent(showDisconnectedNodesCheckBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(closeResultsButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSeparator2)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(rcaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(accuracyLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(coverageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(rcaSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addComponent(accuracySlider, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addComponent(coverageSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addComponent(applyFiltersButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(rcaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addComponent(accuracyTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                                    .addComponent(coverageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(prevWindowButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lagTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nextWindowButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(showAllToggleButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(saveResultsButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(rcaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rcaSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rcaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(accuracyTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(accuracySlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(accuracyLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(coverageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(coverageSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(coverageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(applyFiltersButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(prevWindowButton)
                    .addComponent(nextWindowButton)
                    .addComponent(lagTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(showAllToggleButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(showDisconnectedNodesCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveResultsButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeResultsButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        rcaSlider.addChangeListener(new SliderChangeListener(rcaTextField));
        accuracySlider.addChangeListener(new SliderChangeListener(accuracyTextField));
        coverageSlider.addChangeListener(new SliderChangeListener(coverageTextField));
    }// </editor-fold>//GEN-END:initComponents

    private void saveResultsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveResultsButtonActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save GRNCOP2 results");
        fileChooser.setSelectedFile(new File("grncop2results.grncop2"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("GRNCOP2 file", "grncop2"));
        fileChooser.setCurrentDirectory(LoadResultAction.currentFolder);
        int returnValue = fileChooser.showSaveDialog(CySwing.getDesktopJFrame());
        if (returnValue != 0) {
            return;
        }
        
        LoadResultAction.currentFolder = new File(fileChooser.getSelectedFile().getParent());
        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
        if (!filePath.endsWith(".grncop2")) {
            filePath += ".grncop2";
        }
        taskManager.execute(new TaskIterator(new ExportResultsTask(resultPanelController.getResult(), filePath)));
    }//GEN-LAST:event_saveResultsButtonActionPerformed

    private void accuracyTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_accuracyTextFieldActionPerformed
        accuracySlider.setValue(Integer.parseInt(accuracyTextField.getText()));
    }//GEN-LAST:event_accuracyTextFieldActionPerformed

    private void coverageTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_coverageTextFieldActionPerformed
        coverageSlider.setValue(Integer.parseInt(coverageTextField.getText()));
    }//GEN-LAST:event_coverageTextFieldActionPerformed

    private void accuracyTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_accuracyTextFieldFocusLost
        accuracySlider.setValue(Integer.parseInt(accuracyTextField.getText()));
    }//GEN-LAST:event_accuracyTextFieldFocusLost

    private void coverageTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_coverageTextFieldFocusLost
        coverageSlider.setValue(Integer.parseInt(coverageTextField.getText()));
    }//GEN-LAST:event_coverageTextFieldFocusLost

    private void rcaTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rcaTextFieldActionPerformed
        rcaSlider.setValue(Integer.parseInt(rcaTextField.getText()));
    }//GEN-LAST:event_rcaTextFieldActionPerformed

    private void rcaTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rcaTextFieldFocusLost
        rcaSlider.setValue(Integer.parseInt(rcaTextField.getText()));
    }//GEN-LAST:event_rcaTextFieldFocusLost

    private void applyFiltersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyFiltersButtonActionPerformed
        refreshNetwork();
    }//GEN-LAST:event_applyFiltersButtonActionPerformed

    private void nextWindowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextWindowButtonActionPerformed
        lag++;
        lagTextField.setText(Integer.toString(lag));
        if (lag > 0) {
            prevWindowButton.setEnabled(true);
        }
        if (lag == maxLag) {
            nextWindowButton.setEnabled(false);
        }
        refreshLag();
    }//GEN-LAST:event_nextWindowButtonActionPerformed

    private void lagTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lagTextFieldActionPerformed
        try {
            lag = Integer.parseInt(lagTextField.getText());
        } catch (NumberFormatException e) {
            lag = 0;
        }

        if (lag < 0) {
            lag = 0;
        } else if (lag > maxLag) {
            lag = maxLag;
        }
        refreshLag();
    }//GEN-LAST:event_lagTextFieldActionPerformed

    private void prevWindowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prevWindowButtonActionPerformed
        lag--;
        lagTextField.setText(Integer.toString(lag));
        if (lag == 0) {
            prevWindowButton.setEnabled(false);
        }
        if (lag < maxLag) {
            nextWindowButton.setEnabled(true);
        }
        refreshLag();
    }//GEN-LAST:event_prevWindowButtonActionPerformed

    private void showAllToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showAllToggleButtonActionPerformed
        boolean enableSelectors;
        if  (showAllToggleButton.isSelected()) {
            showAllToggleButton.setText("Show window");
            lag = null;
            enableSelectors = false;
        } else {
            showAllToggleButton.setText("Show all");
            try {
                lag = Integer.parseInt(lagTextField.getText());
            } catch (NumberFormatException e) {
                lag = 0;
            }
            enableSelectors = true;
        }
        prevWindowButton.setEnabled(enableSelectors && lag > 0);
        nextWindowButton.setEnabled(enableSelectors && lag < maxLag);
        lagTextField.setEnabled(enableSelectors);
        refreshLag();
    }//GEN-LAST:event_showAllToggleButtonActionPerformed

    private void showDisconnectedNodesCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showDisconnectedNodesCheckBoxActionPerformed
        taskManager.execute(new TaskIterator(new Task[] {
            new ShowDisconnectedNodesTask(network, showDisconnectedNodesCheckBox.isSelected())
        }));
    }//GEN-LAST:event_showDisconnectedNodesCheckBoxActionPerformed

    private void closeResultsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeResultsButtonActionPerformed
        resultPanelController.dispose();
    }//GEN-LAST:event_closeResultsButtonActionPerformed

    public TaskIterator getRefreshNetworkTasks() {
        TaskIterator tasks = new TaskIterator(new Task[] {
            new UpdateFiltersTask(
                network,
                rcaSlider.getValue() / 100F,
                accuracySlider.getValue() / 100F,
                coverageSlider.getValue() / 100F
            ),
            new ApplyVisualStyleTask(network),
            new ShowDisconnectedNodesTask(network, showDisconnectedNodesCheckBox.isSelected()),
            new UpdateTimeLagFilterTask(network, lag)
        });
        tasks.append(network.getApplyLayoutTask());
        return tasks;
    }
    
    private void refreshNetwork() {
        taskManager.execute(getRefreshNetworkTasks());
    }
    
    private void refreshLag() {
        taskManager.execute(new TaskIterator(new Task[] {
            new UpdateTimeLagFilterTask(network, lag)
        }));
    }
    
    class SliderChangeListener implements ChangeListener {
        private final JTextField textField;
        public SliderChangeListener(JTextField textField) {
            this.textField = textField;
        }
        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider)e.getSource();
            if (!source.getValueIsAdjusting()) {
                textField.setText("" + source.getValue());
            }    
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel accuracyLabel;
    private javax.swing.JSlider accuracySlider;
    private javax.swing.JTextField accuracyTextField;
    private javax.swing.JButton applyFiltersButton;
    private javax.swing.JButton closeResultsButton;
    private javax.swing.JLabel coverageLabel;
    private javax.swing.JSlider coverageSlider;
    private javax.swing.JTextField coverageTextField;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTextField lagTextField;
    private javax.swing.JButton nextWindowButton;
    private javax.swing.JButton prevWindowButton;
    private javax.swing.JLabel rcaLabel;
    private javax.swing.JSlider rcaSlider;
    private javax.swing.JTextField rcaTextField;
    private javax.swing.JButton saveResultsButton;
    private javax.swing.JToggleButton showAllToggleButton;
    private javax.swing.JCheckBox showDisconnectedNodesCheckBox;
    // End of variables declaration//GEN-END:variables

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public CytoPanelName getCytoPanelName() {
         return CytoPanelName.EAST;
    }

    @Override
    public String getTitle() {
        return "GRNCOP2";
    }

    @Override
    public Icon getIcon() {
        return null;
    }
}