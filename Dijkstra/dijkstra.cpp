#include <iostream>
#include <string.h>
#include <fstream>
#include <string>
#include <math.h>
#include <iomanip>

using namespace std;

struct City {
    public:
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
    //If the node is the first node, then we have reached the end of the recursive function, print this as the first city
    if(node->prevCity == nullptr){
        cout << node->name;
    } else {
        printPath(node->prevCity);
        cout << " to " << node->name;
    }
}

void dijkstraAlg(Graph* g, City* sourceNode, int** distances){
    City** unvisitedSet = new City*[g->numCities];
    int unvisitedSize = g->numCities;
    int currentNodeIndex;
    City* currentNode = sourceNode;
    sourceNode->shortestDist = 0;

    //Add cities to unvisitedSet
    for(int i = 0; i < g->numCities; i++){
        unvisitedSet[i] = g->cities[i];
    }

    //Get the index of the current node
    for(int i = 0; i < g->numCities; i++){
        if(g->cities[i] == sourceNode){
            currentNodeIndex = i;
        }
    }
    currentNode = sourceNode;
    //While there are still unvisited nodes in the set
    while(unvisitedSize > 0){
        int shortestDist = INT_MAX;

        //Find the node with the actual shortest dist to use as the current comparison node
        for(int i = 0; i < g->numCities; i++){
            if(!unvisitedSet[i]->visited && unvisitedSet[i]->shortestDist != -1){
                if(unvisitedSet[i]->shortestDist < shortestDist){
                    shortestDist = unvisitedSet[i]->shortestDist;
                    currentNode = unvisitedSet[i];
                    currentNodeIndex = i;
                }
            }
        }
        
        //For all of the unvisited neighbors
        for(int i = 0; i < g->numCities; i++){
            //If the node is a neighbor and it is not visited
            if(distances[currentNodeIndex][i] > 0 && g->cities[i]->visited == false){
                if(g->cities[i]->shortestDist == -1 || g->cities[i]->shortestDist > currentNode->shortestDist + distances[currentNodeIndex][i]){
                    g->cities[i]->shortestDist = currentNode->shortestDist + distances[currentNodeIndex][i];
                    g->cities[i]->prevCity = currentNode;
                }
            }
        }
        currentNode->visited = true;
        unvisitedSize--;
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
    int** distances;
    int lines = 0;
    int numCities;
    ifstream file;
    string filePath;
    string cityName;
    City* inputCity = nullptr;
    

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

    for(int r = 0; r < numCities; r++){
        distances[r] = new int[numCities];
        getline(file, cityName);
        myGraph.cities[r] = new City(cityName);
        for(int c = 0; c < numCities; c++){
            distances[r][c] = 0;
        }
    }

    //Read adjacency matrix
    for(int i = 0; i < numCities; i++){
        for(int j = i + 1; j < numCities; j++){
            file >> distances[i][j];
            distances[j][i] = distances[i][j];
        }
    }
    file.close();

    string searchTerm;
    cout << "Input city name: ";
    getline(cin, searchTerm);

    
    for(int i = 0; i < myGraph.numCities; i++){
        if(myGraph.cities[i]->name == searchTerm){
            inputCity = myGraph.cities[i];
        }
    }

    if(inputCity == nullptr){
        cout << "City could not be found...";
    } else {
        //Print the first city name
        cout << right << setw(12) << myGraph.cities[0]->name;
        for(int i = 1; i < numCities; i++){
            cout << right << setw(6) << myGraph.cities[i]->name;
        }
        cout << endl;
        
        //Print the city name followed by the distances
        for(int i = 0; i < numCities; i++){
            cout << setw(6) << myGraph.cities[i]->name; 
            for(int j = 0; j < numCities; j++){
                cout << setw(6) << distances[i][j];
            }
            cout << endl;
        }

        //Do the dijkstra algorithm
        dijkstraAlg(&myGraph, inputCity, distances);
    }
    return 0;
}
