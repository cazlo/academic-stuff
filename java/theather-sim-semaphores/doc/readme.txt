-----------------
How to compile:
-----------------
The following should be done in a UNIX environment like CS2.  Do NOT run in CS1 due to its limit on the number of threads.
Extract the zip file (which containted this file) to a folder somewhere, let's call this <root>
In <root> there should be 3 folders: doc, src, and theaterSim, as well as 2 shell scripts: compile.sh and run.sh.

To use the convience script run the commands:
  cd <root> (replacing <root> with the actual path to <root>)
  ./compile.sh
  Alternatively, the program could be compiled manually by entering:
    cd <root>
    javac -d . src/*.java

This should generate a bunch of class files and place them in the theaterSim folder.
----------------------
How to run program:
----------------------
Again assumming a UNIX environment.

cd <root>
./run.sh
To run a movies file other than <root>/movies.txt:
  cd <root>
  java theaterSim.Project2 <path to movies file>
---------------
Other details:
---------------
IMPORTANT: due to CS1's limit of 100 threads, this program must be run on CS2 for expected behavior.

Also This will overwrite whatever movies file you use to run this program! 