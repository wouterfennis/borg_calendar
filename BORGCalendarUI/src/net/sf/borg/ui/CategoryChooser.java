/*
This file is part of BORG.
    BORG is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.
    BORG is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    You should have received a copy of the GNU General Public License
    along with BORG; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
Copyright 2003 by Mike Berger
 */

package net.sf.borg.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sf.borg.common.util.Errmsg;

import net.sf.borg.model.AppointmentModel;
import net.sf.borg.model.CategoryModel;
/**
 *
 * @author  mberger
 */
public class CategoryChooser extends View {
 
    private ArrayList cbs = null;
    
    private static CategoryChooser singleton = null;
    static CategoryChooser getReference() {
        if( singleton == null || !singleton.isShowing())
            singleton = new CategoryChooser();
        return( singleton );
    }
    /** Creates new form CategoryChooser */
    private CategoryChooser() {
        
        addModel( AppointmentModel.getReference());
        
        initComponents();
        
        // add current categories
        try{
            CategoryModel catmod = CategoryModel.getReference();
            Collection curcats = catmod.getShownCategories();
            Collection allcats = catmod.getCategories();
            
            if( allcats == null ) {
                allcats = new TreeSet();
            }
            
            cbs = new ArrayList();
            
            Iterator it = allcats.iterator();
            while( it.hasNext() ) {
                String cat = (String) it.next();
                JCheckBox cb = new JCheckBox(cat);
                cbs.add( cb );
                if( curcats != null && curcats.contains(cat))
                    cb.setSelected(true);
                jPanel1.add(cb);
            }
        }
        catch( Exception e) {
            Errmsg.errmsg(e);
        }
        
        pack();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents()//GEN-BEGIN:initComponents
    {
        java.awt.GridBagConstraints gridBagConstraints;

        GridLayout gridLayout2 = new GridLayout();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("catchooser"));
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                exitForm(evt);
            }
        });

        jPanel1.setLayout(new java.awt.GridLayout(0, 1));

        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        gridBagConstraints = new java.awt.GridBagConstraints();
        jPanel2.setLayout(gridLayout2);
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jPanel1, gridBagConstraints);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/Save16.gif")));
        jButton1.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("apply"));
        jButton1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/Stop16.gif")));
        jButton2.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Dismiss"));
        jButton2.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton2ActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        this.setSize(250, 147);
        this.setContentPane(getJPanel());
        gridLayout2.setRows(1);
        jPanel2.add(getJButton3(), null);
        jPanel2.add(getJButton(), null);
        jPanel2.add(jButton1, null);
        jPanel2.add(jButton2, null);
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(jPanel2, gridBagConstraints);

        pack();
    }//GEN-END:initComponents
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // set categories
        TreeSet newcats = new TreeSet();
        Iterator it = cbs.iterator();
        while( it.hasNext()) {
            JCheckBox cb = (JCheckBox) it.next();
            if( cb.isSelected()) {
                newcats.add( cb.getText());
            }
        }
        CategoryModel.getReference().setShownCategories(newcats);
        
    }//GEN-LAST:event_jButton1ActionPerformed
    
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        this.dispose();
    }//GEN-LAST:event_exitForm
    
	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 1;
			gridBagConstraints2.insets = new java.awt.Insets(0,0,0,0);
			gridBagConstraints2.weighty = 0.0D;
			gridBagConstraints2.weightx = 1.0D;
			gridBagConstraints11.weightx = 1.0;
			gridBagConstraints11.weighty = 1.0;
			gridBagConstraints11.fill = java.awt.GridBagConstraints.BOTH;
			jPanel.add(jPanel2, gridBagConstraints2);
			jPanel.add(getJScrollPane(), gridBagConstraints11);
		}
		return jPanel;
	}
	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setPreferredSize(new java.awt.Dimension(40,200));
			jScrollPane.setViewportView(jPanel1);
		}
		return jScrollPane;
	}
	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("clear_all"));
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
			        Iterator it = cbs.iterator();
			        while( it.hasNext()) {
			            JCheckBox cb = (JCheckBox) it.next();
			            cb.setSelected(false);
			        }
				}
			});
		}
		return jButton;
	}
	/**
	 * This method initializes jButton3	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton3() {
		if (jButton3 == null) {
			jButton3 = new JButton();
			jButton3.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("select_all"));
			jButton3.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
			        Iterator it = cbs.iterator();
			        while( it.hasNext()) {
			            JCheckBox cb = (JCheckBox) it.next();
			            cb.setSelected(true);
			        }
				}
			});
		}
		return jButton3;
	}
        /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new CategoryChooser().setVisible(true);
    }
    
    public void destroy() {
        this.dispose();
    }
    
    public void refresh() {
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
    
	private JPanel jPanel = null;
	private JScrollPane jScrollPane = null;
	private JButton jButton = null;
	private JButton jButton3 = null;
}  //  @jve:decl-index=0:visual-constraint="180,110"
