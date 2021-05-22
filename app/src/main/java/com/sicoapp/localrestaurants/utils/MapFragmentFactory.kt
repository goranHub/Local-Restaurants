package com.sicoapp.localrestaurants.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.sicoapp.localrestaurants.data.remote.Restraurant
import com.sicoapp.localrestaurants.domain.Repository
import com.sicoapp.localrestaurants.ui.add.AddNewRestaurantDialog
import com.sicoapp.localrestaurants.ui.all.BottomSheetAdapter
import com.sicoapp.localrestaurants.ui.all.BottomSheetDialog
import com.sicoapp.localrestaurants.ui.details.DetailsFragment
import com.sicoapp.localrestaurants.ui.map.DialogWithData
import com.sicoapp.localrestaurants.ui.map.MapFragmentHome
import com.sicoapp.localrestaurants.ui.map.MapViewModel
import java.io.File
import javax.inject.Inject

class MapFragmentFactory @Inject constructor(
    val dialogWithRestaurantData: DialogWithData,
    val addRestaurantDialog: AddNewRestaurantDialog,
    val bottomSheetDialog: BottomSheetDialog,
    val repository: Repository
): FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className) {

            MapFragmentHome::class.java.name -> MapFragmentHome(
                dialogWithRestaurantData,
                addRestaurantDialog,
                bottomSheetDialog,
                repository
            )

            else -> super.instantiate(classLoader, className)
        }
    }
}