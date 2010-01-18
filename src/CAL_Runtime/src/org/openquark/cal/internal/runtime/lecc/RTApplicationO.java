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
 * RTApplicationO.java
 * Creation date: Jun 14, 2005
 * By: RCypher
 */
package org.openquark.cal.internal.runtime.lecc;

import org.openquark.cal.runtime.CALExecutorException;

/**
 * @author RCypher
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RTApplicationO extends RTApplication {

    RTApplicationO(RTValue function, RTValue argument) {
        super (function, argument);
    }


    @Override
    public RTValue apply(RTValue arg1) {
        return new RTApplicationO(this, arg1);
    }

    @Override
    public RTValue apply(RTValue arg1, RTValue arg2) {
        return new RTApplicationO(new RTApplicationO(this, arg1), arg2);
    }

    @Override
    public RTValue apply(RTValue arg1, RTValue arg2, RTValue arg3) {
        return new RTApplicationO (new RTApplicationO (new RTApplicationO(this, arg1), arg2), arg3);
    }

    @Override
    public RTValue apply(RTValue arg1, RTValue arg2, RTValue arg3, RTValue arg4) {
        return new RTApplicationO(new RTApplicationO (new RTApplicationO (new RTApplicationO(this, arg1), arg2), arg3), arg4);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RTValue reduce(RTExecutionContext ec) throws CALExecutorException {
         if (function != null) {
             // Deal with oversaturation:  first walk down to the right
             // argument for the arity of the underlying supercombinator
             function.evaluate(ec);

             int argCount = getArgCount();
             RTValue rv = this;
             for (RTValue lhs = rv.lhs(); lhs != null; lhs = rv.lhs()) {
                 rv = lhs;
                 argCount += rv.getArgCount();
             }

             if (argCount < rv.getArity()) {
                 return this;
             }

             setResult (rv.f (this, ec));
         } else if (result == null) {
             throw new NullPointerException ("Invalid reduction state in application.  This is probably caused by a circular function definition.");
         }

         return (result);
    }

}
