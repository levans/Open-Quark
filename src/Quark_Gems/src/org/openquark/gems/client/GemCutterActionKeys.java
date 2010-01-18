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
 * GemCutterActionKeys.java
 * Creation date: (04/12/2002 4:02:00 PM)
 * By: Edward Lam
 */
package org.openquark.gems.client;

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

/**
 * Container class which defines the keys used for mnemonics and accelerators in the GemCutter.
 * @author Edward Lam
 */
final class GemCutterActionKeys {
    
    
    /**
     * Constructor for a GemCutterActionKeys.
     * Not intended to be instantiated.
     */
    private GemCutterActionKeys() {
    }

    // Menu bar mnemonics
    public static final int MNEMONIC_FILE_MENU           = KeyEvent.VK_F;
    public static final int MNEMONIC_EDIT_MENU           = KeyEvent.VK_E;
    public static final int MNEMONIC_DEBUG_MENU          = KeyEvent.VK_D;
    public static final int MNEMONIC_VIEW_MENU           = KeyEvent.VK_V;
    public static final int MNEMONIC_GENERATE_MENU       = KeyEvent.VK_G;
    public static final int MNEMONIC_INSERT_MENU         = KeyEvent.VK_I;
    public static final int MNEMONIC_WORKSPACE_MENU      = KeyEvent.VK_W;
    public static final int MNEMONIC_RUN_MENU            = KeyEvent.VK_R;
    public static final int MNEMONIC_HELP_MENU           = KeyEvent.VK_H;

    // Menu mnemonics
    public static final int MNEMONIC_NEW                     = KeyEvent.VK_N;
    public static final int MNEMONIC_OPEN                    = KeyEvent.VK_O;
    public static final int MNEMONIC_SAVE_GEM                = KeyEvent.VK_S;
    public static final int MNEMONIC_DUMP_METADATA           = KeyEvent.VK_E;
    public static final int MNEMONIC_EXIT                    = KeyEvent.VK_X;
    public static final int MNEMONIC_UNDO                    = KeyEvent.VK_U;
    public static final int MNEMONIC_REDO                    = KeyEvent.VK_R;
    public static final int MNEMONIC_CUT                     = KeyEvent.VK_T;
    public static final int MNEMONIC_COPY                    = KeyEvent.VK_C;
    public static final int MNEMONIC_PASTE                   = KeyEvent.VK_P;
    public static final int MNEMONIC_DELETE                  = KeyEvent.VK_D;
    public static final int MNEMONIC_SELECT_ALL              = KeyEvent.VK_A;
    public static final int MNEMONIC_SEARCH                  = KeyEvent.VK_F;
    public static final int MNEMONIC_VIEW_TOOLBAR            = KeyEvent.VK_T;
    public static final int MNEMONIC_VIEW_STATUSBAR          = KeyEvent.VK_S;
    public static final int MNEMONIC_VIEW_OVERVIEW           = KeyEvent.VK_O;
    public static final int MNEMONIC_TARGET_DOCKING          = KeyEvent.VK_G;
    public static final int MNEMONIC_GEM_PROPERTIES_VIEWER   = KeyEvent.VK_P;
    public static final int MNEMONIC_ARRANGE_GRAPH           = KeyEvent.VK_A;
    public static final int MNEMONIC_FIT_TABLETOP            = KeyEvent.VK_F;
    public static final int MNEMONIC_DEBUG_OUTPUT            = KeyEvent.VK_D;
    public static final int MNEMONIC_ADD_VALUE_GEM           = KeyEvent.VK_V;
    public static final int MNEMONIC_ADD_CODE_GEM            = KeyEvent.VK_D;
    public static final int MNEMONIC_ADD_COLLECTOR_GEM       = KeyEvent.VK_L;
    public static final int MNEMONIC_ADD_RECORD_FIELD_SELECTION_GEM       = KeyEvent.VK_X;
    public static final int MNEMONIC_ADD_RECORD_CREATION_GEM              = KeyEvent.VK_Y;
    public static final int MNEMONIC_ADD_REFLECTOR_GEM       = KeyEvent.VK_R;
    public static final int MNEMONIC_INTELLICUT              = KeyEvent.VK_I;
    public static final int MNEMONIC_ADD_MODULE              = KeyEvent.VK_A;
    public static final int MNEMONIC_ADD_STD_VAULT_MODULE    = KeyEvent.VK_S;
    public static final int MNEMONIC_ADD_LOCAL_FILE_MODULE   = KeyEvent.VK_L;
    public static final int MNEMONIC_ADD_ENTERPRISE_MODULE   = KeyEvent.VK_E;    // TODOEL: use setDisplayedMnemonicIndex(), as "e" appears multiple times.
    public static final int MNEMONIC_REMOVE_MODULE           = KeyEvent.VK_R;
    public static final int MNEMONIC_EXPORT_MODULE           = KeyEvent.VK_E;
    public static final int MNEMONIC_EXPORT_MODULE_TO_CE     = KeyEvent.VK_E;
    public static final int MNEMONIC_EXPORT_MODULE_TO_JAR    = KeyEvent.VK_J;
    public static final int MNEMONIC_SYNC                    = KeyEvent.VK_S;
    public static final int MNEMONIC_SWITCH_WORKSPACE        = KeyEvent.VK_W;
    public static final int MNEMONIC_DEPLOY_WORKSPACE        = KeyEvent.VK_D;
    public static final int MNEMONIC_EXPORT_WORKSPACE_TO_CARS= KeyEvent.VK_X;
    public static final int MNEMONIC_REVERT_WORKSPACE        = KeyEvent.VK_V;
    public static final int MNEMONIC_RECOMPILE               = KeyEvent.VK_C;
    public static final int MNEMONIC_COMPILE_MODIFIED        = KeyEvent.VK_M;
    public static final int MNEMONIC_CREATE_MINIMAL_WORKSPACE = KeyEvent.VK_W;
    public static final int MNEMONIC_WORKSPACE_INFO          = KeyEvent.VK_I;
    public static final int MNEMONIC_RUN                     = KeyEvent.VK_U;
    public static final int MNEMONIC_RESUME                  = KeyEvent.VK_E;
    public static final int MNEMONIC_STOP                    = KeyEvent.VK_S;
    public static final int MNEMONIC_RESET                   = KeyEvent.VK_A;
    public static final int MNEMONIC_HELP_TOPICS             = KeyEvent.VK_T;
    public static final int MNEMONIC_ABOUT_BOX               = KeyEvent.VK_A;
    public static final int MNEMONIC_PREFERENCES             = KeyEvent.VK_R;
    public static final int MNEMONIC_BURN                    = KeyEvent.VK_B;
    public static final int MNEMONIC_UNBURN                  = KeyEvent.VK_U;
    public static final int MNEMONIC_DISCONNECT              = KeyEvent.VK_D;
    public static final int MNEMONIC_SPLITCONNECTION         = KeyEvent.VK_S;
    public static final int MNEMONIC_RETARGET_INPUT          = KeyEvent.VK_R;
    public static final int MNEMONIC_RETARGET_COLLECTOR      = KeyEvent.VK_R;
    public static final int MNEMONIC_COPY_AS_IMAGE           = KeyEvent.VK_F11;
    public static final int MNEMONIC_COPY_AS_TEXT            = KeyEvent.VK_F12;
    public static final int MNEMONIC_TESTMETADATA            = KeyEvent.VK_T;
    public static final int MNEMONIC_DUMPGRAPH               = KeyEvent.VK_G;
    public static final int MNEMONIC_DUMPDEFINITION          = KeyEvent.VK_D;
    public static final int MNEMONIC_LOADSAVEMETADATA        = KeyEvent.VK_L;
    public static final int MNEMONIC_CHECKGRAPH              = KeyEvent.VK_C;
    public static final int MNEMONIC_FINDORPHANS             = KeyEvent.VK_O;
    public static final int MNEMONIC_DUMPFREQUENCIES         = KeyEvent.VK_F;
    public static final int MNEMONIC_DUMPCOMPOSITIONALFREQUENCIES = KeyEvent.VK_M;
    public static final int MNEMONIC_DUMPLINTWARNINGS        = KeyEvent.VK_W;
    public static final int MNEMONIC_DUMPINCONSISTENTARGUMENTMETADATA = KeyEvent.VK_A;
    
    // Prompt save dialog and Save dialog mnemonics
    public static final int MNEMONIC_DIALOG_OPTION_SAVE                    = KeyEvent.VK_S;
    public static final int MNEMONIC_DIALOG_OPTION_DONTSAVE                = KeyEvent.VK_D;
    public static final int MNEMONIC_DIALOG_OPTION_CANCEL                  = KeyEvent.VK_C;
    
    
    // Popup menu mnemonics
    public static final int MNEMONIC_CHANGE_MODULE           = KeyEvent.VK_C;

    // Accelerator keys
    public static final KeyStroke ACCELERATOR_NEW                    = KeyStroke.getKeyStroke("control N");
    public static final KeyStroke ACCELERATOR_OPEN                   = KeyStroke.getKeyStroke("control O");
    public static final KeyStroke ACCELERATOR_SAVE_GEM               = KeyStroke.getKeyStroke("control S");
    public static final KeyStroke ACCELERATOR_UNDO                   = KeyStroke.getKeyStroke("control Z");
    public static final KeyStroke ACCELERATOR_FIT_TABLETOP           = KeyStroke.getKeyStroke("control H");
    public static final KeyStroke ACCELERATOR_PREFERENCES            = KeyStroke.getKeyStroke("control R");
    public static final KeyStroke ACCELERATOR_REDO                   = KeyStroke.getKeyStroke("control Y");
    public static final KeyStroke ACCELERATOR_CUT                    = KeyStroke.getKeyStroke("control X");
    public static final KeyStroke ACCELERATOR_COPY                   = KeyStroke.getKeyStroke("control C");
    public static final KeyStroke ACCELERATOR_PASTE                  = KeyStroke.getKeyStroke("control V");
    public static final KeyStroke ACCELERATOR_DELETE                 = KeyStroke.getKeyStroke("DELETE");
    public static final KeyStroke ACCELERATOR_SELECT_ALL             = KeyStroke.getKeyStroke("control A");
    public static final KeyStroke ACCELERATOR_SEARCH                 = KeyStroke.getKeyStroke("control F");
    public static final KeyStroke ACCELERATOR_ARRANGE_GRAPH          = KeyStroke.getKeyStroke("control G");
    public static final KeyStroke ACCELERATOR_RUN                    = KeyStroke.getKeyStroke("control U");
    public static final KeyStroke ACCELERATOR_RESUME                 = KeyStroke.getKeyStroke("control E");
    public static final KeyStroke ACCELERATOR_STOP                   = KeyStroke.getKeyStroke("control T");
    public static final KeyStroke ACCELERATOR_RESET                  = KeyStroke.getKeyStroke("control M");
    public static final KeyStroke ACCELERATOR_INTELLICUT             = KeyStroke.getKeyStroke("control I");
    public static final KeyStroke ACCELERATOR_COPY_AS_IMAGE          = KeyStroke.getKeyStroke("F11");
    public static final KeyStroke ACCELERATOR_COPY_AS_TEXT           = KeyStroke.getKeyStroke("F12");

}
