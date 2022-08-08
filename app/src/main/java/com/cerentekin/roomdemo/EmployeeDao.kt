package com.cerentekin.roomdemo

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import androidx.constraintlayout.helper.widget.Flow as Flow1


@Dao
interface EmployeeDao {
//If you want to store the data, we need to use  that insert key word.
    @Insert
    //suspend fun function using the code routine class because this
    // is an operation that will take quite some time OR relatively
    //speaking will take some time and this must not

    suspend fun insert (employeeEntity: EmployeeEntity)

    // Todo 3: create a suspend update function for updating an existing entr
    @Update
    suspend fun update (employeeEntity: EmployeeEntity)

    // Todo 4: create a suspend delete function for deleting an existing entry
    @Delete
    suspend fun delete (employeeEntity: EmployeeEntity)

    // Todo 5: create a function to read all employee, this returns a Flow
    @Query("SELECT * FROM `employee-table`")
    fun fetchAllEmployees(): Flow<List<EmployeeEntity>>

    // Todo 5: create a function to read one employee, this returns a Flow
    @Query("SELECT * FROM `employee-table` where id=:id")
    fun fetchEmployeeById(id: Int): Flow<EmployeeEntity>
}