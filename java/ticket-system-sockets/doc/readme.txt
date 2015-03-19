Archive Description:

ajp073000-project3.zip
--compile.sh
--movies.txt
--doc
----readme.txt
----summary.doc
--ticketSystem
----ClientMessage.java
----Movie.java
----MovieClient.java
----MovieServer.java
----ServerMessage.java

Compiling the program:
After unzipping the archive, navigate to where you unzipped it.
(You should now be in the folder containing the script compile.sh)
Compile the program by running the compile.sh command:
	./compile.sh
Now the server can be started from this same directory with the command:
	java ticketSystem.MovieServer <port> <path/to/movies.txt>
	(I used "java ticketSystem.MovieServer 3307 movies.txt" for testing)
Similarly the client can be started with the command:
	java ticketSystem.MovieClient <address> <port>
	(I used "java ticketSystem.MovieClient localhost 3307" for testing) 