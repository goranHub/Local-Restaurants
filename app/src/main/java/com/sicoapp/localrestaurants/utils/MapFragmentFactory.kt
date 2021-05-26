package com.sicoapp.localrestaurants.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.sicoapp.localrestaurants.domain.Repository
import com.sicoapp.localrestaurants.ui.map.add.AddNewRestaurantDialog
import com.sicoapp.localrestaurants.ui.map.bottom_sheet.BottomSheetDialog
import com.sicoapp.localrestaurants.ui.map.RestaurantDialog
import com.sicoapp.localrestaurants.ui.map.MapFragmentHome
import javax.inject.Inject

class MapFragmentFactory @Inject constructor(
    val dialogWithRestaurantData: RestaurantDialog,
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