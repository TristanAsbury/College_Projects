#include <iostream>
#include <string.h>
#include <fstream>
#include <iomanip>

using namespace std;

struct record {
    char name[41];
    int keyId;

    record(){
        strcpy(name, "");
        keyId = -2;
    }
};

int addRecord(record table[], int tableSize, int key, char name[41] , int method);
void addFromFile(record table[], int tableSize, string filePath, int method);
int searchRecord(record table[], int tableSize, int key,  int method);
int deleteRecord(record table[], int tableSize, int key, int method);
void readTable(record table[], int tableSize);

int main(){
    const int TABLE_SIZE = 19; //SETS INITIAL TABLE SIZE
    record table[TABLE_SIZE]; //CREATES ARRAY OF STRUCTS
    char filePath[100];
    int option = 0;
    int enteredId;
    char enteredName[41];
    int method = 0;
    //FIRST THING: ASK THE USER FOR A COLLISION RESOLUTION METHOD
    while(!(method > 0 && method < 4)){
        cout << "Enter Collision Resolution Method | 1: Linear Probing | 2: Quadratic Probing | 3: Double Hashing" << endl;
        cin >> method;
    }

    //SECOND: ASK IF THEY ARE IMPORTING ANY RECORDS FROM A FILE
    cout << "Are you entering records from a file?\n";
    cout << "1: Yes | 0: No\n";
    cin >> option;
    if(option == 1){
        cout << "Enter path to record file: \n";
        cin >> filePath;
        ifstream inFile(filePath);
        while(inFile.fail()){
            cout << "Invalid path to record file, re-enter:\n" << endl;
            cin >> filePath;
            inFile.open(filePath);
        }
        option = 0;
        addFromFile(table, TABLE_SIZE, filePath, method);
    }
    
    //FINALLY: START MAIN LOOP
    while(option != -1){
        cout << "///////////////////\n";
        cout << "Select Option:\n";
        cout << "1: Insert | 2: Search | 3: Delete | 4: List | -1: Exit\n";
        cin >> option;
        if(option == 1){ //INSERT RECORD AND RETURN SUCCESS
            cout << "Enter name up to 40 characters: " << endl;
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
        } else if(option == 2){ //SEARCH RECORDS AND RETURN INDEX
            cout << "Enter key: " << endl;
            cin >> enteredId;
            int foundPos = searchRecord(table, TABLE_SIZE, enteredId, method);
            if(foundPos != -1){
                cout << "Found key: " << enteredId << " at index: " << foundPos << endl;
            } else {
                cout << "Error finding record." << endl;
            }
        } else if(option == 3){ //DELETE RECORD AND RETURN SUCCESS
            cout << "Deleting record. Enter key: " << endl;
            cin >> enteredId;
            if(deleteRecord(table, TABLE_SIZE, enteredId, method) != -1){
                cout << "Successfully deleted record!\n";
            } else {
                cout << "Error deleting record.\n";
            }
        } else if(option == 4){ //LIST RECORDS
            readTable(table, TABLE_SIZE);
        }
    }
    return 0;
}

/*ADD RECORD
--Function:
This function will take the key and name of the record, use the chosen hash method to find the index, and depending
on the conditions, either keep searching, or stop trying.
--Input:
table: The table in which will have the same size as tableSize, and has all the records
tableSize: Size of table for the for loops
key: Key for searching
name: name of the person being added to the hash table
method: The collision resolution method used throughout the program
--Output:
(integer) success: IF the key is added successfully, the function will return 1, else -1
*/
int addRecord(record table[], int tableSize, int key, char name[41], int method){
    int index = key % tableSize; //Mod 19
    bool posFound = false;

    cout << "Attempting to insert key at index: " << index << endl;
    //If the first spot is open:
    if(table[index].keyId < 0){ 
        table[index].keyId = key;
        strcpy(table[index].name, name);
        return 1;
    } else if(method == 1) { //LINEAR PROBING
        int postCount = 1;
        //While we havent reached the original spot and we haven't found a space
        while(postCount < tableSize && posFound == false){
            index = (key%tableSize) + postCount;
            cout << "Attempting to insert key at index: " << index << endl;
            if(table[index].keyId < 0){ //Check each space, see if its open
                posFound = true; //Bool that stops the loop
                table[index].keyId = key; //Set key of index to the inserted record
                strcpy(table[index].name, name); //Set name of the index to the inserted record
            }
            postCount++; //Increment position
        }
    } else if(method == 2){ //QUADRATIC PROBING
        int increment = 1;
        int timesChecked = 1;
        //While we haven't tried all available spots and haven't found a space
        while(posFound == false && timesChecked < (tableSize+1)/2){
            index = (index+increment)%tableSize; //Do quadratic probing calculation
            cout << "Attempting to insert key at index: " << index << endl;
            if(table[index].keyId < 0){ //Check each space, see if its open
                posFound = true; //Bool that stops the loop
                table[index].keyId = key; //Set key of index to the inserted record
                strcpy(table[index].name, name); //Set name of the index to the inserted record
            }
            increment+=2; //Increment the increment
            timesChecked++;
        }
    } else if(method == 3){ //DOUBLE HASHING
        int postCount = 1;
        int fixedInc = key/tableSize; //Set fixed increment
        //While we haven't tried all available spots and haven't found a space
        while(posFound == false && postCount < tableSize){
            index = (index + fixedInc) % tableSize; //Set index to previous index checked + the fixed increment all mod by the tableSize
            cout << "Attempting to insert key at index: " << index << endl;
            if(table[index].keyId < 0 ){ //Check each space, see if its open
                posFound = true; //Bool that stops the loop
                table[index].keyId = key; //Set key of index to the inserted record
                strcpy(table[index].name, name); //Set name of the index to the inserted record
            }
            postCount++; //Increment position
        }
    }
    return posFound;
}

/*SEARCH RECORD
--Function:
This function will take in the key of a record and search for it, hashing it the same way that it was added
and depending on the conditions, return a value. 
--Input:
table: The table in which will have the same size as tableSize, and has all the records
tableSize: Size of table for the for loops
key: Key for searching
method: The collision resolution method used throughout the program
--Output:
(integer) success: IF the key is found successfully, the function will return 1, else -1
*/
int searchRecord(record table[], int tableSize, int key,  int method){
    int index = key % tableSize; 
    bool posFound = false;
    cout << "Trying to probe for key at index: " << index << endl;
    if(table[index].keyId == key){
        return index;
    } else if(method == 1) { //LINEAR PROBING
        int postCount = 1;
        while(postCount < tableSize && posFound == false){
            index = (key+postCount)%tableSize;
            cout << "Trying to probe for key at index: " << index << endl;
            if(table[index].keyId == -2){
                return -1;
            } else if(table[index].keyId == key){
                return index;
            }
            postCount++;
        }
        //Return the result depending if the spot was found
    } else if(method == 2){ //QUADRATIC PROBING
        int increment = 1;
        int timesChecked = 1;
        while(posFound == false && timesChecked < (tableSize+1)/2){
            index = (index+increment)%tableSize;
            cout << "Trying to probe for key at index: " << index << endl;
            if(table[index].keyId == -2){
                return -1;
            } else if(table[index].keyId == key){
                return index;
            }
            increment+=2;
            timesChecked++;
        }
    } else if(method == 3){ //DOUBLE HASHING SEARCH
        int postCount = 1;
        int fixedInc = key/tableSize; 
        while(posFound == false && postCount != tableSize){
            index = (index + fixedInc) % tableSize;
            cout << "Trying to probe for key at index: " << index << endl;
            if(table[index].keyId == -2){
                return -1;
            } else if(table[index].keyId == key){
                return index;
            }
            postCount++;
        }
    }
    return -1;
}

/*
DELETE RECORD
--Function:
This function will use the already created searchRecord function and if it returns a value (non -1), then it will use this
index and set the keyId to -1 as to flag it as empty
--Input:
table: The table in which will have the same size as tableSize, and has all the records
tableSize: Size of table for the for loops
key: Key for searching
method: The collision resolution method used throughout the program
--Output:
(integer) success: IF the key is found and deleted successfully, the function will return 1, else -1
*/
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

/*ADD FROM FILE
---Function:
This method will take the string "filePath" and create an ifstream object, to which it will take all the data
and insert it into the hash table using previous methods
---Input
table: The table in which will have the same size as tableSize, and has all the records
tableSize: Size of table for the for loops
filePath: a string to find the file that will be added
method: The collision resolution method used throughout the program
---Ouput:
void
*/
void addFromFile(record table[], int tableSize, string filePath,  int method){
    ifstream recordFile;
    recordFile.open(filePath);
    while(!recordFile.fail()){
        char name[41];
        int key;
        recordFile.get(name, 41);
        recordFile >> key >> ws;
        addRecord(table, tableSize, key, name, method);
    }
    recordFile.close();
}

/*READ TABLE
---Function
This function will simply print the hash table, including Index, Name, and KeyID
---INPUT
table: The table in which will have the same size as tableSize, and has all the records
tableSize: Size of table for the for loops
---Output:
void
*/
void readTable(record table[], int tableSize){
    cout << "///////////////////\n";
    cout << left << setw(10) << "INDEX" << right << setw(5) << "NAME:" << right << setw(40) << "ID:" << endl;
    for(int i = 0; i < tableSize; i++){
        cout << left << setw(10) << i;
        if(!(table[i].keyId < 0)){
            cout << setw(41) << table[i].name <<right<< setw(4) << table[i].keyId << endl;
        } else {
            cout << setw(41) << "" << right << setw(4) << endl;
        }
    }
}
