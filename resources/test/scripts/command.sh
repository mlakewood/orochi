#!/bin/bash

echo "starting command"
echo $$ > resources/test/scripts/server-$1.pid
exec python -m SimpleHTTPServer $1
echo "stopping"
