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
package net.sf.borg.model;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.CategoryList;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.Categories;
import net.fortuna.ical4j.model.property.Clazz;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Duration;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Summary;
import net.sf.borg.common.io.IOHelper;
import net.sf.borg.common.util.Prefs;


public class AppointmentIcalAdapter {
	static public void exportIcal(String filename) throws Exception {
		
		ComponentList clist = new ComponentList();	
		boolean showpriv = false;
		if( Prefs.getPref("showprivate", "false" ).equals("true") )
			showpriv = true;
		boolean todo_as_ev = false;
		if( Prefs.getPref("ical_todo_ev", "false" ).equals("true") )
			todo_as_ev = true;		
		Iterator it = AppointmentModel.getReference().getAllAppts().iterator();
		while( it.hasNext() )
		{
			CategoryList catlist = new CategoryList();
			Appointment ap = (Appointment) it.next();
			Component ve = null;
			if( ap.getTodo() && !todo_as_ev)
			{
				ve = new VToDo();
			}
			else
			{
				ve = new VEvent();
			}
			
			// add text
			Summary sum = new Summary( ap.getText());
			ve.getProperties().add(sum);
			
			// date
			if( AppointmentModel.isNote(ap))
			{
				ParameterList pl = new ParameterList();
				pl.add(new Value(Value.DATE));
				DtStart dts = new DtStart(pl, ap.getDate());
				ve.getProperties().add(dts);
			}
			else
			{
				DtStart dts = new DtStart(ap.getDate());
				ve.getProperties().add(dts);
			}
			
			// duration
			if( ap.getDuration() != null )
			{
				ve.getProperties().add( new Duration( ap.getDuration().intValue()*60));
			}
			
			// vacation is a category
			if( ap.getVacation() != null && ap.getVacation().intValue() != 0)
			{
				catlist.add( "Vacation");
			}
			
			// holiday is a category
			if( ap.getHoliday() != null && ap.getHoliday().intValue() != 0)
			{
				catlist.add( "Holidays");
			}
			
			// private
			if( ap.getPrivate() && !showpriv )
			{
				ve.getProperties().add( new Clazz(Clazz.PRIVATE));
			}
			
			// add color as a cetegory
			if( ap.getColor() != null && !ap.getColor().equals(""))
			{
				catlist.add( ap.getColor() );
			}
			
			if( ap.getCategory() != null && !ap.getCategory().equals(""))
			{
				catlist.add( ap.getCategory() );
			}
			
			if( !catlist.isEmpty() )
			{
				ve.getProperties().add( new Categories(catlist) );
			}
			
			// repeat stuff
			if(ap.getRepeatFlag())
			{
				// build recur string
				String rec = "FREQ=";
				String freq = ap.getFrequency();
				if( freq == null )
				{
					continue;
				}
				if( freq.equals("daily"))
				{
					rec += "DAILY";
				}
				else if( freq.equals("weekly"))
				{
					rec += "WEEKLY";
				}
				else if( freq.equals("biweekly"))
				{
					rec += "WEEKLY;INTERVAL=2";
				}
				else if( freq.equals("monthly"))
				{
					Date dd = ap.getDate();
					GregorianCalendar gc = new GregorianCalendar();
					gc.setTime(dd);
					rec += "MONTHLY;BYMONTHDAY=" + gc.get(GregorianCalendar.DATE);
				}
				else if( freq.equals("yearly"))
				{
					rec += "YEARLY";
				}
				else
				{
					// bad default - need to fix
					rec += "DAILY";
				}
				
				if( ap.getTimes().intValue() != 9999 )
				{
					rec += ";COUNT=" + ap.getTimes();
				}
				//System.out.println(rec);
				
				ve.getProperties().add( new RRule( new Recur(rec)));

			}
			clist.add(ve);
			
		}
		
		PropertyList pl = new PropertyList();
		pl.add(new ProdId("BORG Calendar"));
		pl.add(new net.fortuna.ical4j.model.property.Version("1","4"));
		net.fortuna.ical4j.model.Calendar cal = new net.fortuna.ical4j.model.Calendar( pl, clist );
		OutputStream oostr = IOHelper.createOutputStream(filename);
		CalendarOutputter op = new CalendarOutputter();
		op.output( cal, oostr );
		oostr.close();
	}
	
	static public void importIcal( String file ) throws Exception
	{
		CalendarBuilder builder = new CalendarBuilder();
		InputStream is = IOHelper.openStream(file);
		Calendar cal = builder.build(is);
		is.close();
		
		AppointmentModel amodel = AppointmentModel.getReference();
		ComponentList clist = cal.getComponents();
		Iterator it = clist.iterator();
		while( it.hasNext())
		{
			Component comp = (Component) it.next();
			if( comp instanceof VEvent || comp instanceof VToDo)
			{
				Appointment ap = amodel.newAppt();
				PropertyList pl = comp.getProperties();
				Property prop = pl.getProperty(Property.SUMMARY);
				if( prop != null )
				{
					ap.setText( prop.getValue());
				}
				
				prop = pl.getProperty(Property.DTSTART);
				if( prop != null)
				{
					DtStart dts = (DtStart) prop;
					Date d = dts.getTime();
					ap.setDate(d);
		            
				}
				
				amodel.saveAppt(ap, true);
				
			}
		}
		
		 
		
	}
}
