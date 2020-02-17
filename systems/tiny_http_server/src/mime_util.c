/*
 * mime_util.c
 *
 * Functions for processing MIME types.
 *
 *  @since 2019-04-10
 *  @author: Philip Gust
 */

#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include "mime_util.h"
#include "http_server.h"
#include <stdio.h>
#include "properties.h"

static const char *DEFAULT_MIME_TYPE = "application/octet-stream";

// declare properties list
static Properties* propList;

/**
 * Lowercase a string
 */
char *strlower(char *s)
{
    for (char *p = s; *p != '\0'; p++) {
        *p = tolower(*p);
    }

    return s;
}

void readMimeTypes(const char *filename)
{
    // initiate the properties list if it has not been initiated
    if(propList==NULL){
        propList = newProperties();
    }
    
    FILE* fp;
    char *buff;
    size_t bufsize = 4000;
    size_t nBuff;
    
    buff = (char *)malloc(bufsize * sizeof(char));
    
    fp = fopen(filename, "r");
    
    if(fp == NULL){
        exit(1);
    }
    
    int n = 0;
    char* token;
    char* tokenList[20];
    char delim[]=" \t\r\n\v\f";
    
    while ((nBuff = getline(&buff, &bufsize, fp)) != -1) {
        
        token = strtok(buff, delim);
        int i = 0;
        int numTokens = 0;
        
        // don't tokenize if first token is "#"
        while( token != NULL && strcmp(token, "#")!=0 ) {
            tokenList[i] = token;
            numTokens++;
            token = strtok(NULL, delim);
            i++;
        }
        
        // add to Properties list only if there are at least two tokens: content type & ext
        if(numTokens >= 2){
            for(int k=1; k<numTokens; k++){
                putProperty(propList, tokenList[k], tokenList[0]);
            }
        }
        
        n++;
    }
    
    free(buff);
    fclose(fp);
}


/**
 * Return a MIME type for a given filename.
 *
 * @param filename the name of the file
 * @param mimeType output buffer for mime type
 * @return pointer to mime type string
 */
char *getMimeType(const char *filename, char *mimeType)
{
    
	// special-case directory based on trailing '/'
	if (filename[strlen(filename)-1] == '/') {
		strcpy(mimeType, "text/directory");
		return mimeType;
	}

	// find file extension
    char *p = strrchr(filename, '.');
    if (p == NULL) { // default if no extension
    	strcpy(mimeType, DEFAULT_MIME_TYPE);
    	return mimeType;
    }

    // lower-case extension
    char ext[MAXBUF];
    strcpy(ext, ++p);
    strlower(ext);

    // if extension is not in the mime.types file
    if(findProperty(propList, 0, ext, mimeType) == SIZE_MAX){
        strcpy(mimeType, DEFAULT_MIME_TYPE);
    }
    
    return mimeType;
}
