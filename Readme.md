# jHttpServer
 
A multi-threaded http server with thread-pooling implemented in Java.

## Getting Started

git clone [...]

### Prerequisites

* [Java 9](https://www.oracle.com/java/java9.html)
* [Maven](https://maven.apache.org/) 

## Features

Supports all HTTP methods: GET / HEAD / OPTIONS / TRACE / POST / PUT / DELETE

The http server can be used as a file server but it also allows new request processers to be registered.

TLS can be enabled by providing a keystore and a keystore password as VM options:
* Djavax.net.ssl.keyStore 
* Djavax.net.ssl.keyStorePassword 

The internal processors are:
* DirectoryIndex - retrieves a file named index.html or index.htm if one exists in the current folder
* DirectoryListing - displays a list of files in the current folder
* GetStaticFile - retrieves a single file
* PutStaticFile - saves / overwrites an existing file
* DeleteStaticFile - deletes an existing file
* ResourceNotFound - handles the case where no other listener is available

## Usage

`java -jar jHttpServer<version> <documentRoot> <port>`

## Future Work

* Better HTTP/1.1 compliance
* More Unit Tests

## License

This project is licensed under the MIT License

