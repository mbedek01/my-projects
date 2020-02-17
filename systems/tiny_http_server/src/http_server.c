/*
 * http_server.c
 *
 * The HTTP server main function sets up the listener socket
 * and dispatches client requests to request sockets.
 *
 *  @since 2019-04-10
 *  @author: Philip Gust
 */
#include <stdbool.h>
#include <netdb.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>

#include "http_methods.h"
#include "time_util.h"
#include "http_util.h"
#include "http_request.h"
#include "network_util.h"
#include "http_server.h"
#include "thpool.h"
#include "mime_util.h"
#include "thpool.h"

#define DEFAULT_HTTP_PORT 1500
#define MIN_PORT 1000
#define THREADS 32

/** debug flag */
const bool debug = true;

/** subdirectory of application home directory for web content */
const char *CONTENT_BASE = "/Users/mayuribedekar/5600/Assignment-5/content";

/**
 * Main program starts the server and processes requests
 * @param argv[1]: optional port number (default: 1500)
 */
int main(int argc, char* argv[argc]) {
	int port = DEFAULT_HTTP_PORT;
    
    // populate the properties list by reading the mime.types file
    char* pathToMimeTypeFile = "/Users/mayuribedekar/5600/assignment-5-mayurib/mime.types";
    readMimeTypes(pathToMimeTypeFile);
    

    if (argc == 2) {
		if ((sscanf(argv[1], "%d", &port) != 1) || (port < MIN_PORT)) {
			fprintf(stderr, "Invalid port %s\n", argv[1]);
			return EXIT_FAILURE;
		}
	}

    int listen_sock_fd = get_listener_socket(port);
	if (listen_sock_fd == 0) {
		perror("listen_sock_fd");
		return EXIT_FAILURE;
	}

	fprintf(stderr, "HttpServer running on port %d\n", port);
    
    // create the threadpool
    threadpool thpool = thpool_init(THREADS);

	while (true) {
        // accept client connection
		int socket_fd = accept_peer_connection(listen_sock_fd);

		if (debug) {
			int port;
			char host[MAXBUF];
			if (get_peer_host_and_port(socket_fd, host, &port) != 0) {
			    perror("get_peer_host_and_port");
			} else {
				fprintf(stderr, "New connection accepted  %s:%d\n", host, port);
			}
		}

        // handle request
        int arg = socket_fd;
        thpool_add_work(thpool, (void*)process_request, (void *) arg);
    }

    // close listener socket
    close(listen_sock_fd);
    return EXIT_SUCCESS;

}
