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
 * GemCodeSyntaxListener.java
 * Creation date: Oct 02, 2002 
 * By: Ken Wong
 */

package org.openquark.gems.client;

import java.awt.Color;
import java.util.Arrays;

import org.openquark.cal.compiler.CodeAnalyser;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.SourceIdentifier;
import org.openquark.cal.services.GemEntity;
import org.openquark.cal.services.Perspective;
import org.openquark.gems.client.caleditor.CALSyntaxStyleListener;



/**
 * This is a helper class that handles some of the syntax highlighting for CAL Code Editor.
 * In particular, it highlights CAL keywords, code arguments, local variables, and built-in and user defined
 * type classes, type constructors, and functional agents
 * 
 * Any CALEditor objects can add this GemCodeSyntaxListener to allow for such highlighting, given that
 * a valid TypeChecker is passed in. It is important to update the typechecker in this highlighter
 * if the compiler was changed.
 * 
 * Creation date: Oct 02, 2002
 * 
 * @author Ken Wong
 */
public class GemCodeSyntaxListener implements CALSyntaxStyleListener {
    
    /** Perspective used to look up tokens to figure out how they should be coloured.  */
    private final Perspective perspective;
    
    /** Names of known local variables definied in the code, sorted alphabetically */
    private String[] localVariableNames = new String[0];
    
    /** Names of known arguments to the code, sorted alphabetically */
    private String[] argumentNames = new String[0];
    
    /*
     * GUI members                     ****************************************************************
     */
       
    private static final java.awt.Color USER_CLASS_COLOUR = new java.awt.Color(0,140,190);
    private static final java.awt.Color USER_CONS_COLOUR = new java.awt.Color(210,140,20);
    private static final java.awt.Color USER_TYPE_COLOUR = new java.awt.Color(0,150,90);
    private static final java.awt.Color USER_SC_COLOUR = new java.awt.Color(255,0,170);
    private static final java.awt.Color ARGUMENT_COLOUR = new java.awt.Color(58,154,154);
    private static final java.awt.Color LOCAL_VARIABLE_COLOUR = new java.awt.Color(110,110,150);

    /**
     * Default Contructor for GemCodeSyntaxListener;
     * @param perspective
     */
    public GemCodeSyntaxListener(Perspective perspective) {
        this.perspective = perspective;
    }

    /**
     * Lookup the font for a given scanCode.
     * Creation date: (1/30/01 9:08:01 AM)
     * @return java.awt.Font the font to apply
     * @param scanCode int the scan code (token type)
     * @param image String the token image
     * @param suggestedFont java.awt.Font the suggested font (default or user set) or null
     */
    public java.awt.Font fontLookup(int scanCode, java.lang.String image, java.awt.Font suggestedFont) {
        // Just accept whatever we were given
        return suggestedFont;
    }

    /**
     * Lookup the foreground colour for a given scanCode.
     * Creation date: (1/30/01 9:08:01 AM)
     * @return Style the font to apply
     * @param scanCode the scan code (token type)
     * @param image the token image
     * @param suggestedColour the suggested colour (default or user set) or null
     */
    public Color foreColourLookup(int scanCode, String image, Color suggestedColour) {
    
        //todoBI foreColourLookup needs updating. The problem is that qualified names are not a lexical construct
        //and so something like M.x will result in 3 calls to this method: for "M" for "." and for "x".
        //if (true)
        //    return suggestedColour;
            
        if (scanCode != org.openquark.cal.compiler.CALTokenTypes.VAR_ID &&
            scanCode != org.openquark.cal.compiler.CALTokenTypes.CONS_ID) {
    
            // simply accept the colour we're given            
            return suggestedColour;
        }
    
        // Modify the colour of identifiers depending on what they actually are
        GemEntity entity = null;
        
        int periodPos = image.indexOf('.');
        if (periodPos == -1) {
            // This is an unqualified name.
            
            if (Arrays.binarySearch(argumentNames, image) > -1) {
                // This is an argument
                return ARGUMENT_COLOUR;
            }
            
            // See if this is a known type or class
            
            entity = perspective.resolveAmbiguousGemEntity(image);
            if (entity == null) {
                // Unqualified name is not a visible supercombinator or class method
                // Check if it is a type constructor, type class or local variable
             
                if (CodeAnalyser.getModulesContainingIdentifier(image, SourceIdentifier.Category.TYPE_CONSTRUCTOR, perspective.getWorkingModuleTypeInfo()).size() > 0) {
                    // This is a type constructor, found in a module
                    return USER_TYPE_COLOUR;
                } else if (CodeAnalyser.getModulesContainingIdentifier(image, SourceIdentifier.Category.TYPE_CLASS, perspective.getWorkingModuleTypeInfo()).size() > 0) {
                    // This is a class, found in a module
                    return USER_CLASS_COLOUR;
                } else if (Arrays.binarySearch(localVariableNames, image) > -1) {
                    // This is a local variable
                    return LOCAL_VARIABLE_COLOUR;
                }
            }

        } else if (QualifiedName.isValidCompoundName(image)) {
            QualifiedName rawImageName = QualifiedName.makeFromCompoundName(image);
            ModuleName resolvedModuleName = perspective.getWorkingModuleTypeInfo().getModuleNameResolver().resolve(rawImageName.getModuleName()).getResolvedModuleName();
            QualifiedName imageName = QualifiedName.make(resolvedModuleName, rawImageName.getUnqualifiedName());
            
            if (imageName.getModuleName().equals(perspective.getWorkingModuleName()) && 
                (Arrays.binarySearch(argumentNames, image) > -1)) {
                // This is an argument
                return ARGUMENT_COLOUR;
            }
            
            entity = perspective.getVisibleGemEntity(imageName);
            if (entity == null) {
                // Qualified name is not a visible supercombinator or class method
                // Check if it is a type constructor, type class, or local variable
                
                if (CodeAnalyser.getVisibleModuleEntity(imageName, SourceIdentifier.Category.TYPE_CONSTRUCTOR, perspective.getWorkingModuleTypeInfo()) != null) {
                    return USER_TYPE_COLOUR;
                    
                } else if (CodeAnalyser.getVisibleModuleEntity(imageName, SourceIdentifier.Category.TYPE_CLASS, perspective.getWorkingModuleTypeInfo()) != null) {
                    return USER_CLASS_COLOUR;
                    
                } else if (imageName.getModuleName().equals(perspective.getWorkingModuleName()) &&
                           (Arrays.binarySearch(localVariableNames, image) > -1)) {
                    return LOCAL_VARIABLE_COLOUR;
                } 
            }
        }
    
        if (entity == null) {
            return suggestedColour;
        }
        
        // We have this in our environment; we should know what it is
    
        boolean cons = entity.isDataConstructor();
        //check if constructor
        if (cons) {
            // It's a constructor
            //System.out.println ("Coloured user defined constructor.");            
            return USER_CONS_COLOUR;
        } else {
            // It's a supercombinator
            //System.out.println ("Coloured user defined SC.");            
            return USER_SC_COLOUR;
        }        
    
    }

    /**
     * Lookup the style for a given scanCode.
     * Creation date: (1/30/01 9:08:01 AM)
     * @return javax.swing.text.Style the style to apply
     * @param scanCode the scan code (token type)
     * @param image the token image 
     * @param suggestedStyle the suggested style (default or user set) or null
     */
    public javax.swing.text.Style styleLookup(int scanCode, String image, javax.swing.text.Style suggestedStyle) {
        // Just accept whatever we were given
        return suggestedStyle;
    }
    
    /** 
     * Set the names of known code arguments
     * @param argumentNames names of code arguments, sorted alphabetically
     */
    public void setArgumentNames(String[] argumentNames) {
        this.argumentNames = argumentNames.clone();
    }
    
    /**
     * Set the names of known local variables
     * @param localVariableNames names of local variables, sorted alphabetically
     */
    public void setLocalVariableNames(String[] localVariableNames) {
        this.localVariableNames = localVariableNames.clone();
    }
}