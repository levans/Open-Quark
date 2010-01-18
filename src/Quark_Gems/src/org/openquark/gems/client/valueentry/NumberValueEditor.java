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
 * NumberValueEditor.java
 * Creation date: (1/12/2001 11:38:20 PM)
 * By: Luke Evans
 */
package org.openquark.gems.client.valueentry;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.openquark.cal.compiler.PreludeTypeConstants;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.module.Cal.Core.CAL_Prelude;
import org.openquark.cal.valuenode.LiteralValueNode;
import org.openquark.cal.valuenode.ValueNode;


/**
 * Allows the user to manipulate a number value thru a calculator.
 * @author Luke Evans
 */
class NumberValueEditor extends ValueEditor {

    private static final long serialVersionUID = -789074961805648387L;

    /**
     * A custom value editor provider for the NumberValueEditor.
     */
    public static class NumberValueEditorProvider extends ValueEditorProvider<NumberValueEditor> {
        
        public NumberValueEditorProvider(ValueEditorManager valueEditorManager) {
            super(valueEditorManager);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canHandleValue(ValueNode valueNode, SupportInfo providerSupportInfo) {
            TypeExpr typeExpr = valueNode.getTypeExpr();
            
            PreludeTypeConstants typeConstants = getValueEditorManager().getValueNodeBuilderHelper().getPreludeTypeConstants();
            
            // Integer and Decimal are not built-in types, so we cannot compare them to static
            // constants the way we can for the other types.
            return valueNode instanceof LiteralValueNode && 
                        (typeExpr.sameType(typeConstants.getByteType()) ||
                         typeExpr.sameType(typeConstants.getShortType()) ||
                         typeExpr.sameType(typeConstants.getIntType()) ||
                         typeExpr.sameType(typeConstants.getIntegerType()) ||
                         typeExpr.sameType(typeConstants.getDecimalType()) ||
                         typeExpr.sameType(typeConstants.getLongType()) ||
                         typeExpr.sameType(typeConstants.getFloatType()) ||
                         typeExpr.sameType(typeConstants.getDoubleType()));
        }
        
        /**
         * @see org.openquark.gems.client.valueentry.ValueEditorProvider#getEditorInstance(ValueEditorHierarchyManager, ValueNode)
         */
        @Override
        public NumberValueEditor getEditorInstance(ValueEditorHierarchyManager valueEditorHierarchyManager,
                                             ValueNode valueNode) {
            
            NumberValueEditor editor = new NumberValueEditor(valueEditorHierarchyManager);
            editor.setOwnerValueNode(valueNode);
            return editor;
        }
    }

    private static final String UNDEFINED_STRING = "Undefined.";

    private JButton clearButton = null;
    private JButton decimalButton = null;
    private JButton divideButton = null;
    private JButton minusButton = null;
    private JButton multiplyButton = null;
    private JButton number0Button = null;
    private JButton number1Button = null;
    private JButton number2Button = null;
    private JButton number3Button = null;
    private JButton number4Button = null;
    private JButton number5Button = null;
    private JButton number6Button = null;
    private JButton number7Button = null;
    private JButton number8Button = null;
    private JButton number9Button = null;
    private JButton plusButton = null;
    private JButton signButton = null;
    private JButton sqrtButton = null;
    private JButton equalButton = null;
    private JButton oneOverXButton = null;
    private JPanel buttonPanel = null;
    private JTextField displayField = null;

    private Stack <String> operatorStack = null;
    private Stack <String> operandStack = null;

    /**
     * The precedence of the divide operator.
     * Note: A higher number means a higher precedence and should be evaluated before lower precedence operators.
     */
    private static final int PRECEDENCE_DIVIDE = 2;

    /**
     * The precedence of the multiply operator.
     */
    private static final int PRECEDENCE_MULTIPLY = 2;

    /**
     * The precedence of the minus operator.
     */
    private static final int PRECEDENCE_MINUS = 1;

    /**
     * The precedence of the plus operator.
     */
    private static final int PRECEDENCE_PLUS = 1;

    /**
     * A flag to denote whether or not the next key press should be an operand key press, 
     *   or if it could be either operand or operator key press.
     * True if it should be an operand key press, 
     * False if it could be either.
     * Note: If operandNext is true, and user presses an operator, then the previously pressed 
     *   operator will be replaced by the currently pressed operator.
     */
    private boolean operandNext;

    /**
     * A flag to denote that if the next key press is an 'operand' key ("#0-#9" or "."), 
     * then replace the value in the parent textfield with the operand key pressed.
     */
    private boolean replaceOnOperandKeyPress;

    /** 
     * Flag to signal whether or not we are in the handling undefined results stage.  
     * Set to true the moment we enter handling mode.
     * Set to false when we exit the handling mode (user presses "C", or this NumberValueEditor is closed).
     */
    private boolean isHandlingResultUndefined;

    /**
     * The event handler for button presses.
     */
    private class ButtonEventHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == NumberValueEditor.this.getNumber0Button()) {
                appendToDisplay("0");
            }
            if (e.getSource() == NumberValueEditor.this.getNumber1Button()) {
                appendToDisplay("1");
            }
            if (e.getSource() == NumberValueEditor.this.getNumber2Button()) {
                appendToDisplay("2");
            }
            if (e.getSource() == NumberValueEditor.this.getNumber3Button()) {
                appendToDisplay("3");
            }
            if (e.getSource() == NumberValueEditor.this.getNumber4Button()) {
                appendToDisplay("4");
            }
            if (e.getSource() == NumberValueEditor.this.getNumber5Button()) {
                appendToDisplay("5");
            }
            if (e.getSource() == NumberValueEditor.this.getNumber6Button()) {
                appendToDisplay("6");
            }
            if (e.getSource() == NumberValueEditor.this.getNumber7Button()) {
                appendToDisplay("7");
            }
            if (e.getSource() == NumberValueEditor.this.getNumber8Button()) {
                appendToDisplay("8");
            }
            if (e.getSource() == NumberValueEditor.this.getNumber9Button()) {
                appendToDisplay("9");
            }
            if (e.getSource() == NumberValueEditor.this.getSignButton()) {
                sign_ActionEvents();
            }
            if (e.getSource() == NumberValueEditor.this.getDecimalButton()) {
                decimal_ActionEvents();
            }
            if (e.getSource() == NumberValueEditor.this.getDivideButton()) {
                handleOpKeyPress("/");
            }
            if (e.getSource() == NumberValueEditor.this.getMultiplyButton()) {
                handleOpKeyPress("*");
            }
            if (e.getSource() == NumberValueEditor.this.getMinusButton()) {
                handleOpKeyPress("-");
            }
            if (e.getSource() == NumberValueEditor.this.getPlusButton()) {
                handleOpKeyPress("+");
            }
            if (e.getSource() == NumberValueEditor.this.getEqualButton()) {
                equal_ActionEvents();
            }
            if (e.getSource() == NumberValueEditor.this.getSqrtButton()) {
                sqrt_ActionEvents();
            }
            if (e.getSource() == NumberValueEditor.this.getClearButton()) {
                clear_ActionEvents();
            }
            if (e.getSource() == NumberValueEditor.this.getOneOverXButton()) {
                oneOverX_ActionEvents();
            }
        }
    }

    /**
     * KeyListener used to listen for user's input.
     * Currently, the following KeyEvents are supported:
     * "0" -> "9", "/", "*", "-", "+", ".", "delete"(for clear), "enter"(for equal), and 
     * "esc"(for closing NumberValueEditor and going back to the parent).
     */
    private class CalculatorKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent evt) {
            if ((evt.getKeyCode() == KeyEvent.VK_1 && !evt.isShiftDown()) || evt.getKeyCode() == KeyEvent.VK_NUMPAD1) {
                handleButtonKeyPress(getNumber1Button());

            } else if ((evt.getKeyCode() == KeyEvent.VK_2 && !evt.isShiftDown()) || evt.getKeyCode() == KeyEvent.VK_NUMPAD2) {
                handleButtonKeyPress(getNumber2Button());

            } else if ((evt.getKeyCode() == KeyEvent.VK_3 && !evt.isShiftDown()) || evt.getKeyCode() == KeyEvent.VK_NUMPAD3) {
                handleButtonKeyPress(getNumber3Button());

            } else if ((evt.getKeyCode() == KeyEvent.VK_4 && !evt.isShiftDown()) || evt.getKeyCode() == KeyEvent.VK_NUMPAD4) {
                handleButtonKeyPress(getNumber4Button());

            } else if ((evt.getKeyCode() == KeyEvent.VK_5 && !evt.isShiftDown()) || evt.getKeyCode() == KeyEvent.VK_NUMPAD5) {
                handleButtonKeyPress(getNumber5Button());

            } else if ((evt.getKeyCode() == KeyEvent.VK_6 && !evt.isShiftDown()) || evt.getKeyCode() == KeyEvent.VK_NUMPAD6) {
                handleButtonKeyPress(getNumber6Button());

            } else if ((evt.getKeyCode() == KeyEvent.VK_7 && !evt.isShiftDown()) || evt.getKeyCode() == KeyEvent.VK_NUMPAD7) {
                handleButtonKeyPress(getNumber7Button());

            } else if ((evt.getKeyCode() == KeyEvent.VK_8 && !evt.isShiftDown()) || evt.getKeyCode() == KeyEvent.VK_NUMPAD8) {
                handleButtonKeyPress(getNumber8Button());

            } else if ((evt.getKeyCode() == KeyEvent.VK_9 && !evt.isShiftDown()) || evt.getKeyCode() == KeyEvent.VK_NUMPAD9) {
                handleButtonKeyPress(getNumber9Button());

            } else if ((evt.getKeyCode() == KeyEvent.VK_0 && !evt.isShiftDown()) || evt.getKeyCode() == KeyEvent.VK_NUMPAD0) {
                handleButtonKeyPress(getNumber0Button());

            } else if (evt.getKeyCode() == KeyEvent.VK_DECIMAL || (evt.getKeyCode() == KeyEvent.VK_PERIOD && !evt.isShiftDown())) {
                handleButtonKeyPress(getDecimalButton());

            } else if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                handleCommitGesture();
                evt.consume(); // Don't want the button with the focus to perform its action.

            } else if (evt.getKeyCode() == KeyEvent.VK_ADD || evt.getKeyCode() == KeyEvent.VK_EQUALS && evt.isShiftDown()) {
                handleButtonKeyPress(getPlusButton());

            } else if (evt.getKeyCode() == KeyEvent.VK_SUBTRACT || (evt.getKeyCode() == KeyEvent.VK_MINUS && !evt.isShiftDown())) {
                handleButtonKeyPress(getMinusButton());

            } else if ((evt.getKeyCode() == KeyEvent.VK_MULTIPLY) || (evt.getKeyCode() == KeyEvent.VK_8 && evt.isShiftDown())) {
                handleButtonKeyPress(getMultiplyButton());

            } else if (evt.getKeyCode() == KeyEvent.VK_DIVIDE || (evt.getKeyCode() == KeyEvent.VK_SLASH && !evt.isShiftDown())) {
                handleButtonKeyPress(getDivideButton());

            } else if ((evt.getKeyCode() == KeyEvent.VK_EQUALS && !evt.isShiftDown())) {
                handleButtonKeyPress(getEqualButton());

            } else if (evt.getKeyCode() == KeyEvent.VK_DELETE && !evt.isAltDown()) {
                handleButtonKeyPress(getClearButton());

            } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                handleCancelGesture();
                evt.consume();
            }
        }
        
        /**
         * Handle a key press as if a button were clicked.
         * @param button the button corresponding to the key which was typed.
         */
        private void handleButtonKeyPress(JButton button) {
            if (button.isEnabled()) {
                button.doClick();
                button.requestFocus();
            }
        }
    }

    /**
     * Creates a new NumberValueEditor.
     * @param valueEditorHierarchyManager
     */
    protected NumberValueEditor(ValueEditorHierarchyManager valueEditorHierarchyManager) {
        super(valueEditorHierarchyManager);
        initialize();
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        setName("NumberValueEditor");
        setSize(150, 100);

        // The panel containing all the buttons
        buttonPanel = new JPanel();

        GridLayout buttonPanelGridLayout = new GridLayout();
        buttonPanelGridLayout.setRows(4);
        buttonPanel.setLayout(buttonPanelGridLayout);

        buttonPanel.add(getNumber7Button());
        buttonPanel.add(getNumber8Button());
        buttonPanel.add(getNumber9Button());
        buttonPanel.add(getDivideButton());
        buttonPanel.add(getClearButton());

        buttonPanel.add(getNumber4Button());
        buttonPanel.add(getNumber5Button());
        buttonPanel.add(getNumber6Button());
        buttonPanel.add(getMultiplyButton());
        buttonPanel.add(getSqrtButton());

        buttonPanel.add(getNumber1Button());
        buttonPanel.add(getNumber2Button());
        buttonPanel.add(getNumber3Button());
        buttonPanel.add(getMinusButton());
        buttonPanel.add(getOneOverXButton());

        buttonPanel.add(getNumber0Button());
        buttonPanel.add(getSignButton());
        buttonPanel.add(getDecimalButton());
        buttonPanel.add(getPlusButton());
        buttonPanel.add(getEqualButton());
        
        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.CENTER);
        add(getDisplayField(), BorderLayout.NORTH);

        initConnections();

        // Give this ValueEditor its preferred size.
        this.setSize(this.getPreferredSize());

        // Set the flags.
        operandNext = false;
        replaceOnOperandKeyPress = true;
        isHandlingResultUndefined = false;

        // Init the stacks.
        operatorStack = new Stack<String>();
        operandStack = new Stack<String>();
        
        FocusTraversalPolicy focusTraversalPolicy = new FocusTraversalPolicy() {
            @Override
            public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
                Component[] components = getComponents();
                int index = Arrays.asList(components).indexOf(aComponent);
                return components[(index + 1) % components.length];
            }
    
            @Override
            public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
                Component[] components = getComponents();
                int index = Arrays.asList(components).indexOf(aComponent);
                return components[(index - 1) % components.length];
            }
    
            @Override
            public Component getDefaultComponent(Container focusCycleRoot) {
                return getComponent(0);
            }
    
            @Override
            public Component getFirstComponent(Container focusCycleRoot) {
                return getComponent(0);
            }
    
            @Override
            public Component getLastComponent(Container focusCycleRoot) {
                return getComponent(getComponents().length - 1);
            }
        };

        setFocusTraversalPolicy(focusTraversalPolicy);
        getDisplayField().setFocusTraversalPolicy(focusTraversalPolicy);

        // Ensure that keypad presses are correctly handled.
        CalculatorKeyListener calcKeyListener = new CalculatorKeyListener();
        getDisplayField().addKeyListener(calcKeyListener);
        addKeyListener(calcKeyListener);

        Component[] componentList = this.getComponents();
        for (final Component component : componentList) {
            component.addKeyListener(calcKeyListener);
        }

        Component[] buttonPanelComponentList = buttonPanel.getComponents();
        for (final Component buttonPanelComponent : buttonPanelComponentList) {
            buttonPanelComponent.addKeyListener(calcKeyListener);
        }
    }
    
    /**
     * Initializes connections
     */
    private void initConnections() {
        ButtonEventHandler buttonEventHandler = new ButtonEventHandler();

        getNumber0Button().addActionListener(buttonEventHandler);
        getNumber1Button().addActionListener(buttonEventHandler);
        getNumber2Button().addActionListener(buttonEventHandler);
        getNumber3Button().addActionListener(buttonEventHandler);
        getNumber4Button().addActionListener(buttonEventHandler);
        getNumber5Button().addActionListener(buttonEventHandler);
        getNumber6Button().addActionListener(buttonEventHandler);
        getNumber7Button().addActionListener(buttonEventHandler);
        getNumber8Button().addActionListener(buttonEventHandler);
        getNumber9Button().addActionListener(buttonEventHandler);
        getSignButton().addActionListener(buttonEventHandler);
        getDecimalButton().addActionListener(buttonEventHandler);
        getDivideButton().addActionListener(buttonEventHandler);
        getMultiplyButton().addActionListener(buttonEventHandler);
        getMinusButton().addActionListener(buttonEventHandler);
        getPlusButton().addActionListener(buttonEventHandler);
        getEqualButton().addActionListener(buttonEventHandler);
        getSqrtButton().addActionListener(buttonEventHandler);
        getClearButton().addActionListener(buttonEventHandler);
        getOneOverXButton().addActionListener(buttonEventHandler);
    }

    /**
     * Make a button configured for use in the calculator.
     * @param buttonText the text displayed by the button.
     * @return a new button
     */
    private JButton makeCalculatorButton(String buttonText) {
        JButton button = new JButton();
        button.setText(buttonText);
        button.setVerticalAlignment(SwingConstants.CENTER);
        button.setMargin(new Insets(0, 0, 0, 0));
        return button;
    }

    /**
     * Return the Clear Button
     * @return JButton
     */
    private JButton getClearButton() {
        if (clearButton == null) {
            clearButton = makeCalculatorButton("C");
        }
        return clearButton;
    }
    
    /**
     * Return the Decimal Button
     * @return JButton
     */
    private JButton getDecimalButton() {
        if (decimalButton == null) {
            decimalButton = makeCalculatorButton(".");
        }
        return decimalButton;
    }
    
    /**
     * Return the Divide Button
     * @return JButton
     */
    private JButton getDivideButton() {
        if (divideButton == null) {
            divideButton = makeCalculatorButton("/");
        }
        return divideButton;
    }
    
    /**
     * Return the Equal Button
     * @return JButton
     */
    private JButton getEqualButton() {
        if (equalButton == null) {
            equalButton = makeCalculatorButton("=");
            equalButton.setFont(new Font("sansserif", Font.BOLD, 12));
        }
        return equalButton;
    }
    
    /**
     * Return the Minus Button
     * @return JButton
     */
    private JButton getMinusButton() {
        if (minusButton == null) {
            minusButton = makeCalculatorButton("-");
        }
        return minusButton;
    }
    
    /**
     * Return the Multiply Button
     * @return JButton
     */
    private JButton getMultiplyButton() {
        if (multiplyButton == null) {
            multiplyButton = makeCalculatorButton("*");
        }
        return multiplyButton;
    }
    
    /**
     * Return the 0 button
     * @return JButton
     */
    private JButton getNumber0Button() {
        if (number0Button == null) {
            number0Button = makeCalculatorButton("0");
        }
        return number0Button;
    }
    
    /**
     * Return the 1 Button
     * @return JButton
     */
    private JButton getNumber1Button() {
        if (number1Button == null) {
            number1Button = makeCalculatorButton("1");
        }
        return number1Button;
    }
    
    /**
     * Return the 2 Button
     * @return JButton
     */
    private JButton getNumber2Button() {
        if (number2Button == null) {
            number2Button = makeCalculatorButton("2");
        }
        return number2Button;
    }
    
    /**
     * Return the 3 Button
     * @return JButton
     */
    private JButton getNumber3Button() {
        if (number3Button == null) {
            number3Button = makeCalculatorButton("3");
        }
        return number3Button;
    }
    
    /**
     * Return the 4 Button
     * @return JButton
     */
    private JButton getNumber4Button() {
        if (number4Button == null) {
            number4Button = makeCalculatorButton("4");
        }
        return number4Button;
    }
    
    /**
     * Return the 5 Button
     * @return JButton
     */
    private JButton getNumber5Button() {
        if (number5Button == null) {
            number5Button = makeCalculatorButton("5");
        }
        return number5Button;
    }
    
    /**
     * Return the 6 Button.
     * @return JButton
     */
    private JButton getNumber6Button() {
        if (number6Button == null) {
            number6Button = makeCalculatorButton("6");
        }
        return number6Button;
    }
    
    /**
     * Return the 7 Button
     * @return JButton
     */
    private JButton getNumber7Button() {
        if (number7Button == null) {
            number7Button = makeCalculatorButton("7");
        }
        return number7Button;
    }
    
    /**
     * Return the 8 Button
     * @return JButton
     */
    private JButton getNumber8Button() {
        if (number8Button == null) {
            number8Button = makeCalculatorButton("8");
        }
        return number8Button;
    }
    
    /**
     * Return the 9 Button
     * @return JButton
     */
    private JButton getNumber9Button() {
        if (number9Button == null) {
            number9Button = makeCalculatorButton("9");
        }
        return number9Button;
    }
    
    /**
     * Return the OneOverX Button
     * @return JButton
     */
    private JButton getOneOverXButton() {
        if (oneOverXButton == null) {
            oneOverXButton = makeCalculatorButton("1/x");
        }
        return oneOverXButton;
    }
    
    /**
     * Return the Plus Button
     * @return JButton
     */
    private JButton getPlusButton() {
        if (plusButton == null) {
            plusButton = makeCalculatorButton("+");
        }
        return plusButton;
    }
    
    /**
     * Return the Sign Button
     * @return JButton
     */
    private JButton getSignButton() {
        if (signButton == null) {
            signButton = makeCalculatorButton("+/-");
        }
        return signButton;
    }
    
    /**
     * Return the Sqrt Button
     * @return JButton
     */
    private JButton getSqrtButton() {
        if (sqrtButton == null) {
            sqrtButton = makeCalculatorButton("sqrt");
        }
        return sqrtButton;
    }
    
    /**
     * Returns the calculator display field.
     * @return JTextField the text field used for calculator display
     */
    private JTextField getDisplayField() {
        if (displayField == null) {
            displayField = new JTextField();
        }
        return displayField;
    }
    
    /**
     * Returns the precedence level of the param op if op is one of "/", "*", "-", or "+".
     * Returns -1 otherwise.
     * @return int
     * @param op String 
     */
    private int getOpPrecedence(String op) {
        int precedence = -1;

        if (op.equals("/")) {
            precedence = PRECEDENCE_DIVIDE;
        } else if (op.equals("*")) {
            precedence = PRECEDENCE_MULTIPLY;
        } else if (op.equals("-")) {
            precedence = PRECEDENCE_MINUS;
        } else if (op.equals("+")) {
            precedence = PRECEDENCE_PLUS;
        }

        return precedence;
    }
    
    /**
     * Takes care of an op key being pressed.
     * Call this whenever an op key is pressed.
     * Param op should be one of "/", "*", "-", or "+".
     * @param op String
     */
    private void handleOpKeyPress(String op) {
        replaceOnOperandKeyPress = false;

        // If it's supposed to be an operand next, then replace the top
        // operator in the stack.        
        if (operandNext) {
            // Discard the top operator.
            operatorStack.pop();
        } else {
            operandNext = true;
            operandStack.push(getDisplayField().getText());
        }

        // If the next top operator has higher precedence, then
        // eval it (and keep checking down stack if necessary).
        while (!operatorStack.empty() && getOpPrecedence(operatorStack.peek()) >= getOpPrecedence(op)) {
            evalTopOp();
            // Possible that an illegal eval occured.
            if (isHandlingResultUndefined) {
                return;
            }
        }

        operatorStack.push(op);
    }
    
    /**
     * Takes the top operator in the operatorStack, and the top two operands in the operandStack, and evaluate.
     * Then takes the result and pushes it on the operandStack.
     * Note: You should always check the flag isHandlingResultUndefined, just in case an illegal eval occurred.
     * Creation date: (24/01/01 10:37:16 AM)
     */
    private void evalTopOp() {
        double result = 0;
        double operandB = Double.parseDouble(operandStack.pop());
        double operandA = Double.parseDouble(operandStack.pop());
        String op = operatorStack.pop();

        if (op.equals("/")) {
            // Check for invalid divider.
            if (operandB == 0) {
                handleResultUndefined(UNDEFINED_STRING);
                return;
            }

            result = operandA / operandB;

        } else if (op.equals("*")) {
            result = operandA * operandB;

        } else if (op.equals("-")) {
            result = operandA - operandB;

        } else if (op.equals("+")) {
            result = operandA + operandB;
        }

        operandStack.push(String.valueOf(result));
    }
    
    /**
     * Appends the param (appendStr) to the end of the text in the display.
     * Note: If the display text is "" or "0", then we replace the display text with appendStr.
     * Note: If we are trying to append after an operator (binary) key press (operandNext is true), 
     *   then we replace the display text with appendStr, and set operandNext to false. 
     * @param appendStr 
     */
    private void appendToDisplay(String appendStr) {

        if (operandNext) {
            getDisplayField().setText(appendStr);
            operandNext = false;
            return;
        }

        if (replaceOnOperandKeyPress) {
            getDisplayField().setText(appendStr);
            replaceOnOperandKeyPress = false;
            return;
        }

        String textValue = getDisplayField().getText();

        if (!textValue.equals("") && !textValue.equals("0")) {
            getDisplayField().setText(textValue + appendStr);
        } else {
            // No append, just replace.
            getDisplayField().setText(appendStr);
        }
    }
    
    /**
     * Determines whether or not the text field will be follow its document mode.
     *   (only proper values matching type allowed, or anything goes).
     * @param set whether or not to set document checking.
     */
    private void setDocumentChecking(boolean set) {
        ((ValueFormat)getDisplayField().getDocument()).setChecking(set);
    }
    
    /**
     * Handles the situation where the results of an expression are undefined.
     * Disables all buttons except the clear button "C", and displays an error message.
     * @param displayText the error message to display.
     */
    private void handleResultUndefined(String displayText) {
        isHandlingResultUndefined = true;

        // Note: There are two cases:
        // A) Focus is on calculator, in which case we want to display the error message, 
        //    and disable buttons.
        // B) Focus is on something else (not necessarily the parent), then no need to 
        //    display the error message, or disable buttons.
        if (this.hasOverallFocus()) {
            // Give error msg.    
            setDocumentChecking(false);
            getDisplayField().setText(displayText);

            Component[] compList = buttonPanel.getComponents();

            for (int i = 0; i < compList.length; i++) {
                if (compList[i] instanceof JButton) {
                    // Do not allow the Clear button to be disabled.
                    // (It will mess up the focus).
                    if (!compList[i].equals(getClearButton())) {
                        compList[i].setEnabled(false);
                    }
                }
            }

            // Disable all buttons except for the clear button:
            getClearButton().requestFocus();
        }
    }
    
    /**
     * Reverts the overall state of this NumberValueEditor (and its parent) to normal.
     */
    private void exitHandlingResultUndefined() {
        // Correct the parent's textfield.  
        setDocumentChecking(true);

        // Enable all buttons.
        Component[] compList = buttonPanel.getComponents();

        for (final Component component : compList) {
            if (component instanceof JButton) {
                component.setEnabled(true);
            }
        }

        getDisplayField().setText("0");

        isHandlingResultUndefined = false;
    }
    
    /**
     * Clears everything.
     * (Display value is 0, operand and operator stack is emptied, etc...)
     */
    private void clear_ActionEvents() {

        // First, check to see if we need to exit handling the undefined result.
        if (isHandlingResultUndefined) {
            exitHandlingResultUndefined();
        }

        getDisplayField().setText("0");
        operandNext = false;
        replaceOnOperandKeyPress = true;
        operatorStack.clear();
        operandStack.clear();
    }
    
    /**
     * Reflect the '.' key press in the display.
     * Note: Nothing will happen if the text in the display is empty, or it already contans a "."
     * Note: An exception to the above note is if the "." appears as the last character, in which case we drop the ".".
     */
    private void decimal_ActionEvents() {

        // First, check to see if it's the special case that 
        // an operator (binary) key was just pressed previously.
        if (operandNext) {
            getDisplayField().setText("0.");
            operandNext = false;
            return;
        }

        // Next, check to see if it's the other special case that
        // we need to replace on an operand key press.
        if (replaceOnOperandKeyPress) {
            getDisplayField().setText("0.");
            replaceOnOperandKeyPress = false;
            return;
        }

        String textValue = getDisplayField().getText();

        if ((!textValue.equals("")) && (-1 == textValue.indexOf("."))) {
            // Add a "." to the end of the value.
            getDisplayField().setText(textValue + ".");
        } else if (textValue.indexOf(".") == (textValue.length() - 1)) {
            // Drop the "." at the end of the value.
            getDisplayField().setText(textValue.substring(0, textValue.indexOf(".")));
        }
    }
    
    /**
     * Evaluates the entire expression, and displays it on the parent textfield.
     * Note: If there are no operators to be eval, then nothing happens.
     */
    private void equal_ActionEvents() {
        if (!operatorStack.empty()) {
            operandNext = false;
            operandStack.push(getDisplayField().getText());

            while (!operatorStack.empty()) {
                evalTopOp();

                // Possible that an illegal eval occured.
                if (isHandlingResultUndefined) {
                    return;
                }
            }

            // In theory, the final result should be the one value in the operandStack.
            String result = operandStack.pop();
            try {
                Double.parseDouble(result);

                // the result parses..
                getDisplayField().setText(result);

            } catch (NumberFormatException e) {
                // the double does not parse (eg. it's "Infinity" or "NaN")
                handleResultUndefined(result);
            }
        }

        replaceOnOperandKeyPress = true;
    }
    
    /**
     * Reflect the 1/x key press in the display.
     */
    private void oneOverX_ActionEvents() {
        operandNext = false;
        replaceOnOperandKeyPress = true;

        double value = Double.parseDouble(getDisplayField().getText());

        // Check for invalid value.
        if (value == 0) {
            handleResultUndefined(UNDEFINED_STRING);
            return;
        }

        double result = 1 / value;

        getDisplayField().setText(String.valueOf(result));
    }

    /**
     * Reflect the sign key press in the display.
     */
    private void sign_ActionEvents() {
        String textValue = getDisplayField().getText();

        // Can only apply +/- to non-zero, non-empty strings.
        if (!textValue.equals("") && !textValue.equals("0")) {
            StringBuilder sbTextValue = new StringBuilder(textValue);

            if (sbTextValue.charAt(0) == '-') {
                // Turn value positive.
                sbTextValue.deleteCharAt(0);
            } else {
                // Turn value negative.
                sbTextValue.insert(0, '-');
            }

            getDisplayField().setText(sbTextValue.toString());
        }
    }
    
    /**
     * Perform square root on the Value in the display.
     */
    private void sqrt_ActionEvents() {
        operandNext = false;
        replaceOnOperandKeyPress = true;

        double value = Double.parseDouble(getDisplayField().getText());

        // Check for invalid value.
        if (value < 0) {
            handleResultUndefined(UNDEFINED_STRING);
            return;
        }

        double result = Math.sqrt(value);

        getDisplayField().setText(String.valueOf(result));
    }   
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void commitValue() {
        // Simulate a '=' key press.
        equal_ActionEvents();

        // Check if we need to evaluate the user input - maybe it's an undefined result.
        if (isHandlingResultUndefined) {
            exitHandlingResultUndefined();
        }

        ValueNode returnVN = null;
        TypeExpr typeExpr = getValueNode().getTypeExpr();
        
        PreludeTypeConstants typeConstants = valueEditorManager.getValueNodeBuilderHelper().getPreludeTypeConstants();
        
        if (typeExpr.sameType(typeConstants.getByteType())) {

            Double unRoundedVal = new Double(getDisplayField().getText());
            Byte byteVal = new Byte((byte) Math.round(unRoundedVal.doubleValue()));

            returnVN = new LiteralValueNode(byteVal, getValueNode().getTypeExpr());
            
        } else if (typeExpr.sameType(typeConstants.getShortType())) {

            Double unRoundedVal = new Double(getDisplayField().getText());
            Short shortVal = new Short((short) Math.round(unRoundedVal.doubleValue()));

            returnVN = new LiteralValueNode(shortVal, getValueNode().getTypeExpr());
            
        } else if (typeExpr.sameType(typeConstants.getIntType())) {

            Double unRoundedVal = new Double(getDisplayField().getText());
            Integer integerVal = new Integer((int) Math.round(unRoundedVal.doubleValue()));

            returnVN = new LiteralValueNode(integerVal, getValueNode().getTypeExpr());                               
            
        } else if (typeExpr.sameType(typeConstants.getIntegerType())) {

            BigDecimal unRoundedVal = new BigDecimal(getDisplayField().getText());
            
            // Ridiculously, BigDecimal has 8 rounding modes, not one of which is equivalent
            // to the mode used by Math.round!  ROUND_HALF_CEILING is what such a mode would
            // be called if it existed; we can fake it out by using a different rounding mode
            // depending upon the sign of the unrounded value.
            BigInteger bigIntegerVal;
            
            if(unRoundedVal.signum() >= 0) {
                bigIntegerVal = unRoundedVal.setScale(0, BigDecimal.ROUND_HALF_UP).toBigInteger();
            } else {
                bigIntegerVal = unRoundedVal.setScale(0, BigDecimal.ROUND_HALF_DOWN).toBigInteger();
            }

            returnVN = new LiteralValueNode(bigIntegerVal, getValueNode().getTypeExpr());                               
        
        } else if (typeExpr.sameType(typeConstants.getDecimalType())) {

            BigDecimal decimalVal = new BigDecimal(getDisplayField().getText());
            returnVN = new LiteralValueNode(decimalVal, getValueNode().getTypeExpr());

        } else if (typeExpr.sameType(typeConstants.getLongType())) {

            Double unRoundedVal = new Double(getDisplayField().getText());
            Long longVal = new Long(Math.round(unRoundedVal.doubleValue()));

            returnVN = new LiteralValueNode(longVal, getValueNode().getTypeExpr());
            
        } else if (typeExpr.sameType(typeConstants.getFloatType())) {

            Float floatVal = new Float(getDisplayField().getText());
            returnVN = new LiteralValueNode(floatVal, getValueNode().getTypeExpr());

        } else if (typeExpr.sameType(typeConstants.getDoubleType())) {

            Double doubleVal = new Double(getDisplayField().getText());
            returnVN = new LiteralValueNode(doubleVal, getValueNode().getTypeExpr());
                        
        } else {
             throw new IllegalStateException("Error in NumberValueEditor close:\nCurrently cannot handle this type.");
        }

        replaceValueNode(returnVN, false);
        notifyValueCommitted();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void cancelValue() {
        // Simulate a '=' key press.
        equal_ActionEvents();

        // Check if we need to evaluate the user input - maybe it's an undefined result.
        if (isHandlingResultUndefined) {
            exitHandlingResultUndefined();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Component getDefaultFocusComponent() {
        return getNumber0Button();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInitialValue() {

        // Figure out what the value is that we're editing, as a double.
        Double doubleVal;
        try {
            doubleVal = new Double(getValueNode().getTextValue());

        } catch (NumberFormatException e) {
            // What to do?
            doubleVal = new Double("0.0");
        }

        // Configure the value format - the document for the display area.
        TypeExpr doubleType = TypeExpr.makeNonParametricType(valueEditorManager.getPerspective().getTypeConstructor(CAL_Prelude.TypeConstructors.Double));
        ValueNode doubleValueNode = new LiteralValueNode(doubleVal, doubleType);
        
        ValueFormat valueFormat = new ValueFormat(new ValueEntryPanel(valueEditorHierarchyManager, doubleValueNode));
        valueFormat.setChecking(true);

        // Configure the display area itself.
        getDisplayField().setDocument(valueFormat);
        getDisplayField().setText(getValueNode().getTextValue());
        getDisplayField().setEditable(false);
        getDisplayField().setBackground(Color.WHITE);
        getDisplayField().setHorizontalAlignment(SwingConstants.RIGHT);
    }
    
}
