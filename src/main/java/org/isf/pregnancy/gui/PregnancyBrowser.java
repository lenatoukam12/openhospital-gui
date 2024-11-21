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
import org.isf.utils.jobjects.ModalJFrame;

public class PregnancyBrowser extends ModalJFrame implements PatientInsert.PatientListener, PatientInsertExtended.PatientListener, AdmissionListener{


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
	private PregnancyBrowser myFrame = null;
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
	
	@Override
	public void patientInserted(AWTEvent event) {
	    // Implémentation de la logique pour gérer un patient inséré
	}

	@Override
	public void admissionInserted(AWTEvent event) {
	    // Implémentation de la logique pour gérer une admission insérée
	}

	@Override
	public void patientUpdated(AWTEvent event) {
	    // Implémentation de la logique pour gérer un patient mis à jour
	}

	@Override
	public void admissionUpdated(AWTEvent event) {
	    // Implémentation de la logique pour gérer une admission mise à jour
	}

	
	/**
	 * This method initializes
	 */
	public PregnancyBrowser() {
		super();
		myFrame = this;
		myFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// to free memory
				if (pregnancyPatientList != null)
					pregnancyPatientList.clear();
				dispose();
			}
		});
		initialize();
		setLocationRelativeTo(null);
	}
	
	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setTitle(MessageBundle.getMessage("angal.pregnancy.patientsbrowser.title"));
//		this.setContentPane(getJContainPanel());
//		this.setMinimumSize(new Dimension(400 + getJTableWidth(), 700));
//		rowCounter.setText(rowCounterText + pSur.size());
		validate();
	}
}