package com.vehiclesearch.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.vehiclesearch.data.Vehicle
import com.vehiclesearch.databinding.ActivitySearchResultsBinding

class SearchResultsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchResultsBinding
    private val viewModel: SearchResultsViewModel by viewModels()
    private lateinit var adapter: VehicleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupRecyclerView()
        setupObservers()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun setupRecyclerView() {
        adapter = VehicleAdapter { vehicle ->
            openVehicleDetail(vehicle)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchResultsActivity)
            adapter = this@SearchResultsActivity.adapter
        }
    }

    private fun setupObservers() {
        viewModel.vehicles.observe(this) { vehicles ->
            if (vehicles.isEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.emptyView.visibility = View.VISIBLE
                binding.resultCountText.text = "No results found"
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.emptyView.visibility = View.GONE
                binding.resultCountText.text = "Found ${vehicles.size} vehicles"
                adapter.submitList(vehicles)
            }
        }
    }

    private fun openVehicleDetail(vehicle: Vehicle) {
        val intent = Intent(this, VehicleDetailActivity::class.java).apply {
            putExtra("VEHICLE_ID", vehicle.id)
        }
        startActivity(intent)
    }
}
