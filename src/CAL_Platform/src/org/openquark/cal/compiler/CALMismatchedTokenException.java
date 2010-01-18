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
 * CALMismatchedTokenException.java
 * Created: Apr 29, 2003
 * By: Bo Ilic
 */
package org.openquark.cal.compiler;

import antlr.MismatchedTokenException;
import antlr.Token;

/**
 * The purpose of this class is to allow for localizing the messages given by
 * the antlr class MismatchedTokenException, as well as to allow for helpful
 * CAL specific details to be included in the message.
 * @author Bo Ilic
 */
public final class CALMismatchedTokenException extends MismatchedTokenException {
    
    private static final long serialVersionUID = 695485710573567245L;
    
    /** Token names array for formatting */
    private String[] tokenNames;

    /**
     * @param tokenNames_
     * @param token_
     * @param expecting_
     * @param fileName_
     */
    public CALMismatchedTokenException(String[] tokenNames_, Token token_, int expecting_, String fileName_) {
        super(tokenNames_, token_, expecting_, false, fileName_);
        
        tokenNames = tokenNames_;   
    }
    
    /**
     * Returns a clean error message (no line number/column information)
     */
    @Override
    public String getMessage() {

        if (mismatchType != TOKEN) {
            throw new IllegalStateException();
        }

        StringBuilder sb = new StringBuilder();

        sb.append("expecting " + tokenName(expecting) + ", found " + tokenName(token));

        return sb.toString();
    }
     
    /**
     * @param tokenType
     * @return the name of the token, with an indication whether it is a keyword.
     */
    private String tokenName(int tokenType) {

        String tokenName = tokenNames[tokenType];
        int tokenLength = tokenName.length();
        if (tokenLength > 1 && tokenName.charAt(0) == '"' && tokenName.charAt(tokenLength-1) == '"' &&
            LanguageInfo.isKeyword(tokenName.substring(1, tokenName.length()-1))) {
                return "keyword " + tokenName;
        }
        
        return tokenName;        
    }
    
    /**
     * @param token
     * @return the name of the token, with an indication whether it is a keyword.
     */    
    private String tokenName(Token token){
        String tokenText = token.getText();
        if (LanguageInfo.isKeyword(tokenText)) {
            return "keyword \"" + tokenText + "\"";
        } 
        
        return tokenText;  
    }
}
