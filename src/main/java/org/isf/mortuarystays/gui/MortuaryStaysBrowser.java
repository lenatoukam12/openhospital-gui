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
package org.isf.mortuarystays.gui;

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
import org.isf.mortuarystays.manager.MortuaryStaysBrowserManager;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.gui.OHServiceExceptionUtil;
import org.isf.utils.jobjects.MessageDialog;
import org.isf.utils.jobjects.ModalJFrame;
import org.isf.mortuarystays.model.MortuaryStays;

/**
 * This class shows a list of wards.
 * It is possible to edit-insert-delete records
 *
 * @author Rick
 *
 */
public class MortuaryStaysBrowser extends ModalJFrame {
    private static final long serialVersionUID = 1L;


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
    private String[] pColums = { MessageBundle.getMessage("angal.mortuarystays.code.txt"),
            MessageBundle.getMessage("angal.mortuarystays.name.txt"),
            MessageBundle.getMessage("angal.mortuarystays.description.txt"),
            MessageBundle.getMessage("angal.mortuarystays.dmin.txt"),
            MessageBundle.getMessage("angal.mortuarystays.dmax.txt")};
    private int[] pColumwidth = {50, 80, 90, 30, 30};
    private Class[] pColumnClass = {String.class, String.class, String.class, int.class, int.class};
    private int selectedrow;
    private List<MortuaryStays> pMortuaryStays;
    private MortuaryStays mortuaryStays;
    private final JFrame myFrame;
    private MortuaryStaysBrowserManager mortuaryStaysManager = Context.getApplicationContext().getBean(MortuaryStaysBrowserManager.class);

    /**
     * This is the default constructor
     */
    public MortuaryStaysBrowser() {
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
        this.setTitle(MessageBundle.getMessage("angal.mortuarystays.mortuarybrowser.title"));
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
                        mortuaryStays = (MortuaryStays) model.getValueAt(table.getSelectedRow(), -1);

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
                    mortuaryStays = new MortuaryStays("","", "",0,0);	//operation will reference the new record

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
                    MortuaryStays mortuaryStays = (MortuaryStays) model.getValueAt(table.getSelectedRow(), -1);
                    int answer = MessageDialog.yesNo(this, "angal.mortuarystays.deletemortuarystays.fmt.msg", mortuaryStays.getDescription());
                    try {
                        if (answer == JOptionPane.YES_OPTION) {
                            mortuaryStaysManager.delete(mortuaryStays);
                            pMortuaryStays.remove(table.getSelectedRow());
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
            model = new MortuaryStaysBrowserModel();
            table = new JTable(model);
            table.getColumnModel().getColumn(0).setMaxWidth(pColumwidth[0]);
            table.getColumnModel().getColumn(1).setPreferredWidth(pColumwidth[1]);
            table.getColumnModel().getColumn(2).setPreferredWidth(pColumwidth[2]);
            table.getColumnModel().getColumn(3).setPreferredWidth(pColumwidth[3]);
            table.getColumnModel().getColumn(4).setPreferredWidth(pColumwidth[4]);
        }
        return table;
    }

    class MortuaryStaysBrowserModel extends DefaultTableModel {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public MortuaryStaysBrowserModel() {
            try {
                pMortuaryStays = mortuaryStaysManager.getMortuariesStays();
            } catch (OHServiceException e) {
                pMortuaryStays = new ArrayList<>();
                OHServiceExceptionUtil.showMessages(e);
            }

        }

        @Override
        public int getRowCount() {
            if (pMortuaryStays == null) {
                return 0;
            }
            return pMortuaryStays.size();
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
            MortuaryStays mortuaryStays = pMortuaryStays.get(r);
            if (c == 0) {
                return mortuaryStays.getCode();
            } else if (c == -1) {
                return mortuaryStays;
            } else if (c == 1) {
                return mortuaryStays.getName();
            } else if (c == 2) {
                return mortuaryStays.getDescription();
            } else if (c == 3) {
                return mortuaryStays.getDaysMin();
            } else if (c == 4) {
                return mortuaryStays.getDaysMax();
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
