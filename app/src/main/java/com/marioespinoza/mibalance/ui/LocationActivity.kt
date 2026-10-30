package com.marioespinoza.mibalance.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.marioespinoza.mibalance.R
import com.marioespinoza.mibalance.databinding.ActivityLocationBinding
import com.marioespinoza.mibalance.viewmodel.LocationViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.io.File

class LocationActivity : BaseActivity(), MapEventsReceiver {
    private lateinit var binding: ActivityLocationBinding
    private lateinit var model: LocationViewModel
    private lateinit var marker: Marker
    private var readOnly = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().apply {
            userAgentValue = "MiBalance/1.0 (com.marioespinoza.mibalance; https://github.com/MarioEspinoza16/TareasAppsMoviles)"
            osmdroidBasePath = File(cacheDir, "maps").apply { mkdirs() }
            osmdroidTileCache = File(osmdroidBasePath, "tiles").apply { mkdirs() }
        }
        binding = ActivityLocationBinding.inflate(layoutInflater); setContentView(binding.root); setup(binding.root, binding.toolbar)
        model = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application))[LocationViewModel::class.java]
        readOnly = intent.getBooleanExtra("read_only", false)
        if (model.latitude == null) {
            if (savedInstanceState?.containsKey("latitude") == true) {
                model.latitude = savedInstanceState.getDouble("latitude"); model.longitude = savedInstanceState.getDouble("longitude")
            } else if (intent.hasExtra("latitude")) {
                model.latitude = intent.getDoubleExtra("latitude", 0.0); model.longitude = intent.getDoubleExtra("longitude", 0.0)
            }
        }
        binding.map.apply {
            setTileSource(TileSourceFactory.MAPNIK); setMultiTouchControls(true)
            minZoomLevel = 3.0; maxZoomLevel = 19.0
            controller.setZoom(14.0)
            controller.setCenter(GeoPoint(model.latitude ?: 9.9763, model.longitude ?: -84.8384))
            overlays.add(MapEventsOverlay(this@LocationActivity))
        }
        marker = Marker(binding.map).apply { setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM); title = getString(R.string.location_title) }
        model.latitude?.let { setPoint(it, model.longitude!!) }
        binding.apply.setOnClickListener { readCoordinates() }
        binding.useLocation.setOnClickListener {
            if (readCoordinates()) { setResult(RESULT_OK, Intent().putExtra("latitude", model.latitude!!).putExtra("longitude", model.longitude!!)); finish() }
        }
        binding.attribution.setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.openstreetmap.org/copyright"))) }
        if (readOnly) {
            binding.latitude.isEnabled = false; binding.longitude.isEnabled = false
            binding.apply.visibility = View.GONE; binding.useLocation.visibility = View.GONE; binding.instructions.visibility = View.GONE
        }
    }
    private fun setPoint(latitude: Double, longitude: Double) {
        model.latitude = latitude; model.longitude = longitude
        binding.latitude.setText(String.format(java.util.Locale.US, "%.6f", latitude))
        binding.longitude.setText(String.format(java.util.Locale.US, "%.6f", longitude))
        marker.position = GeoPoint(latitude, longitude)
        if (!binding.map.overlays.contains(marker)) binding.map.overlays.add(marker)
        binding.map.controller.animateTo(marker.position); binding.map.invalidate()
    }
    private fun readCoordinates(): Boolean {
        val lat = binding.latitude.text.toString().trim().replace(',', '.').toDoubleOrNull()
        val lon = binding.longitude.text.toString().trim().replace(',', '.').toDoubleOrNull()
        if (lat == null || lon == null || !lat.isFinite() || !lon.isFinite() || lat !in -85.0..85.0 || lon !in -180.0..180.0) {
            toast(R.string.error_coordinates); return false
        }
        setPoint(lat, lon); return true
    }
    override fun singleTapConfirmedHelper(point: GeoPoint): Boolean {
        if (readOnly) return false
        if (point.latitude in -85.0..85.0) setPoint(point.latitude, ((point.longitude + 180) % 360 + 360) % 360 - 180)
        return true
    }
    override fun longPressHelper(point: GeoPoint): Boolean = singleTapConfirmedHelper(point)
    override fun onResume() { super.onResume(); binding.map.onResume() }
    override fun onPause() { binding.map.onPause(); super.onPause() }
    override fun onDestroy() { binding.map.onDetach(); super.onDestroy() }
    override fun onSaveInstanceState(outState: Bundle) {
        model.latitude?.let { outState.putDouble("latitude", it); outState.putDouble("longitude", model.longitude!!) }
        super.onSaveInstanceState(outState)
    }
}
