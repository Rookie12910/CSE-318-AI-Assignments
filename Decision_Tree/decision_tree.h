#ifndef DECISION_TREE_H
#define DECISION_TREE_H

#include <bits/stdc++.h>
#include "car.h"

class Node {
public:
    string attr_to_split;
    bool is_leaf;
    map<string, Node*> children;
    string class_label;

    Node() : is_leaf(false), class_label("") {}
};

map<string, int> get_class_count(const vector<Car>& cars) {
    map<string, int> class_count;
    for (const auto& car : cars) {
        class_count[car.get_class_val()]++;
    }
    return class_count;
}


string majority_class(const vector<Car>& cars) {
    if (cars.empty()) {
        return "";  
    }

    map<string, int> class_count = get_class_count(cars);
    
    string majority_class;
    int max_count = 0;

    for (const auto& p : class_count) {
        if (p.second > max_count) {
            max_count = p.second;
            majority_class = p.first;
        }
    }

    return majority_class;
}

double calculate_entropy(vector<Car>& cars) {
    if (cars.size() == 0) return 0.0;
    
    map<string, int> class_count = get_class_count(cars);

    double entropy = 0.0;
    int total = cars.size();
    
    for (auto& p : class_count) {
        double prob = (double)p.second / total;
        entropy -= prob * log2(prob);
    }
    
    return entropy;
}

double calculate_information_gain(vector<Car>& cars, string attr) {

    double parent_entropy = calculate_entropy(cars);
    
    map<string, vector<Car>> subsets;
    
    for (auto& car : cars) {
        string attr_value = car.get_attr_val(attr);
        subsets[attr_value].push_back(car);
    }
    
    double weighted_entropy = 0.0;
    for (auto& p : subsets) {
        double subset_prob = (double)p.second.size() / cars.size();
        weighted_entropy += subset_prob * calculate_entropy(p.second);
    }
    
    return parent_entropy - weighted_entropy;
}



double calculate_gini(const vector<Car>& cars) {
    if (cars.empty()) return 0.0;    
    map<string, int> class_count = get_class_count(cars);
    double gini = 1.0;
    int total = cars.size();

    for (auto& p : class_count) {
        double prob = (double)p.second / total;
        gini -= prob * prob;
    }
    
    return gini;
}

double calculate_gini_impurity(vector<Car>& cars, string attr) {

    double parent_gini = calculate_gini(cars); 
    
    map<string, vector<Car>> subsets;
    
    for (auto& car : cars) {
        string attr_value = car.get_attr_val(attr);
        subsets[attr_value].push_back(car);
    }
    
    double weighted_gini = 0.0;

    for (auto& p : subsets) {
        double subset_prob = (double)p.second.size() / cars.size();  
        double subset_gini = calculate_gini(p.second);  
        weighted_gini += subset_prob * subset_gini;  
    }

    return parent_gini - weighted_gini;
}




Node* build_decision_tree(vector<Car>& cars, vector<string>& attributes, string criterion, bool select_best_attr) {

     if (attributes.empty()) {
        Node* leaf_node = new Node();
        leaf_node->is_leaf = true;
        leaf_node->class_label = majority_class(cars);
        return leaf_node;
    }

    map<string, int> class_count = get_class_count(cars);
    if (class_count.size() == 1) {
        Node* leaf_node = new Node();
        leaf_node->is_leaf = true;
        leaf_node->class_label = cars[0].get_class_val();
        return leaf_node;
    }

    double max_gain = -1;
    string best_attr;
    string selected_attr;
    vector<pair<double,string>> top_attrs;

    for (auto& attr : attributes) {
        double gain;
        if(criterion=="information_gain") gain = calculate_information_gain(cars, attr);
        else if(criterion=="gini_impurity") gain = calculate_gini_impurity(cars, attr);
        else assert(false);

        top_attrs.push_back({gain,attr});

        if (gain > max_gain) {
            max_gain = gain;
            best_attr = attr;
        }
    }

    if(select_best_attr) selected_attr = best_attr;
    else {
        sort(top_attrs.begin(), top_attrs.end(), greater<pair<double, string>>());
        int d = min(3,(int)top_attrs.size());
        int random_number = rand() % d;
        selected_attr = top_attrs[random_number].second;
    }
    
    Node* node = new Node();
    node->attr_to_split = selected_attr;
    node->class_label = majority_class(cars);

    map<string, vector<Car>> subsets;
    for (auto& car : cars) {
        string attr_value = car.get_attr_val(selected_attr);
        subsets[attr_value].push_back(car);
    }

    vector<string> remaining_attributes = attributes;
    remaining_attributes.erase(remove(remaining_attributes.begin(), remaining_attributes.end(), selected_attr), remaining_attributes.end());

    for (auto& p : subsets) {
        Node* child_node = build_decision_tree(p.second, remaining_attributes,criterion,select_best_attr);
        node->children[p.first] = child_node;
    }

    return node;
}

string get_class_label(Node* root, const Car& car) {
    Node* curr = root;
    while (!curr->is_leaf) {
        string attr_val = car.get_attr_val(curr->attr_to_split);
        if(curr->children[attr_val]==nullptr) break;
        else curr = curr->children[attr_val];
    }
    return curr->class_label;
}


void print_tree(Node* root, string path = "") {
    if (root == nullptr) {
        return;
    }

    if (root->is_leaf) {
        cout << path << " ---> leaf(" << root->class_label << ")" << endl;
    } else {

        if (path.empty()) {
            path = "root(" + root->attr_to_split + ")";
        } else {
            path += " ---> " + root->attr_to_split;
        }

        for (auto& child : root->children) {
            string new_path = path + " --- " + child.first;
            print_tree(child.second, new_path);
        }
    }
}

#endif


