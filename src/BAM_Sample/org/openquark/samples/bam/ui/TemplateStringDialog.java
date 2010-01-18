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
 * TemplateStringDialog.java
 * Created: 26-Apr-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.GemEntity;
import org.openquark.samples.bam.MetricGemFilter;
import org.openquark.samples.bam.MonitorApp;
import org.openquark.samples.bam.model.InputBinding;
import org.openquark.samples.bam.model.MetricBinding;
import org.openquark.samples.bam.model.MetricDescription;
import org.openquark.samples.bam.model.PropertyBinding;
import org.openquark.samples.bam.model.TemplateStringBinding;
import org.openquark.samples.bam.model.MessageSourceDescription.MessagePropertyDescription;
import org.openquark.util.ui.DialogBase;



/**
 * 
 * 
 */
class TemplateStringDialog extends DialogBase {
    
    private static final long serialVersionUID = 3414194428514988569L;

    private static class MessagePropertyItem {
        
        final MessagePropertyDescription propertyDescription;
        
        /**
         * Constructor MessagePropertyItem
         * 
         * @param propertyDescription
         */
        MessagePropertyItem (final MessagePropertyDescription propertyDescription) {
            this.propertyDescription = propertyDescription;
        }
        
        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString () {
            return propertyDescription.name;
        }
    }
    
    private static class MetricItem {
        
        final MetricDescription metricDescription;
        
        /**
         * Constructor MetricItem
         * 
         * @param metricDescription
         */
        MetricItem (final MetricDescription metricDescription) {
            this.metricDescription = metricDescription;
        }
        
        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString () {
            return metricDescription.getDescription();
        }
    }
    
    private final Collection<MessagePropertyDescription> messagePropertyDescriptions;
       
    private String template = "";
    
    private List<InputBinding> argumentBindings = new ArrayList<InputBinding> ();
    
    private JList argumentJList;

    private JTextArea templateTextArea;
    
    private JButton insertArgumentButton;
    
    private Map<String, InputBinding> argumentNameToBindingMap=null;
    
    /**
     * Constructor TemplateStringDialog
     * 
     * @param owner
     */
    public TemplateStringDialog (Frame owner, TemplateStringBinding binding, Collection<MessagePropertyDescription> messagePropertyInfos) {
        super (owner, "Edit Template String");
        
        this.messagePropertyDescriptions = messagePropertyInfos;
        

        initialise (binding);
    }
    
    /**
     * Method initialize
     * 
     * @param binding
     */
    private void initialise (TemplateStringBinding binding) {
        if (binding != null) {
            template = binding.getTemplate ();
            
            argumentBindings.addAll(binding.getArgumentBindings());
        }
        
        createControls ();
    }

    /**
     * Method getTemplate
     * 
     * @return Returns the template string
     */
    String getTemplate () {
        return template;
    }
    
    /**
     * Method getArgumentBindings
     * 
     * @return Returns the argument bindings
     */
    List<InputBinding> getArgumentBindings () {
        return Collections.unmodifiableList(argumentBindings);
    }

    /**
     * Method createControls
     * 
     * 
     */
    private void createControls () {
        JPanel topPanel = getTopPanel ();

        GridBagConstraints constraints = new GridBagConstraints ();

        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets (2, 2, 2, 2);

        constraints.gridx = 0;
        constraints.gridy = 0;
        
        topPanel.add (new JLabel ("Properties and Metrics"), constraints);
        
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0;
        
        topPanel.add (new JScrollPane (getArgumentJList ()), constraints);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weighty = 0.0;

        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        
        topPanel.add (getInsertArgumentButton (), constraints);

        constraints.anchor = GridBagConstraints.NORTHWEST;

        constraints.gridx = 2;
        constraints.gridy = 0;
        
        topPanel.add (new JLabel ("Template string"), constraints);
        
        constraints.gridx = 2;
        constraints.gridy = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        
        topPanel.add (new JScrollPane (getTemplateTextArea ()), constraints);
        
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;

        addOKCancelButtons(2, 3);

        getContentPane ().add (topPanel);

        
        // pack makes the dialog too small
        // set the size of the dialog to half the screen width & 1/4 the screen height
        // or the preferred size, if larger
        
        Dimension prefSize = getPreferredSize();
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        setSize (Math.max (screenSize.width / 2, prefSize.width), Math.max (screenSize.height / 4, prefSize.height));
        
        validate();

        addComponentListener (new SizeConstrainer (getSize ()));
    }

    
    /**
     * @see org.openquark.util.ui.DialogBase#onOK()
     */
    @Override
    protected boolean onOK () {
        return parseTemplate ();
    }

    /**
     * Method parseTemplate
     * 
     * @return Returns true iff the formatted template is successfully parsed into a template and a list of bindings
     */
    private boolean parseTemplate () {
        String formattedTemplate = templateTextArea.getText();
        
        List<InputBinding> newArgumentBindings = new ArrayList<InputBinding> ();
        
        Pattern pattern = Pattern.compile ("\\{([^\\}]+)\\}");

        Matcher matcher = pattern.matcher (formattedTemplate);
        
        StringBuilder stringBuilder = new StringBuilder ();

        int index = 0;
        
        int argIndex = 0;

        while (matcher.find ()) {
            if (matcher.start () > index) {
                stringBuilder.append(formattedTemplate.substring(index, matcher.start ()));
            }
            
            String argumentNameString = matcher.group(1);
            
            InputBinding binding = findBinding (argumentNameString);

            if (binding != null) {
                newArgumentBindings.add (binding);
            } else {
                JOptionPane.showMessageDialog(this, "The argument '" + argumentNameString + "' is not recognised.", "BAM Sample", JOptionPane.WARNING_MESSAGE);
                
                return false;
            }
            
            stringBuilder.append('{');
            
            stringBuilder.append (Integer.toString(argIndex));
            
            stringBuilder.append ('}');
            
            ++argIndex;
            
            index = matcher.end();
        }
        
        if (index < formattedTemplate.length()) {
            stringBuilder.append(formattedTemplate.substring(index));
        }
        
        template = stringBuilder.toString();
        
        argumentBindings = newArgumentBindings;

        return true;
    }

    /**
     * Method findBinding
     * 
     * @param argumentNameString
     * @return Returns an InputBinding created from the given argument name
     */
    private InputBinding findBinding (String argumentNameString) {
        return argumentNameToBindingMap.get(argumentNameString);
    }

    /**
     * Method getArgumentJList
     * 
     * @return Returns the JList that shows the available properties and metrics
     */
    private JList getArgumentJList () {
        if (argumentJList == null) {
            DefaultListModel model = new DefaultListModel ();
            argumentNameToBindingMap = new HashMap<String, InputBinding>();
            
            //add all message properties
            for (final MessagePropertyDescription info : messagePropertyDescriptions) {
                model.addElement(new MessagePropertyItem (info));
                
                argumentNameToBindingMap.put(info.name, new PropertyBinding (info));
            }

            //add all metrics/properties combinations that produce printable outputs
            BasicCALServices calServices = MonitorApp.getInstance().getCalServices();
            for (final MessagePropertyDescription propertyInfo : messagePropertyDescriptions) {
                TypeExpr propertyType = propertyInfo.getCalType(calServices);
                TypeExpr printableType = calServices.getTypeFromString(MonitorApp.TARGET_MODULE, "Cal.Samples.BusinessActivityMonitor.BAM.Printable a => a");

                Set<GemEntity> gemEntities = new MetricGemFilter(propertyType, printableType ).getMatchingGems();
                for (final GemEntity gemEntity : gemEntities) {
                    MetricDescription metricDescription=new MetricDescription(gemEntity.getName(), propertyInfo );
                    model.addElement(new MetricItem (metricDescription));                        

                    argumentNameToBindingMap.put(metricDescription.getDescription(), new MetricBinding(metricDescription));                    
                }                    
            }
                    
            argumentJList = new JList (model);
            
            argumentJList.addMouseListener(new MouseAdapter () {

                @Override
                public void mouseClicked (MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        onInsertArgument ();
                    }
                }
            });
            
            argumentJList.setSelectedIndex(0);
        }
        
        return argumentJList;
    }

    /**
     * Method getInsertArgumentButton
     * 
     * @return Returns the JButton used to insert an argument
     */
    private Component getInsertArgumentButton () {
        if (insertArgumentButton == null) {
            insertArgumentButton = new JButton (new AbstractAction (">>") {

                private static final long serialVersionUID = -3150734253300126088L;

                public void actionPerformed (ActionEvent e) {
                    onInsertArgument ();
                }
            });
        }

        return insertArgumentButton;
    }

    /**
     * Method onInsertArgument
     * 
     * 
     */
    protected void onInsertArgument () {
        Object selectedValue = argumentJList.getSelectedValue();
        
        int caretPosition = templateTextArea.getCaretPosition();
    
        templateTextArea.insert('{' + selectedValue.toString() + '}', caretPosition);
        
        templateTextArea.requestFocus();
    }

    /**
     * Method getTemplateTextArea
     * 
     * @return Returns the JTextArea used to edit the template
     */
    private JTextArea getTemplateTextArea () {
        if (templateTextArea == null) {
            templateTextArea = new JTextArea (makeFormattedTemplate ());
            
            templateTextArea.setLineWrap(true);
            templateTextArea.setWrapStyleWord(true);
        }
        
        return templateTextArea;
    }

    /**
     * Method makeFormattedTemplate
     * 
     * @return Returns the formatted template
     */
    private String makeFormattedTemplate () {
        Pattern pattern = Pattern.compile ("\\{(\\d+)\\}");

        Matcher matcher = pattern.matcher (template);
        
        StringBuilder stringBuilder = new StringBuilder ();

        int index = 0;

        while (matcher.find ()) {
            if (matcher.start () > index) {
                stringBuilder.append(template.substring(index, matcher.start ()));
            }
            
            String argIndexString = matcher.group(1);
            
            int argIndex = Integer.parseInt(argIndexString);
            
            if (argIndex < argumentBindings.size()) {
                stringBuilder.append ('{');
                
                InputBinding binding = argumentBindings.get(argIndex);
                
                stringBuilder.append(binding.getPresentation());
                
                stringBuilder.append ('}');
            }
            
            index = matcher.end();
        }
        
        if (index < template.length()) {
            stringBuilder.append(template.substring(index));
        }
        
        return stringBuilder.toString();
    }

}
