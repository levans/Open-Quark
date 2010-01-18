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
 * Argument.java
 * Creation date: (1/30/01 5:52:16 PM)
 * By: Luke Evans
 */
package org.openquark.gems.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openquark.cal.compiler.TypeExpr;
import org.openquark.util.Pair;


/**
 * A representation of a single function argument.
 * This class is a utility package class designed to be useful in several situations 
 * where function arguments need to be passed around.
 * Creation date: (1/30/01 5:52:16 PM)
 * @author Luke Evans
 */
public class Argument {

    /**
     * Argument status enum pattern.
     * Creation date: (Aug 14, 2002 12:29:16 PM)
     * @author Edward Lam
     */
    public static final class Status {
        private final String typeString;

        private Status(String s) {
            typeString = s;
        }
        @Override
        public String toString() {
            return typeString;
        }

        /** This argument is unconnected, unburnt. */
        public static final Status NATURAL = new Status("NATURAL");

        /** This argument's type is undefined. */
        public static final Status TYPE_UNDEFINED = new Status("TYPE_UNDEFINED");
        
        /** This argument is burnt. */
        public static final Status BURNT = new Status("BURNT");
        
        /** This argument is connected. */
        public static final Status CONNECTED = new Status("CONNECTED");
        
        /** This argument is connected, but is unused (eg. if it doesn't appear in code gem text). */
        public static final Status CONNECTED_UNUSED = new Status("CONNECTED_UNUSED");

        /** This argument has a connection causing a type clash. */
        public static final Status TYPE_CLASH = new Status("TYPE_CLASH");
    }
    
    /**
     * A simple wrapper class to hold an argument's (string) name and type.
     * @author Edward Lam
     */
    public static final class NameTypePair {
        private final String argName;
        private final TypeExpr type;
        /**
         * Construct an Argument from qualified name and type
         * @param argName String the name of the argument
         * @param type TypeExpr the type 
         */
        public NameTypePair(String argName, TypeExpr type) {
            this.argName = argName;
            this.type = type;
        }
        
        /**
         * Name field accessor.
         * @return the name field of the argument
         */
        public String getName() {
            return argName;
        }

        /**
         * Type field accessor.
         * @return TypeExpr the type field of the argument
         */
        public TypeExpr getType() {
            return type;
        }
    }

    /**
     * This is a class to encapsulate information about an argument during the load process.
     *   This is used in cases where information about the argument is obtained at an earlier stage in the load process
     *   from where the information can actually be used.
     * @author Edward Lam
     */
    public static class LoadInfo {
        /** 
         * Map from gem to its argument info, which is a pair of:
         *   Input identifier: (input gem id, input index)
         *   Other info:       this can be null. 
         * */
        private final Map<Gem, List<Pair<Pair<String, Integer>, Object>>> gemToArgumentInfoListMap = new HashMap<Gem, List<Pair<Pair<String, Integer>, Object>>>();

        /**
         * Trivial constructor for this class.
         */
        public LoadInfo() {
        }

        /**
         * @see #addArgument(Gem, String, Integer, Object) {
         */
        public void addArgument(Gem gem, String inputGemId, Integer inputIndex) {
            addArgument(gem, inputGemId, inputIndex, null);
        }
        
        /**
         * Add an argument to this load info object.
         * @param gem the gem associated with the argument.
         * @param inputGemId the id of the gem on which the input appears.
         * @param inputIndex the index of the inputs on the gem in which it appears.
         * @param otherInfo any other info to associate with the input.  May be null.
         */
        public void addArgument(Gem gem, String inputGemId, Integer inputIndex, Object otherInfo) {
            List<Pair<Pair<String, Integer>, Object>> argumentInfoList = gemToArgumentInfoListMap.get(gem);
            if (argumentInfoList == null) {
                argumentInfoList = new ArrayList<Pair<Pair<String, Integer>, Object>>();
                gemToArgumentInfoListMap.put(gem, argumentInfoList);
            }
            
            Pair<String, Integer> argumentIdPair = new Pair<String, Integer>(inputGemId, inputIndex);
            argumentInfoList.add(new Pair<Pair<String, Integer>, Object>(argumentIdPair, otherInfo));
        }
        
        /**
         * Change the info in this load info object so that getting info for a given gem returns the info for another gem.
         * @param oldGem the gem to be replaced.
         * @param newGem the gem for which, when asked for its info, the info for oldGem will be returned.
         */
        public void remapGem(Gem oldGem, Gem newGem) {
            // This deals with the remapping of the target collector while loading.
            gemToArgumentInfoListMap.put(newGem, gemToArgumentInfoListMap.get(oldGem));
        }
        
        /**
         * Get the number of arguments associated with a given gem.
         * @param gem the gem in question.
         * @return the number of arguments associated with a given gem.
         */
        public int getNArguments(Gem gem) {
            List<Pair<Pair<String, Integer>, Object>> argumentInfoList = gemToArgumentInfoListMap.get(gem);
            if (argumentInfoList == null) {
                return 0;
            }
            return argumentInfoList.size();
        }
        
        /**
         * Get the input information held by this info object.
         * @param gem the gem for which the argument info is relevant.
         * @param argumentIndex the index of the argument within the gem's argument info.
         * @return Pair of (input gem id, input index)
         */
        public Pair<String, Integer> getInputInfo(Gem gem, int argumentIndex) {
            List<Pair<Pair<String, Integer>, Object>> argumentInfoList = gemToArgumentInfoListMap.get(gem);
            return argumentInfoList.get(argumentIndex).fst();
        }

        /**
         * Get the non-input information held by this info object.
         * @param gem the gem for which the argument info is relevant.
         * @param argumentIndex the index of the argument within the gem's argument info.
         * @return the other info associated with the argument - may be null.
         */
        public Object getOtherInfo(Gem gem, int argumentIndex) {
            List<Pair<Pair<String, Integer>, Object>> argumentInfoList = gemToArgumentInfoListMap.get(gem);
            return argumentInfoList.get(argumentIndex).snd();
        }
    }
}

