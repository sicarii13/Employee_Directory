package com.example.employeedirectory

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private var db = Firebase.firestore

    private lateinit var empList: ArrayList<Employee>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val addNew: Button = findViewById(R.id.btnNew)

        var rvEmps: RecyclerView = findViewById(R.id.rvEmps)
        rvEmps.layoutManager = LinearLayoutManager(this)

        empList = arrayListOf()

        db = FirebaseFirestore.getInstance()

        db.collection("EmpDir")
            .get()
            .addOnSuccessListener {
                if(!it.isEmpty){
                    for(data in it.documents){
                        val emp: Employee? = data.toObject(Employee::class.java)
                        if(emp != null){
                            empList.add(emp)
                        }
                    }
                    rvEmps.adapter = EmpAdapter(empList)
                }
            }
            .addOnFailureListener{
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }

        addNew.setOnClickListener {
            val intent = Intent(this, AddEmployee::class.java)
            startActivity(intent)
        }
    }
    inner class EmpAdapter (private val empList: ArrayList<Employee>) : RecyclerView.Adapter<EmpAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            val id: TextView = itemView.findViewById(R.id.tvID)
            val name: TextView = itemView.findViewById(R.id.tvName)
            val phone: TextView = itemView.findViewById(R.id.tvPhone)
            val ibDelete: ImageButton = itemView.findViewById(R.id.ibDelete)
            val ibEdit: ImageButton = itemView.findViewById(R.id.ibEdit)
            val ibCopy: ImageButton = itemView.findViewById(R.id.ibCopy)
            val ibShare: ImageButton = itemView.findViewById(R.id.ibShare)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row, parent, false)

            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.id.text = empList[position].empId
            holder.name.text = empList[position].empName
            holder.phone.text = empList[position].empPhone

            holder.ibCopy.setOnClickListener {
                val str = holder.id.text.toString() + "\n" + holder.name.text.toString() + "\n" + holder.phone.text.toString()
                val clip = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                clip.text = str
                Toast.makeText(this@MainActivity, "Copied...", Toast.LENGTH_SHORT).show()
            }

            holder.ibDelete.setOnClickListener {
                var docID: String = ""
                db.collection("EmpDir")
                    .whereEqualTo("empId", holder.id.text.toString())
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            Toast.makeText(this@MainActivity, document.id, Toast.LENGTH_SHORT).show()
                            docID = document.id
                        }
                        db.collection("EmpDir").document(docID)
                            .delete()
                            .addOnSuccessListener { Toast.makeText(this@MainActivity, "Record successfully deleted.", Toast.LENGTH_SHORT).show() }
                            .addOnFailureListener { Toast.makeText(this@MainActivity, "Error deleting document!", Toast.LENGTH_SHORT).show() }
                    }
            }

            holder.ibEdit.setOnClickListener {
                val intent = Intent(this@MainActivity, AddEmployee::class.java)
                intent.putExtra("EmpID", holder.id.text.toString())
                intent.putExtra("EmpName", holder.name.text.toString())
                intent.putExtra("EmpPhone", holder.phone.text.toString())
                startActivity(intent)
            }

            holder.ibShare.setOnClickListener{
                val str = holder.id.text.toString() + "\n" + holder.name.text.toString() + "\n" + holder.phone.text.toString()

                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, str)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }

        }

        override fun getItemCount(): Int {
            return empList.size
        }
    }
}