package com.example.cs499_app

import WeightRecord
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cs499_app.DatabaseRepository
import com.example.cs499_app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(),
    BottomSheetTargetWeight.TargetWeightListener,
    BottomSheetAddWeight.AddWeightListener,
    BottomSheetEditWeight.EditWeightListener {

    // val database = FirebaseDatabase.getInstance()

    // View binding for MainActivity to handle UI element
    private lateinit var binding: ActivityMainBinding
    private lateinit var databaseRepository: DatabaseRepository
    private lateinit var weightRecordAdapter: WeightRecordAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Initialize database repository from Firebase
        databaseRepository = DatabaseRepository()

        // Call methods to set up Recucler View, Click Listeners, and Observe Data
        setupRecyclerView()
        setupClickListeners()
        observeData()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // == Method for setting up Recycler View == //
    private fun setupRecyclerView() {
        weightRecordAdapter = WeightRecordAdapter(
            onItemClick = { record ->
                showEditWeightBottomSheet(record)
            }
        )
        // Set up the RecyclerView
        binding.recyclerView.apply {
            adapter = weightRecordAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL))
        }
    }
    // == Setup for Click Listeners == //
    private fun setupClickListeners() {
        // Click listener for FAB to add a new weight record
        binding.addRecordFAB.setOnClickListener {
            showAddWeightBottomSheet()
        }
        // Click listener for target weight goal
        binding.editGoal.setOnClickListener {
            showTargetWeightBottomSheet()
        }
    }

    // Observes database changes within Firebase for the Target Weight and Weight Records
    private fun observeData() {
        // Method for Target Weight Observer
        databaseRepository.observeTargetWeight(
            onUpdate = { targetWeight ->
                targetWeight?.let {
                    // Show target weight up to one decimal point and add the "lbs"
                    binding.targetWeightInput.text = getString(R.string.weight_format, it)
                } ?: run {
                    binding.targetWeightInput.setText(R.string.blank_target_weight)
                }
            },
            // Handle error to let user know
            onError = { errorMessage ->
                Toast.makeText(this, "Error observing target weight: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        )
        // Method for Weight Records Observer to handle changes made to them
        databaseRepository.observeWeightRecords(
            onUpdate = { records ->
                weightRecordAdapter.updateRecords(records)
                binding.recyclerView.visibility = if (records.isEmpty()) View.GONE else View.VISIBLE
            },
            // Handle error for weight record
            onError = { errorMessage ->
                Toast.makeText(this, "Error observing weight: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // == Reveal Bottom Sheet for Target Weight Using Tag 'BottomSheetTargetWeight' Used in BottomSheetTargetWeight.kt file == //
    private fun showTargetWeightBottomSheet() {
        BottomSheetTargetWeight().show(supportFragmentManager, "BottomSheetTargetWeight")
    }
    // == Reveal Bottom Sheet for Adding Weight == //
    private fun showAddWeightBottomSheet() {
        BottomSheetAddWeight().show(supportFragmentManager, BottomSheetAddWeight.TAG)
    }
    // == Reveal Bottom Sheet for Editing Weight == //
    private fun showEditWeightBottomSheet(record: WeightRecord) {
        BottomSheetEditWeight().apply {
            setWeightRecord(record)
            show(supportFragmentManager, BottomSheetEditWeight.TAG)
        }
    }

    // == Checking to see if an entered date already exists in the database to handle duplicates == //
    fun checkDateExists(date: Long, onDuplicateFound:() -> Unit, onDateAvailable:() -> Unit, onError:(String) -> Unit) {
        // Check database using passed date
        databaseRepository.checkDateExists(date = date, onResult = { exists ->
            if (exists) {
                onDuplicateFound()
            }
            else {
                onDateAvailable()
            }
        },
            onError = onError)
    }

    // == Setting weight record via bottom sheet after it's added == //
    override fun onWeightRecordSet(weight: Double, date: Long) {
        // addWeightRecord method from Database repository
        databaseRepository.addWeightRecord(
            weight = weight,
            date = date,
            onSuccess = {
                Toast.makeText(this, "Successfully added weight record!", Toast.LENGTH_SHORT).show()
            },
            onError = { errorMessage ->
                Toast.makeText(this, "Error adding weight record: $errorMessage", Toast.LENGTH_SHORT).show()
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        )
    }

    // == Updating weight record via bottom sheet after it's edited == //
    override fun onWeightRecordUpdate(weightRecord: WeightRecord) {
        // updateWeightRecord called from database repository
        databaseRepository.updateWeightRecord(
            record = weightRecord,
            onSuccess = {
                Toast.makeText(this, "Successfully updated weight record!", Toast.LENGTH_SHORT).show()
            },
            onError = { errorMessage ->
                Toast.makeText(this, "Error updating weight record: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // == Deleting weight record via bottom sheet == //
    override fun onWeightRecordDelete(weightRecord: WeightRecord) {
        databaseRepository.deleteWeightRecord(
            record = weightRecord,
            onSuccess = {
                Toast.makeText(this, "Successfully deleted weight record!", Toast.LENGTH_SHORT).show()
            },
            onError = { errorMessage ->
                Toast.makeText(this, "Error deleting weight record: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // == Setting target weight via database repository == //
    override fun onTargetWeightSet(weight: Double) {
        databaseRepository.updateTargetWeight(
            targetWeight = weight,
            onSuccess = {
                Toast.makeText(this, "Successfully updated Target Weight!", Toast.LENGTH_SHORT).show()
            },
            onError = { errorMessage ->
                Toast.makeText(this, "Error updating target weight: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // == Cleanup and removal of listeners == //
    override fun onDestroy() {
        super.onDestroy()
        databaseRepository.removeTargetWeightListener()
        databaseRepository.removeWeightRecordListener()
    }
}

