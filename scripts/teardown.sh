#!/bin/bash


kill -TERM $(cat /Users/underplank/projects/orochi/scripts/server-$1.pid)
rm scripts/server-$1.pid
