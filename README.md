# JavaParser
Class and Object Parser for Java.

User should add code snippet to [TestCode class](https://github.com/kingstabyou/JavaParser/blob/main/src/main/java/TestCode.java) and on execution of main we are presented with the parsed output as shown below.

```
------------------Class Cleft----------------------
------------------Fields----------------------
hash
key
value
next
------------------Methods----------------------
abc[]
Classes that can call - [Cleft]

ac[]
Classes that can call - [Cleft]

bc[]
Classes that can call - [Cleft]

------------------Class Amiddle----------------------
------------------Fields----------------------
hash
key
value
next
------------------Methods----------------------
abc[]
Classes that can call - [Cleft, Amiddle, Bleft]

ab[]
Classes that can call - [Amiddle, Bleft]

ac[]
Classes that can call - [Cleft, Amiddle]

------------------Class Bleft----------------------
------------------Fields----------------------
hash
key
value
next
------------------Methods----------------------
abc[]
Classes that can call - [Cleft, Bleft]

ab[]
Classes that can call - [Bleft]

bc[]
Classes that can call - [Cleft, Bleft]
```

for the code 
```
  public static class Amiddle {
        public void abc() { System.out.println("Amiddle.abc()"); }
        public void ab() { System.out.println("Amiddle.ac()"); }
        public void ac() { System.out.println("Amiddle.ac()"); }
    }

    public static class Bleft extends Amiddle {
        public void abc() { System.out.println("Bleft.abc()"); }
        public void ab() { System.out.println("Bleft.ac()"); }
        public void bc() { System.out.println("Bleft.bc()"); }
    }

    public static class Cleft extends Bleft {
        public void abc() { System.out.println("Cleft.abc()"); }
        public void ac() { System.out.println("Cleft.ac()"); }
        public void bc() { System.out.println("Cleft.bc()"); }
    }
```    


(Future Scope) Be able to parse through a whole project and just not code snippet, This Project can be accessed from repos directly through api's.

Parse through a target Java file to list out all class methods that can be invoked at any call site, with inheritance and
dynamic dispatch taken into account. 

Example:-


Inspect and modify all fields (including inherited) belonging to initialized objects, using Java reflection.
