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

package org.openquark.cal.internal.foreignsupport.concurrent;

import org.openquark.cal.foreignsupport.module.Prelude.UnitValue;
import org.openquark.cal.runtime.CalFunction;
import org.openquark.cal.runtime.CalValue;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Helper methods for some concurrency experiments.
 * 
 * @author Bo Ilic
 */
public final class ConcurrentHelpers {
    
    private ConcurrentHelpers() {}

    /**
     * Starts evaluation of the specified CAL function and returns a Future
     * value which can be used to retrieve the result (once available).
     * @param executor  the executor to be used for processing the CAL function
     * @param f         the CAL function to be evaluated
     * @return          a Future value from which the result can be retrieved (once available)
     * @throws InterruptedException
     * @throws ExecutionException
     */
    static public Future<CalValue> makeFuture(Executor executor, final CalFunction f)
            throws InterruptedException, ExecutionException {
        
        final Callable<CalValue> callable = new Callable<CalValue>() {
                       
            public CalValue call() {
                return (CalValue)f.evaluate(UnitValue.UNIT);
            }            
         
        };
        
        final FutureTask<CalValue> future = new FutureTask<CalValue>(callable);

        executor.execute(future);
        return future;
    }

    /**
     * Construct a thread-per-task executor, which will spawn a new thread for each execution.
     */
    public static Executor makeThreadPerTaskExecutor() {
        return new Executor() {
            public void execute(Runnable command) {
                Thread thread = new Thread(command);
                thread.start();
            }
        };
    }

    /**
     * Construct a synchronous executor, which will run tasks directly on the calling thread when submitted.
     */
    public static Executor makeSynchronousExecutor() {
        return new Executor() {
            public void execute(Runnable command) {
                command.run();
            }
        };
    }
}
