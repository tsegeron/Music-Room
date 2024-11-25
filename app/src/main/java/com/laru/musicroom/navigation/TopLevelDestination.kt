package com.laru.musicroom.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import kotlin.reflect.KClass


enum class TopLevelDestinations(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val iconTextId: Int,
    @StringRes val titleTextId: Int,
    val route: KClass<*>
) {

}
