/**
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

package org.isf.pregnancy.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.isf.admission.gui.AdmissionBrowser.AdmissionListener;
import org.isf.admission.model.Admission;
import org.isf.admission.model.AdmittedPatient;
import org.isf.generaldata.MessageBundle;
import org.isf.menu.gui.MainMenu;
import org.isf.patient.gui.PatientInsert;
import org.isf.patient.gui.PatientInsertExtended;
import org.isf.patient.model.Patient;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.jobjects.MessageDialog;

public class PregnancyBrowser extends JFrame
		implements PatientInsert.PatientListener, PatientInsertExtended.PatientListener, AdmissionListener {

	private static final long serialVersionUID = 1L;
	private String[] pColums = { MessageBundle.getMessage("angal.common.code.txt").toUpperCase(),
			MessageBundle.getMessage("angal.common.name.txt").toUpperCase(),
			MessageBundle.getMessage("angal.common.age.txt").toUpperCase(),
			MessageBundle.getMessage("angal.common.address.txt").toUpperCase() };
	private int[] pColumwidth = { 20, 200, 20, 150 };

	private String[] vColums = { MessageBundle.getMessage("angal.pregnancy.pregnancynumber.col"),
			MessageBundle.getMessage("angal.pregnancy.visitdate.col").toUpperCase(),
			MessageBundle.getMessage("angal.pregnancy.visittype.col").toUpperCase(),
			MessageBundle.getMessage("angal.pregnancy.visitnote.col").toUpperCase() };
	private int[] vColumwidth = { 20, 40, 40, 220 };

	private PregnancyBrowser myFrame;
	private List<AdmittedPatient> pregnancyPatientList;
	List<AdmittedPatient> patientList = new ArrayList<AdmittedPatient>();

	private JTable patientTable;
	private JTable visitTable;
	private JScrollPane patientScrollPane;
	private JScrollPane visitScrollPane;
	private JButton jNewPatientButton;
	private JButton jEditPatientButton;
	private JButton jDeletePatientButton;
	private JButton jNewPrenatalVisitButton;
	private JButton jDeleteVisitButton;
	private JButton jCloseButton;
	private JButton jExamsButton;
	private JButton jVaccinButton;
	private JButton jReportButton;
	private JButton jNewPostnatalVisitButton;
	private JButton jNewPregnancyButton;
	private JButton jEditVisitButton;
	private JButton jDeliveryButton;
	private JButton next;
	private JButton previous;
	private JComboBox pagesCombo;
	private JLabel under;
	private static int PAGE_SIZE = 50;
	private int START_INDEX = 0;
	private int TOTAL_ROWS;
	private Patient patient;
	private JButton jSearchButton;
	private JTextField searchPatientTextField;
	private List<JLabel> deltypeLabel;
	private List<JLabel> deltypeResLabel;
	private String lastKey = "";
	private DefaultTableModel model;

	/**
	 * Constructor called from the main menu
	 */
	public PregnancyBrowser() {
		setTitle(MessageBundle.getMessage("angal.pregnancy.patientsbrowser.title"));
		myFrame = this;
		initComponents();
		pack();
		setLocationRelativeTo(null);
		this.setVisible(true);
		myFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// to free memory
				if (pregnancyPatientList != null)
					pregnancyPatientList.clear();
				dispose();
			}
		});
	}

	/**
	 * constructor for the AdmissionBrowser to see only the pregnancyvisits for the
	 * patient
	 * 
	 * @param admittedpatient the admitted patient
	 */
	public PregnancyBrowser(Patient admittedpatient) {
		setTitle(MessageBundle.getMessage("angal.pregnancy.patientsbrowser.title"));
		myFrame = this;
		pregnancyPatientList = new ArrayList<AdmittedPatient>();
		this.patient = admittedpatient;
		initComponents();
		pack();
		setLocationRelativeTo(null);
		this.setVisible(true);
		myFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// to free memory
				if (pregnancyPatientList != null)
					pregnancyPatientList.clear();
				dispose();
			}
		});

	}

	/**
	 * intis the components
	 */
	private void initComponents() {
		getContentPane().add(getPatientPanel(), BorderLayout.NORTH);
		getContentPane().add(getVisitPanel(), BorderLayout.CENTER);
		getContentPane().add(getPregnancyButtonPanel(), BorderLayout.SOUTH);
	}

	private JPanel getPatientPanel() {
		JPanel dataPatientListPanel = new JPanel(new BorderLayout());
		JPanel navigation = new JPanel(new FlowLayout(FlowLayout.CENTER));
		under = new JLabel(MessageBundle.getMessage("angal.common.page.label"));
		under.setPreferredSize(new Dimension(60, 21));
		navigation.add(getPreviousButton()).setPreferredSize(new Dimension(100, 21));
		navigation.add(getComboBoxPages());
		navigation.add(under);
		navigation.add(getNextButton()).setPreferredSize(new Dimension(100, 21));
		dataPatientListPanel.add(navigation, BorderLayout.NORTH);
		dataPatientListPanel.add(getSearchPanel(), BorderLayout.WEST);
		dataPatientListPanel.add(getPatientScrollPane(), BorderLayout.CENTER);
		dataPatientListPanel.add(getPatientButtonPanel(), BorderLayout.EAST);
		return dataPatientListPanel;
	}

	public JButton getNextButton() {
		if (next == null) {
			next = new JButton(MessageBundle.getMessage("angal.visit.nextarrow.btn"));
			next.setPreferredSize(new Dimension(30, 21));
			next.setMnemonic(KeyEvent.VK_X);
			next.addActionListener(actionEvent -> {
				if (!previous.isEnabled())
					previous.setEnabled(true);
				START_INDEX += PAGE_SIZE;
				model = new PregnancyPatientBrowserModel(null, START_INDEX, PAGE_SIZE);
				if ((START_INDEX + PAGE_SIZE) > TOTAL_ROWS) {
					next.setEnabled(false);
				}
				pagesCombo.setSelectedItem(START_INDEX / PAGE_SIZE + 1);
				model.fireTableDataChanged();
				patientTable.updateUI();
			});
		}
		return next;
	}

	public JButton getPreviousButton() {
		if (previous == null) {
			previous = new JButton(MessageBundle.getMessage("angal.visit.arrowprevious.btn"));
			previous.setPreferredSize(new Dimension(30, 21));
			previous.setMnemonic(KeyEvent.VK_P);
			previous.addActionListener(actionEvent -> {
				if (!next.isEnabled())
					next.setEnabled(true);
				START_INDEX -= PAGE_SIZE;
				model = new PregnancyPatientBrowserModel(null, START_INDEX, PAGE_SIZE);
				if (START_INDEX < PAGE_SIZE)
					previous.setEnabled(false);
				pagesCombo.setSelectedItem(START_INDEX / PAGE_SIZE + 1);
				model.fireTableDataChanged();
				patientTable.updateUI();
			});
		}
		return previous;
	}

	private JComboBox getComboBoxPages() {
		if (pagesCombo == null) {
			pagesCombo = new JComboBox();
			pagesCombo.setPreferredSize(new Dimension(60, 21));
			pagesCombo.setEditable(true);
			pagesCombo.addActionListener(actionEvent -> {
				if (pagesCombo.getItemCount() != 0) {
					int page_number = (Integer) pagesCombo.getSelectedItem();
					START_INDEX = (page_number - 1) * PAGE_SIZE;
					model = new PregnancyPatientBrowserModel(null, START_INDEX, PAGE_SIZE);
					if ((START_INDEX + PAGE_SIZE) > TOTAL_ROWS) {
						next.setEnabled(false);
					}
					if (page_number == 1) {
						previous.setEnabled(false);
					} else {
						previous.setEnabled(true);
					}
					pagesCombo.setSelectedItem(START_INDEX / PAGE_SIZE + 1);
					model.fireTableDataChanged();
				}
				patientTable.updateUI();
			});
		}
		return pagesCombo;
	}

	private JPanel getVisitPanel() {
		JPanel visitListPanel = new JPanel(new BorderLayout());
		visitListPanel.add(getVisitScrollPane(), BorderLayout.NORTH);
		visitListPanel.add(getPregnancyDetailsPanel(), BorderLayout.EAST);
		return visitListPanel;
	}

	private JPanel getSearchPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));

		searchPatientTextField = new JTextField();
		searchPatientTextField.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) {

				if (searchPatientTextField.getText().length() > 7) {
					filterPatient();
				}
			}

			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();
				if (key == KeyEvent.VK_ENTER) {
					if (searchPatientTextField.getText().length() > 4) {
						filterPatient();
					}

				}
			}

			public void keyReleased(KeyEvent e) {
			}
		});

		searchPanel.add(searchPatientTextField, BorderLayout.CENTER);
		searchPanel.add(getPatientSearchButton(), BorderLayout.EAST);
		searchPanel = setMyBorder(searchPanel, MessageBundle.getMessage("angal.common.searchkey.txt"));
		if (patient != null)
			searchPatientTextField.setEnabled(false);
		panel.add(searchPanel, BorderLayout.NORTH);
			JPanel panelPregnantPrint = new JPanel();
			panel.add(panelPregnantPrint, BorderLayout.SOUTH);
			panelPregnantPrint.setLayout(new BorderLayout(0, 0));
				JButton updateDelivery = new JButton(MessageBundle.getMessage("angal.pregnancy.updatedelivery.btn"));
				updateDelivery.addActionListener(actionEvent -> {
				});
				panelPregnantPrint.add(updateDelivery, BorderLayout.NORTH);
				JButton declarationBirth = new JButton(
						MessageBundle.getMessage("angal.pregnancy.declaration_birth_but.btn"));
				declarationBirth.addActionListener(actionEvent -> {
				});
				panelPregnantPrint.add(declarationBirth, BorderLayout.CENTER);
				JButton declarationCertificate = new JButton(
						MessageBundle.getMessage("angal.pregnancy.declaration_certificate_but.btn"));
				declarationCertificate.addActionListener(actionEvent -> {
				});
				panelPregnantPrint.add(declarationCertificate, BorderLayout.SOUTH);

		return panel;
	}

	private JPanel setMyBorder(JPanel c, String title) {
		javax.swing.border.Border b2 = BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(title),
				BorderFactory.createEmptyBorder(0, 0, 0, 0));
		c.setBorder(b2);
		return c;
	}

	private JButton getPatientSearchButton() {
		if (jSearchButton == null) {
			jSearchButton = new JButton();
			jSearchButton.setIcon(new ImageIcon("rsc/icons/zoom_r_button.png"));
			jSearchButton.setPreferredSize(new Dimension(20, 20));
			jSearchButton.addActionListener(actionEvent -> {
			});

		}
		if (patient != null)
			jSearchButton.setEnabled(false);
		return jSearchButton;
	}

	private JPanel getPregnancyDetailsPanel() {

		JPanel panel = new JPanel();
		deltypeLabel = new ArrayList<JLabel>();
		deltypeResLabel = new ArrayList<JLabel>();
		for (int a = 0; a < 15; a++) {
			JLabel typeL = new JLabel("");
			JLabel typeR = new JLabel("");
			panel.add(typeL);
			panel.add(typeR);
			deltypeLabel.add(typeL);
			deltypeResLabel.add(typeR);
			typeL.setFont(new Font("Lucia Grande", 0, 10));
			typeR.setFont(new Font("Lucia Grande", 0, 10));

		}
		panel.setPreferredSize(new Dimension(180, 100));
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		return panel;
	}

	private JPanel getPatientButtonPanel() {

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		int buttonsize = 0;
		getJNewPatientButton();
		getJEditPatientButton();
		getJDelPatientButton();
		if (jNewPatientButton.getText().length() > buttonsize)
			buttonsize = jNewPatientButton.getText().length();
		if (jEditPatientButton.getText().length() > buttonsize)
			buttonsize = jEditPatientButton.getText().length();
		if (jDeletePatientButton.getText().length() > buttonsize)
			buttonsize = jDeletePatientButton.getText().length();
		jNewPatientButton.setPreferredSize(new Dimension(180, 30));
		jEditPatientButton.setPreferredSize(new Dimension(180, 30));
		jDeletePatientButton.setPreferredSize(new Dimension(180, 30));
		jNewPatientButton.setMinimumSize(new Dimension(buttonsize + 100, 30));
		jEditPatientButton.setMinimumSize(new Dimension(buttonsize + 100, 30));
		jDeletePatientButton.setMinimumSize(new Dimension(buttonsize + 100, 30));
		jNewPatientButton.setMaximumSize(new Dimension(buttonsize + 150, 30));
		jEditPatientButton.setMaximumSize(new Dimension(buttonsize + 150, 30));
		jDeletePatientButton.setMaximumSize(new Dimension(buttonsize + 150, 30));
		buttonPanel.add(jNewPatientButton);
		buttonPanel.add(jEditPatientButton);
		buttonPanel.add(jDeletePatientButton);
		// in the case the browser is opened from the admission
		return buttonPanel;
	}

	/**
	 * @return
	 * @uml.property name="visitScrollPane"
	 */
	private JScrollPane getVisitScrollPane() {
		visitTable = new JTable(new PregnancyVisitBrowserModel());

		for (int i = 0; i < vColums.length; i++) {
			visitTable.getColumnModel().getColumn(i).setPreferredWidth(vColumwidth[i]);
		}

		int tableWidth = 0;
		for (int i = 0; i < vColumwidth.length; i++) {
			tableWidth += vColumwidth[i];
		}
		visitScrollPane = new JScrollPane(visitTable);
		visitScrollPane.setPreferredSize(new Dimension(tableWidth + 400, 200));
		return visitScrollPane;
	}

	/**
	 * @return
	 * @uml.property name="patientScrollPane"
	 */
	private JScrollPane getPatientScrollPane() {
		TOTAL_ROWS = (new PregnancyPatientBrowserModel()).total_row;
		model = new PregnancyPatientBrowserModel(null, START_INDEX, PAGE_SIZE);
		patientTable = new JTable(model);
		patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		previous.setEnabled(false);
		if (PAGE_SIZE > TOTAL_ROWS)
			next.setEnabled(false);
		patientTable.setAutoCreateRowSorter(true);
		initialiseCombo(pagesCombo, TOTAL_ROWS);

		for (int i = 0; i < pColums.length; i++) {
			patientTable.getColumnModel().getColumn(i).setPreferredWidth(pColumwidth[i]);
		}

		int tableWidth = 0;
		for (int i = 0; i < pColumwidth.length; i++) {
			tableWidth += pColumwidth[i];
		}
		TableListener listener = new TableListener();
		patientTable.getSelectionModel().addListSelectionListener(listener);
		patientTable.getColumnModel().getSelectionModel().addListSelectionListener(listener);
		if (patient != null) {
			int index = 0;
			for (int a = 0; a < pregnancyPatientList.size(); a++) {
				if (pregnancyPatientList.get(a).getPatient().getCode().equals(patient.getCode()))
					break;
				else
					index++;
			}
			patientTable.setRowSelectionInterval(index, index);
			patientTable.setEnabled(false);
		}
		patientScrollPane = new JScrollPane(patientTable);
		patientScrollPane.setPreferredSize(new Dimension(tableWidth + 400, 300));
		return patientScrollPane;
	}

	private JPanel getPregnancyButtonPanel() {
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(getJNewPregnancyButton());
		buttonPanel.add(getJNewPrenatalVisitButton());
		buttonPanel.add(getJNewPostnatalVisitButton());
		buttonPanel.add(getJDeliveryButton());
		buttonPanel.add(getJEditVisitButton());
		if (MainMenu.checkUserGrants("btnadmexamination"))
			buttonPanel.add(getJExamsButton());
		if (MainMenu.checkUserGrants("patientvaccine"))
			buttonPanel.add(getJVaccinButton());
		buttonPanel.add(getJDeleteVisitButton());
		buttonPanel.add(getJReportButton());
		buttonPanel.add(getJCloseButton());
		return buttonPanel;
	}

	private JButton getJNewPatientButton() {
		if (jNewPatientButton == null) {
			jNewPatientButton = new JButton(MessageBundle.getMessage("angal.common.newpatient.btn"));
			jNewPatientButton.setMnemonic(MessageBundle.getMnemonic("angal.common.new.btn.key"));
			jNewPatientButton.addActionListener(actionEvent -> {
			});
		}
		if (patient != null)
			jNewPatientButton.setEnabled(false);
		return jNewPatientButton;
	}

	private JButton getJEditPatientButton() {
		if (jEditPatientButton == null) {
			jEditPatientButton = new JButton(MessageBundle.getMessage("angal.common.editpatient.btn"));
			jEditPatientButton.setMnemonic(MessageBundle.getMnemonic("angal.common.editpatient.btn.key"));
			jEditPatientButton.addActionListener(actionEvent -> {
				if (patientTable.getSelectedRow() < 0) {
					MessageDialog.error(this, "angal.common.pleaseselectarow.msg");
					return;
				}

				AdmittedPatient adPatient = (AdmittedPatient) patientTable.getValueAt(patientTable.getSelectedRow(),
						-1);
				if (adPatient != null) {
					patient = adPatient.getPatient();
				} else {
					patient = null;
				}
			});
		}
		if (patient != null)
			jEditPatientButton.setEnabled(false);
		return jEditPatientButton;
	}

	private JButton getJDelPatientButton() {
		if (jDeletePatientButton == null) {
			jDeletePatientButton = new JButton(MessageBundle.getMessage("angal.common.delete.btn"));
			jDeletePatientButton.setMnemonic(MessageBundle.getMnemonic("angal.common.delete.btn.key"));
			jDeletePatientButton.addActionListener(actionEvent -> {
				if (patientTable.getSelectedRow() < 0) {
					MessageDialog.error(this, "angal.common.pleaseselectarow.msg");
					return;
				}

				AdmittedPatient adPatient = (AdmittedPatient) patientTable.getValueAt(patientTable.getSelectedRow(),
						-1);
				if (adPatient != null) {
					patient = adPatient.getPatient();
				} else {
					patient = null;
				}

				int n = JOptionPane.showConfirmDialog(null,  MessageBundle.formatMessage("angal.pregnancy.deletepatient.fmt.msg"),
						MessageBundle.getMessage("angal.messagedialog.question.title"), JOptionPane.YES_NO_OPTION);
			});
		}
		if (patient != null)
			jDeletePatientButton.setEnabled(false);
		return jDeletePatientButton;
	}

	public void fireMyDeletedPatient(Patient p) {

		if (pregnancyPatientList == null) {
			filterPatient();
		}
		int cc = 0;
		boolean found = false;
		for (AdmittedPatient elem : pregnancyPatientList) {
			if (elem.getPatient().getCode() == p.getCode()) {
				found = true;
				break;
			}
			cc++;
		}
		if (found) {
			pregnancyPatientList.remove(cc);
			filterPatient();
		}
	}

	private JButton getJNewPrenatalVisitButton() {
		if (jNewPrenatalVisitButton == null) {
			jNewPrenatalVisitButton = new JButton(MessageBundle.getMessage("angal.pregnancy.newprenatalvisit.btn"));
			jNewPrenatalVisitButton.setMnemonic(MessageBundle.getMnemonic("angal.pregnancy.newprenatalvisit.btn.key"));
			jNewPrenatalVisitButton.addActionListener(actionEvent -> {
			});
		}
		return jNewPrenatalVisitButton;
	}

	private JButton getJDeleteVisitButton() {
		if (jDeleteVisitButton == null) {
			jDeleteVisitButton = new JButton(MessageBundle.getMessage("angal.pregnancy.deletevisit.btn"));
			jDeleteVisitButton.setMnemonic(MessageBundle.getMnemonic("angal.pregnancy.deletevisit.btn.key"));
			jDeleteVisitButton.addActionListener(actionEvent -> {
			});
		}
		return jDeleteVisitButton;
	}

	private JButton getJReportButton() {
		if (jReportButton == null) {
			jReportButton = new JButton(MessageBundle.getMessage("angal.pregnancy.report.btn"));
			jReportButton.setMnemonic(MessageBundle.getMnemonic("angal.pregnancy.report.btn.key"));
			jReportButton.addActionListener(actionEvent -> {
			});
		}
		return jReportButton;
	}

	private JButton getJNewPostnatalVisitButton() {
		if (jNewPostnatalVisitButton == null) {
			jNewPostnatalVisitButton = new JButton(MessageBundle.getMessage("angal.pregnancy.newpostnatalvisit.btn"));
			jNewPostnatalVisitButton.setMnemonic(MessageBundle.getMnemonic("angal.pregnancy.newpostnatalvisit.btn.key"));
			jNewPostnatalVisitButton.addActionListener(actionEvent -> {
			});
		}
		return jNewPostnatalVisitButton;
	}

	private JButton getJNewPregnancyButton() {
		if (jNewPregnancyButton == null) {
			jNewPregnancyButton = new JButton(MessageBundle.getMessage("angal.pregnancy.newpregnancy.btn"));
			jNewPregnancyButton.setMnemonic(MessageBundle.getMnemonic("angal.pregnancy.newpregnancy.btn.key"));
			jNewPregnancyButton.addActionListener(actionEvent -> {
			});
		}
		return jNewPregnancyButton;
	}

	private JButton getJEditVisitButton() {
		if (jEditVisitButton == null) {
			jEditVisitButton = new JButton(MessageBundle.getMessage("angal.pregnancy.editvisit.btn"));
			jEditVisitButton.setMnemonic(MessageBundle.getMnemonic("angal.pregnancy.editvisit.btn.key"));
			jEditVisitButton.addActionListener(actionEvent -> {
			});
		}
		return jEditVisitButton;
	}

	private JButton getJDeliveryButton() {
		if (jDeliveryButton == null) {
			jDeliveryButton = new JButton(MessageBundle.getMessage("angal.pregnancy.newdelivery.btn"));
			jDeliveryButton.setMnemonic(MessageBundle.getMnemonic("angal.pregnancy.newdelivery.btn.key"));
			jDeliveryButton.addActionListener(actionEvent -> {
			});
		}
		if (patient != null)
			jDeliveryButton.setEnabled(false);
		return jDeliveryButton;
	}

	private JButton getJExamsButton() {
		if (jExamsButton == null) {
			jExamsButton = new JButton(MessageBundle.getMessage("angal.opd.exams.btn"));
			jExamsButton.setMnemonic(MessageBundle.getMnemonic("angal.opd.exams.btn.key"));
			jExamsButton.addActionListener(actionEvent -> {
				if (patientTable.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.common.pleaseselectarow.msg"),
							MessageBundle.getMessage("angal.admission.editpatient"), JOptionPane.PLAIN_MESSAGE);
					return;
				}
			});
		}
		return jExamsButton;
	}

	private JButton getJVaccinButton() {
		if (jVaccinButton == null) {

			jVaccinButton = new JButton(MessageBundle.getMessage("angal.cpn.vaccin.btn"));
			jVaccinButton.setMnemonic(MessageBundle.getMnemonic("angal.cpn.vaccin.btn.key"));
			jVaccinButton.addActionListener(actionEvent -> {
				if (patientTable.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.common.pleaseselectarow.msg"),
							MessageBundle.getMessage("angal.admission.editpatient"), JOptionPane.PLAIN_MESSAGE);
					return;
				}
			});
		}
		return jVaccinButton;
	}

	private JButton getJCloseButton() {
		if (jCloseButton == null) {
			jCloseButton = new JButton(MessageBundle.getMessage("angal.common.close.btn"));
			jCloseButton.setMnemonic(MessageBundle.getMnemonic("angal.common.close.btn.key"));
			jCloseButton.addActionListener(actionEvent -> dispose());
		}
		return jCloseButton;
	}

	class PregnancyVisitBrowserModel extends DefaultTableModel {
		private static final long serialVersionUID = 1L;

		public String getColumnName(int c) {
			return vColums[c];
		}

		public int getColumnCount() {
			return vColums.length;
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}

	}

	class PregnancyPatientBrowserModel extends DefaultTableModel {
		public int total_row;

		public PregnancyPatientBrowserModel() {
			patientList.clear();
		}

		public PregnancyPatientBrowserModel(Object object, int sTART_INDEX, int pAGE_SIZE) {

			patientList.clear();
		}

		public int getRowCount() {
			if (patientList == null)
				return 0;
			return patientList.size();
		}

		public String getColumnName(int c) {
			return pColums[c];
		}

		public int getColumnCount() {
			return pColums.length;
		}

		public Object getValueAt(int r, int c) {
			if (c == -1) {
				return patientList.get(r);
			} else if (c == 0) {
				return (patientList.get(r)).getPatient().getCode() + "";
			} else if (c == 1) {
				return (patientList.get(r)).getPatient().getSecondName() + " "
						+ patientList.get(r).getPatient().getFirstName();
			} else if (c == 2) {
				return patientList.get(r).getPatient().getAge();

			} else if (c == 3) {
				return patientList.get(r).getPatient().getCity() + " " + patientList.get(r).getPatient().getAddress();
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}
	}

	@Override
	public void patientUpdated(AWTEvent e) {
		Patient u = (Patient) e.getSource();
		int row = patientTable.getSelectedRow();

		if (pregnancyPatientList == null) {
			lastKey = "";
			filterPatient();
		}
		for (int i = 0; i < pregnancyPatientList.size(); i++) {
			if ((pregnancyPatientList.get(i).getPatient().getCode()).equals(u.getCode())) {
				Admission admission = pregnancyPatientList.get(i).getAdmission();
				pregnancyPatientList.remove(i);
				pregnancyPatientList.add(i, new AdmittedPatient(u, admission));
				break;
			}
		}
		lastKey = "";
		filterPatient();
		try {
			patientTable.setRowSelectionInterval(row, row);
		} catch (Exception e1) {
		}
		searchPatientTextField.requestFocus();

	}

	@Override
	public void patientInserted(AWTEvent e) {
		Patient u = (Patient) e.getSource();
		if (pregnancyPatientList == null) {
//			pregnancyPatientList.add(0, u);
			pregnancyPatientList = new ArrayList<AdmittedPatient>();
			pregnancyPatientList.add(0, new AdmittedPatient(u, null));
		} else {
			pregnancyPatientList.add(0, new AdmittedPatient(u, null));
//			pregnancyPatientList.add(0, u);
			lastKey = "";
			filterPatient();
		}
		try {
			if (patientTable.getRowCount() > 0)
				patientTable.setRowSelectionInterval(0, 0);
		} catch (Exception e1) {
		}
		searchPatientTextField.requestFocus();

	}

	private void filterPatient2() {

		TOTAL_ROWS = (new PregnancyPatientBrowserModel()).total_row;

		model = new PregnancyPatientBrowserModel(null, START_INDEX, PAGE_SIZE);

		// patientTable.setModel(model);
		model.fireTableDataChanged();
		previous.setEnabled(false);
		if (PAGE_SIZE > TOTAL_ROWS)
			next.setEnabled(false);
		patientTable.setAutoCreateRowSorter(true);
		initialiseCombo(pagesCombo, TOTAL_ROWS);
		patientTable.updateUI();

	}

	private void filterPatient() {
		patientTable.setModel(new PregnancyPatientBrowserModel());
	}

	/**
	 * fill the pregnancy details with abortions count and deliveries count
	 */
	private void filterPregnancyDetails() {
		// TODO the abort count and the delivery details
		for (int a = 0; a < deltypeLabel.size(); a++) {
			deltypeLabel.get(a).setText("");
			deltypeResLabel.get(a).setText("");
		}
	}

	private void filterVisit() {
		visitTable.setModel(new PregnancyVisitBrowserModel());
		try {
			if (visitTable.getRowCount() > 0)
				visitTable.setRowSelectionInterval(0, 0);
		} catch (Exception e1) {

		}
	}

	class TableListener implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			int row = patientTable.getSelectedRow();
			if (arg0.getValueIsAdjusting() && row > -1) {
				AdmittedPatient adPatient = (AdmittedPatient) patientTable.getValueAt(patientTable.getSelectedRow(),
						-1);
				patient = adPatient.getPatient();
				filterPregnancyDetails();
				filterVisit();
			}
		}
	}

	@Override
	public void admissionUpdated(AWTEvent e) {

	}

	@Override
	public void admissionInserted(AWTEvent e) {

	}

	public void initialiseCombo(JComboBox pagesCombo, int total_rows) {
		int j = 0;
		pagesCombo.removeAllItems();
		for (int i = 0; i < total_rows / PAGE_SIZE; i++) {
			j = i + 1;
			pagesCombo.addItem(j);
		}
		if (j * PAGE_SIZE < total_rows) {
			pagesCombo.addItem(j + 1);
			under.setText("/" + (total_rows / PAGE_SIZE + 1 + " Pages"));
		} else {
			under.setText("/" + total_rows / PAGE_SIZE + " Pages");
		}

	}
}