// Modify this file for your assignment

#include <stdio.h>
#include <string.h>
#include <signal.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <errno.h>
#include <time.h>

// Maximum input size on a line
#define MAX_BUFFER_SIZE 80

// Creating function for signal handler
void sigint_handler(int sig){
        //
        write(1," mini-shell terminated\n",35);
        exit(0);
}

// Function that parses command line input & returns a pointer to an 
// array of pointers to the tokens

char** parse(char* input) {

	char** myArray = malloc(sizeof(char*)*40);
	char* firstPart;	// string before '|'
	char* secondPart;	// string after '|'
	char* pipe;		// to store char '|'
	char* token;		// strings not containing pipe
	int i =0, j =0;		// iterator variables
	
	char* parsed;	// to store token after each call of strtok()
	// calling function to tokenize input
	parsed = strtok(input, " ");

	while(parsed!=NULL){
		
		int foundpipe = 0;
		pipe = "|";
		int length = strlen(parsed);	// stores length of each
						// token

		// iterating through each character of tokenized string
		// to check if it contains '|'
		for(j=0; j<length; j++) {
			
			if (parsed[j]=='|' && length ==1){
				foundpipe = 1;
				myArray[i++] = pipe;
				break;
			}
			if(parsed[j]=='|') {
			
			// copying part of string before '|' into
			// firstPart
			firstPart = malloc(sizeof(char)*(j));
			firstPart = strncpy(firstPart, parsed, j);
			
			// assign firstPart into myArray & increment i
			myArray[i++] = firstPart;

			// assign char '|' into myArray & increment i
			myArray[i++] = pipe;
			
			// storing part of string after '|' in secondPart
			secondPart = malloc(sizeof(char)*(length - j));
			secondPart = strncpy(secondPart, parsed+j+1,(length-j));
			
			// aaigning secondPart into myArray & increment i
			myArray[i++] = secondPart;
			
			// assign foundpipe to 1 since pipe is found
			foundpipe = 1;
			}
		}
		
		// if tokenized string does not contain '|', assign string
		// to myArray
		if(foundpipe==0) {
			token = malloc(strlen(parsed)*sizeof(char));
			token = strncpy(token, parsed, strlen(parsed));
			myArray[i] = token;
			i++;
		}
		parsed = strtok(NULL, " ");
	}
	// allocating memory for token equal to length of last element in
	// myArray minus 1
	token = malloc(strlen(myArray[i-1])-1);
	// copying last element of myArray minus "\0" character into token
	strncpy(token, myArray[i-1], strlen(myArray[i-1])-1);
	// re-assigning value of token to second from last element of 
	// myArray after removal of "\0" character
	//free(myArray[i-1]);
	myArray[i-1] = token;
	// assigning last element of array as NULL character
	myArray[i++] = NULL;
	return myArray;

}
// function exit() to terminate the current shell/process
void quit(){
	printf("Exiting mini-shell...\n");
	exit(0);
}

// function help that lists the usability of the built-in commands
void help(){

	printf("exit: This command will terminate the shell\n");
	printf("help: This command provides information of all the\
built-in functions provided by the shell and their\
usability\n");
	printf("cd: This command takes one argument. This argument\
is the name or the path of the directory that user\
wants to change to. This will now be the present working\
directory in the shell program.\n");
	printf("game: This command launches a number guessing game.\
The gameexits after you successfully guess the randomly\
generated number.\n");
}

// function that changes directory to the path provided as argument
int cd(char* args[]){

	int result;
	if(args[1]==NULL){
		fprintf(stderr, "Path expected after cd");
	}
	result = chdir(args[1]);
	if(result==0){
	printf("Changed directory to: %s\n", args[1]);
	}
	else if(result!=0){
	printf("Unable to change directory\n");
	}
	return result;
}

// built-in command: game
// starts a number guessing game which exits upon correctly guessing number
void guessingGame() {

	int target = 0;
	int guess;
	int count = 0;

	srand(time(0));
	target = rand()%100+1;

	while(guess!=target){

		printf("Enter your guess (number between 1 to 100: ");
		scanf("%d", &guess);
		count++;

		if(guess>target){
			printf("Too high\n");
		}
		else if(guess<target){
			printf("Too low\n");
		}
		else{
			printf("You guessed it right!\n");
		}
	}
	exit(0);
}

// function to check if the input contains pipe, returns position of pipe
// in the array
int checkPipe(char** myArray){
	
	int pipePosition = -1;
	char *pipe = "|";
	for(int i=0; i<40; i++){
		if(myArray[i]==NULL){
			break;
		}else if(*myArray[i] == *pipe){
			pipePosition = i;
			myArray[i]=NULL;
			break;
		} else {
			printf("%s\n",myArray[i]);
		}
	}
	return pipePosition;
}


int main(){

	// A buffer to hold 80 characters at most
	char line[MAX_BUFFER_SIZE];
	int pipefd[2];
	signal(SIGINT, sigint_handler);
        printf("You can only terminate by pressing Ctrl+C\n");

	// A loop that runs forever to keep our mini-shell running
	while(1){
	
		printf("<mini-shell>");		// prompt for mini-shell
		// getting command line input through stdin
		fgets(line, MAX_BUFFER_SIZE, stdin);
		printf("Here is what you typed: %s\n",line);
		// check if line>80 or null before passing through
		if(line==NULL){
			return 0;
		}
		else if(feof(stdin)) {
			exit(0);
		}
		
		// calling function parse() which tokenizes input
		
		char **cmdArray = parse(line);
		
		int pipePosition = checkPipe(cmdArray);
		
		if(pipePosition){
			if(pipe(pipefd)<0){
				printf("minishell having issues with pipe\n");
			}
		}

		// first fork called
		int process1 = fork();
		// if fork is successful, execute command line argument
		if(process1==0){
			
			int len = strlen(cmdArray[0]);
		
			if(strncmp(cmdArray[0], "cd", len)==0){
				cd(cmdArray);
			}
			else if(strncmp(cmdArray[0],"help", len)==0){
				help();
			}
			else if(strncmp(cmdArray[0],"exit", len)==0){
				quit();
			}
			else if(strncmp(cmdArray[0],"game", len)==0){
				guessingGame();
			}
			else{
				if(pipePosition){
					close(pipefd[0]);
					dup2(pipefd[1], 1);
					close(pipefd[1]);
				}
				int x = execve(cmdArray[0], cmdArray, NULL);
				printf("Error code: %d %s\n", x, strerror(errno));

			}
		}
		else{
			wait(NULL);
			
			if(pipePosition){
				int process2 = fork();
				if (process2 == 0) {
					close(pipefd[1]);
					dup2(pipefd[0], 0);
					close(pipefd[0]);
					int x = execve(cmdArray[pipePosition+1], cmdArray+pipePosition+1, NULL);
					printf("Error code: %d %s\n", x, strerror(errno));
					
				}else{
					wait(NULL);
				}
			}
		}
		// freeing cmdArray from heap memory
  		for(int i=0;i<40;i++){
			if(cmdArray[i]==NULL){
				break;
			}
			free(cmdArray[i]);
		}
		if(cmdArray){
			free(cmdArray);
		}
	}

  return 0;
}
