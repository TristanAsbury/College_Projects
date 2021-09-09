#include <iostream>
#include <cstring>
using namespace std;

class OperatorNode {
    OperatorNode* next;
    char op;
    
    public:
    OperatorNode(){
        next = nullptr;
    }

    OperatorNode(char inOp){
        op = inOp;
        next = nullptr;
    }

    ~OperatorNode(){
    }

    OperatorNode* getNext(){
        return next;
    }

    void setNext(OperatorNode* opNode){
        next = opNode;
    }

    char getOperator(){
        return op;
    }
};

class OperandNode {
    OperandNode* next;
    double op;

    public:
    OperandNode(){
        next = nullptr;
    }

    OperandNode(double inOp){
        op = inOp;
        next = nullptr;
    }

    ~OperandNode(){
    }

    OperandNode* getNext(){
        return next;
    }

    void setNext(OperandNode* opNode){
        next = opNode;
    }

    double getOperand(){
        return op;
    }
};

class OperatorStack {
    //Head is a dummy node which contains no data and always exists in the stack
    OperatorNode* head;
    //Top is the node right after the head node
    OperatorNode* top;
    int size;

    public:
    OperatorStack(){
        head = new OperatorNode();
        top = head->getNext();
        size = 0;
    }

    ~OperatorStack(){ 
    }

    OperatorNode* getHead(){
        return head;
    }

    OperatorNode* getTop(){
        return top;
    }

    bool empty(){
        return top==nullptr;
    }

    void push(char inputOp){
        if(empty()){
            //If there is no current top, then make one
            head->setNext(new OperatorNode(inputOp));
        } else {
            //Else, get the top node
            // OperatorNode* nextNode = head->getNext();
            head->setNext(new OperatorNode(inputOp));
            head->getNext()->setNext(top);
        }
        top = head->getNext();
        size++;
    }

    char pop(){
        if(!empty()){ 
            char returnChar = top->getOperator();
            head->setNext(top->getNext());
            delete(top);
            top = head->getNext();
            size--;
            return returnChar;
        }
        return '\0';
    }
};

class OperandStack {
    OperandNode* head;
    OperandNode* top;
    int size;

    public:
    OperandStack(){
        head = new OperandNode();
        top = head->getNext();
        size = 0;
    }

    ~OperandStack(){
    }

    OperandNode* getHead(){
        return head;
    }

    OperandNode* getTop(){
        return top;
    }

    bool empty(){
        return top == nullptr;
    }

    void push(double inputOp){
        if(empty()){
            head->setNext(new OperandNode(inputOp));
        } else {
            OperandNode* nextNode = head->getNext();
            head->setNext(new OperandNode(inputOp));
            head->getNext()->setNext(nextNode);
        }
        top = head->getNext();
        size++;
    }

    double pop(){
        if(empty()){
            cout << "Cannot pop, stack empty!";
            return -1;
        } else {
            double returnNum = top->getOperand();
            head->setNext(top->getNext());
            delete(top);
            top = head->getNext();
            size--;
            return returnNum;
        }
    }

    int getSize(){
        return size;
    }
};

class Expression {
    OperatorStack operatorStack;
    OperandStack operandStack;
    bool expressionIsGood = true;

    public:
    Expression(){
        operatorStack = OperatorStack();
        operandStack = OperandStack();
    }

    //Add term to expression
    void addTerm(char* token){
        if(expressionIsGood){
            if(atoi(token)){
                //If it is an operand, we just add it to the operand stack
                operandStack.push(atoi(token));
                showOperandStack();
            } else {
                //If it is an operator, add the operator to the operator stack AND call doCalculation
                operatorStack.push(token[0]);
                doCalculation();
                showOperandStack();
            }
        }
    }

    void doCalculation(){
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

        //Pop the top operator
        char expOperator;
        if(!operatorStack.empty()){
            expOperator = operatorStack.pop();
        }
        
        //PushedNum is the operand that will pushed back into the operand stack after the math expression
        double pushedNum = 0;

        //If the expression is good, do calculations
        if(expressionIsGood){
            //Find what operand is being used and base the math expression off of that
            switch(expOperator){
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
        //Set the current node in the loop to top node
        OperandNode* currentOperand = operandStack.getTop();

        //While the node pointer is pointing to a valid node
        while(currentOperand != nullptr){
            //Print the operand
            cout << currentOperand->getOperand();
            cout << " ";
            //Set the current operand pointer to the next operand node
            currentOperand = currentOperand->getNext();
        }
        cout << endl;
    }

    void getResult(){
        if(expressionIsGood && operandStack.getSize() == 1){
            cout << "The result of your expression was: " << operandStack.pop();
        } else {
            cout << "Expression not good. Something went wrong.";
        }
    }
    
};

int main(){
    int totalChars = 0;
    int stackChars = 0;

    //This should be enough for a singular term, hopefully no one enters a 256 digit number
    char expression[256] = "";

    //This is an expression, it contains both an operator and operand stack
    Expression myExpression = Expression();
    
    cout << "Enter expression: " << endl;
    cin.getline(expression, 255);
    //Use strtok to split up expression into terms, where a space and a newline are the separators
    char *token = strtok(expression, " \n");
    while (token != NULL)
    {
        //Add term to the expression
        myExpression.addTerm(token);
        token = strtok(NULL, " \n");
    }

    myExpression.getResult();
    return 0;
}
