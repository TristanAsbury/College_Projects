#include <iostream>
#include <string.h>
#include <fstream>
#include <string>
#include <cstring>
#include <vector>
#include <math.h>

using namespace std;

struct City {
    public:
    
    //This is for the visiting queue
    int visited;

    //A vector of connected nodes
    vector<City*> connectedNodes = vector<City*>();

    //Shortest path to this node
    int shortestDist;
    vector<City> shortestPathTo = vector<City>();

    //The name of the city
    string name;

    City(string name){
        this->name = name;
        visited = -1; //-1 = not visited, 1 = visited
    }
};



struct Graph {

};



void dijkstraAlg(Graph* g, int sourceIndex){

    
    
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
    for (int i = 0; i < numCities; ++i) {
		distances[i] = new int[numCities];
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
    

    dijkstra(&myGraph, "E", distances);
    return 0;
}
