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
    double op;
    
    //For head nodes
    OperandNode(){
        next = nullptr;
    }

    OperandNode(double inOp){
        op = inOp;
        next = nullptr;
    }

    double getOperand(){
        return op;
    }
};

//OperandStack
//Members:
//  OperandNode pointer head, which points to the dummy node
//  OperandNode pointer top, which points to the top node, or the node that is pointed to by the head's next pointer
//  Int size, which keeps track of the size
struct OperandStack {
    OperandNode* head;
    OperandNode* top;
    int size;

    OperandStack(){
        head = new OperandNode(); //Set head to new node
        top = head->next;
        size = 0;
    }    

    bool empty(){
        return top == nullptr; //If top is nullptr, then the stack is empty
    }

    void push(double inputOp){
        OperandNode* addedNode = new OperandNode(inputOp); //Create pointer to new node
        addedNode->next = head->next; //Set the new node's next to the heads next
        head->next = addedNode; //Set head's next pointer to the new node
        top = head->next; //Set top pointer to the new node
        size++; //Increase size
    }

    double pop(){
        if(empty()){
            cout << "Cannot pop, stack empty!";
            return -1;
        } else {
            double returnNum = top->getOperand();
            head->next = top->next;
            delete(top);
            top = head->next;
            size--;
            return returnNum;
        }
    }
};

class Expression {
    OperandStack operandStack;
    bool expressionIsGood;

    public:
    Expression(){
        operandStack = OperandStack();
        expressionIsGood = true;
    }

    //Add term to expression
    void enterExpression(char* enteredExpression){
        char* term = strtok(enteredExpression, " \n");
        while (term != NULL && expressionIsGood) 
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
            cout << "Expression not good, too many operands or operators!\n";
        }
    }

    void doCalculation(char operatorChar){
        //Pop the 2 top operands. The first operand popped is the second operand in the math expression and the second popped is the first
        double operand1;
        double operand2;

        //If the operand stack isnt empty, then pop; else, give the user an error and set the expression to bad so it doesn't process any further
        if(!operandStack.empty()){
            operand2 = operandStack.pop();
        } else {
            expressionIsGood = false;
        }

        //If the operand stack isnt empty, then pop; else, give the user an error and set the expression to bad so it doesn't process any further
        if(!operandStack.empty()){
            operand1 = operandStack.pop();
        } else {
            expressionIsGood = false;
        }

        //PushedNum is the operand that will pushed back into the operand stack after the math expression
        double pushedNum = 0;

        //If the expression is good, do calculations
        if(expressionIsGood){
            switch(operatorChar){ //Find what operand is being used and base the math expression off of that
                case('*'):
                    pushedNum = operand1 * operand2;
                    break;
                case('/'):
                    pushedNum = operand1 / operand2;
                    break;
                case('+'):
                    pushedNum = operand1 + operand2;
                    break;
                case('-'):
                    pushedNum = operand1 - operand2;
                    break;
                default:
                    break;
            }
        operandStack.push(pushedNum);
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
    int totalChars = 0;
    int stackChars = 0;
    bool continueGood = true;

    char expression[128] = ""; //This should be enough for a singular expression, hopefully no one enters a 128 digit expression
    Expression myExpression; //This is an expression, it contains an operand stack
    
    while(continueGood){ //While the user chooses to enter
        cout << "Enter expression or -1 to exit program: " << endl;
        cin.getline(expression, 128);
        
        if(strcmp(expression, "-1") == 0){ //If the user enters just -1
            cout << "Exiting program"; //Exit program
            continueGood = false;
        } else {
            myExpression = Expression();
            myExpression.enterExpression(expression);
        }
    }
    
    
    return 0;
}
