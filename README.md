# The Dang Field
Scala library for setting that dang private field in someone else's code.

We've all been in a situation where the lib wasn't flexible enough and didn't allow us to customize some aspect of it.
So you went and contributed to the library to add the missing functionality or add some more setters to let you customize
the behaviour that way. **Yay, Open Source! 🎉**

_Who am I kidding._ Nobody's got time for that. Just lemme set the dang field!

Ha! I'll use reflection!
```scala
val instance = SomeLibrary.getInstance()
val f = instance.getClass.getDeclaredField("privateEngine")
f.setAccessible(true)
f.set(instance, new MyAwesomeEngine())
```
... but then it turns out that the `privateEngine` field wasn't in the `instance`'s class, but its superclass. And you 
just need to replace a piece of the engine, not the whole engine.

It gets ugly really quickly. This library aims to help with that:

```scala
import TheDangFieldImplicits._
val lib = SomeLibrary.getInstance()
val i: Int = lib getMe TheDangField.privateEngine.valveFactory.meaningOfLife
lib set TheDangField.privateEngine.valveFactory.meaningOfLife to (i * 10)
```

You can see more examples [in the tests](src/test/scala/io/github/mrobakowski/thedangfield/TheDangFieldTest.scala).

But seriously. Go contribute to that dang library.

## How to add as a dependency
### maven
```xml
<repositories>
    <repository>
       <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
```xml
<dependency>
    <groupId>com.github.mrobakowski</groupId>
    <artifactId>the-dang-field</artifactId>
    <version>master</version>
</dependency>
```

### other
Go to [https://jitpack.io/](https://jitpack.io/) and figure it out.  