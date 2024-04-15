## Hello World

```kotlin
import jstd

process mainModule is
    write["Hello, world!"]
.
```

## Input example

```kotlin
import jstd

process mainModule is
    write["Enter your name:"]
    var str: [char*]<_str>
    input[str]
    write["Hello, %s", str]
.
```
