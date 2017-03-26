This directory contains a number of tests of the system. If you have a
source distribution, then the included ant build.xml file and run-test
script can run the tests. If you have a binary distribution, then you
have to run the tests "manually" like this:

    stu --source=test-NN/src
    stu --source=test-NN/src.stu
    stu --source=test-NN/src.stut

depending on what sort of file structure is under the test in question, and
you can of course add whatever other options you feel like (such as
--output-dir). 

The file or directory named "expected" in each test subdirectory represents
what *should* result from running the source of that test. A readme file in
each test subdirectory explains the point of the test.
