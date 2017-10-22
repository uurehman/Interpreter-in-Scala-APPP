/*
Subject: APPP Interpreter in Scala

Coded by: Ubaid ur Rehman
Email: uurehman.bscs15seecs@seecs.edu.pk
Date: October 09, 2017
*/

package main

import java.io.File
import scala.io.Source
import scala.util.Try // for try catch (Exception handling)

object Main {

  def main(args: Array[String]): Unit = {
    print("Enter the file name: ")
    val userInput = readLine()
    val fileName = "sample-codes/" + userInput
    val fileContent = readFile(fileName)
    val reformedContent = fileContent.replaceAll("\n", " ## ").replace("\r", " ")
    val wordsList = lexerFunction(reformedContent)
    //    wordsList.foreach(println)
    println("\nSUCCESS: Program Interpreted: \nOUTPUT:")
    interpret(Tuple2(wordsList, List.empty))
  }

  // Read file, containing code
  def readFile(fileName: String): String = {
    val file = new File(fileName)
    if (fileName.contains(".app")) {
      if (file.exists()) return Source.fromFile(fileName).mkString
      else { println("ERROR: File does not exists."); sys.exit(1) }
    } else {
      println("ERROR: Invalid file format: '.app' file required.")
      sys.exit(1) // file not found
    }
  }

  // send raw code to recursive function
  def lexerFunction(content: String) = getToken("", content, List.empty[String])

  // understand the code and provide tokens accordingly
  def getToken(accumulatorString: String, content: String, wordsList: List[String]): List[String] = {
    if (content.length <= 0) { // end function if string length is 0
      return wordsList ::: List(accumulatorString) ::: List("End of file")
    } else { // check each character
      if (content(0) == '\"') { // for string
        val str = getString("", content.substring(1)) // make a token for string till next ' " ' character
        return getToken("", str._2, wordsList ::: List("\"" + str._1))
      }
      if (content(0) == ' ' || content(0) == '\r') { // for carriage return and white spaces
        return getToken("", content.substring(1), if (accumulatorString == "") wordsList else wordsList ::: List(accumulatorString))
      }
      if (content(0) == ';') { // for semicolon
        return getToken("", content.substring(1), if (accumulatorString == "") wordsList ::: List(content(0).toString) else wordsList ::: List(accumulatorString, content(0).toString))
      }
      if ((content(0) == '=' || content(0) == '<' || content(0) == '>') && content(1) == '=') { // for operators with two characters( == <= >= )
        return getToken("", content.substring(2), if (accumulatorString == "") wordsList ::: List(content(0).toString + content(1).toString) else wordsList ::: List(accumulatorString, content(0).toString + content(1).toString))
      }
      if (isOperater(accumulatorString) || isOperater(content(0).toString)) { // for rest of operators (+ ,- , * etc) in List defined above
        return getToken("", content.substring(1), if (accumulatorString == "") wordsList ::: List(content(0).toString) else wordsList ::: List(accumulatorString, content(0).toString))
      }
      if (content(0).toString.matches("[ -~]")) { // for any printable character
        val word = accumulatorString + content(0)
        return getToken(accumulatorString + content(0), content.substring(1), wordsList)
      }
      // else Exit
      println("ERROR: Character not recognized: ", content(0))
      sys.exit(10)
    }
  }

  // return a token containing value of type ALPHA
  def getString(accumulatorString: String, content: String): Tuple2[String, String] = {
    if (content.length <= 0) { println("ERROR: End of string not definned"); sys.exit(11) }
    if (content(0) == '\"') return (accumulatorString + "\"", content.tail)
    else getString(accumulatorString + content(0), content.tail)
  }

  // check if an operator is in operators list
  def isOperater(operator: String): Boolean = {
    val operatorsList = List("+", ";", ":", "=", "/", "*", "<", ">", "^", "-", "==", "><", "<=", ">=", "or", "and", "not")
    if (operatorsList.exists(tempX => tempX == operator)) true else false
  }

  // check data type
  def isType(typeString: String): Boolean = {
    val typesList = List("bool", "int", "alpha")
    if (typesList.exists(tempX => tempX == typeString)) true
    else { println("ERROR: Type not recoginized" + typeString); sys.exit(6) }
  }

  // check integer
  def isInt(content: String) = Try { content.toInt }.isSuccess

  // check boolean
  def isBool(content: String) = if (content == "tt" || content == "ff") true else false

  // check variable name First alphabet and rest is Alphanumeric and * # $_
  def validateVariableName(content: String) = content.matches("^[a-zA-Z][\\w\\d*#$_]*")

  // check newline
  def isNewLine(content: String) = if (content == "##") true else false

  // check semicolen
  def isSemicolen(content: String) = if (content == ";") true else false

  // class for variables
  class Variable(varName: String, varValue: String, varType: String, const: Boolean = false) {
    val isConstant = const // boolean check for constant variables
    val variableName: String = if (validateVariableName(varName)) varName else { println("ERROR: Invalid variable name: " + varName); sys.exit(5) }
    val variableType: String = if (isType(varType)) varType else { println("ERROR: Invalid valiable type: " + varType); sys.exit(6) }
    val variableValue: String = if (varValue != "") {
      variableType match {
        case "bool" => { if (isBool(varValue)) varValue else { println("ERROR: Bad bool value."); sys.exit(7) } }
        case "alpha" => {
          if ((varValue(0) == '\"' && varValue(0) == varValue.last) || (varValue(0) == '\'' && varValue(0) == varValue.last)) varValue.substring(1).dropRight(1)
          else { println("ERROR: Bad alpha value"); sys.exit(8) }
        };
        case "int" => { if (isInt(varValue)) varValue else { println("ERROR: Bad int value: " + varValue); sys.exit(9) } }
      };
    } else { // variableValue is not provided
      variableType match { // default values
        case "bool"  => "ff" // false for boolean
        case "int"   => "0" // 0 for intergers
        case "alpha" => "" // empty string for alpha
      }
    }
    // return true if type and name of variable is same
    def matchNameAndType(newVar: Variable): Boolean = ((this.variableName == newVar.variableName) && (this.variableType == newVar.variableType))
    // print variables content
    def printVar() = println(variableName + " : " + variableType + " = " + variableValue)
  }

  // check variable already exists (match names only)
  def ifVarDefinedAlready(varName: String, vList: List[Variable]): Boolean = if (vList.exists(tempX => tempX.variableName == varName)) true else false

  /**
   * return a Value depending upon list of token given till ; or new line
   * expression is calculated from right to left
   * e.g. 4 + 8 * 9 - 1
   * = 4 + 8 * 8
   * = 4 + 64
   * = 68
   */
  def solveExpression(accumulatorString: String, textAndVariables: Tuple2[List[String], List[Variable]]): Tuple3[String, List[String], List[Variable]] = {
    if (textAndVariables._1.length > 0) { // if there is token(s) in token List
      if (isNewLine(textAndVariables._1(0)) || isSemicolen(textAndVariables._1(0))) { // If end of Statment
        return Tuple3(accumulatorString, textAndVariables._1.drop(1), textAndVariables._2)
      }
      if (isOperater(textAndVariables._1(0))) { // check if operater
        val result = operatersOperations(rightVal = accumulatorString, operationString = textAndVariables._1(0), leftVal = (textAndVariables._1.drop(1), textAndVariables._2))
        return (result._1, result._2._1, result._2._2)
      }
      if (ifVarDefinedAlready(textAndVariables._1(0), textAndVariables._2)) { // check if variable
        for (v <- textAndVariables._2)
          if (v.variableName == textAndVariables._1(0))
            return (solveExpression(accumulatorString + v.variableValue, (textAndVariables._1.drop(1), textAndVariables._2)))
      }
      solveExpression(accumulatorString + textAndVariables._1(0), Tuple2(textAndVariables._1.drop(1), textAndVariables._2)) // recurrsion
    } else { // when token ends return value of Last calculation
      return (accumulatorString, textAndVariables._1, textAndVariables._2)
    }
  }

  // calculate on base of binary operator!
  def operatersOperations(rightVal: String, operationString: String, leftVal: (List[String], List[Variable])): Tuple2[String, Tuple2[List[String], List[Variable]]] = {
    if (operationString == "-" || operationString == "not") { // for unary operations
      val left = solveExpression("", leftVal) // calculate expression
      val result = invertOpfunction(left._1); // invert results
      return (result, (left._2, left._3)); // return with changes
    } else if (isInt(rightVal)) { // for Integers
      val right: Int = rightVal.toInt // convert to integer
      val left = solveExpression("", leftVal) // calculate Expression 2 + (x+6) <- x+6
      if (isInt(left._1)) {
        if (operationString == "+") { // addition
          val result = right + left._1.toInt;
          return (result.toString, (left._2, left._3));
        } else if (operationString == "/") { // subtraction
          val result = right - left._1.toInt;
          return (result.toString, (left._2, left._3));
        } else if (operationString == "*") { // multiplication
          val result = right * left._1.toInt;
          return (result.toString, (left._2, left._3));
        } else if (operationString == "==") { // equal to
          val result = right == left._1.toInt;
          return (if (result) "tt" else "ff", (left._2, left._3));
        } else if (operationString == "><") { // not equal to
          val result = right != left._1.toInt;
          return (if (result) "tt" else "ff", (left._2, left._3));
        } else if (operationString == "<") { // less than
          val result = right < left._1.toInt;
          return (if (result) "tt" else "ff", (left._2, left._3));
        } else if (operationString == ">") { //  greater than
          val result = right > left._1.toInt;
          return (if (result) "tt" else "ff", (left._2, left._3));
        } else if (operationString == "<=") { // less than equal to
          val result = right <= left._1.toInt;
          return (if (result) "tt" else "ff", (left._2, left._3));
        } else if (operationString == ">=") { // greater than equla to
          val result = right >= left._1.toInt;
          return (if (result) "tt" else "ff", (left._2, left._3));
        } else { // undefined characters
          println("ERROR: Invalid operator " + operationString); sys.exit(152)
        }
      } else { println("ERROR: Left value is not int: " + left._1); sys.exit(11) } //unidentified integer
    } else if (isBool(rightVal)) { // first value bool
      val left = solveExpression("", leftVal)
      if (!isBool(left._1)) { println("ERROR: Second Oprand not bool: " + left._1); sys.exit(12) } // second value bool
      if (operationString == "and" || operationString == "or" || operationString == "^") { // calculate
        val result = logicalOperation(rightVal, operationString, left._1);
        return (result, (left._2, left._3));
      }
    }
    println("ERROR: Invalid operation: " + rightVal + " " + operationString + leftVal._1(0)); sys.exit(13)
  }

  // calculate on base of logical operator!
  def logicalOperation(rightVal: String, operationString: String, leftVal: String): String = { //calculate boolean opeartions
    if (!isBool(rightVal) || !isBool(leftVal)) { println("Error in Value Bool expected >> " + rightVal + " " + leftVal); sys.exit(72); }
    if (operationString == "and") { return if ((rightVal == "tt") && (leftVal == "tt")) "tt" else "ff" }
    else if (operationString == "or") { return if ((rightVal == "tt") || (leftVal == "tt")) "tt" else "ff" }
    else if (operationString == "^") { return if (rightVal == leftVal) "tt" else "ff" }
    else { println("Invalid operator for Boolean Operations" + operationString); sys.exit(151) }
  }

  // calculate on base of invert operator!
  def invertOpfunction(rightVal: String): String = {
    if (isInt(rightVal)) { val num = rightVal.toInt; return (-1 * num).toString }
    else if (isBool(rightVal)) { if (rightVal == "tt") return "ff" else return "tt" }
    println("ERROR: Failed in inverting operation: " + rightVal)
    sys.exit(80);
  }

  // declare a variable
  def decVariable(isConst: Boolean, textAndVariables: Tuple2[List[String], List[Variable]]): Tuple2[List[String], List[Variable]] = {
    if (validateVariableName(textAndVariables._1(0))) {
      if (ifVarDefinedAlready(textAndVariables._1(0), textAndVariables._2)) { println("variable already exists"); sys.exit() }
      val name = textAndVariables._1(0)
      if (textAndVariables._1(1) == ":") {
        if (isType(textAndVariables._1(2))) {
          val variableType = textAndVariables._1(2)
          if (textAndVariables._1(3) == "=") {
            // call expression
            val result = solveExpression("", Tuple2(textAndVariables._1.drop(4), textAndVariables._2))
            return (result._2, textAndVariables._2 ::: List(new Variable(varName = name, varValue = result._1, varType = variableType, isConst)))
          }
          return (textAndVariables._1.drop(3), textAndVariables._2 ::: List(new Variable(varName = name, varValue = "", varType = variableType, isConst)))
        }
      }
    }
    println("ERROR: Syntax Error in variable declaration: " + textAndVariables._1(0))
    sys.exit(7)
  }

  def printFunction(textAndVariables: Tuple2[List[String], List[Variable]]): Tuple2[List[String], List[Variable]] = {
    val result = solveExpression("", textAndVariables)
    println(result._1.stripPrefix("\"").stripSuffix("\""))
    return Tuple2(result._2, result._3)
  }

  def assignmentFunction(varName: String, textAndVariables: Tuple2[List[String], List[Variable]]): Tuple2[List[String], List[Variable]] = {
    val result = solveExpression("", textAndVariables)
    val variableType: String = if (isInt(result._1)) "int"
    else if (isBool(result._1)) "bool"
    else if (result._1(0) == '"' || result._1.last == '\"') "alpha"
    else { println("ERROR: Invalid type returned from expresstion: " + result._1); sys.exit(200) }
    val nVar = new Variable(varName = varName, varType = variableType, varValue = result._1)
    val varInList = indexOfVariable(varName, textAndVariables._2)
    if (textAndVariables._2(varInList).isConstant) { println("ERROR: Cannot not assign value to a constant variable: " + varName); sys.exit(45) }
    if (!textAndVariables._2(varInList).matchNameAndType(nVar)) {
      println("ERROR: Variable of type " + textAndVariables._2(varInList).variableType + " but given type " + nVar.variableType)
      sys.exit(201)
    }
    val splitedList = textAndVariables._2.splitAt(varInList)
    val ListWithNewVariable = List(nVar) ::: splitedList._2.tail
    val finalList = splitedList._1 ::: ListWithNewVariable
    return (result._2, finalList)
  }

  // return index of matching variable in given list
  def indexOfVariable(varName: String, variableList: List[Variable]): Int = {
    for (i <- 0 until variableList.length) if (variableList(i).variableName == varName) return i
    -1
  }

  //get Block  of statements for if and While loop
  def getBlock(of: String, block: List[String], code: List[String]): Tuple2[List[String], List[String]] = {
    of match {
      case "if" => {
        if (code(0) == "then") return (block, code)
        else if (isNewLine(code(0))) { println("ERROR: 'THEN' expected at: " + code(0)); sys.exit(265) }
      }
      case "then" => {
        if (code(0) == "else" || isNewLine(code(0))) return (block, code)
      }
      case "else" => {
        if (isNewLine(code(0))) return (block, code)
      }
      case "while" => {
        if (code(0) == "do") return (block, code)
      }
      case "do" => {
        if (isNewLine(code(0))) return (block, code)
      }
      case _ => {
        println("ERROR: Invalid block type: " + of)
        sys.exit(260)
      }
    }
    getBlock(of, block ::: List(code(0)), code.tail)
  }

  // Skip Block of statements for if else
  def skipBlock(accumulatorString: String, code: List[String]): List[String] = {
    if (accumulatorString == "then" && code(0) == "else") return code
    else if (isNewLine(code(0))) return code.tail
    skipBlock(accumulatorString, code.tail)
  }

  // Execution for a IF statement
  def ifStatementFunction(textAndVariables: Tuple2[List[String], List[Variable]]): Tuple2[List[String], List[Variable]] = {
    val ifCondition = getBlock("if", List.empty, textAndVariables._1) // get condition
    val condition = solveExpression("", (ifCondition._1, textAndVariables._2)) // calclulate condtion
    if (condition._1 == "tt") { // if true
      if (ifCondition._2(0) == "then") { // execute then BLock
        val thenBlock = getBlock(ifCondition._2(0), List.empty, ifCondition._2.tail) // get then block
        val afterExecution = interpret(thenBlock._1, condition._3) // execute then block
        if (thenBlock._2(0) == "else") { // skip else block
          return (skipBlock("", thenBlock._2), afterExecution._2)
        }
        return (thenBlock._2, afterExecution._2) // return to normal flow
      }
      println("Then Expected " + condition._1)
      sys.exit(270)
    } // if false
    else if (condition._1 == "ff") {
      val thenskip = skipBlock("then", ifCondition._2.tail)
      if (thenskip(0) == "else") {
        val elseBlock = getBlock("else", List.empty, thenskip.tail)
        val afterExecution = interpret(elseBlock._1, condition._3)
        return (elseBlock._2, afterExecution._2)
      }
      println("ERROR: Missing else at: " + thenskip(0))
      sys.exit(262)
    }
    println("ERROR: 'IF' condition not returning a boolean value: " + condition._1)
    sys.exit(250)
  }
  // Execution for a While statement
  def iterativeLoop(condition: List[String], block: List[String], varList: List[Variable]): List[Variable] = {
    val result = solveExpression("", (condition, varList))
    if (result._1 == "ff") return varList
    else if (result._1 == "tt") {
      val executedBlock = interpret(block, varList)
      return iterativeLoop(condition, block, executedBlock._2)
    }
    println("ERROR: Fatal error in executing while Loop: " + result._1)
    sys.exit(300)
  }

  // Execution for a While statement
  def whileStatement(textAndVariables: Tuple2[List[String], List[Variable]]): Tuple2[List[String], List[Variable]] = {
    val whileCondition = getBlock("while", List.empty, textAndVariables._1) // take condition from code
    if (whileCondition._2(0) == "do") {
      val doBlock = getBlock("do", List.empty, whileCondition._2.tail) // take while block from code
      val whileExecuted = iterativeLoop(whileCondition._1, doBlock._1, textAndVariables._2)
      return (doBlock._2, whileExecuted)
    }
    println("ERROR: Missing condition after while: " + whileCondition._2(0))
    sys.exit(305)
  }

  // interpreter function to execute the code
  def interpret(textAndVariables: Tuple2[List[String], List[Variable]]): Tuple2[List[String], List[Variable]] = {
    if (textAndVariables._1.length <= 0) { // if end of code end interpretation
      //println("End of file")
      return textAndVariables
    } else { // else check for statement type
      val word = textAndVariables._1(0)
      if (word != "") { // empty
        if (word == "End of file") {
          return textAndVariables
        }
        if (word == "var") { // variable declarations
          return interpret(decVariable(false, (textAndVariables._1.drop(1), textAndVariables._2)))
        }
        if (word == "const") { // constant declarations
          return interpret(decVariable(true, (textAndVariables._1.drop(1), textAndVariables._2)))
        }
        if (word == "print") { // print statement
          return interpret(printFunction(Tuple2(textAndVariables._1.drop(1), textAndVariables._2)))
        }
        if (word == "if") { // if statement
          return interpret(ifStatementFunction(Tuple2(textAndVariables._1.drop(1), textAndVariables._2)))
        }
        if (word == "while") { // while statement
          return interpret(whileStatement(Tuple2(textAndVariables._1.drop(1), textAndVariables._2)))
        }
        if (validateVariableName(word)) { // assignment operation
          if (textAndVariables._1(1) == "=") {
            val result = assignmentFunction(word, (textAndVariables._1.drop(2), textAndVariables._2))
            return interpret(result)
          }
          //return interpret(expr("",))
        }
        if (isNewLine(word) || isSemicolen((word))) { // new line
          return interpret(Tuple2(textAndVariables._1.drop(1), textAndVariables._2))
        }
      } else {
        return interpret(Tuple2(textAndVariables._1.drop(1), textAndVariables._2)) //
      }
      println("Interpreter Crashed at: '" + word + "'")
      sys.exit(100)
    }
  }
}
