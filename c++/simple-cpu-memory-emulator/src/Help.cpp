/*
 * Help.cpp
 *
 *  Created on: Feb 4, 2014
 *      Author: Andrew Paettie
 */

#include "Help.h"
#include <iostream>

Help::Help() {
	// TODO Auto-generated constructor stub
}

void Help::printHelp(){
	std::cout << "Program which simulates CPU and Memory\n"<<
			     "interaction using forking and pipes\n"<<
			     "Usage: ./cpuMemSim <Instruction Filename> <Timer Interrupt Value>\n";
}
