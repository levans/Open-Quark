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
 * StandaloneRuntime.java
 * Created: Oct 10, 2007
 * By: Joseph Wong
 */

package org.openquark.cal.runtime.lecc;

import java.math.BigInteger;

import org.openquark.cal.internal.runtime.StandaloneRuntimeEnvironment;
import org.openquark.cal.internal.runtime.lecc.RTData;
import org.openquark.cal.internal.runtime.lecc.RTExecutionContext;
import org.openquark.cal.internal.runtime.lecc.StandaloneJarResourceAccess;
import org.openquark.cal.runtime.CalValue;
import org.openquark.cal.runtime.ExecutionContext;
import org.openquark.cal.runtime.ExecutionContextProperties;

/**
 * This non-instantiable class contains utility methods for supporting the use of standalone JARs.
 *
 * @author Joseph Wong
 */
public final class StandaloneRuntime {

    /** Private constructor. */
    private StandaloneRuntime() {}

    /**
     * Factory method for constructing an instance of this class with the default properties and a suitable class
     * loader for loading foreign classes and accessing resources in standalone JARs.
     *
     * @param callerClass the class object whose class loader is to be used for loading foreign classes and accessing resources when running in standalone JAR mode. Must be non-null.
     * @return an execution context for use with standalone JARs.
     */
    public static ExecutionContext makeExecutionContext(final Class<?> callerClass) {
        return makeExecutionContext(callerClass.getClassLoader());
    }
    
    /**
     * Factory method for constructing an instance of this class with the default properties and the specified class
     * loader for loading foreign classes and accessing resources in standalone JARs.
     *
     * @param classloader the class loader for loading foreign classes and accessing resources when running in standalone JAR mode. Must be non-null.
     * @return an execution context for use with standalone JARs.
     */
    public static ExecutionContext makeExecutionContext(final ClassLoader classloader) {
        return makeExecutionContext(new ExecutionContextProperties.Builder().toProperties(), classloader);
    }

    /**
     * Factory method for constructing an instance of this class with the specified properties and a suitable class
     * loader for loading foreign classes and accessing resources in standalone JARs.
     *
     * @param properties the properties to be associated with the execution context. 
     * @param callerClass the class object whose class loader is to be used for loading foreign classes and accessing resources when running in standalone JAR mode. Must be non-null.
     * @return an execution context for use with standalone JARs.
     */
    public static ExecutionContext makeExecutionContext(final ExecutionContextProperties properties, final Class<?> callerClass) {
        return makeExecutionContext(properties, callerClass.getClassLoader());
    }
    
    /**
     * Factory method for constructing an instance of this class with the specified properties and the specified class
     * loader for loading foreign classes and accessing resources in standalone JARs.
     *
     * @param properties the properties to be associated with the execution context. 
     * @param classloader the class loader for loading foreign classes and accessing resources when running in standalone JAR mode. Must be non-null.
     * @return an execution context for use with standalone JARs.
     */
    public static ExecutionContext makeExecutionContext(final ExecutionContextProperties properties, final ClassLoader classloader) {
        return new RTExecutionContext(properties, new StandaloneRuntimeEnvironment(classloader, new StandaloneJarResourceAccess(classloader)));
    }
    
    /**
     * Constructs a representation of the given value as a CalValue.
     * @param value the value to wrap.
     * @return the corresponding CalValue.
     */
    public static CalValue toCalValue(final char value) {
        return RTData.CAL_Char.make(value);
    }
    
    /**
     * Constructs a representation of the given value as a CalValue.
     * @param value the value to wrap.
     * @return the corresponding CalValue.
     */
    public static CalValue toCalValue(final boolean value) {
        return RTData.CAL_Boolean.make(value);
    }
    
    /**
     * Constructs a representation of the given value as a CalValue.
     * @param value the value to wrap.
     * @return the corresponding CalValue.
     */
    public static CalValue toCalValue(final byte value) {
        return RTData.CAL_Byte.make(value);
    }
    
    /**
     * Constructs a representation of the given value as a CalValue.
     * @param value the value to wrap.
     * @return the corresponding CalValue.
     */
    public static CalValue toCalValue(final short value) {
        return RTData.CAL_Short.make(value);
    }
    
    /**
     * Constructs a representation of the given value as a CalValue.
     * @param value the value to wrap.
     * @return the corresponding CalValue.
     */
    public static CalValue toCalValue(final int value) {
        return RTData.CAL_Int.make(value);
    }
    
    /**
     * Constructs a representation of the given value as a CalValue.
     * @param value the value to wrap.
     * @return the corresponding CalValue.
     */
    public static CalValue toCalValue(final float value) {
        return RTData.CAL_Float.make(value);
    }
    
    /**
     * Constructs a representation of the given value as a CalValue.
     * @param value the value to wrap.
     * @return the corresponding CalValue.
     */
    public static CalValue toCalValue(final long value) {
        return RTData.CAL_Long.make(value);
    }
    
    /**
     * Constructs a representation of the given value as a CalValue.
     * @param value the value to wrap.
     * @return the corresponding CalValue.
     */
    public static CalValue toCalValue(final double value) {
        return RTData.CAL_Double.make(value);
    }
    
    /**
     * Constructs a representation of the given value as a CalValue.
     * @param value the value to wrap.
     * @return the corresponding CalValue.
     */
    public static CalValue toCalValue(final String value) {
        return RTData.CAL_String.make(value);
    }
    
    /**
     * Constructs a representation of the given value as a CalValue.
     * @param value the value to wrap.
     * @return the corresponding CalValue.
     */
    public static CalValue toCalValue(final BigInteger value) {
        return RTData.CAL_Integer.make(value);
    }
    
    /**
     * Constructs a representation of the given value as a CalValue.
     * @param value the value to wrap.
     * @return the corresponding CalValue.
     */
    public static CalValue toCalValue(final Object value) {
        return RTData.CAL_Opaque.make(value);
    }
}
