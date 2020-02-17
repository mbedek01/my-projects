/*
 * methods.c
 *
 * Functions that implement HTTP methods, including
 * GET, HEAD, PUT, POST, and DELETE.
 *
 *  @since 2019-04-10
 *  @author: Philip Gust
 */

#include "http_methods.h"

#include <stddef.h>
#include <string.h>
#include <time.h>
#include <sys/stat.h>
#include <unistd.h>
#include <dirent.h>
#include <curl/curl.h>

#include "http_server.h"
#include "http_util.h"
#include "time_util.h"
#include "mime_util.h"
#include "properties.h"
#include "file_util.h"


/**
 * Handle the dir get request
 */

static void do_get_dir(FILE *stream, const char *uri, const char *dirPath, Properties *responseHeaders, bool sendContent) {
    
    FILE *temp = tmpfile();
    if (temp == NULL) {
        return;
    }
    
    fprintf(temp,
            "<html>"
            "<head><title>index of %s</title></head>"
            "<body>"
            "<h1>Index of %s</h1>\n"
            "<table>\n"
            "<tr>\n"
            "<th valign=\"top\"></th>"
            "<th>Name</th>"
            "<th>Last modified</th>"
            "<th>Size</th>"
            "<th>Description</th>"
            "\n</tr>\n<tr>"
            "<td colspan=\"5\"><hr></td></tr>\n"
            , uri, uri);
    
    DIR *dir = opendir(dirPath);
    struct dirent entry;
    struct dirent *result;
    while ((readdir_r(dir, &entry, &result) == 0) && (result != NULL)) {
        
        if ( (strcmp(entry.d_name, ".") == 0)
            || ((strcmp(uri, "/") == 0) && (strcmp(entry.d_name, "..") == 0))) {
            continue;
        }
        
        const char *name = "";
        if (strcmp(entry.d_name,"..") == 0) {
            name =  "Parent Directory";
        } else {
            name = entry.d_name;
        }
        
        char file_name[PATH_MAX];
        makeFilePath(dirPath, entry.d_name, file_name);
        
        struct stat sb;
        stat(file_name, &sb);
        
        char timebuf[MAXBUF];
        milliTimeToShortHM_Date_Time(sb.st_mtim.tv_sec, timebuf);
        
        char url[MAXBUF];
        sprintf(url, S_ISDIR(sb.st_mode) ? "%s/" : "%s", entry.d_name);
        
        
        const char *icon = "";
        if (strcmp(entry.d_name,"..") == 0) {
            icon = "&#x23ce";
        }
        else if (S_ISDIR(sb.st_mode)) {
            icon = "&#x1F4c1;";
        }
        
        
        fprintf(temp, "<tr>\n"
                "<td>%s</td>\n"
                "<td><a href=\"%s\">%s</a></td>\n"
                "<td align=\"right\">%s</td>\n"
                "<td align=\"right\">%lu</td>\n"
                "<td>%s</td>\n"
                "</tr>\n",
                icon, url, name, timebuf,(unsigned long) sb.st_size, ""
                );
    }
    closedir(dir);
    
    fprintf(temp, "<tr>"
            " <td colspan=\"5\"><hr></td>\n"
            "</tr>\n"
            "</body>\n"
            "</html>\n"
            );
    
    fflush(temp);
    rewind(temp);
    
    
    if (temp == NULL) {
        sendErrorResponse(stream, 405, "Method Not Allowed", responseHeaders);
        return;
    }
    
    struct stat sb;
    fileStat(temp, &sb);
    
    // record the file length
    char buf[MAXBUF];
    size_t contentLen = (size_t)sb.st_size;
    sprintf(buf,"%lu", contentLen);
    putProperty(responseHeaders,"Content-Length", buf);
    
    
    // record the last-modified date/time
    time_t timer = sb.st_mtim.tv_sec;
    putProperty(responseHeaders,"Last-Modified",
                milliTimeToRFC_1123_Date_Time(timer, buf));
    
    
    // get mime type of file
    getMimeType(dirPath, buf);
    if (strcmp(buf, "text/directory") == 0) {
        // some browsers interpret text/directory as a VCF file
        strcpy(buf,"text/html");
    }
    putProperty(responseHeaders, "Content-type", buf);
    
    // send response
    sendResponseStatus(stream, 200, "OK");
    
    // Send response headers
    sendResponseHeaders(stream, responseHeaders);
    
    //
    if (sendContent) {
        copyFileStreamBytes(temp, stream, contentLen);
    }
    
    if (temp != NULL) {
        fclose(temp);
    }
}


/**
 * Handle GET or HEAD request.
 *
 * @param the socket stream
 * @param uri the request URI
 * @param requestHeaders the request headers
 * @param responseHeaders the response headers
 * @param sendContent send content (GET)
 */
static void do_get_or_head(FILE *stream, const char *uri, Properties *requestHeaders, Properties *responseHeaders, bool sendContent) {
	// get path to URI in file system
	char filePath[MAXBUF];
	resolveUri(uri, filePath);

    char cwd[PATH_MAX];
    getcwd(cwd, sizeof(cwd));
    
	// ensure file exists
	struct stat sb;
	if (stat(filePath, &sb) != 0) {
		sendErrorResponse(stream, 404, "Not Found", responseHeaders);
		return;
	}
    
    // if directory
    if(S_ISDIR(sb.st_mode) && (filePath[strlen(filePath)-1] == '/')) {
        do_get_dir(stream, uri, filePath, responseHeaders, sendContent);
        return;
    }
    
	// ensure file is a regular file
	if (!S_ISREG(sb.st_mode)) {
		sendErrorResponse(stream, 404, "Not Found", responseHeaders);
		return;
	}

	// record the file length
	char buf[MAXBUF];
	size_t contentLen = (size_t)sb.st_size;
	sprintf(buf,"%lu", contentLen);
	putProperty(responseHeaders,"Content-Length", buf);

	// record the last-modified date/time
	time_t timer = sb.st_mtim.tv_sec;
	putProperty(responseHeaders,"Last-Modified",
				milliTimeToRFC_1123_Date_Time(timer, buf));

	// get mime type of file
	getMimeType(filePath, buf);
    //strcpy(buf, "application/html");
	putProperty(responseHeaders, "Content-type", buf);

	// send response
	sendResponseStatus(stream, 200, "OK");

	// Send response headers
	sendResponseHeaders(stream, responseHeaders);

	if (sendContent) {  // for GET
		FILE *contentStream = fopen(filePath, "r");
		copyFileStreamBytes(contentStream, stream, contentLen);
		fclose(contentStream);
	}
}

/**
 * Handle GET request.
 *
 * @param the socket stream
 * @param uri the request URI
 * @param requestHeaders the request headers
 * @param responseHeaders the response headers
 * @param headOnly only perform head operation
 */
void do_get(FILE *stream, const char *uri, Properties *requestHeaders, Properties *responseHeaders) {
	do_get_or_head(stream, uri, requestHeaders, responseHeaders, true);
}

/**
 * Handle HEAD request.
 *
 * @param the socket stream
 * @param uri the request URI
 * @param requestHeaders the request headers
 * @param responseHeaders the response headers
 */
void do_head(FILE *stream, const char *uri, Properties *requestHeaders, Properties *responseHeaders) {
	do_get_or_head(stream, uri, requestHeaders, responseHeaders, false);
}

/**
 * Handle PUT request.
 *
 * @param the socket stream
 * @param uri the request URI
 * @param requestHeaders the request headers
 * @param responseHeaders the response headers
 */
void do_put(FILE *stream, const char *uri, Properties *requestHeaders, Properties *responseHeaders) {
    //resolve uri to a file path
    char filePath[PATH_MAX];
    resolveUri(uri, filePath);
    
    //create intermediate directories
    char tempPath[PATH_MAX];
    if(getPath(filePath, tempPath) != NULL) {
        mkdir(tempPath, 0755);
    }
    
    FILE *targetStream = fopen(filePath, "w");
    if(targetStream == NULL) { //cannot open
        sendErrorResponse(stream, 405, "Method Not Allowed", responseHeaders);
        return;
    }
    
    char lenbuf[MAXBUF];
    findProperty(requestHeaders, 0, "Content-Length", lenbuf);
    // copy bytes from input to output for lenbuf bytes
    copyFileStreamBytes(stream, targetStream, atoi(lenbuf));
    //close file
    fclose(targetStream);
    
    //check if it has been created
    struct stat sb;
    bool isCreated = (stat(filePath, &sb) != 0); //return 0 if successful
    //send response
    if(isCreated) {
        sendResponseStatus(stream, 201, "Created");
    }
    else {
        sendResponseStatus(stream, 200, "OK");
    }
    sendResponseHeaders(stream, responseHeaders);
}

/**
 * Handle POST request.
 *
 * @param the socket stream
 * @param uri the request URI
 * @param requestHeaders the request headers
 * @param responseHeaders the response headers
 */
void do_post(FILE *stream, const char *uri, Properties *requestHeaders, Properties *responseHeaders) {
    
    do_put(stream, uri, requestHeaders, responseHeaders);
	//sendErrorResponse(stream, 405, "Method Not Allowed", responseHeaders);
}


/**
 * Handle DELETE request.
 *
 * @param the socket stream
 * @param uri the request URI
 * @param requestHeaders the request headers
 * @param responseHeaders the response headers
 */
void do_delete(FILE *stream, const char *uri, Properties *requestHeaders, Properties *responseHeaders) {
    
    // get path to URI in file system
    char filePath[MAXBUF];
    resolveUri(uri, filePath);
    
    // ensure file exists
    struct stat sb;
    if (stat(filePath, &sb) != 0) {
        sendErrorResponse(stream, 404, "Not Found", responseHeaders);
        return;
    }
    
    // ensure file is a regular file or a directory file
    if (!S_ISREG(sb.st_mode) && (!S_ISDIR(sb.st_mode))) {
        sendErrorResponse(stream, 404, "Not Found", responseHeaders);
        return;
    }
    
    
    // special-case directory based on trailing '/'
    // if file is a directory, delete if it is empty
    if (S_ISDIR(sb.st_mode) && (filePath[strlen(filePath)-1] == '/')) {
        
        // if directory not empty, cannot perform delete operation
        // rmdir does not permit deletion of empty directories
        if(rmdir(filePath)!=0){
            sendErrorResponse(stream, 405, "Method Not Allowed", responseHeaders);
        }
        // else delete
        else{
            // send response
            sendResponseStatus(stream, 200, "OK");
            
            // Send response headers
            sendResponseHeaders(stream, responseHeaders);
        }
    }
    // if it is a directory but file path does not specify trailing '/'
    else if (S_ISDIR(sb.st_mode) && (!(filePath[strlen(filePath)-1] == '/'))){
        sendErrorResponse(stream, 404, "Not Found", responseHeaders);
    }
    
    // else, if it is a regular file & not a directory
    else if ((S_ISREG(sb.st_mode)) && (!S_ISDIR(sb.st_mode))){
        // send response
        sendResponseStatus(stream, 200, "OK");
        
        // Send response headers
        sendResponseHeaders(stream, responseHeaders);

        // delete file after making all checks
        remove(filePath);
        
        
        
    }
	
}
