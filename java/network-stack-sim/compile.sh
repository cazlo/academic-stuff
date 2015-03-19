#!/bin/sh 

echo "Removing old class"
rm simpleNetworkSim/*.class 2>/dev/null
echo "Compiling"
javac -d . simpleNetworkSim/*.java
