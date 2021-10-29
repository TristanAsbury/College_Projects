#include <iostream>
#include <string.h>
#include <fstream>
#include <string>
#include <math.h>
#include <iomanip>
using namespace std;

//Tristan Asbury, COMP 2270, Baker
struct City {
    bool visited;       //This is for the visiting queue
    int shortestDist;   //Shortest distance to this node
    City* prevCity;     //Previous city
    string name;        //The name of the city
    City(string name){
        prevCity = nullptr;
        this->name = name;
        visited = false; 
        shortestDist = -1; //Meaning infinity
    }
};

struct Graph {
    City** cities;
    int numCities;

    Graph(int numCities){
        this->numCities = numCities;
        cities = new City*[numCities];
    }
};

//A recursive function made to read the path by starting at the end node
void printPath(City* node){
    if(node->prevCity == nullptr){  //If the node is the first node, then we have reached the end of the recursive function, print this as the first city
        cout << node->name;
    } else {                        //Else, we just print "to [city name]"
        printPath(node->prevCity);
        cout << " to " << node->name;
    }
}

//Dijkstras Algorithm
void dijkstraAlg(Graph* g, City* sourceNode, int** distances){
    City* currentNode = sourceNode;                     //This is the first node we will be using in dijkstras algo
    City** unvisitedSet = new City*[g->numCities];      //Creat an array called unvisitedSet, this will work similarly as the queue we talked about in class
    int unvisitedSize = g->numCities;                   //Create unvisited size, this will be used to see when we have checked all nodes have been visited
    int currentNodeIndex;                               //This will be used to access the adjacency matrix to get distance values
    sourceNode->shortestDist = 0;                       //Set the initial distance from source node to 0

    for(int i = 0; i < g->numCities; i++){              //Add all cities to unvisitedSet
        unvisitedSet[i] = g->cities[i];
    }
    
    for(int i = 0; i < g->numCities; i++){              //Get the index of the current node
        if(g->cities[i] == sourceNode){
            currentNodeIndex = i;
        }
    }

    while(unvisitedSize > 0){                           //While there are still unvisited nodes in the set
        int shortestDist = INT_MAX;

        for(int i = 0; i < g->numCities; i++){          //Find an unvisited node with the shortest minimum distance to use as the current node (this is the same as the queue by getting the min)
            if(!unvisitedSet[i]->visited && unvisitedSet[i]->shortestDist != -1){
                if(unvisitedSet[i]->shortestDist < shortestDist){
                    shortestDist = unvisitedSet[i]->shortestDist;
                    currentNode = unvisitedSet[i];
                    currentNodeIndex = i;
                }
            }
        }
        
        
        for(int i = 0; i < g->numCities; i++){          //For all nodes
            if(distances[currentNodeIndex][i] > 0 && !g->cities[i]->visited){ //If the node is a neighbor and it is not visited
                if(g->cities[i]->shortestDist == -1 || g->cities[i]->shortestDist > currentNode->shortestDist + distances[currentNodeIndex][i]){ //If there is not a shortest dist yet or the shortest dist is greater than the current nodes shortest + the distance between the two
                    g->cities[i]->shortestDist = currentNode->shortestDist + distances[currentNodeIndex][i];    //Set the shortest dist of the neighbor node to the current nodes shortest dist + the distance between the two
                    g->cities[i]->prevCity = currentNode;   //Set the previous city of the neighbor to the current node
                }
            }
        }
        currentNode->visited = true;                    //We say this current node is now visited
        unvisitedSize--;                                //We decrement the size of the unvisitedNodes
    }
    
    for(int i = 0; i < g->numCities; i++){
        if(sourceNode != g->cities[i]){
            City* currentCity = g->cities[i];
            cout << "**********************"<<endl;
            cout << "The distance from " << sourceNode->name << " to " << g->cities[i]->name << " is " << g->cities[i]->shortestDist << endl;
            cout << "A shortest path is: ";
            printPath(currentCity);
            cout << endl;
        }
    }
    cout << "**********************"<<endl;
}

int main(){
    int** distances;                    //This is the adjacency matrix
    int lines = 0;                      //This is used to keep track of the lines in the file
    int numCities;                      //This will be used to keep track of the number of cities
    ifstream file;                      //Input file stream
    string filePath;                    //Path to the input file
    string cityName;                    //Name of the city that will be used to set the name of a city
    string searchCity;                  //Name of the city to be searched for Dijkstras Algorithm
    City* inputCity = nullptr;          //Pointer to the city in the graph that will be used for Dijkstras Algorithm
    
    do {
        cout << "Enter file path: ";    
        getline(cin, filePath);
        file.open(filePath);
    }while(!file.good());
    
    if(file.good()){
        string token;
        while(!file.eof()){
            getline(file, token);
            lines++;
        }
    }
    file.close();
    
    numCities = (int)(ceil(lines/2)) + 1;
    distances = new int*[numCities];

    Graph myGraph(numCities);
    
    file.open(filePath);

    //For each row
    for(int r = 0; r < numCities; r++){
        distances[r] = new int[numCities];          //Make a new column
        getline(file, cityName);                    //Get the city name at the current line
        myGraph.cities[r] = new City(cityName);
        for(int c = 0; c < numCities; c++){         //For each column
            distances[r][c] = 0;                    //Set the distance to 0
        }
    }

    //Read adjacency matrix
    for(int i = 0; i < numCities; i++){             //For each row
        for(int j = i + 1; j < numCities; j++){     //For each column with an offset of current row + 1
            file >> distances[i][j];                //Take in the distance
            distances[j][i] = distances[i][j];      //Create symmetry
        }
    }
    file.close();                                   //Close the file, we don't need it anymore

    
    cout << "Input city name: ";                    //Prompt user to input city name
    getline(cin, searchCity);

    for(int i = 0; i < myGraph.numCities; i++){     //Loop through all the cities in the graph to find the source node
        if(myGraph.cities[i]->name == searchCity){  //When a node with the same name has been found, set the source node as that node
            inputCity = myGraph.cities[i];
        }
    }

    if(inputCity == nullptr){                       //If a city was not found, end
        cout << "City could not be found...";
    } else {                                        //Else, print the adjacency matrix
        cout << right << setw(12) << myGraph.cities[0]->name;
        for(int i = 1; i < numCities; i++){
            cout << right << setw(6) << myGraph.cities[i]->name;
        }
        cout << endl;
                                    
        for(int i = 0; i < numCities; i++){         //Print the city name followed by the distances
            cout << setw(6) << myGraph.cities[i]->name; 
            for(int j = 0; j < numCities; j++){
                cout << setw(6) << distances[i][j];
            }
            cout << endl;
        }

        dijkstraAlg(&myGraph, inputCity, distances);    //Do the dijkstra algorithm
    }
    return 0;
}
