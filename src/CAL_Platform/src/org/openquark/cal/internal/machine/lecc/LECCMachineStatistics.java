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
 * MachineStatistics.java
 * Creation date: Jun 23, 2006.
 * By: Joseph Wong
 */
package org.openquark.cal.internal.machine.lecc;

import java.util.List;

import org.openquark.cal.machine.MachineStatistics;


/**
 * An immutable value type for representing machine statistics.
 *
 * @author Joseph Wong
 */
public class LECCMachineStatistics implements MachineStatistics {
    
    /** The number of regular classes loaded by the class loaders associated with the modules in the program. */
    private final int nRegularClassesLoaded;
    /** The number of adjunct classes loaded by the class loaders associated with the modules in the program. */
    private final int nAdjunctClassesLoaded;
    /** The number of bytes for regular classes loaded by the class loaders associated with the modules in the program. */
    private final int nRegularClassBytesLoaded;
    /** The number of bytes for adjunct classes loaded by the class loaders associated with the modules in the program. */
    private final int nAdjunctClassBytesLoaded;
    
    /** Whether timing was performed (and therefore the timing values meaningful). */
    private final boolean containsTimings;
    /** The number of milliseconds spent in generating regular class file data by the class loaders associated with the modules in the program. */
    private final long generateRegularClassDataTimeMS;
    /** The number of milliseconds spent in generating adjunct class file data by the class loaders associated with the modules in the program. */
    private final long generateAdjunctClassDataTimeMS;
    /** The number of milliseconds spent in looking up regular class file data by the class loaders associated with the modules in the program. */
    private final long lookupRegularClassDataTimeMS;
    /** The number of milliseconds spent in looking up adjunct class file data by the class loaders associated with the modules in the program. */
    private final long lookupAdjunctClassDataTimeMS;
    /** The number of milliseconds spent in findClass() for regular classes by the class loaders associated with the modules in the program. */
    private final long regularFindClassTimeMS;
    /** The number of milliseconds spent in findClass() for adjunct classes by the class loaders associated with the modules in the program. */
    private final long adjunctFindClassTimeMS;
    
    /**
     * Constructs a MachineStatistics instance.
     * 
     * @param nRegularClassesLoaded
     *            the number of regular classes loaded by the class loaders associated with the modules in the program.
     * @param nAdjunctClassesLoaded
     *            the number of adjunct classes loaded by the class loaders associated with the modules in the program.
     * @param nRegularClassBytesLoaded
     *            the number of bytes for regular classes loaded by the class loaders associated with the modules in the program.
     * @param nAdjunctClassBytesLoaded
     *            the number of bytes for adjunct classes loaded by the class loaders associated with the modules in the program.
     * @param containsTimings
     *            whether timing was performed (and therefore the timing values meaningful).
     * @param generateRegularClassDataTimeMS
     *            number of milliseconds spent in generating regular class file data by the class loaders associated with the modules in the program.
     * @param generateAdjunctClassDataTimeMS
     *            number of milliseconds spent in generating adjunct class file data by the class loaders associated with the modules in the program.
     * @param lookupRegularClassDataTimeMS
     *            number of milliseconds spent in looking up regular class file data by the class loaders associated with the modules in the program.
     * @param lookupAdjunctClassDataTimeMS
     *            number of milliseconds spent in looking up adjunct class file data by the class loaders associated with the modules in the program.
     * @param regularFindClassTimeMS
     *            number of milliseconds spent in findClass() for regular classes by the class loaders associated with the modules in the program.
     * @param adjunctFindClassTimeMS
     *            number of milliseconds spent in findClass() for adjunct classes by the class loaders associated with the modules in the program.
     */
    public LECCMachineStatistics(
        int nRegularClassesLoaded, int nAdjunctClassesLoaded,
        int nRegularClassBytesLoaded, int nAdjunctClassBytesLoaded,
        boolean containsTimings,
        long generateRegularClassDataTimeMS, long generateAdjunctClassDataTimeMS,
        long lookupRegularClassDataTimeMS, long lookupAdjunctClassDataTimeMS,
        long regularFindClassTimeMS, long adjunctFindClassTimeMS) {
        
        this.nRegularClassesLoaded = nRegularClassesLoaded;
        this.nAdjunctClassesLoaded = nAdjunctClassesLoaded;
        this.nRegularClassBytesLoaded = nRegularClassBytesLoaded;
        this.nAdjunctClassBytesLoaded = nAdjunctClassBytesLoaded;
        this.containsTimings = containsTimings;
        this.generateRegularClassDataTimeMS = generateRegularClassDataTimeMS;
        this.generateAdjunctClassDataTimeMS = generateAdjunctClassDataTimeMS;
        this.lookupRegularClassDataTimeMS = lookupRegularClassDataTimeMS;
        this.lookupAdjunctClassDataTimeMS = lookupAdjunctClassDataTimeMS;
        this.regularFindClassTimeMS = regularFindClassTimeMS;
        this.adjunctFindClassTimeMS = adjunctFindClassTimeMS;
    }
    
    /**
     * Calculates the averages of the given statistics.
     * @param statsList the list of statistics.
     * @return a MachineStatistics containing the averages of the given statistics. 
     */
    public static LECCMachineStatistics getAverage(List<MachineStatistics> statsList) {
        int totalNRegularClassesLoaded = 0;
        int totalNAdjunctClassesLoaded = 0;
        int totalNRegularBytesLoaded = 0;
        int totalNAdjunctBytesLoaded = 0;
        long totalGenerateRegularClassDataTimeMS = 0;
        long totalGenerateAdjunctClassDataTimeMS = 0;
        long totalLookupRegularClassDataTimeMS = 0;
        long totalLookupAdjunctClassDataTimeMS = 0;
        long totalRegularFindClassTimeMS = 0;
        long totalAdjunctFindClassTimeMS = 0;
        
        int nStats = statsList.size();
        
        if (nStats == 0) {
            return new LECCMachineStatistics(0, 0, 0, 0, false, 0, 0, 0, 0, 0, 0);
        }
        
        int nStatsWithTimings = 0;
        
        for (int i = 0; i < nStats; i++) {
            LECCMachineStatistics stats = (LECCMachineStatistics)statsList.get(i);
            
            totalNRegularClassesLoaded += stats.getNRegularClassesLoaded();
            totalNAdjunctClassesLoaded += stats.getNAdjunctClassesLoaded();
            totalNRegularBytesLoaded += stats.getNRegularClassBytesLoaded();
            totalNAdjunctBytesLoaded += stats.getNAdjunctClassBytesLoaded();
            
            if (stats.containsTimings) {
                nStatsWithTimings++;
                
                totalGenerateRegularClassDataTimeMS += stats.getGenerateRegularClassDataTimeMS();
                totalGenerateAdjunctClassDataTimeMS += stats.getGenerateAdjunctClassDataTimeMS();
                totalLookupRegularClassDataTimeMS += stats.getLookupRegularClassDataTimeMS();
                totalLookupAdjunctClassDataTimeMS += stats.getLookupAdjunctClassDataTimeMS();
                totalRegularFindClassTimeMS += stats.getRegularFindClassTimeMS();
                totalAdjunctFindClassTimeMS += stats.getAdjunctFindClassTimeMS();
            }
        }
        
        final long averageGenerateRegularClassDataTimeMS;
        final long averageGenerateAdjunctClassDataTimeMS;
        final long averageLookupRegularClassDataTimeMS;
        final long averageLookupAdjunctClassDataTimeMS;
        final long averageRegularFindClassTimeMS;
        final long averageAdjunctFindClassTimeMS;
        
        if (nStatsWithTimings > 0) {
            averageGenerateRegularClassDataTimeMS = totalGenerateRegularClassDataTimeMS / nStatsWithTimings;
            averageGenerateAdjunctClassDataTimeMS = totalGenerateAdjunctClassDataTimeMS / nStatsWithTimings;
            averageLookupRegularClassDataTimeMS = totalLookupRegularClassDataTimeMS / nStatsWithTimings;
            averageLookupAdjunctClassDataTimeMS = totalLookupAdjunctClassDataTimeMS / nStatsWithTimings;
            averageRegularFindClassTimeMS = totalRegularFindClassTimeMS / nStatsWithTimings;
            averageAdjunctFindClassTimeMS = totalAdjunctFindClassTimeMS / nStatsWithTimings;
        } else {
            averageGenerateRegularClassDataTimeMS = 0;
            averageGenerateAdjunctClassDataTimeMS = 0;
            averageLookupRegularClassDataTimeMS = 0;
            averageLookupAdjunctClassDataTimeMS = 0;
            averageRegularFindClassTimeMS = 0;
            averageAdjunctFindClassTimeMS = 0;
        }
        
        return new LECCMachineStatistics(
            totalNRegularClassesLoaded / nStats, totalNAdjunctClassesLoaded / nStats,
            totalNRegularBytesLoaded / nStats, totalNAdjunctBytesLoaded / nStats,
            nStatsWithTimings > 0,
            averageGenerateRegularClassDataTimeMS, averageGenerateAdjunctClassDataTimeMS,
            averageLookupRegularClassDataTimeMS, averageLookupAdjunctClassDataTimeMS,
            averageRegularFindClassTimeMS, averageAdjunctFindClassTimeMS);
    }
    
    /**
     * Calculates the incremental difference of the given statistics. For adjunct-based statistics, the value is calculated by
     * the formula (stats for all modules after - (stats for all modules before - stats for given module before)).
     * 
     * @param programBefore the statistics for the entire program, the "before" snapshot
     * @param programAfter the statistics for the entire program, the "after" snapshot
     * @param moduleBefore the statistics for one particular module, the "before" snapshot
     * @return a MachineStatistics containing the incremental difference calculated from the given statistics. 
     */
    public static LECCMachineStatistics getIncrementalStatistics(LECCMachineStatistics programBefore, LECCMachineStatistics programAfter, LECCMachineStatistics moduleBefore) {
        
        int totalNRegularClassesLoaded = programAfter.nRegularClassesLoaded - programBefore.nRegularClassesLoaded;
        int totalNAdjunctClassesLoaded = programAfter.nAdjunctClassesLoaded - (programBefore.nAdjunctClassesLoaded - moduleBefore.nAdjunctClassesLoaded); 
        int totalNRegularBytesLoaded = programAfter.nRegularClassBytesLoaded - programBefore.nRegularClassBytesLoaded;
        int totalNAdjunctBytesLoaded = programAfter.nAdjunctClassBytesLoaded - (programBefore.nAdjunctClassBytesLoaded - moduleBefore.nAdjunctClassBytesLoaded);
        long totalGenerateRegularClassDataTimeMS = programAfter.generateRegularClassDataTimeMS - programBefore.generateRegularClassDataTimeMS;
        long totalGenerateAdjunctClassDataTimeMS = programAfter.generateAdjunctClassDataTimeMS - (programBefore.generateAdjunctClassDataTimeMS - moduleBefore.generateAdjunctClassDataTimeMS);
        long totalLookupRegularClassDataTimeMS = programAfter.lookupRegularClassDataTimeMS - programBefore.lookupRegularClassDataTimeMS;
        long totalLookupAdjunctClassDataTimeMS = programAfter.lookupAdjunctClassDataTimeMS - (programBefore.lookupAdjunctClassDataTimeMS - moduleBefore.lookupAdjunctClassDataTimeMS);
        long totalRegularFindClassTimeMS = programAfter.regularFindClassTimeMS - programBefore.regularFindClassTimeMS;
        long totalAdjunctFindClassTimeMS = programAfter.adjunctFindClassTimeMS - (programBefore.adjunctFindClassTimeMS - moduleBefore.adjunctFindClassTimeMS);
        
        return new LECCMachineStatistics(
            totalNRegularClassesLoaded, totalNAdjunctClassesLoaded,
            totalNRegularBytesLoaded, totalNAdjunctBytesLoaded,
            programAfter.containsTimings,
            totalGenerateRegularClassDataTimeMS, totalGenerateAdjunctClassDataTimeMS,
            totalLookupRegularClassDataTimeMS, totalLookupAdjunctClassDataTimeMS,
            totalRegularFindClassTimeMS, totalAdjunctFindClassTimeMS);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        String result =
            "#classes loaded     (regular/adjunct): " + nRegularClassesLoaded + "/" + nAdjunctClassesLoaded + "\n" +
            "#class bytes loaded (regular/adjunct): " + nRegularClassBytesLoaded + "/" + nAdjunctClassBytesLoaded + "\n";
        
        if (containsTimings) {
            result +=
                "class data generation time (regular/adjunct): " + generateRegularClassDataTimeMS + "/" + generateAdjunctClassDataTimeMS + " ms\n" +
                "class data lookup time     (regular/adjunct): " + lookupRegularClassDataTimeMS + "/" + lookupAdjunctClassDataTimeMS + " ms\n" +
                "time spent in findClass()  (regular/adjunct): " + regularFindClassTimeMS + "/" + adjunctFindClassTimeMS + " ms\n";
        }
        
        return result;
    }
    
    /**
     * @return the number of classes loaded by the class loaders associated with the modules in the program.
     */
    public int getNClassesLoaded() {
        return nRegularClassesLoaded + nAdjunctClassesLoaded;
    }
    
    /**
     * @return the number of regular classes loaded by the class loaders associated with the modules in the program.
     */
    public int getNRegularClassesLoaded() {
        return nRegularClassesLoaded;
    }

    /**
     * @return the number of adjunct classes loaded by the class loaders associated with the modules in the program.
     */
    public int getNAdjunctClassesLoaded() {
        return nAdjunctClassesLoaded;
    }
    
    /**
     * @return the number of bytes for classes loaded by the class loaders associated with the modules in the program.
     */
    public int getNClassBytesLoaded() {
        return nRegularClassBytesLoaded + nAdjunctClassBytesLoaded;
    }
    
    /**
     * @return the number of bytes for regular classes loaded by the class loaders associated with the modules in the program.
     */
    public int getNRegularClassBytesLoaded() {
        return nRegularClassBytesLoaded;
    }
    
    /**
     * @return the number of bytes for adjunct classes loaded by the class loaders associated with the modules in the program.
     */
    public int getNAdjunctClassBytesLoaded() {
        return nAdjunctClassBytesLoaded;
    }

    /**
     * @return whether timing was performed (and therefore the timing values meaningful).
     */
    public boolean containsTimings() {
        return containsTimings;
    }

    /**
     * @return number of milliseconds spent in generating class file data by the class loaders associated with the modules in the program.
     */
    public long getGenerateClassDataTimeMS() {
        return generateRegularClassDataTimeMS + generateAdjunctClassDataTimeMS;
    }

    /**
     * @return number of milliseconds spent in generating regular class file data by the class loaders associated with the modules in the program.
     */
    public long getGenerateRegularClassDataTimeMS() {
        return generateRegularClassDataTimeMS;
    }

    /**
     * @return number of milliseconds spent in generating adjunct class file data by the class loaders associated with the modules in the program.
     */
    public long getGenerateAdjunctClassDataTimeMS() {
        return generateAdjunctClassDataTimeMS;
    }

    /**
     * @return number of milliseconds spent in looking up class file data by the class loaders associated with the modules in the program.
     */
    public long getLookupClassDataTimeMS() {
        return lookupRegularClassDataTimeMS + lookupAdjunctClassDataTimeMS;
    }

    /**
     * @return number of milliseconds spent in looking up regular class file data by the class loaders associated with the modules in the program.
     */
    public long getLookupRegularClassDataTimeMS() {
        return lookupRegularClassDataTimeMS;
    }

    /**
     * @return number of milliseconds spent in looking up adjunct class file data by the class loaders associated with the modules in the program.
     */
    public long getLookupAdjunctClassDataTimeMS() {
        return lookupAdjunctClassDataTimeMS;
    }
    
    /**
     * @return number of milliseconds spent in findClass() by the class loaders associated with the modules in the program.
     */
    public long getFindClassTimeMS() {
        return regularFindClassTimeMS + adjunctFindClassTimeMS;
    }
    
    /**
     * @return number of milliseconds spent in findClass() for regular classes by the class loaders associated with the modules in the program.
     */
    public long getRegularFindClassTimeMS() {
        return regularFindClassTimeMS;
    }
    
    /**
     * @return number of milliseconds spent in findClass() for adjunct classes by the class loaders associated with the modules in the program.
     */
    public long getAdjunctFindClassTimeMS() {
        return adjunctFindClassTimeMS;
    }
}
