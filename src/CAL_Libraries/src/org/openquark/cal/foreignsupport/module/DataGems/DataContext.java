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
 * DataContext.java
 * Created: 24-Oct-2002
 * By: Richard Webster
 */
package org.openquark.cal.foreignsupport.module.DataGems;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Richard Webster
 */
public class DataContext
{
    private static class SimpleOlapGrid {

        static final int sumSummaryType = 0;
        static final int countSummaryType = 1;

        private final int nDimensions;
        private final int nMeasures;
        private final List<List<String>> dimensionValues;
        private final int [] dimensionSizes;
        private final int [] measureSummaryTypes;
        private final double [] gridValues;

        SimpleOlapGrid (List<List<String>> dimensionValues, List<Integer> measureTypeList) {
            this.nDimensions = dimensionValues.size ();
            this.nMeasures = measureTypeList.size ();
            this.dimensionValues = dimensionValues;
            this.dimensionSizes = new int [nDimensions];
            this.measureSummaryTypes = new int [nMeasures];

            for (int measureN = 0, nMeasures = measureSummaryTypes.length; measureN < nMeasures; ++measureN) {
                measureSummaryTypes [measureN] = measureTypeList.get (measureN).intValue ();
            }

            // Construct the array of grid values.
            int nGridValues = nMeasures;

            for (int dimN = 0; dimN < nDimensions; ++dimN) {
                List<String> valuesForDim = dimensionValues.get (dimN);
                nGridValues *= valuesForDim.size ();

                dimensionSizes [dimN] = valuesForDim.size ();
            }

            this.gridValues = new double [nGridValues];
        }

        void populateGrid (Connection conn, String sqlQuery) throws Exception {
            Statement stmt = conn.createStatement ();
    
            System.out.println ("Executing the query:  " + sqlQuery);
    
            ResultSet rs = stmt.executeQuery (sqlQuery);
            //int nColumns = rs.getMetaData ().getColumnCount ();
    
            while (rs.next ()) {
                processRow (rs);
            }
    
            rs.close ();
        }

        private void processRow (ResultSet rs) throws Exception
        {
            // Determine the index of the value in this row for each dimension.
            int [] dimensionIndexes = new int [nDimensions];

            for (int dimN = 0; dimN < nDimensions; ++dimN)
            {
                List<String> valuesForDim = dimensionValues.get (dimN);

                String columnValue = rs.getString (dimN + 1);

                int dimIndex = valuesForDim.indexOf (columnValue);

                // If the value is not one of the dimension values, then we can ignore this record.
                if (dimIndex < 0) {
                    return;
                }

                dimensionIndexes [dimN] = dimIndex;
            }

            // Update the value for each measure.
            for (int measureN = 0; measureN < nMeasures; ++measureN)
            {
                int columnN = measureN + nDimensions + 1;

                int gridIndex = getIndexOfGridValue (measureN, dimensionIndexes);
                assert (gridIndex < gridValues.length);

                switch (measureSummaryTypes [measureN])
                {
                    case countSummaryType :
                        // For a count summary, update the grid value unless the summarized field has a null value.
                        if (rs.getObject (columnN) != null)
                        {
                            gridValues [gridIndex] = gridValues [gridIndex] + 1;
                        }
                        break;
                        
                    case sumSummaryType :
                        // Get the value of the summarized field and add it to the existing grid value.
                        double fieldValue = rs.getDouble (columnN);

                        if (!rs.wasNull ())
                        {
                            gridValues [gridIndex] = gridValues [gridIndex] + fieldValue;
                        }
                        break;

                    default :
                        assert (false);
                        break;
                }
            }
        }

        private int getIndexOfGridValue (int measureN, int [] dimensionIndexes)
        {
            assert (measureN < nMeasures);

            int index = 0;

            for (int dimN = 0, nDimensions = dimensionIndexes.length; dimN < nDimensions; ++dimN)
            {
                if (dimN > 0) {
                    index *= dimensionSizes [dimN];
                }

                assert (dimensionIndexes [dimN] < dimensionSizes [dimN]);

                index += dimensionIndexes [dimN];
            }

            index *= nMeasures;
            index += measureN;

            return index;   
        }

        double [] getGridValues () {
            return gridValues;
        }
    }


    public static List<Double> fetchGridValues (Connection conn,
                                        String sqlQuery,
                                        List<List<String>> dimValues,
                                        List<Integer> measureTypes) throws Exception {
        SimpleOlapGrid olapGrid = new SimpleOlapGrid (dimValues, measureTypes);
        olapGrid.populateGrid (conn, sqlQuery);

        double [] gridValues = olapGrid.getGridValues ();
        List<Double> valueList = new ArrayList<Double> ();

        for (int valueN = 0, nValues = gridValues.length; valueN < nValues; ++valueN) {
            valueList.add (new Double (gridValues [valueN]));
        }

        return valueList;
    }
}
