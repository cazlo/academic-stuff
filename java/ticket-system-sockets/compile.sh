#!/bin/bash

echo "Removing old class files"
rm ticketSystem/*.class
echo "Compiling java source"
javac  -d . ticketSystem/*.java 
