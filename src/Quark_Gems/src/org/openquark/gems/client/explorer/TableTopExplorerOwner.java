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
 * TableTop.java
 * Creation date: Dec 2nd 2002
 * By: Ken Wong
 */
package org.openquark.gems.client.explorer;

import java.util.Set;

import javax.swing.Action;
import javax.swing.JPopupMenu;

import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.services.GemEntity;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.gems.client.AutoburnLogic;
import org.openquark.gems.client.Connection;
import org.openquark.gems.client.FunctionalAgentGem;
import org.openquark.gems.client.Gem;
import org.openquark.gems.client.ValueGem;
import org.openquark.gems.client.valueentry.MetadataRunner;
import org.openquark.gems.client.valueentry.ValueEditorContext;
import org.openquark.gems.client.valueentry.ValueEditorManager;

/**
 * This class is used as an intermediatary between the Explorer representation of Gem Graphs and the owner frame of
 * the explorer. As the explorer is only a UI shell, all of the dirty work must be done by the owner.
 * @author Ken Wong
 * Creation Date: Dec 2nd 2002
 */
public interface TableTopExplorerOwner {
    /** 
     * Return the supported popup actions
     * @return JPopupMenu
     */
    JPopupMenu /*<Action>*/ getPopupMenu();

    /** 
     * Return the supported popup actions 
     * @param gems
     * @return JPopupMenu
     */
    JPopupMenu /*<Action>*/ getPopupMenu(Gem[] gems);

    /** 
     * Return the supported popup actions 
     * @param inputs
     * @return JPopupMenu
     */
    JPopupMenu /*<Action>*/ getPopupMenu(Gem.PartInput[] inputs);

    /** 
     * Selects the defined gem (null for deselect all) 
     * @param gem
     * @param isSingleton (should we deselect the other gems
     */
    void selectGem(Gem gem, boolean isSingleton);

    /** 
     * adds the specified gem entities into the gem graph and returns the associated gems 
     * @param gemEntities
     * @return Gem[] the corresponding gems that have just been added
     */
    Gem[] addGems(GemEntity[] gemEntities);

    /** 
     * checks whether the action of connection can be completed 
     * @param source
     * @param destination
     * @return boolean
     */
    AutoburnLogic.AutoburnUnifyStatus canConnect(Gem.PartOutput source, Gem.PartInput destination);

    /** 
     * checks whether the action of connection can be completed 
     * @param source
     * @param destination
     * @return boolean
     */
    AutoburnLogic.AutoburnUnifyStatus canConnect(GemEntity source, Gem.PartInput destination);

    /** 
     * Completes the action of connection. Note: The gems being connected are
     * not necessarily on the tabletop already. they should be added before.
     * @param source
     * @param destination
     */
    void connect(Gem.PartOutput source, Gem.PartInput destination);

    /** 
     * Completes the action of disconnection 
     * @param connection
     */
    void disconnect(Connection connection);

    /** 
     * Deletes this gem 
     * @param gem
     * @return Action
     */
    Action getDeleteGemAction(Gem gem);

    /** 
     * Returns all the roots of the gem trees 
     * @return Set
     */
    Set<Gem> getRoots();

    /**
     * Starts a new compound edit operation that should be undoable.  This call will be matched by a endUndoableEdit()
     * call.
     */
    void beginUndoableEdit();

    /**
     * Ends the current compound edit operation.
     */
    void endUndoableEdit();

    /**
     * Sets the name of the current compound edit operation.  It should be called between beginUndoableEdit() and
     * endUndoableEdit().  Note that this method does not need to be called and may in fact have no effect.
     * @param editName String - The name to be used for the compound edit
     */
    void setUndoableName(String editName);

    /**
     * Returns a string that describes the functional agent gem specified
     * @param gem
     * @return String
     */
    String getHTMLFormattedFunctionalAgentGemDescription(FunctionalAgentGem gem);

    /**
     * Returns a string that describes the Part.Input specified
     * @param input
     * @return String
     */
    String getHTMLFormattedMetadata(Gem.PartInput input);

    /**
     * Returns whether the drag and drop actions are enabled
     * @return boolean
     */
    boolean isDNDEnabled();

    /**
     * Returns the ValueEditorManager for ValueEditors.
     * @return ValueEditorManager
     */
    ValueEditorManager getValueEditorManager();

    /**
     * Returns the valueNode associated with this valueGem
     * @param valueGem
     * @return ValueNode
     */
    ValueNode getValueNode(ValueGem valueGem);

    /**
     * Returns the valueNode associated with this Gem.PartInput
     * @param inputPart
     * @return ValueNode
     */
    ValueNode getValueNode(Gem.PartInput inputPart);

    /**
     * Returns a ValueEditorContext that can be associated with a value editor for the value of this value gem.
     * @param valueGem the value gem for which to return a context
     * @return ValueEditorContext an appropriate value editor context
     */
    ValueEditorContext getValueEditorContext(ValueGem valueGem);

    /**
     * Returns a ValueEditorContext that can be associated with a value editor for the value of this input.
     * @param partInput the input for which to return a context
     * @return ValueEditorContext an appropriate value editor context
     */
    ValueEditorContext getValueEditorContext(Gem.PartInput partInput);

    /**
     * Returns the valueNode associated with this valueGem
     * @param valueGem
     * @param valueNode
     */
    void changeValueNode(ValueGem valueGem, ValueNode valueNode);

    /**
     * Returns the valueNode associated with this Gem.PartInput (null for none)
     * @param partInput
     * @param valueNode
     */
    void changeValueNode(Gem.PartInput partInput, ValueNode valueNode);

    /**
     * Specify whether this explorer Scope can edit inputs as holders of value nodes.
     * @return boolean
     */
    boolean canEditInputsAsValues();

    /**
     * Specifies whether to highlight the node corresponding to the specified input.
     * @param input 
     * @return boolean
     */
    boolean highlightInput(Gem.PartInput input);

    /**
     * @return the type info for the current working module.
     */
    ModuleTypeInfo getCurrentModuleTypeInfo();

    /**
     * Returns a metadata runner for the specified gem.  The metadata runner may be different for every
     * gem so this method needs to be called everytime metadata needs to be run for a new gem. 
     * @param gem
     * @return A helper object that can calculate metadata for the specified gem.  This can be null if
     * no metadata can be calculated for the specified gem.
     */
    MetadataRunner getMetadataRunner(Gem gem);

    /**
     * Returns an editor customized for editing gem names for the current client
     * @param gem
     * @return A gem name editor customized for editing the specified gem
     */
    ExplorerGemNameEditor getGemNameEditor(Gem gem);

    /**
     * @return Whether or not the background is an image
     */
    boolean hasPhotoLook();
    
    /**
     * @param typeExpr
     * @return the string representation of the type, using the appropriate naming policy and polymorphic var context.
     */
    String getTypeString(TypeExpr typeExpr);
}
