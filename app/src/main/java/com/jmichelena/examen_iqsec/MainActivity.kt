package com.jmichelena.examen_iqsec

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.location.LocationManager
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    val CAMERA_REQUEST_CODE = 200
    lateinit var map: GoogleMap
    lateinit var locationManager: LocationManager

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val notConnected = intent.getBooleanExtra(
                ConnectivityManager
                    .EXTRA_NO_CONNECTIVITY, false
            )
            if (notConnected) {
                val noConnectionView = findViewById<ConstraintLayout>(R.id.alertMainBackground)
                noConnectionView.visibility = View.VISIBLE
            } else {
                val noConnectionView = findViewById<ConstraintLayout>(R.id.alertMainBackground)
                noConnectionView.visibility = View.GONE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Localización y Mapa

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Botón de captura

        val btnPhoto = findViewById<Button>(R.id.btnPhoto)

        btnPhoto.setOnClickListener {
            capturePhoto()
        }
    }


    // Captura de foto

    fun capturePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    // Resultado de la foto capturada en el proceso anterior

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == CAMERA_REQUEST_CODE && data != null){
            findViewById<ImageView>(R.id.photoImage).setImageBitmap(data.extras?.get("data") as Bitmap)
        }
    }

    // Función de ubicación de marcador acorde a la ubicación actual

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val latitude = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)?.latitude
        val longitude = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)?.longitude
        if (latitude != null && longitude != null) {
            val myLocation = LatLng(latitude!!,longitude!!)
            map.addMarker(MarkerOptions().position(myLocation).title("Mi Ubicación"))
            map.setMinZoomPreference(16.0f)
            map.setMaxZoomPreference(1000f)
            map.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
        } else {
            val deniedView = findViewById<ConstraintLayout>(R.id.deniedView)
            deniedView.visibility = View.VISIBLE
        }
    }
}