#include<bits/stdc++.h> 
#include "car.h"
#include "decision_tree.h"

using namespace std;  

extern vector<string> attributes;
const int ITERATION = 20;
const double train_percentage = 0.8;

int main() {
    srand(time(0));
    vector<Car> cars;  
    ifstream file("car.data"); 
    ofstream ofile("performance_result.txt");
    string line;

    if (file.is_open()) {
        while (getline(file, line)) {
            stringstream ss(line);
            string data[7];
            int i = 0;

            while (getline(ss, data[i], ',') && i < 7) {
                i++;
            }

            if (i == 7) {
                Car car(data[0], data[1], data[2], data[3], data[4], data[5], data[6]);
                cars.push_back(car);  
            }
        }

        file.close();  
    } else {
        cerr << "Unable to open file" << endl;
        return 1;  
    }

    double total_accuracy_1 = 0.0;
    double total_accuracy_2 = 0.0;
    double total_accuracy_3 = 0.0;
    double total_accuracy_4 = 0.0;

    for(int i = 0; i<ITERATION; i++) {

    random_shuffle(cars.begin(), cars.end());
    vector<Car> training_set(cars.begin(), cars.begin() + cars.size() * train_percentage);
    vector<Car> test_set(cars.begin() + cars.size() * train_percentage, cars.end());
    
    Node* root_1 = build_decision_tree(training_set, attributes, "information_gain",true);
    Node* root_2 = build_decision_tree(training_set, attributes, "gini_impurity",true);
    Node* root_3 = build_decision_tree(training_set, attributes, "information_gain",false);
    Node* root_4 = build_decision_tree(training_set, attributes, "gini_impurity",false);

    string predicted_class_val_1;
    string predicted_class_val_2;
    string predicted_class_val_3;
    string predicted_class_val_4;

    string actual_class_val;

    int correct_predictions_1 = 0;
    int correct_predictions_2 = 0;
    int correct_predictions_3 = 0;
    int correct_predictions_4 = 0;

    for(auto& car : test_set) {
        actual_class_val = car.get_class_val();
        
        predicted_class_val_1 = get_class_label(root_1, car);
        predicted_class_val_2 = get_class_label(root_2, car);
        predicted_class_val_3 = get_class_label(root_3, car);
        predicted_class_val_4 = get_class_label(root_4, car);
        
        if(actual_class_val == predicted_class_val_1) {
            correct_predictions_1++;
        }

        if(actual_class_val == predicted_class_val_2) {
            correct_predictions_2++;
        }

        if(actual_class_val == predicted_class_val_3) {
            correct_predictions_3++;
        }

        if(actual_class_val == predicted_class_val_4) {
            correct_predictions_4++;
        }
        
    }

    double accuracy_1 = ((double)correct_predictions_1 / test_set.size()) * 100;
    double accuracy_2 = ((double)correct_predictions_2 / test_set.size()) * 100;
    double accuracy_3 = ((double)correct_predictions_3 / test_set.size()) * 100;
    double accuracy_4 = ((double)correct_predictions_4 / test_set.size()) * 100;

    total_accuracy_1 += accuracy_1;
    total_accuracy_2 += accuracy_2;
    total_accuracy_3 += accuracy_3;
    total_accuracy_4 += accuracy_4;

    // cout<<"accuracy_1 (Information gain): "<<accuracy_1<<" %"<<endl;
    // cout<<"accuracy_2 (Gini impurity): "<<accuracy_2<<" %"<<endl;
    // cout<<"accuracy_3 (Information gain - random): "<<accuracy_3<<" %"<<endl;
    // cout<<"accuracy_4 (Gini impurity - random): "<<accuracy_4<<" %"<<endl;
    //cout<<"-----------------------------------------------------------------------------------"<<endl;


}

    double avg_accuracy_1 = (total_accuracy_1 / ITERATION);
    double avg_accuracy_2 = (total_accuracy_2 / ITERATION);
    double avg_accuracy_3 = (total_accuracy_3 / ITERATION);
    double avg_accuracy_4 = (total_accuracy_4 / ITERATION);


    cout<<"-----------------------------------------------------------------------------------"<<endl;
    cout<<"|                                             |       Average Accuracy over 20 run |"<<endl;
    cout<<"------------------------------------------------------------------------------------"<<endl;
    cout<<"|    Attribute Selection strategy             |  Information gain |  Gini impurity |"<<endl;
    cout<<"------------------------------------------------------------------------------------"<<endl;
    cout<<"|    Always select the best attriute          |    "<<avg_accuracy_1<<" %      |   "<< avg_accuracy_2<<" %    |"<<endl;
    cout<<"------------------------------------------------------------------------------------"<<endl;
    cout<<"| Select one Randomly from top 3 attributes   |    "<<avg_accuracy_3<<" %      |   "<< avg_accuracy_4<<" %    |"<<endl;
    cout<<"------------------------------------------------------------------------------------"<<endl;


    ofile<<"-----------------------------------------------------------------------------------"<<endl;
    ofile<<"|                                             |       Average Accuracy over 20 run |"<<endl;
    ofile<<"------------------------------------------------------------------------------------"<<endl;
    ofile<<"|    Attribute Selection strategy             |  Information gain |  Gini impurity |"<<endl;
    ofile<<"------------------------------------------------------------------------------------"<<endl;
    ofile<<"|    Always select the best attriute          |    "<<avg_accuracy_1<<" %      |   "<< avg_accuracy_2<<" %    |"<<endl;
    ofile<<"------------------------------------------------------------------------------------"<<endl;
    ofile<<"| Select one Randomly from top 3 attributes   |    "<<avg_accuracy_3<<" %      |   "<< avg_accuracy_4<<" %    |"<<endl;
    ofile<<"------------------------------------------------------------------------------------"<<endl;


    return 0;
}