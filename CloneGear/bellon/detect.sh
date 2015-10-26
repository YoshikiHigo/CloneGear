#!/bin/sh
java -ea -Xmx12g -jar CGFinder.jar -src /Users/higo/Desktop/data/versions/bellon/eclipse-ant/src -thd 3 -result higo-eclipse-ant.cpf -thrld 30 -gap 1 -bellon 
java -ea -Xmx12g -jar CGFinder.jar -src /Users/higo/Desktop/data/versions/bellon/eclipse-ant/src -thd 3 -result higo-eclipse-ant-nofolding.cpf -thrld 30 -gap 1 -bellon -folding
java -ea -Xmx12g -jar CGFinder.jar -src /Users/higo/Desktop/data/versions/bellon/eclipse-jdtcore/src -thd 3 -result higo-eclipse-jdtcore.cpf -thrld 30 -gap 1 -bellon 
java -ea -Xmx12g -jar CGFinder.jar -src /Users/higo/Desktop/data/versions/bellon/eclipse-jdtcore/src -thd 3 -result higo-eclipse-jdtcore-nofolding.cpf -thrld 30 -gap 1 -bellon -folding
java -ea -Xmx12g -jar CGFinder.jar -src /Users/higo/Desktop/data/versions/bellon/netbeans-javadoc/src -thd 3 -result higo-netbeans-javadoc.cpf -thrld 30 -gap 1 -bellon 
java -ea -Xmx12g -jar CGFinder.jar -src /Users/higo/Desktop/data/versions/bellon/netbeans-javadoc/src -thd 3 -result higo-netbeans-javadoc-nofolding.cpf -thrld 30 -gap 1 -bellon -folding
java -ea -Xmx12g -jar CGFinder.jar -src /Users/higo/Desktop/data/versions/bellon/j2sdk1.4.0-javax-swing/src -thd 3 -result higo-j2sdk1.4.0-javax-swing.cpf -thrld 30 -gap 1 -bellon 
java -ea -Xmx12g -jar CGFinder.jar -src /Users/higo/Desktop/data/versions/bellon/j2sdk1.4.0-javax-swing/src -thd 3 -result higo-j2sdk1.4.0-javax-swing-nofolding.cpf -thrld 30 -gap 1 -bellon -folding
java -ea -Xmx12g -jar CGFinder.jar -src /Users/higo/Desktop/data/versions/bellon/cook/src -thd 3 -result higo-cook.cpf -thrld 30 -gap 1 -bellon 
java -ea -Xmx12g -jar CGFinder.jar -src /Users/higo/Desktop/data/versions/bellon/snns/src -thd 3 -result higo-snns.cpf -thrld 30 -gap 1 -bellon 
java -ea -Xmx12g -jar CGFinder.jar -src /Users/higo/Desktop/data/versions/bellon/postgresql/src -thd 3 -result higo-postgresql.cpf -thrld 30 -gap 1 -bellon 
java -ea -Xmx12g -jar CGFinder.jar -src /Users/higo/Desktop/data/versions/bellon/weltab/src -thd 3 -result higo-weltab.cpf -thrld 30 -gap 1 -bellon 
perl -pi -e "s/\/Users\/higo\/Desktop\/data\/versions\/bellon\///g" higo-eclipse-ant.cpf
perl -pi -e "s/\/Users\/higo\/Desktop\/data\/versions\/bellon\///g" higo-eclipse-ant-nofolding.cpf
perl -pi -e "s/\/Users\/higo\/Desktop\/data\/versions\/bellon\///g" higo-eclipse-jdtcore.cpf
perl -pi -e "s/\/Users\/higo\/Desktop\/data\/versions\/bellon\///g" higo-eclipse-jdtcore-nofolding.cpf
perl -pi -e "s/\/Users\/higo\/Desktop\/data\/versions\/bellon\///g" higo-netbeans-javadoc.cpf
perl -pi -e "s/\/Users\/higo\/Desktop\/data\/versions\/bellon\///g" higo-netbeans-javadoc-nofolding.cpf
perl -pi -e "s/\/Users\/higo\/Desktop\/data\/versions\/bellon\///g" higo-j2sdk1.4.0-javax-swing.cpf
perl -pi -e "s/\/Users\/higo\/Desktop\/data\/versions\/bellon\///g" higo-j2sdk1.4.0-javax-swing-nofolding.cpf
perl -pi -e "s/\/Users\/higo\/Desktop\/data\/versions\/bellon\///g" higo-cook.cpf
perl -pi -e "s/\/Users\/higo\/Desktop\/data\/versions\/bellon\///g" higo-snns.cpf
perl -pi -e "s/\/Users\/higo\/Desktop\/data\/versions\/bellon\///g" higo-postgresql.cpf
perl -pi -e "s/\/Users\/higo\/Desktop\/data\/versions\/bellon\///g" higo-weltab.cpf
