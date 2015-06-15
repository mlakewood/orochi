#!/bin/bash


kill -TERM $(cat resources/test/scripts/server-$1.pid)
rm resources/test/scripts/server-$1.pid
