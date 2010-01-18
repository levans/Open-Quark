
#this script process benchmark files for submission to the Benchmark game site
#it renames the files to be all lower case
#it changes the module name to the the the name lowercase with the first letter uppercase
#it replaces all leading comments with the comment given below between COMMENT tags
for i in $*; do 
echo $i

bname=`basename $i .cal`
lname=`echo $bname | sed 's/\(.*\)/\L&/g'`
uname=`echo $lname | sed 's/\(^\| \)./\U&/g'`

cp $i $i~

#strip leading comments
sed '/import.*/,$!d' < $i > $i.tmp

#make new comment
rm $i
cat > $lname.cal << COMMENT
/** 
 * The Computer Language Benchmarks Game 
 * Based on the CAL Open Quark version
 * Contributed by Magnus Byne.
 */
COMMENT

#add new module name (no package)
echo module $uname";" >> $lname.cal
echo >> $lname.cal

#add body of module
cat $i.tmp >> $lname.cal

#remove tmp file
rm  $i.tmp

done