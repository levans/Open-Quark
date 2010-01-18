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
 * CALRenaming_DeepTest.java
 * Created: Apr 26, 2005
 * By: Peter Cardwell
 */

package org.openquark.cal.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.openquark.cal.CALPlatformTestModuleNames;
import org.openquark.cal.compiler.CompilerMessage.Severity;
import org.openquark.cal.compiler.SourceIdentifier.Category;
import org.openquark.cal.runtime.MachineType;
import org.openquark.cal.services.BasicCALServices;
import org.openquark.cal.services.CALFeatureName;
import org.openquark.cal.services.CALServicesTestUtilities;
import org.openquark.cal.services.CALWorkspace;
import org.openquark.cal.services.ResourceName;
import org.openquark.cal.services.ResourceStore;
import org.openquark.cal.services.Status;
import org.openquark.cal.services.StringModuleSourceDefinition;
import org.openquark.util.FileSystemHelper;


/**
 * This class contains an exhaustive test of the renaming functionality, which works by renaming
 * all symbols in the M2 module, recompiling, then running M2.
 * @author Peter Cardwell
 */
public class CALRenaming_DeepTest extends TestCase {    
    
    /**
     * Set this flag to true if debugging output is desired regardless of
     * whether a test fails or succeeds.
     */
    private static final boolean SHOW_DEBUGGING_OUTPUT = false;

    /**
     * Constructor for CALRenaming_Test.
     * 
     * @param name
     *            the name of the test.
     */
     public CALRenaming_DeepTest(String name) {
       super(name);
     } 
    
    /**
     * A helper class for the testRenameAllM2Symbols method that contains the information needed to
     * perform a single renaming operation. 
     * @author Peter Cardwell
     */
    private static final class Renaming {
        private final String oldName;
        private final String newName;
        private final Category category;

        private Renaming(String oldName, String newName, SourceIdentifier.Category category) {
            this.oldName = oldName;
            this.newName = newName;
            this.category = category;
        }
    }
    
    /**
     * This method will rename all symbols that can be renamed in the given module, recompile, 
     * and then run the specified function and compare its return value to the expected outcome.
     * 
     * The method will fail if compilation fails or if the return value of the function that is run does not
     * match the expected value.
     * 
     * @param moduleName
     * @param workspaceFile
     * @param targetMethod
     * @param expectedReturnVal
     */
    private void renameAllSymbolsInModule(final ModuleName moduleName, final String workspaceFile, final String targetMethod, boolean renameModule, final Object expectedReturnVal) {
        try {
            String renamedTargetMethod = targetMethod;
            BasicCALServices privateCopyLeccServices = CALServicesTestUtilities.makeUnsharedUnitTestCALServices(
                MachineType.LECC,
                "org.openquark.cal.test.workspace.CALRenaming_DeepTest.renameAllSymbolsInModule",
                workspaceFile,
                null, "CALRenaming_DeepTest.renameAllSymbolsInModule", false);
            
            try {
                privateCopyLeccServices.compileWorkspace(null, new MessageLogger());
                
                ModuleSourceDefinition sourceDef = makeSourceDefFromBuiltIn(moduleName, privateCopyLeccServices.getCALWorkspace());
                String moduleString = getSourceString(sourceDef);
                List <Renaming> renamings = new ArrayList<Renaming>();
                
                // Convert the source into a source model representation, then use this model to obtain a list of symbols to rename.
                SourceModel.ModuleDefn moduleDefn = SourceModelUtilities.TextParsing.parseModuleDefnIntoSourceModel(moduleString);
                SourceModel.TopLevelSourceElement[] sourceElements = moduleDefn.getTopLevelDefns();
                for (int i = 0, n = sourceElements.length; i < n; i++) {
                    if (sourceElements[i] instanceof SourceModel.TypeConstructorDefn) {
                        SourceModel.TypeConstructorDefn typeConstructorDefn = (SourceModel.TypeConstructorDefn)sourceElements[i];
                        renamings.add(new Renaming(typeConstructorDefn.getTypeConsName(), "RenamedTypeCons" + i, SourceIdentifier.Category.TYPE_CONSTRUCTOR));
                        if (typeConstructorDefn instanceof SourceModel.TypeConstructorDefn.AlgebraicType) {
                            SourceModel.TypeConstructorDefn.AlgebraicType algebraicType = (SourceModel.TypeConstructorDefn.AlgebraicType) typeConstructorDefn;
                            SourceModel.TypeConstructorDefn.AlgebraicType.DataConsDefn[] dataConsDefns = algebraicType.getDataConstructors();
                            for (int j = 0, m = dataConsDefns.length; j < m; j++) {
                                renamings.add(new Renaming(dataConsDefns[j].getDataConsName(), ("RenamedDataCons" + i + "_" + j), SourceIdentifier.Category.DATA_CONSTRUCTOR));
                            }
                        }
                    } else if (sourceElements[i] instanceof SourceModel.TypeClassDefn) {
                        SourceModel.TypeClassDefn typeClassDefn = (SourceModel.TypeClassDefn)sourceElements[i];
                        renamings.add(new Renaming(typeClassDefn.getTypeClassName(), "RenamedTypeClass" + i, SourceIdentifier.Category.TYPE_CLASS));
                        SourceModel.TypeClassDefn.ClassMethodDefn[] classMethodDefns = typeClassDefn.getClassMethodDefns();
                        for (int j = 0, m = classMethodDefns.length; j < m; j++) {
                            renamings.add(new Renaming(classMethodDefns[j].getMethodName(), ("renamedClassMethod" + i + "_" + j), SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD));
                        }
                    } else if (sourceElements[i] instanceof SourceModel.FunctionDefn) {
                        SourceModel.FunctionDefn functionDefn = (SourceModel.FunctionDefn)sourceElements[i];
                        if (functionDefn.getName().equals(targetMethod)) {
                            renamedTargetMethod = "renamedFunction" + i;
                        }
                        renamings.add(new Renaming(functionDefn.getName(), "renamedFunction" + i, SourceIdentifier.Category.TOP_LEVEL_FUNCTION_OR_CLASS_METHOD));
                    } else if (sourceElements[i] instanceof SourceModel.InstanceDefn) {                    
                    } else if (sourceElements[i] instanceof SourceModel.FunctionTypeDeclaration) {                    
                    } else {
                        fail("Invalid top level source element type.");
                    }
                }
                
                if(renameModule) {
                    renamings.add(new Renaming(moduleName.toSourceText(), ("ModuleMangledByRenaming_" + moduleName), SourceIdentifier.Category.MODULE_NAME));
                }
                
                ModuleTypeInfo moduleTypeInfo = privateCopyLeccServices.getWorkspaceManager().getModuleTypeInfo(moduleName);
                
                // Now run through the list and perform all the renamings.
                ModuleName recompiledModuleName = moduleName;
                for (int n = renamings.size(), i = 0; i < n; i++) {                
                    Renaming renaming = renamings.get(i);
                    
                    QualifiedName oldQualifiedName;
                    QualifiedName newQualifiedName;
                    
                    if (renaming.category == SourceIdentifier.Category.MODULE_NAME) {
                        oldQualifiedName = QualifiedName.make(ModuleName.make(renaming.oldName), Refactorer.Rename.UNQUALIFIED_NAME_FOR_MODULE_RENAMING);
                        newQualifiedName = QualifiedName.make(ModuleName.make(renaming.newName), Refactorer.Rename.UNQUALIFIED_NAME_FOR_MODULE_RENAMING);
                    } else {
                        oldQualifiedName = QualifiedName.make(moduleName, renaming.oldName);
                        newQualifiedName = QualifiedName.make(moduleName, renaming.newName);
                    }
                    
                    CompilerMessageLogger logger = new MessageLogger();
                    SourceModifier renamer = IdentifierRenamer.getSourceModifier(moduleTypeInfo, moduleString, oldQualifiedName, newQualifiedName, renaming.category, logger);
                    assertTrue(logger.getMaxSeverity().compareTo(Severity.ERROR) < 0);
                    
                    moduleString = renamer.apply(moduleString);
                    if (renaming.category == SourceIdentifier.Category.MODULE_NAME && recompiledModuleName.equals(ModuleName.make(renaming.oldName))) {
                        recompiledModuleName = ModuleName.make(renaming.newName);
                    }
                }
                
                sourceDef = new StringModuleSourceDefinition(recompiledModuleName, moduleString);
                
                // Remove the old module from the workspace and add the new one. This will also force a recompilation of the relevant modules.
                CompilerMessageLogger logger = new MessageLogger();
                privateCopyLeccServices.getWorkspaceManager().removeModule(moduleName, new Status(""));     
                privateCopyLeccServices.getWorkspaceManager().makeModule(sourceDef, logger);
                if (logger.getMaxSeverity().compareTo(CompilerMessage.Severity.ERROR) >= 0) {
                    List<CompilerMessage> errors = logger.getCompilerMessages(CompilerMessage.Severity.ERROR);
                    for (int i = 0, n = errors.size(); i < n; i++) {
                        System.out.println(errors.get(i).toString());
                    }
                    fail();
                }
                
                // Run the specified function and compare its value to the expected result
                assertEquals(expectedReturnVal,
                    CALServicesTestUtilities.runNamedFunction(QualifiedName.make(recompiledModuleName, renamedTargetMethod), privateCopyLeccServices));
                
            } finally {
                File rootDir =  CALServicesTestUtilities.getWorkspaceRoot(null, "CALRenaming_DeepTest.renameAllSymbolsInModule");

                boolean deleted = FileSystemHelper.delTree(rootDir);
                if (!deleted && SHOW_DEBUGGING_OUTPUT) {
                    System.err.println("Workspace directory not deleted: " + rootDir);
                }
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("An exception was thrown: " + ex.toString());
        }
    }
    
    /**
     * Reads in the contents of a ModuleSourceDefinition and returns it as a string.
     * @param sourceDef
     * @return the module source definition as a string
     * @throws IOException
     */
    private static String getSourceString(ModuleSourceDefinition sourceDef) throws IOException {
        Reader reader = sourceDef.getSourceReader(new Status("Get source reader status"));
        reader = new BufferedReader(reader);
        char buff[] = new char[1024];
        StringBuilder moduleTextStringBuilder = new StringBuilder();
    
        int br = reader.read(buff);
        while (br != -1) {
            moduleTextStringBuilder.append(new String(buff, 0, br));
            br = reader.read(buff);
        }
        reader.close();
        return moduleTextStringBuilder.toString();
    }
    
    /**
     * Creates a ModuleSourceDefinition from a module in the standard vault.
     * @param builtInModuleName The name of the built in module.
     * @return A ModuleSourceDefinition object
     */
    private static ModuleSourceDefinition makeSourceDefFromBuiltIn (ModuleName builtInModuleName, CALWorkspace workspace) {
        
        final ResourceStore sourceStore = workspace.getSourceManager(builtInModuleName).getResourceStore();
        final CALFeatureName moduleFeatureName = CALFeatureName.getModuleFeatureName(builtInModuleName);
        final ResourceName moduleResourceName = new ResourceName(moduleFeatureName);
        return new ModuleSourceDefinition(builtInModuleName) {
            @Override
            public InputStream getInputStream(Status status) {
                return sourceStore.getInputStream(moduleResourceName);
            }
            
            @Override
            public long getTimeStamp() {
                return sourceStore.getTimeStamp(moduleResourceName);
            }

            @Override
            public String getDebugInfo() {
                return sourceStore.getDebugInfo(moduleResourceName);
            }
        };
    }

    /**
     * Renames all symbols in the M2 module and then runs the mainM2 function. 
     */
    public void testRenameAllSymbolsInM2() {
        // We cannot rename the module itself, as M2 contains a number of foreign functions
        // implemented in the org.openquark.cal.foreignsupport.module.M2 package.
        // As such the compiler will complain that the package of these foreign classes do
        // not correspond to the name of the renamed module.
        renameAllSymbolsInModule(CALPlatformTestModuleNames.M2, "cal.platform.test.cws", "mainM2", false, Boolean.TRUE);
    }
    
    /**
     * Renames all symbols in the CALDocTest module and then runs the returnTrue function.
     * (this helps us verify that we're dealing properly with CALDoc comments) 
     */
    public void testRenameAllSymbolsInCALDocTest() {
        // Go ahead and rename the module itself, since there are no foreignsupport functions in this
        // module.
        renameAllSymbolsInModule(CALPlatformTestModuleNames.CALDocTest, "cal.platform.test.cws", "returnTrue", true, Boolean.TRUE);
    }
}
