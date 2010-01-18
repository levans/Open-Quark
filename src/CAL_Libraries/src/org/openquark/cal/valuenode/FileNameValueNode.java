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
 * FileNameValueNode.java
 * Creation date: (05/06/01 11:02:31 AM)
 * By: Michael Cheng
 */
package org.openquark.cal.valuenode;

import org.openquark.cal.compiler.DataConstructor;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.io.InputPolicy;
import org.openquark.cal.compiler.io.OutputPolicy;
import org.openquark.cal.module.Cal.IO.CAL_File;


/**
 * A specialized AlgebraicValueNode used to handle values of the File.FileName type.
 *
 * Creation date: (05/06/01 11:02:31 AM)
 * @author Michael Cheng
 */
public class FileNameValueNode extends AlgebraicValueNode {

    /**
     * A custom ValueNodeProvider for the FileNameValueNode.
     * @author Frank Worsley
     */
    public static class FileNameValueNodeProvider extends ValueNodeProvider<FileNameValueNode> {

        public FileNameValueNodeProvider(ValueNodeBuilderHelper builderHelper) {
            super(builderHelper);
        }
        
        /**
         * @see org.openquark.cal.valuenode.ValueNodeProvider#getValueNodeClass()
         */
        @Override
        public Class<FileNameValueNode> getValueNodeClass() {
            return FileNameValueNode.class;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public FileNameValueNode getNodeInstance(Object value, DataConstructor dataConstructor, TypeExpr typeExpr) {
            
            // Check for handleability.
            if (!typeExpr.isNonParametricType(CAL_File.TypeConstructors.FileName)) {
                return null;
            }
            
            return new FileNameValueNode((String) value, typeExpr);
        }        
    }    
    
    /** The absolute path of the file name this value node represents. */
    private String fileName;

    /**
     * FileNameValueNode constructor.
     * @param fileName if null, then the default value of an empty "" filename will be given.
     * @param typeExpr the type expression of the value node. Must be of type FileName.
     */
    public FileNameValueNode(String fileName, TypeExpr typeExpr) {
        super(typeExpr);
        checkTypeConstructorName(typeExpr, CAL_File.TypeConstructors.FileName);
        this.fileName = (fileName == null) ? "" : fileName;
    }
    
    /**
     * @see org.openquark.cal.valuenode.ValueNode#containsParametricValue()
     */
    @Override
    public boolean containsParametricValue() {
        return false;
    }    

    /**
     * @see org.openquark.cal.valuenode.ValueNode#copyValueNode(org.openquark.cal.compiler.TypeExpr)
     */
    @Override
    public FileNameValueNode copyValueNode(TypeExpr newTypeExpr) {
        checkCopyType(newTypeExpr);
        return new FileNameValueNode(getFileName(), newTypeExpr);
    }

    /**
     * @return the source model representation of the expression represented by this ValueNode.
     * @see org.openquark.cal.valuenode.ValueNode#getCALSourceModel()
     */
    @Override
    public SourceModel.Expr getCALSourceModel() {
        return CAL_File.Functions.makeFileName(fileName);
    }
    
    /**   
     * @return the file name represented by this value node
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * @see org.openquark.cal.valuenode.ValueNode#getTextValue()
     */
    @Override
    public String getTextValue() {
        return fileName;
    }    
    
    /**
     * @see org.openquark.cal.valuenode.ValueNode#getValue()
     */
    @Override
    public Object getValue() {   
        return fileName;
    }   

    /**
     * Return an input policy which describes how to marshall a value represented
     * by a value node from Java to CAL.
     * @return - the input policy associated with ValueNode instance.
     */
    @Override
    public InputPolicy getInputPolicy () {
        return InputPolicy.makeTypedDefaultInputPolicy(getTypeExpr().toSourceModel().getTypeExprDefn());
    }
    
    /**
     * Return an array of objects which are the values needed by the marshaller
     * described by 'getInputPolicy()'.
     * @return - an array of Java objects corresponding to the value represented by a value node instance.
     */
    @Override
    public Object[] getInputJavaValues () {
        
        return new Object[]{fileName};
    }

    /**
     * Return an output policy which describes how to marshall a value represented
     * by a value node from CAL to Java.
     * @return - the output policy associated with the ValueNode instance.
     */
    @Override
    public OutputPolicy getOutputPolicy() {
        return OutputPolicy.DEFAULT_OUTPUT_POLICY;
    }
    
    /**
     * Set a value which is the result of the marshaller described by
     * 'getOutputPolicy()'.
     * @param value - the java value
     */
    @Override
    public void setOutputJavaValue(Object value) {
        if (!(value instanceof String)) {
            throw new IllegalArgumentException("Error in FileNameValueNode.setOutputJavaValue: output must be an instance of String, not an instance of: " + value.getClass().getName());
        }
        
        fileName = (String)value;
    }
    
}
