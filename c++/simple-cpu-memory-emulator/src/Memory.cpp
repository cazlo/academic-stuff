/*
 * Memory.cpp
 *
 *  Created on: Feb 4, 2014
 *      Author: Andrew Paettie
 */

#include "Memory.h"
#include "CPU.h"
#include <string.h>
#include <cstdio>
#include <cstdlib>
#include <fstream>
#include <string>
#include <sstream>

Memory::Memory() {
	clearMem();
}

Memory::~Memory() {
	// TODO Auto-generated destructor stub
}

void Memory::clearMem(){
	for (int i = 0; i < MEMORY_SIZE; i ++){
		this->memArray[i] = 0;
	}
}

//the initial loading of instructions into memory from a file
//returns the number of instructions read in
int Memory::loadInstructions(char * filename){
	//TODO
	int numInstr = 0;
	int currAddress = 0;
	std::ifstream file(filename);
	std::string line = " ";

	while(std::getline(file, line)){
		//const char * cLine = line.c_str();
		std::string instrStr = "";
		int instr = 0;
		double directiveTest = 0.0;
		std::istringstream iss(line);

		if (!(iss >> directiveTest)){
			//not a double parsable value; try to parse it into an unsigned int
		}else{
			if (directiveTest < 1 && directiveTest >= 0 &&
				line.find('.', 0) != std::string::npos){//encountered a directive
				instrStr = line.substr(line.find_first_of('.')+1, -1);
				iss.str("");
				iss.clear();
				iss.str(instrStr);
				iss >> instr;
				currAddress = instr;
				continue;//continue on with the loop
			}
		}
		iss.str("");
		iss.clear();
		iss.str(line);//reset the string stream
		if (!(iss >> instr)){continue;}//skip unparsable line
		if ((instr <= 30 && instr > 0) || (instr == 50)){  //this check skips over erraneous error message
			if (CPU::instructionHasOperand(instr)){
				this->write(currAddress, instr);
				while(std::getline(file, line)){//read ahead to the next operand
					iss.str("");
					iss.clear();
					iss.str(line);
					//unsigned int op=0;
					if(!(iss>>instr)){continue;}
					else{
						//op <<= 6;
						//instr |= op;
						currAddress++;
						this->write(currAddress, instr);
						break;
					}
				}
			}else{
				this->write(currAddress, instr);
			}
		}else{
			this->write(currAddress, instr);
		}
		currAddress++;
		numInstr++;
	}
	return numInstr;
}

int Memory::read(int address){
	if (address >= MEMORY_SIZE ||
				address < 0){
		perror("ERROR: Address out of bounds");
		exit(1);
	}
	return (memArray[address]);
}

void Memory::write(int address, int data){
	if (address >= MEMORY_SIZE ||
			address < 0){
		perror("ERROR: Address out of bounds");
		exit(1);
	}
	memArray[address] = data;
}

