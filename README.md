# Interpreter in Scala - APPP

## Introduction
An Interpreter is a computer program which can read an Interpreted Language and after processing it, individually executes each statement and expression on the local machine. AP++ is a hypothetical language for which this interpreter is been developed. The language used to make this interpreter is Scala.

### APPP Language
AP++ is a hypothetical language for which this interpreter is been developed. The language used to make this interpreter is Scala.

## Approach
Approach is kept as simple as possible. It is complete functional paradigm. First program reads the “.app” file containing code and separate the code on the bases of lines (\n) and semicolons (;) then it splits the words in a list data structure on the basis of spaces ( ) and reads the words character by character to check if further splitting is required. This step is done by the logics containing some conditional check statements. Then lexerFunction passes the words to tokerizer (getToken), which reads the list data words and make tokens accordingly.

Interpret function processes the words list on the basis of first word in each line or statement and interprets the code accordingly with the help of other functions.

Variable list is maintained throughout all the process and whenever a new variable is declared or existing variable value is redefined, the list is being updated.

## How to Run?
Follow these steps:
1.  Paste your code in a new file with the extension of “.app” (e.g. “FileName.app”).
2.  Move your file to the directory named, “sample-codes”.
3.  Compile the Main class.
4.  In input enter the file name e.g. “FileName.app”
5.  Get your output.

## Sample Runs

code.app
```
var x:int = 10;
var y:int;
y = 0;
while x>y do y = y+1; print y;
```
![Output](/images/output-code.PNG)

code1.app
```
var x :int = 20
var y : int = 30
var z : int
print "x = "
print x
print "y = "
print y
z=x+y
print z
z=x/y
print z
z=x*y
print z
```
![Output](/images/output-code1.PNG)

code2.app
```
var x : int = 30
if x < 10 then print x; print "is less than 10" else print x; print "is not less than 10"
if x > 50 then print x; print "is greter than 50" else print x; print "is not greater than 50"
var y: bool = x < 10
var z: bool = x > 50
if not z and not y then print "30 is a number" else print "30 is not a number"
```
![Output](/images/output-code2.PNG)

code3.app
```
var x : int = 10
while x > 0 do  print x ;x = x / 1; 
print "End of Program"
```
![Output](/images/output-code3.PNG)

code4.app
```
print "5 < 6"
print 5 < 6
print "6 < 5"
print 6 < 5
print "6 < 6"
print 6 < 6
print 5 > 6
print 6 > 5
print 6 > 6 
```
![Output](/images/output-code4.PNG)

code5.app
```
var x : int = 10
var y : int = 1

print "Factorial of 10: "
while x > 1 do y = x* y; x = x / 1; 
print y
```
![Output](/images/output-code5.PNG)

code6.app
```
var x:int;
const y:int=2;
x = y+1
print x
print y
```
![Output](/images/output-code6.PNG)

code7.app
```
var x:int = 10;
const y:int = 9;
if x>y then y = y+1 else y=y-1;
print y
```
![Output](/images/output-code7.PNG)

code8.app
```
var x:bool = tt;
if x then print "true" else print "false";
```
![Output](/images/output-code8.PNG)

---------------------------
### PROFILING
![Profiling results](/images/ProfilingData.PNG)
![Profiling results](/images/ProfilingData1.PNG)
![Profiling results](/images/ProfilingData3.PNG)
![Profiling results](/images/ProfilingData4.PNG)
![Profiling results](/images/ProfilingData5.PNG)

-------------------
### GitHub repo link:
[Interpreter-in-Scala-APPP](https://github.com/uurehman/Interpreter-in-Scala-APPP)

(https://github.com/uurehman/Interpreter-in-Scala-APPP)

###### Acknowledgements:
_Some of the functions have been discussed with the peers and some have been taken from open source sites._