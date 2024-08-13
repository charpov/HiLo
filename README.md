# HiLo

This code repository highlights key differences between Java and Scala syntax.
It is intended to get Java programmers quickly up to speed and comfortable writing "Java in Scala", that is, programs that mostly rely on features available in Java, but written in a Scala syntax.
The same simple game, HiLo, is implemented in Java (22) and in Scala (3.4.2).
The differences between the two implementations are explained in a pdf file [B.pdf](B.pdf), included in this repository.

## Notes

- The pdf file refers to chapters and sections from my book [Functional and Concurrent Programming](https://www.fcpbook.org), available from [Addison-Wesley Professional](https://www.informit.com/store/functional-and-concurrent-programming-core-concepts-9780137466573).

- Scala is often associated with functional programming. There is nothing functional in the implementation of the HiLo game. For a description of Scala as a functional programming language, I recommend Part I of the book.

- The Scala and Java implementations are evaluated using the same test code, which is not duplicated. This code sharing is achieved using a technique known as _type classes_. Files [`HiLo.scala`](src/test/scala/HiLo.scala) and [`HiLoSuite.scala`](src/test/scala/HiLoSuite.scala) in the test files are a nice example of the power of type classes. The concept is typically associated with functional programming and is explained in Chapter 15 in the book. 

- I'm also including a Python version of the game, but it is not described in the pdf. (I'm no Python expert, feedback is welcome.)
