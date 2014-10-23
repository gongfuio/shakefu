#!/usr/bin/env bash

# Download the Toxiclibs for Processing and install them in the local repository
# 
# The libraries can then be added to the `project.clj` descriptor as normal dependencies:
#
#   :dependencies [
#     ...
#     [toxiclibs/toxiclibscore "0020"]
#     [toxiclibs/toxiclibs-p5 "0003"]
#     ... ]
#
# See http://hg.postspectacular.com/toxiclibs/wiki/Home to check for the version numbers of the artifacts 

TOXICLIB_ARCHIVE=toxiclibs-complete-0020
wget -O lib/$TOXICLIB_ARCHIVE.zip http://hg.postspectacular.com/toxiclibs/downloads/toxiclibs-complete-0020.zip
unzip $TOXICLIB_ARCHIVE.zip
mvn install:install-file -Dfile=lib/$TOXICLIB_ARCHIVE/toxiclibscore/library/toxiclibscore.jar -DgroupId=toxiclibs -DartifactId=toxiclibscore -Dversion=0020 -Dpackaging=jar 
mvn install:install-file -Dfile=lib/$TOXICLIB_ARCHIVE/toxiclibs_p5/library/toxiclibs_p5.jar -DgroupId=toxiclibs -DartifactId=toxiclibs-p5 -Dversion=0003 -Dpackaging=jar 
mvn install:install-file -Dfile=lib/$TOXICLIB_ARCHIVE/verletphysics/library/verletphysics.jar -DgroupId=toxiclibs -DartifactId=verletphysics -Dversion=0010 -Dpackaging=jar 
mvn install:install-file -Dfile=lib/$TOXICLIB_ARCHIVE/colorutils/library/colorutils.jar -DgroupId=toxiclibs -DartifactId=colorutils -Dversion=0009 -Dpackaging=jar 
mvn install:install-file -Dfile=lib/$TOXICLIB_ARCHIVE/datautils/library/datautils.jar -DgroupId=toxiclibs -DartifactId=datautils -Dversion=0002 -Dpackaging=jar 
mvn install:install-file -Dfile=lib/$TOXICLIB_ARCHIVE/simutils/library/simutils.jar -DgroupId=toxiclibs -DartifactId=simutils -Dversion=0003 -Dpackaging=jar 
mvn install:install-file -Dfile=lib/$TOXICLIB_ARCHIVE/volumeutils/library/volumeutils.jar -DgroupId=toxiclibs -DartifactId=volumeutils -Dversion=0006 -Dpackaging=jar 
mvn install:install-file -Dfile=lib/$TOXICLIB_ARCHIVE/audioutils/library/audioutils.jar -DgroupId=toxiclibs -DartifactId=audioutils -Dversion=0008 -Dpackaging=jar 
