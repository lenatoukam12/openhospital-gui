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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.isf.generaldata.MessageBundle;
import org.isf.menu.manager.Context;
import org.isf.mortuary.manager.MortuaryBrowserManager;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.gui.OHServiceExceptionUtil;
import org.isf.utils.jobjects.MessageDialog;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.mortuary.model.Mortuary;
import org.isf.mortuary.gui.MortuaryEdit.MortuaryListener;
import org.isf.ward.model.Ward;

/**
 * This class shows a list of wards.
 * It is possible to edit-insert-delete records
 *
 * @author Rick
 *
 */
public class MortuaryBrowser extends ModalJFrame implements MortuaryListener {
    private static final long serialVersionUID = 1L;

    @Override
    public void mortuaryInserted(AWTEvent e) {
        pMortuary.add(0, mortuary);
        ((MortuaryBrowser.MortuaryBrowserModel) table.getModel()).fireTableDataChanged();
        if (table.getRowCount() > 0) {
            table.setRowSelectionInterval(0, 0);
        }
    }

    @Override
    public void mortuaryUpdated(AWTEvent e) {
        pMortuary.set(selectedrow, mortuary);
        ((MortuaryBrowser.MortuaryBrowserModel) table.getModel()).fireTableDataChanged();
        table.updateUI();
        if (table.getRowCount() > 0 && selectedrow > -1) {
            table.setRowSelectionInterval(selectedrow, selectedrow);
        }
    }

    private int pfrmBase = 10;
    private int pfrmWidth = 8;
    private int pfrmHeight = 6;
    private int pfrmBordX;
    private int pfrmBordY;
    private JPanel jContentPane = null;
    private JPanel jButtonPanel = null;
    private JButton jEditButton = null;
    private JButton jNewButton = null;
    private JButton jDeleteButton = null;
    private JButton jCloseButton = null;
    private JScrollPane jScrollPane = null;
    private JTable table = null;
    private DefaultTableModel model = null;
    private String[] pColums = { MessageBundle.getMessage("angal.mortuary.code"),
            MessageBundle.getMessage("angal.mortuary.name"),
            MessageBundle.getMessage("angal.mortuary.dmin"),
            MessageBundle.getMessage("angal.mortuary.dmax")};
    private int[] pColumwidth = {50, 80, 30, 30};
    private Class[] pColumnClass = {String.class, String.class, int.class, int.class};
    private int selectedrow;
    private List<Mortuary> pMortuary;
    private Mortuary mortuary;
    private final JFrame myFrame;
    private MortuaryBrowserManager mortuaryManager = Context.getApplicationContext().getBean(MortuaryBrowserManager.class);

    /**
     * This is the default constructor
     */
    public MortuaryBrowser() {
        super();
        myFrame = this;
        initialize();
        setVisible(true);
    }

    /**
     * This method initializes this
     *
     * @return void
     */
    private void initialize() {
        this.setTitle(MessageBundle.getMessage("angal.mortuary.mortuarybrowser"));
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screensize = kit.getScreenSize();
        pfrmBordX = (screensize.width - (screensize.width / pfrmBase * pfrmWidth)) / 2;
        pfrmBordY = (screensize.height - (screensize.height / pfrmBase * pfrmHeight)) / 2;
        this.setBounds(pfrmBordX,pfrmBordY,screensize.width / pfrmBase * pfrmWidth,screensize.height / pfrmBase * pfrmHeight);
        this.setContentPane(getJContentPane());
        this.setLocationRelativeTo(null);
        pack();

    }

    /**
     * This method initializes jContentPane
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getJButtonPanel(), java.awt.BorderLayout.SOUTH);
            jContentPane.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
        }
        return jContentPane;
    }

    /**
     * This method initializes jButtonPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getJButtonPanel() {
        if (jButtonPanel == null) {
            jButtonPanel = new JPanel();
            jButtonPanel.add(getJNewButton(), null);
            jButtonPanel.add(getJEditButton(), null);
            jButtonPanel.add(getJDeleteButton(), null);
            jButtonPanel.add(getJCloseButton(), null);
        }
        return jButtonPanel;
    }

    /**
     * This method initializes jEditButton
     *
     * @return javax.swing.JButton
     */
    private JButton getJEditButton() {
        if (jEditButton == null) {
            jEditButton = new JButton();
            jEditButton.setText(MessageBundle.getMessage("angal.common.edit"));
            jEditButton.setMnemonic(KeyEvent.VK_E);
            jEditButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent event) {
                    if (table.getSelectedRow() < 0) {
                        MessageDialog.error(null, "angal.common.pleaseselectarow.msg");
                    }else {
                        selectedrow = table.getSelectedRow();
                        mortuary = (Mortuary) model.getValueAt(table.getSelectedRow(), -1);
                        MortuaryEdit editrecord = new MortuaryEdit(myFrame, mortuary, false);
                        editrecord.addMortuaryListener(MortuaryBrowser.this);
                        editrecord.setVisible(true);
                    }
                }
            });
        }
        return jEditButton;
    }

    /**
     * This method initializes jNewButton
     *
     * @return javax.swing.JButton
     */
    private JButton getJNewButton() {
        if (jNewButton == null) {
            jNewButton = new JButton();
            jNewButton = new JButton(MessageBundle.getMessage("angal.common.new.btn"));
            jNewButton.setMnemonic(MessageBundle.getMnemonic("angal.common.new.btn.key"));
            jNewButton.setMnemonic(KeyEvent.VK_N);
            jNewButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent event) {
                    mortuary=new Mortuary("","",0,0);	//operation will reference the new record
                    MortuaryEdit newrecord = new MortuaryEdit(myFrame, mortuary, true);
                    newrecord.addMortuaryListener(MortuaryBrowser.this);
                    newrecord.setVisible(true);
                }
            });
        }
        return jNewButton;
    }

    /**
     * This method initializes jDeleteButton
     *
     * @return javax.swing.JButton
     */
    private JButton getJDeleteButton() {
        if (jDeleteButton == null) {
            jDeleteButton = new JButton();
            jDeleteButton = new JButton(MessageBundle.getMessage("angal.common.delete.btn"));
            jDeleteButton.setMnemonic(MessageBundle.getMnemonic("angal.common.delete.btn.key"));
            jDeleteButton.setMnemonic(KeyEvent.VK_D);
            jDeleteButton.addActionListener(actionEvent -> {
                if (table.getSelectedRow() < 0) {
                    MessageDialog.error(this, "angal.common.pleaseselectarow.msg");
                } else {
                    Mortuary mortuary = (Mortuary) model.getValueAt(table.getSelectedRow(), -1);
                    int answer = MessageDialog.yesNo(this, "angal.mortuary.deletemortuary.fmt.msg", mortuary.getDescription());
                    try {
                        if (answer == JOptionPane.YES_OPTION) {
                            mortuaryManager.deleteMortuary(mortuary);
                            pMortuary.remove(table.getSelectedRow());
                            model.fireTableDataChanged();
                            table.updateUI();
                        }
                    } catch (OHServiceException e) {
                        OHServiceExceptionUtil.showMessages(e);
                    }
                }
            });
        }
        return jDeleteButton;
    }

    /**
     * This method initializes jCloseButton
     *
     * @return javax.swing.JButton
     */
    private JButton getJCloseButton() {
        if (jCloseButton == null) {
            jCloseButton = new JButton();
            jCloseButton = new JButton(MessageBundle.getMessage("angal.common.close.btn"));
            jCloseButton.setMnemonic(MessageBundle.getMnemonic("angal.common.close.btn.key"));
            jCloseButton.setMnemonic(KeyEvent.VK_C);
            jCloseButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    dispose();
                }
            });
        }
        return jCloseButton;
    }

    /**
     * This method initializes jScrollPane
     *
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(getJTable());
        }
        return jScrollPane;
    }

    /**
     * This method initializes table
     *
     * @return javax.swing.JTable
     */
    private JTable getJTable() {
        if (table == null) {
            model = new MortuaryBrowserModel();
            table = new JTable(model);
            table.getColumnModel().getColumn(0).setMaxWidth(pColumwidth[0]);
            table.getColumnModel().getColumn(1).setPreferredWidth(pColumwidth[1]);
            table.getColumnModel().getColumn(2).setPreferredWidth(pColumwidth[2]);
            table.getColumnModel().getColumn(3).setPreferredWidth(pColumwidth[3]);
        }
        return table;
    }

    class MortuaryBrowserModel extends DefaultTableModel {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public MortuaryBrowserModel() {
            try {
                pMortuary = mortuaryManager.getMortuaries();
            } catch (OHServiceException e) {
                pMortuary = new ArrayList<>();
                OHServiceExceptionUtil.showMessages(e);
            }

        }

        @Override
        public int getRowCount() {
            if (pMortuary == null) {
                return 0;
            }
            return pMortuary.size();
        }

        @Override
        public String getColumnName(int c) {
            return pColums[c];
        }

        @Override
        public int getColumnCount() {
            return pColums.length;
        }

        @Override
        public Object getValueAt(int r, int c) {
            Mortuary mortuary = pMortuary.get(r);
            if (c == 0) {
                return mortuary.getCode();
            } else if (c == -1) {
                return mortuary;
            } else if (c == 1) {
                return mortuary.getDescription();
            } else if (c == 2) {
                return mortuary.getDaysMin();
            } else if (c == 3) {
                return mortuary.getDaysMax();
            }
            return null;
        }

        /* (non-Javadoc)
         * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
         */
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return pColumnClass[columnIndex];
        }
        @Override
        public boolean isCellEditable(int arg0, int arg1) {
            //return super.isCellEditable(arg0, arg1);
            return false;
        }
    }
}
