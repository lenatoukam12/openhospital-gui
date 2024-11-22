/**
 * 
 */
/**
 * 
 */
package org.isf.pregnancy.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
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
//import org.isf.parameters.manager.Param;
import org.isf.patient.gui.PatientInsert;
import org.isf.patient.gui.PatientInsertExtended;
import org.isf.patient.model.Patient;

public class PregnancyBrowser extends JFrame implements PatientInsert.PatientListener,
		PatientInsertExtended.PatientListener, AdmissionListener {
	
	private static final long serialVersionUID = 1L;
	private String[] pColums = { MessageBundle.getMessage("angal.common.code.txt"),
			MessageBundle.getMessage("angal.common.name.txt"), MessageBundle.getMessage("angal.common.age.txt"),
			MessageBundle.getMessage("angal.common.address.txt") };
	private int[] pColumwidth = { 20, 200, 20, 150 };

	private String[] vColums = { MessageBundle.getMessage("angal.pregnancy.pregnancynumber"),
			MessageBundle.getMessage("angal.pregnancy.visitdate"),
			MessageBundle.getMessage("angal.pregnancy.visittype"),
			MessageBundle.getMessage("angal.pregnancy.visitnote") };
	private int[] vColumwidth = { 20, 40, 40, 220 };

	private PregnancyBrowser myFrame = null;
	private List<AdmittedPatient> pregnancyPatientList = null;
	List<AdmittedPatient> patientList = new ArrayList<AdmittedPatient>();

	private JTable patientTable = null;
	private JTable visitTable = null;
	private JScrollPane patientScrollPane = null;
	private JScrollPane visitScrollPane = null;
	private JButton newPatientButton = null;
	private JButton editPatientButton = null;
	private JButton deletePatientButton = null;

	JButton next  = null;
	JButton previous  = null;
	JComboBox pagesCombo  = null;
	JLabel under = new JLabel("/ 0 Page");
	private static int PAGE_SIZE = 50;
	private int START_INDEX = 0;
	private int TOTAL_ROWS;

	private Patient patient = null;


	private JButton jSearchButton = null;
	private JTextField searchPatientTextField = null;
	private List<JLabel> deltypeLabel = null;
	private List<JLabel> deltypeResLabel = null;

	private String lastKey = "";
	private JButton jButtonExams;
	private JButton jButtonVaccin;
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
	
	public JButton getNextButton() {
		if (next == null) {
			next = new JButton(
					MessageBundle.getMessage("angal.visit.nextarrow.btn"));
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
				}
			);
		}
		return previous;
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

	private JComboBox getComboBoxPages() {
		if (pagesCombo == null) {
			pagesCombo = new JComboBox();
			pagesCombo.setPreferredSize(new Dimension(60, 21));
			pagesCombo.setEditable(true);
			pagesCombo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
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
				}
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

				if (searchPatientTextField.getText().length() > 7) { // Dechencher la recherche lorsqu'on a tape la 6ieme
																		// lettre
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
		{
			JPanel panelPregnantPrint = new JPanel();
			panel.add(panelPregnantPrint, BorderLayout.SOUTH);
			panelPregnantPrint.setLayout(new BorderLayout(0, 0));
			{
				JButton updateDelivery = new JButton(MessageBundle.getMessage("angal.pregnancy.updatedelivery"));
				updateDelivery.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
///
					}
				});
				panelPregnantPrint.add(updateDelivery, BorderLayout.NORTH);
			}

			{
				JButton declarationBirth = new JButton(
						MessageBundle.getMessage("angal.pregnancy.declaration_birth_but"));
				declarationBirth.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
///
					}
				});
				panelPregnantPrint.add(declarationBirth, BorderLayout.CENTER);
			}
			{
				JButton declarationCertificate = new JButton(
						MessageBundle.getMessage("angal.pregnancy.declaration_certificate_but"));
				declarationCertificate.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
///
					}
				});
				panelPregnantPrint.add(declarationCertificate, BorderLayout.SOUTH);
			}
		}
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
			jSearchButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
///
				}
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
		getButtonNewPatient();
		getButtonEditPatient();
		getButtonDelPatient();
		if (newPatientButton.getText().length() > buttonsize)
			buttonsize = newPatientButton.getText().length();
		if (editPatientButton.getText().length() > buttonsize)
			buttonsize = editPatientButton.getText().length();
		if (deletePatientButton.getText().length() > buttonsize)
			buttonsize = deletePatientButton.getText().length();
		newPatientButton.setPreferredSize(new Dimension(180, 30));
		editPatientButton.setPreferredSize(new Dimension(180, 30));
		deletePatientButton.setPreferredSize(new Dimension(180, 30));
		newPatientButton.setMinimumSize(new Dimension(buttonsize + 100, 30));
		editPatientButton.setMinimumSize(new Dimension(buttonsize + 100, 30));
		deletePatientButton.setMinimumSize(new Dimension(buttonsize + 100, 30));
		newPatientButton.setMaximumSize(new Dimension(buttonsize + 150, 30));
		editPatientButton.setMaximumSize(new Dimension(buttonsize + 150, 30));
		deletePatientButton.setMaximumSize(new Dimension(buttonsize + 150, 30));
		buttonPanel.add(newPatientButton);
		buttonPanel.add(editPatientButton);

		buttonPanel.add(deletePatientButton);
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
		buttonPanel.add(getButtonNewPregnancy());
		buttonPanel.add(getButtonNewPrenatalVisit());
		buttonPanel.add(getButtonNewPostnatalVisit());
		buttonPanel.add(getButtonDelivery());
		buttonPanel.add(getButtonVisitDetails());
		if (MainMenu.checkUserGrants("btnadmexamination"))
			buttonPanel.add(getJButtonExams());
		if (MainMenu.checkUserGrants("patientvaccine"))
			buttonPanel.add(getJButtonVaccin());
		// buttonPanel.add(getJButtonVaccin());
		buttonPanel.add(getButtonDeleteVisit());
		buttonPanel.add(getReportButton());
		buttonPanel.add(getButtonClose());
		return buttonPanel;
	}

	private JButton getButtonNewPatient() {
		newPatientButton = new JButton(MessageBundle.getMessage("angal.admission.newpatient"));

		newPatientButton.setMnemonic(KeyEvent.VK_N);
		newPatientButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
///
			}
		});
		if (patient != null)
			newPatientButton.setEnabled(false);
		return newPatientButton;
	}

	private JButton getButtonEditPatient() {
		editPatientButton = new JButton(MessageBundle.getMessage("angal.admission.editpatient"));

		editPatientButton.setMnemonic(KeyEvent.VK_E);
		editPatientButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if (patientTable.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.pregnancy.pleaseselectpatient"),
							MessageBundle.getMessage("angal.admission.editpatient"), JOptionPane.PLAIN_MESSAGE);
					return;
				}

				AdmittedPatient adPatient = (AdmittedPatient) patientTable.getValueAt(patientTable.getSelectedRow(),
						-1);
				if (adPatient != null) {
					patient = adPatient.getPatient();
				} else {
					patient = null;
				}
///				
			}
		});
		if (patient != null)
			editPatientButton.setEnabled(false);
		return editPatientButton;
	}

	private JButton getButtonDelPatient() {
		deletePatientButton = new JButton(MessageBundle.getMessage("angal.admission.deletepatient"));

		deletePatientButton.setMnemonic(KeyEvent.VK_T);
		deletePatientButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if (patientTable.getSelectedRow() < 0) {
					JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.pregnancy.pleaseselectpatient.mg"),
							MessageBundle.getMessage("angal.admission.deletepatient"), JOptionPane.PLAIN_MESSAGE);
					return;
				}

				AdmittedPatient adPatient = (AdmittedPatient) patientTable.getValueAt(patientTable.getSelectedRow(),
						-1);
				if (adPatient != null) {
					patient = adPatient.getPatient();
				} else {
					patient = null;
				}

				int n = JOptionPane.showConfirmDialog(null,
						MessageBundle.getMessage("angal.admission.deletepatient") + " " + patient.getName() + "?",
						MessageBundle.getMessage("angal.admission.deletepatient"), JOptionPane.YES_NO_OPTION);
///				
			}
		});
		if (patient != null)
			deletePatientButton.setEnabled(false);
		return deletePatientButton;
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

	private JButton getButtonNewPrenatalVisit() {
		JButton buttonNewVisit = new JButton(MessageBundle.getMessage("angal.pregnancy.newprenatalvisit"));

		buttonNewVisit.setMnemonic(KeyEvent.VK_T);
		buttonNewVisit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
///
			}
		});
		return buttonNewVisit;
	}

	private JButton getButtonDeleteVisit() {
		JButton buttonDeleteVisit = new JButton(MessageBundle.getMessage("angal.pregnancy.deletevisit"));

		buttonDeleteVisit.setMnemonic(KeyEvent.VK_T);
		buttonDeleteVisit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
///
			}
		});
		return buttonDeleteVisit;
	}

	private JButton getReportButton() {
		JButton jButtonReport = new JButton();
		jButtonReport.setText(MessageBundle.getMessage("angal.pregnancy.report"));
		jButtonReport.setMnemonic(KeyEvent.VK_R);
		jButtonReport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
///
			}
		});
		return jButtonReport;
	}

	private JButton getButtonNewPostnatalVisit() {
		JButton buttonNewVisit = new JButton(MessageBundle.getMessage("angal.pregnancy.newpostnatalvisit"));

		buttonNewVisit.setMnemonic(KeyEvent.VK_T);
		buttonNewVisit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
///
			}
		});
		return buttonNewVisit;
	}

	private JButton getButtonNewPregnancy() {
		JButton buttonNewPregnancy = new JButton(MessageBundle.getMessage("angal.pregnancy.newpregnancy"));

		buttonNewPregnancy.setMnemonic(KeyEvent.VK_T);
		buttonNewPregnancy.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
///
			}
		});
		return buttonNewPregnancy;
	}

	private JButton getButtonVisitDetails() {
		JButton buttonEditVisit = new JButton(MessageBundle.getMessage("angal.pregnancy.visitdetails"));

		buttonEditVisit.setMnemonic(KeyEvent.VK_T);
		buttonEditVisit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
///
			}
		});
		return buttonEditVisit;
	}

	private JButton getButtonDelivery() {
		JButton buttonDelivery = new JButton(MessageBundle.getMessage("angal.pregnancy.newdelivery"));

		buttonDelivery.setMnemonic(KeyEvent.VK_T);
		buttonDelivery.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
///
			}
		});
		if (patient != null)
			buttonDelivery.setEnabled(false);
		return buttonDelivery;
	}

	private JButton getJButtonExams() {
		if (jButtonExams == null) {

			jButtonExams = new JButton(MessageBundle.getMessage("angal.opd.exams"));

			jButtonExams.setMnemonic(KeyEvent.VK_E);
			jButtonExams.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					if (patientTable.getSelectedRow() < 0) {
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.common.pleaseselectarow.mg"),
								MessageBundle.getMessage("angal.admission.editpatient"), JOptionPane.PLAIN_MESSAGE);
						return;
					}
///					
				}
			});
		}
		return jButtonExams;
	}

	private JButton getJButtonVaccin() {
		if (jButtonVaccin == null) {

			jButtonVaccin = new JButton(MessageBundle.getMessage("angal.cpn.vaccin"));

			jButtonVaccin.setMnemonic(KeyEvent.VK_E);
			jButtonVaccin.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					if (patientTable.getSelectedRow() < 0) {
						JOptionPane.showMessageDialog(null, MessageBundle.getMessage("angal.common.pleaseselectarow.mg"),
								MessageBundle.getMessage("angal.admission.editpatient"), JOptionPane.PLAIN_MESSAGE);
						return;
					}
///
				}
			});
		}
		return jButtonVaccin;
	}

	private JButton getButtonClose() {
		JButton buttonClose = new JButton(MessageBundle.getMessage("angal.pregnancy.close"));
		buttonClose.setMnemonic(KeyEvent.VK_T);
		buttonClose.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if (pregnancyPatientList != null)
					pregnancyPatientList.clear();

			}
		});
		return buttonClose;

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