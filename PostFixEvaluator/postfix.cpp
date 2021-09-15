#include <iostream>
#include <cstring>
using namespace std;

struct OperandNode {
    OperandNode* next;
    double op;
    
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

struct OperandStack {
    OperandNode* head;
    OperandNode* top;
    int size;

    OperandStack(){
        //Set head to new node
        head = new OperandNode();
        top = head->next;
        size = 0;
    }    

    bool empty(){
        //If top is nullptr, then the stack is empty
        return top == nullptr;
    }

    void push(double inputOp){
        //Create pointer to new node
        OperandNode* addedNode = new OperandNode(inputOp);
        //Set the new node's next to the heads next
        addedNode->next = head->next;
        //Set head's next pointer to the new node
        head->next = addedNode;
        //Set top pointer to the new node
        top = head->next;
        //Increase size
        size++;
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
        char* token = strtok(enteredExpression, " \n");
        while (token != NULL && expressionIsGood)
        {
            //If an error occurs while parsing, don't partse
            if(expressionIsGood){
                if(atof(token)){
                    //If it is an operand, we just add it to the operand stack
                    operandStack.push(atof(token));
                    showOperandStack();
                } else {
                    //if the character is an operator, take it to the calculation
                    doCalculation(token[0]);
                    showOperandStack();
                }
            }
            token = strtok(NULL, " \n");
        }
        if(expressionIsGood && operandStack.size == 1){
            cout << "The result of your expression was: " << operandStack.pop() << "\n";
        } else {
            cout << "Expression not good. Something went wrong.\n";
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
            //Find what operand is being used and base the math expression off of that
            switch(operatorChar){
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
        //Set the current node in the loop to the top node of the stack
        OperandNode* currentOperand = operandStack.top;

        //While the node pointer is pointing to a valid node
        while(currentOperand != nullptr){
            //Print the operand
            cout << currentOperand->getOperand() << " ";

            //Set the current operand pointer to the next operand node
            currentOperand = currentOperand->next;
        }
        cout << endl;
    }
};

int main(){
    int totalChars = 0;
    int stackChars = 0;

    //This should be enough for a singular expression, hopefully no one enters a 256 digit expression
    char expression[256] = "";

    //This is an expression, it contains an operand stack
    Expression myExpression = Expression();
    cout << "Enter expression: " << endl;
    cin.getline(expression, 255);
    myExpression.enterExpression(expression);
    
    return 0;
}
