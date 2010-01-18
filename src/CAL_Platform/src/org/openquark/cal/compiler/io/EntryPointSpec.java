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
 * EntryPointSpec.java
 * Created: Apr 8, 2004
 * By: RCypher
 */

package org.openquark.cal.compiler.io;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openquark.cal.compiler.QualifiedName;


/**
 * EntryPointSpec encapsulates the information needed to generate an EntryPoint.
 * @author RCypher
 * Created: Apr 8, 2004
 * @see EntryPoint
 */
public final class EntryPointSpec {

    /** The qualified name of the functional agent to run. This cannot be null. */
    private final QualifiedName functionalAgentName;
    
    /** The policies for marshaling any inputs. If null default input policies are assumed. No elements in the array can be null. */
    private final InputPolicy[] inputPolicies;
    
    /** The policy for marshaling the output. This cannot be null. */
    private final OutputPolicy outputPolicy;
    
    /** 
     * Creates a new entry point specification with specified input and output policies.
     * @param functionalAgentName the qualified name of the functional agent to run. This cannot be null.
     * @param inputPolicies  array of input policies. 
     *        If this is null, default input policies are assumed for each of the functional agents inputs. 
     *        If the array is not null, no elements in the array may be null.
     *        A zero length array means that there are no inputs. 
     * @param outputPolicy the output policy. This cannot be null.
     */ 
    private EntryPointSpec(QualifiedName functionalAgentName, InputPolicy[] inputPolicies, OutputPolicy outputPolicy) {
        if (functionalAgentName == null) {
            throw new NullPointerException ("Argument functionalAgentName cannot be null.");        
        }
        if (outputPolicy == null) {
            throw new NullPointerException ("Argument outputPolicy cannot be null.");
        }            

        this.functionalAgentName = functionalAgentName;                  
        this.outputPolicy = outputPolicy;

        if (inputPolicies == null ) {
            this.inputPolicies = null; 
        } else {
            this.inputPolicies = inputPolicies.clone();  
            for (int i = 0, length = this.inputPolicies.length; i < length; ++i) {
                if (this.inputPolicies[i] == null) {
                    throw new NullPointerException ("Unable to create EntryPointSpec: null InputPolicy.");
                }
            }
        }   
    }


    /**
     * Creates a new entry point specification with default input and output policies.
     * 
     * @param functionalAgentName the qualified name of the functional agent to run
     * @return the new entry point spec
     */
    static public EntryPointSpec make(QualifiedName functionalAgentName) {
        return make(functionalAgentName, null, OutputPolicy.DEFAULT_OUTPUT_POLICY);
    }

    /**
     * Creates a new entry point specification with default input policies and specified output policy.
     * 
     * @param functionalAgentName the qualified name of the functional agent to run
     * @param outputPolicy the output policy
     * @return the new entry point spec
     */
    static public EntryPointSpec make(QualifiedName functionalAgentName, OutputPolicy outputPolicy) {
        return make(functionalAgentName, null, outputPolicy);
    }

    /**
     * Creates a new entry point specification with input and output policies
     * @param functionalAgentName
     * @param inputPolicies
     * @param outputPolicy
     * @return the new entry point spec
     */
    public static EntryPointSpec make(QualifiedName functionalAgentName, InputPolicy[] inputPolicies, OutputPolicy outputPolicy) {
        return new EntryPointSpec(functionalAgentName, inputPolicies, outputPolicy);
    }

    /**
     * Creates a list of entry point specifications with default input and output policies.
     * 
     * @param functionalAgentNames collection of qualified names. This argument cannot be null.
     * @return a list of entry point specs
     */
    static public List<EntryPointSpec> buildListFromQualifiedNames(Collection<QualifiedName> functionalAgentNames) {        
        if (functionalAgentNames == null) {
            throw new NullPointerException("FunctionalAgentNames cannot be null.");
        }
        
        List<EntryPointSpec> entryPointSpecs = new ArrayList<EntryPointSpec>();
        
        for (final QualifiedName functionalAgentName : functionalAgentNames) {
            entryPointSpecs.add(EntryPointSpec.make(functionalAgentName));
        }
        return entryPointSpecs;
    }

    /**
     * Get the functional agent name of this entry point spec. It cannot be null.
     * @return the qualified name of the target.
     */
    public final QualifiedName getFunctionalAgentName () {
        return functionalAgentName;
    }
    
    /**
     * This gets an array containing all the input policies.
     * It may be null, which means that default input policies are used. 
     * If it is zero length it means there are no inputs.
     * @return an array of input policies, or null.
     */
    public final InputPolicy[] getInputPolicies () {
        if (inputPolicies == null) {
            return null;
        } else {
            return inputPolicies.clone();
        }       
    }
    
    /**
     * Get the output policy. This cannot be null.
     * @return the output policy.
     */
    public final OutputPolicy getOutputPolicy () {
        return outputPolicy;
    }
    
    /**
     * Returns a new entry point spec with specified output policy
     * @param outputPolicy
     * @return entryPointSpec with new output policy
     */
    public final EntryPointSpec cloneWithOutputPolicy (OutputPolicy outputPolicy) {
        return EntryPointSpec.make(getFunctionalAgentName(), getInputPolicies(), outputPolicy);
    }
    
    /**
     * Returns a new entry point spec with specified input policies
     * @param inputPolicies
     * @return entryPointSpec with new input policies
     */
    public final EntryPointSpec cloneWithInputPolicies (InputPolicy[] inputPolicies) {
        return EntryPointSpec.make(getFunctionalAgentName(), inputPolicies, getOutputPolicy());
    }
    
    /**
     * Returns a new entry point spec with specified input/output policies
     * @param inputPolicies
     * @param outputPolicy
     * @return entryPointSpec with new input/output policies
     */
    public final EntryPointSpec cloneWithInputOutputPolicies (InputPolicy[] inputPolicies, OutputPolicy outputPolicy) {
        return EntryPointSpec.make(getFunctionalAgentName(), inputPolicies, outputPolicy);
    }
    
    @Override
    public String toString () {
        StringBuilder sb = new StringBuilder();
        sb.append ("EntryPointSpec: target - ");
        sb.append (functionalAgentName.getQualifiedName());
        if (inputPolicies == null) {
            sb.append (", default input policies, output policy - ");
        } else {
            sb.append (", input policies - [");
            for (int i = 0; i < inputPolicies.length; ++i) {
                sb.append (inputPolicies[i].toString());
                if (i < inputPolicies.length - 1) {
                    sb.append (", ");
                }
            }
            sb.append ("], output policy - ");
        }
        sb.append (outputPolicy.toString());
        
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (! (other instanceof EntryPointSpec)) {
            return false;
        }
        
        EntryPointSpec otherSpec = (EntryPointSpec) other;
        
        if (otherSpec.functionalAgentName != functionalAgentName) {
            return false;
        }
        
        if (inputPolicies != null) {
            if (otherSpec.inputPolicies == null) {
                return false;
            }
            
            if (otherSpec.inputPolicies.length != inputPolicies.length) {
                return false;
            }

            for(int i=0; i < inputPolicies.length; i++) {
                if (!otherSpec.inputPolicies[i].getMarshaler().toString().equals(
                    inputPolicies[i].getMarshaler().toString())) {
                    return false;
                }
                if (otherSpec.inputPolicies[i].getNArguments() != 
                    inputPolicies[i].getNArguments()) {
                    return false;
                }
            }
            
        }
        if (outputPolicy != null) {
            if (otherSpec.outputPolicy == null) {
                return false;
            }

            if (!outputPolicy.getMarshaler().toString().equals(
                otherSpec.outputPolicy.getMarshaler().toString())) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int result = 17;
        
        result = 37 * result + functionalAgentName.hashCode();
        
        if (inputPolicies != null) {
            for(InputPolicy input : inputPolicies) {
                result = 37 * result + input.getMarshaler().toString().hashCode();
                
                int f = input.getNArguments();
                result = 37 * result + (f ^ (f >>> 32));
            }
        }

        if (outputPolicy != null) {
            result = 37 * result + outputPolicy.toString().hashCode();
        }

        return result;
       
    }
    
}
    
