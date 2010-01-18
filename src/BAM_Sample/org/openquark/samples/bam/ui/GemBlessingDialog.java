/*
 * Copyright (c) 2007 BUSINESS OBJECTS SOFTWARE LIMITED
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *  
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *  
 *     * Neither the name of Business Objects nor the names of its contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */


/*
 * GemBlessingDialog.java
 * Created: 20-Apr-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam.ui;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Collection;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListModel;

import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.metadata.ScopedEntityMetadata;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.services.GemEntity;
import org.openquark.cal.services.GemFilter;
import org.openquark.samples.bam.ActionGemFilter;
import org.openquark.samples.bam.GemManager;
import org.openquark.samples.bam.MetricGemFilter;
import org.openquark.samples.bam.MonitorApp;
import org.openquark.samples.bam.TriggerGemFilter;
import org.openquark.util.ui.DialogBase;


/**
 * 
 *  
 */
class GemBlessingDialog extends DialogBase {

    private static final long serialVersionUID = -5118454765744523233L;
    
    private static final int NO_BLESSING = 0;
    private static final int TRIGGER_BLESSING = 1;
    private static final int ACTION_BLESSING = 2;
    private static final int METRIC_BLESSING = 3;

    /**
     * this filter1 only returns gems that return boolean values
     * @author Magnus Byne
     */
    private static class BooleanGemFilter extends GemFilter {

        //only select gems that return a boolean
        @Override
        public boolean select (GemEntity gemEntity) {
            TypeExpr gemTypeExpr = gemEntity.getTypeExpr();
      
            return gemTypeExpr.getResultType().isNonParametricType(CAL_Prelude.TypeConstructors.Boolean);
        }
    }
    
    /**
     * this filter1 only returns gems of the form [a]->[b], i.e. possible metrics
     * @author Magnus Byne
     */
    private static class ListGemFilter extends GemFilter {

        //only select gems that map from a list to a list
        @Override
        public boolean select (GemEntity gemEntity) {
            TypeExpr gemTypeExpr = gemEntity.getTypeExpr();

            return (gemTypeExpr.getArgumentType() != null &&
                    gemTypeExpr.getArgumentType().isListType() &&
                    gemTypeExpr.getResultType().isListType());
        }
    }

    private static class GemListItem {

        private static Icon triggerBlessingIcon;
        private static Icon actionBlessingIcon;
        private static Icon metricBlessingIcon;
        private static Icon noBlessingIcon;

        private final GemEntity gemEntity;

        private int blessing;

        /**
         * Method getNoBlessingIcon
         * 
         * @return Returns the icon used for gems that have no blessing
         */
        private static Icon getNoBlessingIcon () {
            if (noBlessingIcon == null) {
                noBlessingIcon = loadIcon ("smallBlank.gif"); //$NON-NLS-1$ 
            }

            return noBlessingIcon;
        }

        /**
         * Method getMetricBlessingIcon
         * 
         * @return Returns the icon used for gems that have no blessing
         */
        private static Icon getMetricBlessingIcon () {
            if (metricBlessingIcon == null) {
                metricBlessingIcon = loadIcon ("smallBlank.gif"); //$NON-NLS-1$ 
            }

            return metricBlessingIcon;
        }
        
        /**
         * Method getTriggerBlessingIcon
         * 
         * @return Returns the icon used for gems that are blessed as triggers
         */
        private Icon getTriggerBlessingIcon () {
            if (actionBlessingIcon == null) {
                actionBlessingIcon = loadIcon ("smallTrigger.gif"); //$NON-NLS-1$ 
            }

            return actionBlessingIcon;
        }

        /**
         * Method getActionBlessingIcon
         * 
         * @return Returns the icon used for gems that are blessed as actions
         */
        private Icon getActionBlessingIcon () {
            if (triggerBlessingIcon == null) {
                triggerBlessingIcon = loadIcon ("smallAction.gif"); //$NON-NLS-1$ 
            }

            return triggerBlessingIcon;
        }

        /**
         * Method loadIcon
         * 
         * @param iconFileName
         * @return Returns an Icon loaded from the resource for this class
         */
        private static Icon loadIcon (String iconFileName) {
            String iconFilePath = "/Resources/" + iconFileName; //$NON-NLS-1$ 

            URL url = GemBlessingDialog.class.getResource (iconFilePath);

            if (url != null) {
                return new ImageIcon (url);
            } else {
                return null;
            }
        }
        
        GemListItem (GemEntity gemEntity, int initialBlessing) {
            this.gemEntity = gemEntity;
            this.blessing = initialBlessing;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString () {
            return gemEntity.getName ().getQualifiedName ();
        }

        /**
         * Method getIcon
         * 
         * @return Returns an icon that represents the blessing state of this gem
         */
        public Icon getIcon () {
            switch (blessing) {
                case TRIGGER_BLESSING:
                    return getTriggerBlessingIcon ();
                case ACTION_BLESSING:
                    return getActionBlessingIcon ();
                case METRIC_BLESSING:
                    return getMetricBlessingIcon ();
                default:
                    return getNoBlessingIcon ();
            }
        }

        /**
         * Method setBlessing
         * 
         * @param blessing
         */
        public void setBlessing (int blessing) {
            this.blessing = blessing;
        }

    }

    private static class GemListCellRenderer extends DefaultListCellRenderer {

        private static final long serialVersionUID = -7547133397739989698L;

        /**
         * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object,
         *      int, boolean, boolean)
         */
        @Override
        public Component getListCellRendererComponent (JList list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            Component renderer = super.getListCellRendererComponent (list, value, index, isSelected, cellHasFocus);

            if (renderer instanceof JLabel) {
                if (value instanceof GemListItem) {
                    GemListItem item = (GemListItem)value;

                    ((JLabel)renderer).setIcon (item.getIcon ());
                }
            }

            return renderer;
        }
    }

    private final MonitorApp app;

    /**
     * Constructor GemBlessingDialog
     * 
     * @param owner
     */
    public GemBlessingDialog (Frame owner, MonitorApp app) {
        super (owner, "Manage Gems");

        this.app = app;

        createTopLevelPanel ();
    }

    private JPanel createBlessingTab(GemFilter allFilter, GemFilter blessedFilter, int blessing ) {
        GridBagConstraints constraints = new GridBagConstraints ();
        JPanel panel = new JPanel();
        
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridheight = 2;
        constraints.weightx = 2.0;
        constraints.weighty = 2.0;
        constraints.fill = GridBagConstraints.BOTH;

        panel.add ( createBlessingPanel(allFilter, blessedFilter, blessing));
       
        return panel;
    }
    
    
    /**
     * Method createControls
     * 
     *  
     */
    private void createTopLevelPanel () {

        JTabbedPane tabPane = new JTabbedPane();
        tabPane.addTab("Triggers", createBlessingTab( new BooleanGemFilter(), new TriggerGemFilter(), TRIGGER_BLESSING));
        tabPane.addTab("Actions", createBlessingTab( new BooleanGemFilter(), new ActionGemFilter(), ACTION_BLESSING));
        tabPane.addTab("Metrics", createBlessingTab( new ListGemFilter(), new MetricGemFilter(), METRIC_BLESSING));
              
        getContentPane().add (tabPane);
        pack ();
        addComponentListener (new SizeConstrainer (getSize ()));
    }
    
 
    /**
     * Set the blessing on a gem
     * @param gem
     * @param blessing the value to set the gem blessing to  - NO_BLESSING to clear existing blessing
     */
    private void blessGem(GemEntity gem, int blessing) {
        ScopedEntityMetadata metadata = gem.getMetadata(GemManager.getLocaleForMetadata());
        metadata.setAttribute(TriggerGemFilter.TRIGGER_ATTRIBUTE_NAME, blessing == TRIGGER_BLESSING ? "trigger" : null);
        metadata.setAttribute(ActionGemFilter.ACTION_ATTRIBUTE_NAME, blessing == ACTION_BLESSING ? "action" : null);
        metadata.setAttribute(MetricGemFilter.METRIC_ATTRIBUTE_NAME, blessing == METRIC_BLESSING ? "metric" : null);

        if (!app.getCalServices().getCALWorkspace().saveMetadata(metadata)) {
            JOptionPane.showMessageDialog(this, "The metadata for the gem could not be updated.\nIs the metadata XML file read-only?", "BAM Sample", JOptionPane.ERROR_MESSAGE);    
        }   
    }   
    
    
    private JPanel createBlessingPanel(GemFilter allFilter, GemFilter blessedFilter, int blessing) {
        JPanel panel = new JPanel();
        
        GridBagConstraints constraints = new GridBagConstraints ();
        
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.gridx = 0;
        constraints.gridheight = 2;
        constraints.weightx = 2.0;
        constraints.weighty = 2.0;
        constraints.fill = GridBagConstraints.BOTH;

        JPanel allGemsPanel = new JPanel();
        allGemsPanel.setLayout(new BoxLayout(allGemsPanel, BoxLayout.Y_AXIS)); 
        allGemsPanel.add (new JLabel ("Available Gems"), constraints);

        Set<GemEntity> avaiableGems = app.getCalServices().getMatchingGems(allFilter);
        Set<GemEntity> blessedGems =  app.getCalServices().getMatchingGems(blessedFilter);
        avaiableGems.removeAll(blessedGems);
        
        JList allGemsList=createGemList(avaiableGems);
        allGemsPanel.add(new JScrollPane(allGemsList), constraints);
        
        panel.add(allGemsPanel);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); 
        JButton buttonAdd=new JButton(">");
        JButton buttonRemove=new JButton("<");
        

        buttonPanel.add(buttonAdd);
        buttonPanel.add(buttonRemove);
        panel.add(buttonPanel);
        
        JPanel selectedGemsPanel = new JPanel();
        selectedGemsPanel.setLayout(new BoxLayout(selectedGemsPanel, BoxLayout.Y_AXIS)); 
        selectedGemsPanel.add (new JLabel ("Selected Gems"), constraints);

        JList blessedGemsList=createGemList(blessedGems);
        selectedGemsPanel.add(new JScrollPane(blessedGemsList), constraints);
        panel.add(selectedGemsPanel);
 
        buttonAdd.addActionListener (new ChangeBlessingAction (allGemsList, blessedGemsList, blessing));
        buttonRemove.addActionListener (new ChangeBlessingAction (blessedGemsList, allGemsList,  NO_BLESSING));
        return panel;
        
    }
    
    /**
     * This class is used to handle the event of moving a gem between the blessed and unblessed lists
     * @author Magnus Byne
     */
    class ChangeBlessingAction implements ActionListener{
        private JList fromList;
        private JList toList;
        private int blessing;
        
        ChangeBlessingAction(JList fromList, JList toList, int blessing){
            this.fromList = fromList;
            this.toList = toList;
            this.blessing = blessing;
        }
        
        /**
         * move selected items from the fromList to the toList and set the blessing
         * {@inheritDoc}
         */
        public void actionPerformed (ActionEvent e) {
            Object[] selectedGems= fromList.getSelectedValues();
            DefaultListModel fromListModel = (DefaultListModel) fromList.getModel();
            DefaultListModel toListModel = (DefaultListModel) toList.getModel();
            for (final Object element : selectedGems) {
                GemListItem listItem = (GemListItem)element;                
                fromListModel.removeElement(listItem);
                
                blessGem(listItem.gemEntity, blessing);
                
                toListModel.addElement(new GemListItem (listItem.gemEntity, blessing));
            }   

            toList.setModel(toListModel);
            fromList.setModel(fromListModel);
            
            fromList.setSelectedIndices(new int[0]);
        }
    }    
    
 
    /**
     * Method getGemList
     * 
     * @return Returns the JList that displays available gems
     */
    private JList createGemList (Collection<GemEntity> gemEntities) {
        ListModel gemListModel = makeGemListModel (gemEntities);

        JList gemList = new JList (gemListModel);

        gemList.setCellRenderer (new GemListCellRenderer ());

        return gemList;
    }


    /**
     * Method makeGemListModel
     * 
     * @return Returns a ListModel filled with the gems that can be blessed
     */
    private ListModel makeGemListModel (Collection<GemEntity> gemEntities) {
        DefaultListModel listModel = new DefaultListModel ();

        for (final GemEntity gemEntity : gemEntities) {
            ScopedEntityMetadata metadata = gemEntity.getMetadata(GemManager.getLocaleForMetadata());
            
            int blessing = NO_BLESSING;
            
            if (metadata.getAttribute(TriggerGemFilter.TRIGGER_ATTRIBUTE_NAME) != null) {
                blessing = TRIGGER_BLESSING;
            } else if (metadata.getAttribute(ActionGemFilter.ACTION_ATTRIBUTE_NAME) != null) {
                blessing = ACTION_BLESSING;
            } else if (metadata.getAttribute(MetricGemFilter.METRIC_ATTRIBUTE_NAME) != null) {
                blessing = METRIC_BLESSING;
            }
            
            listModel.addElement (new GemListItem (gemEntity, blessing));
        }

        return listModel;
    }
    
}