*************************************************************
*                                                           *
*			Term Project 	CSCI-6405           *
*			Athi Narayanan,	B00736197           *
*			Kartik Puri,	B00762840           *
*                                                           *
*************************************************************

The project is implemented in JAVA.

****************************************************************************************************************
LIST OF FILES/Directories:
****************************************************************************************************************
<Src>	:		This directory contains following source code files
	- Node.java
	- ItemList.java
	- SortedSetTest.java

<Test>	:		This directory contains the source code used for comparison (Downloaded from University of Liverpool website)

<Data>		:	Contains data files for input
	- Data.csv 	contains input file for our implemenataion
	- liverpool.csv	contains inout file for other academic implementation
	- Mushroom.csv	contains the unprocessed dataset downloaded from UCI machine learning repository

<Results>	:	Contains output rules generated
	- Results_10	:	Frequent Patterns generated when support is 10%
	- Results_20	:	Frequent Patterns generated when support is 20%
	- Results_30	:	Frequent Patterns generated when support is 30%
	- Results_40	:	Frequent Patterns generated when support is 40%
	- Results_50	:	Frequent Patterns generated when support is 50%

- athi_kartik.pptx	:	Presenation Slides

- Report.pdf		:	

- FPGrowth.pdf		:	Base Paper
 	
****************************************************************************************************************
RUNNING THE PROGRAM (Our Implementation)
****************************************************************************************************************
	- Input/data files should be kept in the directory in which we are running the program
	- OUTPUT FILE generated	:	Results
	- INPUT FILE		: 	any user specified comma separated file (csv preferred)
	- Delimiter should be comma
	- Copy the Data.csv file from Data directory into Src directory
	
	$ cd "Src"
	$ javac SortedSetTest.java
	$ java SortedSetTest

-------Disable Pruning and Run-------

	$ java SortedSetTest Y
	- Y or y command line argument is passed as a yes to skip the pruning process


****************************************************************************************************************
RUNNING THE Academic Implementation
****************************************************************************************************************
	- Input/data files should be kept in the directory in which we are running the program
	- OUTPUT FILE generated	:	Compare.csv
	- INPUT FILE		: 	any file having numeric data and comma separated file (csv preferred)
	- Delimiter should be comma
	
	$ cd "Test"
	$ javac FPgrowthApp.java
	$ java FPgrowthApp -FLiverpool.csv -S10
		o -FLiverpool.csv	:	means input file is Liverpool.csv
		o -S10 			:	means minimum support is 10%

****************************************************************************************************************
Following is the  program structure:
****************************************************************************************************************	
   main()---->fileexists
	      validateSupport
	      getUniqueElements		
		---->sortByValues()		// Sort the treemap on the basis on value
		---->order()			// Sort the transaction as part of pre processing
		---->buildFP()			// Build Initial FP Tree
			---->addNode()			// Insert node to the FP Tree
		---->projection()		// Projection of the tree
			---->pruning()			// Prune each and every projection


-------------------------------------------------------------
SortedSetTest.java
-------------------------------------------------------------
This file is the main executable file and has following methods:

			main()
				The main method, at which the execution starts
			sortByValues()
				This method sorts the treemap on the basis of values
			order()
				Sorts the transaction as per requirement of pre processing
			buildFP()
				Initial FP tree is built with the help of this node, starting from null
			projection()
				Projection of tree starting from deepest level
			pruning()
				Pruning of each and every projection for performance enhancement

-------------------------------------------------------------
ItemList.java
-------------------------------------------------------------
This file is the file which we classify the test data
			addNode()
				This method is used to add a node to tree and has two variants, one with source node and one without source node

-------------------------------------------------------------
Node.java
-------------------------------------------------------------
This file is the data structure used to represent a tree node


***************************************************************
*                           MISC                              *
***************************************************************
- Missing data to be represented by "?".
- Some ouput statements are commented and can be used to track intermediate values.
- Input file may require some additional two to three empty lines to write data properly.
	
	
