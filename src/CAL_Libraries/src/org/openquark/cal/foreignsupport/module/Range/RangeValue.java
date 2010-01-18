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
 * RangeValue.java
 * Creation date (05/26/04 1:02:03pm).
 * By: Iulian Radu
 */
package org.openquark.cal.foreignsupport.module.Range;

/**
 * Represents a value of a Range data type constructed within CAL.
 * @author Iulian Radu
 */
public abstract class RangeValue {
    
    // Public construction methods
    
    public final static RangeValue constructEntireRange() {
        return new EntireRange();
    }
    
    public final static RangeValue constructIsLessThan (Object right) { 
        return new IsLessThan (right);
    }
    
    public final static RangeValue constructIsLessThanEquals (Object right) {
        return new IsLessThanEquals (right);
    }
    
    public final static RangeValue constructIsGreaterThan (Object left) {
        return new IsGreaterThan (left);
    }
    
    public final static RangeValue constructIsGreaterThanEquals (Object left) {
        return new IsGreaterThanEquals (left);
    }
    
    public final static RangeValue constructBetweenIncludingEndpoints (Object left, Object right) {
        return new BetweenIncludingEndpoints (left, right);
    }
    
    public final static RangeValue constructBetweenIncludingLeftEndpoint (Object left, Object right) {
        return new BetweenIncludingLeftEndpoint (left, right);
    }
    
    public final static RangeValue constructBetweenIncludingRightEndpoint (Object left, Object right) {
        return new BetweenIncludingRightEndpoint (left, right);
    }
    
    public final static RangeValue constructBetweenExcludingEndpoints (Object left, Object right) {
        return new BetweenExcludingEndpoints (left, right);
    }
    
    // Subclasses
    
    /** Range representing the entire space */
    private static class EntireRange extends RangeValue {
        private EntireRange() {
        }
        
        @Override
        public boolean hasLeftBound() {
            return false;
        }
        @Override
        public boolean hasRightBound() {
            return false;
        }
        
        @Override
        public boolean includesLeftBound() {
            return false;
        }
        @Override
        public boolean includesRightBound() {
            return false;
        }
        
        @Override
        public Object getLeftEndpoint() {
            throw new UnsupportedOperationException();
        }
        @Override
        public Object getRightEndpoint() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public String toString() {
            return "(entire range)";
        }
    }
    
    /** Range representing values less than the specified value (non-inclusive) */ 
    private static class IsLessThan extends RangeValue {
        
        private final Object rightEndpoint;
        
        /** Constructor */
        private IsLessThan(Object rightEndpoint) {
            if (rightEndpoint == null) {
                throw new IllegalArgumentException();
            }
            this.rightEndpoint = rightEndpoint;
        }
        
        @Override
        public boolean hasLeftBound() {
            return false;
        }
        @Override
        public boolean hasRightBound() {
            return true;
        }
        
        @Override
        public boolean includesLeftBound() {
            return false;
        }
        @Override
        public boolean includesRightBound() {
            return false;
        }
        
        @Override
        public Object getLeftEndpoint() {
            throw new UnsupportedOperationException();
        }
        @Override
        public Object getRightEndpoint() {
            return rightEndpoint;
        }
        
        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder("(x < ");
            buffer.append(rightEndpoint);
            buffer.append(')');
            return buffer.toString();
        }
    }
    
    /** Range representing values less than or equal to the specified value */
    private static class IsLessThanEquals extends RangeValue {
        
        private final Object rightEndpoint;
        
        /** Constructor */
        private IsLessThanEquals(Object rightEndpoint) {
            if (rightEndpoint == null) {
                throw new IllegalArgumentException();
            }
            this.rightEndpoint = rightEndpoint;
        }
        
        @Override
        public boolean hasLeftBound() {
            return false;
        }
        @Override
        public boolean hasRightBound() {
            return true;
        }
        
        @Override
        public boolean includesLeftBound() {
            return false;
        }
        @Override
        public boolean includesRightBound() {
            return true;
        }
        
        @Override
        public Object getLeftEndpoint() {
            throw new UnsupportedOperationException();
        }
        @Override
        public Object getRightEndpoint() {
            return rightEndpoint;
        }
        
        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder("(x <= ");
            buffer.append(rightEndpoint);
            buffer.append(')');
            return buffer.toString();
        }
    }
    
    /** Range representing values greater than the specified value (non-inclusive) */
    private static class IsGreaterThan extends RangeValue {
        
        private final Object leftEndpoint;
        
        /** Constructor */
        private IsGreaterThan(Object leftEndpoint) {
            if (leftEndpoint == null) {
                throw new IllegalArgumentException();
            }
            this.leftEndpoint = leftEndpoint;
        }
        
        @Override
        public boolean hasLeftBound() {
            return true;
        }
        @Override
        public boolean hasRightBound() {
            return false;
        }
        
        @Override
        public boolean includesLeftBound() {
            return false;
        }
        @Override
        public boolean includesRightBound() {
            return false;
        }
        
        @Override
        public Object getLeftEndpoint() {
            return leftEndpoint;
        }
        @Override
        public Object getRightEndpoint() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder("(x > ");
            buffer.append(leftEndpoint);
            buffer.append(')');
            return buffer.toString();
        }
    }
    
    /** Range representing values greater than or equal to the specified value */
    private static class IsGreaterThanEquals extends RangeValue {
        
        private final Object leftEndpoint;
        
        /** Constructor */
        private IsGreaterThanEquals(Object leftEndpoint) {
            if (leftEndpoint == null) {
                throw new IllegalArgumentException();
            }
            this.leftEndpoint = leftEndpoint;
        }
        
        @Override
        public boolean hasLeftBound() {
            return true;
        }
        @Override
        public boolean hasRightBound() {
            return false;
        }
        
        @Override
        public boolean includesLeftBound() {
            return true;
        }
        @Override
        public boolean includesRightBound() {
            return false;
        }
        
        @Override
        public Object getLeftEndpoint() {
            return leftEndpoint;
        }
        @Override
        public Object getRightEndpoint() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder("(x >= ");
            buffer.append(leftEndpoint);
            buffer.append(')');
            return buffer.toString();
        }
    }
    
    /** Range representing values greater than or equal to the specified left endpoints,
     *  and less than or equal to the specified right endpoint */
    private static class BetweenIncludingEndpoints extends RangeValue {
        
        private final Object leftEndpoint;
        private final Object rightEndpoint;
        
        /** Constructor */
        private BetweenIncludingEndpoints(Object leftEndpoint, Object rightEndpoint) {
            if (leftEndpoint == null || rightEndpoint == null) {
                throw new IllegalArgumentException();
            }
            this.leftEndpoint = leftEndpoint;
            this.rightEndpoint = rightEndpoint;
        }
        
        @Override
        public boolean hasLeftBound() {
            return true;
        }
        @Override
        public boolean hasRightBound() {
            return true;
        }
        
        @Override
        public boolean includesLeftBound() {
            return true;
        }
        @Override
        public boolean includesRightBound() {
            return true;
        }
        
        @Override
        public Object getLeftEndpoint() {
            return leftEndpoint;
        }
        @Override
        public Object getRightEndpoint() {
            return rightEndpoint;
        }
        
        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder("(");
            buffer.append(leftEndpoint);
            buffer.append(" <= x <= ");
            buffer.append(rightEndpoint);
            buffer.append(')');
            return buffer.toString();
        }
    }
    
    /** Range representing values greater than or equal to the specified left endpoints,
     *  and less than the specified right endpoint */
    private static class BetweenIncludingLeftEndpoint extends RangeValue {
        
        private final Object leftEndpoint;
        private final Object rightEndpoint;
        
        /** Constructor */
        private BetweenIncludingLeftEndpoint(Object leftEndpoint, Object rightEndpoint) {
            if (leftEndpoint == null || rightEndpoint == null) {
                throw new IllegalArgumentException();
            }
            this.leftEndpoint = leftEndpoint;
            this.rightEndpoint = rightEndpoint;
        }
        
        @Override
        public boolean hasLeftBound() {
            return true;
        }
        @Override
        public boolean hasRightBound() {
            return true;
        }
        
        @Override
        public boolean includesLeftBound() {
            return true;
        }
        @Override
        public boolean includesRightBound() {
            return false;
        }
        
        @Override
        public Object getLeftEndpoint() {
            return leftEndpoint;
        }
        @Override
        public Object getRightEndpoint() {
            return rightEndpoint;
        }
        
        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder("(");
            buffer.append(leftEndpoint);
            buffer.append(" <= x < ");
            buffer.append(rightEndpoint);
            buffer.append(')');
            return buffer.toString();
        }
    }
    
    /** Range representing values greater than the specified left endpoints,
     *  and less than or equal to the specified right endpoint */
    private static class BetweenIncludingRightEndpoint extends RangeValue {
        
        private final Object leftEndpoint;
        private final Object rightEndpoint;
        
        /** Constructor */
        private BetweenIncludingRightEndpoint(Object leftEndpoint, Object rightEndpoint) {
            if (leftEndpoint == null || rightEndpoint == null) {
                throw new IllegalArgumentException();
            }
            this.leftEndpoint = leftEndpoint;
            this.rightEndpoint = rightEndpoint;
        }
        
        @Override
        public boolean hasLeftBound() {
            return true;
        }
        @Override
        public boolean hasRightBound() {
            return true;
        }
        
        @Override
        public boolean includesLeftBound() {
            return false;
        }
        @Override
        public boolean includesRightBound() {
            return true;
        }
        
        @Override
        public Object getLeftEndpoint() {
            return leftEndpoint;
        }
        @Override
        public Object getRightEndpoint() {
            return rightEndpoint;
        }
        
        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder("(");
            buffer.append(leftEndpoint);
            buffer.append(" < x <= ");
            buffer.append(rightEndpoint);
            buffer.append(')');
            return buffer.toString();
        }
    }
    
    /** Range representing values greater than the specified left endpoints,
     *  and less than the specified right endpoint */
    private static class BetweenExcludingEndpoints extends RangeValue {
        
        private final Object leftEndpoint;
        private final Object rightEndpoint;
        
        /** Constructor */
        private BetweenExcludingEndpoints(Object leftEndpoint, Object rightEndpoint) {
            if (leftEndpoint == null || rightEndpoint == null) {
                throw new IllegalArgumentException();
            }
            this.leftEndpoint = leftEndpoint;
            this.rightEndpoint = rightEndpoint;
        }
        
        @Override
        public boolean hasLeftBound() {
            return true;
        }
        @Override
        public boolean hasRightBound() {
            return true;
        }
        
        @Override
        public boolean includesLeftBound() {
            return false;
        }
        @Override
        public boolean includesRightBound() {
            return false;
        }
        
        @Override
        public Object getLeftEndpoint() {
            return leftEndpoint;
        }
        @Override
        public Object getRightEndpoint() {
            return rightEndpoint;
        }
        
        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder("(");
            buffer.append(leftEndpoint);
            buffer.append(" < x < ");
            buffer.append(rightEndpoint);
            buffer.append(')');
            return buffer.toString();
        }
    }
    
    
    /** Constructor */
    private RangeValue() {
    }
    
    /**
     * @return whether the range has a left bound
     */
    public abstract boolean hasLeftBound();
    
    /**
     * @return whether the range has a right bound
     */
    public abstract boolean hasRightBound();
    
    /**
     * @return whether the range includes its left bound
     */
    public abstract boolean includesLeftBound();
    
    /**
     * @return whether the range includes its right bound
     */
    public abstract boolean includesRightBound();
    
    /**
     * @return Returns the leftEndpoint.
     */
    public abstract Object getLeftEndpoint();
    
    /**
     * @return Returns the rightEndpoint.
     */
    public abstract Object getRightEndpoint();
}