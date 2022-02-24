package com.jmichelena.examen_iqsec

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class SplashActivity: AppCompatActivity() {

    // Creación del BroadcastReceiver

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val notConnected = intent.getBooleanExtra(
                ConnectivityManager
                    .EXTRA_NO_CONNECTIVITY, false
            )
            if (notConnected) {
                disconnected()
            } else {
                connected()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


    }

    // Función de checkeo de permisos con la librería Dexter

    fun permissionGranted(){
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (!report!!.areAllPermissionsGranted() || report.isAnyPermissionPermanentlyDenied) {

                        val alertBuilder = AlertDialog.Builder(this@SplashActivity)
                        alertBuilder.setTitle("Permisos Denegados")
                        alertBuilder.setMessage("Es necesario habilitar todos los permisos requeridos para utilizar la aplicación.")
                        alertBuilder.setNegativeButton("ACEPTAR") { dialog, which ->
                            finish()
                        }
                        alertBuilder.show()
                    }
                    else {
                        val intent = Intent(this@SplashActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).check()
    }

    // Registra el receptor de conexión

    override fun onStart() {
        super.onStart()
        registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    // Remueve el receptor de conexión

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    // Función que procede si se encuentra conectado

    private fun connected() {
        val alertMainBackground = findViewById<ConstraintLayout>(R.id.alertMainBackground)
        alertMainBackground.visibility = View.GONE
        permissionGranted()
    }

    // Función que muestra el fondo de conectividad si no se encuentra conectado

    private fun disconnected() {
        val alertMainBackground = findViewById<ConstraintLayout>(R.id.alertMainBackground)
        alertMainBackground.visibility = View.VISIBLE
    }
}