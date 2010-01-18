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
 * StatusListener.java
 * Created: Feb 19, 2003 at 12:15:01 PM
 * By: Raymond Cypher
 */
package org.openquark.cal.machine;

import org.openquark.cal.compiler.ModuleName;

/**
 * An interface for objects that want to listen to the status of program generation.
 * Status listeners are presented with module or entity status updates as defined by interface Status members.
 * @author Raymond Cypher
 */
public interface StatusListener {
    
    /*
     * Module status.
     */
    
    /** Status: creating a new module.  */
    public static final Status.Module SM_NEWMODULE = new Status.Module("SM_NewModule");

    /** Status: starting code generation for a module.  */
    public static final Status.Module SM_GENCODE = new Status.Module("SM_GenCode");

    /** Status: ending code generation for a module.  */
    public static final Status.Module SM_GENCODE_DONE = new Status.Module("SM_GenCode_Done");

    /** Status: a module has been loaded. */
    public static final Status.Module SM_LOADED = new Status.Module("SM_Loaded");
    
    /** Status: start compiling generated Java sources. */
    public static final Status.Module SM_START_COMPILING_GENERATED_SOURCE = new Status.Module("SM_Start_Compiling_Generated_Source");
    
    /** Status: finished compiling generated Java sources. */
    public static final Status.Module SM_END_COMPILING_GENERATED_SOURCE = new Status.Module("SM_End_Compiling_Generated_Source");
    
    
    /*
     * Entity status.
     */
    
    /** Status: an entity has been compiled.  */
    public static final Status.Entity SM_COMPILED = new Status.Entity("SM_Compiled");          // Expression generation.

    /** Status: an entity has had its code generated. */
    public static final Status.Entity SM_ENTITY_GENERATED = new Status.Entity("SM_Entity_Generated");
    
    /** Status: an entity has had its code generated and a new class/source file has been written. */
    public static final Status.Entity SM_ENTITY_GENERATED_FILE_WRITTEN = new Status.Entity("SM_Entity_Generated_File_Written");
    
    /**
     * Enum pattern for program generation status.
     * @author Edward Lam
     */
    public static abstract class Status {
        /** The name of the module or entity for which the status applies. */
        protected final String name;

        /**
         * Private constructor for a Status.
         * Used as a key in calls to setStatusFromResource();
         * @param name
         */
        private Status(String name) {
            this.name = name;
        }
        
        /**
         * A status update for a module.
         * @author Edward Lam
         */
        public static class Module extends Status {
            private Module(String moduleName) {
                super(moduleName);
            }
            /**
             * {@inheritDoc}
             */
            @Override
            public String toString() {
                return "StatusListener.Module - Name: " + name;
            }
        }
        
        /**
         * A status update for an entity in a module.
         * @author Edward Lam
         */
        public static class Entity extends Status {
            private Entity(String entityName) {
                super(entityName);
            }
            /**
             * {@inheritDoc}
             */
            @Override
            public String toString() {
                return "StatusListener.Entity - Name: " + name;
            }
        }
    }
    
    /**
     * Abstract adapter class for receiving Status updates.
     * This class exists as a convenience for creating listener objects.
     * @author Edward Lam
     */
    public abstract class StatusListenerAdapter implements StatusListener {
        /**
         * {@inheritDoc}
         */
        public void setModuleStatus(Status.Module moduleStatus, ModuleName moduleName) {
        }
        /**
         * {@inheritDoc}
         */
        public void setEntityStatus(Status.Entity entityStatus, String entityName) {
        }
        /**
         * {@inheritDoc}
         */
        public void incrementCompleted (double d) {
        }
    }
    /**
     * Communicate a module's compilation status.
     * @param moduleStatus the module's updated status.
     * @param moduleName the name of the module.
     */
    public void setModuleStatus(Status.Module moduleStatus, ModuleName moduleName);
    
    /**
     * Communicate an entity's compilation status.
     * @param entityStatus the entity's updated status.
     * @param entityName the unqualified name of the entity.
     */
    public void setEntityStatus(Status.Entity entityStatus, String entityName);
    
    /**
     * An increment of a process has been completed.  
     * The increment is expressed as a percentage of the whole.
     * @param d
     */
    public void incrementCompleted (double d);

}
