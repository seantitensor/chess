# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922


Sequence diagram url: https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoFmipzGsvz-BwVygYKQH+iMykoKp+h-Ds0KPMB4lUEiMAIEJ4oYoJwkEkSYCkm+hi7jS+4MkyU76aSKrBhqAByEBlrAOp6jAepyGFRpWjaN6eXeSa+s6XYwD2fbbu5Fn+iFbqclGMZxoUWkpfAyCpjA6b4aMYw5qoebzNBRYlvUIVhdqur6tFzD6TA8U5KoDb0YlvLJZUy4wGKEpurK8r6WNC4CuVbmujOW5uYKw78vUh5yCgz7xOel7Xjti6TcKAZrgGp2bWl7Y6aWDnihkqgAZgT0gdUumEeWnykd8FFUfWgOaT98LJpV2EwLhtXgf9zWoUDl4g8hYONgxnjeH4-heCg6AxHEiT44TDm+FgomCqB9QNNIEb8RG7QRt0PRyaoCnDMDiHoOh8KVE99Tc0hn1mRhFlWTZ9gU-ZQkU05aguZgAXqsal5RTzaBGBAahoAA5Mwg1YOdK0Sy6GW9v2W2rRD7UQEdRUoLGCl85h0NgGmTgAIwEQ1TUFmMrXQPUR0a0hMDxPoA2WtgSAorsdFNtbVK3rtRgoNwx6o5rJ3wZrc5JQ6S5XRwGdMoYFEwGj2WPWLz1y6eb0fV9NsSWB4Nt27JQe7DeFZonWNMbj8cxLHaAavxaIwAA4kqGhU7lpYNNPTOs-YSpc9nItlQLddC1vvMt6lnb1MgOSzzm9logrJI1x541p4yYBHbnlH50t+73ldABm4rrsLvMVYakrpAcOHAdaqH1oba0OQP7JTNulTKVsHqtxqHbB20YnYlVduVUSnsfZ1T9vmFqxZg63UiqAwmkcBRWljvHEaScUEp0LvSayaIL5qAxAXB+F0hT1F-hKE0CAYDrxzHfD8e8BLX3-AgQCddvptxeMsURagCwNHGCogAktIAsXtwjBECCCTY8RdQoAKpBQGIJkigDVOYgGyMQQqKCkqMGnQO6Jiht3HCfdRjKLnmojRSptG6P0YY5YxjTF2KRjBZY1iQC2KItEsiYwnEuORhcNxphRrY2Yv4DgAB2NwTgUBOBiBGYIcAuIADZ4ATkMBwmARR3bU1to0VoHQ14b2mAfRSoxUlzHceLXesJdL9JQKZEZ-MOyWXNvtdEHCMQcJvkrZOMhU6sKfi-ABaBuHLS-t5Zgldq5ALVhQzW2tdYG2jmiOBRcEHdktuI4+aCYAhQwcVF2ZVPFVXTAQ7M-J-YkLauQnq5zqHRzoSgBOmM77MJ4aOGAcyUALN2Z-Yu-C-7wHiPyBpEV9RjOVqqDUKjKxmgtDA5geKoqR2YJyW5i57kzyVK+FBi8Q5VlDOGNAjtnbxi+ZUPB1UMy+wBcQwspDSwNKNCaMlUUuUMIYibBRMzEGPNWSbPadSUX0pWpdTVR4K4ZRQJsLFOKlRPIeJM+oTK5hN1kaLSZyrdJ+LmME+oeiDEwEGZDAV7tvHwxdSgN1MAPWBC9TCnJuNLAZxsia2ISAEhgGjX2CAJqABSEBxQ2sMP4OJaomndxaYotpzIZI9BUZvPOSEszYAQMAaNUA4AQBslANYWjpDeu0pI7ZNa60NqbS2ttQSO0Oq-EWlV9QABWma0ALIza9FAhJFauSYWslhCLNmXlfmjVFE0+EiKZKc0FIsTkgPOeAy50CblKtQVZJBFqaavPtpeHl2D+UVS8UKv59VRXRKDqWUOlCtbgtoXHKFCrYVrvhQchZ7bd1Fz1TAAR644DYvULi7qIjh2EsClhuYFzIFXKNl1ChNK8MoCQIq9Zptnl3rVay55eUIAcNfZ8iGXcfk+P+bmMVgcJUMgrBewjV6cgQfVdRoWa4mh9soAhBZtb62UAHdAIdrrpDwd4cuPwWh0TkZI-qNAxqYAKf7c26AOHVbKdgDK4M5o5UixvYyjhLLOxOoAxyywYYkKsb5ex3BfqhWZkIb+gO-76hWZgNkUltmazyoHhawWMB51oDtXIx1qDdKdv85+uG-cslNkjQELw9aiYJpJlAEriBgzWewLWwgeQCiNIXoxpe9NGbM1ZsYHBwyvwvAmWOxlIBuB4AULVjEVW8DLJXa5++y09rDagKN7AXCdX7MRQtow2g9AGAS5IibUBUujvMmymAIwsvfJhrl0Y8WgA
```
