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
 * IntellicutPanel.java
 * Creation date: (16/04/01 2:03:41 PM)
 * By: Michael Cheng
 */
package org.openquark.gems.client;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.openquark.cal.services.GemEntity;
import org.openquark.gems.client.Gem.PartConnectable;
import org.openquark.gems.client.IntellicutListModelAdapter.IntellicutListEntry;
import org.openquark.gems.client.IntellicutListModelAdapter.PartConnectableAdapter;
import org.openquark.gems.client.IntellicutManager.IntellicutMode;
import org.openquark.gems.client.utilities.ExtendedUndoableEditSupport;
import org.openquark.util.Messages;


/**
 * The GemCutter adapter for the IntellicutListPanel. It handles creating the panel, connecting the
 * selected gem and placing it on the table top.
 * @author Michael Cheng
 */
public class IntellicutPanelAdapter implements IntellicutPanelOwner {

    /** The IntellicutPanel this adapter is for. */    
    private final IntellicutPanel intellicutPanel;
    
    /**
     * The part input intellicut is being performed on.
     * This is null if intellicut is taking place for the table top.
     */
    private final PartConnectable intellicutPart;
    
    /** The GemCutter that is using this adapter. */
    private final GemCutter gemCutter;
    
    /** The table top location at which the new gem should appear. */
    private final Point location;
    
    /** The component that invoked Intellicut and should receive focus when intellicut completes. */
    private final JComponent invoker;

    /** The popup menu that appears when right-clicking on the IntelilcutList */    
    private IntellicutPopupMenu intellicutPopupMenu = null;

    /**
     * This class implements the popup menu for the IntellicutPanel that is displayed when the user right-clicks
     * on an item in the IntellicutList. The menu allows you to connect the gem or view the gem properties.
     * @author Ken Wong
     */
    private class IntellicutPopupMenu extends JPopupMenu implements ActionListener {
        
        private static final long serialVersionUID = 1136769591841746425L;

        /** Menu Item that represents the connect Gem option */
        private final JMenuItem connectGem;

        /**  Menu Item that represents the view properties option */        
        private final JMenuItem viewProperties;
        
        /** 
         * Default constructor for IntellicutPopupMenu
         */
        IntellicutPopupMenu() {
            
            connectGem = new JMenuItem(GemCutter.getResourceString("IPM_Connect_Gem"));
            connectGem.addActionListener(this);   
            viewProperties = new JMenuItem(GemCutter.getResourceString("IPM_View_Properties"));
            viewProperties.addActionListener(this);
            
            add(connectGem);
            add(viewProperties);
        }
        
        /**
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e) {
            
            if (e.getSource() == connectGem) {
                connectSelectedGem();
            
            } else if (e.getSource() == viewProperties) { 
                
                IntellicutListEntry item = (IntellicutListEntry) intellicutPanel.getIntellicutList().getSelectedValue();
                
                if (item.getData() instanceof GemEntity) {
                    GemEntity gemEntity = (GemEntity) item.getData();
                    gemCutter.getNavigatorOwner().displayMetadata(gemEntity, true);
                }                
            }
        }
        
        /**
         * Enable and disable submenus depending on what intellicut item is selected.
         */
        public void update() {
            
            Object selectedItem = intellicutPanel.getIntellicutList().getSelected().getData();
            
            viewProperties.setEnabled(selectedItem instanceof GemEntity);
            
            if (intellicutPart == null) {
                connectGem.setText(GemCutter.getResourceString("IPM_Add_Gem"));
            } else {
                connectGem.setText(GemCutter.getResourceString("IPM_Connect_Gem"));
            }
        }
    }
    

    /**
     * IntellicutPanelAdapter constructor.
     * @param intellicutPart the part that intellicut was invoked for (this can be null)
     * @param gemCutter the GemCutter this adapter is used by
     * @param location the table top location where the new gem should appear
     */
    public IntellicutPanelAdapter(Gem.PartConnectable intellicutPart, GemCutter gemCutter, Point location, JComponent invoker) {
        
        this.invoker = invoker;
        this.gemCutter = gemCutter;
        this.location = location;
        this.intellicutPart = intellicutPart;
        
        PartConnectableAdapter adapter = new PartConnectableAdapter(intellicutPart,
                                                                    gemCutter.getPerspective(),
                                                                    gemCutter.getTypeCheckInfo());
        
        Color typeColor = intellicutPart != null ? gemCutter.getTypeColourManager().getTypeColour(intellicutPart.getType()) : null;
        
        IntellicutMode mode = gemCutter.getIntellicutManager().getIntellicutMode();
        this.intellicutPanel = new IntellicutPanel(this, adapter, typeColor, mode);
    }
    
    /**
     * @return the IntellicutPanel managed by this adapter.
     */
    public IntellicutPanel getIntellicutPanel() {
        return intellicutPanel;
    }
    
    /**
     * Drops the currently selected item in the IntellicutList onto onto the table top and connects
     * it to the intellicut part, if there is an intellicut part. If there is no selected item it will
     * stop the intellicut panel.
     */
    public void connectSelectedGem() {
    
        if (intellicutPanel.getIntellicutListModel().getSize() == 0 ||
            intellicutPanel.getIntellicutList().getSelectedValue() == null) {
                
            stopIntellicutPanel();
            return;
        }

        // Increment the update level for the edit undo.  This will aggregate any gem addition, connection, and burnings..
        ExtendedUndoableEditSupport tableTopUndoableEditSupport = gemCutter.getTableTop().getUndoableEditSupport();
        tableTopUndoableEditSupport.beginUpdate();
        
        // Get a new displayed gem.
        Object selectedItem = ((IntellicutListEntry) intellicutPanel.getIntellicutList().getSelectedValue()).getData();
        DisplayedGem newDisplayedGem = getDisplayedGem(selectedItem, location);

        // Connect the gem.
        gemCutter.getIntellicutManager().attemptIntellicutAutoConnect(newDisplayedGem);
        gemCutter.getIntellicutManager().stopIntellicut();
        
        tableTopUndoableEditSupport.endUpdate();

        // Move the new gem into view and repaint the table top.
        gemCutter.getTableTopPanel().scrollRectToVisible(newDisplayedGem.getBounds());  
        gemCutter.getTableTopPanel().repaint();

        invoker.requestFocusInWindow();
    }

    /**
     * Get a displayed gem for the selected item in the intellicut list.
     * This will either create a new displayed gem or move an existing one to the correct location.
     * @return DisplayedGem
     */
    private DisplayedGem getDisplayedGem(Object selectedItem, Point location) {
        
        // Check if we have to reposition the new gem.
        boolean needToReposition = false;
        if (location == null) {
            location = new Point(1, 1);
            needToReposition = true;
        }

        // Check what kind of entry is selected and if we have to create a new gem or
        // if we can reuse an existing one that is already on the table top.
        DisplayedGem newGem = null;
        boolean needToAddGem = true;
                
        TableTop tableTop = gemCutter.getTableTop();
                        
        if (selectedItem instanceof GemEntity) {
            newGem = tableTop.createDisplayedFunctionalAgentGem(location, (GemEntity)selectedItem);
        
        } else if (selectedItem instanceof CollectorGem) {
            
            if (intellicutPart == null || intellicutPart instanceof Gem.PartInput) {
                newGem = tableTop.createDisplayedReflectorGem(location, (CollectorGem)selectedItem);
            } else {
                newGem = gemCutter.getTableTop().getDisplayedGem((Gem) selectedItem);
                needToAddGem = false;
            }
            
        } else {
            throw new IllegalArgumentException("item type not supported");
        }

        // Add the gem to the table top if needed.
        if (needToAddGem) {

            if (needToReposition) {
                location = gemCutter.getTableTop().findAvailableDisplayedGemLocation(newGem);
            }
            
            gemCutter.getTableTop().getUndoableEditSupport().setEditName(GemCutterMessages.getString("UndoText_Add", newGem.getDisplayText()));
            gemCutter.getTableTop().doAddGemUserAction(newGem, location);
        }
        
        gemCutter.getTableTop().selectDisplayedGem(newGem, true);
        gemCutter.getTableTop().setFocusedDisplayedGem(newGem);
                
        return newGem;
    }

    /**
     * @return the intellicut popup menu (creates a new one if none exists).
     */
    public JPopupMenu getIntellicutPopupMenu() {
        if (intellicutPopupMenu == null) {
            intellicutPopupMenu = new IntellicutPopupMenu();
        }
        
        intellicutPopupMenu.update();
        
        return intellicutPopupMenu;
    }


    /**
     * @see org.openquark.gems.client.IntellicutPanelOwner#stopIntellicutPanel()
     */
    public void stopIntellicutPanel() {
        gemCutter.getIntellicutManager().stopIntellicut();
        invoker.requestFocusInWindow();
    }
    
    /**
     * @see org.openquark.gems.client.IntellicutPanelOwner#getMessages()
     */
    public Messages getMessages() {
        return GemCutterMessages.instance;
    }
}
