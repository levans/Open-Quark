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
 * CodeGem.java
 * Creation date: (1/18/01)
 * By: Luke Evans
 */
package org.openquark.gems.client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openquark.cal.compiler.CodeAnalyser;
import org.openquark.cal.compiler.CodeQualificationMap;
import org.openquark.cal.compiler.CompositionNode;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.metadata.ArgumentMetadata;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.services.CALFeatureName;
import org.openquark.cal.services.CALPersistenceHelper;
import org.openquark.gems.client.Argument.NameTypePair;
import org.openquark.util.Pair;
import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.XMLPersistenceConstants;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * A CodeGem is a FunctionalAgentGem which is defined from code which can be modified.
 * @author Luke Evans
 */
public final class CodeGem extends Gem implements CompositionNode.Lambda, NamedGem {

    /** Listener for definition change events on this gem */
    private CodeGemDefinitionChangeListener definitionChangeListener;
    
    /**
     * A default name for a code gem.
     * This is needed to pass validation on creation, but we shouldn't see ever see this in the GemCutter.
     */
    private static final String DEFAULT_NAME = "unassignedCodeGem";
    
    /** Unqualified name of the code gem */
    private String unqualifiedName;
    
    /** Fully qualified code which this gem is responsible for. */
    private String qualifiedCode;
    
    /** Code visible to the user. This is a version of the qualified code which may contain unqualified identifiers. */
    private String visibleCode;

    //
    // Definition of the pseudo-gem entity.
    // The size of the arguments array gives the number of named arguments.
    // The type of the pseudo-entity is arg1type -> arg2type -> ... -> argntype -> codeResultType.
    //
    
    /** Arguments used in the codegem */
    private Argument.NameTypePair[] arguments = new Argument.NameTypePair[0];
    
    /** Result type of the code expression; null if broken. */
    private TypeExpr codeResultType;
    
    /** Gem broken state (appearance to indicate incompleteness) */
    private boolean broken;
    
    /* 
     * Keys for fields in map used with state editable interface 
     */
    private static final String BROKEN_KEY = "BrokenStateKey";
    private static final String INPUT_PARTS_KEY = "InputPartsStateKey";
    private static final String VISIBLECODE_KEY = "VisibleCodeStateKey";
    private static final String QUALIFICATIONMAP_KEY = "QualificationMapStateKey";
    private static final String ARGUMENTS_KEY = "ArgumentsStateKey";
    private static final String CODE_RESULT_TYPE_KEY = "CodeResultTypeStateKey";
    private static final String UNQUALIFIED_NAME_KEY = "UnqualifiedNameKey";
    private static final String QUALIFIED_CODE_KEY = "QualifiedCodeKey";
    
    /** Symbol mapping from unqualified to qualified names */
    private CodeQualificationMap qualificationMap;
    
    
    /**
     * Constructor for a CodeGem.
     */
    public CodeGem() {
        // Initialize with no inputs.
        super(0);

        setName(DEFAULT_NAME);
        
        // Create an empty qualification map
        qualificationMap = new CodeQualificationMap();
        
        // Initialize code and arguments to nothing
        qualifiedCode = "";
        visibleCode = "";

        // As this constructor creates an empty code object, this gem must start out broken
        setBroken(true);
    }

    /**
     * Constructor for a code gem with a given number of inputs, and whose connectable parts will have
     *   parametric type.
     * @param nInputs the number of inputs on the code gem.
     */
    CodeGem(int nInputs) {
        this();
        
        // Create the arguments.        
        Argument.NameTypePair[] codeGemArguments = new Argument.NameTypePair[nInputs];
        for (int i = 0; i < nInputs; i++) {
            String argName = "x" + i;
            codeGemArguments[i] = new Argument.NameTypePair(argName, TypeExpr.makeParametricType());
        }
        
        // Set the code
        setQualifiedCode(CAL_Prelude.Functions.error.getQualifiedName() + " \"Uninitialized code gem.\"");
        visibleCode = qualifiedCode;
        qualificationMap = new CodeQualificationMap();

        // Set the arguments
        setArguments(codeGemArguments);

        // Set the code result type.
        codeResultType = TypeExpr.makeParametricType();
        
        // Make appropriate inputs
        morphInputs(codeGemArguments, new HashMap<String, PartInput>());

        // Update the part types.  
        for (int i = 0; i < codeGemArguments.length; i++) {
            getInputPart(i).setType(codeGemArguments[i].getType());
        }
        getOutputPart().setType(codeResultType);

        // update the broken state
        setBroken(false);
    }

    /**
     * Constructor for a CodeGem.
     * The result type and argument info will be determined (if possible) using the specified code analyser.
     * @param codeAnalyser
     * @param visibleCode
     * @param varNamesWhichAreArgs
     */
    public CodeGem (CodeAnalyser codeAnalyser, 
                    String visibleCode,
                    Set<String> varNamesWhichAreArgs) {
        this();

        this.visibleCode = visibleCode;
        CodeAnalyser.AnalysisResults results = codeAnalyser.analyseCode (visibleCode, varNamesWhichAreArgs, null);
        qualificationMap = results.getQualificationMap();

        // Find the new arguments in the appropriate order.
        TypeExpr newCodeGemType = results.getTypeExpr ();
        String [] argNamesFromCode = results.getAllArgumentNames();

        // Make an argument list
        int argCount = argNamesFromCode.length;
        Argument.NameTypePair[] argsFromCode = new Argument.NameTypePair[argCount];
    
        if (newCodeGemType != null) {
            TypeExpr[] typePieces = newCodeGemType.getTypePieces();
            for (int i = 0; i < argCount; i++) {
                argsFromCode[i] = new Argument.NameTypePair(argNamesFromCode[i], typePieces[i]);
            }
            
        } else {
            // No type - broken gem.
            for (int i = 0; i < argCount; i++) {
                argsFromCode[i] = new Argument.NameTypePair(argNamesFromCode[i], TypeExpr.makeParametricType());
            }
        }
        
        // Get the output type; note that the type expression contains only used argument types
        Map<String, PartInput> oldNameToInputMap = new HashMap<String, PartInput> ();
        TypeExpr codeResultType = results.getTypeExpr() == null ? null : results.getTypeExpr().dropFirstNArgs(argCount);

        // Update the code gem            
        definitionUpdate(results.getQualifiedCode(), argsFromCode, codeResultType, oldNameToInputMap, results.getQualificationMap(), visibleCode);
        
        // Update the part types.  
        for (int i = 0; i < argsFromCode.length; i++) {
            getInputPart(i).setType(argsFromCode[i].getType());
        }
        getOutputPart().setType(codeResultType);
    }

    /**
     * Obtain the code contribution of this CompositionNode, assuming this node is of an 
     * appropriate code contributing type.
     * @return the code contribution
     */
    public String getLambda() {
        // CodeGems are lambda expressions.  Return the validated code only

        //todoBI remove this (taken from SupercombinatorFromCode.makeLambda())
        /*
         // Need to omit the trailing semicolon on the body
         String body;
         int lastChar = qualifiedCode.length() - 1;
         if (qualifiedCode.charAt(lastChar) == ';') {
         body = qualifiedCode.substring(0, lastChar);
         } else {
         body = qualifiedCode;
         }
         */
        
        String body = qualifiedCode;
        
        //constants such as "abc" are not written out as \ -> "abc", since this is invalid CAL syntax.
        if (arguments.length == 0) {
            return body;
        }
        
        // Create the root of a lambda definition
        StringBuilder lambda = new StringBuilder("\\");
        
        // Add formal parameters
        for (int i = 0; i < arguments.length; i++) {        
            // Get this argument
            Argument.NameTypePair arg = arguments[i];
            
            // Add space?
            if (i > 0) {
                lambda.append(' ');
            }
            
            // Add argument name (unqualified, as qualification yields bad CAL syntax)
            // The arguments come from the code, so the unqualified names unambiguously come from the current module
            lambda.append(arg.getName());
        }
        
        lambda.append(" -> " + body);
        
        // Return the final lambda
        return lambda.toString();
    }
    
    /**
     * Returns the fully qualified code contained in this code gem.
     */
    public String getCode () {
        return qualifiedCode;
    }
    
    /**
     * Get the arguments for this code gem.
     * @return Argument.NameTypePair[] the arguments.  This should never be null.
     */
    public Argument.NameTypePair[] getArguments() {
        return arguments.clone();
    }
    
    /**
     * Set the arguments for this code gem.
     * @param args the arguments
     */
    private void setArguments(Argument.NameTypePair[] args) {
        this.arguments = (args == null) ? new Argument.NameTypePair[0] : (Argument.NameTypePair[])args.clone();
    }
    
    /**
     * Returns the code visible to the user (may include unqualified names)
     */
    public String getVisibleCode() {
        return visibleCode;
    }

    /**
     * Get the unqualified name of the Gem (the name of the underlying supercombinator)
     * @return the name
     */
    public String getUnqualifiedName() {
        return unqualifiedName;
    }

    /**
     * Returns true if this Gem has been given a name.
     * @return boolean
     */
    boolean isNameInitialized() {
        // If the name is not the same object as the default name then it has been initialized.
        return DEFAULT_NAME != getUnqualifiedName();
    }

    /**
     * Returns whether this gem is broken
     * @return boolean true if this gem is broken
     */
    @Override
    public boolean isBroken() {
        return broken;
    }

    /**
     * Set the broken state of this Gem
     * @param newBroken boolean the new broken state of the Gem
     */    
    void setBroken(boolean newBroken) {
        if (broken != newBroken) {
            broken = newBroken;
            if (gemStateListener != null) {
                gemStateListener.brokenStateChanged(new GemStateEvent(this, GemStateEvent.EventType.BROKEN));
            }
        }
    }
    
    /**
     * Get an updated map from arg name to input.
     * @return Map an updated map from arg name to input.
     */
    public Map<String, PartInput> getArgNameToInputMap() {

        Map<String, PartInput> resultMap = new HashMap<String, PartInput>();

        int nArgs = getNInputs();
        for (int i = 0; i < nArgs; i++) {
            String argName = arguments[i].getName();
            PartInput input = getInputPart(i);
            resultMap.put(argName, input);
        }

        return resultMap;
    }

    /**
     * Indicate that the definition of the code gem function has changed.
     * Note that the brokenness characteristic of the code gem will be updated such that it reflects the validity of the code gem itself.
     * It will be up to the caller to further update the brokenness of the codegem according to whether its connections (if any) are valid.
     * 
     * @param qualifiedCodeGemText the qualified expression code
     * @param newCodeGemArgs what the new list of arguments will be.  Argument types can be null 
     *      if the new code gem state is broken.
     * @param codeResultType the result type from the code.  Null if broken.
     *   The type of the code will be argType_1 -> .. -> argType_n -> codeResultType.
     * @param argNameToInputMap the map from arg name to input from the last "good" state of the code gem
     */
    void definitionUpdate(String qualifiedCodeGemText, Argument.NameTypePair[] newCodeGemArgs, TypeExpr codeResultType, 
            Map<String, PartInput> argNameToInputMap, CodeQualificationMap qualificationMap, String codeGemVisibleText) {

        // Set the code 
        setQualifiedCode(qualifiedCodeGemText);
        visibleCode = codeGemVisibleText;
        
        // Set the qualification map
        this.qualificationMap = qualificationMap;

        // Set the arguments
        setArguments(newCodeGemArgs);

        // Set the result type.
        this.codeResultType = codeResultType;

        // Make appropriate inputs
        morphInputs(newCodeGemArgs, argNameToInputMap);

        // update the broken state, according to whether the code was ok.
        setBroken(codeResultType == null);

        // notify listeners of the definition change
        if (definitionChangeListener != null) {
            definitionChangeListener.codeGemDefinitionChanged(new CodeGemDefinitionChangeEvent(CodeGem.this));
        }
    }

    /**
     * Morph the number and type of the input parts.
     * 
     * @param args the array of arguments in their new order
     * @param argNameToInputMap the map from old arg name to input.  
     *   We need this as a parameter (rather than generating it from current inputs) since we want to preserve inputs 
     *   and the code gem may be in an intermediate invalid form
     */
    private void morphInputs(Argument.NameTypePair[] args, Map<String, PartInput> argNameToInputMap) {

        PartInput[] newInputPartArray = new PartInput[args.length];

        // make a list of the inputs with corresponding variables in the typed-in code
        PartInput input;
        for (int i = 0; i < args.length; i++) {
            // if you want to preserve the input, take the reference, else make up the type using args
            input = argNameToInputMap.get(args[i].getName());

            if (input != null) {
                input.setInputNum(i);                    // update input #
            } else {
                input = createInputPart(i);
                input.setArgumentName(new ArgumentName(args[i].getName()));
                
                ArgumentMetadata metadata = new ArgumentMetadata(CALFeatureName.getArgumentFeatureName(i), input.getDesignMetadata().getLocale());
                metadata.setDisplayName(args[i].getName());
                input.setDesignMetadata(metadata);
            }
            newInputPartArray[i] = input;
        }

        // newInputPartArray has the new input parts.  Switch the parts over
        setInputParts(newInputPartArray);
    }

    /**
     * Describe this Gem
     * @return the description
     */
    @Override
    public String toString() {
        return getUnqualifiedName();
    }

    /*
     * Methods to handle listeners     ****************************************************************
     */

    /**
     * Adds the specified gem definition listener to receive gem definition events from this gem.
     * If l is null, no exception is thrown and no action is performed.
     * @param l GemDefinitionListener the GemDefinitionListener to notify upon source code change
     */
    public void addDefinitionChangeListener(CodeGemDefinitionChangeListener l) {
        if (l == null) {
            return;
        }
        definitionChangeListener = GemEventMulticaster.add(definitionChangeListener, l);
    }

    /**
     * Removes the specified definition listener so that it no longer receives burn events from this gem. 
     * This method performs no function, nor does it throw an exception, if the listener specified by 
     * the argument was not previously added to this component.
     * If l is null, no exception is thrown and no action is performed.
     * @param l GemDefinitionListener the GemDefinitionListener to remove
     */
    public void removeDefinitionChangeListener(CodeGemDefinitionChangeListener l) {
        if (l == null) {
            return;
        }
        definitionChangeListener = GemEventMulticaster.remove(definitionChangeListener, l);
    }

    /*
     * Methods supporting javax.swing.undo.StateEditable ********************************************
     */

    /**
     * Restore the stored codegem state.
     * @param state the stored state
     */
    void restoreDefinitionState(Hashtable<?, ?> state) {

        Object stateValue;
        
        stateValue = state.get(new Pair<CodeGem, String>(CodeGem.this, QUALIFICATIONMAP_KEY));
        if (stateValue != null) {
            qualificationMap = ((CodeQualificationMap)stateValue).makeCopy();
        }

        stateValue = state.get(new Pair<CodeGem, String>(CodeGem.this, INPUT_PARTS_KEY));
        if (stateValue != null) {
            PartInput[] newInputParts = ((PartInput[])stateValue).clone();
            
            // Make sure inputs have the appropriate indices
            for (int i = 0; i < newInputParts.length; i++) {
                PartInput input = newInputParts[i];
                input.setInputNum(i);
            }
            
            setInputParts(newInputParts);
        }

        stateValue = state.get(new Pair<CodeGem, String>(CodeGem.this, BROKEN_KEY));
        if (stateValue != null) {
            broken = ((Boolean)stateValue).booleanValue();
        }
        
        stateValue = state.get(new Pair<CodeGem, String>(CodeGem.this, VISIBLECODE_KEY));
        if (stateValue != null) {
            visibleCode = (String)stateValue;
        }
        
        stateValue = state.get(new Pair<CodeGem, String>(CodeGem.this, ARGUMENTS_KEY));
        if (stateValue != null) {
            arguments = (Argument.NameTypePair[])stateValue;
        }
        
        stateValue = state.get(new Pair<CodeGem, String>(CodeGem.this, CODE_RESULT_TYPE_KEY));
        if (stateValue != null) {
            codeResultType = (TypeExpr)stateValue;
        }
        
        stateValue = state.get(new Pair<CodeGem, String>(CodeGem.this, UNQUALIFIED_NAME_KEY));
        if (stateValue != null) {
            unqualifiedName = (String)stateValue;
        }
        
        stateValue = state.get(new Pair<CodeGem, String>(CodeGem.this, QUALIFIED_CODE_KEY));
        if (stateValue != null) {
            qualifiedCode = (String)stateValue;
        }
        
        // notify listeners of the definition change
        if (definitionChangeListener != null) {
            definitionChangeListener.codeGemDefinitionChanged(new CodeGemDefinitionChangeEvent(CodeGem.this));
        }
    }

    /**
     * Save the codegem state.
     * @param state the table in which to store the codegem state
     */
    void storeDefinitionState(Hashtable<Object, Object> state) {
        
        // Now store all the states.  The state key we use is a pair: this code gem and the field key.
        state.put(new Pair<CodeGem, String>(CodeGem.this, QUALIFICATIONMAP_KEY), qualificationMap);
        state.put(new Pair<CodeGem, String>(CodeGem.this, INPUT_PARTS_KEY), getInputParts());
        state.put(new Pair<CodeGem, String>(CodeGem.this, BROKEN_KEY), Boolean.valueOf(isBroken()));
        state.put(new Pair<CodeGem, String>(CodeGem.this, VISIBLECODE_KEY), visibleCode);
        state.put(new Pair<CodeGem, String>(CodeGem.this, ARGUMENTS_KEY), arguments);
        state.put(new Pair<CodeGem, String>(CodeGem.this, UNQUALIFIED_NAME_KEY), unqualifiedName);
        state.put(new Pair<CodeGem, String>(CodeGem.this, QUALIFIED_CODE_KEY), qualifiedCode);
        if (codeResultType != null) {
            state.put(new Pair<CodeGem, String>(CodeGem.this, CODE_RESULT_TYPE_KEY), codeResultType);
        }
    }


    /*
     * Methods supporting XMLPersistable                 ********************************************
     * 
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveXML(Node parentNode, GemContext gemContext) {
        
        Document document = (parentNode instanceof Document) ? (Document)parentNode : parentNode.getOwnerDocument();

        // Create the code gem element
        Element resultElement = document.createElementNS(GemPersistenceConstants.GEM_NS, GemPersistenceConstants.CODE_GEM_TAG);
        resultElement.setPrefix(GemPersistenceConstants.GEM_NS_PREFIX);
        parentNode.appendChild(resultElement);

        // Add info for the superclass gem.
        super.saveXML(resultElement, gemContext);


        // Now add CodeGem-specific info

        // Add an element for the name.
        Element nameElement = CALPersistenceHelper.unqualifiedNameToElement(getUnqualifiedName(), document);
        resultElement.appendChild(nameElement);

        // Add the code
        Element codeElement = document.createElement(GemPersistenceConstants.CODE_GEM_CODE_TAG);
        resultElement.appendChild(codeElement);
        String code = getVisibleCode();
        CDATASection codeNode = XMLPersistenceHelper.createCDATASection(document, code);
        codeElement.appendChild(codeNode);
        
        // Add the code qualification map
        qualificationMap.saveToXML(codeElement);

        // Add the args (tells us about any input reordering).
        Element argsElement = document.createElement(GemPersistenceConstants.CODE_GEM_ARGUMENTS_TAG);
        resultElement.appendChild(argsElement);

        for (final NameTypePair arg : arguments) {

            Element argElement = document.createElement(GemPersistenceConstants.CODE_GEM_ARGUMENT_TAG);
            argElement.setAttribute(GemPersistenceConstants.CODE_GEM_ARGUMENT_NAME_ATTR, arg.getName());

            argsElement.appendChild(argElement);
        }
        
        // Add brokenness
        resultElement.setAttribute(GemPersistenceConstants.CODE_GEM_BROKEN_ATTR, isBroken() ? XMLPersistenceConstants.TRUE_STRING : XMLPersistenceConstants.FALSE_STRING);
    }

    /**
     * Create a new CodeGem and loads its state from the specified XML element.
     * @param gemElement Element the element representing the structure to deserialize.
     * @param gemContext the context in which the gem is being instantiated.
     * @param codeAnalyser used to analyze the gem code 
     * @return CodeGem
     * @throws BadXMLDocumentException
     */
    public static CodeGem getFromXML(Element gemElement, 
                                     GemContext gemContext, 
                                     CodeAnalyser codeAnalyser) throws BadXMLDocumentException {
        CodeGem gem = new CodeGem();
        gem.loadXML(gemElement, gemContext, codeAnalyser);
        return gem;
    }

    /**
     * Load this object's state.
     * Note: part types are not set in this method.
     * @param gemElement Element the element representing the structure to deserialize.
     * @param gemContext the context in which the gem is being instantiated.
     * @param codeAnalyser object to use for code analysis 
     */
    void loadXML(Element gemElement, 
                 GemContext gemContext, 
                 CodeAnalyser codeAnalyser) throws BadXMLDocumentException {

        XMLPersistenceHelper.checkTag(gemElement, GemPersistenceConstants.CODE_GEM_TAG);
        XMLPersistenceHelper.checkPrefix(gemElement, GemPersistenceConstants.GEM_NS_PREFIX);

        List<Element> childElems = XMLPersistenceHelper.getChildElements(gemElement);
        
        // Get info for the underlying gem.
        Element superGemElem = (childElems.size() < 1) ? null : childElems.get(0);
        XMLPersistenceHelper.checkIsElement(superGemElem);
        super.loadXML(superGemElem, gemContext);

        // Get the name
        Element nameElem = (childElems.size() < 2) ? null : childElems.get(1);
        XMLPersistenceHelper.checkIsElement(nameElem);
        String gemName = CALPersistenceHelper.elementToUnqualifiedName(nameElem);
        setName(gemName);

        // Get the code
        Element codeElement = (childElems.size() < 3) ? null : childElems.get(2);
        XMLPersistenceHelper.checkIsElement(codeElement);
        XMLPersistenceHelper.checkTag(codeElement, GemPersistenceConstants.CODE_GEM_CODE_TAG);

        StringBuilder codeText = new StringBuilder();
        Node codeChild = codeElement.getFirstChild();
        XMLPersistenceHelper.getAdjacentCharacterData(codeChild, codeText);
        String codeString = codeText.toString();
        setQualifiedCode(codeString);
        
        // Get and check the qualification map
        // This check is done for backwards compatibility, in case we are loading 
        // a code gem without a qualification map.
        if (XMLPersistenceHelper.getChildElement(codeElement, CodeQualificationMap.QUALIFICATION_MAP_TAG) != null) {
            qualificationMap.loadFromXML(codeElement);
            if (!codeAnalyser.checkQualificationMapValidity(qualificationMap)) {
                // The code qualification mappings have become invalid. We'll create a blank
                // qualification map and likely end up with a broken code gem.
                qualificationMap = new CodeQualificationMap();
            }
        } else {
            qualificationMap = new CodeQualificationMap();
        }
        
        // Get the args
        Element argsElement = (childElems.size() < 4) ? null : childElems.get(3);
        XMLPersistenceHelper.checkIsElement(argsElement);
        XMLPersistenceHelper.checkTag(argsElement, GemPersistenceConstants.CODE_GEM_ARGUMENTS_TAG);

        List<Element> argNodes = XMLPersistenceHelper.getChildElements(argsElement, GemPersistenceConstants.CODE_GEM_ARGUMENT_TAG);
        int numArgs = argNodes.size();
        String[] argNames = new String[numArgs];

        for (int i = 0; i < numArgs; i++) {
            Element argNode = argNodes.get(i);

            String argName = argNode.getAttribute(GemPersistenceConstants.CODE_GEM_ARGUMENT_NAME_ATTR);
            argNames[i] = argName;
        }
        Set<String> argNamesSet = new HashSet<String>(Arrays.asList(argNames));

        // Get whether this code gem is broken.
        boolean shouldBeBroken;
        try {
            shouldBeBroken = XMLPersistenceHelper.getBooleanAttribute(gemElement, GemPersistenceConstants.CODE_GEM_BROKEN_ATTR);
        } catch (BadXMLDocumentException e) {
            // Must be in the old save format.
            // TODOEL: remove when save formats have been updated.
            shouldBeBroken = false;
        }

        
        //
        // Begin code gem definition initialization.
        //
        
        // Analyze the code, and reevaluate arguments
        CodeAnalyser.AnalysisResults analysisResults = codeAnalyser.analyseCode(codeString, argNamesSet, qualificationMap);
        
        TypeExpr typeExpr = analysisResults.getTypeExpr();
        String[] argNamesFromCode = analysisResults.getAllArgumentNames();
        
        // Map arg names to types
        Map<String, TypeExpr> argNameToTypeMap = new HashMap<String, TypeExpr>();
        if (typeExpr != null) {
            TypeExpr[] typePieces = typeExpr.getTypePieces();
            for (int i = 0; i < argNamesFromCode.length; i++) {
                argNameToTypeMap.put(argNamesFromCode[i], typePieces[i]);
            }
        }

        // Get the arguments.
        Argument.NameTypePair[] codeGemArgs = new Argument.NameTypePair[argNames.length];
        for (int i = 0; i < argNames.length; i++) {
            String argName = argNames[i];
            TypeExpr argType = argNameToTypeMap.get(argName);
            if (argType == null) {
                // Argument is not used in code but part of the code gem
                argType = TypeExpr.makeParametricType();
                if (typeExpr != null) {
                    // This would not show as part of the type expression, so add it
                    typeExpr = TypeExpr.makeFunType(argType, typeExpr);
                }
            } 
            codeGemArgs[i] = new Argument.NameTypePair(argName, argType);
        }
        setArguments(codeGemArgs);
        
        Map<String, PartInput> argNameToInputMap = getArgNameToInputMap();

        // Get the output type.
        TypeExpr codeResultType = typeExpr == null ? null : typeExpr.dropFirstNArgs(argNames.length);

        // Update the code gem
        definitionUpdate(analysisResults.getQualifiedCode(), 
                         codeGemArgs, 
                         codeResultType, 
                         argNameToInputMap,
                         analysisResults.getQualificationMap(), 
                         codeString);
        
        // Update with the real broken state.
        shouldBeBroken |= (typeExpr == null);       // old save format..
        setBroken(shouldBeBroken);
    }
    
    /**
     * Make a copy of this codeGem. Note that namespace validity is not ensured
     * @return CodeGem
     */
    public CodeGem makeCopy() {
        
        // Make a new clone
        CodeGem clone = new CodeGem();
         
        // get the code from the original
        String oldCode = qualifiedCode;
        
        // Get the old arguments
        Argument.NameTypePair[] oldArguments = arguments;
        
        // make a new copy
        Argument.NameTypePair[] newArguments = new Argument.NameTypePair[oldArguments.length];
        
        // build a blank mapping so everything is updated in definition update.
        Map<String, PartInput> argNameToArgMap = new HashMap<String, PartInput>();
      
        for (int k =0; k < oldArguments.length; k++) {
            newArguments[k] = new Argument.NameTypePair(oldArguments[k].getName(), oldArguments[k].getType());
            argNameToArgMap.put(newArguments[k].getName(), null);
        }
        
        CodeQualificationMap newQualificationMap = this.qualificationMap.makeCopy();
        
        // update everything!
        clone.setName(getUnqualifiedName());
        clone.definitionUpdate(oldCode, newArguments, getCodeResultType(), argNameToArgMap, newQualificationMap, visibleCode);
        
        clone.setBroken(broken);
                
        return clone;
    }
    
    /**
     * Sets the name of this code
     * @param name
     */
    public void setName(String name) {
        String oldName = this.unqualifiedName;
        this.unqualifiedName = name;
        if (nameChangeListener != null) {
            nameChangeListener.nameChanged(new NameChangeEvent(this, oldName));
        }
    }
    
    /**
     * Set the CAL source for this supercombinator.
     * @param qualifiedCode the code
     */
    private void setQualifiedCode(String qualifiedCode) {
        this.qualifiedCode = qualifiedCode;
    }
    
    /**
     * Get the code result type.
     * @return TypeExpr the code result type.
     */
    public TypeExpr getCodeResultType() {
        return codeResultType;
    }

    /** 
     * Gets mapping from unqualified to qualified identifiers 
     */
    public CodeQualificationMap getQualificationMap() {
        return qualificationMap;
    }
}

