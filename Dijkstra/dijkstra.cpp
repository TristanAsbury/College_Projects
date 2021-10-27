#include <iostream>
#include <string.h>
#include <fstream>
#include <string>
#include <math.h>

using namespace std;

struct City {
    public:
    
    //This is for the visiting queue
    bool visited;

    //Shortest path to this node
    int shortestDist;
    City* prevCity;

    //The name of the city
    string name;

    City(string name){
        prevCity = nullptr;
        this->name = name;
        visited = false; //-1 = not visited, 1 = visited
        shortestDist = -1;
    }
};


struct Graph {
    City** cities;
    int numCities;
    int currentNodes;

    Graph(int numCities){
        this->numCities = numCities;
        cities = new City*[numCities];
        currentNodes = 0;
    }

    void addCity(string name){    
        cities[currentNodes] = new City(name);
        currentNodes++;

    }
};

void dijkstraAlg(Graph* g, City* sourceNode, int** distances){
    //Mark all nodes as unvisited [YES]
    //Give every node a distance value, and set source node dist to 0 [YES]

    City** unvisitedSet = new City*[g->numCities];
    int unvisitedSize = g->numCities;
    int currentNodeIndex;
    City* currentNode = sourceNode;

    //Add cities to unvisitedSet
    for(int i = 0; i < g->numCities; i++){
        if(g->cities[i] != currentNode){
            unvisitedSet[i] = g->cities[i];
        }
    }

    //Get the index of the current node
    for(int i = 0; i < g->numCities; i++){
        if(g->cities[i] == sourceNode){
            cout << "Index of source node is: " << i << endl;
            currentNodeIndex = i;
        }
    }

    //For all of the unvisited neighbors
    for(int i = 0; i < g->numCities; i++){
        //If the node is a neighbor and it is not visited
        if(distances[currentNodeIndex][i] > 0 && g->cities[i]->visited == false){
            cout << "A connected node to " << currentNode->name << " is " << g->cities[i]->name << endl;
            //If the neighboring city we are checking has a distance that is greater than the current node + the distance to that node, then the shorter distance is this path
            if(g->cities[i]->shortestDist > currentNode->shortestDist + distances[currentNodeIndex][i]){
                currentNode->shortestDist = distances[currentNodeIndex][i];
                g->cities[i]->prevCity = currentNode;
            }
        }
    }
    currentNode->visited = true;
    unvisitedSize--;
    
    sourceNode->shortestDist = 0;

    for(int i = 0; i < g->numCities; i++){
        if(sourceNode != g->cities[i]){
            cout << "Shortest distance from " << sourceNode->name << " to " << g->cities[i]->name << " is: ";
            City* currentCity = g->cities[i];
            while(currentCity->prevCity != nullptr){
                cout << currentCity->name << " to ";
                currentCity = currentCity->prevCity;
            }
            cout << endl;
        }
    }
}


int main(){
    int step = 1;
    int** distances;
    ifstream file;
    int lines;
    file.open("test.txt");
    if(file.good()){
        string token;
        while(!file.eof()){
            getline(file, token);
            lines++;
        }
    }
    file.close();
    
    
    int numCities = (int)(ceil(lines/2)) + 1;
    distances = new int*[numCities];
    
    for (int i = 0; i < numCities; i++) {
		distances[i] = new int[numCities];
	}

    for(int r = 0; r < numCities; r++){
        for(int c = 0; c < numCities; c++){
            distances[r][c] = 0;
        }
    }

    Graph myGraph(numCities);

    file.open("test.txt");
    string cityName;

    for(int i = 0; i < numCities; i++){
        getline(file, cityName);
        myGraph.addCity(cityName);
    }

    //Read adjacency matrix
    for(int i = 0; i < numCities; i++){
        for(int j = i + 1; j < numCities; j++){
            file >> distances[i][j];
            distances[j][i] = distances[i][j];
        }
    }

    City* inputCity;
    for(int i = 0; i < myGraph.numCities; i++){
        if(myGraph.cities[i]->name == "B"){
            inputCity = myGraph.cities[i];
        }
    }

    for(int i = 0; i < numCities; i++){
        for(int j = 0; j < numCities; j++){
            cout << distances[i][j] << " ";
        }
        cout << endl;
    }

    dijkstraAlg(&myGraph, inputCity, distances);
    return 0;
}
