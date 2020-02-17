/*
 * http_server.h
 *
 * Constants and extern declarations for http server
 *
 *  @since 2019-04-10
 *  @author: Philip Gust
 */

#ifndef HTTP_SERVER_H_
#define HTTP_SERVER_H_

#include <stdbool.h>

/** maximum buffer size */
#define MAXBUF 256

/** web newline sequence */
static const char *CRLF = "\r\n";

/** debug flag */
extern const bool debug;

/** subdirectory of application home directory for web content */
extern const char *CONTENT_BASE;

#endif /* HTTP_SERVER_H_ */
