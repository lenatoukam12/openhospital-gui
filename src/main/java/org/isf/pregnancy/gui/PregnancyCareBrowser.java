/**
 * 
 */
/**
 * 
 */
package org.isf.pregnancy.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.isf.admission.gui.AdmissionBrowser.AdmissionListener;
import org.isf.admission.model.AdmittedPatient;
import org.isf.generaldata.MessageBundle;
import org.isf.patient.gui.PatientInsert;
import org.isf.patient.gui.PatientInsertExtended;
import org.isf.patient.model.Patient;
//import org.isf.pregnancy.gui.PregnancyCareBrowser.PregnancyPatientBrowserModel;
//import org.isf.pregnancy.manager.PregnancyCareManager;
//import org.isf.pregnancy.manager.PregnancyDeliveryManager;
//import org.isf.pregnancy.model.PregnancyVisit;

public class PregnancyCareBrowser extends JFrame implements PatientInsert.PatientListener, PatientInsertExtended.PatientListener, PregnancyEdit.PregnancyListener , AdmissionListener{


	private String[] pColums = {
			MessageBundle.getMessage("angal.admission.code"),
			MessageBundle.getMessage("angal.admission.name"),
			MessageBundle.getMessage("angal.admission.age"),
			MessageBundle.getMessage("angal.admission.address") };
	private int[] pColumwidth = { 20, 200, 20, 150 };
	private String[] vColums = {
			MessageBundle.getMessage("angal.pregnancy.pregnancynumber"),
			MessageBundle.getMessage("angal.pregnancy.visitdate"),
			MessageBundle.getMessage("angal.pregnancy.visittype"),
			MessageBundle.getMessage("angal.pregnancy.visitnote") };
	
	private int[] vColumwidth = { 20, 40, 40, 220 };
	private static final long serialVersionUID = 1L;
	private PregnancyCareBrowser myFrame = null;
	private List<AdmittedPatient> pregnancyPatientList = null;
	List<AdmittedPatient> patientList = new  ArrayList<AdmittedPatient>();
//	private List<PregnancyVisit> pregnancyvisits = null;
//	private PregnancyCareManager manager = new PregnancyCareManager();
//	private PregnancyDeliveryManager pregdelManager = new PregnancyDeliveryManager();
	private JTable patientTable = null;
	private JTable visitTable = null;
	private JButton newPatientButton = null;
	private JButton editPatientButton = null;
	private JButton deletePatientButton = null;
	private Patient patient = null;
//	private PregnancyVisit pvisit;
	private JScrollPane patientScrollPane = null;
	private JScrollPane visitScrollPane = null;
	private JTextField searchPatientTextField = null;
	private List<JLabel> deltypeLabel = null;
	private List<JLabel> deltypeResLabel = null;
	private JButton jSearchButton = null;
	private String lastKey = "";
	private JButton jButtonExams;
	private JButton jButtonVaccin;
	private DefaultTableModel model;
	
	JButton next = new JButton(">");
	JButton previous = new JButton("<");
	JComboBox pagesCombo = new JComboBox();
    JLabel under = new JLabel("/ 0 Page");
	private static int PAGE_SIZE = 50;
	private int START_INDEX = 0;
	private int TOTAL_ROWS;
	/**
	 * Constructor called from the main menu
	 */
	public PregnancyCareBrowser() {
		setTitle(MessageBundle.getMessage("angal.pregnancy.patientsbrowser"));
		myFrame = this;
//		pregnancyvisits = new List<PregnancyVisit>();
//		initComponents();
		pack();
		setLocationRelativeTo(null);
		this.setVisible(true);
		myFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// to free memory
				if (pregnancyPatientList != null)
					pregnancyPatientList.clear();
//				if (pregnancyvisits != null)
//					pregnancyvisits.clear();
				dispose();
			}
		});
	}
}