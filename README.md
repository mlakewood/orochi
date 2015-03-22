# Orochi
<img src="https://raw.githubusercontent.com/mlakewood/orochi/master/YamataNoOrochi.jpg"
title="Yamata No Orochi"/>

"Yamata no Orochi (八岐の大蛇?, literally "8-branched giant snake") or Orochi, translated as the Eight-Forked Serpent in English, is a legendary 8-headed and 8-tailed[1][2] Japanese dragon that was slain by the Shinto storm-god Susanoo."
 - Wikipedia (http://en.wikipedia.org/wiki/Yamata_no_Orochi)

Integration testing between http services is hard. Often when there is more than two it is very easy to introduce race conditions into the system without even realising it. In these situations, functional and unit tests are not enough to guarantee system reliability, you need to be able to automate testing of the entire system which can be quite hard to do.

Orochi is simply a framework to wrap multiple services in simple http proxy and record all requests and responses going through those proxies, in the order they happen. To do this a single jvm process is spun up with a http interface to build the proxies and run the shell commands that start the services inside the proxy. You can then make your requests in any language you like, and then once the requests have finished you can request the recording of all requests in the system. 

# Key Concepts

## Proxy

## Command

## API

## Controller


# Prerequisites

# Running


# License

