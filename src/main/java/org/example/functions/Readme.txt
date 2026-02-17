The Function Interface is a part of the java.util.function package which has been introduced since Java 8, to implement functional programming in Java. It represents a function which takes in one argument and produces a result. Hence, this functional interface takes in 2 generics namely as follows:

T: denotes the type of the input argument.
R: denotes the return type of the function.

This interface consists of the following 4 methods as listed which are later discussed as follows:
a.apply()
b.andThen()
c.compose()
d.identity()

1. "andThen" is a method provided by the Function interface. It allows chaining functions by applying the current function first, then applying the function provided to andThen.

2. "compose" is another method provided by the Function interface. It allows chaining functions by applying the provided function first, then applying the current function. Right-to-left approach.

Key Points
=======================
Order of Operations: compose applies functions in a right-to-left order. The function provided to compose is applied first.
Flexibility: Allows for creating complex function chains without deeply nested lambda expressions or method calls.
Readability: Enhances the readability of code by clearly expressing the order in which functions are applied.



