package com.example.cs499_app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase
import android.view.LayoutInflater
import android.widget.Toast
import com.example.cs499_app.DatabaseRepository
import com.example.cs499_app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), BottomSheetTargetWeight.TargetWeightListener {
    val database = FirebaseDatabase.getInstance()
    private lateinit var binding: ActivityMainBinding
    private var databaseRepository = DatabaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        databaseRepository = DatabaseRepository()

        // Click listener for target weight goal
        binding.editGoal.setOnClickListener {
            showTargetWeightBottomSheet()
        }
        // Observe Target Weight
        setupTargetWeightObserver()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    // == Reveal Bottom Sheet for Target Weight Using Tag 'BottomSheetTargetWeight' Used in BottomSheetTargetWeight.kt file == //
    private fun showTargetWeightBottomSheet() {
        BottomSheetTargetWeight().show(supportFragmentManager, "BottomSheetTargetWeight")
    }

    // == Method for Target Weight Observer == //
    private fun setupTargetWeightObserver() {
        databaseRepository.observeTargetWeight(
            onUpdate = { targetWeight ->
                targetWeight?.let {
                    // Show target weight up to one decimal point
                    binding.targetWeightInput.text = getString(R.string.weight_format, it)
                } ?: run {
                    binding.targetWeightInput.setText(R.string.blank_target_weight)
                }
            },
            onError = { errorMessage ->
                Toast.makeText(this, "Error observing target weight: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        )
    }

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

    override fun onDestroy() {
        super.onDestroy()
        databaseRepository.removeTargetWeightListener()
    }
}

