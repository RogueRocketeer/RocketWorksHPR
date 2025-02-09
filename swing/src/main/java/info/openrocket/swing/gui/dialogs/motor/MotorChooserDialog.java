package info.openrocket.swing.gui.dialogs.motor;


import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;
import info.openrocket.swing.gui.dialogs.motor.thrustcurve.ThrustCurveMotorSelectionPanel;
import info.openrocket.swing.gui.util.GUIUtil;
import info.openrocket.core.l10n.Translator;
import info.openrocket.core.motor.Motor;
import info.openrocket.core.rocketcomponent.FlightConfigurationId;
import info.openrocket.core.rocketcomponent.MotorMount;
import info.openrocket.core.startup.Application;

@SuppressWarnings("serial")
public class MotorChooserDialog extends JDialog implements CloseableDialog {

	private final ThrustCurveMotorSelectionPanel selectionPanel;
	
	private boolean okClicked = false;
	private static final Translator trans = Application.getTranslator();
	
	public MotorChooserDialog(MotorMount mount, FlightConfigurationId currentConfigID, Window owner) {
		this(owner);
		setMotorMountAndConfig( currentConfigID, mount);
	}
	
	public MotorChooserDialog(Window owner) {
		super(owner, trans.get("MotorChooserDialog.title"), Dialog.ModalityType.APPLICATION_MODAL);
		
		// We're going to reuse this dialog so only hide it when it's closed.
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		
		JPanel panel = new JPanel(new MigLayout("fill"));
		
		selectionPanel = new ThrustCurveMotorSelectionPanel();
		
		panel.add(selectionPanel, "grow, wrap");
		
		
		// OK / Cancel buttons
		JButton okButton = new JButton(trans.get("dlg.but.ok"));
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close(true);
			}
		});
		panel.add(okButton, "tag ok, spanx, split");
		
		//// Cancel button
		JButton cancelButton = new JButton(trans.get("dlg.but.cancel"));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close(false);
			}
		});
		panel.add(cancelButton, "tag cancel");
		
		this.add(panel);
		
		this.setModal(true);
		this.pack();
		this.setLocationByPlatform(true);
		Action closeAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent event) {
				close(false);
			}
		};
		GUIUtil.installEscapeCloseOperation(this, closeAction);
		
		JComponent focus = selectionPanel.getDefaultFocus();
		if (focus != null) {
			focus.grabFocus();
		}
		
		// Set the closeable dialog after all initialization
		selectionPanel.setCloseableDialog(this);

		GUIUtil.setWindowIcons(this);
	}
	
	public void setMotorMountAndConfig( FlightConfigurationId _fcid, MotorMount _mount ) {
		selectionPanel.setMotorMountAndConfig( _fcid, _mount );
	}
	
	/**
	 * Return the motor selected by this chooser dialog, or <code>null</code> if the selection has been aborted.
	 * 
	 * @return	the selected motor, or <code>null</code> if no motor has been selected or the selection was canceled.
	 */
	public Motor getSelectedMotor() {
		if (!okClicked)
			return null;
		return selectionPanel.getSelectedMotor();
	}
	
	/**
	 * Return the selected ejection charge delay.
	 * 
	 * @return	the selected ejection charge delay.
	 */
	public double getSelectedDelay() {
		return selectionPanel.getSelectedDelay();
	}

	public void open() {
		// Update the motor selection based on the motor table value that was already selected in a previous session.
		selectionPanel.selectMotorFromTable();
		setVisible(true);
	}

	@Override
	public void close(boolean ok) {
		okClicked = ok;
		this.setVisible(false);
		
		Motor selected = getSelectedMotor();
		if (okClicked && selected != null) {
			selectionPanel.selectedMotor(selected);
		}
	}
	
}
