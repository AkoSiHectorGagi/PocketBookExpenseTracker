package com.example.pocketbookexpensetracker.activities

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketbookexpensetracker.adapters.ItemAdapter
import com.example.pocketbookexpensetracker.models.ExpenseModelClass
import com.example.pocketbookexpensetracker.sqlite.ExpenseDatabaseHandler
import com.example.pocketbookexpensetracker.R
import com.example.pocketbookexpensetracker.models.ExpensesModelClass
import com.example.pocketbookexpensetracker.sqlite.ExpensesDatabaseHandler
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    lateinit var rvItemsList: RecyclerView
    lateinit var tvNoRecordsAvailable: TextView
    lateinit var txtTotalExpense: TextView
    lateinit var txtDate: TextView
    lateinit var txtBudget: TextView
    lateinit var txtBalance: TextView
    var currentIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)

        if (getExpensesItemsList().size == 0) {//checks if there is existing budget
            addBudget()
            currentIndex = 0
        } else
            currentIndex = getExpensesItemsList().size - 1

        rvItemsList = findViewById(R.id.rvItemsList)
        tvNoRecordsAvailable = findViewById(R.id.tvNoRecordsAvailable)
        txtTotalExpense = findViewById(R.id.txt_totalExpense)
        txtDate = findViewById(R.id.txt_date)
        txtBudget = findViewById(R.id.txt_budget_amount)
        txtBalance = findViewById(R.id.txt_bal_amount)

        val addButton = findViewById<Button>(R.id.btnAdd)
        addButton.setOnClickListener {
            addRecord()
        }

        var ivLeftArrow = findViewById<ImageView>(R.id.iv_left_arrow)
        ivLeftArrow.setOnClickListener {
            currentIndex--
            log()
        }
        var ivRightArrow = findViewById<ImageView>(R.id.iv_right_arrow)
        ivRightArrow.setOnClickListener {
            if (currentIndex == getExpensesItemsList().size - 1) {
                currentIndex++
                addBudget()
            } else
                currentIndex++
//            log()
//            txtDate.setText(getExpensesItemsList()[currentIndex].date)
            setupListofDataIntoRecyclerView()
            setupDataofExpenses()
        }

        setupListofDataIntoRecyclerView()
        setupDataofExpenses()
        log()


    }

    private fun addBudget() {
        val budgetDialog = Dialog(this)
        budgetDialog.setCancelable(false)
        budgetDialog.setContentView(R.layout.dialog_budget)
        val edtAddBudget = budgetDialog.findViewById<EditText>(R.id.edt_addBudget)
        val btnAdd = budgetDialog.findViewById<TextView>(R.id.tv_add)


        btnAdd.setOnClickListener(View.OnClickListener {

            val budget = edtAddBudget.text.toString()
            val balance = edtAddBudget.text.toString()

            val databaseHandler = ExpensesDatabaseHandler(this)

            if (!budget.isEmpty()) {
                val status =
                    databaseHandler.addExpenses(
                        ExpensesModelClass(
                            budget.toInt(),
                            balance.toInt(),
                            getDate(),
                            currentIndex
                        )
                    )
                if (status > -1) {
                    Toast.makeText(applicationContext, "Budget Added", Toast.LENGTH_LONG).show()
                    //setupListofDataIntoRecyclerView()
                    setupDataofExpenses()
                    budgetDialog.dismiss()
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Budget cannot be empty",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
        budgetDialog.show()

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

            val databaseHandler = ExpenseDatabaseHandler(this)

            if (!name.isEmpty() && !amount.isEmpty()) {
                val status =
                    databaseHandler.addExpense(ExpenseModelClass(0, name, amount.toInt(), 0))
                if (status > -1) {
                    Toast.makeText(applicationContext, "Expense Added", Toast.LENGTH_LONG).show()
                    setupListofDataIntoRecyclerView()
                    addDialog.dismiss()
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Expense Name or Expense Amount cannot be blank",
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
    fun updateRecordDialog(expenseModelClass: ExpenseModelClass) {


        val updateDialog = Dialog(this)
        updateDialog.setCancelable(false)
        /*Set the screen content from a layout resource.
         The resource will be inflated, adding all top-level views to the screen.*/
        updateDialog.setContentView(R.layout.dialog_update)

        val etUpdateName = updateDialog.findViewById<EditText>(R.id.edt_addName)
        val etUpdateEmailId = updateDialog.findViewById<EditText>(R.id.edt_addAmount)
        val tvUpdate = updateDialog.findViewById<TextView>(R.id.tv_add)
        val tvCancel = updateDialog.findViewById<TextView>(R.id.tv_cancel)

        etUpdateName.setText(expenseModelClass.name)
        etUpdateEmailId.setText(expenseModelClass.amount)

        tvUpdate.setOnClickListener(View.OnClickListener {

            val name = etUpdateName.text.toString()
            val amount = etUpdateEmailId.text.toString()

            val databaseHandler = ExpenseDatabaseHandler(this)

            if (!name.isEmpty() && !amount.isEmpty()) {
                val status =
                    databaseHandler.updateExpense(
                        ExpenseModelClass(
                            expenseModelClass.id,
                            name,
                            amount.toInt(),
                            0
                        )
                    )
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
    fun deleteRecordAlertDialog(expenseModelClass: ExpenseModelClass) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Delete Record")
        //set message for alert dialog
        builder.setMessage("Are you sure you wants to delete ${expenseModelClass.name}.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->


            //creating the instance of DatabaseHandler class
            val expenseDatabaseHandler: ExpenseDatabaseHandler = ExpenseDatabaseHandler(this)
            //calling the deleteEmployee method of DatabaseHandler class to delete record
            val status = expenseDatabaseHandler.deleteExpense(
                ExpenseModelClass(
                    expenseModelClass.id,
                    "",
                    expenseModelClass.amount,
                    expenseModelClass.groupId
                )
            )
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
    private fun getExpenseItemsList(): ArrayList<ExpenseModelClass> {
        //creating the instance of DatabaseHandler class
        val expenseDatabaseHandler: ExpenseDatabaseHandler = ExpenseDatabaseHandler(this)
        //calling the viewEmployee method of DatabaseHandler class to read the records
        val expenseList: ArrayList<ExpenseModelClass> = expenseDatabaseHandler.viewExpense()

        return expenseList
    }

    private fun getExpensesItemsList(): ArrayList<ExpensesModelClass> {
        val expensesDatabaseHandler: ExpensesDatabaseHandler = ExpensesDatabaseHandler(this)
        val expensesList: ArrayList<ExpensesModelClass> = expensesDatabaseHandler.viewExpenses()

        return expensesList
    }

    /**
     * Function is used to show the list on UI of inserted data.
     */
    private fun setupListofDataIntoRecyclerView() {

        if (getExpenseItemsList().size > 0) {
            rvItemsList.visibility = View.VISIBLE
            tvNoRecordsAvailable.visibility = View.GONE

            // Set the LayoutManager that this RecyclerView will use.
            rvItemsList.layoutManager = LinearLayoutManager(this)
            // Adapter class is initialized and list is passed in the param.
            val itemAdapter = ItemAdapter(this, getExpenseItemsList())
            // adapter instance is set to the recyclerview to inflate the items.
            rvItemsList.adapter = itemAdapter
        } else {
            rvItemsList.visibility = View.GONE
            tvNoRecordsAvailable.visibility = View.VISIBLE
        }
        calculateTotalExpense()
    }

    private fun setupDataofExpenses(){
        try {
            txtDate.text = getDate()
            txtBudget.text = getExpensesItemsList()[currentIndex].balance.toString()
            txtBalance.text = getExpensesItemsList()[currentIndex].budget.toString()
        } catch (Ex: Exception) {
            Log.e("error","era")
        }

    }

    private fun calculateTotalExpense() {
        var total = 0
        for (i in 1..getExpenseItemsList().size) {

            total += getExpenseItemsList()[i - 1].amount
        }
        txtTotalExpense.setText(total.toString())
    }

    private fun getDate(): String {
        //val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("MMMM dd")
        //val formatted = current.format(formatter)
        val today: LocalDate = LocalDate.now()
        val tomorrow: LocalDate = today.plusDays(1)
        val formatted = tomorrow.format(formatter)
        return formatted
    }

    private fun log() {
        var totalrecords = 0
        for (i in 1..getExpensesItemsList().size)
            totalrecords += 1

        Log.i("mytag", "total records: " + totalrecords.toString())
        Log.i("mytag", "current Index: " + currentIndex.toString())
        Log.i("mytag", getExpensesItemsList()[currentIndex].date)
    }

}