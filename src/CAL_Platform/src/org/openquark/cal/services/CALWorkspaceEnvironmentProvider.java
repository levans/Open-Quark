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
 * CALWorkspaceEnvironmentProvider.java
 * Creation date: Dec 9, 2005.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import org.openquark.cal.machine.ProgramResourceRepository;
import org.openquark.cal.runtime.MachineType;


/**
 * Warning- this class should only be used by the CAL services implementation. It is not part of the
 * external API of the CAL platform.
 * <p>
 * A provider for a cal workspace environment.
 * Instances of CALWorkspace are obtained by the workspace manager by:
 * 1) instantiating a workspace provider factory
 * 2) using the factory to create a provider
 * 3) instantiating the workspace using the provider
 * 
 * @author Edward Lam
 */
public interface CALWorkspaceEnvironmentProvider {
    
    /**
     * Warning- this class should only be used by the CAL services implementation. It is not part of the
     * external API of the CAL platform.
     * <p>
     * Factory for a CALWorkspaceEnvironmentProvider.
     * Concrete implementations of this class should have a no argument constructor if specified as the provider factory by the system property.
     * 
     * @author Edward Lam
     */
    public interface Factory {

        /**
         * @param workspaceID the discrete workspace identifier, or null for a nullary-type workspace.
         * @return a workspace provider for a workspace with the given workspace id.
         */
        public CALWorkspaceEnvironmentProvider createCALWorkspaceProvider(String workspaceID);
    }
    
    /**
     * Warning- this class should only be used by the CAL services implementation. It is not part of the
     * external API of the CAL platform.
     * <p>
     * An exception which is thrown when there is a problem creating the workspace provider.
     * @author Edward Lam
     */
    public static final class FactoryException extends Exception {
       
        private static final long serialVersionUID = -3335184452023636595L;

        FactoryException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    /**
     * @return a CALWorkspace with the provider's workspace identifier.
     */
    public CALWorkspace getCALWorkspace();
    
    /**
     * @return the default program resource repository provider to associate with the workspace.
     * This may not necessarily be used to instantiate the program resource repository,
     * in the case where the repository is otherwise overridden.
     * Null if the default provider could not be instantiated.
     */
    public ProgramResourceRepository.Provider getDefaultProgramResourceRepositoryProvider();
    
    /**
     * @return the default program resource manager to associate with the workspace.
     * This may not necessarily be used in the case where the manager is otherwise overridden.
     * Null if the default resource manager could not be instantiated.
     */
    public ResourceManager getDefaultProgramResourceManager(MachineType machineType);
    
    /**
     * @return the nullary environment associated with the current execution environment.
     */
    public NullaryEnvironment getNullaryEnvironment();
    
}
