package map.naver.plugin.net.note11.naver_map_plugin

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Looper
import com.google.android.gms.location.*
import com.naver.maps.map.LocationSource
import com.naver.maps.map.LocationSource.OnLocationChangedListener


class LocationSourceImpl constructor(
    private val activity: Activity,

    ) : LocationCallback(), LocationSource {
    val permissions = arrayOf(
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.ACCESS_COARSE_LOCATION"
    )
    private var isActivated = false
    private var locationChangeListener: OnLocationChangedListener? = null

    private val fusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(activity)


    override fun activate(listener: OnLocationChangedListener) {
        locationChangeListener = listener

        val settingsRequest = buildLocationSettingsRequest()


        val settingsClient = LocationServices.getSettingsClient(activity)
        settingsClient
            .checkLocationSettings(settingsRequest)
            .addOnSuccessListener { locationSettingsResponse: LocationSettingsResponse? ->
                requestPositionUpdates()
            }
            .addOnFailureListener { e: Exception ->
                e.printStackTrace()
            }
    }

    @SuppressLint("MissingPermission")
    private fun requestPositionUpdates() {
        val locationRequest: LocationRequest = buildLocationRequest()

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, this, Looper.getMainLooper()
        )
    }

    override fun onLocationResult(locationResult: LocationResult) {
        super.onLocationResult(locationResult)
        locationChangeListener?.onLocationChanged(locationResult.lastLocation)
    }


    private fun buildLocationRequest(): LocationRequest {
        val locationRequestBuilder = LocationRequest.Builder(600_000L)
            .setPriority(100)
            .setIntervalMillis(1_000L)

        return locationRequestBuilder.build()
    }


    private fun buildLocationSettingsRequest(): LocationSettingsRequest {
        val locationSettingsRequestBuilder = LocationSettingsRequest.Builder()
            .addLocationRequest(buildLocationRequest())

        return locationSettingsRequestBuilder.build()
    }


    override fun deactivate() {
        locationChangeListener = null
    }
}