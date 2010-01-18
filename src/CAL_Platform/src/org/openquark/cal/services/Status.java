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
 * Status.java
 * Creation date: Sep 23, 2002.
 * By: Edward Lam
 */
package org.openquark.cal.services;

import org.openquark.cal.compiler.CompilerMessage;
import org.openquark.cal.compiler.MessageKind;


/**
 * A class representing the status of an action.
 * @author Edward Lam
 */
public class Status {
    
    /*
     * Implementation note:
     *   There is a bit of a problem here, as this class doubles as a logger.
     *   This class should probably be split up into at least two pieces:
     *     - a logger
     *     - a message to be logged.
     *   The ability to log a tree-structured message could potentially be preserved by logging other loggers as messages.
     *   There should also be simple (leaf) messages which are guaranteed not to have children / be mutable.
     */
    
    /** The severity level of this status. */
    private Severity severity = Severity.OK;

    /** The message associated with this status. */
    private final String message;

    /** The associated throwable, or null if none. */
    private Throwable throwable;

    /** The child status objects. */
    private Status[] children = new Status[0];

    /**
     * Severity enum pattern.
     * @author Edward Lam
     */
    public static final class Severity implements Comparable<Severity> {

        /** The default status level. */
        public static final Severity OK = new Severity("Ok", 0);
        
        /** Purely informational, and not a problem of any sort. */
        public static final Severity INFO = new Severity("Info", 1);
        
        /**
         * A problem that does not prevent the program from continuing in the regular way,
         * but nevertheless may be of interest to the user as it indicates a potential problem.
         */
        public static final Severity WARNING = new Severity("Warning", 2);
        
        /** A problem which prevents the program from continuing in the regular way. */
        public static final Severity ERROR = new Severity("Error", 3);
        
        private final int level;
        private final String typeString;

        /**
         * Constructor for a Severity object.
         */
        private Severity(String s, int level) {
            typeString = s;
            this.level = level;
        }

        /**
         * Get the severity.  This number should only be used for comparative purposes.
         * @return int the comparative severity level
         */
        public int getLevel(){
            return level;
        }

        /**
         * @return the internal string describing the severity level.
         */
        public String getTypeString() {
            return typeString;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return typeString + "(" + level + ")";
        }
        
        /**
         * {@inheritDoc}
         */
        public int compareTo(Severity otherSeverity) {
            int otherLevel = otherSeverity.getLevel();
            
            if (level < otherLevel) {
                return -1;
            } else if (level == otherLevel) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    /**
     * Constructor for a Status.
     * @param message The message associated with this status object.
     */
    public Status(String message) {
        Assert.isNotNullArgument(message, "message");
        this.message = message;
    }

    /**
     * Constructor for a Status.
     *
     * @param message The message associated with this status object.
     * @param throwable the associated throwable, or null if none.
     */
    public Status(String message, Throwable throwable) {
        this(message);
        this.throwable = throwable;
    }

    /**
     * Constructor for a Status.
     *
     * @param severity the status severity.
     * @param message The message associated with this status object.
     */
    public Status(Severity severity, String message) {
        this(severity, message, null);
    }

    /**
     * Constructor for a Status.
     *
     * @param severity the status severity.
     * @param message The message associated with this status object.
     * @param throwable the associated throwable, or null if none.
     */
    public Status(Severity severity, String message, Throwable throwable) {
        this(message, throwable);
        this.severity = severity;
    }

    /**
     * @return the associated Throwable object, or null if none.
     */
    public Throwable getThrowable() {
        return throwable;
    }

    /**
     * @return the associated message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns a string representation of the status, suitable for debugging purposes only.
     * @return a string representation of the status and its children, suitable for debugging purposes.
     */
    public String getDebugMessage() {

        StringBuilder sb = new StringBuilder();

        // If the status has children, it's just the component statuses
        if (children.length > 0) {
            for (int i = 0; i < children.length; i++) {
                if (i != 0) {
                    sb.append("\n");
                }
                sb.append(children[i].getDebugMessage());
            }

        } else {

            sb.append(this.severity.getTypeString().toUpperCase() + ": ");
            
            sb.append(message);
            
            if (throwable != null) {
                sb.append(' ');
                sb.append(throwable);
            }
        }

        return sb.toString();
    }

    /**
     * @return the severity
     */
    public Severity getSeverity() {
        return severity;
    }

    /**
     * Return whether the severity of this status is OK.
     * @return whether the associated severity is Severity.OK.
     */
    public boolean isOK() {
        return severity == Severity.OK;
    }

    /**
     * Add a given status as a child of this status.
     *
     * @param status the new child status
     */
    public void add(Status status) {
        Assert.isNotNullArgument(status, "status");
        
        Status[] newChildren = new Status[children.length + 1];
        System.arraycopy(children, 0, newChildren, 0, children.length);
        newChildren[newChildren.length - 1] = status;
        
        this.children = newChildren;
        
        Severity newSev = status.getSeverity();
        if (newSev.getLevel() > severity.getLevel()) {
            this.severity = newSev;
        }
    }

    /**
     * Returns a list of status object immediately contained in this
     * status, or an empty list if this is not a multi-status.
     *
     * @return an array of status objects
     */
    public Status[] getChildren() {
        return children;
    }

    /**
     * Returns a string representation of the status, suitable for debugging purposes only.
     * @return the string representation.
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("Status ");
        
        sb.append(severity.getTypeString());

        sb.append(' ');
        sb.append(message);
        
        if (throwable != null) {
            sb.append(' ');
            sb.append(throwable);
        }

        sb.append(" children=[");
        for (int i = 0; i < children.length; i++) {
            if (i != 0) {
                sb.append(" ");
            }
            
            sb.append(children[i].toString());
        }
        sb.append("]");

        return sb.toString();
    }
    
    /**
     * @return a CompilerMessage which corresponds to this status object.
     */
    public CompilerMessage asCompilerMessage() {
        if (severity == Severity.OK || severity == Severity.INFO) {
            return new CompilerMessage(new MessageKind.Info.DebugMessage(getDebugMessage()));

        } else if (severity == Severity.WARNING) {
            return new CompilerMessage(new MessageKind.Warning.DebugMessage(getDebugMessage()));
        
        } else if (severity == Severity.ERROR) {
            return new CompilerMessage(new MessageKind.Error.DebugMessage(getDebugMessage()));
        
        } else {
            throw new IllegalStateException("Unknown Severity type:" + severity);
        }
    }

}
