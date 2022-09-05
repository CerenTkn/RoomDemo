package com.cerentekin.roomdemo

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cerentekin.roomdemo.databinding.ActivityMainBinding
import com.cerentekin.roomdemo.databinding.DialogUpdateBinding
import kotlinx.android.synthetic.main.dialog_update.*
import kotlinx.android.synthetic.main.items_row.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val employeeDao = (application as EmployeeApp).db.employeeDao()
        binding.btnAdd.setOnClickListener {
            addRecord(employeeDao)
        }
        lifecycleScope.launch {
            employeeDao.fetchAllEmployees().collect{
                val list = ArrayList(it)
                setupListOfDataIntoRecyclerView(list,employeeDao)
            }
        }
    }

    //Todo 1 create an employeeDao param to access the insert method
    //launch a coroutine block to call the method for inserting entry
    private fun addRecord(employeeDao: EmployeeDao) {
        val name = binding.etName.text.toString()
        val email = binding.etEmailId.text.toString()
        if (name.isNotEmpty() && email.isNotEmpty()) {
            lifecycleScope.launch {
                employeeDao.insert(EmployeeEntity(name=name, email=email))
                Toast.makeText(applicationContext, "Record saved",Toast.LENGTH_LONG).show()
                binding.etName.text.clear()
                binding.etEmailId.text.clear()
            }
            
        } else {
            Toast.makeText(
                applicationContext,
                "Name or Email cannot be blank",
                Toast.LENGTH_LONG).show()
        }
    }
    private fun setupListOfDataIntoRecyclerView(employeesList:ArrayList<EmployeeEntity>, employeeDao: EmployeeDao){
         if (employeesList.isNotEmpty()){
             val itemAdapter = ItemAdapter(employeesList,
                 {
                     updateId ->
                     updateRecordDialog(updateId, employeeDao)
                 },
                 {
                     deleteId ->
                     deleteRecordAlertDialog(deleteId, employeeDao, employeeEntity = EmployeeEntity( ))
                 }
             )
             // Set the LayoutManager that this RecyclerView will use.
             binding.rvItemList.layoutManager = LinearLayoutManager(this)
             // adapter instance is set to the recyclerview to inflate the items.
             binding.rvItemList.adapter = itemAdapter
             binding.rvItemList.visibility = View.VISIBLE
             binding.tvNoRecordsAvailable.visibility = View.GONE


         }else{
             binding.rvItemList.visibility = View.GONE
             binding.tvNoRecordsAvailable.visibility = View.VISIBLE
         }
    }
    private fun updateRecordDialog(id: Int, employeeDao: EmployeeDao){
        val updateDialog = Dialog(this, R.style.Theme_Dialog)
        updateDialog.setCancelable(false)
        val binding = DialogUpdateBinding.inflate(layoutInflater)
        updateDialog.setContentView(binding.root)

        lifecycleScope.launch {
            employeeDao.fetchEmployeeById(id).collect{
                binding.etUpdateName.setText(it.name)
                binding.etUpdateEmailId.setText(it.email)
            }
        }
        binding.tvUpdate.setOnClickListener {
            val name = binding.etUpdateName.text.toString()
            val email = binding.etUpdateEmailId.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty()){
                lifecycleScope.launch {
                    employeeDao.update(EmployeeEntity(id, name, email) )
                    Toast.makeText(applicationContext, "Record Updated.", Toast.LENGTH_LONG).show()
                    updateDialog.dismiss()
                }

            }else{
                Toast.makeText(applicationContext, "Name or email cannot be blank", Toast.LENGTH_LONG).show()

            }
        }
        binding.tvCancel.setOnClickListener {
            updateDialog.dismiss()
        }
        updateDialog.show()

    }
    private fun deleteRecordAlertDialog(id: Int, employeeDao: EmployeeDao, employeeEntity: EmployeeEntity){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Record")
        builder.setIcon(android.R.drawable.ic_dialog_alert )

        lifecycleScope.launch {
            employeeDao.fetchEmployeeById(id).collect{
                if (it != null){
                    builder.setMessage("Are you sure you want to delete ${it.name}.")
                }
            }
        }


        builder.setPositiveButton("Yes"){dialogInterface, _ ->
            lifecycleScope.launch {
                employeeDao.delete(EmployeeEntity(id))
                Toast.makeText(applicationContext,"Record deleted successfully", Toast.LENGTH_LONG).show()
            }
            dialogInterface.dismiss()
        }
        builder.setNegativeButton("No"){dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

    }

}