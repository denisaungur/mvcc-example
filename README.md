# mvcc-example
Example of multi version concurrency control


### Prerequisites
* Java 8
* Maven
* MySQL

### Example

Read statement:  *id=1* // reads the account with id=1  
Update statement: *id=1 total=100* // updates account with id=1  
Commit statement: *commit* // commits transaction  
Abort statement: *abort* // aborts transaction