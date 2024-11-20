/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.mortuary.gui;


import java.awt.event.ActionListener;
import java.util.EventListener;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.border.EmptyBorder;
import javax.swing.event.EventListenerList;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.isf.generaldata.MessageBundle;
import org.isf.menu.manager.Context;
import org.isf.mortuary.manager.MortuaryBrowserManager;
import org.isf.mortuary.model.Mortuary;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.gui.OHServiceExceptionUtil;
import org.isf.utils.jobjects.MessageDialog;
import org.isf.utils.jobjects.VoLimitedTextField;
import org.isf.ward.gui.WardEdit;

public class MortuaryEdit extends JDialog {
    private static final long serialVersionUID = 1L;
    private EventListenerList mortuaryListeners = new EventListenerList();

    public interface MortuaryListener extends EventListener {

        void mortuaryUpdated(AWTEvent e);

        void mortuaryInserted(AWTEvent e);
    }

    public void addMortuaryListener(MortuaryListener l) {
        mortuaryListeners.add(MortuaryListener.class, l);
    }

    public void removeMortuaryListener(MortuaryListener listener) {
        mortuaryListeners.add(MortuaryListener.class, listener);
    }

    private void fireMortuaryInserted() {
        AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

            private static final long serialVersionUID = 1L;
        };

        EventListener[] listeners = mortuaryListeners.getListeners(org.isf.mortuary.gui.MortuaryEdit.MortuaryListener.class);
        for (EventListener listener : listeners) {
            ((org.isf.mortuary.gui.MortuaryEdit.MortuaryListener) listener).mortuaryInserted(event);
        }
    }

    private void fireMortuaryUpdated() {
        AWTEvent event = new AWTEvent(new Object(), AWTEvent.RESERVED_ID_MAX + 1) {

            private static final long serialVersionUID = 1L;
        };

        EventListener[] listeners = mortuaryListeners.getListeners(org.isf.mortuary.gui.MortuaryEdit.MortuaryListener.class);
        for (EventListener listener : listeners) {
            ((org.isf.mortuary.gui.MortuaryEdit.MortuaryListener) listener).mortuaryUpdated(event);
        }
    }

    private MortuaryBrowserManager mortuaryBrowserManager = Context.getApplicationContext().getBean(MortuaryBrowserManager.class);

    private JPanel jContentPane;
    private JPanel dataPanel;
    private JPanel buttonPanel;
    private JButton cancelButton;
    private JButton okButton;
    private JTextField descriptionTextField;
    private JTextField codeTextField;
    private JTextField maxDTextField;
    private JTextField minDTextField;
    private Mortuary mortuary;
    private boolean insert;
    private String code;
    private String desc;
    private int maxD;
    private int minD;


    /**
     * This is the default constructor; we pass the parent frame
     * (because it is a jdialog), the arraylist and the selected
     * row because we need to update them
     */
    public MortuaryEdit(JFrame parent, Mortuary old, boolean inserting) {
        super(parent, true);
        insert = inserting;
        mortuary = old;        //operation will be used for every operation
        initialize();
    }

    /**
     * This method initializes this
     */
    private void initialize() {
        this.setContentPane(getJContentPane());
        if (insert) {
            this.setTitle(MessageBundle.getMessage("angal.mortuary.newmortuary.title"));
        } else {
            this.setTitle(MessageBundle.getMessage("angal.mortuary.editmortuary.title"));
        }
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * This method initializes jContentPane
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getDataPanel(), BorderLayout.CENTER);
            jContentPane.add(getButtonPanel(), BorderLayout.SOUTH);
        }
        return jContentPane;
    }

    /**
     * This method initializes dataPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getDataPanel() {
        if (dataPanel == null) {
            dataPanel = new JPanel();
            GridBagLayout gblDataPanel = new GridBagLayout();
            gblDataPanel.columnWeights = new double[] { 0.0, 1.0 };
            dataPanel.setLayout(gblDataPanel);
            JLabel codeLabel = new JLabel(MessageBundle.getMessage("angal.common.codestar"));
            GridBagConstraints gbcCodeLabel = new GridBagConstraints();
            gbcCodeLabel.anchor = GridBagConstraints.WEST;
            gbcCodeLabel.insets = new Insets(0, 0, 5, 5);
            gbcCodeLabel.gridx = 0;
            gbcCodeLabel.gridy = 0;
            dataPanel.add(codeLabel, gbcCodeLabel);
            GridBagConstraints gbcCodeTextField = new GridBagConstraints();
            gbcCodeTextField.fill = GridBagConstraints.HORIZONTAL;
            gbcCodeTextField.insets = new Insets(0, 0, 5, 0);
            gbcCodeTextField.gridx = 1;
            gbcCodeTextField.gridy = 0;
            dataPanel.add(getCodeTextField(), gbcCodeTextField);

            JLabel descLabel = new JLabel(MessageBundle.getMessage("angal.mortuary.nameedit"));
            GridBagConstraints gbcDescLabel = new GridBagConstraints();
            gbcDescLabel.anchor = GridBagConstraints.WEST;
            gbcDescLabel.insets = new Insets(0, 0, 5, 5);
            gbcDescLabel.gridx = 0;
            gbcDescLabel.gridy = 1;
            dataPanel.add(descLabel, gbcDescLabel);
            GridBagConstraints gbcDescriptionTextField = new GridBagConstraints();
            gbcDescriptionTextField.fill = GridBagConstraints.HORIZONTAL;
            gbcDescriptionTextField.insets = new Insets(0, 0, 5, 0);
            gbcDescriptionTextField.gridx = 1;
            gbcDescriptionTextField.gridy = 1;
            dataPanel.add(getDescriptionTextField(), gbcDescriptionTextField);

            GridBagConstraints gbcMinDTextField = new GridBagConstraints();
            gbcMinDTextField.insets = new Insets(0, 0, 5, 0);
            gbcMinDTextField.gridx = 1;
            gbcMinDTextField.gridy = 2;
            dataPanel.add(getMinDTextField(), gbcMinDTextField);
            JLabel minDLabel = new JLabel(MessageBundle.getMessage("angal.mortuary.mindays.txt"));
            GridBagConstraints gbcMinDLabel = new GridBagConstraints();
            gbcMinDLabel.anchor = GridBagConstraints.WEST;
            gbcMinDLabel.insets = new Insets(0, 0, 5, 5);
            gbcMinDLabel.gridx = 0;
            gbcMinDLabel.gridy = 2;
            dataPanel.add(minDLabel, gbcMinDLabel);

            JLabel maxDLabel = new JLabel(MessageBundle.getMessage("angal.mortuary.maxdays.txt"));
            GridBagConstraints gbcMaxDLabel = new GridBagConstraints();
            gbcMaxDLabel.anchor = GridBagConstraints.WEST;
            gbcMaxDLabel.insets = new Insets(0, 0, 5, 5);
            gbcMaxDLabel.gridx = 0;
            gbcMaxDLabel.gridy = 3;
            dataPanel.add(maxDLabel, gbcMaxDLabel);
            GridBagConstraints gbcMaxDTextField = new GridBagConstraints();
            gbcMaxDTextField.fill = GridBagConstraints.HORIZONTAL;
            gbcMaxDTextField.insets = new Insets(0, 0, 5, 0);
            gbcMaxDTextField.gridx = 1;
            gbcMaxDTextField.gridy = 3;
            dataPanel.add(getMaxDTextField(), gbcMaxDTextField);

            JLabel requiredLabel = new JLabel(MessageBundle.getMessage("angal.mortuary.requiredfields"));
            GridBagConstraints gbcRequiredLabel = new GridBagConstraints();
            gbcRequiredLabel.gridwidth = 2;
            gbcRequiredLabel.anchor = GridBagConstraints.EAST;
            gbcRequiredLabel.gridx = 0;
            gbcRequiredLabel.gridy = 13;
            dataPanel.add(requiredLabel, gbcRequiredLabel);
        }
        return dataPanel;
    }

    /**
     * This method initializes buttonPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            buttonPanel.add(getOkButton(), null);
            buttonPanel.add(getCancelButton(), null);
        }
        return buttonPanel;
    }

    /**
     * This method initializes cancelButton
     *
     * @return javax.swing.JButton
     */
    private JButton getCancelButton() {
        if (cancelButton == null) {
            cancelButton = new JButton(MessageBundle.getMessage("angal.common.cancel.btn"));
            cancelButton.setMnemonic(MessageBundle.getMnemonic("angal.common.cancel.btn.key"));
            cancelButton.addActionListener(actionEvent -> dispose());
        }
        return cancelButton;
    }

    /**
     * This method initializes okButton
     *
     * @return javax.swing.JButton
     */
    private JButton getOkButton() {
        if (okButton == null) {
            okButton = new JButton(MessageBundle.getMessage("angal.common.ok.btn"));
            okButton.setMnemonic(MessageBundle.getMnemonic("angal.common.ok.btn.key"));

            okButton.addActionListener(actionEvent -> {
                if(insert){
                    code = codeTextField.getText().trim();
                    if (code.isEmpty()) {
                        MessageDialog.error(this, "angal.common.pleaseinsertacode.msg");
                        return;
                    }

                    if (code.length() > 11) {
                        MessageDialog.error(this, "angal.common.thecodeistoolongmax1char.msg");
                        return;
                    }

                    try {
                        if (mortuaryBrowserManager.isCodePresent(code)) {
                            MessageDialog.error(this, "angal.mortuary.codealreadyinuse.msg");
                            return;
                        }
                    } catch (OHServiceException e) {
                        throw new RuntimeException(e);
                    }

                }
                desc = descriptionTextField.getText().trim();
                if (desc.isEmpty()) {
                    MessageDialog.error(this, "angal.common.pleaseinsertavaliddescription.msg");
                    return;
                }
                try {
                    minD = Integer.parseInt(minDTextField.getText());
                } catch (NumberFormatException f) {
                    MessageDialog.error(this, "angal.mortuary.insertavalidmindnumber");
                    return;
                }
                if (minD < 0) {
                    MessageDialog.error(this, "angal.mortuary.insertavalidmindnumber");
                    return;
                }
                try {
                    maxD = Integer.parseInt(maxDTextField.getText());
                } catch (NumberFormatException f) {
                    MessageDialog.error(this, "angal.mortuaty.insertavalidmaxdnumber");
                    return;
                }
                if (maxD < 0) {
                    MessageDialog.error(this, "angal.mortuaty.insertavalidmaxdnumber");
                    return;
                }
                if(minD >= maxD){
                    MessageDialog.error(this, "angal.mortuary.insertcoherencemaxminvalues");
                    return;
                }

                mortuary.setDescription(desc);
                mortuary.setCode(codeTextField.getText());
                mortuary.setDaysMin(minD);
                mortuary.setDaysMax(maxD);

                boolean result = false;
                Mortuary savedMortuary;
                if (insert) { // inserting
                    try {
                        savedMortuary = mortuaryBrowserManager.newMortuary(mortuary);
                        if (savedMortuary != null) {
                            result = true;
                        }
                    } catch (OHServiceException ex) {
                        OHServiceExceptionUtil.showMessages(ex);
                    }
                    if (result) {
                        fireMortuaryInserted();
                    }
                } else {
                    try { // updating
                        savedMortuary = mortuaryBrowserManager.updateMortuary(mortuary);
                        if (savedMortuary != null) {
                            result = true;
                        }
                    } catch (OHServiceException ex) {
                        OHServiceExceptionUtil.showMessages(ex);
                    }
                    if (result) {
                        fireMortuaryUpdated();
                    }
                }
                if (!result) {
                    MessageDialog.error(null, "angal.common.datacouldnotbesaved.msg");
                }
                else {
                    dispose();
                }
            });
        }
        return okButton;
    }

    /**
     * This method initializes descriptionTextField
     *
     * @return javax.swing.JTextField
     */
    private JTextField getDescriptionTextField() {
        if (descriptionTextField == null) {
            descriptionTextField = new VoLimitedTextField(50);
            if (!insert) {
                descriptionTextField.setText(mortuary.getDescription());
            }
        }
        return descriptionTextField;
    }

    /**
     * This method initializes codeTextField
     *
     * @return javax.swing.JTextField
     */
    private JTextField getCodeTextField() {
        if (codeTextField == null) {
            codeTextField = new VoLimitedTextField(11, 20);
            if (!insert) {
                codeTextField.setText(mortuary.getCode());
                codeTextField.setEnabled(false);
            }
        }
        return codeTextField;
    }

    /**
     * This method initializes telTextField
     *
     * @return javax.swing.JTextField
     */
    private JTextField getMinDTextField() {
        if (minDTextField == null) {
            minDTextField = new VoLimitedTextField(50, 20);
            if (!insert) {
                minDTextField.setText(String.valueOf(mortuary.getDaysMin()));
            }
        }
        return minDTextField;
    }

    /**
     * This method initializes faxTextField
     *
     * @return javax.swing.JTextField
     */
    private JTextField getMaxDTextField() {
        if (maxDTextField == null) {
            maxDTextField = new VoLimitedTextField(50, 20);
            if (!insert) {
                maxDTextField.setText(String.valueOf(mortuary.getDaysMax()));
            }
        }
        return maxDTextField;
    }
}
