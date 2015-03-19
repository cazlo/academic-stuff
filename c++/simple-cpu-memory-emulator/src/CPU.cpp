/*
 * CPU.cpp
 *
 *  Created on: Feb 4, 2014
 *      Author: Andrew Paettie
 */

#include "CPU.h"
#include "Memory.h"
#include "Protocol.h"

#include <cstdio>
#include <iostream>
#include <time.h>
#include <cstdlib>
#include <unistd.h>

CPU::CPU(int * CPU2MEM, int * MEM2CPU) {
	AC = IR = PC = X = Y = 0;//make most registers initially 0
	SP = 999;//stack starts off at end of user memory and grows downward
	srand(time(NULL));
	cpu2Mem = CPU2MEM;
	mem2Cpu = MEM2CPU;
}

CPU::~CPU() {
	// TODO Auto-generated destructor stub
}

//handle a timed interrupt
void CPU::handleTimedInterrupt(){
	//switch stack to system stack
	//push registers
	saveRegisters();
	PC = 1000;//set pc = 1000
	while (true){
		IR = readFromMem(PC);
		if (instructionHasOperand(IR)){
			PC++;
			int operand = readFromMem(PC);
			handleInstr(IR, operand);
		} else{
			if (IR == 30){
				handleInstr(IR);//reset registers to those saved on stack
				return;//get back to the main (non interrupt) loop
			}
			handleInstr(IR);
		}

		PC++;
	}
}

//save registers to system stack
void CPU::saveRegisters(){
	write2Mem(1999, SP, true);
	write2Mem(1998, PC, true);
	write2Mem(1997, IR, true);
	write2Mem(1996, AC, true);
	write2Mem(1995, X, true);
	write2Mem(1994, Y, true);
	SP = 1994;
}

//helper stuff for pipe communication between CPU and memory
int CPU::readFromMem(int address){
	int command = READ;//what command the CPU sends over the pipe
							//0 = read; 1 = write; 2 = kernel write; 3 = shutdown
	write(cpu2Mem[1], &command, sizeof(int));//send read command over pipe
	read(mem2Cpu[0], &command, sizeof(int));//wait for confirmation of request
	if (command != CONFIRMATION){
		perror("ERROR: memory did not send confirmation");
		exit(1);
	}
	write(cpu2Mem[1], &address, sizeof(int));//send the address to read from
	read(mem2Cpu[0], &command, sizeof(int));//read the data into command
	return command;
}

void CPU::write2Mem(int address, int data, bool kernelMode){
	int command;
	if (kernelMode){
		command = KERNEL_WRITE;//2 = kernel_write
	} else{
		command = WRITE;//1 = write
	}
	write(cpu2Mem[1], &command, sizeof(int));//send write command over pipe
	read(mem2Cpu[0], &command, sizeof(int));//read confirmation from mem
	if (command != CONFIRMATION){
		perror("ERROR: memory did not send confirmation");
		exit(1);
	}
	write(cpu2Mem[1], &address, sizeof(int));//write address to pipe
	read(mem2Cpu[0], &command, sizeof(int));//read confirmation
	if (command != CONFIRMATION){
		perror("ERROR: memory did not send confirmation");
		exit(1);
	}
	write(cpu2Mem[1], &data, sizeof(int));//write data to pipe
}


//function which handles instruction without an operand
void CPU::handleInstr(int instr){
	switch (instr){
	case 6://load from sp+x into ac
		AC = this->readFromMem(SP + X);
		break;
	case 8://AC = random(1-100)
		this->AC = (rand()%100 + 1);
		break;
	case 10://AC += x
		this->AC = this->AC + this->X;
		break;
	case 11://AC += y
		this->AC = this->AC + this->Y;
		break;
	case 12://AC -= x
		this->AC = this->AC - this->X;
		break;
	case 13://AC -= y
		this->AC = this->AC - this->Y;
		break;
	case 14://X = AC
		X = AC;
		break;
	case 15://AC = X
		AC = X;
		break;
	case 16:// y = ac
		Y = AC;
		break;
	case 17://AC = Y
		AC = Y;
		break;
	case 18://sp = ac
		SP = AC;
		break;
	case 19://ac = sp
		AC = SP;
		break;
	case 24://pop ra from stack, jump to that address
	{
		int ra = this->readFromMem(SP);
		SP ++;
		PC = ra;
	}
		break;
	case 25://x++
		X++;
		break;
	case 26://x--
		X--;
		break;
	case 27://push ac onto stack
		SP--;
		if (PC >= Memory::KERNEL_BEGIN){//we are in kernel mode
			this->write2Mem(SP, AC, true);
		}else{
			this->write2Mem(SP, AC, false);
		}
		break;
	case 28://pop from stack to ac
		AC = readFromMem(SP);
		SP++;
		break;
	case 29://system interrupt
	{
		saveRegisters();
		PC = 1500;
		while (true){
			IR = readFromMem(PC);
			if (instructionHasOperand(IR)){
				PC++;
				int operand = readFromMem(PC);
				handleInstr(IR, operand);
			} else{
				if (IR == 30){
					handleInstr(IR);
					return;//get back to main loop so that we can get timed interrupts
				}
				handleInstr(IR);
			}

			PC++;
		}
	}//stupid c++
		break;
	case 30://restore registers, set user mode
		SP = readFromMem(1999);
		PC = readFromMem(1998);
		IR = readFromMem(1997);
		AC = readFromMem(1996);
		X = readFromMem(1995);
		Y = readFromMem(1994);
		break;
	case 50://end execution
	{
		int command = TERMINATE;
		write(cpu2Mem[1], &command, sizeof(int));
		exit(0);
	}
		break;
	default:
		perror("ERROR: Invalid instruction!\n");
		break;
	}
}

//function which handles instruction that has an operand
void CPU::handleInstr(int instr, int operand){
	switch (instr){
	case 1://load value into ac
		AC = operand;
		break;
	case 2://load value at address into ac
		AC = this->readFromMem(operand);
		break;
	case 3://load value from address found in address into ac
	{
		int address = this->readFromMem(operand);
		AC = this->readFromMem(address);
	}
		break;
	case 4://load value at address+x into ac
		AC = this->readFromMem(operand + X);
		break;
	case 5://load value at address+y into ac
		AC = this->readFromMem(operand + Y);
		break;
	case 7://store value in AC into address
		if (PC >= Memory::KERNEL_BEGIN){
			this->write2Mem(operand, AC, true);
		} else{
			this->write2Mem(operand, AC, false);
		}
		break;
	case 9://put port
		//if port =1; write ac as int to screen
		//if port =2; write ac as char to screen
		if (operand == 1){
			std::cout << AC;
		}else if (operand == 2){
			if (AC == 10){
				std::cout<< std::endl;
			}else{
				std::cout << (char)AC;
			}
		}else{
			perror("ERROR: Invalid operand for put");
		}
		break;
	case 20://jump to address
		PC = operand -1;//minus 1 because it will be incremented in main
		break;
	case 21://jump to address if ac ==0
		if (AC == 0){
			PC = operand -1;//minus 1 because it will be incremented in main
		}
		break;
	case 22://jump to address if ac != 0
		if (AC != 0){
			PC = operand -1;//minus 1 because it will be incremented in main
		}
		break;
	case 23://push ra onto stack, jump to address
		SP--;
		if (PC >= Memory::KERNEL_BEGIN){
			this->write2Mem(SP, PC, true);//write in kernel mode
		} else{
			this->write2Mem(SP, PC, false);//normal write
		}
		PC = operand - 1;//-1 because it will be incremented in main
		break;
	default:
		perror("ERROR: Invalid instruction!\n");
		break;
	}
}

//static method which returns true if the instruction has an associated operand,
//and false otherwise
bool CPU::instructionHasOperand(int instructionCode){
	switch (instructionCode){
	case 1: case 2: case 3: case 4: case 5: case 7:	case 9:	case 20: case 21:
	case 22: case 23:
		return true;
	case 6: case 8: case 10: case 11: case 12: case 13: case 14: case 15:
	case 16: case 17: case 18: case 19: case 24: case 25: case 26: case 27:
	case 28: case 29: case 30: case 50:
		return false;
	default:
		std::perror("Unknown instruction code");
		return false;
	}
}

