package com.example.employeedirectory

data class Employee(val empId: String? = null, val empName: String? = null, val empPhone: String? = null) {
    // Null default values create a no-argument default constructor, which is needed
    // for deserialization from a DataSnapshot.
    //Parameters used to create document fields
}