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
 * DrawTest.java
 * Creation date: July 4, 2005.
 * By: Richard Webster
 */

package org.openquark.cal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import org.openquark.cal.compiler.CompilerMessage;
import org.openquark.cal.compiler.CompilerMessageLogger;
import org.openquark.cal.compiler.MessageLogger;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.Scope;
import org.openquark.cal.compiler.SourceModel;
import org.openquark.cal.compiler.SourceModel.Expr;
import org.openquark.cal.compiler.io.EntryPointSpec;
import org.openquark.cal.runtime.CALExecutorException;
import org.openquark.cal.runtime.ExecutionContext;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.GemCompilationException;
import org.openquark.cal.services.GemEntity;
import org.openquark.util.BusinessObjectsException;


/**
 * Test for CAL drawing functions.
 */
public class DrawTest extends JFrame {

    private static final long serialVersionUID = -5255424946932404551L;

    /** CAL service for running code, looking up gems, etc.... */
    private final CALServicesHelper calHelper;

    /** The current drawing function. */
    private Expr currentDrawExpr = Expr.makeGemCall(QualifiedName.make("Cal.Test.Experimental.Graphics.Drawing_Tests", "drawTest1"));

    /**
     * DrawTest constructor.
     */
    DrawTest() throws BusinessObjectsException {
        super("Draw Test");
        this.calHelper = new CALServicesHelper();

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });

        // Get the available drawing gems and display these in a combo box.
        Collection<GemEntity> drawingGems = calHelper.findGemsOfType("Cal.Experimental.Graphics.Drawing.Graphics -> Cal.Experimental.Graphics.Drawing.Graphics");

        final JComboBox drawFnCombo = new JComboBox(new Vector<GemEntity>(drawingGems));
        drawFnCombo.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    GemEntity gemEntity = (GemEntity) drawFnCombo.getSelectedItem();
                    currentDrawExpr = Expr.makeGemCall(gemEntity.getName());
                    repaint();
                }
            });
        drawFnCombo.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = -2433581223714718290L;

                @Override
                public Component getListCellRendererComponent(
                        JList list,
                        Object value,
                        int index,
                        boolean isSelected,
                        boolean cellHasFocus) {
                    JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    
                    GemEntity gemEntity = (GemEntity) value;
                    label.setText(gemEntity.getName().getQualifiedName());
                    return label;
                }
            });

        // Set the initial drawing gem.
        if (drawingGems.isEmpty()) {
            throw new BusinessObjectsException("No drawing gems are available.");
        }
        GemEntity initialDrawGem = drawingGems.iterator().next();
        currentDrawExpr = Expr.makeGemCall(initialDrawGem.getName());

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(drawFnCombo, BorderLayout.NORTH);
        this.getContentPane().add(new DrawPanel());
        
        this.addWindowListener(new WindowAdapter(){});
    }

    /**
     * Main method for test application.
     */
    public static void main(String[] args) {
        try {
            JFrame frame = new DrawTest();

            frame.setSize(400, 400);
            frame.setVisible(true);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * A panel to draw the test output.
     */
    private class DrawPanel extends JPanel {

        private static final long serialVersionUID = -2535389023763350640L;

        /**
         * @see java.awt.Component#paint(java.awt.Graphics)
         */
        @Override
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

            super.paint(g);

            try {
                g2 = (Graphics2D) calHelper.runCode(currentDrawExpr, new Object[] { g2 });
            }
            catch (BusinessObjectsException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * A wrapper around the cal services to provide additional functionality.
     */
    private static class CALServicesHelper {
    
        /** The run module for code common to all users. */
        private static final ModuleName COMMON_RUN_MODULE = ModuleName.make("Cal.Test.Experimental.Graphics.Drawing_Tests");
    
        /** A property for overriding the default CAL workspace. */
        private static final String WORKSPACE_FILE_PROPERTY = "experimental.drawing_test.workspace";
    
        /** The CAL workspace file. */
        private static final String DEFAULT_WORKSPACE_FILE = "cal.libraries.test.cws";
        
        /** The default workspace client id. */
        private static final String DEFAULT_WORKSPACE_CLIENT_ID = null;
    
        /** The CAL services. */
        private final BasicCALServices calServices;
    
        private ExecutionContext executionContext;
    
        /**
         * CALServicesHelper constructor.
         */
        public CALServicesHelper() throws BusinessObjectsException {
            boolean compilationSuccessful = false;
            
            CompilerMessageLogger messageLogger = new MessageLogger();
            
            BasicCALServices newCalServices = null;
            try {
                newCalServices = BasicCALServices.make (WORKSPACE_FILE_PROPERTY,
                        DEFAULT_WORKSPACE_FILE, DEFAULT_WORKSPACE_CLIENT_ID);
                
                if (newCalServices != null) {
                    // If there's a local cache, the workspace will use it, rather than the modules in the repo
                    // ...so if the cache is out of date, now's the time to synch
                    compilationSuccessful = newCalServices.compileWorkspace(null, messageLogger);
                }
            } 
            catch (Throwable t) {
                t.printStackTrace();
                
                newCalServices = null;
            }
            
            if ((newCalServices == null) || !compilationSuccessful){
                String message = "";
                if (messageLogger.getNErrors() > 0) {
                    StringBuffer msg = new StringBuffer("Failed to compile workspace:");
                    for (final CompilerMessage cm : messageLogger.getCompilerMessages()) {
                        msg.append("\n");
                        msg.append(cm);
                    }
    
                    message = msg.toString();
                    System.err.println(message);
                }
    
                throw new BusinessObjectsException(message);
            }
    
            this.calServices = newCalServices;
    
            // Get an execution context.
            this.executionContext = calServices.getWorkspaceManager().makeExecutionContextWithDefaultProperties();
        }
    
        /**
         * Returns a collection of gems which have the specified type signature. 
         * @param typeName  the type signature of the gems to be found
         * @return          a collection of the matching gems
         */
        public Collection<GemEntity> findGemsOfType(String typeName) {
            return calServices.findGemsOfType(COMMON_RUN_MODULE, typeName, true);
        }
    
        /**
         * Runs the a function with the specified arguments.
         * The result will be marshalled to Java using the default output policy.
         */
        public Object runCode(Expr code, Object[] arguments) throws BusinessObjectsException {
    
            final String RUN_TARGET_NAME = "runDrawTestCode";
            final ModuleName TARGET_MODULE_NAME = ModuleName.make(COMMON_RUN_MODULE + "_Temp");     // TODO: allow this to be specified...
    
            arguments = fixUpArgumentValues(arguments);
    
            try {
                SourceModel.FunctionDefn functionDefn = SourceModel.FunctionDefn.Algebraic.make(RUN_TARGET_NAME, 
                                                                                                Scope.PUBLIC, 
                                                                                                new SourceModel.Parameter[0], 
                                                                                                code);
                
                EntryPointSpec entryPointSpec = calServices.addNewModuleWithFunction(TARGET_MODULE_NAME, functionDefn);
                Object result = calServices.runFunction(entryPointSpec, executionContext, arguments);
    
                return result;
            }
            catch (CALExecutorException e) {
                throw new BusinessObjectsException(e);
            }
            catch (GemCompilationException e) {
                throw new BusinessObjectsException(e);
            }
        }
    
        /**
         * Replace any InternalCalValueHolder objects with the actual CalValues.
         */
        private Object[] fixUpArgumentValues(Object[] arguments) {
            if (arguments == null) {
                arguments = new Object[0];
            } else {
                // Make a copy of the array.
                arguments = arguments.clone();
            }
    
            return arguments;
        }
    }
}
