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
 * TextFileMessageSource.java
 * Created: 5-Mar-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.openquark.cal.services.NullaryEnvironment;
import org.openquark.cal.services.ResourcePath;
import org.openquark.samples.bam.model.MessageSourceDescription;
import org.openquark.samples.bam.model.TextFileMessageSourceDescription;
import org.openquark.samples.bam.model.MessageSourceDescription.MessagePropertyDescription;



/**
 * This message source reads messages form a text file.
 * 
 */
public class TextFileMessageSource extends AbstractMessageSource {
    
    private static class ColumnDef {
        final String name;
        
        final int type;
        
        ColumnDef (String name, int type) {
            this.name = name;
            this.type = type;
        }
    }
    
    private final File inputFile; 
    
    private ArrayList<ColumnDef> columns = new ArrayList<ColumnDef> (); // of ColumnDefs

    private final String messageType; 

    private boolean cancelled = false;
    
    static TextFileMessageSource createInstance (MessageSourceDescription messageSourceDescription) {
        if (messageSourceDescription instanceof TextFileMessageSourceDescription) {
            String textFilename = ((TextFileMessageSourceDescription)messageSourceDescription).getFileName();
            
            return new TextFileMessageSource (textFilename, messageSourceDescription.getName());
        } else {
            return null;
        }
    }
    
    public static Collection<MessagePropertyDescription> getMessagePropertyInfos (TextFileMessageSourceDescription messageSourceDescription) {
        Collection<MessagePropertyDescription> result = null;
        
        TextFileMessageSource messageSource = createInstance(messageSourceDescription);
        
        if (messageSource != null) {
            try {
                result = messageSource.getMessagePropertyInfos();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        return result;
    }


    public TextFileMessageSource (String fileName, String messageType) {
        ResourcePath.FilePath filePath = new ResourcePath.FilePath(fileName.split("/|\\\\"));
        inputFile = NullaryEnvironment.getNullaryEnvironment().getFile(filePath, false);
        this.messageType = messageType;
    }
    
    private Collection<MessagePropertyDescription> getMessagePropertyInfos () throws IOException {
        Collection<MessagePropertyDescription> result = null;
        
        FileReader reader;
        reader = new FileReader (inputFile);
        
        BufferedReader bufferedReader = new BufferedReader (reader);

        try {
            processHeader (bufferedReader);
            
            if (columns.size () > 0) {
                result = new ArrayList<MessagePropertyDescription> ();
                
                for (final ColumnDef columnDef : columns) {
                    result.add (new MessagePropertyDescription (columnDef.name, columnDef.type));
                }
            }
        } finally {
            bufferedReader.close ();
        }
        
        return result;
    }
    
    /**
     * @see org.openquark.samples.bam.MessageSource#start()
     */
    public boolean start() {
        run ();
        
        return true;
    }

    /**
     * @see org.openquark.samples.bam.MessageSource#stop()
     */
    public boolean stop() {
        cancelled = true;
        
        return true;
    }

    private void run () {
        cancelled = false;
        
        Thread t = new Thread () {

            /**
             * @see java.lang.Thread#run()
             */
            @Override
            public void run () {
                FileReader reader;
                try {
                    reader = new FileReader (inputFile);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    
                    return;
                }
                
                BufferedReader bufferedReader = new BufferedReader (reader);

                try {
                    fireStatusChanged(STATUS_RUNNING);
                    
                    processHeader (bufferedReader);
                    processBody (bufferedReader);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } finally {
                    fireStatusChanged(STATUS_IDLE);
                    
                    try {
                        bufferedReader.close();
                    } catch (IOException e2) {
                        // TODO Auto-generated catch block
                        e2.printStackTrace();
                    }
                }
            }
        };
        
        t.start();
        
    }

    /**
     * Method processHeader
     * 
     * @param bufferedReader
     */
    private void processHeader (BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        
        while (line != null) {
            if (!isComment (line)) {
                String [] pieces = line.split(",");
                
                if (pieces.length == 2) {
                    int type = parseType (pieces [1]);
                    
                    columns.add (new ColumnDef (pieces[0], type));
                } else {
                    break;
                }
            }
            
            line = bufferedReader.readLine();
        }
    }

    /**
     * Method parseType
     * 
     * @param string
     * @return Returns the code for the data type represented by the given String
     */
    private int parseType (String string) {
        switch (string.charAt (0)) {
            case 's':
            case 'S':
                return Message.STRING;
                
            case 'i':
            case 'I':
                return Message.INT;

            case 'l':
            case 'L':
                return Message.LONG;

            case 'd':
            case 'D':
                return Message.DOUBLE;

            default:
                throw new IllegalArgumentException ("Invalid type specifier: " + string);
        }
    }

    /**
     * Method isComment
     * 
     * @param line
     * @return Returns true iff this line is a comment
     */
    private boolean isComment (String line) {
        return line.length() == 0 || line.startsWith("//");
    }

    /**
     * Method processBody
     * 
     * @param bufferedReader
     */
    private void processBody (BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        
        while (line != null && !cancelled) {
            if (!isComment (line)) {
                processLine (line);
            }                
                
            line = bufferedReader.readLine();
        }
    }

    /**
     * Method processLine
     * 
     * @param line
     */
    private void processLine (String line) {
        DefaultMessage message = new DefaultMessage (messageType);
        
        String [] pieces = line.split(",");
        
        if (pieces.length != columns.size()) {
            throw new IllegalArgumentException ("Line does not match schema: " + line);
        }
        
        for (int i = 0; i < pieces.length; i++) {
            String string = pieces[i];
            ColumnDef columnDef = columns.get(i);
            
            message.addValue(columnDef.name, decodeValue (string, columnDef.type));
        }
        
        fireMessageReceived(message);
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Method decodeValue
     * 
     */
    private Object decodeValue (String string, int type) {
        switch (type) {
            case Message.STRING:
                return string;
                
            case Message.INT:
                return new Integer (Integer.parseInt(string));
            
            case Message.LONG:
                return new Long (Long.parseLong(string));
            
            case Message.DOUBLE:
                return new Double (Double.parseDouble(string));
            
            default:
                throw new IllegalArgumentException ("Unknown type: " + type);
        }
    }

}
