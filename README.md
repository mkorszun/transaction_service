# Transaction Service

## Build
~~~bash
$ mvn clean package
~~~

## Test
~~~bash
$ mvn test
~~~

## Usage
~~~bash
$ java -jar target/transaction_service-1.0-SNAPSHOT.jar
~~~

## Example:
~~~bash
$ curl http://localhost:4567/transactionservice/transaction/1234
> null

$ curl http://localhost:4567/transactionservice/transaction/sadas
> {"reason":"transaction id should be long value","status":"error"}
~~~

## Decisions
* data is stored in two structures: `hashmap` and `forest` (disjoint union of trees)
* `forest` is a storage used for transaction relations: summing transactions linked to a particular transaction means
finding parent node and traversing it
* `hashmap` is a storage used for reading (`O(1)`) and listing by type

## Discussion
Would be possible to totally remove `hashmap` but this would increase read and list time complexity with current forest
implementation.
