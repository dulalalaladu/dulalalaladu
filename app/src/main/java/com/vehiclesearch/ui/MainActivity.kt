package com.vehiclesearch.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.vehiclesearch.R
import com.vehiclesearch.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            binding.notificationSwitch.isChecked = false
            Toast.makeText(
                this,
                getString(R.string.notification_permission_required),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSpinners()
        setupObservers()
        setupListeners()
    }

    private fun setupSpinners() {
        // Mileage options
        val mileageOptions = listOf(
            "Any",
            "10,000",
            "25,000",
            "50,000",
            "75,000",
            "100,000",
            "150,000",
            "200,000"
        )
        (binding.mileageSpinner as? MaterialAutoCompleteTextView)?.apply {
            setAdapter(ArrayAdapter(this@MainActivity, android.R.layout.simple_dropdown_item_1line, mileageOptions))
            setText(mileageOptions[0], false)
        }

        // Year options
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        val yearOptions = mutableListOf("Any")
        for (year in currentYear downTo 2000) {
            yearOptions.add(year.toString())
        }
        (binding.yearSpinner as? MaterialAutoCompleteTextView)?.apply {
            setAdapter(ArrayAdapter(this@MainActivity, android.R.layout.simple_dropdown_item_1line, yearOptions))
            setText(yearOptions[0], false)
        }

        // Fuel type options
        val fuelTypeOptions = listOf(
            getString(R.string.any),
            getString(R.string.gas),
            getString(R.string.hybrid),
            getString(R.string.electric),
            getString(R.string.diesel)
        )
        (binding.fuelTypeSpinner as? MaterialAutoCompleteTextView)?.apply {
            setAdapter(ArrayAdapter(this@MainActivity, android.R.layout.simple_dropdown_item_1line, fuelTypeOptions))
            setText(fuelTypeOptions[0], false)
        }

        // Condition options
        val conditionOptions = listOf(
            getString(R.string.any),
            getString(R.string.new_condition),
            getString(R.string.used_condition),
            getString(R.string.certified_condition)
        )
        (binding.conditionSpinner as? MaterialAutoCompleteTextView)?.apply {
            setAdapter(ArrayAdapter(this@MainActivity, android.R.layout.simple_dropdown_item_1line, conditionOptions))
            setText(conditionOptions[0], false)
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.searchButton.isEnabled = !isLoading
            binding.searchButton.text = if (isLoading) {
                getString(R.string.searching)
            } else {
                getString(R.string.search)
            }
        }

        viewModel.searchResults.observe(this) { vehicles ->
            if (vehicles.isNotEmpty()) {
                val intent = Intent(this, SearchResultsActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.no_results), Toast.LENGTH_LONG).show()
            }
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    private fun setupListeners() {
        binding.searchButton.setOnClickListener {
            performSearch()
        }

        binding.notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                checkNotificationPermission()
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission already granted
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    showPermissionRationale()
                }
                else -> {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun showPermissionRationale() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.permission_required))
            .setMessage(getString(R.string.notification_permission_required))
            .setPositiveButton(getString(R.string.grant_permission)) { _, _ ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                binding.notificationSwitch.isChecked = false
            }
            .show()
    }

    private fun performSearch() {
        val makeModel = binding.makeModelEditText.text.toString().trim()
        val address = binding.addressEditText.text.toString().trim()

        if (makeModel.isEmpty()) {
            binding.makeModelEditText.error = "Please enter a vehicle make/model"
            return
        }

        if (address.isEmpty()) {
            binding.addressEditText.error = "Please enter an address"
            return
        }

        val mileageText = binding.mileageSpinner.text.toString()
        val maxMileage = if (mileageText == "Any") null else mileageText.replace(",", "").toIntOrNull()

        val yearText = binding.yearSpinner.text.toString()
        val minYear = if (yearText == "Any") null else yearText.toIntOrNull()

        val fuelType = binding.fuelTypeSpinner.text.toString().let {
            if (it == getString(R.string.any)) null else it
        }

        val condition = binding.conditionSpinner.text.toString().let {
            if (it == getString(R.string.any)) null else it
        }

        val enableNotifications = binding.notificationSwitch.isChecked

        viewModel.searchVehicles(
            makeModel = makeModel,
            address = address,
            radiusMiles = 50,
            maxMileage = maxMileage,
            minYear = minYear,
            maxYear = null,
            fuelType = fuelType,
            condition = condition,
            enableNotifications = enableNotifications
        )
    }
}
