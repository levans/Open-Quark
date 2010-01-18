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
 * IOResult.java
 * Creation date: Jun 30, 2005.
 * By: Joseph Wong
 */

package org.openquark.cal.foreignsupport.module.File;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * The IOResult class encapsulates what a Java foreign support method in this
 * package returns to CAL, i.e. either a return value, or an uncaught
 * IOException. An instance of IOResult gets translated by helper functions in
 * the File module into a more native CAL representation, namely that of the type
 * (Either IOError a).
 * 
 * @author Joseph Wong
 */
public final class IOResult {
    
    /**
     * The return value of the operation, or null if the operation terminated with an exception.
     */
    private final Object result;
    /**
     * The exception which terminated the operation, or null if the operation returned its result successfully.
     */
    private final IOException exception;
    /**
     * The file associated with the error, or null if not applicable.
     */
    private final File file;
    
    /**
     * Private constructor for IOResult.
     * 
     * @param res
     *            the return value of the operation, or null if the operation
     *            terminated with an exception.
     * @param ex
     *            the exception which terminated the operation, or null if the
     *            operation returned its result successfully.
     * @param f
     *            the file associated with the error, or null if not applicable.
     */
    private IOResult(Object res, IOException ex, File f) {
        result = res;
        exception = ex;
        file = f;
    }

    /**
     * Construct an IOResult representing the successful execution of an
     * operation whose return type is void (or equivalently the CAL Unit type).
     * 
     * @return the new IOResult instance.
     */
    public static IOResult makeVoidResult() {
        return new IOResult(org.openquark.cal.foreignsupport.module.Prelude.UnitValue.UNIT, null, null);
    }
    
    /**
     * Construct an IOResult representing the return value of a successful
     * operation.
     * 
     * @param result
     *            the return value of the operation.
     * @return the new IOResult instance.
     */
    public static IOResult makeResult(Object result) {
        return new IOResult(result, null, null);
    }
    
    /**
     * Construct an IOResult representing an IOException which terminated the
     * operation.
     * 
     * @param exception
     *            the IOException which terminated the operation.
     * @param fileName
     *            the name of the file involved in the error, or null if not
     *            applicable.
     * @return the new IOResult instance.
     */
    public static IOResult makeError(IOException exception, File fileName) {
        return new IOResult(null, exception, fileName);
    }
    
    /**
     * Returns whether this IOError instance represents an IOException which
     * terminated the operation.
     * 
     * @return true iff this instance represents an IOException which terminated
     *         the operation.
     */
    public boolean isError() {
        return exception != null;
    }
    
    /**
     * Returns the return value represented by this instance, or null if the
     * operation was terminated with an exception.
     * 
     * @return the return value of the operation, or null if it failed with an
     *         exception.
     */
    public Object getResult() {
        return result;
    }
    
    /**
     * Returns the exception which terminated the operation, or null if the
     * operation executed successfully.
     * 
     * @return the exception which terminated the operation, or null if the
     *         operation succeeded.
     */
    public IOException getException() {
        return exception;
    }
    
    /**
     * Returns the File object associated with the exception that terminated the
     * operation, or null if the operation executed successfully or if the
     * exception is not associated with a particular File object.
     * 
     * @return the associated File object, or null if not applicable.
     */
    public File getFile() {
        return file;
    }    
    
    /**
     * Returns the stack trace associated with the given exception object.
     * 
     * @param ex
     *            the exception whose stack trace is desired.
     * @return the stack trace as a string.
     */
    public static String getStackTrace(IOException ex) {
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        ex.printStackTrace(writer);
        return sw.toString();
    }
    
    /**
     * Returns true iff the given exception object represents an error that
     * arose because the user did not having sufficient privileges to perform
     * the operation.
     * 
     * @param ex
     *            the exception to check
     */
    public static boolean isPermissionDenied_FileNotFoundException(IOException ex) {
        return ex instanceof FileNotFoundException &&
            (ex.getMessage().endsWith("(Access is denied)")
            || ex.getMessage().endsWith("(Permission denied)")
            || ex.getMessage().endsWith("(Read-only file system)"));
    }
    
    /**
     * Returns true iff the given exception object represents an error that
     * arose because a hardware device was not ready.
     * 
     * @param ex
     *            the exception to check
     */
    public static boolean isDeviceNotReady_FileNotFoundException(IOException ex) {
        return ex instanceof FileNotFoundException && ex.getMessage().endsWith("(The device is not ready)");
    }      
    
    /**
     * Returns true iff the given exception object represents an error that
     * arose because the specified file or directory was already in use.
     * 
     * @param ex
     *            the exception to check
     */
    public static boolean isResourceBusyException(IOException ex) {
        return ex.getMessage().endsWith("The process cannot access the file because another process has locked a portion of the file");
    }
    
    /**
     * Returns true iff the given exception object represents an error that
     * arose because the hardware device (e.g. the disk) was full.
     * 
     * @param ex
     *            the exception to check
     */
    public static boolean isResourceExhaustedException(IOException ex) {
        return ex.getMessage().endsWith("There is not enough space on the disk")
            || ex.getMessage().endsWith("No space left on device");
    }      
}
