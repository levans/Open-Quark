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
 * FixedSizeList.java
 * Created: Apr 22, 2005
 * By: Bo Ilic
 */

package org.openquark.cal.util;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;

/**
 * Provides factory methods for creating fixed sized lists with sizes
 * varying from 1 to 7.
 * 
 * <P>These lists provide fast random access to elements and are also more memory efficient
 * than using ArrayList or Vector.
 * 
 * <P>If the list is size 0, one can just use Collections.EMPTY_LIST.
 * If the list is size 1, Collections.singletonList for an unmodifiable list. However, List1 is modifiable.
 * 
 * @author Bo Ilic
 */
public abstract class FixedSizeList  {
          
   private static final class List1<T> extends AbstractList<T> implements RandomAccess, Serializable {
    
        /** Only change this when an incompatible change has been made to the class schema */
        static final long serialVersionUID = -55636750666820200L;
       
        private T element0;
       
        List1(T element0) {
            this.element0 = element0;            
        }
    
        /** {@inheritDoc}*/
        @Override
        public T get(int index) {
            
            switch (index) {
                case 0: return element0;                
                default: throw new IndexOutOfBoundsException("index must be be 0 but was " + index + ".");
            }       
        }
        
        /** {@inheritDoc}*/
        @Override
        public T set(int index, T element) {
            T oldValue;
            switch (index) {
                case 0:
                    oldValue = element0;
                    element0 = element;
                    return oldValue;               
                default: throw new IndexOutOfBoundsException("index must be between 0 and 1 but was " + index + ".");
            }       
        }
    
        /** {@inheritDoc}*/
        @Override
        public int size() {        
            return 1;
        }     
    }            
    
    private static final class List2<T> extends AbstractList<T> implements RandomAccess, Serializable {
    
        /** Only change this when an incompatible change has been made to the class schema */
        static final long serialVersionUID = -1520120374883127099L;
        private T element0;
        private T element1;
        
        List2(T element0, T element1) {
            this.element0 = element0;
            this.element1 = element1;        
        }
    
        /** {@inheritDoc}*/
        @Override
        public T get(int index) {
            
            switch (index) {
                case 0: return element0;
                case 1: return element1;
                default: throw new IndexOutOfBoundsException("index must be between 0 and 1 but was " + index + ".");
            }       
        }
        
        /** {@inheritDoc}*/
        @Override
        public T set(int index, T element) {
            T oldValue;
            switch (index) {
                case 0:
                    oldValue = element0;
                    element0 = element;
                    return oldValue;
                case 1: 
                    oldValue = element1;
                    element1 = element;
                    return oldValue;
                default: throw new IndexOutOfBoundsException("index must be between 0 and 1 but was " + index + ".");
            }       
        }
    
        /** {@inheritDoc}*/
        @Override
        public int size() {        
            return 2;
        }     
    }
    
    private static final class List3<T> extends AbstractList<T> implements RandomAccess, Serializable {
        
        /** Only change this when an incompatible change has been made to the class schema */
        static final long serialVersionUID = 5775458853820390711L;
        private T element0;
        private T element1;
        private T element2;
        
        List3(T element0, T element1, T element2) {
            this.element0 = element0;
            this.element1 = element1;
            this.element2 = element2;
        }
    
        /** {@inheritDoc}*/
        @Override
        public T get(int index) {
            
            switch (index) {
                case 0: return element0;
                case 1: return element1;
                case 2: return element2;
                default: throw new IndexOutOfBoundsException("index must be between 0 and 2 but was " + index + ".");
            }       
        }
        
        /** {@inheritDoc}*/
        @Override
        public T set(int index, T element) {
            T oldValue;
            switch (index) {
                case 0:
                    oldValue = element0;
                    element0 = element;
                    return oldValue;
                case 1: 
                    oldValue = element1;
                    element1 = element;
                    return oldValue;
                case 2: 
                    oldValue = element2;
                    element2 = element;
                    return oldValue;                    
                default: throw new IndexOutOfBoundsException("index must be between 0 and 1 but was " + index + ".");
            }       
        }        
    
        /** {@inheritDoc}*/
        @Override
        public int size() {        
            return 3;
        }
    }
    
    private static final class List4<T> extends AbstractList<T> implements RandomAccess, Serializable {
        
        /** Only change this when an incompatible change has been made to the class schema */
        static final long serialVersionUID = 1199643529534887277L;
        private T element0;
        private T element1;
        private T element2;
        private T element3;
        
        List4(T element0, T element1, T element2, T element3) {
            this.element0 = element0;
            this.element1 = element1;
            this.element2 = element2;
            this.element3 = element3;
        }
    
        /** {@inheritDoc}*/
        @Override
        public T get(int index) {
            
            switch (index) {
                case 0: return element0;
                case 1: return element1;
                case 2: return element2;
                case 3: return element3;
                default: throw new IndexOutOfBoundsException("index must be between 0 and 3 but was " + index + ".");
            }       
        }
        
        /** {@inheritDoc}*/
        @Override
        public T set(int index, T element) {
            T oldValue;
            switch (index) {
                case 0:
                    oldValue = element0;
                    element0 = element;
                    return oldValue;
                case 1: 
                    oldValue = element1;
                    element1 = element;
                    return oldValue;
                case 2: 
                    oldValue = element2;
                    element2 = element;
                    return oldValue;
                case 3:
                    oldValue = element3;
                    element3 = element;
                    return oldValue;
                default: throw new IndexOutOfBoundsException("index must be between 0 and 1 but was " + index + ".");
            }       
        }        
    
        /** {@inheritDoc}*/
        @Override
        public int size() {        
            return 4;
        }
    }
    
    private static final class List5<T> extends AbstractList<T> implements RandomAccess, Serializable {
        
        /** Only change this when an incompatible change has been made to the class schema */
        static final long serialVersionUID = -6168645202459303757L;
        private T element0;
        private T element1;
        private T element2;
        private T element3;
        private T element4;
        
        List5(T element0, T element1, T element2, T element3, T element4) {
            this.element0 = element0;
            this.element1 = element1;
            this.element2 = element2;
            this.element3 = element3;
            this.element4 = element4;
        }
    
        /** {@inheritDoc}*/
        @Override
        public T get(int index) {
            
            switch (index) {
                case 0: return element0;
                case 1: return element1;
                case 2: return element2;
                case 3: return element3;
                case 4: return element4;
                default: throw new IndexOutOfBoundsException("index must be between 0 and 4 but was " + index + ".");
            }       
        }
        
        /** {@inheritDoc}*/
        @Override
        public T set(int index, T element) {
            T oldValue;
            switch (index) {
                case 0:
                    oldValue = element0;
                    element0 = element;
                    return oldValue;
                case 1: 
                    oldValue = element1;
                    element1 = element;
                    return oldValue;
                case 2: 
                    oldValue = element2;
                    element2 = element;
                    return oldValue;
                case 3:
                    oldValue = element3;
                    element3 = element;
                    return oldValue;
                case 4:
                    oldValue = element4;
                    element4 = element;
                    return oldValue;
                default: throw new IndexOutOfBoundsException("index must be between 0 and 1 but was " + index + ".");
            } 
        }
    
        /** {@inheritDoc}*/
        @Override
        public int size() {        
            return 5;
        }      
    }
    
    private static final class List6<T> extends AbstractList<T> implements RandomAccess, Serializable {
        
        /** Only change this when an incompatible change has been made to the class schema */
        static final long serialVersionUID = -8214268383872794025L;
        private T element0;
        private T element1;
        private T element2;
        private T element3;
        private T element4;
        private T element5;
        
        List6(T element0, T element1, T element2, T element3, T element4, T element5) {
            this.element0 = element0;
            this.element1 = element1;
            this.element2 = element2;
            this.element3 = element3;
            this.element4 = element4;
            this.element5 = element5;
        }
    
        /** {@inheritDoc}*/
        @Override
        public T get(int index) {
            
            switch (index) {
                case 0: return element0;
                case 1: return element1;
                case 2: return element2;
                case 3: return element3;
                case 4: return element4;
                case 5: return element5;
                default: throw new IndexOutOfBoundsException("index must be between 0 and 5 but was " + index + ".");
            }       
        }
        
        /** {@inheritDoc}*/
        @Override
        public T set(int index, T element) {
            T oldValue;
            switch (index) {
                case 0:
                    oldValue = element0;
                    element0 = element;
                    return oldValue;
                case 1: 
                    oldValue = element1;
                    element1 = element;
                    return oldValue;
                case 2: 
                    oldValue = element2;
                    element2 = element;
                    return oldValue;
                case 3:
                    oldValue = element3;
                    element3 = element;
                    return oldValue;
                case 4:
                    oldValue = element4;
                    element4 = element;
                    return oldValue;
                case 5:
                    oldValue = element5;
                    element5 = element;
                    return oldValue;
                default: throw new IndexOutOfBoundsException("index must be between 0 and 1 but was " + index + ".");
            } 
        }        
    
        /** {@inheritDoc}*/
        @Override
        public int size() {        
            return 6;
        }                     
    }
    
    private static final class List7<T> extends AbstractList<T> implements RandomAccess, Serializable {
        
        /** Only change this when an incompatible change has been made to the class schema */
        static final long serialVersionUID = 2104497566436723863L;
        private T element0;
        private T element1;
        private T element2;
        private T element3;
        private T element4;
        private T element5;
        private T element6;
        
        List7(T element0, T element1, T element2, T element3, T element4, T element5, T element6) {
            this.element0 = element0;
            this.element1 = element1;
            this.element2 = element2;
            this.element3 = element3;
            this.element4 = element4;
            this.element5 = element5;
            this.element6 = element6;
        }
    
        /** {@inheritDoc}*/
        @Override
        public T get(int index) {
            
            switch (index) {
                case 0: return element0;
                case 1: return element1;
                case 2: return element2;
                case 3: return element3;
                case 4: return element4;
                case 5: return element5;
                case 6: return element6;            
                default: throw new IndexOutOfBoundsException("index must be between 0 and 6 but was " + index + ".");
            }       
        }
        
        /** {@inheritDoc}*/
        @Override
        public T set(int index, T element) {
            T oldValue;
            switch (index) {
                case 0:
                    oldValue = element0;
                    element0 = element;
                    return oldValue;
                case 1: 
                    oldValue = element1;
                    element1 = element;
                    return oldValue;
                case 2: 
                    oldValue = element2;
                    element2 = element;
                    return oldValue;
                case 3:
                    oldValue = element3;
                    element3 = element;
                    return oldValue;
                case 4:
                    oldValue = element4;
                    element4 = element;
                    return oldValue;
                case 5:
                    oldValue = element5;
                    element5 = element;
                    return oldValue;
                case 6:
                    oldValue = element6;
                    element6 = element;
                    return oldValue;
                default: throw new IndexOutOfBoundsException("index must be between 0 and 1 but was " + index + ".");
            } 
        }                
    
        /** {@inheritDoc}*/
        @Override
        public int size() {        
            return 7;
        }                      
    }    
    
    /**
     * Non-instantiable factory class.    
     */
    private FixedSizeList() {}
            
   
   /**       
    * @return a two element fixed-size modifiable random-access list. The returned list is Serializable.
    */      
    public static final <T> List<T> make(T e0) {
        return new List1<T>(e0);
    }    
    
   /**       
    * @return a two element fixed-size modifiable random-access list. The returned list is Serializable.
    */      
    public static final <T> List<T> make(T e0, T e1) {
        return new List2<T>(e0, e1);
    }
    
    /**       
     * @return a three element fixed-size modifiable random-access list. The returned list is Serializable.
     */    
    public static final <T> List<T> make(T e0, T e1, T e2) {
        return new List3<T>(e0, e1, e2);
    }
    
    /**       
     * @return a four element fixed-size modifiable random-access list. The returned list is Serializable.
     */    
    public static final <T> List<T> make(T e0, T e1, T e2, T e3) {
        return new List4<T>(e0, e1, e2, e3);
    }
    
    /**       
     * @return a five element fixed-size modifiable random-access list. The returned list is Serializable.
     */    
    public static final <T> List<T> make(T e0, T e1, T e2, T e3, T e4) {
        return new List5<T>(e0, e1, e2, e3, e4);
    }
    
    /**       
     * @return a six element fixed-size modifiable random-access list. The returned list is Serializable.
     */    
    public static final <T> List<T> make(T e0, T e1, T e2, T e3, T e4, T e5) {
        return new List6<T>(e0, e1, e2, e3, e4, e5);
    }
    
    /**       
     * @return a seven element fixed-size modifiable random-access list. The returned list is Serializable.
     */    
    public static final <T> List<T> make(T e0, T e1, T e2, T e3, T e4, T e5, T e6) {
        return new List7<T>(e0, e1, e2, e3, e4, e5, e6);
    }   
}
