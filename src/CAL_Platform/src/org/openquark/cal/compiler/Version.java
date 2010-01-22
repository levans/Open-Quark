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
 * Version.java
 * Created: Dec 19, 2005
 * By: Bo Ilic
 */

package org.openquark.cal.compiler;

/**
 * A class that holds constants representing the version number of the CAL platform.
 * 
 * The numbering system is a simple one, adapted from that used by the Unicode standard, and has the format:
 * majorVersionNumber.minorVersionNumber.updateVersionNumber_buildNumber
 *  
 * @author Bo Ilic
 */

// Version history:
// 1.2.0_100 initial version
// 1.2.0_101 build of Dec 22, 2005
// 1.2.0_102 build of Jan 20, 2006
// 1.2.0_103 build of Feb  2, 2006
// 1.2.0_104 build of Feb  9, 2006
// 1.2.0_105 build of Feb 16, 2006
// 1.2.0_106 build of Mar  3, 2006
// 1.2.0_107 build of Mar  9, 2006
// 1.2.0_108 build of Mar 20, 2006
// 1.2.0_109 build of Mar 23, 2006
// 1.2.0_110 build of Mar 30, 2006
// 1.2.0_111 build of Apr  6, 2006
// 1.2.0_112 build of Apr 17, 2006
// 1.2.0_113 build of Apr 28, 2006
// 1.2.0_114 build of May  5, 2006
// 1.2.0_115 build of May 18, 2006
// 1.2.0_116 build of May 26, 2006
// 1.2.0_117 build of Jun  1, 2006
// 1.2.0_118 build of Jun  8, 2006
// 1.2.0_119 build of Jun 16, 2006
// 1.2.0_120 build of Jul  7, 2006
// 1.2.0_121 build of Jul 13, 2006
// 1.2.0_122 build of Jul 20, 2006
// 1.2.0_123 build of Jul 27, 2006
// 1.2.0_124 build of Aug  4, 2006
// 1.2.0_125 build of Aug 10, 2006
// 1.2.0_126 build of Aug 17, 2006
// 1.2.0_127 build of Aug 24, 2006
// 1.2.0_128 build of Aug 31, 2006
// 1.2.0_129 build of Sep  7, 2006
// 1.2.0_130 build of Sep 14, 2006
// 1.2.0_131 build of Sep 21, 2006
// 1.2.0_132 build of Sep 29, 2006
// 1.2.0_133 build of Oct 12, 2006
// 1.2.0_134 build of Oct 19, 2006
// 1.2.0_135 build of Oct 27, 2006
// 1.2.0_136 build of Nov  2, 2006
// 1.2.0_137 build of Nov  9, 2006
// 1.2.0_138 build of Nov 16, 2006
// 1.2.0_139 build of Nov 23, 2006
// 1.2.0_140 build of Nov 30, 2006
// 1.2.0_141 build of Dec  7, 2006
// 1.2.0_142 build of Dec 15, 2006
// 1.2.0_143 build of Dec 28, 2006
// 1.2.0_144 build of Jan  4, 2007
// 1.2.0_145 build of Jan 11, 2007
// 1.2.0_146 build of Jan 19, 2007
// 1.3.0_0   first Open Quark release with BSD license, Jan 23, 2007 
// 1.3.0_1   build of Jan 25, 2007
// 1.3.0_2   build of Feb  1, 2007
// 1.3.0_3   build of Feb  8, 2007
// 1.3.0_4   build of Feb 15, 2007
// 1.3.0_5   build of Feb 22, 2007
// 1.3.0_6   build of Mar  1, 2007
// 1.3.0_7   build of Mar  8, 2007
// 1.3.0_8   build of Mar 16, 2007
// 1.3.0_9   build of Mar 22, 2007
// 1.3.0_10  build of Mar 29, 2007
// 1.3.0_11  build of Apr  5, 2007
// 1.4.0_0   Open Quark release of Apr 11, 2007.  First release of CAL Eclipse Plug-in.
// 1.4.0_1   build of Apr 12, 2007
// 1.4.0_2   build of Apr 19, 2007
// 1.4.0_3   build of Apr 26, 2007
// 1.4.0_4   build of May  3, 2007
// 1.4.0_5   build of May 18, 2007
// 1.4.0_6   build of May 24, 2007
// 1.4.0_7   build of May 31, 2007
// 1.4.0_8   build of Jun  7, 2007
// 1.5.0_0   Open Quark release of Jun 15, 2007.
// 1.5.0_1   build of Jun 21, 2007
// 1.5.0_2   build of Jun 28, 2007
// 1.5.0_3   build of Jul  5, 2007
// 1.5.0_4   build of Jul 12, 2007
// 1.5.1_0   Open Quark release of Jul 19, 2007.
// 1.5.1_1   build of Jul 20, 2007
// 1.5.1_2   build of Jul 26, 2007
// 1.5.1_3   build of Aug  3, 2007
// 1.5.1_4   build of Aug  9, 2007
// 1.5.1_5   build of Aug 16, 2007
// 1.5.1_6   build of Aug 23, 2007
// 1.6.0_0   Open Quark release of Aug 29, 2007.
// 1.6.0_1   build of Aug 30, 2007
// 1.6.0_2   build of Sep  6, 2007
// 1.6.0_3   build of Sep 13, 2007
// 1.6.0_4   build of Sep 20, 2007
// 1.6.1_0   Open Quark release of Sep 21, 2007.
// 1.6.1_1   build of Sep 27, 2007
// 1.6.1_2   build of Oct  4, 2007
// 1.6.1_3   build of Oct 11, 2007
// 1.6.1_4   build of Oct 18, 2007
// 1.6.1_5   build of Oct 25, 2007
// 1.7.0_0   Open Quark release of Oct 26, 2007.
// 1.7.0_1   build of Nov  1, 2007
// 1.7.1_0   Open Quark release of Nov 15, 2007.
// 1.7.2_0   Open Quark release of Jan 22, 2010.

public final class Version {
            
    public static final Version CURRENT = new Version();
        
    private static final int majorVersion = 1;   
    
    private static final int minorVersion = 7;
        
    private static final int updateVersion = 2;
    
    /** build should be incremented by 1 for each build. */
    private static final int build = 0;
    
    private Version() {}
    
    @Override
    public String toString() {
        return new StringBuilder().
            append(majorVersion).append('.').
            append(minorVersion).append('.').
            append(updateVersion).append('_').
            append(build).toString();
    }
}
