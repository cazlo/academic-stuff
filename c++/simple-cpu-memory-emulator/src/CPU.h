/*
 * CPU.h
 *
 *  Created on: Feb 4, 2014
 *      Author: Andrew Paettie
 */

#ifndef CPU_H_
#define CPU_H_

class CPU {
private:
	//registers
	int PC;//program counter
	int SP;//stack pointer
	int IR;//instruction read register
	int AC;//accumulator
	int X;//scratch reg
	int Y;//scratch reg
	int *cpu2Mem;//the pipe from cpu to the memory
	int *mem2Cpu;//the pipe from the memory to the cpu

	//helper to save registers to the system stack
	void saveRegisters();
public:
	//constructor/destructor
	CPU(int *CPU2MEM, int*MEM2CPU);
	virtual ~CPU();

	void handleTimedInterrupt();

	//function which handles an instruction
	void handleInstr(int instr);
	void handleInstr(int instr, int operand);

	static bool instructionHasOperand(int instructionCode);

	//stuff for pipe communication
	int readFromMem(int address);
	void write2Mem(int address, int data, bool kernelMode);

	//getters and setters
	int getAc() const {return AC;}
	void setAc(int ac) {AC = ac;}

	int getIr() const {return IR;}
	void setIr(int ir) {IR = ir;}

	int getPc() const {return PC;}
	void setPc(int pc) {PC = pc;}

	int getSp() const {return SP;}
	void setSp(int sp) {SP = sp;}

	int getX() const {return X;}
	void setX(int x) {X = x;}

	int getY() const {return Y;}
	void setY(int y) {Y = y;}
};

#endif /* CPU_H_ */
