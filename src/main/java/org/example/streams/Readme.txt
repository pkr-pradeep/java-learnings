Java Stream Features
==============================================

The features of Java stream are mentioned below:

1. A stream is not a data structure instead it takes input from the Collections, Arrays or I/O channels.
2. Streams don’t change the original data structure, they only provide the result as per the pipelined methods.
3. Each intermediate operation is lazily executed and returns a stream as a result, hence various intermediate operations can be pipelined. Terminal operations mark the end of the stream and return the result.


Benefit of Java Stream
========================
There are some benefits because of which we use Stream in Java as mentioned below:

a. No Storage
b. Pipeline of Functions
c. Laziness
d. Can be infinite
e. Can be parallelized
f. Can be created from collections, arrays, Files Lines, Methods in Stream, IntStream etc.

We can create a stream from individual objects using Stream.of() Or simply using Stream.builder()
==============
Example
==============
Stream.of(arrayOfEmps[0], arrayOfEmps[1], arrayOfEmps[2]);
-----------------
Stream.Builder<Employee> empStreamBuilder = Stream.builder();
empStreamBuilder.accept(arrayOfEmps[0]);
Stream<Employee> empStream = empStreamBuilder.build();
