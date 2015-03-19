/*
 * Memory.h
 *
 *  Created on: Feb 4, 2014
 *      Author: Andrew Paettie
 */

#ifndef MEMORY_H_
#define MEMORY_H_

//#define int MEMORY_SIZE = 2000;

class Memory {
public:
	//constants
	const static int MEMORY_SIZE = 2000;
	const static int KERNEL_BEGIN = 1000;
	Memory();
	virtual ~Memory();
	void clearMem();
	int loadInstructions(char * filename);
	int read(int address);
	void write(int address, int data);
private:
	int memArray [MEMORY_SIZE];
};

#endif /* MEMORY_H_ */
