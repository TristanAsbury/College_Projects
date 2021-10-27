#include <iostream>
#include <fstream>
#include <string>
#include <math.h>

using namespace std;

struct Node {
    
    string name;
    //Minimum total path length to node
    int length;
    //Previous node
    Node* previousNode;

    Node(string name){
        this->name = name;
    }

    Node(){}
};

struct Graph {
    Node** cities;
    int numCities;
    int currentNodes;

    Graph(int numCities){
        this->numCities = numCities;
        cities = new Node*[numCities];
        currentNodes = 0;
    }

    void addCity(string name){    
        cities[currentNodes] = new Node(name);
        currentNodes++;

    }
};

void dijkstra(Graph* g, string source, int** distances){
    int step = 1;
    Node* sourceNode;
    Node* currentNode;
    int cityIndex;
    int queue[100] = {0};
    int front = 0, back = 1;
    for(int i = 0; i < g->numCities; i++){
        if(g->cities[i]->name == source){
            sourceNode = g->cities[i];
            cityIndex = i;
        }
    }
    
    while(front != back){
        for(int i = 0; i < g->numCities; i++){
            cout << "Searching from node " << g->cities[i]->name << endl;
            if(g->cities[queue[front]] != g->cities[i]){
                if(distances[cityIndex][i] > 0){
                    int minDist = currentNode->length + distances[cityIndex][i];
                    if(minDist < g->cities[i]->length || g->cities[i]->length == 0){
                        queue[back] = i;
                        back++;
                        if(back >= 100){
                            back -= 100;
                        }
                        g->cities[i]->length = minDist;
                        g->cities[i]->previousNode = currentNode;
                    }
                }
            }
        }
        front++;
        if(front >= 100){
            front -= 100;
        }
    }

    for(int i = 0; i < g->numCities; i++){
        cout << "Step " << step++ << " completed.";
        cout << "The shortest distance from " << sourceNode->name << " to " << g->cities[i]->name << " is: " << g->cities[i]->length << endl;
        int lastElement = 1;
        bool completePath = false;
        Node** path = new Node*[g->numCities];
        for(int j = 0; j < g->numCities; j++){
            path[j] = 0;
        }

        path[0] = g->cities[i];

        while(!completePath){
            path[lastElement] = path[lastElement-1]->previousNode;
            if(path[lastElement] == sourceNode){
                completePath = true;
            }
            lastElement++;
        }

        cout << "The shortest path is: " << g->cities[0]->name;
        lastElement-=2;
        while(lastElement >= 0){
            cout << " to " << path[lastElement]->name;
            lastElement--;
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
