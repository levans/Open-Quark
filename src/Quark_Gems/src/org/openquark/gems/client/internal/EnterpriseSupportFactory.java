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
 * EnterpriseSupportFactory.java
 * Creation date: Oct 5, 2006.
 * By: Joseph Wong
 */
package org.openquark.gems.client.internal;

import java.awt.Frame;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.services.CALWorkspace;
import org.openquark.cal.services.Status;
import org.openquark.cal.services.Vault;


/**
 * Factory class for obtaining an {@link EnterpriseSupport} implementation.
 *
 * @author Joseph Wong
 */
public class EnterpriseSupportFactory {
    
    /**
     * A stub implementation of {@link EnterpriseSupport} that implements each
     * method with a no-op.
     *
     * @author Joseph Wong
     */
    private static final class StubImpl implements EnterpriseSupport {
        
        /** {@inheritDoc} */
        public boolean isEnterpriseSupported() {
            return false;
        }

        /** {@inheritDoc} */
        public void registerEnterpriseVaultProvider(CALWorkspace workspace) {}

        /** {@inheritDoc} */
        public void registerEnterpriseVaultAuthenticator(CALWorkspace workspace, Frame ownerFrame) {}
        
        /** {@inheritDoc} */
        public Vault getEnterpriseVault() {
            return null;
        }

        /** {@inheritDoc} */
        public void ensureConnected(Frame ownerFrame) {}
        
        /** {@inheritDoc} */
        public void ensureLoggedOff() {}

        /** {@inheritDoc} */
        public boolean isConnected() {
            return false;
        }

        /** {@inheritDoc} */
        public int getLatestIdenticalRevisionFromMaybeEnterpriseVault(Vault maybeEnterpriseVault, ModuleName moduleName, CALWorkspace workspace, Status status) {
            return 0;
        }

    }

    /**
     * @return an implementation of the {@link EnterpriseSupport} interface, which may be a stub implementation
     * if connection to Enterprise is not supported. The returned value should never be null.
     */
    public static EnterpriseSupport getInstance() {
        return getInstance(Logger.getAnonymousLogger());
    }

    /**
     * @param logger the logger for logging messages.
     * @return an implementation of the {@link EnterpriseSupport} interface, which may be a stub implementation
     * if connection to Enterprise is not supported. The returned value should never be null.
     */
    public static EnterpriseSupport getInstance(Logger logger) {
        try {
            Class<?> ceSupportClass = Class.forName("com.businessobjects.gems.client.calcebridge.CESupport");
            Constructor<?> ctor = ceSupportClass.getConstructor(new Class[] {Logger.class});
            EnterpriseSupport result = (EnterpriseSupport)ctor.newInstance(new Object[] {logger});
            
            return result;
            
        } catch (SecurityException e) {
        } catch (ClassNotFoundException e) {
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
        
        return new StubImpl();
    }
}
