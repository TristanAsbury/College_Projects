#include <iostream>
#include <string.h>
#include <fstream>

using namespace std;

struct record {
    char name[41];
    int keyId;

    record(){
        strcpy(name, "");
        keyId = -1;
    }
};

int addRecord(record table[], int tableSize, int key, char name[41] , int method);
void addFromFile(record table[], int tableSize, istream &inFile, int method);
int searchRecord(record table[], int tableSize, int key,  int method);
int deleteRecord(record table[], int tableSize, int key, int method);
void readTable(record table[], int tableSize);
void clearTable(record table[], int tableSize);

int main(){
    const int TABLE_SIZE = 19; //SETS INITIAL TABLE SIZE
    record table[TABLE_SIZE]; //CREATES ARRAY OF STRUCTS
    string filePath;
    ifstream inFile;
    int input = 0;
    int enteredId;
    char enteredName[41];
    int method;
    //FIRST THING: ASK THE USER FOR A COLLISION RESOLUTION METHOD
    cout << "Enter Collision Resolution Method | 1: Linear Probing | 2: Quadratic Probing | 3: Double Hashing" << endl;
    cin >> method;

    //SECOND: ASK IF THEY ARE IMPORTING ANY RECORDS FROM A FILE
    cout << "Are you entering records from a file?\n";
    cout << "1: Yes | 0: No\n";
    cin >> input;
    if(input == 1){
        cout << "Enter path to record file: \n";
        cin >> filePath;
        inFile.open(filePath);
        
        while(inFile.fail()){
            cout << "Invalid path to record file, re-enter:\n";
            cin >> filePath;
            inFile.open(filePath);
        }
        addFromFile(table, TABLE_SIZE, inFile, method);
    }

    
    while(input != -1){
        cout << "///////////////////\n";
        cout << "Select Option:\n";
        cout << "1: Insert | 2: Search | 3: Delete | 4: List | -1: Exit\n";
        cin >> input;
        if(input == 1){ //INSERT RECORD AND RETURN SUCCESS
            cout << "Enter name: " << endl;
            cin >> enteredName;
            cout << "Enter ID: " << endl;
            cin >> enteredId;
            while(enteredId < 000 || enteredId > 999){
                cout << "Invalid ID Number. Re-enter: " << endl;
                cin >> enteredId;
            }
            if(addRecord(table, TABLE_SIZE, enteredId, enteredName, method) == 1){
                cout << "Successfully added record!\n";
            } else {
                cout << "Error adding record.\n";
            }
        } else if(input == 2){ //SEARCH RECORDS AND RETURN INDEX
            cout << "Enter key: " << endl;
            cin >> enteredId;
            int foundPos = searchRecord(table, TABLE_SIZE, enteredId, method);
            if(foundPos != -1){
                cout << "Found key: " << enteredId << " at index: " << foundPos << endl;
            } else {
                cout << "Error finding record." << endl;
            }
        } else if(input == 3){ //DELETE RECORD AND RETURN SUCCESS
            cout << "Deleting record. Enter key: " << endl;
            cin >> enteredId;
            if(deleteRecord(table, TABLE_SIZE, enteredId, method) != -1){
                cout << "Successfully deleted record!\n";
            } else {
                cout << "Error deleting record.\n";
            }
        } else if(input == 4){ //LIST RECORDS
            readTable(table, TABLE_SIZE);
        }
    }
    return 0;
}

//ADD RECORD
//INPUT: 
//name: THe name of the record
//tableSize
int addRecord(record table[], int tableSize, int key, char name[41] , int method){
    int index = key % tableSize; //Mod 19
    bool posFound = false;

    cout << "Attempting to insert key at index: " << index << endl;
    if(table[index].keyId == -1){
        table[index].keyId = key;
        strcpy(table[index].name, name); //Set name of CURRENT POS to REQUESTED RECORD 
        return 1;
    } else if(method == 1) { //LINEAR PROBING
        int postCount = 0;
        while(postCount < tableSize && posFound == false){
            postCount++;
            index = (key+postCount)%tableSize;
            cout << "Attempting to insert key at index: " << index << endl;
            if(table[index].keyId == -1){
                posFound = true;
                table[index].keyId = key;
                strcpy(table[index].name, name);
            }
        }
        //Return the result depending if the spot was found
    } else if(method == 2){ //QUADRATIC PROBING
        int postCount = 1;
        while(posFound == false && postCount < (tableSize+1)/2){
            int newIndex = (index+postCount*postCount)%tableSize;
            cout << "Attempting to insert key at index: " << newIndex << endl;
            if(table[newIndex].keyId == -1){
                posFound = true;
                table[newIndex].keyId = key;
                strcpy(table[newIndex].name, name);
            }
            postCount++;
        }
    } else if(method == 3){ //DOUBLE HASHING
        int postCount = 1;
        int fixedInt = key/tableSize; 
        while(posFound == false && postCount < tableSize){
            index = (index + fixedInt) % tableSize;
            cout << "Attempting to insert key at index: " << index << endl;
            if(table[index].keyId == -1){
                posFound = true;
                table[index].keyId = key;
                strcpy(table[index].name, name);
            }
            postCount++;
        }
    }
    return posFound;
}

//SEARCH TABLE
//INPUT:
//tableSize: Size of table for the for loops
//key: Key for searching
//table: The table in which will have the same size as tableSize, and has all the records
//method: The collision resolution method used throughout the program
//OUTPUT:
//(integer) index: IF the key is found at an index, it will return the index, otherwise it will return -1
int searchRecord(record table[], int tableSize, int key,  int method){
    int index = key % tableSize; //Mod 19
    bool posFound = false;
    cout << "Trying to probe for key at index: " << index << endl;
    if(table[index].keyId == key){
        return index;
    } else if(method == 1) { //LINEAR PROBING
        int postCount = 0;
        while(postCount < tableSize && posFound == false){
            postCount++;
            index = (key+postCount)%tableSize;
            cout << "Trying to probe for key at index: " << index << endl;
            //With probing, if the next searched position is empty, this means that an insertion never reached this spot
            if(table[index].keyId == -1){
                return -1;
            } else if(table[index].keyId == key){
                return index;
            }
        }
        //Return the result depending if the spot was found
    } else if(method == 2){ //QUADRATIC PROBING
        int postCount = 0;
        while(posFound == false && postCount != (tableSize+1)/2){
            postCount++;
            int newIndex = (index+postCount*postCount)%tableSize;
            cout << "Trying to probe for key at index: " << newIndex << endl;
            //With probing, if the next searched position is empty, this means that an insertion never reached this spot
            if(table[newIndex].keyId == -1){
                return -1;
            } else if(table[newIndex].keyId == key){
                return newIndex;
            }
            
        }
    } else if(method == 3){ //DOUBLE HASHING
        int postCount = 0;
        int fixedInt = key/tableSize; 
        while(posFound == false && postCount != tableSize){
            postCount++;
            index = (index + fixedInt) % tableSize;
            cout << "Trying to probe for key at index: " << index << endl;
            if(table[index].keyId == -1){
                return -1;
            } else if(table[index].keyId == key){
                return index;
            }
        }
    }
    return -1;
}

//DELETE RECORD
//INPUT:
//table: The table in which will have the same size as tableSize, and has all the records
//tableSize: Size of table for the for loops
//key: Key for searching
//method: The collision resolution method used throughout the program
//OUTPUT:
//(integer) success: IF the key is found and deleted successfully, the function will return 1, else -1
int deleteRecord(record table[], int tableSize, int key, int method){
    //Use search method, and if it finds the index, then set keyId to -1 at that index
    int index = searchRecord(table, tableSize, key, method);
    if(index == -1){
        return -1;
    } else {
        table[index].keyId = -1;
        return 1;
    }
    return -1;
}

void addFromFile(record table[], int tableSize, ifstream &inFile, int method){
    while(inFile.good()){
        char name[41];
        int key;
        inFile >> name;
        inFile >> key;
        addRecord(table, tableSize, key, name, method);
    }
}

//READ TABLE
//INPUT:
//table: The table in which will have the same size as tableSize, and has all the records
//tableSize: SIze of table for the for loops
//OUTPUT: None, will print the records in the table
void readTable(record table[], int tableSize){
    for(int i = 0; i < tableSize; i++){
        cout << "Index " << i << ": " << "Name: " << table[i].name << ", ID: " << table[i].keyId << endl;
    }
}
