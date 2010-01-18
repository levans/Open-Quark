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
 * TemplateStringBinding.java
 * Created: 23-Apr-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.module.Cal.Collections.CAL_List;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.gems.client.FunctionalAgentGem;
import org.openquark.gems.client.Gem;
import org.openquark.gems.client.GemGraph;
import org.openquark.gems.client.ValueGem;
import org.openquark.gems.client.services.GemFactory;
import org.openquark.samples.bam.BindingContext;
import org.openquark.samples.bam.model.MessageSourceDescription.MessagePropertyDescription;
import org.openquark.util.xml.BadXMLDocumentException;
import org.openquark.util.xml.XMLPersistenceHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * A template binding represents a gem input that is bound to a template string.
 */
public class TemplateStringBinding extends InputBinding {
    
    private final String template;
    
    private final List<InputBinding> argumentBindings;
    
    public TemplateStringBinding (String template, List<InputBinding> argumentBindings) {
        this.template = template;
        this.argumentBindings = new ArrayList<InputBinding> (argumentBindings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<MetricDescription> getRequiredMetrics() {
        Set<MetricDescription> metrics=new HashSet<MetricDescription>();
        
        for (final InputBinding binding : argumentBindings) {
            metrics.addAll( binding.getRequiredMetrics());
        }
        
        return metrics;
    }
    
    /**
     * @see org.openquark.samples.bam.model.InputBinding#getOutputGem(BasicCALServices, org.openquark.gems.client.GemGraph, org.openquark.samples.bam.BindingContext)
     */
    @Override
    public Gem getOutputGem (BasicCALServices calServices, GemGraph gemGraph, BindingContext bindingContext) {
        List<InputBinding> allBindings = parseAndSubstitute (template, argumentBindings);
        
        Gem resultGem = null;
        
        if (allBindings.isEmpty()) {
            resultGem = new GemFactory(calServices).makeValueGem("");
            
            gemGraph.addGem(resultGem);
        } else {
            // build the string up from right to left
            for (int bindingN = allBindings.size() - 1; bindingN >= 0; --bindingN) {
                InputBinding binding = allBindings.get (bindingN);
                
                Gem gem = binding.getOutputGem(calServices, gemGraph, bindingContext);
                
                if (resultGem == null) {
                    resultGem = gem;
                } else {
                    resultGem = appendGems(calServices, gemGraph, gem, resultGem);
                }
            }
        }

        return resultGem;
    }

    private Gem appendGems(BasicCALServices calServices, GemGraph gemGraph, Gem gem1, Gem gem2) {
        
        Gem appendGem = new FunctionalAgentGem(calServices.getGemEntity(QualifiedName.makeFromCompoundName("Cal.Samples.BusinessActivityMonitor.BAM.appendStrings")));
        gemGraph.addGem(appendGem);
                
        if  ( !(gem1 instanceof ValueGem)) {
            appendGem.getInputPart(0).setBurnt(true);
        }
        if ( !(gem2 instanceof ValueGem)) {
            appendGem.getInputPart(1).setBurnt(true);
        }
        
        switch (appendGem.getTargetInputs().size()) {
        case 2:
            gemGraph.connectGems(gem1.getOutputPart(), appendGem.getInputPart(0));
            gemGraph.connectGems(gem2.getOutputPart(), appendGem.getInputPart(1));
            return appendGem;
        case 1:
            Gem mapGem = new FunctionalAgentGem(calServices.getGemEntity(CAL_List.Functions.map));
            gemGraph.addGem(mapGem);
            gemGraph.connectGems(appendGem.getOutputPart(), mapGem.getInputPart(0));
            
            if (!(gem1 instanceof ValueGem)) {
                gemGraph.connectGems(gem1.getOutputPart(), mapGem.getInputPart(1));
                gemGraph.connectGems(gem2.getOutputPart(), appendGem.getInputPart(1));
            } else {
                gemGraph.connectGems(gem2.getOutputPart(), mapGem.getInputPart(1));
                gemGraph.connectGems(gem1.getOutputPart(), appendGem.getInputPart(0));
            }
            return mapGem;
            
        default:
            Gem zipGem = new FunctionalAgentGem(calServices.getGemEntity(CAL_List.Functions.zipWith));
            gemGraph.addGem(zipGem);
            gemGraph.connectGems(appendGem.getOutputPart(), zipGem.getInputPart(0));
            gemGraph.connectGems(gem1.getOutputPart(), zipGem.getInputPart(1));
            gemGraph.connectGems(gem2.getOutputPart(), zipGem.getInputPart(2));
            return zipGem;          
        }

    }
    
    
    /**
     * Method getTemplate
     * 
     * @return Returns the template string
     */
    public String getTemplate () {
        return template;
    }
    
    /**
     * Method getArgumentBindings
     * 
     * @return Returns an unmodifiable copy of the list or argument bindings
     */
    public List<InputBinding> getArgumentBindings () {
        return Collections.unmodifiableList(argumentBindings);
    }

    /**
     * Method parseAndSubstitute
     * 
     * @param template
     * @param argumentBindings
     * @return Returns a List of InputBindings that represent the template with the given arguments substituted
     */
    private static List<InputBinding> parseAndSubstitute (String template, List<InputBinding> argumentBindings) {
        List<InputBinding> items = new ArrayList<InputBinding> ();

        Pattern pattern = Pattern.compile ("\\{(\\d+)\\}");

        Matcher matcher = pattern.matcher (template);

        int index = 0;

        while (matcher.find ()) {
            if (matcher.start () > index) {
                items.add (new ConstantBinding (template.substring(index, matcher.start ())));
            }
            
            String argIndexString = matcher.group(1);
            
            int argIndex = Integer.parseInt(argIndexString);
            
            if (argIndex < argumentBindings.size()) {
                items.add (argumentBindings.get(argIndex));
            }
            
            index = matcher.end();
        }
        
        if (index < template.length()) {
            items.add (new ConstantBinding (template.substring(index)));
        }
        
        return items;
    }

    /**
     * @see org.openquark.samples.bam.model.InputBinding#getPresentation()
     */
    @Override
    public String getPresentation () {
        return "<Template String>";
    }

    /**
     * @see org.openquark.samples.bam.model.InputBinding#store(org.w3c.dom.Element)
     */
    @Override
    public void store (Element parentElem) {
        Document document = parentElem.getOwnerDocument();
        
        Element bindingElement = document.createElement(getXmlTag());
        parentElem.appendChild(bindingElement);
        
        bindingElement.setAttribute(MonitorSaveConstants.TemplateAttr, template);
        
        storeArguments (bindingElement);
    }

    /**
     * Method storeBindings
     * 
     * @param parentElement
     */
    private void storeArguments (Element parentElement) {
        Document document = parentElement.getOwnerDocument();
        
        Element bindingsElem = document.createElement(MonitorSaveConstants.ArgumentBindings);
        parentElement.appendChild(bindingsElem);
        
        for (final InputBinding binding : argumentBindings) {
            binding.store (bindingsElem);
        }
    }
    /**
     * Method getXmlTag
     * 
     * @return Returns the XML tag used to store a TemplateStringBinding
     */
    public static String getXmlTag () {
        return MonitorSaveConstants.TemplateStringBinding;
    }

    /**
     * Method Load
     * 
     * @param bindingElem
     * @param messagePropertyInfos
     * @return Returns a TemplateStringBinding loaded from the given XML element
     */
    public static InputBinding Load (Element bindingElem, Collection<MessagePropertyDescription> messagePropertyInfos) throws BadXMLDocumentException, InvalidFileFormat {
        XMLPersistenceHelper.checkTag(bindingElem, getXmlTag());
        
        String template = bindingElem.getAttribute(MonitorSaveConstants.TemplateAttr);
        
        List<InputBinding> argumentBindings = loadArguments (bindingElem, messagePropertyInfos);
        
        return new TemplateStringBinding (template, argumentBindings);
    }

    /**
     * Method loadArguments
     * 
     * @param bindingElem
     * @return Returns a List of InputBindings
     */
    private static List<InputBinding> loadArguments (Element bindingElem, Collection<MessagePropertyDescription> messagePropertyInfos) throws BadXMLDocumentException, InvalidFileFormat {
        Element bindingsElem = XMLPersistenceHelper.getChildElement(bindingElem, MonitorSaveConstants.ArgumentBindings);
        
        List<InputBinding> inputBindings = new ArrayList<InputBinding> ();
        
        List<Element> bindingElemList = XMLPersistenceHelper.getChildElements(bindingsElem);
        
        for (final Element argumentElem : bindingElemList) {
            inputBindings.add(loadArgument (argumentElem, messagePropertyInfos));
        }
        
        return inputBindings;
    }

    /**
     * Method loadArgument
     * 
     * @param argumentElem
     * @param messagePropertyInfos
     * @return Returns an InputBinding loaded from the given XML element
     */
    private static InputBinding loadArgument (Element argumentElem, Collection<MessagePropertyDescription> messagePropertyInfos) throws BadXMLDocumentException, InvalidFileFormat {
        String tag = argumentElem.getTagName();
        
        // The only kind of binding we allow as an argument is a PropertyBinding
        if (tag.equals(PropertyBinding.getXmlTag ())) {
            return PropertyBinding.Load (argumentElem, messagePropertyInfos);
        } else if (tag.equals(MetricBinding.getXmlTag())) {
            return MetricBinding.Load (argumentElem, messagePropertyInfos);
        } else {
            throw new InvalidFileFormat ("Unexpected argument binding tag: " + tag);
        }
    }
    
    @Override
    public boolean isConstant() {
        return false;   
    }

}
