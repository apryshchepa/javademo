# javademo

## building
It has been developed and tested with Oracle 1.8 JDK

run "gradlew build" script

## usage
### parsing file using default parser
java -jar demo-0.0.1-SNAPSHOT.jar [path to the file]

### all supported keys

	demo <file path> -parameter parameterValue
	
	Parameters:
	-q quick parsing strategy, no parameter value;
	-r reliable parsing strategy, no parameter value;
	(  -q and -r are mutually exclusive)
	-g <number of events> example file generation mode;
	Spare parameters or values are ignored.
	
## 0.0.1 Release notes
Unfortunately, less, than I've initially planned, is implemeted.
Ideas, that did not fit into few hours, are described in comments.