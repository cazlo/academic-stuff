#!/bin/sh 

echo "Removing previous channels"
rm from?to? 2>/dev/null
echo "Removing previous logs"
rm *.nlog 2>/dev/null
rm *.elog 2>/dev/null

#echo "Running scenario 1: 1 node sending message to neighbor"
#java simpleNetworkSim.Node 0 30 1 "The Simplest thing in the world is SSS" 1 > Log0.nlog 2> Err0.elog &
#java simpleNetworkSim.Node 1 30 1 0 > Log1.nlog 2> Err1.elog &

#echo "Running scenario 2: 6 nodes, 1 sender "
#java simpleNetworkSim.Node 0 60 5 "Message Sent Over" 1 2 > Log0.nlog 2> Err0.elog&
#java simpleNetworkSim.Node 1 60 1 0 2 > Log1.nlog 2> Err1.elog&
#java simpleNetworkSim.Node 2 60 2 0 1 3 4 > Log2.nlog 2> Err2.elog&
#java simpleNetworkSim.Node 3 60 3 2 4 6 > Log3.nlog 2> Err3.elog&
#java simpleNetworkSim.Node 4 60 4 2 3 5 > Log4.nlog 2> Err4.elog&
#java simpleNetworkSim.Node 5 60 5 4 6 > Log5.nlog 2> Err5.elog&
#java simpleNetworkSim.Node 6 60 6 3 5 > Log6.nlog 2> Err6.elog&
#(sleep 30 && java simpleNetworkSim.Node 5 30 5 4 6 > Log5.nlog 2> Err5.elog&)&

echo "Running scenario 3: 5 nodes, 3 senders, 1 rcv"
java simpleNetworkSim.Node 0 60 0 1 2 3 4 > Log0.nlog 2> Err0.elog&
java simpleNetworkSim.Node 4 60 4 0 > Log4.nlog 2> Err4.elog&
java simpleNetworkSim.Node 1 60 4 "StringFrom1"  0 > Log1.nlog 2> Err1.elog&
java simpleNetworkSim.Node 2 60 4 "StringFrom2"  0 > Log2.nlog 2> Err2.elog&
java simpleNetworkSim.Node 3 60 4 "StringFrom3"  0 > Log3.nlog 2> Err3.elog&
