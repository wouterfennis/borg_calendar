
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
 
Copyright 2003 by ==Quiet==
 */

package net.sf.borg.ui;

import java.awt.Font;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import net.sf.borg.common.app.AppHelper;
import net.sf.borg.common.ui.NwFontChooserS;
import net.sf.borg.common.util.Errmsg;
import net.sf.borg.common.util.PrefName;
import net.sf.borg.common.util.Prefs;
import net.sf.borg.common.util.Resource;
import net.sf.borg.common.util.Version;
import net.sf.borg.model.AddressModel;
import net.sf.borg.model.AppointmentModel;
import net.sf.borg.model.TaskModel;




// propgui displays the edit preferences window
public class OptionsView extends View
{
    static
    {
        Version.addVersion("$Id$");
    }
    
	// to break a dependency with the contol package
	public interface RestartListener
	{
		public void restart();
	}
    
    private CalendarView cg_;     // the parent calendar window
    private RestartListener rl_;  // someone to call to request a restart
    
    OptionsView(CalendarView cg, RestartListener rl)
    {
        super();
        
        addModel(AppointmentModel.getReference());
        cg_ = cg;
        rl_ = rl;
        initComponents();
        
        // set the various screen items based on the existing user preferences
        
        // color print option
        String cp = Prefs.getPref(PrefName.COLORPRINT);
        if( cp.equals("true") )
            colorprint.setSelected(true);
        else
            colorprint.setSelected(false);
        
        // options to show public and private appts
        cp = Prefs.getPref(PrefName.SHOWPUBLIC );
        if( cp.equals("true") )
            pubbox.setSelected(true);
        else
            pubbox.setSelected(false);
        cp = Prefs.getPref(PrefName.SHOWPRIVATE);
        if( cp.equals("true") )
            privbox.setSelected(true);
        else
            privbox.setSelected(false);
        
        // database directory
        String dbdir = Prefs.getPref(PrefName.DBDIR);
        jTextField3.setText(dbdir);
        
        // print logo directory
        String logo = Prefs.getPref(PrefName.LOGO);
        logofile.setText(logo);
        if( !logo.equals("") )
            logobox.setSelected(true);
        else
            logobox.setSelected(false);
        
        // email enabled
        cp = Prefs.getPref(PrefName.EMAILENABLED );
        if( cp.equals("true") )
            emailbox.setSelected(true);
        else
            emailbox.setSelected(false);
        
        // email server and address
        cp = Prefs.getPref(PrefName.EMAILSERVER);
        smtptext.setText(cp);
        cp = Prefs.getPref(PrefName.EMAILADDR);
        emailtext.setText(cp);
        
        // set email server and address editable if the email option is
        // enabled
        //smtptext.setEditable( !emailbox.isSelected() );
        //emailtext.setEditable( !emailbox.isSelected() );
        
        // logging is not a preference - check the DB to see if logging is really on
        try
        {
            logging.setSelected( AppointmentModel.getReference().isLogging() );
        }
        catch( Exception e )
        { Errmsg.errmsg(e); }
        
        // US holidays
        String ush = Prefs.getPref(PrefName.SHOWUSHOLIDAYS);
        if( ush.equals("true") )
            holiday1.setSelected(true);
        else
            holiday1.setSelected(false);
        
        // CAN holidays
        ush = Prefs.getPref(PrefName.SHOWCANHOLIDAYS);
        if( ush.equals("true") )
            canadabox.setSelected(true);
        else
            canadabox.setSelected(false);
        
        String csort = Prefs.getPref(PrefName.COLORSORT);
        if( csort.equals("true") )
            colorsortbox.setSelected(true);
        else
            colorsortbox.setSelected(false);
        
        int fdow = Prefs.getIntPref(PrefName.FIRSTDOW );
        if( fdow == Calendar.MONDAY )
            mondaycb.setSelected(true);
        else
            mondaycb.setSelected(false);
        
        String mt = Prefs.getPref(PrefName.MILTIME);
        if( mt.equals("true") )
            miltime.setSelected(true);
        else
            miltime.setSelected(false);
        
        String bg = Prefs.getPref(PrefName.BACKGSTART);
        if( bg.equals("true") )
            backgbox.setSelected(true);
        else
            backgbox.setSelected(false);
        
        String splash = Prefs.getPref(PrefName.SPLASH);
        if( splash.equals("true") )
            splashbox.setSelected(true);
        else
            splashbox.setSelected(false);
        
        String stacktrace = Prefs.getPref(PrefName.STACKTRACE);
        if( stacktrace.equals("true") )
            stackbox.setSelected(true);
        else
            stackbox.setSelected(false);
        
        bg = Prefs.getPref(PrefName.WRAP);
        if( bg.equals("true") )
            wrapbox.setSelected(true);
        else
            wrapbox.setSelected(false);
        
        // auto update check
        int au = Prefs.getIntPref(PrefName.VERCHKLAST );
        if(au != -1 )
            autoupdate.setSelected(true);
        else
            autoupdate.setSelected(false);
        
        // add installed look and feels to lnfBox
        lnfBox.removeAllItems();
        TreeSet lnfs = new TreeSet();
        String curlnf = Prefs.getPref(PrefName.LNF);
        LookAndFeelInfo lnfinfo[] = UIManager.getInstalledLookAndFeels();
        for( int i = 0; i < lnfinfo.length; i++ )
        {
            String name = lnfinfo[i].getClassName();
            lnfs.add(name);
        }
        try
        {
            Class.forName("com.jgoodies.plaf.plastic.PlasticXPLookAndFeel");
            lnfs.add("com.jgoodies.plaf.plastic.PlasticXPLookAndFeel");
        }
        catch( Exception e)
        {}
        try
        {
            Class.forName("com.incors.plaf.kunststoff.KunststoffLookAndFeel");
            lnfs.add("com.incors.plaf.kunststoff.KunststoffLookAndFeel");
        }
        catch( Exception e)
        {}
        
        lnfs.add( curlnf );
        
        
        Iterator it = lnfs.iterator();
        while( it.hasNext())
            lnfBox.addItem( (String) it.next());
        
        lnfBox.setSelectedItem(curlnf);
        
        String shr = Prefs.getPref(PrefName.WKSTARTHOUR);
        String ehr = Prefs.getPref(PrefName.WKENDHOUR);
        wkstarthr.setSelectedItem( shr );
        wkendhr.setSelectedItem( ehr );
        
        // add locales
        String nolocale = Prefs.getPref(PrefName.NOLOCALE);
        if( !nolocale.equals("1"))
        {
            localebox.removeAllItems();
            
            Locale locs[] = Locale.getAvailableLocales();
            for( int i = 0; i < locs.length; i++ )
            {
                //String name = locs[i].
                localebox.addItem(locs[i].getDisplayName());
            }
            
            String currentlocale = Locale.getDefault().getDisplayName();
            localebox.setSelectedItem(currentlocale);
        }
        else
        {
            localebox.setEnabled(false);
        }
        
        // popups
        bg = Prefs.getPref(PrefName.REMINDERS);
        if( bg.equals("true") )
            popenablebox.setSelected(true);
        else
            popenablebox.setSelected(false);
        
        bg = Prefs.getPref(PrefName.BEEPINGREMINDERS);
        if( bg.equals("true") )
            soundbox.setSelected(true);
        else
            soundbox.setSelected(false);
        
        int mins = Prefs.getIntPref(PrefName.REMINDERCHECKMINS);
        checkfreq.setValue(new Integer(mins));
        
        mins = Prefs.getIntPref(PrefName.POPBEFOREMINS);
        popminbefore.setValue(new Integer(mins));
        
        mins = Prefs.getIntPref(PrefName.POPAFTERMINS );
        popminafter.setValue(new Integer(mins));
               
        mins = Prefs.getIntPref(PrefName.BEEPINGMINS);
        beepmins.setValue(new Integer(mins));
         
        bg = Prefs.getPref(PrefName.SHARED);
        if( bg.equals("true") )
            sharedbox.setSelected(true);
        else
            sharedbox.setSelected(false);
        
        bg = Prefs.getPref(PrefName.ICALTODOEV );
        if( bg.equals("true") )
            icaltodobox.setSelected(true);
        else
            icaltodobox.setSelected(false);
            
        logobrowse.setEnabled(AppHelper.isApplication());
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        privbox = new javax.swing.JCheckBox();
        pubbox = new javax.swing.JCheckBox();
        incfont = new javax.swing.JButton();
        decfont = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        lnfBox = new javax.swing.JComboBox();
        holiday1 = new javax.swing.JCheckBox();
        mondaycb = new javax.swing.JCheckBox();
        miltime = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        wkstarthr = new javax.swing.JComboBox();
        wkendhr = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        wrapbox = new javax.swing.JCheckBox();
        canadabox = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        localebox = new javax.swing.JComboBox();
        jButton1 = new javax.swing.JButton();
        colorsortbox = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        chgdb = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        smtptext = new javax.swing.JTextField();
        emailtext = new javax.swing.JTextField();
        emailbox = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        colorprint = new javax.swing.JCheckBox();
        logobox = new javax.swing.JCheckBox();
        logofile = new javax.swing.JTextField();
        logobrowse = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        logging = new javax.swing.JCheckBox();
        autoupdate = new javax.swing.JCheckBox();
        versioncheck = new javax.swing.JButton();
        splashbox = new javax.swing.JCheckBox();
        backgbox = new javax.swing.JCheckBox();
        stackbox = new javax.swing.JCheckBox();
        icaltodobox = new javax.swing.JCheckBox();
        jPanel6 = new javax.swing.JPanel();
        popenablebox = new javax.swing.JCheckBox();
        jLabel9 = new javax.swing.JLabel();
        popminbefore = new javax.swing.JSpinner();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        popminafter = new javax.swing.JSpinner();
        jLabel12 = new javax.swing.JLabel();
        soundbox = new javax.swing.JCheckBox();
        jLabel13 = new javax.swing.JLabel();
        beepmins = new javax.swing.JSpinner();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        checkfreq = new javax.swing.JSpinner();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel16 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        sharedbox = new javax.swing.JCheckBox();
        jButton2 = new javax.swing.JButton();
        applyButton = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Options"));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jPanel2.setName(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("appearance"));
        privbox.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Show_Private_Appointments"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(privbox, gridBagConstraints);

        pubbox.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Show_Public_Appointments"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(pubbox, gridBagConstraints);

        incfont.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("set_pre_font"));
        incfont.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        incfont.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                incfontActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(incfont, gridBagConstraints);

        decfont.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("set_appt_font"));
        decfont.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        decfont.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                decfontActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(decfont, gridBagConstraints);

        jLabel4.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Look_and_Feel:"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(jLabel4, gridBagConstraints);

        lnfBox.setEditable(true);
        lnfBox.setMaximumSize(new java.awt.Dimension(131, 24));
        lnfBox.setPreferredSize(new java.awt.Dimension(50, 24));
        lnfBox.setAutoscrolls(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(lnfBox, gridBagConstraints);

        holiday1.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Show_U.S._Holidays"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(holiday1, gridBagConstraints);

        mondaycb.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Week_Starts_with_Monday"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(mondaycb, gridBagConstraints);

        miltime.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Use_24_hour_time_format"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(miltime, gridBagConstraints);

        jLabel5.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Week_View_Start_Hour:_"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(jLabel5, gridBagConstraints);

        wkstarthr.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "4", "5", "6", "7", "8", "9", "10", "11" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(wkstarthr, gridBagConstraints);

        wkendhr.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(wkendhr, gridBagConstraints);

        jLabel6.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Week_View_End_Hour:_"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(jLabel6, gridBagConstraints);

        wrapbox.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Wrap_Appointment_Text"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(wrapbox, gridBagConstraints);

        canadabox.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Show_Canadian_Holidays"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(canadabox, gridBagConstraints);

        jLabel8.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("locale"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(jLabel8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(localebox, gridBagConstraints);

        jButton1.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("set_def_font"));
        jButton1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(jButton1, gridBagConstraints);

        colorsortbox.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("colorsort"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel2.add(colorsortbox, gridBagConstraints);

        jTabbedPane1.addTab(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("appearance"), jPanel2);

        jPanel4.setLayout(new java.awt.GridBagLayout());

        jLabel3.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("DataBase_Directory_or_URL"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        jPanel4.add(jLabel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(4, 8, 4, 8);
        jPanel4.add(jTextField3, gridBagConstraints);

        jButton5.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Browse"));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel4.add(jButton5, gridBagConstraints);

        chgdb.setForeground(new java.awt.Color(255, 51, 51));
        chgdb.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Apply_DB_Change"));
        chgdb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chgdbActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        jPanel4.add(chgdb, gridBagConstraints);

        jTabbedPane1.addTab(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("DatabaseInformation"), jPanel4);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("SMTP_Server"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPanel1.add(jLabel1, gridBagConstraints);

        jLabel2.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Your_Email_Address"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        jPanel1.add(jLabel2, gridBagConstraints);

        smtptext.setColumns(30);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(smtptext, gridBagConstraints);

        emailtext.setColumns(30);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(emailtext, gridBagConstraints);

        emailbox.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Enable_Email"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(emailbox, gridBagConstraints);

        jTabbedPane1.addTab(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("EmailParameters"), jPanel1);

        jPanel5.setLayout(new java.awt.GridBagLayout());

        colorprint.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Print_In_Color?"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        jPanel5.add(colorprint, gridBagConstraints);

        logobox.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Print_Logo_(GIF/JPG/PNG)"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel5.add(logobox, gridBagConstraints);

        logofile.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 8);
        jPanel5.add(logofile, gridBagConstraints);

        logobrowse.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Browse"));
        logobrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logobrowseActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel5.add(logobrowse, gridBagConstraints);

        jTabbedPane1.addTab(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("printing"), jPanel5);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        logging.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Enable_Logging_(requires_program_restart)"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(logging, gridBagConstraints);

        autoupdate.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Auto_Update_Check"));
        autoupdate.setToolTipText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Enable_a_daily_check_to_the_BORG_website_to_see_if_a_new_version_is_out._Does_not_update_the_product."));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(autoupdate, gridBagConstraints);

        versioncheck.setFont(new java.awt.Font("Dialog", 0, 10));
        versioncheck.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Check_for_updates_now"));
        versioncheck.setToolTipText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Check_for_the_latest_BORG_version_now"));
        versioncheck.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        versioncheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                versioncheckActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(versioncheck, gridBagConstraints);

        splashbox.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("splash"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel3.add(splashbox, gridBagConstraints);

        backgbox.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Start_in_background_(Windows_only,_TrayIcon_req)"));
        backgbox.setToolTipText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Do_not_open_todo_and_month_view_on_startup,_start_in_systray"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(backgbox, gridBagConstraints);

        stackbox.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("stackonerr"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel3.add(stackbox, gridBagConstraints);

        icaltodobox.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("icaltodo"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel3.add(icaltodobox, gridBagConstraints);

        jTabbedPane1.addTab(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("misc"), jPanel3);

        jPanel6.setLayout(new java.awt.GridBagLayout());

        popenablebox.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("enable_popups"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        jPanel6.add(popenablebox, gridBagConstraints);

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel9.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("pop_app"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 8);
        jPanel6.add(jLabel9, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel6.add(popminbefore, gridBagConstraints);

        jLabel10.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("min_bef_app"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 8);
        jPanel6.add(jLabel10, gridBagConstraints);

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel11.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("pop_app"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 8);
        jPanel6.add(jLabel11, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel6.add(popminafter, gridBagConstraints);

        jLabel12.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("min_aft_app"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 8);
        jPanel6.add(jLabel12, gridBagConstraints);

        soundbox.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("beeps"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        jPanel6.add(soundbox, gridBagConstraints);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel13.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("beepingstarts"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 8);
        jPanel6.add(jLabel13, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel6.add(beepmins, gridBagConstraints);

        jLabel14.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("min_bef_app"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 8);
        jPanel6.add(jLabel14, gridBagConstraints);

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel15.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("min_between_chks"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 8);
        jPanel6.add(jLabel15, gridBagConstraints);

        checkfreq.setMinimumSize(new java.awt.Dimension(50, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel6.add(checkfreq, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(8, 8, 8, 8);
        jPanel6.add(jSeparator1, gridBagConstraints);

        jLabel16.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("restart_req"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        jPanel6.add(jLabel16, gridBagConstraints);

        jTabbedPane1.addTab(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("popup_reminders"), jPanel6);

        sharedbox.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("shared"));
        jPanel7.add(sharedbox);

        jTabbedPane1.addTab(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Multi_User"), jPanel7);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jTabbedPane1, gridBagConstraints);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/Stop16.gif")));
        jButton2.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("Dismiss"));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jButton2, gridBagConstraints);

        applyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/Save16.gif")));
        applyButton.setText(java.util.ResourceBundle.getBundle("resource/borg_resource").getString("apply"));
        applyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                apply(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(applyButton, gridBagConstraints);

        pack();
    }//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton2ActionPerformed
    {//GEN-HEADEREND:event_jButton2ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed


    private void apply(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_apply
        applyChanges();
    }//GEN-LAST:event_apply

    private void applyChanges()
    {
        if( icaltodobox.isSelected() )
            Prefs.putPref(PrefName.ICALTODOEV, "true" );
        else
            Prefs.putPref(PrefName.ICALTODOEV, "false" );
        
        if( colorsortbox.isSelected() )
            Prefs.putPref(PrefName.COLORSORT, "true" );
        else
            Prefs.putPref(PrefName.COLORSORT, "false" );
        
        if( stackbox.isSelected() )
            Prefs.putPref(PrefName.STACKTRACE, "true" );
        else
            Prefs.putPref(PrefName.STACKTRACE, "false" );
        
        if( sharedbox.isSelected() )
            Prefs.putPref(PrefName.SHARED, "true" );
        else
            Prefs.putPref(PrefName.SHARED, "false" );
        
        Integer i = (Integer) beepmins.getValue();
        int cur = Prefs.getIntPref(PrefName.BEEPINGMINS);
        if( i.intValue() != cur )
            Prefs.putPref(PrefName.BEEPINGMINS, i);
        
        i = (Integer) popminafter.getValue();
        cur = Prefs.getIntPref(PrefName.POPAFTERMINS);
        if( i.intValue() != cur )
            Prefs.putPref(PrefName.POPAFTERMINS, i);
        
        i = (Integer) popminbefore.getValue();
        cur = Prefs.getIntPref(PrefName.POPBEFOREMINS);
        if( i.intValue() != cur )
            Prefs.putPref(PrefName.POPBEFOREMINS, i);
         
        i = (Integer) checkfreq.getValue();
        cur = Prefs.getIntPref(PrefName.REMINDERCHECKMINS);
        if( i.intValue() != cur )
            Prefs.putPref(PrefName.REMINDERCHECKMINS, i);
        
        if( soundbox.isSelected() )
            Prefs.putPref(PrefName.BEEPINGREMINDERS, "true" );
        else
            Prefs.putPref(PrefName.BEEPINGREMINDERS, "false" );
        
        if( popenablebox.isSelected() )
            Prefs.putPref(PrefName.REMINDERS, "true" );
        else
            Prefs.putPref(PrefName.REMINDERS, "false" );
        
        if( splashbox.isSelected() )
            Prefs.putPref(PrefName.SPLASH, "true" );
        else
            Prefs.putPref(PrefName.SPLASH, "false" );
        
        // update US holiday preference and refresh the month view accordingly
        if( canadabox.isSelected() )
            Prefs.putPref(PrefName.SHOWCANHOLIDAYS, "true" );
        else
            Prefs.putPref(PrefName.SHOWCANHOLIDAYS, "false" );
        
        if( wrapbox.isSelected() )
            Prefs.putPref(PrefName.WRAP, "true" );
        else
            Prefs.putPref(PrefName.WRAP, "false" );
        
        if( backgbox.isSelected() )
            Prefs.putPref(PrefName.BACKGSTART, "true" );
        else
            Prefs.putPref(PrefName.BACKGSTART, "false" );
        
        if( miltime.isSelected() )
            Prefs.putPref(PrefName.MILTIME, "true" );
        else
            Prefs.putPref(PrefName.MILTIME, "false" );
        
        if( mondaycb.isSelected() )
            Prefs.putPref(PrefName.FIRSTDOW, new Integer(Calendar.MONDAY) );
        else
            Prefs.putPref(PrefName.FIRSTDOW, new Integer(Calendar.SUNDAY) );
        
        if( holiday1.isSelected() )
            Prefs.putPref(PrefName.SHOWUSHOLIDAYS, "true" );
        else
            Prefs.putPref(PrefName.SHOWUSHOLIDAYS, "false" );
        
        Prefs.putPref( PrefName.WKENDHOUR, wkendhr.getSelectedItem());
        Prefs.putPref( PrefName.WKSTARTHOUR, wkstarthr.getSelectedItem());
        
        // enable/disable auto-update-check
        // value is the last day-of-year that check was done (1-365)
        // phony value 400 will cause check during current day
        // value -1 is the shut-off value
        if( autoupdate.isSelected() )
            Prefs.putPref(PrefName.VERCHKLAST, new Integer(400) );
        else
            Prefs.putPref(PrefName.VERCHKLAST, new Integer(-1) );
        
        if( privbox.isSelected() )
            Prefs.putPref(PrefName.SHOWPRIVATE, "true" );
        else
            Prefs.putPref(PrefName.SHOWPRIVATE, "false" );
        
        // update the show public option and refresh the month view
        if( pubbox.isSelected() )
            Prefs.putPref(PrefName.SHOWPUBLIC, "true" );
        else
            Prefs.putPref(PrefName.SHOWPUBLIC, "false" );
        
        if( colorprint.isSelected() )
            Prefs.putPref(PrefName.COLORPRINT, "true" );
        else
            Prefs.putPref(PrefName.COLORPRINT, "false" );
        
        if( !logobox.isSelected() )
        {
            Prefs.putPref(PrefName.LOGO, "" );
            logofile.setText("");
        }
        else
        {
            Prefs.putPref(PrefName.LOGO, logofile.getText() );
        }
        
        if( emailbox.isSelected() )
        {
            Prefs.putPref(PrefName.EMAILENABLED, "true" );
            Prefs.putPref(PrefName.EMAILSERVER, smtptext.getText() );
            Prefs.putPref(PrefName.EMAILADDR, emailtext.getText() );
        }
        else
            Prefs.putPref(PrefName.EMAILENABLED, "false" );
        
        // turn logging on/off
        try
        {
            
            if( logging.isSelected() )
            {
            
                AppointmentModel.getReference().setLogging(true);
				AddressModel.getReference().setLogging(true);
				TaskModel.getReference().setLogging(true);
            }
            else
            {
            
			   	AppointmentModel.getReference().setLogging(false);
			  	AddressModel.getReference().setLogging(false);
			 	TaskModel.getReference().setLogging(false);
            }
        }
        catch( Exception e )
        {
            Errmsg.errmsg(e);
        }
        
        Locale locs[] = Locale.getAvailableLocales();
        String choice = (String) localebox.getSelectedItem();
        for( int ii = 0; ii < locs.length; ii++ )
        {
            if( choice.equals(locs[ii].getDisplayName()))
            {
                Prefs.putPref(PrefName.COUNTRY, locs[ii].getCountry() );
                Prefs.putPref(PrefName.LANGUAGE, locs[ii].getLanguage());
            }
        }
        
        String newlnf = (String) lnfBox.getSelectedItem();
        String oldlnf = Prefs.getPref(PrefName.LNF);
        if( !newlnf.equals(oldlnf) )
        {
            try
            {
                UIManager.setLookAndFeel(newlnf);
                // don't try to change the main window l&f - is doesn't work 100%
                //SwingUtilities.updateComponentTreeUI(cg_);
                Prefs.putPref(PrefName.LNF, newlnf );
            }
            catch( Exception e )
            {
                // Errmsg.notice( "Could not find look and feel: " + newlnf );
                Errmsg.notice( e.toString() );
                return;
            }
        }
        
        try
        {
            cg_.setDayLabels();
            cg_.refresh();
        }
        catch( Exception e )
        { Errmsg.errmsg(e); }
    }
 
  
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       
        Font f = NwFontChooserS.showDialog(null, null,null);
        if( f == null) return;
        String fs = NwFontChooserS.fontString(f);
        Prefs.putPref(PrefName.DEFFONT, fs );
        NwFontChooserS.setDefaultFont(f);
        SwingUtilities.updateComponentTreeUI(this);
        SwingUtilities.updateComponentTreeUI(cg_);
    
    }//GEN-LAST:event_jButton1ActionPerformed
    

    private void chgdbActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_chgdbActionPerformed
    {//GEN-HEADEREND:event_chgdbActionPerformed
        int ret = JOptionPane.showConfirmDialog(null, Resource.getResourceString("Really_change_the_database?"), Resource.getResourceString("Confirm_DB_Change"), JOptionPane.YES_NO_OPTION);
        if( ret == JOptionPane.YES_OPTION )
        {
            String dbdir = jTextField3.getText();
            Prefs.putPref(PrefName.DBDIR, dbdir );
            rl_.restart();
        }
    }//GEN-LAST:event_chgdbActionPerformed
    
 
    private void logobrowseActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_logobrowseActionPerformed
    {//GEN-HEADEREND:event_logobrowseActionPerformed
        
        // browse for new logo file
        logobox.setSelected(true);
        String logo = null;
        while( true )
        {
            JFileChooser chooser = new JFileChooser();
            
            chooser.setCurrentDirectory( new File(".") );
            chooser.setDialogTitle(Resource.getResourceString("Please_choose_the_logo_file_-_GIF/JPG/PNG_only"));
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            
            int returnVal = chooser.showOpenDialog(null);
            if(returnVal != JFileChooser.APPROVE_OPTION)
                return;
            
            logo = chooser.getSelectedFile().getAbsolutePath();
            File lf = new File(logo);
            String err = null;
            if( !lf.exists() )
            {
                err = Resource.getResourceString("File_[") + logo + Resource.getResourceString("]_does_not_exist");
            }
            else if( !lf.canRead() )
            {
                err = Resource.getResourceString("Database_Directory_[") + logo + Resource.getResourceString("]_is_not_writable");
            }
            
            if( err == null )
                break;
            
            Errmsg.notice( err );
        }
        
 
        // update text field - nothing else changes. DB change will take effect only on restart
        logofile.setText(logo);
    }//GEN-LAST:event_logobrowseActionPerformed
   
    
    private void versioncheckActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_versioncheckActionPerformed
    {//GEN-HEADEREND:event_versioncheckActionPerformed
        try
        {
            // get version and compare
            URL webverurl = new URL("http://borg-calendar.sourceforge.net/latest_version");
            InputStream is = webverurl.openStream();
            int i;
            String webver = "";
            while( true )
            {
                i = is.read();
                if( i == -1 || i == '\n' || i == '\r') break;
                webver += (char )i;
            }
            
            
            String info = Resource.getResourceString("Your_BORG_version_=_") + Resource.getVersion() + Resource.getResourceString("Latest_version_at_sourceforge_=_") + webver;
            JOptionPane.showMessageDialog(null, info, Resource.getResourceString("BORG_Version_Check"), JOptionPane.INFORMATION_MESSAGE, new ImageIcon(getClass().getResource("/resource/borg.jpg")));
        }
        catch( Exception e )
        { Errmsg.errmsg(e); }
        
    }//GEN-LAST:event_versioncheckActionPerformed
   
    
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        
        // browse for new database dir
        String dbdir = OptionsView.chooseDbDir(false);
        if( dbdir == null ) return;
        
        // update text field - nothing else changes. DB change will take effect only on restart
        jTextField3.setText(dbdir);
        
        
    }//GEN-LAST:event_jButton5ActionPerformed
    
    private void incfontActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_incfontActionPerformed
        
        Font f = NwFontChooserS.showDialog(null, null, null);
        if( f == null) return;
        String s = NwFontChooserS.fontString(f);
  
        Prefs.putPref( PrefName.PREVIEWFONT, s );
        
        // update styles used in month view text panes with new font size
        cg_.updStyles();
        
        try
        {
            // refresh the month view
            cg_.refresh();
        }
        catch( Exception e )
        {
            Errmsg.errmsg(e);
        }
        
    }//GEN-LAST:event_incfontActionPerformed
    
    private void decfontActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_decfontActionPerformed
       
        Font f = NwFontChooserS.showDialog(null, null, null);
        if( f == null) return;
        String s = NwFontChooserS.fontString(f);
  
        Prefs.putPref(PrefName.APPTFONT, s );
        
        // update styles used in month view text panes with new font size
        cg_.updStyles();
        
        try
        {
            // refresh the month view
            cg_.refresh();
        }
        catch( Exception e )
        {
            Errmsg.errmsg(e);
        }
    }//GEN-LAST:event_decfontActionPerformed

        
    
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
        this.dispose();
    }//GEN-LAST:event_exitForm
    
    public void destroy()
    {
        this.dispose();
    }
    
    public void refresh()
    {
    }

	// prompt the user to enter a database directory
	public static String chooseDbDir(boolean update) {
	    
	    String dbdir = null;
	    while( true ) {
	        JFileChooser chooser = new JFileChooser();
	        
	        chooser.setCurrentDirectory( new File(".") );
	        chooser.setDialogTitle("Please choose directory for database files");
	        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	        
	        int returnVal = chooser.showOpenDialog(null);
	        if(returnVal != JFileChooser.APPROVE_OPTION)
	            return(null);
	        
	        dbdir = chooser.getSelectedFile().getAbsolutePath();
	        File dir = new File(dbdir);
	        String err = null;
	        if( !dir.exists() ) {
	            err = "Database Directory [" + dbdir + "] does not exist";
	        }
	        else if( !dir.isDirectory() ) {
	            err = "Database Directory [" + dbdir + "] is not a directory";
	        }
	        else if( !dir.canWrite() ) {
	            err = "Database Directory [" + dbdir + "] is not writable";
	        }
	        
	        if( err == null )
	            break;
	        
	        Errmsg.notice( err );
	    }
	    
	    if( update )
	        Prefs.putPref(PrefName.DBDIR, dbdir );
	    return(dbdir);
	}
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyButton;
    private javax.swing.JCheckBox autoupdate;
    private javax.swing.JCheckBox backgbox;
    private javax.swing.JSpinner beepmins;
    private javax.swing.JCheckBox canadabox;
    private javax.swing.JSpinner checkfreq;
    private javax.swing.JButton chgdb;
    private javax.swing.JCheckBox colorprint;
    private javax.swing.JCheckBox colorsortbox;
    private javax.swing.JButton decfont;
    private javax.swing.JCheckBox emailbox;
    private javax.swing.JTextField emailtext;
    private javax.swing.JCheckBox holiday1;
    private javax.swing.JCheckBox icaltodobox;
    private javax.swing.JButton incfont;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JComboBox lnfBox;
    private javax.swing.JComboBox localebox;
    private javax.swing.JCheckBox logging;
    private javax.swing.JCheckBox logobox;
    private javax.swing.JButton logobrowse;
    private javax.swing.JTextField logofile;
    private javax.swing.JCheckBox miltime;
    private javax.swing.JCheckBox mondaycb;
    private javax.swing.JCheckBox popenablebox;
    private javax.swing.JSpinner popminafter;
    private javax.swing.JSpinner popminbefore;
    private javax.swing.JCheckBox privbox;
    private javax.swing.JCheckBox pubbox;
    private javax.swing.JCheckBox sharedbox;
    private javax.swing.JTextField smtptext;
    private javax.swing.JCheckBox soundbox;
    private javax.swing.JCheckBox splashbox;
    private javax.swing.JCheckBox stackbox;
    private javax.swing.JButton versioncheck;
    private javax.swing.JComboBox wkendhr;
    private javax.swing.JComboBox wkstarthr;
    private javax.swing.JCheckBox wrapbox;
    // End of variables declaration//GEN-END:variables
    
}
