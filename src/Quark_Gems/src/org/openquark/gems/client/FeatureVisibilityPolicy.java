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
 * FeatureVisibilityPolicy.java
 * Created: Nov 20, 2003
 * By: Kevin Sit
 */
package org.openquark.gems.client;

import org.openquark.cal.compiler.ScopedEntity;
import org.openquark.cal.services.MetaModule;


/**
 * This class is responsible for determining the "visibility" of a CAL feature.  If a feature
 * is determined to be invisible, it means that the feature is not visible to the user, but
 * it is still accessible by the CAL machine.  Another important thing to notice is, a
 * feature predicate is not the only way to control a feature's visibility.  For example,
 * a module can be invisible to the user if it is not imported by the current working module.
 * <p>
 * By default, all predicate methods in this class return <code>true</code>.  Subclasses
 * should override appropriate method(s) to filter unwanted features. 
 * 
 * @author ksit
 */
public class FeatureVisibilityPolicy {
    
    /**
     * The shared instance of the feature visibility predicate.  By default, all
     * predicate methods return <code>true</code>.
     */
    private static final FeatureVisibilityPolicy SINGLETON = new FeatureVisibilityPolicy();
    
    /**
     * Returns <code>true</code> if the given module is visible to the user.
     * By default, this method always returns <code>true</code>.  Subclasses are encouraged
     * to override this method to specify custom filtering options. 
     * @param module
     * @return boolean
     */
    public boolean isModuleVisible(MetaModule module) {
        return true; 
    }
    
    /**
     * Returns <code>true</code> if the given entity is visible to the user.
     * By default, this method always returns <code>true</code>.  Subclasses are encouraged
     * to override this method to specify custom filtering options.
     * @param entity
     * @return boolean
     */
    public boolean isEntityVisible(ScopedEntity entity) {
        return true;
    }

    /**
     * Returns the shared instance of the feature visibility predicate.  By default,
     * all predicate methods return <code>true</code>.
     * @return FeatureVisibilityPolicy
     */
    public static FeatureVisibilityPolicy getDefault() {
        return SINGLETON; 
    }

}