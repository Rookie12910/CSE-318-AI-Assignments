#ifndef CAR_H
#define CAR_H

#include <vector>
#include <string>

using namespace std; 

vector<string> attributes = {"buying_attr", "maint_attr", "doors_attr", "persons_attr", "lug_boot_attr", "safety_attr"};

class Car {
private:
    string buying_attr;
    string maint_attr;
    string doors_attr;
    string persons_attr;
    string lug_boot_attr;
    string safety_attr;
    string class_val;

public:
    Car(const string& buying_attr, const string& maint_attr, const string& doors_attr, 
        const string& persons_attr, const string& lug_boot_attr, const string& safety_attr, 
        const string& class_val) {
        this->buying_attr = buying_attr;
        this->maint_attr = maint_attr;
        this->doors_attr = doors_attr;
        this->persons_attr = persons_attr;
        this->lug_boot_attr = lug_boot_attr;
        this->safety_attr = safety_attr;
        this->class_val = class_val;
    }
    
    string get_class_val() const { return class_val; }

    string get_attr_val (const string& attr) const {
        if (attr == "buying_attr") return buying_attr;
        else if (attr == "maint_attr") return maint_attr;
        else if (attr == "doors_attr") return doors_attr;
        else if (attr == "persons_attr") return persons_attr;
        else if (attr == "lug_boot_attr") return lug_boot_attr;
        else if (attr == "safety_attr") return safety_attr;
        else assert(false);
    }
};

#endif