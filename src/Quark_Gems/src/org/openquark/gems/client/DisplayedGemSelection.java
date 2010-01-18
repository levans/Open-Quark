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
 * DisplayedGemSelection.java
 * Creation date: October 21st 2002
 * By: Ken Wong
 */

package org.openquark.gems.client;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openquark.cal.services.GemEntity;
import org.openquark.cal.valuenode.ValueNode;
import org.openquark.gems.client.CollectorGem.CollectingPart;
import org.openquark.util.Pair;


/**
 * This class is used to handle the copy/cut/paste actions that occur on the tabletop. It is a definition
 * of a transferable object for Displayed Gems. To make a copy of a selection, simply create a new instance of this
 * object, and then get the transferdata and create a new object.
 * 
 * Creation Date: October 21st 2002
 * @author Ken Wong
 *
 */
class DisplayedGemSelection implements ClipboardOwner, Transferable {
    
    /** 
     * This is the basic dataflavor of this transferable.
     * It represents the Pair: (DisplayedGem[], List of Connection)
     */
    public static final DataFlavor CONNECTION_PAIR_FLAVOR = new DataFlavor(Pair.class, "DisplayedGemConnectionPair");
    
    /** Use this dataflavor to get the original location of the gems 
     *  Represents a Point object. */
    public static final DataFlavor ORIGINAL_LOCATION_FLAVOR = new DataFlavor(Point.class, "DisplayedGemOriginalLocation");
    
    /** the list of supported flavors */
    private static final DataFlavor[] SUPPORTED_FLAVORS = {CONNECTION_PAIR_FLAVOR, ORIGINAL_LOCATION_FLAVOR};
    
    /** a reference to the gemCutter */
    private final GemCutter gemCutter; 
    
    /** a copy of the originals which we use to make more copies */
    private final DisplayedGem[] originalMolds;

    /** The origin of the bounds for the gems being copied. */
    private final Point originalLocation;
    
    /**
     * A comparator that sorts code and collector gems relative to each other, 
     * but doesn't apply any particular sort order to other gems.
     */
    private static final Comparator<DisplayedGem> codeCollectorNameComparator = new Comparator<DisplayedGem>() {

        public int compare(DisplayedGem o1, DisplayedGem o2) {
            Gem gem1 = o1.getGem();
            Gem gem2 = o2.getGem();

            String name1;
            if (gem1 instanceof CodeGem) {
                name1 = ((CodeGem)gem1).getUnqualifiedName();
            
            } else if (gem1 instanceof CollectorGem) {
                name1 = ((CollectorGem)gem1).getUnqualifiedName();
            
            } else {
                name1 = "";
            }
            
            String name2;
            if (gem2 instanceof CodeGem) {
                name2 = ((CodeGem)gem2).getUnqualifiedName();
            
            } else if (gem2 instanceof CollectorGem) {
                name2 = ((CollectorGem)gem2).getUnqualifiedName();
            
            } else {
                name2 = "";
            }
            
            return name1.compareToIgnoreCase(name2);
        }
    };

    
    /** 
     * Default constructor for this transferable. 
     * Actual creation of transfer objects does not occur until the 'get' call.
     * @param displayedGems the gems being copied/cut
     * @param gemCutter the Gem Cutter    
     */
    public DisplayedGemSelection(DisplayedGem[] displayedGems, GemCutter gemCutter) {
        this.gemCutter = gemCutter;
       
        Rectangle gemBounds = displayedGems[0].getBounds();
        for (int i = 1; i < displayedGems.length; i++) {
            gemBounds.add(displayedGems[i].getBounds());
        }
        
        Point currentOrigin = gemCutter.getTableTop().getOriginalOrigin();
        originalLocation = new Point(gemBounds.x - currentOrigin.x, gemBounds.y - currentOrigin.y);

        // Build the 'mold', based on the top-left corner of the gem bounds.
        Pair<DisplayedGem[], List<Connection>> originals = getNewCopy(displayedGems, gemBounds.getLocation(), true);
        originalMolds = originals.fst();        
        List<Connection> connections = originals.snd();
        
        // Sort so that the assignment of the numerical suffix to the collector and code gem names will 
        // be in the same order as the originals on the tabletop.
        Collections.sort(Arrays.asList(originalMolds), codeCollectorNameComparator);
        
        // Bind the 'mold' connections.
        for (final Connection connection : connections) {
            Connection conn = connection;
            Gem.PartInput dest = conn.getDestination();
            Gem.PartOutput source = conn.getSource();
            dest.bindConnection(conn);
            source.bindConnection(conn);
        }
    }
    
    
    /**
     * Creates another copy of the originals. stored with the gems as the first item in the pair, and the
     * connections stored as the second object in the pair.
     * @param displayedGemOriginals the displayed gems to copy.
     * @param referenceOrigin the point to use as the origin of the copy operation.  
     *   Copied gem locations are adjusted so that the reference origin appears at (0,0).
     * @param asOriginals if true, makes a perfect clone (collector names are not changed to work on the TableTop). 
     *                    if false, the resulting copy works on the TableTop.
     * @return Pair the copies of displayed gems and connections.
     */
    private Pair<DisplayedGem[], List<Connection>> getNewCopy(DisplayedGem[] displayedGemOriginals, Point referenceOrigin, boolean asOriginals) {
        
        TableTop tableTop = gemCutter.getTableTop();
        
        // Get the gems to copy.
        Set<Gem> gemsToCopySet = new HashSet<Gem>();
        for (final DisplayedGem dGem : displayedGemOriginals) {
            gemsToCopySet.add(dGem.getGem());
        }

        // Create a new list the same size as the old one
        DisplayedGem[] newDisplayedGems = new DisplayedGem[displayedGemOriginals.length];
        
        // Create a list of the connections
        List<Connection> newConnections = new ArrayList<Connection>();
        
        // A map from original gem to gem copy.
        Map<Gem, Gem> oldToNewGemMap = new HashMap<Gem, Gem>();
        
        // To keep track of the names we've used up so far
        Set<String> collectorsCodeGemsNames = new HashSet<String>(tableTop.getGemGraph().getCodeGemsCollectorsNames());        
        List<Integer> emitterIndices = new ArrayList<Integer>();
        
        // The original collector gems for which copies can be emitted by emitter copies 
        //   (rather than emitting the original collector).
        Set<Gem> originalCollectorsEmittableWhenCopied = new HashSet<Gem>();
        
        Set<CollectorGem> copiedCollectors = new HashSet<CollectorGem>();
        
        // So for every display gem selected...
        for (int i = 0; i < displayedGemOriginals.length; i++) {
            // Get the gem behind the displaygem
            Gem gem = displayedGemOriginals[i].getGem();
            
            Point oldLocation = displayedGemOriginals[i].getLocation();
            Point location = new Point(oldLocation.x - referenceOrigin.x, oldLocation.y - referenceOrigin.y);
           
            if (gem instanceof FunctionalAgentGem) {
                // get the SC behind this gem and produce a new one
                GemEntity gemEntity = ((FunctionalAgentGem)gem).getGemEntity();
                newDisplayedGems[i] = tableTop.createDisplayedFunctionalAgentGem(location, gemEntity);
                
            } else if (gem instanceof CodeGem) {
                 // Clone this codeGem and create a new DisplayedGem to associate with it
                 CodeGem clone = ((CodeGem)gem).makeCopy();
                 // If not used as 'molds' then we want to find a new valid name.
                 if (!asOriginals) {
                     String oldName = clone.getUnqualifiedName();
                     String newName = Gem.getNextValidName(oldName, collectorsCodeGemsNames);
                     clone.setName(newName);
                 }
                 
                 newDisplayedGems[i] = tableTop.createDisplayedGem(clone, location);
                 newDisplayedGems[i].updateDisplayedInputs();
                 collectorsCodeGemsNames.add(clone.getUnqualifiedName());
                 
            } else if (gem instanceof RecordFieldSelectionGem) {
                // Clone the RecordFieldSelectionGem and make a new DisplayedGem to associate with it
                RecordFieldSelectionGem clone = ((RecordFieldSelectionGem)gem).makeCopy();
                
                newDisplayedGems[i] = tableTop.createDisplayedGem(clone, location);
             
            } else if (gem instanceof CollectorGem) {
                
                // Clone the collector and create a new display gem to associate with it.
                CollectorGem collectorGem = ((CollectorGem)gem).makeCopy();
                
                // If not used as 'molds', then we want to find a new name
                if (!asOriginals) {
                    // If a collector of this name already exists, then we want to search for the
                    // next available name. Such that if "value2" is the name of the original, and
                    // then we find its 'baseName' which is "value", then we count upwards beginning from
                    // the numerical suffix of the original (which happens to be 2), and so we'll try
                    // "value3" and "value4", until a name is available.
                    String oldName = collectorGem.getUnqualifiedName();
                    String newName = Gem.getNextValidName(oldName, collectorsCodeGemsNames);
                    collectorGem.setName(newName);
                }
                
                newDisplayedGems[i] = tableTop.createDisplayedGem(collectorGem, location);
                collectorsCodeGemsNames.add(collectorGem.getUnqualifiedName());

                copiedCollectors.add((CollectorGem)gem);
                
                // Calculate whether simultaneously copied emitters should emit the original collector or the copy.
                if (shouldEmitCollectorCopy((CollectorGem)gem, gemsToCopySet)) {
                    originalCollectorsEmittableWhenCopied.add(gem);
                }
                
            } else if (gem instanceof ReflectorGem) {
                // If it is an emitter, we don't deal with it until the end, because we want to know whether or not 
                // it should emit its associated collector, or a copy of the collector.  But we can't do this until 
                // we loop through all of the collectors. So we'll store their indices and hold off until after the loop.
                emitterIndices.add(Integer.valueOf(i));
                continue;
                
            } else if (gem instanceof ValueGem) {
                // Get The associated value node
                ValueNode valueNode = ((ValueGem)gem).getValueNode();
                                
                // Copy the valueNode but link it with a different typeExpr
                ValueNode valueNodeClone = valueNode.copyValueNode();
                
                // Create the new valueGem
                ValueGem valueGemClone = new ValueGem(valueNodeClone);
                
                // Build the associated displayedGem
                newDisplayedGems[i] = tableTop.createDisplayedGem(valueGemClone, location);
                
            } else if (gem instanceof RecordCreationGem) {
                RecordCreationGem gemClone = ((RecordCreationGem)gem).makeCopy();
                
                // Build the associated displayedGem
                newDisplayedGems[i] = tableTop.createDisplayedGem(gemClone, location);
                
            } else {
                throw new IllegalArgumentException("Unknown gem type: " + gem.getClass());
            }
            
            oldToNewGemMap.put(gem, newDisplayedGems[i].getGem());
        }
        
        // Go through the emitters that we ignored earlier
        Set<ReflectorGem> emittersToEmitCollectorCopies = new HashSet<ReflectorGem>();      // (Set of EmitterGem) emitters whose copies will emit collector copies.
        for (int i = 0, nEmitters = emitterIndices.size(); i < nEmitters; ++i) {
       
            int originalIndex = emitterIndices.get(i).intValue();
            
            // Get the emitters
            ReflectorGem reflectorGem = (ReflectorGem)displayedGemOriginals[originalIndex].getGem();
            Point location = displayedGemOriginals[originalIndex].getLocation();
           
            // Shift their locations slightly
            location.x -= referenceOrigin.x;
            location.y -= referenceOrigin.y;
            
            CollectorGem reflectorCollector = reflectorGem.getCollector();
            
            // Determine whether we should map the reflector to the original collector or the copy.
            // Note that the check ensures that the number of reflected inputs in the copy will be the same as in the original.
            if (originalCollectorsEmittableWhenCopied.contains(reflectorCollector)) {
                // Get the collector copy and create an emitter with the appropriate number of inputs.
                // Note that this emitter will be in an inconsistent state (until arguments state is set).
                CollectorGem collectorGem = (CollectorGem)oldToNewGemMap.get(reflectorCollector);
                ReflectorGem emitterGemCopy = new ReflectorGem(collectorGem, reflectorGem.getNInputs());
                newDisplayedGems[originalIndex] = tableTop.createDisplayedGem(emitterGemCopy, location);
                emittersToEmitCollectorCopies.add(reflectorGem);

            } else {
                // Create an emitter for the original collector.
                newDisplayedGems[originalIndex] = tableTop.createDisplayedReflectorGem(location, reflectorCollector);
            }

            oldToNewGemMap.put(reflectorGem, newDisplayedGems[originalIndex].getGem());
        }
        
        // Populate argument state for emitters emitting collector copies. 
        for (final ReflectorGem originalEmitterGem : emittersToEmitCollectorCopies) {
            ReflectorGem emitterGemCopy = (ReflectorGem)oldToNewGemMap.get(originalEmitterGem);
            emitterGemCopy.copyArgumentState(originalEmitterGem, oldToNewGemMap);
        }
        
        // Set collector copy argument state for any copied collectors.
        for (final CollectorGem copiedCollector : copiedCollectors) {
            CollectorGem collectorCopy = (CollectorGem)oldToNewGemMap.get(copiedCollector);
            
            // Iterate over copied target arguments, and set their targets if possible.
            for (final Gem.PartInput targetingInput : copiedCollector.getTargetArguments()) {
                if (canTargetGemCopy(targetingInput, gemsToCopySet)) {
                    Gem.PartInput inputCopy = oldToNewGemMap.get(targetingInput.getGem()).getInputPart(targetingInput.getInputNum());
                    collectorCopy.addArgument(inputCopy);
                }
            }
        }
        
        for (int i = 0; i < displayedGemOriginals.length; i++) {
            // Get the gem behind the displayed gem
            Gem oldGem = displayedGemOriginals[i].getGem();
            Gem newGem = newDisplayedGems[i].getGem();

            // loop through all the arguments of this gem so connections and metadatas and burns are recorded properly.
            for (int j = 0, nArgs = oldGem.getNInputs(); j < nArgs; j++) {
                
                Gem.PartInput newPartInput = newGem.getInputPart(j);
                Gem.PartInput oldPartInput = oldGem.getInputPart(j);

                // Copy the metadata and name. We don't do this for CollectingParts since they
                // don't have any metadata and automatically synchronize their name with the collector name.
                if (!(newPartInput instanceof CollectingPart)) {
                    newPartInput.setArgumentName(oldPartInput.getArgumentName());
                    newPartInput.setDesignMetadata(oldPartInput.getDesignMetadata());
                }
                
                // If it is burnt, then we want to have the same thing in the copy.
                if (oldPartInput.isBurnt()) { 
                    newPartInput.setBurnt(true);
                }
            }
            
            // If the output was connected, reconnect the copy if the input gem was also copied.
            Gem.PartOutput originalOutput = oldGem.getOutputPart();
            if (originalOutput != null && originalOutput.isConnected()) {
                Gem.PartInput oldDestination = originalOutput.getConnection().getDestination();
                Gem destinationGemCopy = oldToNewGemMap.get(oldDestination.getGem());
                
                if (destinationGemCopy != null) {
                    Gem.PartOutput newOutput = oldToNewGemMap.get(oldGem).getOutputPart();
                    Gem.PartInput newInput = destinationGemCopy.getInputPart(oldDestination.getInputNum());
                    Connection connection = new Connection(newOutput, newInput);
                    newOutput.bindConnection(connection);
                    newInput.bindConnection(connection);
                    newConnections.add(connection);
                }
            }
        }
        
        // return the new displayed gems and connections
        return new Pair<DisplayedGem[], List<Connection>>(newDisplayedGems, newConnections);
    }
    
    /**
     * Return whether emitters for a given collector should emit the copy of the collector, or the original collector.
     * @param collectorGemToCopy the collector gem being copied.
     * @param gemsToCopy the gems being copied.
     * @return whether emitters for a given collector should emit the copy of the collector, or the original collector.
     * If true, the emitter should emit the definition of the copy of the collector.  If false, it should emit the definition of the original.
     * False if the collector is not being copied.  
     * True if any emitter inputs are connected, reflected inputs are all copied, and those inputs are all connected to the collector via
     *   copied gems.
     */
    private static boolean shouldEmitCollectorCopy(CollectorGem collectorGemToCopy, Set<Gem> gemsToCopy) {
        
        // Check that the collector gem is being copied.
        if (!gemsToCopy.contains(collectorGemToCopy)) {
            return false;
        }

        // If the collector doesn't reflect any inputs, we can return true right away..
        List<Gem.PartInput> reflectedInputs = collectorGemToCopy.getReflectedInputs();
        int nReflectedInputs = reflectedInputs.size();
        if (nReflectedInputs == 0) {
            return true;
        }
        
        // Get any emitters for collectorGemToCopy being copied..
        Set<ReflectorGem> emittersToCopy = new HashSet<ReflectorGem>();
        
        // Iterate over the collector's emitters - in the worst case, this will be a lot better than iterating over gemsToCopy.
        for (final ReflectorGem emitterGem : collectorGemToCopy.getReflectors()) {
            if (gemsToCopy.contains(emitterGem)) {
                emittersToCopy.add(emitterGem);
            }
        }
        
        // Check for no emitters being copied..
        if (emittersToCopy.isEmpty()) {
            return true;
        }

        // Uncomment this section to return true when all emitter inputs are unconnected.
        // This may result in the emitter changing its shape when copied.
/*
        // Check whether any emitter inputs being copied are connected.
        boolean anyEmitterInputsConnected = false;
        for (Iterator it = emittersToCopy.iterator(); it.hasNext(); ) {
            ReflectorGem emitterGem = (ReflectorGem)it.next();
            for (int i = 0; i < nReflectedInputs; i++) {
                if (emitterGem.getInputPart(i).isConnected()) {
                    anyEmitterInputsConnected = true;
                    break;
                }
            }
        }
        
        if (!anyEmitterInputsConnected) {
            return true;
        }
/**/
        
        // Return true if all reflected inputs are being copied, and connect to the collector being copied via copied gems
        //   (ie. whether the reflected input copy can be retargeted to the collector's copy).
        
        // Check reflected inputs for connectivity with the collector being copied.
        for (final Gem.PartInput reflectedInput : reflectedInputs) {
            if (!canTargetGemCopy(reflectedInput, gemsToCopy)) {
                return false;
            }
        }
        
        
        // If we're here, all reflected inputs are connected to the target collector by gems which are also being copied.
        return true;
    }

    /**
     * Return whether a copy of the given input can target the target copy.
     * True if all gems between the input and the target are being copied.
     * @param inputToCopy the input being copied.
     * @param gemsToCopy the gems being copied.
     * @return whether a copy of the given input can target the target copy.
     */
    private static final boolean canTargetGemCopy(Gem.PartInput inputToCopy, Set<Gem> gemsToCopy) {
        CollectorGem inputTarget = GemGraph.getInputArgumentTarget(inputToCopy);
        Gem gemToCheck = inputToCopy.getGem();
        for (;;) {
            // If it's not being copied, return false.
            if (gemToCheck == null || !gemsToCopy.contains(gemToCheck)) {
                return false;
            }
            
            if (gemToCheck == inputTarget) {
                return true;
            }

            // Get the ancestor / target.
            gemToCheck = (gemToCheck instanceof CollectorGem) ? ((CollectorGem)gemToCheck).getTargetCollectorGem() 
                                                              : gemToCheck.getOutputPart().getConnectedGem();
        } 
    }


    /**
     * @see java.awt.datatransfer.ClipboardOwner#lostOwnership(Clipboard, Transferable)
     */
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }

    /**
     * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
     */
    public DataFlavor[] getTransferDataFlavors() {
        return SUPPORTED_FLAVORS;
    }

    /**
     * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(DataFlavor)
     */
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return (flavor.equals(CONNECTION_PAIR_FLAVOR) || flavor.equals(ORIGINAL_LOCATION_FLAVOR));
    }

    /**
     * @see java.awt.datatransfer.Transferable#getTransferData(DataFlavor)
     */
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {

        if (flavor.equals(CONNECTION_PAIR_FLAVOR)) {
            // Get a new copy of the information
            return getNewCopy(originalMolds, new Point(), false);
            
        } else if (flavor.equals(ORIGINAL_LOCATION_FLAVOR)){
            Point currentOrigin = gemCutter.getTableTop().getOriginalOrigin();
            return new Point(currentOrigin.x + originalLocation.x, currentOrigin.y + originalLocation.y);    
            
        } else {
            // Not supported.
            throw new UnsupportedFlavorException(flavor);
        }
    }
    
}
