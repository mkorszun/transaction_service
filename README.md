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