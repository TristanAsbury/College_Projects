#include <iostream>
#include <string.h>

using namespace std;

struct record {
    char name[41];
    int keyId;
};

//Add Record
//Input: char array Record Name, integer Record Key, record struct array Table, integer Probing Method
int addRecord(char[], int, int, record[], int);

//Search Record (return index and ID)
//Input: char array Record Name, integer Record Key, record struct array Table
int searchTable(char[], int, record[], int, int);

//Delete Record 
//Input: char array Record Name, integer Record Key
//Output: None
void deleteRecord(char[], int, int);

//Input: Table
//Output: All the indices, names, and ID's
void readTable(record[], int);

void clearTable(record[], int);

int main(){
    //Table size 19
    //Index: Name, Number
    const int TABLE_SIZE = 19;
    record table[TABLE_SIZE];
    //Set table ID all to -1
    clearTable(table, TABLE_SIZE);

    int input = 0;
    int method;
    char name[41];
    int keyId;
    while(input != -1){
        cout << "Select Option:\n";
        cout << "1: Insert | 2: Search | 3: Delete | 4: List | -1: Exit" << endl;
        cin >> input;
        if(input == 1){
            cout << "Enter name: " << endl;
            cin >> name;
            cout << "Enter ID: " << endl;
            cin >> keyId;
            cout << "Enter Collision Resolution Method | 1: Linear Probing | 2: Quadratic Probing | 3: Double Hashing" << endl;
            cin >> input;
            if(addRecord(name, TABLE_SIZE, keyId, table, input) == 1){
                cout << "Successfully added record!\n";
            } else {
                cout << "Error adding record.\n";
            }
        } else if(input == 2){
            
        } else if(input == 3){

        } else if(input == 4){
            readTable(table, TABLE_SIZE);
        } else {

        }
    }
    return 0;
}

//
int addRecord(char name[41], int tableSize, int key, record table[19], int method){
    int index = key % 19; //Mod 19
    bool posFound = false;

    if(table[index].keyId == -1){
        table[index].keyId = key;
        strcpy(table[index].name, name); //Set name of CURRENT POS to REQUESTED RECORD 
        return 1;
    } else if(method == 1) { //LINEAR PROBING
        int postCount = 0;
        while(postCount < tableSize && posFound == false){
            postCount++;
            if(table[index+postCount].keyId == -1){
                posFound = true;
                table[index+postCount].keyId = key;
                strcpy(table[index+postCount].name, name);
            }
        }
        //Return the result depending if the spot was found
    } else if(method == 2){ //QUADRATIC PROBING
        int postCount = 1;
        while(posFound == false && postCount != tableSize){
            index = (index+postCount*postCount)%tableSize;
            if(table[index].keyId == -1){
                posFound = true;
                table[index].keyId = key;
                strcpy(table[index].name, name);
            }
            postCount++;
        }
    } else if(method == 3){ //DOUBLE HASHING
        int postCount = 1;
        int fixedInt = key/tableSize; 
        while(posFound == false && postCount != tableSize){
            index = (index + fixedInt) % tableSize;
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

int searchTable(char[], int tableSize, int key, record table[], int method){
    return 1;
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