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
 * MonitorDocumentEditor.java
 * Created: 19-Mar-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openquark.samples.bam.MonitorApp;
import org.openquark.samples.bam.model.ActionDescription;
import org.openquark.samples.bam.model.MonitorDocument;
import org.openquark.samples.bam.model.MonitorJobDescription;
import org.openquark.samples.bam.model.TriggerDescription;
import org.openquark.util.ui.WatermarkedList;


/**
 * 
 *  
 */
class MonitorDocumentEditor extends JPanel {
    
    private static final long serialVersionUID = 2465696016513825168L;

    private static class MessageListItem {

        private final MonitorJobDescription jobDescription;

        /**
         * Constructor MessageListItem
         * 
         * @param jobDescription
         */
        MessageListItem (final MonitorJobDescription jobDescription) {
            this.jobDescription = jobDescription;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString () {
            return jobDescription.getMessageSourceDescription ().getName ();
        }

    }

    private static class TriggerListItem {

        private final TriggerDescription triggerDescription;

        /**
         * Constructor TriggerListItem
         * 
         * @param description
         */
        public TriggerListItem (TriggerDescription description) {
            this.triggerDescription = description;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString () {
            return triggerDescription.getGemName ();
        }

    }

    private static class ActionListItem {

        private final ActionDescription actionDescription;

        /**
         * Constructor ActionListItem
         * 
         * @param actionDescription
         */
        public ActionListItem (final ActionDescription actionDescription) {
            this.actionDescription = actionDescription;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString () {
            return actionDescription.getGemName ();
        }
    }

    private JList messageList = null;

    private JList triggerList = null;

    private JList actionList = null;

    private class AppPropertyChangeListener implements PropertyChangeListener {

        /**
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        public void propertyChange (PropertyChangeEvent evt) {
            if (evt.getPropertyName ().equals (MonitorApp.DOCUMENT_PROPERTY_NAME)) {
                onChangedDocument ((MonitorDocument)evt.getNewValue ());
            }
        }

    }

    private class DocumentListener implements MonitorDocument.MonitorDocumentListener {

        private MonitorDocument document;

        /**
         * @see org.openquark.samples.bam.model.MonitorDocument.MonitorDocumentListener#jobDescriptionAdded(org.openquark.samples.bam.model.MonitorJobDescription)
         */
        public void jobDescriptionAdded (MonitorJobDescription jobDescription) {
            onJobDescriptionAdded (jobDescription);
        }

        /**
         * @see org.openquark.samples.bam.model.MonitorDocument.MonitorDocumentListener#jobDescriptionRemoved(org.openquark.samples.bam.model.MonitorJobDescription)
         */
        public void jobDescriptionRemoved (MonitorJobDescription jobDescription) {
            onJobDescriptionRemoved (jobDescription);
        }

        void listen (MonitorDocument newDocument) {
            if (document != null) {
                document.removeDocumentListener (this);
            }

            if (newDocument != null) {
                newDocument.addDocumentListener (this);
            }

            document = newDocument;
//System.out.println("DL now listening to " + document);            
        }

    }

    private class JobDescriptionListener implements MonitorJobDescription.MonitorJobDescriptionListener {

        private MonitorJobDescription jobDescription;

        /**
         * @see org.openquark.samples.bam.model.MonitorJobDescription.MonitorJobDescriptionListener#triggerAdded(org.openquark.samples.bam.model.TriggerDescription)
         */
        public void triggerAdded (TriggerDescription triggerDescription) {
            onTriggerAdded (triggerDescription);
        }

        /**
         * @see org.openquark.samples.bam.model.MonitorJobDescription.MonitorJobDescriptionListener#triggerReplaced(org.openquark.samples.bam.model.TriggerDescription, org.openquark.samples.bam.model.TriggerDescription)
         */
        public void triggerReplaced (TriggerDescription oldTriggerDescription, TriggerDescription triggerDescription) {
            onTriggerReplaced (oldTriggerDescription, triggerDescription);
        }

        /**
         * @see org.openquark.samples.bam.model.MonitorJobDescription.MonitorJobDescriptionListener#triggerRemoved(org.openquark.samples.bam.model.TriggerDescription)
         */
        public void triggerRemoved (TriggerDescription triggerDescription) {
            onTriggerRemoved (triggerDescription);
        }

        /**
         * @see org.openquark.samples.bam.model.MonitorJobDescription.MonitorJobDescriptionListener#actionAdded(org.openquark.samples.bam.model.ActionDescription)
         */
        public void actionAdded (ActionDescription actionDescription) {
            onActionAdded (actionDescription);
        }

        /**
         * @see org.openquark.samples.bam.model.MonitorJobDescription.MonitorJobDescriptionListener#actionReplaced(org.openquark.samples.bam.model.ActionDescription, org.openquark.samples.bam.model.ActionDescription)
         */
        public void actionReplaced (ActionDescription oldActionDescription, ActionDescription actionDescription) {
            onActionReplaced (oldActionDescription, actionDescription);
        }


        /**
         * @see org.openquark.samples.bam.model.MonitorJobDescription.MonitorJobDescriptionListener#actionRemoved(org.openquark.samples.bam.model.ActionDescription)
         */
        public void actionRemoved (ActionDescription actionDescription) {
            onActionRemoved (actionDescription);
        }

        void listen (MonitorJobDescription newDescription) {
            if (jobDescription != null) {
                jobDescription.removeJobDescriptionListener (this);
            }

            if (newDescription != null) {
                newDescription.addJobDescriptionListener (this);
            }

            jobDescription = newDescription;
//System.out.println ("JDL now listening to " + jobDescription);            
        }

    }

    private final DocumentListener documentListener = new DocumentListener ();

    private final JobDescriptionListener jobDescriptionListener = new JobDescriptionListener ();

    private final MonitorApp app;

    private Action addMessageSourceAction;

    private Action removeMessageSourceAction;
    
    private Action addTriggerAction;

    private Action editTriggerAction;

    private Action removeTriggerAction;

    private Action addActionAction;

    private Action editActionAction;

    private Action removeActionAction;

    /**
     * Constructor MonitorDocumentEditor
     * 
     * @param app
     */
    MonitorDocumentEditor (final MonitorApp app) {
        this.app = app;

        app.addPropertyChangeListener (new AppPropertyChangeListener ());

        documentListener.listen (app.getDocument ());

        buildContents ();
    }

    /**
     * Method buildContents
     * 
     *  
     */
    private void buildContents () {
        setLayout (new GridBagLayout ());

        GridBagConstraints constraints = new GridBagConstraints ();

        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0.5;
        constraints.weighty = 0.5;
        constraints.insets = new Insets (3, 3, 3, 3);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weighty = 0.0;

        add (getMessageSourceHeader(), constraints);

        constraints.weighty = 0.5;

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridheight = 3;

        add (new JScrollPane (getMessageList ()), constraints);

        constraints.gridheight = 1;

        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weighty = 0.0;

        add (getTriggerHeader(), constraints);

        constraints.weighty = 0.5;

        constraints.gridx = 1;
        constraints.gridy = 1;

        add (new JScrollPane (getTriggerList ()), constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.weighty = 0.0;

        add (getActionHeader(), constraints);

        constraints.weighty = 0.5;

        constraints.gridx = 1;
        constraints.gridy = 3;

        add (new JScrollPane (getActionList ()), constraints);

        setPreferredSize (new Dimension (500, 500));

        refresh ();
    }
    
    private Box getMessageSourceHeader () {
        return makeHeader("Message sources", getAddMessageSourceAction(), getRemoveMessageSourceAction());
    }
    
    private Box getTriggerHeader () {
        return makeHeader("Triggers", getAddTriggerAction(), getRemoveTriggerAction());
    }
    
    private Box getActionHeader () {
        return makeHeader("Actions", getAddActionAction(), getRemoveActionAction());
    }
    
    private Box makeHeader (String label, Action addAction, Action removeAction) {
        Box header = new Box (BoxLayout.X_AXIS);

        header.add (new JLabel (label));
        
        addHeaderButton (header, addAction,    "add.gif");
        addHeaderButton (header, removeAction, "remove.gif");
        
        return header;
    }
    
    private void addHeaderButton (Box box, Action action, String iconFileName) {
        JButton button = new JButton (action);
        
        Icon icon = loadIcon(iconFileName);
        
        if (icon != null) {
            button.setIcon(icon);
            button.setText("");
        }
        
        button.setMargin(new Insets (0, 0, 0, 0));
        
        box.add (Box.createHorizontalStrut(3));
        box.add (button);
    }
    
    private ImageIcon loadIcon (String iconFileName) {
        String iconFilePath = "/Resources/" + iconFileName; //$NON-NLS-1$ 

        URL url = getClass().getResource (iconFilePath);

        if (url != null) {
            return new ImageIcon (url);
        } else {
            System.out.println ("Failed to load icon <" + iconFileName + ">");
            
            return null;
        }
    }

    private JList getMessageList () {
        if (messageList == null) {
            messageList = new WatermarkedList (loadIcon("messagesWatermark.png"));
            
            messageList.addListSelectionListener (new ListSelectionListener () {

                public void valueChanged (ListSelectionEvent e) {
                    onMessageListSelectionChanged ();
                }
            });

            messageList.addMouseListener (new MouseAdapter () {

                /**
                 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
                 */
                @Override
                public void mouseClicked (MouseEvent e) {
                    if (e.getClickCount () > 1 && messageList.locationToIndex (e.getPoint ()) >= 0) {
                        onMessageListDoubleClick ();
                    }
                }
                
                
                /**
                 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
                 */
                @Override
                public void mousePressed (MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        onMessageContextMenu (e);
                    }
                }
                
                
                /**
                 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
                 */
                @Override
                public void mouseReleased (MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        onMessageContextMenu (e);
                    }
                }
            });
        }

        return messageList;
    }

    /**
     * Method onMessageContextMenu
     * 
     * @param e
     */
    protected void onMessageContextMenu (MouseEvent e) {
        JPopupMenu popupMenu = new JPopupMenu ();
        
        popupMenu.add(getAddMessageSourceAction());
        popupMenu.add(getRemoveMessageSourceAction());
        
        popupMenu.show(getMessageList(), e.getX(), e.getY());
    }

    /**
     * Method onMessageListDoubleClick
     * 
     *  
     */
    protected void onMessageListDoubleClick () {
        //        JOptionPane.showMessageDialog(this, "Message list double click: " + getMessageList ().getSelectedValue());

    }

    /**
     * Method onMessageListSelectionChanged
     */
    protected void onMessageListSelectionChanged () {
        MonitorJobDescription jobDescription = getSelectedJobDescription ();

        getTriggerList ().setModel (makeTriggerModel (jobDescription));
        getActionList ().setModel (makeActionModel (jobDescription));

        jobDescriptionListener.listen (jobDescription);

        updateActions ();
    }

    private MonitorJobDescription getSelectedJobDescription () {
        Object selectedValue = getMessageList ().getSelectedValue ();

        if (selectedValue instanceof MessageListItem) {
            return ((MessageListItem)selectedValue).jobDescription;
        } else {
            return null;
        }
    }

    private TriggerDescription getSelectedTriggerDescription () {
        Object selectedValue = getTriggerList ().getSelectedValue ();

        if (selectedValue instanceof TriggerListItem) {
            return ((TriggerListItem)selectedValue).triggerDescription;
        } else {
            return null;
        }
    }

    private ActionDescription getSelectedActionDescription () {
        Object selectedValue = getActionList ().getSelectedValue ();

        if (selectedValue instanceof ActionListItem) {
            return ((ActionListItem)selectedValue).actionDescription;
        } else {
            return null;
        }
    }

    /**
     * Method onJobDescriptionAdded
     * 
     * @param jobDescription
     */
    private void onJobDescriptionAdded (MonitorJobDescription jobDescription) {
        DefaultListModel messageListModel = (DefaultListModel)getMessageList ().getModel ();
        
        messageListModel.addElement (new MessageListItem (jobDescription));
        
        int messageCount = messageListModel.size();
        
        if (messageCount > 0) {
            getMessageList ().setSelectedIndex(messageCount - 1);
        }
    }

    /**
     * Method onJobDescriptionRemoved
     * 
     * @param jobDescription
     */
    private void onJobDescriptionRemoved (MonitorJobDescription jobDescription) {
        DefaultListModel messageListModel = (DefaultListModel)getMessageList ().getModel ();

        for (int elementN = 0; elementN < messageListModel.getSize (); ++elementN) {
            MessageListItem item = (MessageListItem)messageListModel.get (elementN);

            if (item.jobDescription == jobDescription) {
                messageListModel.remove (elementN);

                break;
            }
        }
    }

    /**
     * Method onTriggerAdded
     * 
     * @param triggerDescription
     */
    private void onTriggerAdded (TriggerDescription triggerDescription) {
        DefaultListModel triggerListModel = (DefaultListModel)getTriggerList().getModel();
        
        TriggerListItem item = new TriggerListItem (triggerDescription);
        
        triggerListModel.addElement(item);
        
        int index = triggerListModel.indexOf(item);
        
        triggerList.setSelectedIndex(index);
    }
    
    /**
     * Method onTriggerReplaced
     * 
     * @param oldTriggerDescription
     * @param triggerDescription
     */
    private void onTriggerReplaced (TriggerDescription oldTriggerDescription, TriggerDescription triggerDescription) {
        DefaultListModel triggerListModel = (DefaultListModel)getTriggerList ().getModel ();

        for (int elementN = 0; elementN < triggerListModel.getSize (); ++elementN) {
            TriggerListItem item = (TriggerListItem)triggerListModel.get (elementN);

            if (item.triggerDescription == oldTriggerDescription) {
                triggerListModel.set(elementN, new TriggerListItem (triggerDescription));

                break;
            }
        }
    }

    /**
     * Method onTriggerRemoved
     * 
     * @param triggerDescription
     */
    private void onTriggerRemoved (TriggerDescription triggerDescription) {
        DefaultListModel triggerListModel = (DefaultListModel)getTriggerList ().getModel ();

        for (int elementN = 0; elementN < triggerListModel.getSize (); ++elementN) {
            TriggerListItem item = (TriggerListItem)triggerListModel.get (elementN);

            if (item.triggerDescription == triggerDescription) {
                triggerListModel.remove (elementN);

                break;
            }
        }
    }

    /**
     * Method onActionAdded
     * 
     * @param actionDescription
     */
    private void onActionAdded (ActionDescription actionDescription) {
        DefaultListModel actionListModel = (DefaultListModel)getActionList().getModel();
        
        ActionListItem item = new ActionListItem (actionDescription);
        
        actionListModel.addElement(item);

        int index = actionListModel.indexOf(item);
        
        actionList.setSelectedIndex(index);
    }
    
    /**
     * Method onActionReplaced
     * 
     * @param oldActionDescription
     * @param actionDescription
     */
    private void onActionReplaced (ActionDescription oldActionDescription, ActionDescription actionDescription) {
        DefaultListModel actionListModel = (DefaultListModel)getActionList ().getModel ();

        for (int elementN = 0; elementN < actionListModel.getSize (); ++elementN) {
            ActionListItem item = (ActionListItem)actionListModel.get (elementN);

            if (item.actionDescription == oldActionDescription) {
                actionListModel.set(elementN, new ActionListItem (actionDescription));

                break;
            }
        }
    }
    
    /**
     * Method onActionRemoved
     * 
     * @param actionDescription
     */
    private void onActionRemoved (ActionDescription actionDescription) {
        DefaultListModel actionListModel = (DefaultListModel)getActionList ().getModel ();

        for (int elementN = 0; elementN < actionListModel.getSize (); ++elementN) {
            ActionListItem item = (ActionListItem)actionListModel.get (elementN);

            if (item.actionDescription == actionDescription) {
                actionListModel.remove (elementN);

                break;
            }
        }
    }

    /**
     * Method updateActions
     * 
     *  
     */
    private void updateActions () {
        boolean messageSelected = getMessageList ().getSelectedIndex () >= 0;
        boolean triggerSelected = getTriggerList ().getSelectedIndex () >= 0;
        boolean actionSelected = getActionList ().getSelectedIndex () >= 0;

        getRemoveMessageSourceAction ().setEnabled (messageSelected);
        
        getAddTriggerAction ().setEnabled (messageSelected);
        getEditTriggerAction().setEnabled(triggerSelected);
        getRemoveTriggerAction ().setEnabled (triggerSelected);

        getAddActionAction ().setEnabled (messageSelected);
        getEditActionAction().setEnabled(actionSelected);
        getRemoveActionAction ().setEnabled (actionSelected);
    }

    /**
     * Method makeTriggerModel
     * 
     * @param jobDescription
     * @return Returns a ListModel that contains all the triggers for the job
     */
    private ListModel makeTriggerModel (MonitorJobDescription jobDescription) {
        DefaultListModel result = new DefaultListModel ();

        if (jobDescription != null) {
            List<TriggerDescription> triggerDescriptions = jobDescription.getTriggerDescriptions ();

            for (final TriggerDescription description : triggerDescriptions) {
                result.addElement (new TriggerListItem (description));
            }
        }

        return result;
    }

    /**
     * Method makeActionModel
     * 
     * @param jobDescription
     * @return Returns a ListModel that contains all the actions for the job
     */
    private ListModel makeActionModel (MonitorJobDescription jobDescription) {
        DefaultListModel result = new DefaultListModel ();

        if (jobDescription != null) {
            List<ActionDescription> actionDescriptions = jobDescription.getActionDescriptions ();

            for (final ActionDescription description : actionDescriptions) {
                result.addElement (new ActionListItem (description));
            }
        }

        return result;
    }

    private JList getTriggerList () {
        if (triggerList == null) {
            triggerList = new WatermarkedList (loadIcon("triggersWatermark.png"));

            triggerList.addListSelectionListener (new ListSelectionListener () {

                public void valueChanged (ListSelectionEvent e) {
                    onTriggerListSelectionChanged ();
                }
            });

            triggerList.addMouseListener(new MouseAdapter () {

                @Override
                public void mouseClicked (MouseEvent e) {
                    if (e.getClickCount() > 1) {
                        onTriggerDoubleClick ();
                    }
                }
                
                
                /**
                 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
                 */
                @Override
                public void mousePressed (MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        onTriggerContextMenu (e);
                    }
                }
                
                
                /**
                 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
                 */
                @Override
                public void mouseReleased (MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        onTriggerContextMenu (e);
                    }
                }
            });
        }

        return triggerList;
    }

    /**
     * Method onTriggerDoubleClick
     * 
     * 
     */
    protected void onTriggerDoubleClick () {
        onEditTrigger();
    }

    /**
     * Method onTriggerContextMenu
     * 
     * 
     */
    protected void onTriggerContextMenu (MouseEvent e) {
        JPopupMenu popupMenu = new JPopupMenu ();
        
        popupMenu.add(new JMenuItem (getAddTriggerAction()));
        
        popupMenu.add(new JMenuItem (getEditTriggerAction()));
        popupMenu.add(new JMenuItem (getRemoveTriggerAction()));
        
        popupMenu.show(getTriggerList(), e.getX(), e.getY());
    }

    /**
     * Method onTriggerListSelectionChanged
     */
    protected void onTriggerListSelectionChanged () {
        updateActions ();
    }

    private JList getActionList () {
        if (actionList == null) {
            actionList = new WatermarkedList (loadIcon("actionsWatermark.png"));

            actionList.addListSelectionListener (new ListSelectionListener () {

                public void valueChanged (ListSelectionEvent e) {
                    onActionListSelectionChanged ();
                }
            });
            
            actionList.addMouseListener(new MouseAdapter () {

                @Override
                public void mouseClicked (MouseEvent e) {
                    if (e.getClickCount() > 1) {
                        onActionDoubleClick ();
                    }
                }
                
                
                /**
                 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
                 */
                @Override
                public void mousePressed (MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        onActionContextMenu (e);
                    }
                }
                
                
                /**
                 * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
                 */
                @Override
                public void mouseReleased (MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        onActionContextMenu (e);
                    }
                }
            });
        }

        return actionList;
    }

    /**
     * Method onActionDoubleClick
     * 
     * 
     */
    protected void onActionDoubleClick () {
        onEditAction();
    }

    /**
     * Method onActionContextMenu
     * 
     * @param e
     */
    protected void onActionContextMenu (MouseEvent e) {
        JPopupMenu popupMenu = new JPopupMenu ();
        
        popupMenu.add(new JMenuItem (getAddActionAction()));
        
        popupMenu.add(new JMenuItem (getEditActionAction()));
        popupMenu.add(new JMenuItem (getRemoveActionAction()));
        
        popupMenu.show(getActionList(), e.getX(), e.getY());
    }

    /**
     * Method onActionListSelectionChanged
     */
    protected void onActionListSelectionChanged () {
        updateActions ();
    }

    /**
     * Method onChangedDocument
     * 
     *  
     */
    private void onChangedDocument (MonitorDocument newDocument) {
        documentListener.listen (newDocument);

        refresh ();
    }

    /**
     * Method refresh
     * 
     *  
     */
    private void refresh () {
        DefaultListModel newModel = new DefaultListModel ();

        MonitorDocument document = app.getDocument ();

        for (int jobN = 0; jobN < document.getJobDescriptionCount (); ++jobN) {
            MonitorJobDescription jobDescription = document.getNthJobDescription (jobN);

            newModel.addElement (new MessageListItem (jobDescription));
        }

        getMessageList ().setModel (newModel);

        if (newModel.getSize () != 0) {
            getMessageList ().setSelectedIndex (0);
        }
    }

    /**
     * Method getAddMessageAction
     * 
     * @return Returns an Action that will add a message source
     */
    Action getAddMessageSourceAction () {
        if (addMessageSourceAction == null) {
            addMessageSourceAction = new AbstractAction ("Add Message Source") {

                private static final long serialVersionUID = 2283275822570202073L;

                public void actionPerformed (ActionEvent e) {
                    onAddMessageSource ();
                }
            };
        }

        return addMessageSourceAction;
    }

    /**
     * Method getRemoveMessageAction
     * 
     * @return Returns an Action that will remove the selected message source
     */
    Action getRemoveMessageSourceAction () {
        if (removeMessageSourceAction == null) {
            removeMessageSourceAction = new AbstractAction ("Remove Message Source") {

                private static final long serialVersionUID = 7955265784985428917L;

                public void actionPerformed (ActionEvent e) {
                    onRemoveMessageSource ();
                }
            };

            removeMessageSourceAction.setEnabled (false);
        }

        return removeMessageSourceAction;
    }


    /**
     * Method getAddTriggerAction
     * 
     * @return Returns an Action that will add a trigger
     */
    Action getAddTriggerAction () {
        if (addTriggerAction == null) {
            addTriggerAction = new AbstractAction ("Add Trigger") {

                private static final long serialVersionUID = 9093687229776903285L;

                public void actionPerformed (ActionEvent e) {
                    onAddTrigger ();
                }
            };

            addTriggerAction.setEnabled (false);
        }

        return addTriggerAction;
    }

    /**
     * Method getEditTriggerAction
     * 
     * @return Returns an Action that will edit the selected trigger
     */
    Action getEditTriggerAction () {
        if (editTriggerAction == null) {
            editTriggerAction = new AbstractAction ("Edit Trigger") {

                private static final long serialVersionUID = 4453631346071443075L;

                public void actionPerformed (ActionEvent e) {
                    onEditTrigger ();
                }
            };

            editTriggerAction.setEnabled (false);
        }

        return editTriggerAction;
    }


    /**
     * Method getRemoveTriggerAction
     * 
     * @return Returns an Action that will remove the selected trigger
     */
    Action getRemoveTriggerAction () {
        if (removeTriggerAction == null) {
            removeTriggerAction = new AbstractAction ("Remove Trigger") {

                private static final long serialVersionUID = 2225030261962889366L;

                public void actionPerformed (ActionEvent e) {
                    onRemoveTrigger ();
                }
            };

            removeTriggerAction.setEnabled (false);
        }

        return removeTriggerAction;
    }

    /**
     * Method getAddActionAction
     * 
     * @return Returns an Action that will add an action
     */
    Action getAddActionAction () {
        if (addActionAction == null) {
            addActionAction = new AbstractAction ("Add Action") {

                private static final long serialVersionUID = 1567525912001876340L;

                public void actionPerformed (ActionEvent e) {
                    onAddAction ();
                }
            };

            addActionAction.setEnabled (false);
        }

        return addActionAction;
    }

    /**
     * Method getEditActionAction
     * 
     * @return Returns an Action that will edit the selected action
     */
    Action getEditActionAction () {
        if (editActionAction == null) {
            editActionAction = new AbstractAction ("Edit Action") {

                private static final long serialVersionUID = 7629201334117188442L;

                public void actionPerformed (ActionEvent e) {
                    onEditAction ();
                }
            };

            editActionAction.setEnabled (false);
        }

        return editActionAction;
    }
    
    
    /**
     * Method getRemoveActionAction
     * 
     * @return Returns an Action that will remove the selected action
     */
    Action getRemoveActionAction () {
        if (removeActionAction == null) {
            removeActionAction = new AbstractAction ("Remove Action") {

                private static final long serialVersionUID = 1988710033348412916L;

                public void actionPerformed (ActionEvent e) {
                    onRemoveAction ();
                }
            };

            removeActionAction.setEnabled (false);
        }

        return removeActionAction;
    }

    /**
     * Method onAddMessageSource
     * 
     *  
     */
    protected void onAddMessageSource () {
        AddMessageWizard wizard = new AddMessageWizard (JOptionPane.getFrameForComponent(this), app.getDocument());
        
        wizard.doModal();
    }

    /**
     * Method onRemoveMessageSource
     * 
     *  
     */
    protected void onRemoveMessageSource () {
        MonitorJobDescription jobDescription = getSelectedJobDescription();
        
        if (jobDescription != null) {
            if (confirmAction("Are you sure you want to delete this message source?")) {
                app.getDocument ().removeJobDescription (jobDescription);
            }
        }
    }
    


    /**
     * Method onAddTrigger
     * 
     *  
     */
    protected void onAddTrigger () {
        MonitorJobDescription jobDescription = getSelectedJobDescription();
        
        if (jobDescription != null) {
            AddTriggerWizard wizard = new AddTriggerWizard (JOptionPane.getFrameForComponent(this), app, jobDescription);
            
            wizard.doModal();
        }
    }

    /**
     * Method onEditTrigger
     * 
     * 
     */
    protected void onEditTrigger () {
        MonitorJobDescription jobDescription = getSelectedJobDescription ();

        if (jobDescription != null) {
            TriggerDescription triggerDescription = getSelectedTriggerDescription ();

            if (triggerDescription != null) {
                EditBindingsDialog dialog = new EditBindingsDialog (JOptionPane.getFrameForComponent(this), app, jobDescription, triggerDescription, BindInputsCard.FOR_TRIGGER);
                
                dialog.doModal ();
            }
        }
    }


    /**
     * Method onRemoveTrigger
     * 
     *  
     */
    protected void onRemoveTrigger () {
        MonitorJobDescription jobDescription = getSelectedJobDescription ();

        if (jobDescription != null) {
            if (confirmAction("Are you sure you want to delete this trigger?")) {
                TriggerDescription triggerDescription = getSelectedTriggerDescription ();

                if (triggerDescription != null) {
                    jobDescription.removeTrigger (triggerDescription);
                }
            }
        }
    }

    /**
     * Method onAddAction
     * 
     *  
     */
    protected void onAddAction () {
        MonitorJobDescription jobDescription = getSelectedJobDescription();
        
        if (jobDescription != null) {
            AddActionWizard wizard = new AddActionWizard (JOptionPane.getFrameForComponent(this), app, jobDescription);
            
            wizard.doModal();
        }
    }
    
    /**
     * Method onEditAction
     * 
     * 
     */
    protected void onEditAction () {
        MonitorJobDescription jobDescription = getSelectedJobDescription ();

        if (jobDescription != null) {
            ActionDescription actionDescription = getSelectedActionDescription ();

            if (actionDescription != null) {
                EditBindingsDialog dialog = new EditBindingsDialog (JOptionPane.getFrameForComponent(this), app, jobDescription, actionDescription, BindInputsCard.FOR_ACTION);
                
                dialog.doModal ();
            }
        }
    }
    
    /**
     * Method onRemoveAction
     * 
     *  
     */
    protected void onRemoveAction () {
        MonitorJobDescription jobDescription = getSelectedJobDescription ();

        if (jobDescription != null) {
            if (confirmAction("Are you sure you want to delete this action?")) {
                ActionDescription actionDescription = getSelectedActionDescription ();
    
                if (actionDescription != null) {
                    jobDescription.removeAction (actionDescription);
                }
            }
        }
    }

    private boolean confirmAction (String message) {
        return JOptionPane.showConfirmDialog(this, message, "BAM Sample", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

}
