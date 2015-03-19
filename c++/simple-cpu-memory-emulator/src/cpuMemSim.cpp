//============================================================================
// Name        : cpuMemSim.cpp
// Author      : Andrew Paettie
// Version     :
// Copyright   : 
// Description :
//============================================================================
#include "Help.h"
#include "Memory.h"
#include "CPU.h"
#include "Protocol.h"

#include <stdio.h>
#include <stdlib.h>
#include <cstdlib>
#include <iostream>
#include <unistd.h>
#include <sys/types.h>

using namespace std;

int main(int argc, char * argv[]) {
	//variable declarations
	pid_t PID;

	int cpu2Mem [2];//pipe used to communicate from CPU to memory
	int mem2Cpu [2];//pipe used to communicate from memory to CPU

	//aliases of arguments
	char * filename;
	int interruptTimerValue;

	//sanity checks
	if (argc != 3){
		Help::printHelp();
		for (int i= 0; i < argc; i++){
			cout << "argv["<<i<<"]: " << argv[i] << endl;
		}
		return 1;
	} else if ((strtol(argv[2], NULL, 10)) == 0){
		//either 0 entered for interrupt timer (invalid)
		//or a non number was entered
		cout << "ERROR: interrupt timer value cannot be 0" << endl;
		Help::printHelp();
		return 1;
	}
	if (pipe(cpu2Mem) == -1){
		cout << "ERROR in plumbing"<< endl;
		return 1;
	}
	if (pipe(mem2Cpu) == -1){
		cout << "ERROR in plumbing"<< endl;
		return 1;
	}
	//passed sanity checks, so set up argument variables
	interruptTimerValue = strtol(argv[2],NULL, 10);
	filename = argv[1];

	//make the instructions magically appear in memory
	//this will make a memory object visible to the cpu,
	//but it will never be used (as this is not the memory which is updated
	//during program execution)
	Memory mem = Memory();
	int numInstructions = mem.loadInstructions(filename);

	//split process into 2
	PID = fork();
	switch (PID){
	case -1://here be dragons
		cout << "Could not create child process" << endl;
		return -1;
	/**************************************************************************
	 *                                   Memory
	 **************************************************************************/
	case 0://child; will interact with the memory object
		{//need brackets so I can declare variables inside this switch
		//setup pipes
		close(cpu2Mem[1]);
		close(mem2Cpu[0]);
		//dup2(cpu2Mem[0], 0);//dont do this, I want to be able to print for easy debugging
		//dup2(mem2Cpu[1], 1);

		//How to communicate over a pipe?  Why not use a protocol?
		//read(address)=0, write(address, data)=1, shutdown=2
		int pipeRead = 0;//scratch variable for reading/writing in pipe
		while(true){//mem just loops until it recieves a command from the cpu pipe
			int bytesRead = read(cpu2Mem[0], &pipeRead, sizeof(int));//sit idle until a command from CPU process is recieved
			if (bytesRead == -1){
				perror("ERROR: memory process could not read from pipe");
				return 1;
			}//else address has the address of the next command from the CPU
			switch (pipeRead){//now its getting spicy
			case READ://read
				//send confirmation
				pipeRead = CONFIRMATION;
				write(mem2Cpu[1], &pipeRead, sizeof(int));
				read(cpu2Mem[0], &pipeRead, sizeof(int));//the CPU responds with an address request
				pipeRead = mem.read(pipeRead);
				write(mem2Cpu[1], &pipeRead, sizeof(int));//write the data at the address to the pipe
				break;
			case WRITE:
			case KERNEL_WRITE://write
			{
				//send confirmation
				pipeRead = CONFIRMATION;
				write(mem2Cpu[1], &pipeRead, sizeof(int));

				//the CPU responds with the address first
				int address = mem.KERNEL_BEGIN;
				read(cpu2Mem[0], &address, sizeof(int));
				//sanity check on the address
				if (pipeRead == WRITE//only need to worry about this check when in normal write mode
					&& address >= mem.KERNEL_BEGIN){
					perror("ERROR: cannot write to system memory!\nExiting now\n");
					return 1;
				}
				//passed sanity checks, now we need the data
				//send confirmation
				pipeRead = CONFIRMATION;
				write(mem2Cpu[1], &pipeRead, sizeof(int));
				int data = 0;
				read(cpu2Mem[0], &data, sizeof(int));//the CPU responds with the data
				mem.write(address, data);//commit the data to the address
			}
				break;
			case TERMINATE://shutdown
				//TODO: memory cleanup?
				return 0;
			default:
				perror("WOOPS! CPU sent invalid command over pipe\n");
				return 1;
			}
		};
		}//c++ is crazy
		break;

	/**************************************************************************
	 *                                CPU
	 **************************************************************************/
	default://parent; interacts with the CPU object
		{
		//setup pipes
		close(cpu2Mem[0]);//close read for cpu2mem pipe
		close(mem2Cpu[1]);//close write for mem2cpu pipe
		//dup2(cpu2Mem[1], 1);
		//dup2(mem2Cpu[0], 0);

		int instructionCount = 0;//for interrupt
		int command = READ;//what command the CPU sends over the pipe
		CPU cpu = CPU(cpu2Mem, mem2Cpu);

		while(true){
			cpu.setIr(cpu.readFromMem(cpu.getPc()));
			//int instr = cpu.getIr() & 63;
			//if (cpu.instructionHasOperand(cpu.getIr())){
			if (cpu.instructionHasOperand(cpu.getIr())){
				cpu.setPc(cpu.getPc()+1);//point PC at the operand for the current instruction
				command = cpu.readFromMem(cpu.getPc());
				cpu.handleInstr(cpu.getIr(),
						         command);
			}else{
				cpu.handleInstr(cpu.getIr());
			}
			cpu.setPc(cpu.getPc()+1);
			instructionCount++;
			if ((instructionCount % interruptTimerValue) == 0){//time to handle a timed interrupt
				cpu.handleTimedInterrupt();
			}
		}}
		break;

	}

	return 0;
}
