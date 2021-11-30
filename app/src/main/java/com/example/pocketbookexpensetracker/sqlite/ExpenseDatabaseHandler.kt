package com.example.pocketbookexpensetracker.sqlite


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.pocketbookexpensetracker.models.ExpenseModelClass

//creating the database logic, extending the SQLiteOpenHelper base class
class ExpenseDatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_VERSION = 11
        private val DATABASE_NAME = "ExpenseDatabase"

        private val TABLE_CONTACTS = "ExpenseTable"

        private val KEY_ID = "_id"
        private val KEY_NAME = "name"
        private val KEY_EXPENSE_AMOUNT = "amount"
        private val KEY_GROUP_ID = "group_id"
    }



    override fun onCreate(db: SQLiteDatabase?) {
        //creating table with fields
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EXPENSE_AMOUNT + " INTEGER," + KEY_GROUP_ID + " INTEGER" + ")")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        onCreate(db)
    }
    /**
     * Function to insert data
     */
    fun addExpense(expense: ExpenseModelClass): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, expense.name) // EmpModelClass Name
        contentValues.put(KEY_EXPENSE_AMOUNT, expense.amount) // EmpModelClass Email
        contentValues.put(KEY_GROUP_ID, expense.groupId)
        // Inserting employee details using insert query.
        val success = db.insert(TABLE_CONTACTS, null, contentValues)
        //2nd argument is String containing nullColumnHack

        db.close() // Closing database connection
        return success
    }
    //Method to read the records from database in form of ArrayList
    fun viewExpense(currentIndex: Int): ArrayList<ExpenseModelClass> {

        val expenseList: ArrayList<ExpenseModelClass> = ArrayList<ExpenseModelClass>()

        // Query to select all the records from the table.
        val selectQuery = "SELECT  * FROM $TABLE_CONTACTS WHERE $KEY_GROUP_ID == "+currentIndex.toInt()

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
        var groupId: Int

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                name = cursor.getString(cursor.getColumnIndex(KEY_NAME))
                amount = cursor.getInt(cursor.getColumnIndex(KEY_EXPENSE_AMOUNT))
                groupId = cursor.getInt(cursor.getColumnIndex(KEY_GROUP_ID))

                val exp = ExpenseModelClass(id = id, name = name, amount = amount, groupId = groupId)
                expenseList.add(exp)

            } while (cursor.moveToNext())
        }
        return expenseList
    }
    /**
     * Function to update record
     */
    fun updateExpense(expense: ExpenseModelClass): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_NAME, expense.name) // EmpModelClass Name
        contentValues.put(KEY_EXPENSE_AMOUNT, expense.amount) // EmpModelClass Email

        // Updating Row
        val success = db.update(TABLE_CONTACTS, contentValues, KEY_ID + "=" + expense.id, null)
        //2nd argument is String containing nullColumnHack

        // Closing database connection
        db.close()
        return success
    }
    /**
     * Function to delete record
     */
    fun deleteExpense(expense: ExpenseModelClass): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, expense.id) // EmpModelClass id
        // Deleting Row
        val success = db.delete(TABLE_CONTACTS, KEY_ID + "=" + expense.id, null)
        //2nd argument is String containing nullColumnHack

        // Closing database connection
        db.close()
        return success
    }
}