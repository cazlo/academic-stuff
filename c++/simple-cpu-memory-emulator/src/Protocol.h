/*
 * Protocol.h
 *
 *  Created on: Feb 12, 2014
 *      Author: Andrew Paettie
 */

#ifndef PROTOCOL_H_
#define PROTOCOL_H_

enum CPU_COMMAND {
	READ, WRITE, KERNEL_WRITE, TERMINATE
};

enum MEM_COMMAND{
	CONFIRMATION, ERROR
};

#endif /* PROTOCOL_H_ */
