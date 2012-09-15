#!/bin/bash

#### Change following parameters based on the environment ####

JAVA_HOME=/usr/jdk/jdk1.6.0_21
MULE_HOME=/export/home/admsvs/mule-standalone-3.0.1/

##############################################################

PATH=$PATH:$JAVA_HOME/bin
PATH=$PATH:$MULE_HOME/bin
MULE_LIB=$MULE_HOME/conf

# Export environment variables
export JAVA_HOME MULE_HOME MULE_LIB PATH

### Delete the existing custom components before copying the new one across ###
### This will safegaurd against version updates for these custom components ###
rm -r $MULE_HOME/apps/app-docmgmt/*

mkdir $MULE_HOME/apps/app-docmgmt
mkdir $MULE_HOME/apps/app-docmgmt/lib
mkdir $MULE_HOME/apps/app-docmgmt/classes

### copy all the custom jars to the mule lib\user directory so that mule can load custom components 
cp *.jar $MULE_HOME/apps/app-docmgmt/lib
cp *.properties $MULE_HOME/apps/app-docmgmt/classes
cp conf/*.xml $MULE_HOME/apps/app-docmgmt

$MULE_HOME/bin/mule -app app-docmgmt
