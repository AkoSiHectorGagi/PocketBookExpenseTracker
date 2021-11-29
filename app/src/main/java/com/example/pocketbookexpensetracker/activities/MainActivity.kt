package com.example.pocketbookexpensetracker.activities

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketbookexpensetracker.adapters.ItemAdapter
import com.example.pocketbookexpensetracker.models.ExpensesModelClass
import com.example.pocketbookexpensetracker.sqlite.DatabaseHandler
import com.example.pocketbookexpensetracker.R


class MainActivity : AppCompatActivity() {

    lateinit var rvItemsList: RecyclerView
    lateinit var tvNoRecordsAvailable: TextView
    lateinit var txtTotalExpense: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)


        rvItemsList = findViewById(R.id.rvItemsList)
        tvNoRecordsAvailable = findViewById(R.id.tvNoRecordsAvailable)
        txtTotalExpense = findViewById(R.id.txt_totalExpense)

        val addButton = findViewById<Button>(R.id.btnAdd)
        addButton.setOnClickListener {
            addRecord()
        }
        setupListofDataIntoRecyclerView()



    }
    //Method for saving the employee records in database
    private fun addRecord() {
        val addDialog = Dialog(this)
        addDialog.setCancelable(true)
        addDialog.setContentView(R.layout.dialog_add)
        val edtAddName = addDialog.findViewById<EditText>(R.id.edt_addName)
        val edtAddAmount = addDialog.findViewById<EditText>(R.id.edt_addAmount)
        val btnAdd = addDialog.findViewById<TextView>(R.id.tv_add)
        val btnCancel = addDialog.findViewById<TextView>(R.id.tv_cancel)

        btnAdd.setOnClickListener(View.OnClickListener {

            val name = edtAddName.text.toString()
            val amount = edtAddAmount.text.toString()

            val databaseHandler = DatabaseHandler(this)

            if (!name.isEmpty() && !amount.isEmpty()) {
                val status =
                    databaseHandler.addEmployee(ExpensesModelClass(0, name, amount.toInt()))
                if (status > -1) {
                    Toast.makeText(applicationContext, "Expense Added", Toast.LENGTH_LONG).show()
                    setupListofDataIntoRecyclerView()
                    addDialog.dismiss()
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Name or Email cannot be blank",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
        btnCancel.setOnClickListener(View.OnClickListener {
            addDialog.dismiss()
        })
        addDialog.show()
    }
    /**
     * Method is used to show the custom update dialog.
     */
    fun updateRecordDialog(expensesModelClass: ExpensesModelClass) {


        val updateDialog = Dialog(this)
        updateDialog.setCancelable(false)
        /*Set the screen content from a layout resource.
         The resource will be inflated, adding all top-level views to the screen.*/
        updateDialog.setContentView(R.layout.dialog_update)

        val etUpdateName = updateDialog.findViewById<EditText>(R.id.edt_addName)
        val etUpdateEmailId = updateDialog.findViewById<EditText>(R.id.edt_addAmount)
        val tvUpdate = updateDialog.findViewById<TextView>(R.id.tv_add)
        val tvCancel = updateDialog.findViewById<TextView>(R.id.tv_cancel)

        etUpdateName.setText(expensesModelClass.name)
        etUpdateEmailId.setText(expensesModelClass.amount)

        tvUpdate.setOnClickListener(View.OnClickListener {

            val name = etUpdateName.text.toString()
            val amount = etUpdateEmailId.text.toString()

            val databaseHandler = DatabaseHandler(this)

            if (!name.isEmpty() && !amount.isEmpty()) {
                val status =
                    databaseHandler.updateEmployee(ExpensesModelClass(expensesModelClass.id, name, amount.toInt()))
                if (status > -1) {
                    Toast.makeText(applicationContext, "Record Updated.", Toast.LENGTH_LONG).show()

                    setupListofDataIntoRecyclerView()

                    updateDialog.dismiss() // Dialog will be dismissed
                }
        } else {
            Toast.makeText(
                applicationContext,
                "Name or Email cannot be blank",
                Toast.LENGTH_LONG
            ).show()
        }
        })
        tvCancel.setOnClickListener(View.OnClickListener {
            updateDialog.dismiss()
        })
        //Start the dialog and display it on screen.
        updateDialog.show()
    }

    /**
     * Method is used to show the delete alert dialog.
     */
    fun deleteRecordAlertDialog(expensesModelClass: ExpensesModelClass) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Delete Record")
        //set message for alert dialog
        builder.setMessage("Are you sure you wants to delete ${expensesModelClass.name}.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->;

            //creating the instance of DatabaseHandler class
            val databaseHandler: DatabaseHandler = DatabaseHandler(this)
            //calling the deleteEmployee method of DatabaseHandler class to delete record
            val status = databaseHandler.deleteEmployee(ExpensesModelClass(expensesModelClass.id, "", expensesModelClass.amount))
            if (status > -1) {
                Toast.makeText(
                    applicationContext,
                    "Record deleted successfully.",
                    Toast.LENGTH_LONG
                ).show()

                setupListofDataIntoRecyclerView()
            }

            dialogInterface.dismiss() // Dialog will be dismissed
        }
        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }


    /**
     * Function is used to get the Items List which is added in the database table.
     */
    private fun getItemsList(): ArrayList<ExpensesModelClass> {
        //creating the instance of DatabaseHandler class
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        //calling the viewEmployee method of DatabaseHandler class to read the records
        val expensesList: ArrayList<ExpensesModelClass> = databaseHandler.viewEmployee()

        return expensesList
    }
    /**
     * Function is used to show the list on UI of inserted data.
     */
    private fun setupListofDataIntoRecyclerView() {

        if (getItemsList().size > 0) {
            rvItemsList.visibility = View.VISIBLE
            tvNoRecordsAvailable.visibility = View.GONE

            // Set the LayoutManager that this RecyclerView will use.
            rvItemsList.layoutManager = LinearLayoutManager(this)
            // Adapter class is initialized and list is passed in the param.
            val itemAdapter = ItemAdapter(this, getItemsList())
            // adapter instance is set to the recyclerview to inflate the items.
            rvItemsList.adapter = itemAdapter
        } else {
            rvItemsList.visibility = View.GONE
            tvNoRecordsAvailable.visibility = View.VISIBLE
        }
        calculateTotalExpense()
    }
    private fun calculateTotalExpense(){
        var total = 0
        for (i in 1..getItemsList().size) {

            total +=  getItemsList()[i-1].amount
        }
        txtTotalExpense.setText(total.toString())
    }
}