# Copyright (c) 2007 BUSINESS OBJECTS SOFTWARE LIMITED
# All rights reserved.
# 
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
# 
#     * Redistributions of source code must retain the above copyright notice,
#       this list of conditions and the following disclaimer.
#  
#     * Redistributions in binary form must reproduce the above copyright
#       notice, this list of conditions and the following disclaimer in the
#       documentation and/or other materials provided with the distribution.
#  
#     * Neither the name of Business Objects nor the names of its contributors
#       may be used to endorse or promote products derived from this software
#       without specific prior written permission.
#  
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.

#this script can be run on Linux, or Windows (using cygwin)
#it takes a list of zip files and generates benchmark timing files
#for each of the benchmarks listed in benchmarks.ice.

#this script should be run with a list of zip files representing builds, e.g.
#comparebuilds.sh build1.zip build2.zip ....


#these are the arguments used to start ICE for each benchmark
export vmargs="-Xms512M -Xmx512M -Xss1M -Dorg.openquark.cal.machine.lecc.non_interruptible"

#these define the number of times to re-run the benchmark suite for each build
#and the labels that are used
#you can add or remove labels to increase the quality of the results or decrease the
#time taken to obtain them
runlabels="_1 _2 _3 _4 _5"

#this defines the number of times each benchmark is run by the :pt command
#increasing this will improve the quality of the generated statistics but
#will take longer to run.
numPtRuns=10

#show usage information
if [ $# -lt 1 ]; then
    echo "Usage $0 <zipfile to benchmark>..."
    echo "If a zip file name contains the string 'con', it will be run with the concurrent runtime flag"
    exit 1
fi

#get the type of system we're running on
system=`uname`

if [ "$system" == "Linux" ] ; then
startice=startice.sh
else
startice=startice.bat
fi


#extract the zip archives
for j in $*; do
    name=`basename $j .zip`
    unzip -u $j -d $name
    
    #remove all shipped cmi and class files
    find $name -name "*.class" | xargs rm -f 
    find $name -name "*.cmi" | xargs rm -f 
done

for i in $runlabels; do
    for j in $*; do

        name=`basename $j .zip`

        #remove old perf file
        rm -f $name$i

        #run ice with each benchmark in the pt file
        cat benchmarks.ice | grep '^:pt\|^:sm\|^-D'  | 
        while read ptCommand ; do

            #keep track of which module we should be in
            if [ `echo $ptCommand | grep -c :sm` == 1 ]  ; then
                smCommand=$ptCommand
                continue
            fi

            #keep track of -D commands
            if [ `echo $ptCommand | grep -c -- ^-D` == 1 ]  ; then
                defines=$ptCommand
                continue
            fi

            #if the zip file name contains the "con" string run it with the concurtrent flag
            if [ `echo $name | grep -c con` -ne 0 ]; then
                echo Running with Concurrent
                defines="-Dorg.openquark.cal.machine.lecc.concurrent_runtime"
            else
                echo Running non-Concurrent
                defines=""
            fi

            #make the benchmark script for just this pt command
            rm -f tempResult
            echo ":brf ../../tempResult" > benchscript
            echo ":brl $name$i" >> benchscript
            echo ":sptr $numPtRuns" >> benchscript
            echo $smCommand >> benchscript
            echo $ptCommand >> benchscript
            echo ":q" >> benchscript

            echo "Benchmark run: " $name$i
            cp $startice $name/Quark
            pushd $name/Quark

            cat ../../benchscript
            ./$startice $defines org.openquark.cal.ICE -script ../../benchscript &> ../../$name$i.log &
            wait $!
            popd

            #concat intermediate results.
            cat tempResult >> $name$i
        done

    done
done

#build and display an ICE command for comparing the generated benchmark timing files
last=""
args="["
sep1=""
for j in $*; do
    sep2=""
    name=`basename $j .zip`
    last=$name
    args=$args$sep1"["
    for i in $runlabels; do
       file=`pwd`"/"$name$i

       #fix paths for windows
       if [ "$system" != "Linux" ]; then
           file=`cygpath -m $file`
       fi

       args=$args$sep2'"'$file'"'
       sep2=", "
    done
    args=$args"]"
    sep1=", "
done
args=$args"]"

echo ":sm Cal.Benchmarks.Internal.Report" > run.cmd
echo "Cal.Benchmarks.Internal.Report.compareBenchmarkBuilds " $args >> run.cmd
echo ":q" $args >> run.cmd
cat run.cmd

#produce the results.txt file
cp $startice $last/Quark
pushd $last/Quark
cat ../../benchscript
./$startice org.openquark.cal.ICE -script ../../run.cmd &> ../../results.txt &
wait $!
popd

