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

package org.openquark.cal.internal.machine;

import org.openquark.cal.compiler.ForeignTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.UnableToResolveForeignEntityException;
import org.openquark.cal.internal.runtime.RuntimeEnvironment;
import org.openquark.cal.machine.MachineFunction;
import org.openquark.cal.machine.Program;
import org.openquark.cal.runtime.ResourceAccess;

/**
 * A RuntimeEnvironment when the Program object is available.
 * @author Bo Ilic
 */
public final class DynamicRuntimeEnvironment extends RuntimeEnvironment {   
    
    /** The program in which we are operating. */
    private final Program program;
    
    
    public DynamicRuntimeEnvironment(Program program, ResourceAccess resourceAccess) {
        super(resourceAccess);
        this.program = program;
    }

    /** {@inheritDoc} */
    @Override
    public final Class<?> getForeignClass(final String qualifiedTypeConsName, final String foreignName) throws UnableToResolveForeignEntityException {

        final QualifiedName typeName = QualifiedName.makeFromCompoundName(qualifiedTypeConsName);

        //fetch the Class object from the type's ForeignTypeInfo 

        final ForeignTypeInfo foreignTypeInfo =
            program
            .getModule(typeName.getModuleName())
            .getModuleTypeInfo()
            .getTypeConstructor(typeName.getUnqualifiedName())
            .getForeignTypeInfo();

        if (foreignTypeInfo != null) {
            return foreignTypeInfo.getForeignType();
        }

        return null;       
    }

    /** {@inheritDoc} */
    @Override
    public String[][] getArgNamesAndTypes(String qualifiedFunctionName) {
        try {           
            MachineFunction mf = program.getCodeLabel(QualifiedName.makeFromCompoundName(qualifiedFunctionName));
            if (mf != null && mf.getArity() > 0) {
                String argNames[] = mf.getParameterNames();
                TypeExpr argTypes[] = mf.getParameterTypes();
                String argTypeStrings[] = new String[argTypes.length];
                for (int i = 0; i < argTypeStrings.length; ++i) {
                    if (argTypes[i] == null) {
                        argTypeStrings[i] = "internal type";
                    } else {
                        argTypeStrings[i] = argTypes[i].toString();
                    }
                }
                
                return new String[][] {argNames, argTypeStrings};
                
            } else {
                return super.getArgNamesAndTypes(qualifiedFunctionName);
            }
        } catch (Exception e) {
            //todoBI it is dubious to just place a catch-all Exception here. I'm not sure why it was put in.
            return super.getArgNamesAndTypes(qualifiedFunctionName);
        }
    }          
}
