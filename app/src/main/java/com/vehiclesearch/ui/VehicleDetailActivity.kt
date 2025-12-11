package com.vehiclesearch.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.vehiclesearch.databinding.ActivityVehicleDetailBinding
import com.vehiclesearch.utils.DistanceCalculator
import java.text.NumberFormat

class VehicleDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVehicleDetailBinding
    private val viewModel: VehicleDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVehicleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val vehicleId = intent.getLongExtra("VEHICLE_ID", -1)
        if (vehicleId != -1L) {
            viewModel.loadVehicle(vehicleId)
        }

        setupObservers()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun setupObservers() {
        viewModel.vehicle.observe(this) { vehicle ->
            vehicle?.let {
                binding.apply {
                    titleText.text = it.title
                    priceText.text = it.price
                    yearText.text = it.year.toString()

                    val numberFormat = NumberFormat.getNumberInstance()
                    mileageText.text = "${numberFormat.format(it.mileage)} miles"

                    fuelTypeText.text = it.fuelType
                    conditionText.text = it.condition
                    distanceText.text = "${DistanceCalculator.formatDistance(it.distance)} away"

                    dealerNameText.text = it.dealerName
                    dealerAddressText.text = it.dealerAddress
                    dealerPhoneText.text = it.dealerPhone

                    callDealerButton.setOnClickListener { _ ->
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${it.dealerPhone}")
                        }
                        startActivity(intent)
                    }

                    viewListingButton.setOnClickListener { _ ->
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(it.listingUrl)
                        }
                        startActivity(intent)
                    }
                }
            }
        }
    }
}
