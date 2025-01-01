#include<bits/stdc++.h>
using namespace std;

int calculateHammingDistance(vector<vector<int>>& currState,vector<vector<int>>& goalState,int k);
int calculateManhattanDistance(vector<vector<int>>& currState,vector<vector<int>>& goalState,int k);

class searchNode
{
public:
    int moves;
    int estimatedCost;
    vector<vector<int>> state;
    searchNode *prev;

    searchNode(int moves, vector<vector<int>>& state,searchNode *prev)
    {
        this->moves = moves;
        this->state = state;
        this->prev = prev;
    }

    void calculateEstimatedCost(vector<vector<int>>& goalState, string heuristics)
    {
        if(heuristics=="Hamming") this->estimatedCost = this->moves + calculateHammingDistance(state,goalState,state.size());
        if(heuristics=="Manhattan") this->estimatedCost = this->moves + calculateManhattanDistance(state,goalState,state.size());
    }
};

vector<searchNode*> allocatedNodes; // To track allocated nodes for deletion later

bool isSolvable(vector<vector<int>>& initialState, int k)
{
    int inversionCount = 0;
    vector<int> initialOrder;
    int blankPos;
    for(int i=0; i < k; i++)
    {
        for(int j=0; j<k; j++)
        {
            if(initialState[i][j] == 0) blankPos = k-i;
            else initialOrder.push_back(initialState[i][j]);
        }
    }

    for(int i=0; i < initialOrder.size(); i++)
    {
        for(int j=i+1; j<initialOrder.size(); j++)
        {
            if(initialOrder[i] > initialOrder[j] ) inversionCount++;
        }
    }
    if(k%2!=0)
    {
        if(inversionCount%2 == 0) return true;
        return false;
    }
    else
    {
        if(blankPos%2 == 0 && inversionCount%2 != 0) return true;
        if(blankPos%2 != 0 && inversionCount%2 == 0) return true;
        return false;
    }

}

void generateGoalState(vector<vector<int>>& goalState,int k)
{
    int counter = 0;
    for(int i=0; i < k; i++)
    {
        for(int j=0; j<k; j++)
        {
            goalState[i][j] = ++counter;
        }
    }
    goalState[k-1][k-1] = 0;
}


int calculateHammingDistance(vector<vector<int>>& currState,vector<vector<int>>& goalState,int k)
{
    int distanceCount = 0;
    for(int i=0; i < k; i++)
    {
        for(int j=0; j<k; j++)
        {
            if(currState[i][j]!=0 && currState[i][j]!= goalState[i][j]) distanceCount++;
        }
    }
    return distanceCount;
}

int calculateManhattanDistance(vector<vector<int>>& currState,vector<vector<int>>& goalState,int k)
{
    int Size = k*k;
    int distanceCount = 0;

    vector<pair<int,int>> currStatePos(Size);
    vector<pair<int,int>> goalStatePos(Size);

    for(int i=0; i < k; i++)
    {
        for(int j=0; j<k; j++)
        {
            currStatePos[currState[i][j]] = {i,j};
            goalStatePos[goalState[i][j]] = {i,j};
        }
    }

    for(int i = 1; i<Size; i++)
    {
        distanceCount += abs(currStatePos[i].first - goalStatePos[i].first);
        distanceCount += abs(currStatePos[i].second - goalStatePos[i].second);
    }
    return distanceCount;
}

struct comparator
{
    bool operator()(searchNode* const& node1, searchNode* const& node2)
    {
        return node1->estimatedCost > node2->estimatedCost;
    }
};

pair<int,int> getBlankPos(vector<vector<int>>& state, int k)
{
    for(int i = 0; i<k; i++)
    {
        for(int j = 0; j<k; j++)
        {
            if(state[i][j]==0) return {i,j};
        }
    }
    return {-1,-1}; //INVALID
}

vector<searchNode*> generateChildren(searchNode* currNode,int k)
{
    pair<int,int> blankPos = getBlankPos(currNode->state,k);
    vector<pair<int,int>> pos = {{1,0},{-1,0},{0,1},{0,-1}};
    vector<vector<int>> changedState;
    vector<searchNode*> children;
    int indx,indy;
    for(int i = 0; i<pos.size(); i++)
    {
        changedState = currNode->state;
        indx = blankPos.first + pos[i].first;
        indy = blankPos.second + pos[i].second;
        if((indx<k && indx>=0) && (indy<k && indy>=0))
        {
            swap(changedState[blankPos.first][blankPos.second],changedState[indx][indy]);
            searchNode* newNode = new searchNode(currNode->moves + 1, changedState, currNode);
            children.push_back(newNode);
            allocatedNodes.push_back(newNode);
        }
    }
    return children;
}

bool hasMatched(vector<vector<int>>& currState, vector<vector<int>>& goalState, int k)
{
    for(int i = 0; i<k; i++)
    {
        for(int j = 0; j<k; j++)
        {
            if(currState[i][j]!=goalState[i][j]) return false;
        }
    }
    return true;
}

void printNode(searchNode* node,int k)
{
    vector<vector<int>> state = node->state;
    cout<<"Move : "<<node->moves<<endl;
    for(int i = 0; i<k; i++)
    {
        for(int j = 0; j<k; j++)
        {
            if(state[i][j]==0) cout<<'*'<<"  ";
            else cout<<state[i][j]<<"  ";
        }
        cout<<endl;
    }
    cout<<endl;
}

string hashedState(vector<vector<int>>& state)
{
    string hashedState;
    for (const auto& row : state) {
        for (const auto& val : row) {
            hashedState += to_string(val) + ",";
        }
    }
    return hashedState;
}


searchNode* solve(searchNode* initialNode, int k, string heuristics, int& exploredNodes, int& expandedNodes)
{
    priority_queue<searchNode*, vector<searchNode*>,comparator> pq;
    vector<vector<int>> goalState(k,vector<int>(k));
    vector<searchNode*> children;
    unordered_set<string> visitedBoards;

    generateGoalState(goalState,k);

    initialNode->calculateEstimatedCost(goalState,heuristics);
    pq.push(initialNode);

    while(!pq.empty())
    {
        searchNode* currNode = pq.top();
        pq.pop();
        expandedNodes++;
        string hashedNodeState = hashedState(currNode->state);
        visitedBoards.insert(hashedNodeState);
        if(hasMatched(currNode->state,goalState,k))
        {
            pq = priority_queue<searchNode*, std::vector<searchNode*>, comparator>();
            visitedBoards.clear();
            return currNode;
        }
        else
        {
            children = generateChildren(currNode,k);
            for(int i = 0; i<children.size(); i++)
            {
                exploredNodes++;
                string hashedChildState = hashedState(children[i]->state);
                if(visitedBoards.find(hashedChildState) == visitedBoards.end())
                {
                    children[i]->calculateEstimatedCost(goalState,heuristics);
                    pq.push(children[i]);
                }
            }
        }

    }

    return nullptr;
}

void cleanupNodes(vector<searchNode*>& allocatedNodes)
{
    for (auto node : allocatedNodes)
    {
        delete node;
    }
    allocatedNodes.clear();
}



int main()
{
    int k;
    string value;

    cout<<"Enter the grid size : ";
    cin>>k;

    vector<vector<int>> initialState(k,vector<int>(k));
    vector<searchNode*> ans1;
    vector<searchNode*> ans2;

    cout<<"Enter initial board positions:"<<endl;
    for(int i=0; i < k; i++)
    {
        for(int j=0; j<k; j++)
        {
            cin>>value;
            if (value=="*") initialState[i][j] = 0;
            else initialState[i][j] = stoi(value);
        }
    }

    cout<<endl;

    if(isSolvable(initialState,k))
    {
        cout<<"Yes, solvable"<<endl<<endl;
        int expandedNodes1 = 0, expandedNodes2 = 0;
        int exploredNodes1 = 1, exploredNodes2 = 1;

        searchNode* initialNode = new searchNode(0,initialState,nullptr);
        allocatedNodes.push_back(initialNode);
        searchNode* finalNode1 = solve(initialNode,k,"Manhattan",exploredNodes1,expandedNodes1);
        searchNode* finalNode2 = solve(initialNode,k,"Hamming",exploredNodes2,expandedNodes2);

        cout<<endl<< "For Manhattan Distance : "<<endl;
        cout<<"Total moves taken : "<<finalNode1->moves<<endl;
        cout<<"Total explored nodes : "<<exploredNodes1<<endl;
        cout<<"Total expanded nodes : "<<expandedNodes1<<endl;
        while(finalNode1!=nullptr)
        {
            ans1.push_back(finalNode1);
            finalNode1 = finalNode1->prev;
        }
        reverse(ans1.begin(),ans1.end());
        for(int i =0; i<ans1.size(); i++)
        {
            printNode(ans1[i],k);
        }

        cout<<endl<<"For Hamming Distance : "<<endl;
        cout<<"Total moves taken : "<<finalNode2->moves<<endl;
        cout<<"Total explored nodes : "<<exploredNodes2<<endl;
        cout<<"Total expanded nodes : "<<expandedNodes2<<endl;
        while(finalNode2!=nullptr)
        {
            ans2.push_back(finalNode2);
            finalNode2 = finalNode2->prev;
        }
        reverse(ans2.begin(),ans2.end());
        for(int i =0; i<ans2.size(); i++)
        {
            printNode(ans2[i],k);
        }
        cleanupNodes(allocatedNodes);
    }
    else cout<<"Not solvable"<<endl<<endl;

    return 0;
}

