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
 * GemBrowserActionKeys.java
 * Creation date: Apr 15, 2003.
 * By: Edward Lam
 */
package org.openquark.gems.client.browser;

import java.awt.event.KeyEvent;

/**
 * Container class for browser action-related public constants.
 * @author Edward Lam
 */
final class GemBrowserActionKeys {

    /** Constant to use when accessing/setting the selected state for an action. */
    public static final String ACTION_SELECTED_KEY = "SelectedState";

    /*
     * Some mnemonic keys for the browser menu items.
     */
    public static final int MNEMONIC_SORT_ALPHABETICALLY             = KeyEvent.VK_A;
    public static final int MNEMONIC_SORT_BY_FORM                    = KeyEvent.VK_G;
    public static final int MNEMONIC_CATEGORIZE_BY_MODULE_FLAT_ABBREV = KeyEvent.VK_M;
    public static final int MNEMONIC_CATEGORIZE_BY_MODULE_FLAT       = KeyEvent.VK_F;
    public static final int MNEMONIC_CATEGORIZE_BY_MODULE_HIERARCHCICAL = KeyEvent.VK_H;
    public static final int MNEMONIC_CATEGORIZE_BY_ARITY             = KeyEvent.VK_R;
    public static final int MNEMONIC_CATEGORIZE_BY_TYPE              = KeyEvent.VK_T;
    public static final int MNEMONIC_CATEGORIZE_BY_OUTPUT            = KeyEvent.VK_O;
    public static final int MNEMONIC_CATEGORIZE_BY_INPUT             = KeyEvent.VK_I;
    public static final int MNEMONIC_EDIT_METADATA                   = KeyEvent.VK_E;
    public static final int MNEMONIC_VIEW_METADATA                   = KeyEvent.VK_V;

    /**
     * Not intended to be instantiated.
     */
    private GemBrowserActionKeys() {
    }
    
}
