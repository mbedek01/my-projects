/*
 * mime_util.h
 *
 * Functions for processing MIME types.
 *
 *  @since 2019-04-10
 *  @author: Philip Gust
 */

#ifndef MIME_UTIL_H_
#define MIME_UTIL_H_

/**
 * Return a MIME type for a given filename.
 *
 * @param filename the name of the file
 * @param mimeType output buffer for mime type
 * @return pointer to mime type string
 */
char *getMimeType(const char *filename, char *mimeType);


/**
 * Reads the mime.types file and populates the properties list with MIME type for a given extension.
 *
 * @param filename the name of the file
 * 
 */
void readMimeTypes(const char *filename);

#endif /* MIME_UTIL_H_ */
