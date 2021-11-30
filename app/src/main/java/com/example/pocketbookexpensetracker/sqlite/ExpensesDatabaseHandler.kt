package com.example.pocketbookexpensetracker.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.pocketbookexpensetracker.models.ExpensesModelClass

class ExpensesDatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_VERSION = 3
        private val DATABASE_NAME = "ExpensesDatabase"

        private val TABLE_CONTACTS = "ExpensesTable"


        private val KEY_BUDGET = "budget"
        private val KEY_BALANCE = "balance"
        private val KEY_DATE = "date"
        private val KEY_GROUP_ID = "group_id"
    }



    override fun onCreate(db: SQLiteDatabase?) {
        //creating table with fields
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CONTACTS + "("
                + " INTEGER ," + KEY_BUDGET + " INTEGER,"
                + KEY_BALANCE + " INTEGER," + KEY_DATE + " TEXT,"+ KEY_GROUP_ID + " INTEGER PRIMARY KEY, FOREIGN KEY("+KEY_GROUP_ID+")"
                + "REFERENCES ExpenseTable("+KEY_GROUP_ID+")"+")")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        onCreate(db)
    }
    /**
     * Function to insert data
     */
    fun addExpenses(expenses: ExpensesModelClass): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_BUDGET, expenses.budget) // ExpensesModelClass budget
        contentValues.put(KEY_BALANCE, expenses.balance) // ExpensesModelClass balance
        contentValues.put(KEY_DATE, expenses.date)
        contentValues.put(KEY_GROUP_ID, expenses.groupId)

        // Inserting employee details using insert query.
        val success = db.insert(TABLE_CONTACTS, null, contentValues)
        //2nd argument is String containing nullColumnHack

        db.close() // Closing database connection
        return success
    }
    //Method to read the records from database in form of ArrayList
    fun viewExpenses(): ArrayList<ExpensesModelClass> {

        val expenseList: ArrayList<ExpensesModelClass> = ArrayList<ExpensesModelClass>()

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

        var budget: Int
        var balance: Int
        var date: String
        var group_id: Int

        if (cursor.moveToFirst()) {
            do {
                budget = cursor.getInt(cursor.getColumnIndex(KEY_BUDGET))
                balance = cursor.getInt(cursor.getColumnIndex(KEY_BALANCE))
                date = cursor.getString(cursor.getColumnIndex(KEY_DATE))
                group_id = cursor.getInt(cursor.getColumnIndex(KEY_GROUP_ID))

                val exp = ExpensesModelClass(budget = budget, balance = balance, date = date, groupId = group_id)
                expenseList.add(exp)

            } while (cursor.moveToNext())
        }
        return expenseList
    }
    /**
     * Function to update record
     */
    fun updateExpenses(expenses: ExpensesModelClass): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_BUDGET, expenses.budget)
        contentValues.put(KEY_BALANCE, expenses.balance)


        // Updating Row
        val success = db.update(TABLE_CONTACTS, contentValues, KEY_GROUP_ID + "=" + expenses.groupId, null)
        //2nd argument is String containing nullColumnHack

        // Closing database connection
        db.close()
        return success
    }
    /**
     * Function to delete record
     */
    fun deleteExpenses(expenses: ExpensesModelClass): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_GROUP_ID, expenses.groupId) // EmpModelClass id
        // Deleting Row
        val success = db.delete(TABLE_CONTACTS, KEY_GROUP_ID + "=" + expenses.groupId, null)
        //2nd argument is String containing nullColumnHack

        // Closing database connection
        db.close()
        return success
    }
}