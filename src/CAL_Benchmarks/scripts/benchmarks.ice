// Copyright (c) 2007 BUSINESS OBJECTS SOFTWARE LIMITED
// All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// 
//     * Redistributions of source code must retain the above copyright notice,
//       this list of conditions and the following disclaimer.
//  
//     * Redistributions in binary form must reproduce the above copyright
//       notice, this list of conditions and the following disclaimer in the
//       documentation and/or other materials provided with the distribution.
//  
//     * Neither the name of Business Objects nor the names of its contributors
//       may be used to endorse or promote products derived from this software
//       without specific prior written permission.
//  
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.


// This is a special ICE script that runs standard benchmarks with
// standard options. IT is interpreted by the comparebuild.sh script.
// it allows :pt commands, :sm commands, and -D defines. 
// each :pt command is run separately, with the preceding :sm and -D command.


// Run the performance related benchmarks.
:sptr 5
:sm Cal.Benchmarks.Internal.Benchmarks
:pt BonusRatings.bonusRatedPeople (BonusRatings.peopleScoresN 800)
:pt Benchmarks.badRegressionGemTest 500
:pt Benchmarks.getNthPrime 20000
:pt last (take 6000000 (upFrom 1.0))
:pt Benchmarks.foreignFunctionTester 60000000
:pt Benchmarks.primitiveOpTester 60000000
:pt sumIntBoxes (incrementIntBoxes (buildIntBoxTree 14))
:pt buildIntBoxTree 12
:pt primitiveOpOverhead2Helper 60000000
:pt letTestHelper 10000000
:pt applyTest 1.0 2.0 60000000
:pt benchmarkAverage (upFromTo 1.0 2000000.0)
:pt benchmarkAverage (expandedDoubleList 2000000)
:pt benchmarkPartialApplications 200000.0
:pt dynamicMapLookupBenchmark 1000000
:pt letVarBenchmark_1_a 30000000
:pt letVarBenchmark_1_b 30000000
:pt letVarBenchmark_2_a 30000000
:pt letVarBenchmark_2_b 30000000
:pt letVarBenchmark_3_a 30000000
:pt letVarBenchmark_3_b 30000000
:pt letVarBenchmark_4_a 30000000
:pt letVarBenchmark_4_b 30000000
:pt letVarBenchmark_5_a 30000000
:pt letVarBenchmark_5_b 30000000
:pt letVarBenchmark_6_a 30000000
:pt letVarBenchmark_6_b 30000000
:pt letVarBenchmark_7_a 30000000
:pt letVarBenchmark_7_b 30000000
:pt letVarScopingBenchmark_1 3000000
:pt letVarScopingBenchmark_2_a 9000000
:pt letVarScopingBenchmark_2_b 9000000
:pt letVarScopingBenchmark_3_a 9000000
:pt letVarScopingBenchmark_3_b 9000000

:sm CAL_Platform_TestSuite
:pt CAL_Platform_TestSuite.testModule

:sm M2

//uses a java quicksort to sort a java int array
:pt intArray_javaQuicksort (randomIntArray 2000000)

//uses a pure CAL quicksort which is a port of the java quicksort to sort a java int array
//the goal is for the performance of this benchmark to match the intArray_javaQuicksort benchmark above
:pt quicksortIntArray_v1 (randomIntArray 2000000)

//uses a pure CAL quicksort which is another port of the java quicksort to sort a java int array
//the goal is for the performance of this benchmark to match the intArray_javaQuicksort benchmark above
:pt quicksortIntArray_v3 (randomIntArray 2000000)

//uses a java sieve based prime number generator
:pt java_sieveBasedGetNthPrime 300000

//uses a CAL sieve based prime number generator that is a close port of the java sieve based prime generator
//the goal if for the performance of this benchmark to match the java_sieveBasedGetNthPrime benchmark above 
:pt sieveBasedGetNthPrime 300000

//uses a CAL sieve based prime number generator that is a close port of the java sieve based prime generator
//the goal if for the performance of this benchmark to match the java_sieveBasedGetNthPrime benchmark above 
:pt sieveBasedGetNthPrime2 300000

:sm Cal.Test.General.Nofib
:pt List.subscript digits_of_e1 800
:pt exp3 8


:sm Cal.Test.Collections.IntMap_Tests
:pt unionBenchmark 1
:pt intersectionBenchmark 1
:pt lookupBenchmark 1
:pt filterBenchmark 1
:pt partitionBenchmark 1
:pt deleteBenchmark 1
:pt fromListBenchmark 1
:pt fromAscListBenchmark 1
:pt fromDistinctAscListBenchmark 1

:sm Cal.Test.Collections.LongMap_Tests
:pt unionBenchmark 1
:pt intersectionBenchmark 1
:pt lookupBenchmark 1
:pt filterBenchmark 1
:pt partitionBenchmark 1
:pt deleteBenchmark 1
:pt fromListBenchmark 1
:pt fromAscListBenchmark 1
:pt fromDistinctAscListBenchmark 1

//run the Shootout benchmarks
:sm TestHarness
:pt runOne bigTests "binarytrees" CAL
:pt runOne bigTests "fannkuch" CAL
:pt runOne bigTests "fasta" CAL
:pt runOne bigTests "knucleotide" CAL
:pt runOne bigTests "mandelbrot" CAL
:pt runOne bigTests "nbody" CAL
:pt runOne bigTests "nsieve" CAL
:pt runOne bigTests "nsievebits" CAL
:pt runOne bigTests "partialsums" CAL
:pt runOne bigTests "pidigits" CAL
:pt runOne bigTests "recursive" CAL
:pt runOne bigTests "regexdna" CAL
:pt runOne bigTests "spectralnorm" CAL
:pt runOne bigTests "sumcol" CAL
:pt runOne bigTests "revcomp" CAL
:pt runOne bigTests "meteor" CAL

//we must use concurrent runtime for these benchmarks
//-Dorg.openquark.cal.machine.lecc.concurrent_runtime
//:pt runOne bigTests "chameneos" CAL
//:pt runOne bigTests "message" CAL



