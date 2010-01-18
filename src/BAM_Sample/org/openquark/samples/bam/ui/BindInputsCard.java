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
 * BindInputsCard.java
 * Created: 25-Mar-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam.ui;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.GemEntity;
import org.openquark.samples.bam.Message;
import org.openquark.samples.bam.MetricGemFilter;
import org.openquark.samples.bam.MonitorApp;
import org.openquark.samples.bam.model.ActionDescription;
import org.openquark.samples.bam.model.ConstantBinding;
import org.openquark.samples.bam.model.InputBinding;
import org.openquark.samples.bam.model.MetricBinding;
import org.openquark.samples.bam.model.MetricDescription;
import org.openquark.samples.bam.model.MonitorJobDescription;
import org.openquark.samples.bam.model.PropertyBinding;
import org.openquark.samples.bam.model.TemplateStringBinding;
import org.openquark.samples.bam.model.TriggerDescription;
import org.openquark.samples.bam.model.MessageSourceDescription.MessagePropertyDescription;
import org.openquark.util.ui.WizardCard;


/**
 * BindInputsCard
 *
 * This card displays the input bindings for a gem used in a monitor job 
 */
class BindInputsCard extends WizardCard {
    
    private static final long serialVersionUID = 5340812523046918976L;
    public static final int FOR_TRIGGER = 0;
    public static final int FOR_ACTION  = 1;

    private static class InputListItem {

        private final String name;

        private final TypeExpr typeExpr;

        private InputBinding inputBinding;

        /**
         * Constructor InputListItem
         * 
         * @param name
         * @param typeExpr
         */
        InputListItem (final String name, final TypeExpr typeExpr) {
            this.name = name;
            this.typeExpr = typeExpr;
        }

        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString () {
            if (inputBinding == null) {
                return name + " = ?";
            } else {
                return name + " = " + inputBinding.getPresentation ();
            }
        }

        /**
         * @return Returns the name of the input.
         */
        String getName () {
            return name;
        }
        
        /**
         * @return Returns the InputBinding.
         */
        InputBinding getInputBinding () {
            return inputBinding;
        }

        /**
         * Method setInputBinding
         * 
         * @param newBinding
         */
        void setInputBinding (InputBinding newBinding) {
            inputBinding = newBinding;
        }
    }
    
    private static final class MetricComboItem {
        
        final MetricDescription metricDescription;
        
        MetricComboItem (MetricDescription metricDescription) {
            this.metricDescription = metricDescription;
        }
        
        
        /**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString () {
            return metricDescription.getDescription();
        }
        
        
        /**
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals (Object obj) {
            if (obj instanceof MetricComboItem) {
                return metricDescription.equals(((MetricComboItem)obj).metricDescription);
            }
            
            return false;
        }
        
        
        /**
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode () {
            return metricDescription.hashCode ();
        }
    }

    static final String CARD_NAME = "BindInputs";
    
    private final MonitorJobDescription jobDescription;

    private final ConfigureGemWizardState wizardState;
    
    private final List<InputBinding> initialInputBindings;
    
    private final int usage; // one of the FOR_* constants

    private JList inputList;

    private JRadioButton propertyRadioButton;

    private JRadioButton metricRadioButton;
    
    private JRadioButton constantRadioButton;
    
    private JRadioButton templateRadioButton;
    
    private ButtonGroup bindingButtonGroup;

    private PropertyBindingCombo propertyCombo;
    
    private JComboBox metricCombo;

    private JTextField constantTextField;

    private JButton editTemplateButton;
    
    private boolean ignoreControlNotifications = false;

    /**
     * Constructor BindInputsCard
     * 
     * @param jobDescription
     * @param wizardState
     * @param usage
     */
    public BindInputsCard (MonitorApp app, MonitorJobDescription jobDescription, ConfigureGemWizardState wizardState, int usage) {
        if (usage != FOR_TRIGGER && usage != FOR_ACTION) {
            throw new IllegalArgumentException ("Not a valid value for usage: " + usage);
        }

        this.jobDescription = jobDescription;
        this.wizardState = wizardState;
        initialInputBindings = null;
        this.usage = usage;
    }

    /**
     * Constructor BindInputsCard
     * 
     * @param jobDescription
     * @param gemEntity
     * @param inputBindings
     * @param usage
     */
    public BindInputsCard (MonitorApp app, MonitorJobDescription jobDescription, GemEntity gemEntity, List<InputBinding> inputBindings, int usage) {
        if (usage != FOR_TRIGGER && usage != FOR_ACTION) {
            throw new IllegalArgumentException ("Not a valid value for usage: " + usage);
        }

        this.jobDescription = jobDescription;
        this.wizardState = new ConfigureGemWizardState (gemEntity);
        this.initialInputBindings = inputBindings;
        this.usage = usage;
    }

    /**
     * @see org.openquark.util.ui.WizardCard#getTitle()
     */
    @Override
    protected String getTitle () {
        return "Input Bindings";
    }

    /**
     * @see org.openquark.util.ui.WizardCard#getSubtitle()
     */
    @Override
    protected String getSubtitle () {
        return "Specify bindings for the inputs";
    }

    /**
     * @see org.openquark.util.ui.WizardCard#getCardName()
     */
    @Override
    public String getCardName () {
        return CARD_NAME;
    }

    /**
     * @see org.openquark.util.ui.WizardCard#getNextCardName()
     */
    @Override
    protected String getNextCardName () {
        return null;
    }

    /**
     * @see org.openquark.util.ui.WizardCard#getMainPanel()
     */
    @Override
    protected JComponent getMainPanel () {
        JPanel mainPanel = new JPanel (new GridBagLayout ());

        GridBagConstraints constraints = new GridBagConstraints ();

        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets (2, 2, 2, 2);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.gridheight = 8;
        mainPanel.add (new JScrollPane (getInputList ()), constraints);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.gridheight = 1;

        constraints.gridx = 1;
        constraints.gridy = 0;
        mainPanel.add (getPropertyRadioButton (), constraints);

        constraints.gridx = 1;
        constraints.gridy = 1;
        mainPanel.add (getPropertyCombo (), constraints);

        constraints.gridx = 1;
        constraints.gridy = 2;
        mainPanel.add (getMetricRadioButton (), constraints);

        constraints.gridx = 1;
        constraints.gridy = 3;
        mainPanel.add (getMetricCombo (), constraints);

        constraints.gridx = 1;
        constraints.gridy = 4;
        mainPanel.add (getConstantRadioButton (), constraints);

        constraints.gridx = 1;
        constraints.gridy = 5;
        mainPanel.add (getConstantTextField (), constraints);

        constraints.gridx = 1;
        constraints.gridy = 6;
        mainPanel.add (getTemplateRadioButton (), constraints);

        constraints.gridx = 1;
        constraints.gridy = 7;
        mainPanel.add (getEditTemplateButton (), constraints);

        return mainPanel;
    }

    /**
     * Method getInputList
     * 
     * @return the Jlist that displays the inputs
     */
    private JList getInputList () {
        if (inputList == null) {
            inputList = new JList ();

            inputList.addListSelectionListener (new ListSelectionListener () {

                public void valueChanged (ListSelectionEvent e) {
                    onInputListSelectionChanged ();
                }
            });
        }

        return inputList;
    }
    
    /**
     * Method getPropertyRadioButton
     * 
     * @return the JRadioButton used to select a message property
     */
    private JRadioButton getPropertyRadioButton () {
        if (propertyRadioButton == null) {
            initRadioButtons ();
        }

        return propertyRadioButton;
    }

    /**
     * Method getPropertyCombo
     * 
     * @return the JComboBox that displays message properties
     */
    private PropertyBindingCombo getPropertyCombo () {
        if (propertyCombo == null) {
            propertyCombo = new PropertyBindingCombo ();

            propertyCombo.addItemListener (new ItemListener () {

                public void itemStateChanged (ItemEvent e) {
                    if (!ignoreControlNotifications) {
                        onPropertyComboChanged ();
                    }
                }
            });
        }

        return propertyCombo;
    }

    /**
     * Method onPropertyComboChanged
     * 
     * Handles a change in the current selection of the property combo box 
     */
    protected void onPropertyComboChanged () {
        boolean wasIgnoringNotifications = ignoreControlNotifications;
        
        ignoreControlNotifications = true;
        
        getPropertyRadioButton ().setSelected (true);

        updateBindingOfSelectedInput ();
        
        ignoreControlNotifications = wasIgnoringNotifications;
    }
    
    /**
     * Method getCMetricRadioButton
     * 
     * @return Returns the JRadioButton used to select a metric
     */
    private JRadioButton getMetricRadioButton () {
        if (metricRadioButton == null) {
            initRadioButtons();
        }
        
        return metricRadioButton;
    }
    
    private JComboBox getMetricCombo () {
        if (metricCombo == null) {
            metricCombo = new JComboBox ();

            metricCombo.addItemListener (new ItemListener () {

                public void itemStateChanged (ItemEvent e) {
                    if (!ignoreControlNotifications) {
                        onMetricComboChanged ();
                    }
                }
            });
        }

        return metricCombo;
    }

    /**
     * Method onMetricComboChanged
     * 
     * 
     */
    protected void onMetricComboChanged () {
        boolean wasIgnoringNotifications = ignoreControlNotifications;
        
        ignoreControlNotifications = true;
        
        getMetricRadioButton ().setSelected (true);

        updateBindingOfSelectedInput ();
        
        ignoreControlNotifications = wasIgnoringNotifications;
    }

    /**
     * Method getConstantRadioButton
     * 
     * @return a JRadioButton used to select a constant binding
     */
    private JRadioButton getConstantRadioButton () {
        if (constantRadioButton == null) {
            initRadioButtons ();
        }

        return constantRadioButton;
    }

    /**
     * Method getConstantEdit
     * 
     * @return a JTextField in which the value of the constant binding is displayed & edited
     */
    private JTextField getConstantTextField () {
        if (constantTextField == null) {
            constantTextField = new JTextField ();

            constantTextField.getDocument ().addDocumentListener (new DocumentListener () {

                public void changedUpdate (DocumentEvent e) {
                    if (!ignoreControlNotifications) {
                        onConstantTextFieldChanged ();
                    }
                }

                public void insertUpdate (DocumentEvent e) {
                    if (!ignoreControlNotifications) {
                        onConstantTextFieldChanged ();
                    }
                }

                public void removeUpdate (DocumentEvent e) {
                    if (!ignoreControlNotifications) {
                        onConstantTextFieldChanged ();
                    }
                }
            });
        }

        return constantTextField;
    }

    /**
     * Method onConstantTextFieldChanged
     * 
     * Handles a change in the contents of the constant text field 
     */
    protected void onConstantTextFieldChanged () {
        boolean wasIgnoringNotifications = ignoreControlNotifications;
        
        ignoreControlNotifications = true;
        
        getConstantRadioButton ().setSelected (true);

        updateBindingOfSelectedInput ();
        
        ignoreControlNotifications = wasIgnoringNotifications;
    }
    
    private JRadioButton getTemplateRadioButton () {
        if (templateRadioButton == null) {
            initRadioButtons();
        }
        
        return templateRadioButton;
    }

    /**
     * Method getEditTemplateButton
     * 
     * @return Returns the JButton that launches the template editor
     */
    private JButton getEditTemplateButton () {
        if (editTemplateButton == null) {
            editTemplateButton = new JButton (new AbstractAction ("Edit Template") {

                private static final long serialVersionUID = -1241474925155795156L;

                public void actionPerformed (ActionEvent e) {
                    onEditTemplate ();
                }
            });
        }

        return editTemplateButton;
    }


    /**
     * Method onEditTemplate
     * 
     * 
     */
    protected void onEditTemplate () {
        // Ensure that the template radio button is selected
        if (!getTemplateRadioButton ().isSelected ()) {
            boolean wasIgnoringNotifications = ignoreControlNotifications;

            ignoreControlNotifications = true;

            getTemplateRadioButton ().setSelected (true);

            updateBindingOfSelectedInput ();

            ignoreControlNotifications = wasIgnoringNotifications;
        }

        InputListItem item = getSelectedInputListItem ();

        if (item.inputBinding == null || item.inputBinding instanceof TemplateStringBinding) {
            Frame frame = JOptionPane.getFrameForComponent (this);

            TemplateStringDialog dialog = new TemplateStringDialog (frame, (TemplateStringBinding)item.inputBinding,
                    jobDescription.getMessagePropertyDescriptions ());

            if (dialog.doModal ()) {
                item.setInputBinding (new TemplateStringBinding (dialog.getTemplate (), dialog.getArgumentBindings ()));

                refreshInputList ();

                cardStateChanged ();
            }
        }
    }

    /**
     * Method initRadioButtons
     * 
     * Creates the radio buttons and puts them into a group
     */
    private void initRadioButtons () {
        propertyRadioButton = new JRadioButton ("Message property");

        propertyRadioButton.addActionListener (new ActionListener () {

            public void actionPerformed (ActionEvent e) {
                if (!ignoreControlNotifications) {
                    onPropertyRadioButtonAction ();
                }
            }
        });
        
        metricRadioButton = new JRadioButton ("Metric");
        
        metricRadioButton.addActionListener(new ActionListener () {

            public void actionPerformed (ActionEvent e) {
                if (!ignoreControlNotifications) {
                    onMetricRadioButtonAction ();
                }
            }
        });

        constantRadioButton = new JRadioButton ("Constant value");

        constantRadioButton.addActionListener (new ActionListener () {

            public void actionPerformed (ActionEvent e) {
                if (!ignoreControlNotifications) {
                    onConstantRadioButtonAction ();
                }
            }
        });
        
        templateRadioButton = new JRadioButton ("Template string");
        
        templateRadioButton.addActionListener(new ActionListener () {

            public void actionPerformed (ActionEvent e) {
                if (!ignoreControlNotifications) {
                    onTemplateRadioButtonAction ();
                }
            }
        });

        bindingButtonGroup = new ButtonGroup ();

        bindingButtonGroup.add (propertyRadioButton);
        bindingButtonGroup.add (metricRadioButton);
        bindingButtonGroup.add (constantRadioButton);
        bindingButtonGroup.add (templateRadioButton);
    }

    /**
     * Method onPropertyRadioButtonAction
     * 
     * Handles selecting the property radio button 
     */
    protected void onPropertyRadioButtonAction () {
        updateBindingOfSelectedInput ();
    }
    
    /**
     * Method onMetricRadioButtonAction
     * 
     * Handles selecting the metric radio button
     */
    protected void onMetricRadioButtonAction () {
        updateBindingOfSelectedInput ();
    }

    /**
     * Method onConstantRadioButtonAction
     * 
     *  Handles selecting the constant radio button 
     */
    protected void onConstantRadioButtonAction () {
        updateBindingOfSelectedInput ();
    }

    
    /**
     * Method onTemplateRadioButtonAction
     * 
     * 
     */
    protected void onTemplateRadioButtonAction () {
        updateBindingOfSelectedInput();
    }

    /**
     * Method updateBindingOfSelectedInput
     * 
     * Creates a binding based on the current settings of the controls
     * and passes it to the selected input 
     */
    private void updateBindingOfSelectedInput () {
        InputListItem inputListItem = getSelectedInputListItem ();

        if (inputListItem != null) {
            InputBinding newBinding = makeBinding (typeExprToDataType (inputListItem.typeExpr));

            // OK if it's null

            inputListItem.setInputBinding (newBinding);

            refreshInputList ();
            
            cardStateChanged();
        }
    }

    /**
     * Method makeBinding
     * 
     * @return an InputBinding created from the current settings of the controls 
     */
    private InputBinding makeBinding (int dataType) {
        InputBinding result = null;

        if (getPropertyRadioButton ().isSelected ()) {
            MessagePropertyDescription propertyInfo = getPropertyCombo ().getSelectedInfo();

            if (propertyInfo != null) {
                result = new PropertyBinding (propertyInfo);
            }
        } else if (metricRadioButton.isSelected()) {
            MetricComboItem item = (MetricComboItem)getMetricCombo().getSelectedItem();
            
            if (item != null) {
                result = new MetricBinding (item.metricDescription);
            }
        } else if (getConstantRadioButton ().isSelected ()) {
            String text = constantTextField.getText ();

            Object value = null;

            try {
                switch (dataType) {
                    case Message.DOUBLE:
                        value = Double.valueOf (text);
                        break;

                    case Message.INT:
                        value = Integer.valueOf (text);
                        break;

                    case Message.LONG:
                        value = Long.valueOf (text);
                        break;

                    case Message.STRING:
                        value = text;
                        break;

                    default:
                        throw new IllegalArgumentException ("Unknown data type: " + dataType);
                }
            } catch (Exception e) {
                System.out.println ("Exception while trying to parse the constant value: " + e);
            }

            if (value != null) {
                result = new ConstantBinding (value);
            }
        }

        return result;
    }

    /**
     * Method refreshInputList
     * 
     * Force a redraw of the input list 
     */
    private void refreshInputList () {
        inputList.repaint();
    }


    /**
     * Method onInputListSelectionChanged
     * 
     * Handles a change in the current selection of the input list
     */
    protected void onInputListSelectionChanged () {
        boolean oldIgnoreControlNotifications = ignoreControlNotifications;
        
        ignoreControlNotifications = true;
        
        updatePropertyControls ();
        updateMetricControls ();
        updateConstantControls ();
        updateTemplateControls ();
        
        ignoreControlNotifications = oldIgnoreControlNotifications;
    }

    /**
     * Method updatePropertyControls
     * 
     * 
     */
    private void updatePropertyControls () {
        fillPropertyCombo();
        
        boolean propertiesAvailable = propertyCombo.getItemCount() != 0;
        
        propertyRadioButton.setEnabled(propertiesAvailable);
        propertyCombo.setEnabled(propertiesAvailable);
        
        PropertyBinding propertyBinding = null;
        
        InputListItem item = getSelectedInputListItem ();
        
        if (item != null) {
            InputBinding binding = item.getInputBinding();
            
            if (binding instanceof PropertyBinding) {
                propertyBinding = (PropertyBinding)binding;
            }
        }
        
        if (propertyBinding != null) {
            propertyCombo.setSelectedInfo(propertyBinding.getPropertyInfo());
            
            propertyRadioButton.setSelected (true);
        } else {
            propertyCombo.setSelectedIndex(-1);
            
            selectBindingRadioButton (propertyRadioButton, false);
        }
            
    }

    /**
     * Method updateMetricControls
     * 
     * 
     */
    private void updateMetricControls () {
        fillMetricCombo();
        
        boolean metricAvailable = metricCombo.getItemCount() != 0;
        
        metricRadioButton.setEnabled(metricAvailable);
        metricCombo.setEnabled(metricAvailable);
        
        MetricBinding metricBinding = null;
        
        InputListItem item = getSelectedInputListItem ();
        
        if (item != null) {
            InputBinding binding = item.getInputBinding();
            
            if (binding instanceof MetricBinding) {
                metricBinding = (MetricBinding)binding;
            }
        }
        
        if (metricBinding != null) {
            metricCombo.setSelectedItem(new MetricComboItem (metricBinding.getMetricDescription()));
            
            metricRadioButton.setSelected(true);
        } else {
            metricCombo.setSelectedIndex(-1);
            
            selectBindingRadioButton (metricRadioButton, false);
        }
    }

    /**
     * Method updateConstantControls
     * 
     * 
     */
    private void updateConstantControls () {
        ConstantBinding constantBinding = null;
        
        InputListItem item = getSelectedInputListItem ();
        
        if (item != null) {
            InputBinding binding = item.getInputBinding();
            
            if (binding instanceof ConstantBinding) {
                constantBinding = (ConstantBinding)binding;
            }
        }
     
        if (constantBinding != null) {
            constantTextField.setText (constantBinding.getValue().toString());
            
            constantRadioButton.setSelected(true);
        } else {
            constantTextField.setText ("");
            
            selectBindingRadioButton (constantRadioButton, false);
        }
    }

    /**
     * Method updateTemplateControls
     * 
     * 
     */
    private void updateTemplateControls () {
        InputListItem item = getSelectedInputListItem ();
        
        boolean enabled = false;

        TemplateStringBinding templateStringBinding = null;

        if (item != null) {
            enabled = item.typeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.String);
            
            InputBinding binding = item.getInputBinding();
            
            if (binding instanceof TemplateStringBinding) {
                templateStringBinding = (TemplateStringBinding)binding;
            }
        }
     
        templateRadioButton.setEnabled(enabled);
        editTemplateButton.setEnabled(enabled);
        
        selectBindingRadioButton (templateRadioButton, templateStringBinding != null);
    }
    
    private void selectBindingRadioButton (JRadioButton button, boolean selected) {
        if (selected == button.isSelected()) {
            return;
        }
            
        if (selected) {
            button.setSelected(selected);
        } else {
            bindingButtonGroup.remove(button);
            
            button.setSelected(false);
            
            bindingButtonGroup.add(button);
        }
    }

    /**
     * Method getSelectedInputListItem
     * 
     * @return the InputListItem that is currently selected in the input list
     */
    private InputListItem getSelectedInputListItem () {
        Object selectedValue = inputList.getSelectedValue ();

        if (selectedValue instanceof InputListItem) {
            return (InputListItem)selectedValue;
        } else {
            return null;
        }
    }

    /**
     * Method fillPropertyCombo
     * 
     * Initialises the property combo, based on the available message properties
     * and the data type of the selected input 
     */
    private void fillPropertyCombo () {
        InputListItem item = getSelectedInputListItem ();

        propertyCombo.removeAllItems ();

        if (item != null) {
            TypeExpr inputTypeExpr = item.typeExpr;

            int inputDataType = typeExprToDataType (inputTypeExpr);

            propertyCombo.initialise(jobDescription.getMessagePropertyDescriptions (), inputDataType);
        }
    }

    /**
     * Method typeExprToDataType
     * 
     * @param inputTypeExpr
     * @return the int value that corresponds to the given TypeExpr (see Message)
     */
    private int typeExprToDataType (TypeExpr inputTypeExpr) {
        if (inputTypeExpr.isNonParametricType (CAL_Prelude.TypeConstructors.Double)) {
            return Message.DOUBLE;
        } else if (inputTypeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Int)) {
            return Message.INT;
        } else if (inputTypeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.String)) {
            return Message.STRING;
        } else if (inputTypeExpr.isNonParametricType(CAL_Prelude.TypeConstructors.Long)) {
            return Message.LONG;
        } else {
            // TODO rbc: handle time
            throw new IllegalArgumentException ("This type is not handled: " + inputTypeExpr);
        }
    }
    
    /**
     * Method fillMetricCombo
     * 
     * 
     */
    private void fillMetricCombo () {
        InputListItem inputItem = getSelectedInputListItem (); 

        metricCombo.removeAllItems ();

        if (inputItem != null) {           
            BasicCALServices calServices = MonitorApp.getInstance().getCalServices();
 
            //for all message properties consider which metrics are suitable
            Collection<MessagePropertyDescription> msgProperties = jobDescription.getMessagePropertyDescriptions(); 
            for (final MessagePropertyDescription propertyInfo : msgProperties) {
                TypeExpr propertyType = propertyInfo.getCalType(calServices);
                               
                Set<GemEntity> gemEntities = new MetricGemFilter(propertyType, inputItem.typeExpr).getMatchingGems();
                for (final GemEntity gemEntity : gemEntities) {
                    metricCombo.addItem(new MetricComboItem (new MetricDescription(gemEntity.getName(), propertyInfo )));
                                            
                }
            }
        }
    }

    /**
     * @see org.openquark.util.ui.WizardCard#initControls()
     */
    @Override
    protected boolean initControls () {
        GemEntity gemEntity = wizardState.getGemEntity();
        
        if (gemEntity != null) {
            return fillInputList (gemEntity);
        }

        return false;
    }

    /**
     * Method fillInputList
     * 
     * @param gemEntity
     * @return true iff the input list is successfully filled, based on the given gem
     */
    private boolean fillInputList (GemEntity gemEntity) {
        DefaultListModel listModel = new DefaultListModel ();
        
        TypeExpr[] argTypes = gemEntity.getTypeExpr ().getTypePieces ();

        for (int argN = 0; argN < gemEntity.getNNamedArguments (); ++argN) {
            String argName = gemEntity.getNamedArgument (argN);

            TypeExpr argType = argTypes[argN];

            if (canBindType (argType)) {
                InputListItem item = new InputListItem (argName, argType);
                
                if (initialInputBindings != null && argN < initialInputBindings.size()) {
                    item.setInputBinding(initialInputBindings.get(argN));
                }
                
                listModel.addElement (item);
            }
        }

        if (listModel.getSize() > 0) {
            getInputList ().setModel (listModel);
    
            getInputList ().setSelectedIndex (0);
    
            return true;
        } else {
            JOptionPane.showMessageDialog(this, "None of the inputs gem " + gemEntity.getName().getQualifiedName() + " are suitable for binding.", "BAM Sample", JOptionPane.WARNING_MESSAGE);
            
            return false;
        }
    }

 
    /**
     * Method canBindType
     * 
     * @param argType
     * @return Returns true iff the UI can bind this type
     */
    private boolean canBindType (TypeExpr argType) {
        return argType.isNonParametricType (CAL_Prelude.TypeConstructors.Double)
            || argType.isNonParametricType (CAL_Prelude.TypeConstructors.Int)
            || argType.isNonParametricType (CAL_Prelude.TypeConstructors.String)
            || argType.isNonParametricType (CAL_Prelude.TypeConstructors.Long);
    }

    /**
     * @see org.openquark.util.ui.WizardCard#commitChanges()
     */
    @Override
    protected boolean commitChanges () {
        return true;
    }
    
    
    /**
     * @see org.openquark.util.ui.WizardCard#canFinish()
     */
    @Override
    protected boolean canFinish () {
        return getFirstUnboundListItem() == null;
    }

    /**
     * @see org.openquark.util.ui.WizardCard#onFinish()
     */
    @Override
    protected boolean onFinish () {
        GemEntity gemEntity = wizardState.getGemEntity();
        
        if (gemEntity == null) {
            return false;
        }
        
        List<InputBinding> inputBindings = null;
        
        try {
            inputBindings = getInputBindings();
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "BAM Sample", JOptionPane.WARNING_MESSAGE);
            
            return false;
        }
        
        if (inputBindings == null) {
            InputListItem listItem = getFirstUnboundListItem();
            
            JOptionPane.showMessageDialog(this, "You must provide a binding for '" + listItem.name + "'", "BAM Sample", JOptionPane.WARNING_MESSAGE);
            
            return false;
        }
        
        switch (usage) {
            case FOR_TRIGGER:
                TriggerDescription triggerDescription = new TriggerDescription (gemEntity.getName().getQualifiedName(), inputBindings);
                
                jobDescription.addTrigger(triggerDescription);
                break;
                
            case FOR_ACTION:
                ActionDescription actionDescription = new ActionDescription (gemEntity.getName().getQualifiedName(), inputBindings);
                
                jobDescription.addAction(actionDescription);
                break;
                
            default:
                throw new IllegalStateException ("The usage field is invalid");
        }
        
        return true;
    }

    /**
     * @see org.openquark.util.ui.WizardCard#getTipInfo()
     */
    @Override
    protected TipInfo getTipInfo () {
        // check whether all inputs have bindings
        InputListItem listItem = getFirstUnboundListItem();
        
        if (listItem != null) {
            return new TipInfo (WARNING_TIP, "Bind the input '" + listItem.name + "' to a message property or a constant");
        }
        
        return new TipInfo (ALLOK_TIP, "The " + (usage == FOR_TRIGGER ? "trigger" : "action") + " is now complete.");
    }

    private InputListItem getFirstUnboundListItem () {
        // check whether all inputs have bindings
        ListModel inputListModel =  getInputList().getModel();
        
        for (int i = 0; i < inputListModel.getSize(); ++i) {
            Object element = inputListModel.getElementAt(i);
            
            if (element instanceof InputListItem) {
                InputListItem listItem = (InputListItem)element;
                
                InputBinding inputBinding = listItem.getInputBinding();
              
                if (inputBinding == null) {
                    return listItem;
                }
            } 
        }
        
        return null;
    }

    /**
     * Method getInputBindings
     * 
     * 
     */
    public List<InputBinding> getInputBindings () {
        List<InputBinding> inputBindings = new ArrayList<InputBinding> ();
        
        ListModel inputListModel =  getInputList().getModel();
        
        GemEntity gemEntity = wizardState.getGemEntity();

        TypeExpr[] argTypes = gemEntity.getTypeExpr ().getTypePieces ();
        
        for (int argN = 0; argN < gemEntity.getNNamedArguments (); ++argN) {
            TypeExpr argType = argTypes [argN];
            
            String argName = gemEntity.getNamedArgument (argN);
            
            InputBinding inputBinding = null;
            
            if (canBindType(argType)) {
                inputBinding = findInputBinding (argName, inputListModel); 
            } 
            
            if (inputBinding == null) {
                throw new IllegalStateException ("Cannot bind argument '" + argName + "'");
            }
            
            inputBindings.add (inputBinding);
        }
        
        return inputBindings;
    }

    /**
     * Method findInputBinding
     * 
     * @param argName
     * @param inputListModel
     * @return Returns the InputBinding for the given argument, if there is one
     */
    private InputBinding findInputBinding (String argName, ListModel inputListModel) {
        for (int i = 0; i < inputListModel.getSize(); ++i) {
            Object element = inputListModel.getElementAt(i);
            
            if (element instanceof InputListItem) {
                InputListItem listItem = (InputListItem)element;

                if (listItem.getName().equals(argName)) {
                    return listItem.getInputBinding();
                }
            } 
        }
        
        return null;
    }
}