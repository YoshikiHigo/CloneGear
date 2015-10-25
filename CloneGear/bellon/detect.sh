#!/bin/sh
java -ea -Xmx4g -jar CGFinder.jar -src /Users/higo/Desktop/data/versions/bellon/eclipse-ant/src -thd 3 -result higo-eclipse-ant.txt -thrld 30 -gap 1 -bellon -module
java -ea -Xmx4g -jar CGFinder.jar -src /Users/higo/Desktop/data/versions/bellon/eclipse-ant/src -thd 3 -result higo-eclipse-ant2.txt -thrld 30 -gap 1 -bellon 
java -ea -Xmx4g -jar CGFinder.jar -src /Users/higo/Desktop/data/versions/bellon/eclipse-jdtcore/src -thd 3 -result higo-eclipse-jdtcore.txt -thrld 30 -gap 1 -bellon -module
java -ea -Xmx4g -jar CGFinder.jar -src /Users/higo/Desktop/data/versions/bellon/eclipse-jdtcore/src -thd 3 -result higo-eclipse-jdtcore2.txt -thrld 30 -gap 1 -bellon 
java -ea -Xmx4g -jar CGFinder.jar -src /Users/higo/Desktop/data/versions/bellon/netbeans-javadoc/src -thd 3 -result higo-netbeans-javadoc.txt -thrld 30 -gap 1 -bellon -module
java -ea -Xmx4g -jar CGFinder.jar -src /Users/higo/Desktop/data/versions/bellon/netbeans-javadoc/src -thd 3 -result higo-netbeans-javadoc2.txt -thrld 30 -gap 1 -bellon 
java -ea -Xmx4g -jar CGFinder.jar -src /Users/higo/Desktop/data/versions/bellon/j2sdk1.4.0-javax-swing/src -thd 3 -result higo-j2sdk1.4.0-javax-swing.txt -thrld 30 -gap 1 -bellon -module
java -ea -Xmx4g -jar CGFinder.jar -src /Users/higo/Desktop/data/versions/bellon/j2sdk1.4.0-javax-swing/src -thd 3 -result higo-j2sdk1.4.0-javax-swing2.txt -thrld 30 -gap 1 -bellon 
perl -pi -e "s/\/Users\/higo\/Desktop\/data\/versions\/bellon\///g" higo-eclipse-ant.txt
perl -pi -e "s/\/Users\/higo\/Desktop\/data\/versions\/bellon\///g" higo-eclipse-ant2.txt
perl -pi -e "s/\/Users\/higo\/Desktop\/data\/versions\/bellon\///g" higo-eclipse-jdtcore.txt
perl -pi -e "s/\/Users\/higo\/Desktop\/data\/versions\/bellon\///g" higo-eclipse-jdtcore2.txt
perl -pi -e "s/\/Users\/higo\/Desktop\/data\/versions\/bellon\///g" higo-netbeans-javadoc.txt
perl -pi -e "s/\/Users\/higo\/Desktop\/data\/versions\/bellon\///g" higo-netbeans-javadoc2.txt
perl -pi -e "s/\/Users\/higo\/Desktop\/data\/versions\/bellon\///g" higo-j2sdk1.4.0-javax-swing.txt
perl -pi -e "s/\/Users\/higo\/Desktop\/data\/versions\/bellon\///g" higo-j2sdk1.4.0-javax-swing2.txt
