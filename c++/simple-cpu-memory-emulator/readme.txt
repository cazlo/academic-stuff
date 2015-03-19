Building: Extract the package somewhere, navigate to this directory, and run g++ -o cpuMemSim *.cpp

Running: ./cpuMemSim <instructionFilename> <interruptTimerLength>

File listing:

Project-summary.pdf:
   The report for the project featuring the project description and purpose, implementation details, and some personal notes from me.

readme.txt
   This file (the readme).

sample1.txt 
   Exercises the indexed load instructions.
   Prints two tables, one of A-Z, the other of 1-10.

sample2.txt
   Exercises the call/ret instructions.
   Prints a face where the lines are printed using subroutine calls.

sample3.txt 
   Exercises the int/iret instructions.
   An interrupt handler begins incrementing a value.
   The main program is in a loop of 10 iterations printing the
   letter A followed by the value obtained using a system call.

sample4.txt
   A program which prints "ALL DONE!" using methods and returns.

CPU.cpp
   Defines the CPU object, where all registers are stored as data variables.  This is where all of the CPU logic (including handling instructions) and helper (including reading/writing over pipe) stuff is.

CPU.h
   The header for the CPU object.
   
cpuMemSim.cpp
   The main driver for the program.  This is the program which defines the pipes, declares and initializes memory, and forks to create the seperate processes for CPU and Memory which communicate with eac other over the pipe.
   
Help.cpp
   A simple helper function which prints out program usage.  This is done when invalid arguments are used, or when none are used.
   
Help.h
   The header for the Help.cpp file

Memory.cpp
   The definition of the Memory object. This stores an array of length 2000 which represents the memory space.  In here is also logic to load the instructions into memory, and the functions to read from and write to memory addresses.

Memory.h
   The header for the Memory.cpp file.
   
Protocol.h
   The definition of the enumerations used for the communication protocol over the pipe.  With these enumerations, when writing a command to pipe, it can be done with the appropriate enumeration (like READ, KERNEL_WRITE, etc.).
   
