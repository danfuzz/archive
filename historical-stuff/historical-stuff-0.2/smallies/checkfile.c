/*
checkfile: look at the existence and time stamps of a file and
print one of three things: if the file doesn't exist, if it
exists and has been modified more recently than accessed, and
any other case. Typical usage:

checkfile /var/mail/danfuzz "" "You have mail." "You have new mail."

by Dan Bornstein
Copyright 1995, all rights reserved.
*/

#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>

void show (char *msg)
{
    if (msg[0] != '\0')
    {
        printf ("%s\n", msg);
    }
}

int main (int argc, char *argv[])
{
    struct stat statBuf;

    if (argc != 5)
    {
        fprintf (stderr, "usage: %s file nonexistMsg existMsg newMsg\n",
                 argv[0]);
    }

    if (stat (argv[1], &statBuf) != -1)
    {
        if (statBuf.st_atime < statBuf.st_mtime)
        {
            show (argv[4]);
        }
        else
        {
            show (argv[3]);
        }
    }
    else
    {
        show (argv[2]);
    }
}
