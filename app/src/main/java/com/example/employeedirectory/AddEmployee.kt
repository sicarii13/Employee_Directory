package com.example.employeedirectory

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class AddEmployee : AppCompatActivity() {
    var id = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_employee)

        val eID: TextView = findViewById(R.id.etEmpID)
        val eName: EditText = findViewById(R.id.etEmpName)
        val ePhone: EditText = findViewById(R.id.etEmpPhone)

        val addEmp: Button = findViewById(R.id.btnAdd)

        try{
            val extras = intent.extras

            if (extras != null) {
                id = extras.getString("EmpID", "0")
            }

            Toast.makeText(this, id, Toast.LENGTH_SHORT).show()
            if(id.toInt()!=0){
                addEmp.text = "Update Employee"
                eID.text = extras?.getString("EmpID")
                eName.setText(extras?.getString("EmpName"))
                ePhone.setText(extras?.getString("EmpPhone"))
            }
        } catch(ex: Exception){}

        addEmp.setOnClickListener{
            val db = Firebase.firestore
            if(id.toInt()==0) {
                if (eID.text.isEmpty() || eName.text.isEmpty() || ePhone.text.isEmpty()) {
                    Toast.makeText(this, "Please complete all fields!", Toast.LENGTH_SHORT).show()
                } else {
                    val employee =
                        Employee(eID.text.toString(), eName.text.toString(), ePhone.text.toString())

                    db.collection("EmpDir")
                        .add(employee)

                    Toast.makeText(this, "Employee added successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }else{
                var docID: String = ""
                db.collection("EmpDir")
                    .whereEqualTo("empId", eID.text.toString())
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            docID = document.id
                        }
                        db.collection("EmpDir").document(docID)
                            .update(
                                mapOf(
                                    "empId" to eID.text.toString(),
                                    "empName" to eName.text.toString(),
                                    "empPhone" to ePhone.text.toString()
                                )
                            )
                            .addOnSuccessListener {
                                Toast.makeText(this, "Document successfully updated.", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, MainActivity::class.java))
                            }
                            .addOnFailureListener { Toast.makeText(this, "Error updating document!", Toast.LENGTH_SHORT).show() }
                    }
            }
        }
    }
}