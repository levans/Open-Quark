#!/bin/bash

#This script is for the Computer Language Benchmarks Game site.
#http://shootout.alioth.debian.org/gp4/index.php
#
#It will take a CAL file (which is all lower case, as required by the site's makefiles), 
#copy it to an appropriately named file (first letter uppercase), 
#create a workspace declaration for it and directory structure, and compile it to create 
#a .jar with the same name. The jar will have with a main called <benchmark>.main
#
#To use this script you should have the unpacked Quark 1.5 bin distribution on your path

if [ $# != 1 ]; then
    echo "Usage $0 <calbenchmark>"
    exit 1
fi

#create directory structure for source and workspace decl. 
mkdir -p tmp/cal/src/CAL "tmp/cal/src/Workspace Declarations"

#copy the cal source file to the src dir and capitalize the first letter of the file
bname=`basename $1 .cal`
uname=`echo $bname | sed 's/\(^\| \)./\U&/g'`
cp $bname.cal tmp/cal/src/CAL/$uname.cal

#create a workspace file for just this benchmark
cat > "tmp/cal/src/Workspace Declarations/cal.$bname.cws" << END
StandardVault $uname
import StandardVault cal.libraries.cws
END

#compile the benchmark
QUARK_CP=tmp/cal/src QUARK_JAVACMD=java quarkc.sh cal.$bname.cws -main  $uname.main $bname.main $bname.jar
