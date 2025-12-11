package com.vehiclesearch.ui

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vehiclesearch.data.Vehicle
import com.vehiclesearch.databinding.ItemVehicleBinding
import com.vehiclesearch.utils.DistanceCalculator
import java.text.NumberFormat

class VehicleAdapter(
    private val onVehicleClick: (Vehicle) -> Unit
) : ListAdapter<Vehicle, VehicleAdapter.VehicleViewHolder>(VehicleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val binding = ItemVehicleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VehicleViewHolder(binding, onVehicleClick)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class VehicleViewHolder(
        private val binding: ItemVehicleBinding,
        private val onVehicleClick: (Vehicle) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(vehicle: Vehicle) {
            binding.apply {
                titleText.text = vehicle.title
                priceText.text = vehicle.price
                distanceText.text = DistanceCalculator.formatDistance(vehicle.distance)

                val numberFormat = NumberFormat.getNumberInstance()
                mileageText.text = "${numberFormat.format(vehicle.mileage)} miles"

                fuelTypeText.text = vehicle.fuelType
                dealerText.text = vehicle.dealerName

                callButton.setOnClickListener {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${vehicle.dealerPhone}")
                    }
                    it.context.startActivity(intent)
                }

                detailsButton.setOnClickListener {
                    onVehicleClick(vehicle)
                }

                root.setOnClickListener {
                    onVehicleClick(vehicle)
                }
            }
        }
    }

    class VehicleDiffCallback : DiffUtil.ItemCallback<Vehicle>() {
        override fun areItemsTheSame(oldItem: Vehicle, newItem: Vehicle): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Vehicle, newItem: Vehicle): Boolean {
            return oldItem == newItem
        }
    }
}
