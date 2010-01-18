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
 * IntellicutListModelAdapter.java
 * Creation date: Dec 12, 2003
 * By: Frank Worsley
 */
package org.openquark.gems.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JComponent;

import org.openquark.cal.compiler.FieldName;
import org.openquark.cal.compiler.ModuleTypeInfo;
import org.openquark.cal.compiler.QualifiedName;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy;
import org.openquark.cal.compiler.SourceMetrics;
import org.openquark.cal.compiler.TypeExpr;
import org.openquark.cal.compiler.ScopedEntityNamingPolicy.UnqualifiedUnlessAmbiguous;
import org.openquark.cal.compiler.TypeChecker.TypeCheckInfo;
import org.openquark.cal.services.CALWorkspace;
import org.openquark.cal.services.GemEntity;
import org.openquark.cal.services.GemViewer;
import org.openquark.cal.services.MetaModule;
import org.openquark.cal.services.Perspective;
import org.openquark.cal.services.UnorderedTypeMatchFilter;
import org.openquark.gems.client.AutoburnLogic.AutoburnUnifyStatus;
import org.openquark.gems.client.AutoburnLogic.BurnCombination;
import org.openquark.gems.client.IntellicutListModel.FilterLevel;
import org.openquark.gems.client.IntellicutManager.IntellicutInfo;

/**
 * An adapter class for the IntellicutListModel. Specific implementations of an adapter derive
 * from this class and implement the abstract methods. This class allows the IntellicutListModel
 * to be customized to work with additional data types.
 * @author Frank Worsley
 */
public abstract class IntellicutListModelAdapter {
    
    /**
     * Comparator class used to do a compare between IntellicutListEntry objects.
     */
    private static class IntellicutListEntryComparator implements Comparator<Object> {
        
        private static final CaseInsensitiveStringComparator stringComparator = new CaseInsensitiveStringComparator();
        
        /**
         * Case insensitive string comparator. This is identical to the case insensitive
         * comparator in the java.lang.String class, except that it bumps the importance 
         * of strings beginning with "{" to be ordered right after strings beginning with "(".
         */
        private static class CaseInsensitiveStringComparator implements Comparator<String> {
            
            public int compare(String s1, String s2) {
                int n1 = s1.length(), n2 = s2.length();
                
                if (n1 > 0 && n2 > 0) {
                    
                    if (s1.charAt(0) == '{') {
                        char c2 = s2.charAt(0);
                        if (c2 <= '(') {
                            return 1;
                            
                        } else if (c2 != '{') {
                            return -1;
                            
                        } else {
                            s1 = s1.substring(1);
                            s2 = s2.substring(1);
                            n1--;
                            n2--;
                        }
                        
                    } else if (s2.charAt(0) == '{') {
                        
                        char c1 = s1.charAt(0);
                        if (c1 <= '(') {
                            return -1;
                            
                        } else if (c1 != '{') {
                            return 1;
                            
                        } else {
                            s1 = s1.substring(1);
                            s2 = s2.substring(1);
                            n1--;
                            n2--;
                        }
                    }
                } 
                
                for (int i1 = 0, i2 = 0; i1 < n1 && i2 < n2; i1++, i2++) {
                    char c1 = s1.charAt(i1);
                    char c2 = s2.charAt(i2);
                    if (c1 != c2) {
                        c1 = Character.toUpperCase(c1);
                        c2 = Character.toUpperCase(c2);
                        if (c1 != c2) {
                            c1 = Character.toLowerCase(c1);
                            c2 = Character.toLowerCase(c2);
                            if (c1 != c2) {
                                return c1 - c2;
                            }
                        }
                    }
                }
                return n1 - n2;
            }
        }
        
        /** Map to hold ordering of intellicut list entry classes */
        private static final Map<String, Integer> classOrderMap = new HashMap<String, Integer>();
        static {
            classOrderMap.put(   GemEntityListEntry.class.toString(), Integer.valueOf(0));
            classOrderMap.put(CollectorGemListEntry.class.toString(), Integer.valueOf(1));
            classOrderMap.put(    TypeExprListEntry.class.toString(), Integer.valueOf(2));
        }
        
        /**
         * Compares the list entry objects.
         * The entries are first ordered by alphabetically ascending sort strings,
         * sub-ordered by alphabetically ascending display strings, sub-ordered by
         * class type and contents. 
         * All string comparisons are case insensitive, and the class orderings are
         * defined in the classOrderMap. 
         * 
         * @param o1
         * @param o2
         * @return negative, zero, or positive integer, when the first object
         *         is prior to, the same as, or greater than the second object
         */
        public int compare (Object o1, Object o2) {
            
            // Compare sorted strings
            String s1 = ((IntellicutListEntry) o1).getSortString();
            String s2 = ((IntellicutListEntry) o2).getSortString();
            int c = stringComparator.compare(s1, s2);
            
            if (c == 0) {
                // Sorted strings are equal, so compare their display strings
                s1 = ((IntellicutListEntry) o1).getDisplayString();
                s2 = ((IntellicutListEntry) o2).getDisplayString();
                c = stringComparator.compare(s1, s2);
            }
            
            if (c == 0) {
                // Display strings are equal, so compare their secondary display strings
                s1 = ((IntellicutListEntry) o1).getSecondaryDisplayString();
                s2 = ((IntellicutListEntry) o2).getSecondaryDisplayString();
                c = stringComparator.compare(s1, s2);
            }
            
            if (c == 0) {
                // Secondary display strings are equal, so compare the classes
                c = classOrderMap.get(o1.getClass().toString()).compareTo(classOrderMap.get(o2.getClass().toString()));
            }
            
            if (c == 0) {
                // Classes are equal, so compare their string representations
                c = o1.toString().compareTo(o2.toString());
            }
            
            return c;
        }
    }
    
    /**
     * This class represents an entry in the intellicut model. It encapsulates the data object
     * this entry represents and the intellicut info of that data object.
     * @author Frank Worsley
     */
    public static abstract class IntellicutListEntry {

        /** The data object that this list entry encapsulates. */
        private final Object dataObject;
        
        /** The intellicut info associated with this list entry. */
        private IntellicutInfo intellicutInfo;
        
        /**
         * Constructor for a new IntellicutListEntry object.
         * @param dataObject the data object this list entry is for
         */
        public IntellicutListEntry(Object dataObject) {
            
            if (dataObject == null) {
                throw new NullPointerException();
            }
            
            this.dataObject = dataObject;
        }
        
        /**
         * @return the data object held onto by this list entry
         */
        public final Object getData() {
            return dataObject;
        }
        
        /**
         * @param intellicutInfo the intellicut info for this list entry (must be non-null)
         */
        public void setIntellicutInfo(IntellicutInfo intellicutInfo) {
            
            if (intellicutInfo == null) {
                throw new NullPointerException();
            }
            
            this.intellicutInfo = intellicutInfo;
        }
        
        /**
         * @return the intellicut info for this list entry
         */
        public IntellicutInfo getIntellicutInfo() {
            return intellicutInfo;
        }
        
        /**
         * @return the string representation of the entry as it should be displayed in the list.
         */
        public abstract String getDisplayString();

        /**
         * @return the string representation of the entry as it should be written in the source code.
         */
        public String getSourceText(){        
            String sourceText = getSourceText();
            Object entryData = getData();
            if (entryData instanceof GemEntity) {
                if (!getSecondaryDisplayString().equals("")) {
                    // Secondary display string exists; this indicates the module for an ambiguous entry
                    sourceText = ((GemEntity)entryData).getName().getQualifiedName();
                }
            }
            return sourceText;
        }

        /**
         * @return a string containing additional information which is subjected to
         *         special formatting when displayed in the list.  Never null.
         */
        public String getSecondaryDisplayString() {
            return "";
        }
        
        /**
         * @return the string to use for sorting this entry in the intellicut list. This should
         * always be the unqualified name of the entry. This name is also used for narrowing
         * the intellicut list if the user types.
         */
        public abstract String getSortString();
        
        /**
         * @return the result type of the stored object or null if there is no result type.
         */
        public abstract TypeExpr getResultType();
        
        /**
         * @return the type expression of the stored object or null if there is no type expression
         */
        public abstract TypeExpr getTypeExpr();
        
        /**
         * @return the type pieces of the stored object to use for autoburning or null if 
         * there are no such type pieces.
         */
        public abstract TypeExpr[] getTypePiecesForBurning();
        
        /**
         * @return the argument type pieces of the stored object or null if there are no such type pieces.
         */
        public abstract TypeExpr[] getArgumentTypePieces();
        
        /**
         * @return true if the stored object has any disconnected inputs, false if all inputs
         * are connected or if there are no inputs at all.
         */
        public abstract boolean hasDisconnectedInputs();

        /**
         * @return whether autoburning should be attempted on the stored object
         */
        public abstract boolean isAutoBurnable();
        
        /**
         * We need to override equals so we can find list entries after the list has been
         * reloaded and restore the list position. Two list entries are equal if they
         * have the same data object.
         * @param o the object to compare this list entry with
         * @return true if this list entry equals the list entry referenced by object o
         */
        @Override
        public boolean equals(Object o) {
            
            if (o == this) {
                return true;
            }
            
            if (o instanceof IntellicutListEntry) {
                
                IntellicutListEntry listEntry = (IntellicutListEntry) o;
                
                if (listEntry.getData().equals(dataObject)) {
                    return true;
                }
            }
            
            return false;
        }
        
        /**
         * The hashcode for a list entry is the hash code of its stored data object
         * This is overridden so that it corresponds with the equals implementation.
         * @return the hash code for this list entry
         */
        @Override
        public int hashCode() {
            return dataObject.hashCode();
        }

        /**
         * Don't remove this. It's used by the IntellicutComboBox to display the text of an
         * entry in the combobox text field.
         * @return the String representation of the list entry as it should be displayed in the list
         */
        @Override
        public String toString() {
            return getDisplayString();
        }
    }
    
    /**
     * A list entry implementation for a GemEntity.
     * @author Frank Worsley
     */
    private static class GemEntityListEntry extends IntellicutListEntry {
        
        /** The naming policy to use when displaying the entity name. */
        private ScopedEntityNamingPolicy namingPolicy;
        
        public GemEntityListEntry(GemEntity entity, ScopedEntityNamingPolicy namingPolicy) {
            super(entity);
            
            if (namingPolicy == null) {
                throw new NullPointerException();
            }
            
            this.namingPolicy = namingPolicy;
        }
        
        public GemEntity getEntity() {
            return (GemEntity) getData();
        }
        
        @Override
        public String getDisplayString() {
            return getEntity().getName().getUnqualifiedName();
        }

        @Override
        public String getSourceText(){
            return getEntity().getAdaptedName(namingPolicy); 
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String getSecondaryDisplayString() {
            String adaptedName = getEntity().getAdaptedName(namingPolicy);
            if (QualifiedName.isValidCompoundName(adaptedName)) {
                
                // Adapted name is fully qualified, so entry is ambiguous
                // (use module as secondary display string)
                QualifiedName qualifiedName = QualifiedName.makeFromCompoundName(adaptedName);
                return qualifiedName.getModuleName().toSourceText();
                
            } else {
                // Unqualified name; no additional info
                return "";
            }
        }
        
        @Override
        public String getSortString() {
            return getEntity().getName().getUnqualifiedName();
        }
        
        @Override
        public TypeExpr getTypeExpr() {
            return getEntity().getTypeExpr();
        }
        
        @Override
        public TypeExpr getResultType() {
            return getEntity().getTypeExpr().getResultType();
        }
        
        @Override
        public TypeExpr[] getTypePiecesForBurning() {
            return getEntity().getTypeExpr().getTypePieces();
        }

        @Override
        public TypeExpr[] getArgumentTypePieces() {

            TypeExpr typeExpr = getEntity().getTypeExpr();
            int numArgs = typeExpr.getArity();

            if (numArgs == 0) {
                return null;
                
            } else {
                TypeExpr arguments[] = new TypeExpr[numArgs];
                TypeExpr pieces[] = typeExpr.getTypePieces();

                System.arraycopy(pieces, 0, arguments, 0, numArgs);
                
                return arguments;
            }
        }
        
        @Override
        public boolean hasDisconnectedInputs() {
            return getEntity().getTypeArity() > 0;
        }
        
        @Override
        public boolean isAutoBurnable() {
            return true;
        }
        
        void setNamingPolicy(ScopedEntityNamingPolicy namingPolicy) {
            this.namingPolicy = namingPolicy;
        }
    }
    
    /**
     * A list entry implementation for a TypeExpr.
     * @author Frank Worsley
     */
    private static class TypeExprListEntry extends IntellicutListEntry {
        
        /** The naming policy to use when displaying the entity name. */
        private final ScopedEntityNamingPolicy namingPolicy;
        
        public TypeExprListEntry(TypeExpr typeExpr, ScopedEntityNamingPolicy namingPolicy) {
            super(typeExpr);
            
            if (namingPolicy == null) {
                throw new NullPointerException();
            }
            
            this.namingPolicy = namingPolicy;
        }
        
        @Override
        public String getDisplayString() {
            
            // Special case: Record polymorphic types are displayed as {Record} rather than {r} 
            if (getTypeExpr().sameType(TypeExpr.makeFreeRecordType(Collections.<FieldName>emptySet()))) {
                return "{Record}";
                
            } else {
                return getTypeExpr().toString(namingPolicy);
            }
        }
        
        @Override
        public String getSortString() {
            return getTypeExpr().toString(namingPolicy);
        }
        
        @Override
        public TypeExpr getTypeExpr() {
            return (TypeExpr) getData();
        }
        
        @Override
        public TypeExpr getResultType() {
            return getTypeExpr().getResultType();
        }
        
        @Override
        public TypeExpr[] getTypePiecesForBurning() {
            return getTypeExpr().getTypePieces();
        }

        @Override
        public TypeExpr[] getArgumentTypePieces() {

            int numArgs = getTypeExpr().getArity();

            if (numArgs == 0) {
                return null;
                
            } else {
                TypeExpr arguments[] = new TypeExpr[numArgs];
                TypeExpr pieces[] = getTypeExpr().getTypePieces();

                System.arraycopy(pieces, 0, arguments, 0, numArgs);
                
                return arguments;
            }
        }
        
        @Override
        public boolean hasDisconnectedInputs() {
            return getTypeExpr().getArity() > 0;
        }
        
        @Override
        public boolean isAutoBurnable() {
            return true;
        }
    }
    
    /**
     * A list entry implementation for a CollectorGem.
     * @author Frank Worsley
     */
    private static class CollectorGemListEntry extends IntellicutListEntry {
        
        public CollectorGemListEntry(CollectorGem collector) {
            super(collector);
        }
        
        public CollectorGem getCollector() {
            return (CollectorGem) getData();
        }
        
        @Override
        public String getDisplayString() {
            return getCollector().getUnqualifiedName();
        }
        
        @Override
        public String getSortString() {
            return getCollector().getUnqualifiedName();
        }
        
        @Override
        public TypeExpr getResultType() {
            return getCollector().getCollectingPart().getType();
        }
        
        @Override
        public TypeExpr getTypeExpr() {
            return getCollector().getCollectingPart().getType();
        }
        
        @Override
        public TypeExpr[] getTypePiecesForBurning() {
            return null;
        }

        @Override
        public TypeExpr[] getArgumentTypePieces() {
            return getCollector().getCollectingPart().getType().getTypePieces();
        }
        
        @Override
        public boolean hasDisconnectedInputs() {
            return false;
        }

        @Override
        public boolean isAutoBurnable() {
            return false;
        }
    }
    
    /**
     * An adapter that includes all visible gems from the perspective for that can 
     * match given input AND output types.
     * @author Frank Worsley
     */
    public static class InputOutputAdapter extends IntellicutListModelAdapter {
        
        /** The type check info to use for autoburning. */
        private final TypeCheckInfo typeCheckInfo;

        /** The workspace to load modules from. */
        private final CALWorkspace workspace;
        
        /** List of valid input types that gems must support. */
        private TypeExpr[] inputExpr = null;
        
        /** List of valid output types that gems must support. */
        private TypeExpr[] outputExpr = null;

        /**
         * Constructor for the InputOuputAdapter which is used by the IntellicutComboBox.
         *  
         * @param inputTypes a list of valid input types to the gems
         * @param outputTypes a list of valid output types to the gems
         * @param workspace the workspace to load gems from
         * @param typeCheckInfo the type check info to use
         */
        public InputOutputAdapter(TypeExpr[] inputTypes,
                                  TypeExpr[] outputTypes,
                                  CALWorkspace workspace,
                                  TypeCheckInfo typeCheckInfo) {
            
            if (workspace == null || typeCheckInfo == null) {
                throw new NullPointerException();
            }
            
            this.inputExpr = inputTypes.clone();
            this.outputExpr = outputTypes.clone();
            this.typeCheckInfo = typeCheckInfo;
            this.workspace = workspace;
            
            setNamingPolicy(new UnqualifiedUnlessAmbiguous(typeCheckInfo.getModuleTypeInfo()));
        }

        @Override
        protected Set<GemEntity> getDataObjects() {
            
            MetaModule targetMetaModule = workspace.getMetaModule(typeCheckInfo.getModuleName());
            
            GemViewer gemViewer = new GemViewer();
            gemViewer.addFilter(new UnorderedTypeMatchFilter(inputExpr, outputExpr, targetMetaModule.getTypeInfo(), true));

            Perspective perspective = new Perspective(workspace, targetMetaModule);
            perspective.setGemEntityViewer(gemViewer);
            
            return getVisibleGemsFromPerspective(perspective);   
        }
        
        @Override
        protected IntellicutInfo getIntellicutInfo(IntellicutListEntry listEntry) {

            // If only output type restrictions have been specified, then calculate type
            // type closeness of the results.
            if (inputExpr == null && outputExpr != null && outputExpr.length > 0) {
                return getIntellicutInfo(listEntry, outputExpr, typeCheckInfo, true, workspace.getSourceMetrics());
            } else {
                return new IntellicutInfo(AutoburnUnifyStatus.NOT_NECESSARY, -1, 1, false, getReferenceFrequency(workspace.getSourceMetrics(), listEntry));
            }
        }

        /**
         * Resets this adapter by clearing all loaded gems and updating the input/output
         * types to use. Next time the model that uses this adapter is refreshed, it will
         * cause the adapter to reload the model.
         * @param inputExpr a list of valid input types to the gems
         * @param outputExpr a list of valid output types from the gems
         */
        public void reset(TypeExpr[] inputExpr, TypeExpr[] outputExpr) {
            this.inputExpr = inputExpr.clone();
            this.outputExpr = outputExpr.clone();
            clear();
        }
    }
    
    /**
     * The adapter used by the IntellicutList for the TableTop. It includes all entities that
     * can connect to a given part connectable.
     * @author Frank Worsley
     */
    public static class PartConnectableAdapter extends IntellicutListModelAdapter {
        
        /** The intellicut part we are building the list for. */
        private final Gem.PartConnectable intellicutPart;
        
        /** The type check info to use for autoburning. */
        private final TypeCheckInfo typeCheckInfo;
        
        /** The perspective we loaded the gems from. */
        private final Perspective perspective;
        
        /**
         * Constructor for IntellicutListModel used by the IntellicutList. The PartConnectable passed in
         * will be used to find a list of gems that can connect to that part.
         * 
         * If the connectable is a PartInput then all gems whose output (maybe with burning) can connect to
         * that input will be included. If the connectable is a PartOutput then all gems whose input can
         * connect with the connectable will be included. If the connectable is null then all gems will be
         * included in the list.
         * 
         * @param intellicutPart the PartConnectable to match gems with
         * @param perspective the perspective to get gems from
         * @param typeCheckInfo the type check info to use for autoburning
         */
        public PartConnectableAdapter(Gem.PartConnectable intellicutPart,
                                      Perspective perspective,
                                      TypeCheckInfo typeCheckInfo) {

            if (perspective == null || typeCheckInfo == null) {
                throw new NullPointerException();
            }
            
            this.perspective = perspective;
            this.intellicutPart = intellicutPart;
            this.typeCheckInfo = typeCheckInfo;
            
            setNamingPolicy(new UnqualifiedUnlessAmbiguous(perspective.getWorkingModuleTypeInfo()));
        }

        @Override
        protected Set<GemEntity> getDataObjects() {
            return getVisibleGemsFromPerspective(perspective);   
        }
        
        @Override
        protected IntellicutInfo getIntellicutInfo(IntellicutListEntry listEntry) {
            
            if (intellicutPart instanceof Gem.PartInput) {
                return getIntellicutInfo(listEntry, intellicutPart.getType(), typeCheckInfo, true, perspective.getWorkspace().getSourceMetrics());
                
            } else if (intellicutPart instanceof Gem.PartOutput) {
                return getIntellicutInfo(listEntry, intellicutPart.getType(), typeCheckInfo.getModuleTypeInfo(), perspective.getWorkspace().getSourceMetrics());
                
            } else {
                return new IntellicutInfo(AutoburnUnifyStatus.NOT_NECESSARY, -1, 1, false, getReferenceFrequency(perspective.getWorkspace().getSourceMetrics(), listEntry));
            }
        }
        
        /**
         * Resolves ambiguity between displayed names of two entities.
         * Clashes may occur between the displayed unqualified names of
         * Collector and Gem, or Gem and Gem entries; these will be resolved 
         * by fully qualifying the names of Gem entries.
         * 
         * @see org.openquark.gems.client.IntellicutListModelAdapter#resolveDisplayAmbiguity(org.openquark.gems.client.IntellicutListModelAdapter.IntellicutListEntry, org.openquark.gems.client.IntellicutListModelAdapter.IntellicutListEntry)
         */
        @Override
        protected void resolveDisplayAmbiguity(IntellicutListEntry newEntry, IntellicutListEntry existingEntry) {
            if (newEntry instanceof CollectorGemListEntry) {
                if (existingEntry instanceof GemEntityListEntry) {
                    // Collector clashes with an existing entity
                    
                    // Collector always has unqualified name as its displayed name,
                    // thus the existing entry is fully qualifiable
                    ((GemEntityListEntry)existingEntry).setNamingPolicy(ScopedEntityNamingPolicy.FULLY_QUALIFIED);
                }
                
            } else if (newEntry instanceof GemEntityListEntry) {
                if (existingEntry instanceof GemEntityListEntry) {
                    // Gem vs Gem
                    // Qualify both parties if they are not qualified
                    GemEntityListEntry existingGemEntry = ((GemEntityListEntry)existingEntry);
                    if (!QualifiedName.isValidCompoundName(existingGemEntry.getDisplayString())) {
                        existingGemEntry.setNamingPolicy(ScopedEntityNamingPolicy.FULLY_QUALIFIED);
                        ((GemEntityListEntry)newEntry).setNamingPolicy(ScopedEntityNamingPolicy.FULLY_QUALIFIED);
                    }
                    
                } else if (existingEntry instanceof CollectorGemListEntry) {
                    // Gem vs Collector
                    // Collector always has unqualified name, so qualify the gem
                    ((GemEntityListEntry)newEntry).setNamingPolicy(ScopedEntityNamingPolicy.FULLY_QUALIFIED);
                }
            }
        }
        
        /**
         * Filters gems based on reference frequency and type closeness if possible.
         * If there is no type to compare for type closeness, then uses the inherited
         * reference-frequency-only filter.
         * 
         * @param listEntry The entry to check against the specified filter
         * @param filterLevel The level of filter to apply
         * @return true if the specified list entry passes the specified filter
         */
        @Override
        public boolean passesFilter(IntellicutListEntry listEntry, FilterLevel filterLevel) {
            
            if (filterLevel == null) {
                throw new NullPointerException("filterLevel must not be null in IntellicutListModelAdapter.PartConnectableAdapter.passesFilter");
            }
        
            // If we have no current part (and therefore can't do type closeness checks),
            // then defer to the default filtering (which only uses reference frequency)
            if (intellicutPart == null) {
                return super.passesFilter(listEntry, filterLevel);
            }
        
            // Filter gems based on both reference frequency and type closeness
            if (filterLevel == FilterLevel.SHOW_BEST) {
                if (listEntry.getIntellicutInfo().isSameNonpolymorphicType()) {
                    return listEntry.getIntellicutInfo().isSameNonpolymorphicTypeThreshold() ||
                           listEntry.getIntellicutInfo().isReferenceFrequencyInTopFifth();
                } else {
                    return (listEntry.getIntellicutInfo().getMaxTypeCloseness() >= 1) &&
                           (listEntry.getIntellicutInfo().isReferenceFrequencyInTopFifth());
                }
            } else if (filterLevel == FilterLevel.SHOW_LIKELY) {
                return (listEntry.getIntellicutInfo().getMaxTypeCloseness() >= 1) ||
                (listEntry.getIntellicutInfo().isReferenceFrequencyInTopFifth());
            } else { // SHOW_ALL
                return true;
            }
        }
    }
    
    /** 
     * Gems are divided into those whose reference frequency is in the top
     * REFERENCE_FREQUENCY_PERCENTILE_THRESHOLD percent, and those whose 
     * frequency is not.
     */
    private static final int REFERENCE_FREQUENCY_PERCENTILE_THRESHOLD = 20; 
    
    /**
     * The top SAME_NONPOLYMORPHIC_TYPE_REFERENCE_FREQUENCY_RANK gems with the
     * same non-polymorphic type as the target gem are included in the Best Gems
     * list even if they are not in the top REFERENCE_FREQUENCY_PERCENTILE_THRESHOLD
     * of gems by reference frequency.  (See IntellicutInfo for a description of why).
     */
    private static final int SAME_NONPOLYMORPHIC_TYPE_REFERENCE_FREQUENCY_RANK = 10;
    
    /** 
     * This set contains all the list entries that can be shown in the intellicut list.
     * That is the combination of entries for the entities from the perspective and entries
     * for the additional gems that have been added. Not all entries in this set may be 
     * visible at all times, this depends on the show all/show best gems setting and the
     * prefix that the user has typed in.
     */
    private final SortedSet<IntellicutListEntry> intellicutEntrySet = new TreeSet<IntellicutListEntry>(new IntellicutListEntryComparator());
      
    /**
     * A map from a data object to the associated IntellicutListEntry.
     */
    private final Map<Object, IntellicutListEntry> intellicutEntryMap = new HashMap <Object,IntellicutListEntry>();
    
    /**
     * A set of additional objects that should be added to the model as list entries.
     */
    private final Set<Gem> additionalDataObjects = new HashSet<Gem>();
    
    /** The visibility policy to use to decide which items to display. */
    private FeatureVisibilityPolicy visibilityPolicy = FeatureVisibilityPolicy.getDefault();
    
    /** The naming policy to use to display scoped entity names. */
    private ScopedEntityNamingPolicy namingPolicy = ScopedEntityNamingPolicy.FULLY_QUALIFIED;
    
    /**
     * Returns the set of all available data objects that will be used to create
     * list items. Subclasses override this to provide whatever set of objects
     * they want. Note that a corresponding list entry type for the data type must
     * exist, otherwise an exception will be thrown when the list entries are created.
     * @return Set of Object
     */
    protected abstract Set<?> getDataObjects();
    
    /**
     * Returns new IntellicutInfo for a given list entry. If the entry should
     * not appear in the list, then this method should return null. Subclasses override
     * this to not create list items for entities they do not want to appear.
     * @param listEntry the list entry for which to get intellicut info
     * @return the intellicut info for the given list entry
     */
    protected abstract IntellicutInfo getIntellicutInfo(IntellicutListEntry listEntry);
    
    /**
     * Returns a new IntellicutListEntry for a data object. The default implementation
     * supports GemEntity, CollectorGem and TypeExpr data object types. Subclasses override
     * this if they want to add support for additional data object types.
     * @param dataObject the data object to get a list entry for
     * @return the list entry for the data object. This method should never return null.
     * @throws IllegalArgumentException if the data object type is not supported
     */
    protected IntellicutListEntry getNewListEntry(Object dataObject) {
        
        if (dataObject instanceof GemEntity) {
            return new GemEntityListEntry((GemEntity) dataObject, namingPolicy);
            
        } else if (dataObject instanceof CollectorGem) {
            return new CollectorGemListEntry((CollectorGem) dataObject);
            
        } else if (dataObject instanceof TypeExpr) {
            return new TypeExprListEntry((TypeExpr) dataObject, namingPolicy);
        }
        
        throw new IllegalArgumentException("data object not supported: " + dataObject);
    }
    
    /**
     * Filters gems based on reference frequency only.  Gems whose reference frequency
     * are in the top fifth are both Likely and Best. 
     * 
     * @param listEntry the list entry to check
     * @param filterLevel the filter level to use
     * @return whether or not the given list entry passes the specified filter
     */
    public boolean passesFilter(IntellicutListEntry listEntry, FilterLevel filterLevel) {

        if (filterLevel == null) {
            throw new NullPointerException("filterLevel must not be null in IntellicutListModelAdapter.passesFilter");
        }

        // Filter based on reference frequency
        if (filterLevel == FilterLevel.SHOW_ALL) {
            return true;
        } else {
            return listEntry.getIntellicutInfo().isReferenceFrequencyInTopFifth();
        }
    }
    
    /**
     * @param namingPolicy the naming policy to use to display scoped entity names
     */
    public void setNamingPolicy(ScopedEntityNamingPolicy namingPolicy) {
        this.namingPolicy = namingPolicy;
    }
    
    /**
     * @return the naming policy used by this adapter to display scoped entity names
     */
    public ScopedEntityNamingPolicy getNamingPolicy() {
        return namingPolicy;
    }
    
    /**
     * @param visibilityPolicy the visibility policy to use to decide what items to display
     */
    public void setVisibilityPolicy(FeatureVisibilityPolicy visibilityPolicy) {
        this.visibilityPolicy = visibilityPolicy;
    }
    
    /**
     * @return the visibility policy used by this adapter to decide what items to display
     */
    public FeatureVisibilityPolicy getVisibilityPolicy() {
        return visibilityPolicy;
    }
    
    /**
     * Includes the set of specified object in the list of data object to be loaded by the model.
     * This must be called before the model is loaded the first time.
     * @param additionalDataObjects a set of Gems or GemEntities to be added
     */
    public void addAdditionalDataObjects(Set<? extends Gem> additionalDataObjects) {
        this.additionalDataObjects.addAll(additionalDataObjects);
    }
    
    /**
     * Clears all entries that have been loaded. Calling this will cause the adapter
     * to reload entries the next time the model that uses it is refreshed.
     */
    public void clear() {
        intellicutEntrySet.clear();
        intellicutEntryMap.clear();
    }
    
    /**
     * Performs the actual loading of the list items.
     */
    public void load() {
        // Clear previously loaded stuff
        clear();
        
        // Load entries for entities from the perspective.
        loadListEntries(getDataObjects());
        loadListEntries(additionalDataObjects);
    }
    
    /**
     * Loads new list entries from a given data set and puts them into the result collection.
     * This method also resolves ambiguities between entities with same displayed string.
     * @param sourceDataSet the source data set from which to create them
     */        
    private void loadListEntries(Set<?> sourceDataSet) {
    
        SortedSet<IntellicutListEntry> additionalEntrySet = new TreeSet<IntellicutListEntry>(intellicutEntrySet.comparator());
        int[] referenceFrequencies = new int[sourceDataSet.size()];
        int[] sameNonpolymorphicTypeReferenceFrequencies = new int[sourceDataSet.size()];
        Map<Object, IntellicutListEntry> additionalEntryMap = new HashMap<Object, IntellicutListEntry>();
        
        // First gather a list of all the IntellicutInfo objects, keeping track
        // of the reference frequencies of each in referenceFrequencies (so that
        // we can sort them later)
        Arrays.fill(referenceFrequencies, 0);
        Arrays.fill(sameNonpolymorphicTypeReferenceFrequencies, 0);
        int numReferenceFrequencies = 0;
        int numSameNonpolymorphicType = 0;
    
        for (final Object dataObject : sourceDataSet) {
            IntellicutListEntry listEntry = getNewListEntry(dataObject);
            IntellicutInfo info = getIntellicutInfo(listEntry);
            if (info != null) {
                listEntry.setIntellicutInfo(info);
                
                additionalEntrySet.add(listEntry);
                additionalEntryMap.put(listEntry.getData(), listEntry);
                
                referenceFrequencies[numReferenceFrequencies] = info.getReferenceFrequency();
                numReferenceFrequencies++;
                
                if (info.isSameNonpolymorphicType()) {
                    sameNonpolymorphicTypeReferenceFrequencies[numSameNonpolymorphicType] = info.getReferenceFrequency();
                    numSameNonpolymorphicType++;
                }
            }
        }
        
        // Sort reference frequencies to determine the 80th percentile
        Arrays.sort(referenceFrequencies);
        int referenceFrequencyThresholdIdx = (referenceFrequencies.length-1) - (numReferenceFrequencies/(100 / REFERENCE_FREQUENCY_PERCENTILE_THRESHOLD));
        
        // Sort reference frequencies of the gems with the same nonpolymorphic type to 
        // determine the threshold for the top 10 values (if there are more than 10 values)
        int sameNonpolymorphicTypeReferenceFrequencyThreshold = 0;
        if (numSameNonpolymorphicType > SAME_NONPOLYMORPHIC_TYPE_REFERENCE_FREQUENCY_RANK) {
            Arrays.sort(sameNonpolymorphicTypeReferenceFrequencies);
            sameNonpolymorphicTypeReferenceFrequencyThreshold = sameNonpolymorphicTypeReferenceFrequencies[(sameNonpolymorphicTypeReferenceFrequencies.length - 1) - 10];
        }
        
        // Step through again to normalize the heuristic outputs
        for (final IntellicutListEntry listEntry : additionalEntrySet) {
            listEntry.getIntellicutInfo().setTopFifthThresholds(referenceFrequencies[referenceFrequencyThresholdIdx]);
            listEntry.getIntellicutInfo().setSameNonpolymorphicTypeReferenceFrequencyThreshold(sameNonpolymorphicTypeReferenceFrequencyThreshold);
        }
        
        if (intellicutEntrySet.isEmpty()) {
            intellicutEntrySet.addAll(additionalEntrySet);
            return;
        }
        
        // Entries in the original set may be modified, and this set tracks them 
        SortedSet<IntellicutListEntry> modifiedOriginalEntries = new TreeSet<IntellicutListEntry>(intellicutEntrySet.comparator());
        
        // Lazily iterate through the existing entry set, while progressing
        // through the new entries. 
        Iterator<IntellicutListEntry> o = intellicutEntrySet.iterator();
        IntellicutListEntry originalEntry = o.next();
        for (final IntellicutListEntry newEntry : additionalEntrySet) {
            // Hop over original entries until we reach our a sort string equal/greater 
            // than that of the new entry's.
            while (o.hasNext() && newEntry.getSortString().compareToIgnoreCase(originalEntry.getSortString()) > 0) {
                originalEntry = o.next();
            }
            
            // If sort strings are equal, then there is a possibility the display strings are equal
            // Note: Displayed strings will not be equal if sort strings are different. 
            if ((newEntry.getSortString().compareToIgnoreCase(originalEntry.getSortString()) == 0) &&
                (newEntry.getDisplayString().compareToIgnoreCase(originalEntry.getDisplayString()) == 0)) {
                
                resolveDisplayAmbiguity(newEntry, originalEntry);
                modifiedOriginalEntries.add(originalEntry);
            }
        }
        
        // Refresh the modified entry in the original set
        intellicutEntrySet.removeAll(modifiedOriginalEntries);
        intellicutEntrySet.addAll(modifiedOriginalEntries);
        
        // Now add the new entries to this set
        intellicutEntrySet.addAll(additionalEntrySet);
    }
    
    /**
     * Resolves an ambiguity in the displayed string between two list entities.
     * 
     * This method is invoked when an entity must be added to the intellicut list,
     * but its display string equals that of another entity. The purpose of this
     * method is to modify one or both of the entities in order to resolve 
     * this ambiguity. It is assumed that the resolution does not cause extra
     * ambiguities.
     * 
     * If resolution is not possible, the new entry will be added to the list 
     * entries as is, and the list will contain two entries with identical 
     * display strings.
     * 
     * Note: The ambiguity finding mechanism assumes that it is not possible for
     * resolution to cause further ambiguities, as these would not be detected. 
     * 
     * @param newEntry entry which is to be added to the list
     * @param existingEntry entry already existing in the list
     */
    protected void resolveDisplayAmbiguity(IntellicutListEntry newEntry, IntellicutListEntry existingEntry) {
        
        // By default, no action is taken to resolve the ambiguity
        return;
    }
    
    /**
     * @return the set of all loaded list entries sorted in alphabetical order
     */
    public SortedSet<IntellicutListEntry> getListEntrySet() {
        return Collections.unmodifiableSortedSet(intellicutEntrySet);
    }
    
    /**
     * @param data the data object to get a list entry for
     * @return the list entry that corresponds to the given data object or null if no such entry
     */
    public IntellicutListEntry getListEntryForData(Object data) {
        return intellicutEntryMap.get(data);
    }
    
    /**
     * @param listEntry the list entry to get the tooltip text for
     * @param invoker the component to get the tooltip text for
     * @return the tooltip text for the list entry
     */
    public String getToolTipTextForEntry(IntellicutListEntry listEntry, JComponent invoker) {
        
        Object data = listEntry.getData();
        
        if (data instanceof GemEntity) {
            return ToolTipHelpers.getEntityToolTip(((GemEntity)data), namingPolicy, invoker);
        }
            
        return listEntry.getDisplayString();
    }

    /**
     * Returns a set of gems that visible to the user.  The visibility of a gem is determined
     * by several checks.  First, the module containing the gem has to be visible from the
     * perspective.  For example, the module has to be referenced by the current working module.
     * Second, the module has to go through the visibility predicate check to ensure its
     * visibility.  The third check is to ensure that the given gem is a public gem, or it
     * is part of the working module.
     * @param perspective
     */
    protected Set<GemEntity> getVisibleGemsFromPerspective(Perspective perspective) {
        
        Set<GemEntity> entities = new LinkedHashSet<GemEntity>();
        List<MetaModule> modules = perspective.getVisibleMetaModules();
        
        for (int i = 0, size = modules.size(); i < size; i++) {
            
            MetaModule module = modules.get(i);
            
            if (visibilityPolicy.isModuleVisible(module)) {
                // TODO currently we don't support metadata expert/hidden/preferred flag
                // checking for gem entities, so as long as the module is visible,
                // all public gems are visible
                entities.addAll(perspective.getVisibleGemEntities(module));
            }
        }
        
        return entities;
    }
    
    /**
     * Returns a new IntellicutInfo if the output type of the given entry data can be connected
     * to the specified type expression. Returns null if the connection is not possible.
     * @param data the IntellicutListEntry for the new entry
     * @param destTypeExpr the type that the data must connect to
     * @param typeCheckInfo the type check info to use
     * @param sourceMetrics The WorkspaceSourceMetrics provider to fetch reference frequency with.     
     * @return new IntellicutInfo or null if connection not possible
     */
    protected static IntellicutInfo getIntellicutInfo(IntellicutListEntry data, TypeExpr destTypeExpr, TypeCheckInfo typeCheckInfo, boolean allowFreeArguments, SourceMetrics sourceMetrics) {
        return getIntellicutInfo(data, new TypeExpr[] { destTypeExpr }, typeCheckInfo, allowFreeArguments, sourceMetrics);
    }

    /**
     * Get the reference frequency for a specific list entry
     * @param sourceMetrics the metrics store to fetch the frequency from
     * @param candidate the list entry to fetch the frequency for
     * @return The number of times the specified gem is referred to in the body of other gems
     */
    protected static int getReferenceFrequency(SourceMetrics sourceMetrics, IntellicutListEntry candidate) {
        if (sourceMetrics != null && candidate instanceof GemEntityListEntry) {
            
            GemEntityListEntry gemCandidate = (GemEntityListEntry)candidate;
            QualifiedName name = gemCandidate.getEntity().getName();
            return sourceMetrics.getGemReferenceFrequency(name);
        }
        return 0;
    }
    
    /**
     * Returns a new IntellicutListEntry if the output type of the given entry data can be connected
     * to at least one of the specified type expression. Returns null if the connection is not possible.
     * @param outputListEntry the IntellicutListEntry
     * @param destTypeExprs the types available for the data to connect to
     * @param typeCheckInfo the type check info to use   
     * @param allowFreeArguments whether or not the entry data is allowed to have free arguments
     * @param sourceMetrics The WorkspaceSourceMetrics provider to fetch reference frequency with.     
     * @return new IntellicutListEntry or null if connection not possible
     */
    protected static IntellicutInfo getIntellicutInfo(IntellicutListEntry outputListEntry, TypeExpr[] destTypeExprs, TypeCheckInfo typeCheckInfo, boolean allowFreeArguments, SourceMetrics sourceMetrics) {
        TypeExpr outputType = outputListEntry.getResultType();
        int dependents = getReferenceFrequency(sourceMetrics, outputListEntry);
        
        // Code gems return null if no code is entered in the code editor.
        if (outputType == null || destTypeExprs == null) {
            return null;
        }

        ModuleTypeInfo currentModuleTypeInfo = typeCheckInfo.getModuleTypeInfo();

        boolean targetIsNonParametric = !outputType.isPolymorphic();
        boolean exactMatch = false;
        
        // Check whether any of the provided types are an exact match
        for(int typeN = 0, nTypes=destTypeExprs.length; typeN < nTypes; ++typeN) {
            if (targetIsNonParametric && outputType.sameType(destTypeExprs[typeN])) {
                exactMatch = true;
                break;
            }
        }
        
        // Check to see if the destTypeExpr and outputType are 'connectable'.
        if (!outputListEntry.isAutoBurnable()) {
            
            // Try connecting without burning.
            // Find the maximum closeness value for the types provided.
            int noBurnTypeCloseness = -1;
            for (int typeN = 0, nTypes = destTypeExprs.length; typeN < nTypes; ++typeN) {
                int closenessForType = TypeExpr.getTypeCloseness(destTypeExprs[typeN], outputType, currentModuleTypeInfo);
    
                if (closenessForType > noBurnTypeCloseness) {
                    noBurnTypeCloseness = closenessForType;
                }
            }            

            if (noBurnTypeCloseness != -1 && (allowFreeArguments || !outputListEntry.hasDisconnectedInputs())) {
                return new IntellicutInfo(AutoburnUnifyStatus.NOT_NECESSARY, -1, noBurnTypeCloseness, exactMatch, dependents);
            }
            
            return null;

        } 
        
        // Try connecting with auto burning.
        // Find the autoburning for the type which provides the maximum closeness value.
        AutoburnLogic.AutoburnInfo autoburnInfo = null;
        
        int noBurnTypeCloseness = -1;

        for (int typeN = 0, nTypes = destTypeExprs.length; typeN < nTypes; ++typeN) {
            AutoburnLogic.AutoburnInfo autoburnInfoForType = AutoburnLogic.getAutoburnInfo(destTypeExprs[typeN], outputListEntry.getTypePiecesForBurning(), typeCheckInfo);
            
            noBurnTypeCloseness = Math.max(noBurnTypeCloseness, autoburnInfoForType.getNoBurnTypeCloseness());
            
            if (autoburnInfo == null || autoburnInfoForType.getMaxTypeCloseness() > autoburnInfo.getMaxTypeCloseness()) {
                autoburnInfo = autoburnInfoForType;
            }
        }

        if (autoburnInfo != null) {
            AutoburnUnifyStatus autoburnUnifyStatus = autoburnInfo.getAutoburnUnifyStatus();
            
            if (autoburnUnifyStatus != AutoburnUnifyStatus.NOT_POSSIBLE &&
                    (allowFreeArguments || canBurnAllArguments(outputListEntry, autoburnInfo))) {
                
                return new IntellicutInfo(autoburnInfo.getAutoburnUnifyStatus(), autoburnInfo.getMaxTypeCloseness(), noBurnTypeCloseness, exactMatch, dependents);
            }
        }        
        
        return null;
    }

    /**
     * Returns a new IntellicutListEntry if any of the input types of the given list entry data
     * can be connected to the specified type expression. Returns null if the connection is not possible.
     * @param inputListEntry the IntellicutListEntry object
     * @param sourceTypeExpr the type that the entity must connect to
     * @param moduleTypeInfo the type info of the context module
     * @return new IntellicutListEntry or null if connection not possible
     */
    protected static IntellicutInfo getIntellicutInfo(IntellicutListEntry inputListEntry, TypeExpr sourceTypeExpr, ModuleTypeInfo moduleTypeInfo, SourceMetrics sourceMetrics) {

        TypeExpr[] typePieces = inputListEntry.getArgumentTypePieces(); 

        // No arguments means we can't connect to it.
        if (typePieces == null) {
            return null;
        }
        
        int references = getReferenceFrequency(sourceMetrics, inputListEntry); 

        boolean foundCandidate = false;
        int finalClosenessForType = 0;
        boolean finalExactMatch = false;
        boolean sourceIsNonParametric = !sourceTypeExpr.isPolymorphic();
        
        for (final TypeExpr typeExp : typePieces) {
            
            // Check to see if the sourceTypeExpr and one of the type pieces are 'connectable'.
            // We need to check the whole list, because we might encounter a "connectable but not exact"
            // piece before we encounter a "connectable exact match" piece.
            if (TypeExpr.canUnifyType(sourceTypeExpr, typeExp, moduleTypeInfo)) {
                
                foundCandidate = true;
                
                int closenessForType = TypeExpr.getTypeCloseness(sourceTypeExpr, typeExp, moduleTypeInfo);
                boolean exactMatch = sourceIsNonParametric && sourceTypeExpr.sameType(typeExp);
                
                if (exactMatch || closenessForType > finalClosenessForType) {
                    finalClosenessForType = closenessForType;
                    finalExactMatch = exactMatch || finalExactMatch;
                }
            } 
        }               

        if (foundCandidate) {
            return new IntellicutInfo(AutoburnUnifyStatus.NOT_NECESSARY, -1, finalClosenessForType, finalExactMatch, references);
        } else {
            return null;
        }
    }
    
    /**
     * Checks if the given autoburn info indicates that all arguments of the given list entry data can be burned.
     * @param listEntry the list entry
     * @param autoburnInfo the autoburn info
     * @return true if all arguments can be burned
     */
    private static boolean canBurnAllArguments(IntellicutListEntry listEntry, AutoburnLogic.AutoburnInfo autoburnInfo) {
        
        List<BurnCombination> burnCombos = autoburnInfo.getBurnCombinations();
        
        if (burnCombos.isEmpty()) {
            return false;
        }
        
        AutoburnLogic.BurnCombination lastBurnCombo = burnCombos.get(burnCombos.size() - 1); 

        return lastBurnCombo.getInputsToBurn().length == listEntry.getTypePiecesForBurning().length - 1;
    }
}
