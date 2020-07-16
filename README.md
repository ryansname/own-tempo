# own-tempo

I use a wrapper script to launch which works for me:

```
#! /usr/bin/env bash

cd ~/code/own-tempo/

export AUTH_HEADER="Basic <you figure this out>"
./gradlew installDist && (cd build/install/own-tempo/; ./bin/own-tempo)
```

Useful keybinds unless out of date:
`R` - Add "Review" to the remark

`r` - Edit the remark, (use \<enter> to enter a literal new line)

`d` - Debug view

`i` - Interrupt current task

`c` - Continue working on selected task

`<enter>` - Complete current task

`u` - Upload selected task

`U` - Upload all tasks
