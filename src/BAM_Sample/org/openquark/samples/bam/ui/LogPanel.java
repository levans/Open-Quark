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
 * LogPanel.java
 * Created: 15-Apr-2004
 * By: Rick Cameron
 */

package org.openquark.samples.bam.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * 
 *  
 */
class LogPanel extends JPanel {

    private static final long serialVersionUID = 3163963438353684852L;

    private static class LogFormatter extends Formatter {
        
        private final static String format = "{0,date} {0,time}";
        private final static String lineSeparator = "\n";
        
        private Date dat = new Date ();
        private MessageFormat formatter;

        private Object args[] = new Object[1];

        /**
         * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
         */
        @Override
        public String format (LogRecord record) {
            StringBuilder sb = new StringBuilder ();
            // Minimize memory allocations here.
            dat.setTime (record.getMillis ());
            args[0] = dat;
            StringBuffer text = new StringBuffer ();
            if (formatter == null) {
                formatter = new MessageFormat (format);
            }
            formatter.format (args, text, null);
            sb.append (text);
            sb.append (" ");
//            if (record.getSourceClassName () != null) {
//                sb.append (record.getSourceClassName ());
//            } else {
//                sb.append (record.getLoggerName ());
//            }
//            if (record.getSourceMethodName () != null) {
//                sb.append (" ");
//                sb.append (record.getSourceMethodName ());
//            }
//            sb.append (lineSeparator);
            String message = formatMessage (record);
            sb.append (record.getLevel ().getLocalizedName ());
            sb.append (": ");
            sb.append (message);
            sb.append (lineSeparator);
//            if (record.getThrown () != null) {
//                try {
//                    StringWriter sw = new StringWriter ();
//                    PrintWriter pw = new PrintWriter (sw);
//                    record.getThrown ().printStackTrace (pw);
//                    pw.close ();
//                    sb.append (sw.toString ());
//                } catch (Exception ex) {
//                }
//            }
            return sb.toString ();
        }

    }

    private class LogHandler extends Handler {

        LogHandler () {
            setFormatter (new LogFormatter ());
        }

        @Override
        public void close () throws SecurityException {
            // TODO Auto-generated method stub

        }

        @Override
        public void flush () {
            // TODO Auto-generated method stub

        }

        @Override
        public void publish (LogRecord record) {
            String string = getFormatter ().format (record);

            messageArea.append (string);
        }
    }

    private final JTextArea messageArea;

    LogPanel () {
        super (new BorderLayout ());

        messageArea = makeMessageArea ();
    }

    /**
     * Method makeMessageArea
     * 
     * @return Returns the JTextArea that will display log messages
     */
    private JTextArea makeMessageArea () {
        JTextArea textArea = new JTextArea ();

        textArea.setEditable (false);

        add (new JScrollPane (textArea), BorderLayout.CENTER);

        return textArea;
    }

    Handler getLogHandler () {
        return new LogHandler ();
    }

    /**
     * Method getClearLogAction
     * 
     * @return Returns the Action that clears the log
     */
    public Action getClearLogAction () {
        return new AbstractAction ("Clear Log") {

            private static final long serialVersionUID = -2219816441739096055L;

            /**
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed (ActionEvent e) {
                onClearLog ();
            }
        };
    }

    /**
     * Method onClearLog
     * 
     * 
     */
    protected void onClearLog () {
        messageArea.setText("");
    }

}