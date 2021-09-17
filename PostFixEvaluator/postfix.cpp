//Author: Tristan Asbury
//Finished: 9/12/21
#include <iostream>
#include <cstring>
using namespace std;

//OperandNode
//Members:
//  OperandNode pointer next
//  double op which contains the number
struct OperandNode {
    OperandNode* next;
    double operand;
    
    OperandNode(){ //For head nodes
        next = nullptr;
    }

    OperandNode(double inOp){ //For any other node
        operand = inOp;
        next = nullptr;
    }

    double getOperand(){
        return operand;
    }
};

//OperandStack
//Members:
//  OperandNode pointer head, which points to the dummy node
//  OperandNode pointer top, which points to the top node, or the node that is pointed to by the head's next pointer
//  Int size, which keeps track of the size
struct OperandStack {
    OperandNode head;
    OperandNode* top;
    int size;

    OperandStack(){
        head = OperandNode(); //Set head to new node
        top = head.next;
        size = 0;
    }

    bool empty(){
        return top == nullptr; //If top is nullptr, then the stack is empty
    }

    void push(double inputOp){
        OperandNode* addedNode = new OperandNode(inputOp); //Create pointer to new node
        addedNode->next = head.next; //Set the new node's next to the heads next
        head.next = addedNode; //Set head's next pointer to the new node
        top = head.next; //Set top pointer to the new node
        size++; //Increase size
    }

    double pop(){
        double returnNum;
        if(empty()){
            cout << "Cannot pop, stack empty!";
            returnNum = -1;
        } else {
            returnNum = top->operand;
            head.next = top->next;
            delete(top); //Delete what the top was pointing to
            top = head.next; //Set top to point to heads next
            size--;
        }
        return returnNum;
    }

    //I had been thinking about what happens if there were any nodes remaining in the stack. This function is called to remove any remaning nodes
    //and sets the size to zero, allowing it (the stack) to be used for the next expression entered
    void clear(){
        OperandNode* tempNode = nullptr;
        while(top != nullptr){
            tempNode = top;
            top = top->next;
            delete(tempNode);
        }
        head.next = top;
        size = 0;
    }
};

class ExpressionEvaluator {
    OperandStack operandStack;
    bool expressionIsGood; //This boolean is optional, but the runtime is shorter if a user enters an invalid expression

    public:
    ExpressionEvaluator(){
        operandStack = OperandStack();
        expressionIsGood = false;
    }

    //Add term to expression
    void evalExpression(char* enteredExpression){
        expressionIsGood = true;
        char* term = strtok(enteredExpression, " \n"); //Tokenize
        while (term != NULL && expressionIsGood) //While there is still a term and the expression being read is valid  
        {
            if(expressionIsGood){ //If an error occurs while parsing, don't parse
                if(atof(term)){  //If the token is an operand, we just add it to the operand stack
                    operandStack.push(atof(term));
                    showOperandStack();   
                } else {
                    doCalculation(term[0]); //if the character is an operator, take it to the calculation
                    showOperandStack();
                }
            }
            term = strtok(NULL, " \n");
        }
        if(expressionIsGood && operandStack.size == 1){
            cout << "The result of your expression was: " << operandStack.pop() << "\n";
        } else {
            cout << "Expression not good, not enough operands or operators!\n";
        }

        operandStack.clear(); //Calls a very important method. Clears the stack if there are any nodes remaining for reusability
    }

    void doCalculation(char operatorChar){
        double operand1; //Pop the 2 top operands. The first operand popped is the second operand in the math expression and the second popped is the first
        double operand2;

        if(operandStack.size >= 2){ //If the operand stack isnt empty, then pop; else, give the user an error and set the expression to bad so it doesn't process any further
            operand2 = operandStack.pop();
            operand1 = operandStack.pop();
        } else {
            expressionIsGood = false;
        }

        //If the expression is good, do calculations
        if(expressionIsGood){
            switch(operatorChar){ //Find what operand is being used and base the math expression off of that
                case('*'):
                    operandStack.push(operand1 * operand2);
                    break;
                case('/'):
                    operandStack.push(operand1 / operand2);
                    break;
                case('+'):
                    operandStack.push(operand1 + operand2);
                    break;
                case('-'):
                    operandStack.push(operand1 - operand2);
                    break;
                default:
                    break;
            }
        }
    }

    void showOperandStack(){
        cout << "Operand Stack (TOP ON LEFT):\n";
        OperandNode* currentOperand = operandStack.top; //Set the current node in the loop to the top node of the stack
        while(currentOperand != nullptr){ //While the node pointer is pointing to a valid node
            cout << currentOperand->getOperand() << " "; //Print the operand
            currentOperand = currentOperand->next; //Set the current operand pointer to the next operand node
        }
        cout << endl;
    }
};

int main(){
    bool doContinue = true;
    char expression[128] = ""; //This should be enough for a singular expression, hopefully no one enters a 128 digit expression
    ExpressionEvaluator myEvaluator = ExpressionEvaluator();

    while(doContinue){ //While the user chooses to continue
        cout << "Enter expression or -1 to exit program: " << endl;
        cin.getline(expression, 128);
        
        if(strcmp(expression, "-1") == 0){ //If the user enters just -1
            cout << "Exiting program"; //Exit program
            doContinue = false;
        } else {
            myEvaluator.evalExpression(expression);
        }
    }
    return 0;
}
