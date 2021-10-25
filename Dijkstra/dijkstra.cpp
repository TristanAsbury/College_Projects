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
    vector<City> shortestPath = vector<City>();

    //The name of the city
    string name;

    City(string name){
        this->name = name;
        visited = -1; //-1 = not visited, 1 = visited
    }
};

struct QueueNode {
    public:
    City* data;
    QueueNode* next;

    QueueNode(City* node){
        data = node;
        next = nullptr;
    }
};

struct Queue {
    public:
    QueueNode* front;
    QueueNode* back;

    Queue(){
        front = nullptr;
        back = front;
    }

    void push(City* node){
        if(front == nullptr){
            front = new QueueNode(node);
            back = front;
        } else {
            back->next = new QueueNode(node);
            back = back->next;
        }
    }

    City* pop(){
        if(back == nullptr){
            cout << "Cannot pop, queue empty!";
            return nullptr;
        } else {
            City* node = front->data;
            QueueNode* nextFront = front->next;
            delete(front);
            front = nextFront;
            return node;
        }
    }
};

struct Graph {
    public:
    int* distances;
    int rows;
    vector<City*> cities = vector<City*>();
    Graph(){
        distances = new int[36];
    }
    Graph(int numCities){
        distances = new int[numCities*numCities]; //Creates a square matrix of the cities distances, 0 will be a -> a, 1 will be a -> b, etc.
        for(int i = 0; i < numCities*numCities; i++){
            distances[i] = 0;
        }
        rows = numCities;
    }

    //row = from, col = to
    void setDist(int row, int col, int dist){
        distances[row*rows+col] = dist; //a, b
        distances[col*rows+row] = dist; //b, a 
    }

    void setUpDists(){
        distances = new int[cities.size()*cities.size()];
        for(int i = 0; i < cities.size()*cities.size(); i++){
            distances[i] = 0;
        }
    }

    int getDist(int row, int col){
        return distances[row*rows+col];
    }

    void addNode(string cityName){
        cities.push_back(new City(cityName));
    }
};



void dijkstraAlg(Graph* g){
//For each node in the graph
    for(int i = 0; i < g->rows; i++){
        Queue* waitingQueue = new Queue();
        cout << "Looking at city: " << g->cities.at(i)->name;

    }
}

int main(){
    string fileName;

    Graph* g = new Graph(6);
    
    int matrix[6][6] = {
    {3, 1, 0, 0, 0},
       {1, 2, 0, 0},
          {3, 5, 0},
             {1, 3},
                {1},
                 {}
    };

    cout << "Adding nodes" << endl;
    g->addNode("S");
    g->addNode("A");
    g->addNode("B");
    g->addNode("C");
    g->addNode("D");
    g->addNode("E");

    cout << "Setting dists" << endl;
    for(int r = 0; r < 6; r++){
        for(int c = 0; c < 6-(c+1); c++){
            g->setDist(r, c+(r+1), matrix[r][c]);
        }
    }

    cout << "Getting dists" << endl;
    for(int r = 0; r < 6; r++){
        for(int c = 0; c < 6; c++){
            cout << "Distance from " << g->cities.at(r)->name << " to " << g->cities.at(c)->name << " is: " << g->getDist(r,c) << endl;
        }
    }

    dijkstraAlg(g);

    

    

    return 0;
}
