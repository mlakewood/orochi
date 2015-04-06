# Orochi
<img src="https://raw.githubusercontent.com/mlakewood/orochi/master/YamataNoOrochi.jpg"
title="Yamata No Orochi"/>

"Yamata no Orochi (八岐の大蛇?, literally "8-branched giant snake") or Orochi, translated as the Eight-Forked Serpent in English, is a legendary 8-headed and 8-tailed[1][2] Japanese dragon that was slain by the Shinto storm-god Susanoo."
 - Wikipedia (http://en.wikipedia.org/wiki/Yamata_no_Orochi)

When building small microservices it is very easy to introduce race conditions into the system. Because of the many moving parts across many processes it is often very hard to debug and troubleshoot. In these situations, functional and unit tests are not enough to guarantee system reliability, you need to be able to automate testing of the entire system. Given that by definition these services are running on seperate processes, how to we keep track of requests and more importantly the order that these requests happen in.

Orochi is a simple framework to wrap each service under test in a http proxy and record all requests and responses going through those proxies, and build a total order that this happens in. To do this a single jvm process is spun up with an admin http interface to build the proxies and run the shell commands that start the services inside the proxy. You can then make your requests in any language you like, and once the requests have finished you can request the recording of all requests that were triggered in the system.

# Getting Started

## build from source

```
$ git clone https://github.com/mlakewood/orochi.git
$ cd orochi
$ lein uberjar
$ java -jar target/orochi-0.1.0-standalone.jar 8080
```

## download jar

1. Download the jar from the releases page of Orochi. (https://github.com/mlakewood/orochi/releases/download/v0.1.0/orochi-0.1.0.jar)
2. java -jar orochi-0.1.0.jar 8080

## Configure a Proxy server

Once orochi is up and running you can then query the api through the http interface and add a proxy and command to run inside that proxy. For a simple example project please see https://github.com/mlakewood/orochi-example




# Key Concepts
Following are the key concepts required to understand how orochi works.

## Proxy
The main workhorse of orochi is the proxy. When you want test the interaction between two services, wrap both services in a proxy and make http requests to them. Orochi will record all the requests, responses and the ordering. 

## Command

## API

## Controller


# Prerequisites

# Running


# License

