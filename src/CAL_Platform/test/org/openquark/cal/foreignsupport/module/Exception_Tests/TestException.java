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
 * TestException.java
 * Created: Jul 12, 2007
 * By: Joseph Wong
 */

package org.openquark.cal.foreignsupport.module.Exception_Tests;

/**
 * An exception class in the test hierarchy for testing exception handling when running in
 * standalone JAR mode, and the calPlatform.jar is loaded by a different class loader from the one
 * loading calPlatform_test.jar - useful for testing standalone JARs with the quarklaunch script.
 * 
 * @author Joseph Wong
 */
public final class TestException extends Exception {

    private static final long serialVersionUID = 9016004803535897240L;

    /**
     * Constructs an instance of this exception class.
     */
    public TestException() {
    }

    /**
     * Constructs an instance of this exception class.
     * @param message the detail message.
     */
    public TestException(String message) {
        super(message);
    }

    /**
     * Constructs an instance of this exception class.
     * @param cause the cause.
     */
    public TestException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs an instance of this exception class.
     * @param message the detail message.
     * @param cause the cause.
     */
    public TestException(String message, Throwable cause) {
        super(message, cause);
    }

}
