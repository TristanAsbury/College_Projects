#include <iostream>
#include <string.h>

using namespace std;

struct record {
    char name[41];
    int keyId;
};

int addRecord(char name[41], int tableSize, int key, record table[], int method);
int searchTable(int tableSize, int key, record table[], int method);
int deleteRecord(record table[], int tableSize, int key, int method);
void readTable(record[], int);
void clearTable(record[], int);

int main(){
    const int TABLE_SIZE = 11; //SETS INITIAL TABLE SIZE
    record table[TABLE_SIZE]; //CREATES ARRAY OF STRUCTS
    clearTable(table, TABLE_SIZE); //"EMPTIES" ALL RECORDS

    int input = 0;
    int method;
    char enteredName[41];
    int enteredId;
    cout << "Enter Collision Resolution Method | 1: Linear Probing | 2: Quadratic Probing | 3: Double Hashing" << endl;
    cin >> method;
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
            if(addRecord(enteredName, TABLE_SIZE, enteredId, table, method) == 1){
                cout << "Successfully added record!\n";
            } else {
                cout << "Error adding record.\n";
            }
        } else if(input == 2){ //SEARCH RECORDS AND RETURN INDEX
            cout << "Enter key: " << endl;
            cin >> enteredId;
            int foundPos = searchTable(TABLE_SIZE, enteredId, table, method);
            if(foundPos != -1){
                cout << "Found key: " << enteredId << " at index: " << foundPos << endl;
            } else {
                cout << "Error. Could not find record." << endl;
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

//
int addRecord(char name[41], int tableSize, int key, record table[], int method){
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
        while(posFound == false && postCount != (tableSize+1)/2){
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
        while(posFound == false && postCount != tableSize){
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

//Search table is pretty much the same as the insert method
int searchTable(int tableSize, int key, record table[], int method){
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
        int postCount = 1;
        int fixedInt = key/tableSize; 
        while(posFound == false && postCount != tableSize){
            index = (index + fixedInt) % tableSize;
            cout << "Trying to probe for key at index: " << index << endl;
            if(table[index].keyId == -1){
                return -1;
            } else if(table[index].keyId == -1){
                return index;
            }
            postCount++;
        }
    }
    return -1;
}

int deleteRecord(record table[], int tableSize, int key, int method){
    //Use search method, and if it finds the index, then set keyId to -1 at that index
    int index = searchTable(tableSize, key, table, method);
    if(index == -1){
        return -1;
    } else {
        table[index].keyId = -1;
        return 1;
    }
    return -1;
}

void readTable(record table[], int tableSize){
    for(int i = 0; i < tableSize; i++){
        cout << "Index " << i << ": " << "Name: " << table[i].name << ", ID: " << table[i].keyId << endl;
    }
}


void clearTable(record table[], int tableSize){
    for(int i = 0; i < tableSize; i++){
        strcpy(table[i].name, "");
        table[i].keyId = -1;
    }
}
