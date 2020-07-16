# own-tempo

I use a wrapper script to launch which works for me:

```
#! /usr/bin/env bash

cd ~/code/own-tempo/

export AUTH_HEADER="Basic <you figure this out>"
./gradlew installDist && (cd build/install/own-tempo/; ./bin/own-tempo)
```
