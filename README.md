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

1. Download the jar from the releases page of Orochi. (https://github.com/mlakewood/orochi/releases/download/v0.1.0/orochi-0.1.0-standalone.jar)
2. java -jar orochi-0.1.0.jar 8080

## Configure a Proxy server

Once orochi is up and running you can then query the api through the http interface and add a proxy and command to run inside that proxy.

make the following request to add a new proxy

```
curl -XPOST '127.0.0.1:8080/proxy' ' -H "Accept: application/json" -d '{"name": "proxy-1", "backend": {"remote-addr": "127.0.0.1", "server-port": "8000"}, "front-port": 8081, "command": {"setup": "setup.sh", "command": "command.sh 8000", "started-check": "python started-check.py http://127.0.0.1:8000/README.md", "teardown": "teardown.sh"}}'

```

lets break the payload down to understand whats going on

```
"name:": "proxy-1"
```

This is the unique name for the proxy you have just created.

```
"backend": {"remote-addr": "127.0.0.1", "server-port": "8000"}
```

This is how the proxy re-routes requests to the service under test. Clojure ring passes through a map that represents the request. The proxy then merges the backend map specified over the top of the ring map, and the uses clj-http to make the request, using the merged map. In the case above we overrite the remote-addr and the server-port with the above values and so re-route to the local service under test.

```
"command": {"setup": "setup.sh",
"command": "command.sh 8000",
"started-check": "python started-check.py http://127.0.0.1:8000/README.md",
"teardown": "teardown.sh"}
}
```

This specifies how you would like to start and stop the service under test. Setup is the first command run and blocks until finished. command is the next to run and is a non-blocking command so that a service can be spun off and run in parallel. started-check is to verify that the service is running and is a blocking command. When creating the service all these are run in sequence. Which means by the time your post has finished the service under-test should have been started. The teardown command will be run when a proxy is commanded to stop. It is also a blocking command.





# Key Concepts
Following are the key concepts required to understand how orochi works.

## Proxy
The main workhorse of orochi is the proxy. When you want test the interaction between two services, wrap both services in a proxy and make http requests to the forwarding port on the proxy. Orochi will record all the requests, responses and the ordering. 

## Command
The command is the service under test. There are four seperate commands available to control the services under test. Each command is a string that will be run as a command on the commandline as is.
* setup: This is any initial setup that might be required
* command: This should be the command to run the service
* started-check: A service could take an amount of time before its ready to recieve requests. This command should make sure that the service is started and ready.
* teardown: This test will stop the service under test.

## API
This allows language agnostic configuration of Orochi, such as adding more proxies, removing them and listing the current recorded requests.

## Controller
The Controller handles spinning up of new proxies and commands and also holding the reference the list of requests.



