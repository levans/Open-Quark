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
 * UnsafeCast.java
 * Created: Aug 1, 2007
 * By: Edward Lam
 */

package org.openquark.util;

import java.util.Collections;
import java.util.List;



/**
 * Helper class to perform unsafe object casts.
 * <p>
 * Note that the finest granularity for which a {@code @SuppressWarnings} annotation can be applied is at the method level.
 * Therefore, in cases where a single unsafe case is desired in the context of a series of type-safe operations, 
 * it is necessary to abstract the operation of the unsafe cast into its own method with the annotation.
 * <p>
 * This class provides such methods, conveniently centralizing unsafe casts and minimizing use of the {@code @SuppressWarnings} annotation.
 * <p>
 * *** NOTE *** <br>
 *   It is almost always preferable to avoid using this class if possible. In many cases this is possible by generous use of {@code <?>}
 * 
 * @author Edward Lam
 */
public final class UnsafeCast {
    
    /*
     * Implementation note: 
     *  We make the @SuppressWarnings annotation per-method rather than for the whole class because
     *  we want to allow for methods for which we want warnings reported.
     */
    
    /**
     * Private constructor - not intended to be instantiated.
     */
    private UnsafeCast() {
    }
    
    /**
     * Cast an object to a different type.
     * <p>
     * *** WARNING *** <br>
     *  Circumstances in which this method need be used for avoiding warnings should be rare.
     *  Avoid if possible!
     * 
     * @param <T> The type to which obj should be cast.
     * @param obj the object to cast.  May be null
     * @return (T)obj the input object cast to the desired type.
     */
    @SuppressWarnings("unchecked")
    public static <T> T unsafeCast(Object obj) {
        return (T)obj;
    }
    
    /**
     * Cast an object to be the same type as another object.
     * <p>
     * e.g. to cast a {@code List} to a {@code List<TargetType>}: <br>
     * {@code 
     *   UnsafeCast.asTypeOf(listToCast, Collections.<TargetType>emptyList()); 
     * }
     * 
     * @param <T> The type to which objectToCast should be cast.
     * @param objectToCast the object to cast.  May be null.
     * @param objectWithTargetType 
     * @return (T)objectToCast the input object cast to the desired type.
     */
    @SuppressWarnings("unchecked")
    public static <T> T asTypeOf(final Object objectToCast, final T objectWithTargetType) {
        return (T)objectToCast;
    }
    
    /**
     * Cast a List.
     * <p>
     * e.g. to cast a {@code List} to a {@code List<TargetType>}: <br>
     * {@code 
     *   UnsafeCast.<TargetType>castList(listToCast)
     * }
     * @param <T> The type to which listToCast should be cast.
     * @param listToCast the list to cast.  May be null.
     * @return (T)listToCast the input list cast to the desired type.
     */
    public static <T> List<T> castList(final List<?> listToCast) {
        return asTypeOf(listToCast, Collections.<T>emptyList());
    }

}
