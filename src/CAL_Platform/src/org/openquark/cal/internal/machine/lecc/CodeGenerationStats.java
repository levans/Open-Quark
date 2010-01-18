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
 * CodeGenerationStats.java
 * Creation date: Jan 5, 2006
 * By: RCypher
 */
package org.openquark.cal.internal.machine.lecc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openquark.cal.compiler.Expression;
import org.openquark.cal.compiler.ModuleName;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.internal.runtime.lecc.LECCMachineConfiguration;


/**
 * A class used to collect statistics about the code generated
 * by the lecc machine.
 * @author RCypher
 */
public class CodeGenerationStats {

    /** Map of ModuleName -> ModuleStats. Module name to stats for that module. */
    private Map<ModuleName, ModuleStats> moduleStats = new HashMap<ModuleName, ModuleStats>();
    
    private ModuleStats currentModuleStats = null;
    
    void startNewModule (ModuleName moduleName) {
        currentModuleStats = new ModuleStats (moduleName.toSourceText());
        moduleStats.put (moduleName, currentModuleStats);
    }

    void incrementSCArity (int arity) {
        if (currentModuleStats == null) {
            throw new NullPointerException("Attempt to update code generation statistics without a current module.");
        }
        currentModuleStats.incrementSCArity(arity);
    }

    void incrementDCArity (int arity) {
        if (currentModuleStats == null) {
            throw new NullPointerException("Attempt to update code generation statistics without a current module.");
        }
        currentModuleStats.incrementDCArity(arity);
    }
    
    void incrementDataType (int nDCs) {
        if (currentModuleStats == null) {
            throw new NullPointerException("Attempt to update code generation statistics without a current module.");
        }
        currentModuleStats.incrementDataType(nDCs);
    }
    
    void incrementLazyPrimOps (QualifiedName name) {
        if (currentModuleStats == null) {
            throw new NullPointerException("Attempt to update code generation statistics without a current module.");
        }
        currentModuleStats.incrementLazyPrimOps(name);
    }
    
    void incrementFullySaturatedDCInLazyContext (QualifiedName name) {
        if (currentModuleStats == null) {
            throw new NullPointerException("Attempt to update code generation statistics without a current module.");
        }
        currentModuleStats.incrementFullySaturatedDCInLazyContext(name);
    }

    void incrementFullySaturatedDCInStrictContext (QualifiedName name) {
        if (currentModuleStats == null) {
            throw new NullPointerException("Attempt to update code generation statistics without a current module.");
        }
        currentModuleStats.incrementFullySaturatedDCInStrictContext(name);
    }
    
    void incrementFullySaturatedDCInTopLevelContext (QualifiedName name) {
        if (currentModuleStats == null) {
            throw new NullPointerException("Attempt to update code generation statistics without a current module.");
        }
        currentModuleStats.incrementFullySaturatedDCInTopLevelContext(name);
    }
    
    void incrementOptimizedIfThenElse() {
        if (currentModuleStats == null) {
            throw new NullPointerException("Attempt to update code generation statistics without a current module.");
        }
        currentModuleStats.incrementOptimizedIfThenElse();
    }
    
    void incrementStrictDCFieldSelectionCount () {
        if (currentModuleStats == null) {
            throw new NullPointerException("Attempt to update code generation statistics without a current module.");
        }
        currentModuleStats.incrementStrictDCFieldSelectionCount();
    }
    
    void incrementLazyDCFieldSelectionCount () {
        if (currentModuleStats == null) {
            throw new NullPointerException("Attempt to update code generation statistics without a current module.");
        }
        currentModuleStats.incrementLazyDCFieldSelectionCount();
    }
    
    void incrementTopLevelDCFieldSelectionCount () {
        if (currentModuleStats == null) {
            throw new NullPointerException("Attempt to update code generation statistics without a current module.");
        }
        currentModuleStats.incrementTopLevelDCFieldSelectionCount();
    }
    
    void incrementDirectForeignCalls () {
        if (currentModuleStats == null) {
            throw new NullPointerException("Attempt to update code generation statistics without a current module.");
        }
        currentModuleStats.incrementDirectForeignCalls();
    }

    void incrementDirectSCCalls () {
        if (currentModuleStats == null) {
            throw new NullPointerException("Attempt to update code generation statistics without a current module.");
        }
        currentModuleStats.incrementDirectSCCalls();
    }
    
    void incrementLetNonRecCounts (int nVars) {
        if (currentModuleStats == null) {
            throw new NullPointerException("Attempt to update code generation statistics without a current module.");
        }
        currentModuleStats.incrementLetNonRecCounts(nVars);
    }
    
    void incrementLetRecCount (int nVars) {
        if (currentModuleStats == null) {
            throw new NullPointerException("Attempt to update code generation statistics without a current module.");
        }
        currentModuleStats.incrementLetRecCount(nVars);
    }
    
    void incrementOptimizedAndOr () {
        if (currentModuleStats == null) {
            throw new NullPointerException("Attempt to update code generation statistics without a current module.");
        }
        currentModuleStats.incrementOptimizedAndOr();
    }
    
    void incrementOptimizedPrimOp (QualifiedName name) {
        if (currentModuleStats == null) {
            throw new NullPointerException("Attempt to update code generation statistics without a current module.");
        }
        currentModuleStats.incrementOptimizedPrimOp(name);
    }

    void incrementNCases (Expression.Switch eSwitch) {
        if (currentModuleStats == null) {
            throw new NullPointerException("Attempt to update code generation statistics without a current module.");
        }
        currentModuleStats.incrementNCases(eSwitch);
    }
    
    void incrementSingleDCCases () {
        if (currentModuleStats == null) {
            throw new NullPointerException("Attempt to update code generation statistics without a current module.");
        }
        currentModuleStats.incrementSingleDCCases();
    }
    
    void incrementStrictSCNodeCount () {
        if (currentModuleStats == null) {
            throw new NullPointerException("Attempt to update code generation statistics without a current module.");
        }
        currentModuleStats.incrementStrictSCNodeCount();
    }
    void incrementStrictForeignNodeCount () {
        if (currentModuleStats == null) {
            throw new NullPointerException("Attempt to update code generation statistics without a current module.");
        }
        currentModuleStats.incrementStrictForeignNodeCount();
    }
    
    void incrementLazySCNodeCount () {
        if (currentModuleStats == null) {
            throw new NullPointerException("Attempt to update code generation statistics without a current module.");
        }
        currentModuleStats.incrementLazySCNodeCount();
    }
    void incrementLazyForeignNodeCount () {
        if (currentModuleStats == null) {
            throw new NullPointerException("Attempt to update code generation statistics without a current module.");
        }
        currentModuleStats.incrementLazyForeignNodeCount();
    }
    
    void incrementTailRecursiveFunctions (int arity) {
        if (currentModuleStats == null) {
            throw new NullPointerException("Attempt to update code generation statistics without a current module.");
        }
        currentModuleStats.incrementTailRecursiveFunctions(arity);
    }
    
    void incrementOptimizedDictionaryApplication (QualifiedName name) {
        if (currentModuleStats == null) {
            throw new NullPointerException("Attempt to update code generation statistics without a current module.");
        }
        currentModuleStats.incrementOptimizedDictionaryApplication(name);
    }
    
    void incrementUnderSaturation (int arity, int nArgs) {
        if (currentModuleStats == null) {
            throw new NullPointerException("Attempt to update code generation statistics without a current module.");
        }
        currentModuleStats.incrementUnderSaturation(arity, nArgs);
    }
    
    void dumpCodeGenerationStatistics () {
        String module = System.getProperty(LECCMachineConfiguration.CODE_GENERATION_STATS_PROP);
        if (module == null) {
            return;
        }
        
        if (module.equals("")) {
            module = "all";
        }
        ModuleStats mStats = moduleStats.get(ModuleName.maybeMake(module));
        if (mStats == null) {
            module = module.toLowerCase().trim();
            if (module.equals("all") && moduleStats.size() > 0) {
                mStats = ModuleStats.mergeStats(new ArrayList<ModuleStats>(moduleStats.values()));
            }
        }
        
        if (mStats == null) {
            System.out.println("Unable to find code generation statistics for module " + module);
        } else {
            mStats.dumpStats();
        }
    }
    
    
    private static class ModuleStats {
        String moduleNameOrLabel;

        /** Map of Integer -> Integer.  Maps function arity to number of functions with that arity. */
        private Map<Integer, Integer> scArities = new HashMap<Integer, Integer>();

        /** Map of Integer -> Integer.  Maps DC arity to number of DCs with that arity. */
        private Map<Integer, Integer> dcArities = new HashMap<Integer, Integer>();
        
        /** Total number of SCs */
        private int scCount; 
        
        /** Total number of DCs */
        private int dcCount;
        
        /** Total number of data types */
        private int dataTypeCount;
        
        /** Map of Integer -> Integer.  Maps number of non-recursive let vars to number of functions with that many let non-rec. */
        private Map<Integer, Integer> letNonRecCounts = new HashMap<Integer, Integer>();
        
        /** Total number on non recursive let variables. */
        private int letNonRecCount;
        
        /** Map of QualifiedName -> Integer.  Maps primitive ops encountered in a lazy context
         * to the number of occurrences. */
        private Map<QualifiedName, Integer> lazyPrimOps = new HashMap<QualifiedName, Integer>();
        
        /** QualifiedName -> Integer.  Maps data constructor name to the number of fully saturated occurrences in
         * a lazy context. */
        private Map<QualifiedName, Integer> saturatedDCsInLazyContext = new HashMap<QualifiedName, Integer>();
        private int nSaturatedLazyDCs;
        
        /** QualifiedName -> Integer.  Maps data constructor name to the number of fully saturated occurrences in
         * a strict context. */
        private Map<QualifiedName, Integer> saturatedDCsInStrictContext = new HashMap<QualifiedName, Integer>();
        private int nSaturatedStrictDCs;

        /** QualifiedName -> Integer.  Maps data constructor name to the number of fully saturated occurrences in
         * a top level context. */
        private Map<QualifiedName, Integer> saturatedDCsInTopLevelContext = new HashMap<QualifiedName, Integer>();
        private int nSaturatedTopLevelDCs;
        
        /** QualifiedName -> Integer.  Maps primitive op name to the number of instances which are directly evaluated. */
        private Map<QualifiedName, Integer> optimizedPrimOps = new HashMap<QualifiedName, Integer>();

        /** Count of conditionals that are generated as a java if-then-else. */
        private int optimizedIfThenElseCount;
        
        /** Count of DC field selections in a strict context. */
        private int strictDCFieldSelectionCount;
        
        /** Count of DC field selections in a lazy context. */
        private int lazyDCFieldSelectionCount;
        
        /** Count of DC field selections in a top level context. */
        private int topLevelDCFieldSelectionCount;
        
        /** Count the number of direct foreign calls generation. */
        private int directForeignCallsCount;
        
        /** Count the number of SC calls done directly.  i.e. fully saturated SC calls in a strict context. */
        private int directSCCallsCount;
        
        /** Count number of recursive let variables. */
        private int letRecVarCount;
        
        /** Count of and/or applications that are optimized by generating java && or || */
        private int optimizedAndOrCount;
        
        /** Integer -> Integer.  Maps number of alts to number of cases with that many alts. */
        private Map<Integer, Integer> casesByNAlts = new HashMap<Integer, Integer>();
        
        /** String -> Map (Integer -> Integer). Maps tag type to a Map of number of alts to number of cases with that many alts. */
        private Map<String, Map<Integer, Integer>> casesByTagType = new HashMap<String, Map<Integer, Integer>>();
        
        /** Total number of cases. */
        private int caseCount;
        
        /** Number of cases on a data type with only one DC. */
        private int singleDCCaseCount;

        /** Number of fully saturated applications that generate a specialized strict application node. */
        private int strictSaturatedSCNodeCount;

        /** Number of fully saturated foreign applications that generate a specialized strict application node. */
        private int strictSaturatedForeignNodeCount;
        
        /** Number of fully saturated applications that generate a specialized lazy application node. */
        private int lazySaturatedSCNodeCount;

        /** Number of fully saturated foreign applications that generate a specialized lazy application node. */
        private int lazySaturatedForeignNodeCount;
        
        /** Integer -> Integer.  Maps arity to number of tail recursive functions with that arity. */
        private Map<Integer, Integer> nTailRecursiveFunctionsByArity = new HashMap<Integer, Integer>();
        
        /** Total number of tail recursive functions. */
        private int tailRecursiveFunctionCount;
        
        /** QualifiedName -> Integer.  Maps TypeClass name to number of optimized applications. */
        private Map<QualifiedName, Integer> optimizedDictionaryApplicationsByTypeClass = new HashMap<QualifiedName, Integer>();
        
        /** Integer -> Integer.  Maps number of data constructors to number of data types with that many data constructors. */
        private Map<Integer, Integer> dataTypesByNDCs = new HashMap<Integer, Integer>();
        
        /** Integer -> (Map Integer -> Integer) Maps arity to map of # of undersaturated arguments to
         * number of instances. */
        private Map<Integer, Map<Integer, Integer>> underSatMap = new HashMap<Integer, Map<Integer, Integer>>();
        
        ModuleStats (String moduleNameOrLabel) {
            this.moduleNameOrLabel = moduleNameOrLabel;
        }

        void incrementSCArity (int arity) {
            Integer key = Integer.valueOf(arity);
            int count = 1;
            Integer ccount = scArities.get(key);
            if (ccount != null) {
                count += ccount.intValue();
            }
            scArities.put(key, Integer.valueOf(count));
            scCount++;
        }

        void incrementDCArity (int arity) {
            Integer key = Integer.valueOf(arity);
            int count = 1;
            Integer ccount = dcArities.get(key);
            if (ccount != null) {
                count += ccount.intValue();
            }
            dcArities.put(key, Integer.valueOf(count));
            dcCount++;
        }
        
        void incrementLazyPrimOps (QualifiedName name) {
            Integer ccount = lazyPrimOps.get(name);
            if (ccount == null) {
                ccount = Integer.valueOf(1);
            } else {
                ccount = Integer.valueOf(ccount.intValue() + 1);
            }
            lazyPrimOps.put(name, ccount);
        }
        
        void incrementFullySaturatedDCInLazyContext (QualifiedName name) {
            Integer ccount = saturatedDCsInLazyContext.get(name);
            if (ccount == null) {
                ccount = Integer.valueOf(1);
            } else {
                ccount = Integer.valueOf(ccount.intValue() + 1);
            }
            saturatedDCsInLazyContext.put(name, ccount);
            nSaturatedLazyDCs++;
        }
        
        void incrementFullySaturatedDCInStrictContext (QualifiedName name) {
            Integer ccount = saturatedDCsInStrictContext.get(name);
            if (ccount == null) {
                ccount = Integer.valueOf(1);
            } else {
                ccount = Integer.valueOf(ccount.intValue() + 1);
            }
            saturatedDCsInStrictContext.put(name, ccount);
            nSaturatedStrictDCs++;
        }

        void incrementFullySaturatedDCInTopLevelContext (QualifiedName name) {
            Integer ccount = saturatedDCsInTopLevelContext.get(name);
            if (ccount == null) {
                ccount = Integer.valueOf(1);
            } else {
                ccount = Integer.valueOf(ccount.intValue() + 1);
            }
            saturatedDCsInTopLevelContext.put(name, ccount);
            nSaturatedTopLevelDCs++;
        }
        
        void incrementOptimizedIfThenElse() {
            optimizedIfThenElseCount++;
        }
        
        void incrementStrictDCFieldSelectionCount () {
            strictDCFieldSelectionCount++;
        }
        
        void incrementLazyDCFieldSelectionCount () {
            lazyDCFieldSelectionCount++;        
        }
        
        void incrementTopLevelDCFieldSelectionCount () {
            topLevelDCFieldSelectionCount++;
        }
        
        void incrementDirectForeignCalls () {
            directForeignCallsCount++;
        }

        void incrementDirectSCCalls () {
            directSCCallsCount++;
        }

        void incrementLetNonRecCounts (int nVars) {
            if (nVars == 0) {
                return;
            }
            
            Integer key = Integer.valueOf(nVars);
            int count = 1;
            Integer ccount = letNonRecCounts.get(key);
            if (ccount != null) {
                count += ccount.intValue();
            }
            letNonRecCounts.put(key, Integer.valueOf(count));
            letNonRecCount += nVars;
        }
        
        void incrementLetRecCount (int nVars) {
            letRecVarCount++;
        }
        
        void incrementOptimizedAndOr () {
            optimizedAndOrCount++;
        }
        
        void incrementOptimizedPrimOp (QualifiedName name) {
            Integer ccount = optimizedPrimOps.get(name);
            if (ccount == null) {
                ccount = Integer.valueOf(1);
            } else {
                ccount = Integer.valueOf(ccount.intValue() + 1);
            }
            optimizedPrimOps.put(name, ccount);
        }
        
        void incrementNCases (Expression.Switch eSwitch) {
            int nAlts = eSwitch.getNAlts();
            Object tag = null;
            for (int i = 0; i < nAlts; ++i) {
                Expression.Switch.SwitchAlt alt = eSwitch.getAlt(i);
                if (!alt.isDefaultAlt()) {
                    tag = alt.getFirstAltTag();
                    break;
                }
            }
            String tagType;
            if (tag == null) {
                tagType = "default only";
            } else {
                tagType = tag.getClass().getName();
            }
            
            // Track cases by number of alts.
            Integer key = Integer.valueOf(nAlts);
            int count = 1;
            Integer ccount = casesByNAlts.get(key);
            if (ccount != null) {
                count += ccount.intValue();
            }
            casesByNAlts.put(key, Integer.valueOf(count));
            
            // Track cases by number of alts within tag type.
            Map<Integer, Integer> casesByNAltsForTag = casesByTagType.get(tagType);
            if (casesByNAltsForTag == null) {
                casesByNAltsForTag = new HashMap<Integer, Integer>();
                casesByTagType.put (tagType, casesByNAltsForTag);
            }
            
            count = 1;
            ccount = casesByNAltsForTag.get(key);
            if (ccount != null) {
                count += ccount.intValue();
            }
            casesByNAltsForTag.put(key, Integer.valueOf(count));
            
            caseCount++;
        }
        
        void incrementSingleDCCases () {
            singleDCCaseCount++;
        }
        
        void incrementStrictSCNodeCount () {
            strictSaturatedSCNodeCount++;
        }
        void incrementStrictForeignNodeCount () {
            strictSaturatedForeignNodeCount++;
        }

        void incrementLazySCNodeCount () {
            lazySaturatedSCNodeCount++;
        }
        void incrementLazyForeignNodeCount () {
            lazySaturatedForeignNodeCount++;
        }

        void incrementTailRecursiveFunctions (int arity) {
            Integer key = Integer.valueOf(arity);
            int count = 1;
            Integer ccount = nTailRecursiveFunctionsByArity.get(key);
            if (ccount != null) {
                count += ccount.intValue();
            }
            nTailRecursiveFunctionsByArity.put(key, Integer.valueOf(count));
            tailRecursiveFunctionCount++;
        }
        
        void incrementOptimizedDictionaryApplication (QualifiedName name) {
            Integer ccount = optimizedDictionaryApplicationsByTypeClass.get(name);
            if (ccount == null) {
                ccount = Integer.valueOf(1);
            } else {
                ccount = Integer.valueOf(ccount.intValue() + 1);
            }
            optimizedDictionaryApplicationsByTypeClass.put(name, ccount);
        }
        
        void incrementDataType (int nDCs) {
            Integer key = Integer.valueOf(nDCs);
            int count = 1;
            Integer ccount = dataTypesByNDCs.get(key);
            if (ccount != null) {
                count += ccount.intValue();
            }
            dataTypesByNDCs.put(key, Integer.valueOf(count));
            dataTypeCount++;
        }
        
        void dumpStats () {
            System.out.println("");
            System.out.println("Code generation statistics for module: " + moduleNameOrLabel);
            System.out.println("");
            System.out.println("    Total number of supercombinators: " + scCount);

            // Display SCs by arity.
            System.out.println(dumpMap_sortByKey(scArities, "SCs by Arity:", "arity -> count"));
            System.out.println("");

            
            System.out.println("    Total number of data types: " + dataTypeCount);
            
            // Display data types by number of DCs
            System.out.println(dumpMap_sortByKey(dataTypesByNDCs, "Data types by data constructor count:", "number of DCs -> count"));
            System.out.println("");
            
            System.out.println("    Total number of data constructors: " + dcCount);
            
            // Display DCs by arity.
            System.out.println(dumpMap_sortByKey(dcArities, "DCs by Arity:", "arity -> count" ));
            System.out.println("");
            
            // Cases
            System.out.println("    Total number of cases: " + caseCount);
            System.out.println("    Number of cases on single DC data type: " + singleDCCaseCount);
            System.out.println("");
            
            // Cases by number alts by tag type.
            System.out.println("    Cases by number of alternates, within each tag type: ");
            List<String> tagTypeList = new ArrayList<String>(casesByTagType.keySet());
            Collections.sort(tagTypeList);
            for (final String tagType : tagTypeList) {
                
                Map<Integer, Integer> tagMap = casesByTagType.get(tagType);
                
                System.out.println("        Tag: " + tagType);
                System.out.println("            N Alternates -> Count");
                List<Integer> nAltsList = new ArrayList<Integer>(tagMap.keySet());
                Collections.sort(nAltsList);
                for (final Integer key : nAltsList) {                    
                    Integer value = tagMap.get(key);
                    System.out.println("            " + key.toString() + " -> " + value.toString());
                }
            }
            System.out.println("");
            
            System.out.println(dumpMap_sortByValue(optimizedPrimOps, "Optimized primitive operation instances", "operation -> Count"));
            System.out.println("");

            System.out.println(dumpMap_sortByValue(lazyPrimOps, "Lazy primitive operation instances", "operation -> Count"));
            System.out.println("");

            System.out.println("    Number of direct foreign calls: " + directForeignCallsCount);
            System.out.println("    Number of direct SC calls: " + directSCCallsCount);
            System.out.println("    Number of optimized strict SC application nodes: " + strictSaturatedSCNodeCount);
            System.out.println("    Number of optimized strict foreign application nodes: " + strictSaturatedForeignNodeCount);
            System.out.println("    Number of optimized lazy SC application nodes: " + lazySaturatedSCNodeCount);
            System.out.println("    Number of optimized lazy foreign application nodes: " + lazySaturatedForeignNodeCount);
            System.out.println("    Number of top level DC field selections: " + topLevelDCFieldSelectionCount);
            System.out.println("    Number of strict DC field selections: " + strictDCFieldSelectionCount);
            System.out.println("    Number of lazy DC field selections: " + lazyDCFieldSelectionCount);
            System.out.println("    Number of optimized conditionals: " + optimizedIfThenElseCount);
            System.out.println("    Number of optimized &&/||: " + optimizedAndOrCount);
            System.out.println("");
            
            System.out.println("    Total number of non recursive let variables: " + letNonRecCount);
            System.out.println(dumpMap_sortByKey(letNonRecCounts, "Number of functions by number of let nonrec.", "# of let vars -> # of functions."));
            System.out.println("");
            
            System.out.println("    Total number of tail recursive functions: " + tailRecursiveFunctionCount);
            System.out.println(dumpMap_sortByKey(nTailRecursiveFunctionsByArity, "Tail recursive functions by arity.", "arity -> Count"));
            System.out.println("");
            
            System.out.println("    Total number of saturated data construcors in a lazy context: " + nSaturatedLazyDCs);
            System.out.println(dumpMap_sortByValue(saturatedDCsInLazyContext, "Fully saturated DCs in lazy context.", "name -> count"));
            System.out.println("");
            
            System.out.println("    Total number of saturated data construcors in a strict context: " + nSaturatedStrictDCs);
            System.out.println(dumpMap_sortByValue(saturatedDCsInStrictContext, "Fully saturated DCs in strict context.", "name -> count"));
            System.out.println("");

            System.out.println("    Total number of saturated data construcors in a top level context: " + nSaturatedTopLevelDCs);
            System.out.println(dumpMap_sortByValue(saturatedDCsInTopLevelContext, "Fully saturated DCs in top level context.", "name -> count"));
            System.out.println("");

            // Display SCs by arity.
            System.out.println("    Undersaturated SC applications");
            Object[] keys = underSatMap.keySet().toArray();
            Arrays.sort(keys);
            for (int i = 0, n = keys.length; i < n; ++i) {
                Object key = keys[i];
                System.out.println("        arity = " + key.toString());
                Map<Integer, Integer> argMap = underSatMap.get(key);
                Object[] keys2;
                Arrays.sort(keys2 = argMap.keySet().toArray());
                for(int j = 0, jn = keys2.length; j < jn; ++j) {
                    Object key2 = keys2[j];
                    Object value = argMap.get(key2);
                    System.out.println("            arg count = " + key2 + ", instances = " + value);
                }
            }
            System.out.println("");
            
        }
        
        static private String dumpMap_sortByKey (Map<Integer, Integer> map, String title, String keyName) {
            StringBuilder sb = new StringBuilder();
            sb.append ("    " + title + "\n");
            sb.append ("        " + keyName + "\n");
            List<Integer> keyList = new ArrayList<Integer>(map.keySet());
            Collections.sort(keyList);
            for (final Integer key : keyList) {               
                Integer value = map.get(key);
                sb.append("        " + key.toString() + " -> " + value.toString() + "\n");
            }
            
            return sb.toString();
        }

        static private <K> String dumpMap_sortByValue (Map<K, Integer> map, String title, String keyName) {
            StringBuilder sb = new StringBuilder();
            sb.append ("    " + title + "\n");
            sb.append ("        " + keyName + "\n");
            List<K> keyList = new ArrayList<K>(map.keySet());
            
            List<ObjectPair> pairList = new ArrayList<ObjectPair>();
            for (final K key : keyList) {                
                Integer value = map.get(key);
                pairList.add(new ObjectPair(key, value));
            }
            
            Collections.sort(pairList);
            
            for (final ObjectPair pair : pairList) {                
                sb.append("        " + pair.o1.toString() + " -> " + pair.o2.toString() + "\n");
            }
            return sb.toString();
        }
        
        
        
        private void increment (ModuleStats other) {
            this.directForeignCallsCount += other.directForeignCallsCount;
            this.directSCCallsCount += other.directSCCallsCount;
            this.lazyDCFieldSelectionCount += other.lazyDCFieldSelectionCount;
            this.lazySaturatedForeignNodeCount += other.lazySaturatedForeignNodeCount;
            this.lazySaturatedSCNodeCount += other.lazySaturatedSCNodeCount;
            this.letRecVarCount += other.letRecVarCount;
            this.optimizedAndOrCount += other.optimizedAndOrCount;
            this.optimizedIfThenElseCount += other.optimizedIfThenElseCount;
            this.singleDCCaseCount += other.singleDCCaseCount;
            this.strictDCFieldSelectionCount += other.strictDCFieldSelectionCount;
            this.strictSaturatedForeignNodeCount += other.strictSaturatedForeignNodeCount;
            this.strictSaturatedSCNodeCount += other.strictSaturatedSCNodeCount;
            this.topLevelDCFieldSelectionCount += other.topLevelDCFieldSelectionCount;
            this.dataTypeCount += other.dataTypeCount;
            this.dcCount += other.dcCount;
            this.scCount += other.scCount;
            this.caseCount += other.caseCount;
            this.letNonRecCount += other.letNonRecCount;
            this.tailRecursiveFunctionCount += other.tailRecursiveFunctionCount;
            this.nSaturatedLazyDCs += other.nSaturatedLazyDCs;
            this.nSaturatedStrictDCs += other.nSaturatedStrictDCs;
            this.nSaturatedTopLevelDCs += other.nSaturatedTopLevelDCs;
            
            mergeMapOfInteger (this.casesByNAlts, other.casesByNAlts);
            mergeMapOfInteger (this.dataTypesByNDCs, other.dataTypesByNDCs);
            mergeMapOfInteger (this.dcArities, other.dcArities);
            mergeMapOfInteger (this.lazyPrimOps, other.lazyPrimOps);
            mergeMapOfInteger (this.letNonRecCounts, other.letNonRecCounts);
            mergeMapOfInteger (this.nTailRecursiveFunctionsByArity, other.nTailRecursiveFunctionsByArity);
            mergeMapOfInteger (this.optimizedDictionaryApplicationsByTypeClass, other.optimizedDictionaryApplicationsByTypeClass);
            mergeMapOfInteger (this.optimizedPrimOps, other.optimizedPrimOps);
            mergeMapOfInteger (this.saturatedDCsInLazyContext, other.saturatedDCsInLazyContext);
            mergeMapOfInteger (this.saturatedDCsInStrictContext, other.saturatedDCsInStrictContext);
            mergeMapOfInteger (this.saturatedDCsInTopLevelContext, other.saturatedDCsInTopLevelContext);
            mergeMapOfInteger (this.scArities, other.scArities);
            mergeMapOfMapOfInteger (this.casesByTagType, other.casesByTagType);
            mergeMapOfMapOfInteger (this.underSatMap, other.underSatMap);
        }
        
        private void incrementUnderSaturation (int arity_, int nArgs_) {
            Integer arity = Integer.valueOf(arity_);
            Integer nArgs = Integer.valueOf(nArgs_);
            
            Map<Integer, Integer> argMap = underSatMap.get(arity);
            if (argMap == null) {
                argMap = new HashMap<Integer, Integer>();
                underSatMap.put (arity, argMap);
            }
            
            Integer count = argMap.get(nArgs);
            if (count == null) {
                count = Integer.valueOf(1);
            } else {
                count = Integer.valueOf(count.intValue()+1);
            }
            argMap.put(nArgs, count);
            
        }

        private static <K1, K2> void mergeMapOfMapOfInteger (Map<K1, Map<K2, Integer>> map1, Map<K1, Map<K2, Integer>> map2) {
            for (final Map.Entry<K1, Map<K2, Integer>> entry : map2.entrySet()) {
                
                K1 key = entry.getKey();
                Map<K2, Integer> value1 = map1.get(key);
                Map<K2, Integer> value2 = entry.getValue();
                if (value1 == null) {
                    map1.put(key, value2);
                } else {
                    mergeMapOfInteger(value1, value2);
                }
            }
        }
        
        /**
         * Merge the contents of map2 into map1. 
         * @param map1
         * @param map2
         */
        private static <K> void mergeMapOfInteger (Map<K, Integer> map1, Map<K, Integer> map2) {
            for (final Map.Entry<K, Integer> entry : map2.entrySet()) {
               
                K key = entry.getKey();
                Integer value2 = entry.getValue();
                
                Integer value1 = map1.get(key);
                if (value1 == null) {
                    map1.put(key, value2);
                } else {
                    map1.put(key, Integer.valueOf(value1.intValue() + value2.intValue()));
                }
            }
        }
        
        private static ModuleStats mergeStats (List<ModuleStats> stats) {
            ModuleStats cumulative = new ModuleStats("all modules");
            
            for (final ModuleStats mStats : stats) {                
                cumulative.increment(mStats);
            }
            return cumulative;
        }
    }
    
    private static class ObjectPair implements Comparable<ObjectPair> {
        private final Object o1; 
        private final Integer o2;
        
        ObjectPair (Object o1, Integer o2) {this.o1 = o1; this.o2 = o2;}
        
        public int compareTo (ObjectPair other) {
            return other.o2.compareTo(o2);
        }
        @Override
        public boolean equals (Object o) {
            if (o == null || !(o instanceof ObjectPair)) {
                return false;
            }
            
            ObjectPair other = (ObjectPair)o;
            return o1.equals(other.o1) && o2.equals(other.o2);
        }
        @Override
        public int hashCode () {
            return 37 * (17 + o1.hashCode()) + o2.hashCode();           
        }
        
    }
    
}
