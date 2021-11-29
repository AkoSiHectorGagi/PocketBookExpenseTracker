package com.example.pocketbookexpensetracker.sqlite


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.pocketbookexpensetracker.models.ExpensesModelClass

//creating the database logic, extending the SQLiteOpenHelper base class
class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_VERSION = 2
        private val DATABASE_NAME = "ExpenseDatabase"

        private val TABLE_CONTACTS = "ExpensesTable"

        private val KEY_ID = "_id"
        private val KEY_NAME = "name"
        private val KEY_EXPENSE_AMOUNT = "amount"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        //creating table with fields
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EXPENSE_AMOUNT + " INTEGER" + ")")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        onCreate(db)
    }
    /**
     * Function to insert data
     */
    fun addEmployee(expenses: ExpensesModelClass): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, expenses.name) // EmpModelClass Name
        contentValues.put(KEY_EXPENSE_AMOUNT, expenses.amount) // EmpModelClass Email

        // Inserting employee details using insert query.
        val success = db.insert(TABLE_CONTACTS, null, contentValues)
        //2nd argument is String containing nullColumnHack

        db.close() // Closing database connection
        return success
    }
    //Method to read the records from database in form of ArrayList
    fun viewEmployee(): ArrayList<ExpensesModelClass> {

        val expensesList: ArrayList<ExpensesModelClass> = ArrayList<ExpensesModelClass>()

        // Query to select all the records from the table.
        val selectQuery = "SELECT  * FROM $TABLE_CONTACTS"

        val db = this.readableDatabase
        // Cursor is used to read the record one by one. Add them to data model class.
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var name: String
        var amount: Int

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                name = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                amount = cursor.getInt(cursor.getColumnIndex(KEY_EXPENSE_AMOUNT))

                val exp = ExpensesModelClass(id = id, name = name, amount = amount)
                expensesList.add(exp)

            } while (cursor.moveToNext())
        }
        return expensesList
    }
    /**
     * Function to update record
     */
    fun updateEmployee(expenses: ExpensesModelClass): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, expenses.name) // EmpModelClass Name
        contentValues.put(KEY_EXPENSE_AMOUNT, expenses.amount) // EmpModelClass Email

        // Updating Row
        val success = db.update(TABLE_CONTACTS, contentValues, KEY_ID + "=" + expenses.id, null)
        //2nd argument is String containing nullColumnHack

        // Closing database connection
        db.close()
        return success
    }
    /**
     * Function to delete record
     */
    fun deleteEmployee(expenses: ExpensesModelClass): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, expenses.id) // EmpModelClass id
        // Deleting Row
        val success = db.delete(TABLE_CONTACTS, KEY_ID + "=" + expenses.id, null)
        //2nd argument is String containing nullColumnHack

        // Closing database connection
        db.close()
        return success
    }
}