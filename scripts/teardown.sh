#!/bin/bash


killall Python
num=$(ps aux | grep Python | wc -l)
if [ $num -gt 1 ]
   then 
       return 16
fi

