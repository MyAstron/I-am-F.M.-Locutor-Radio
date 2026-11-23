package locutor.cristopher.sic

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import locutor.cristopher.sic.ui.RadioHistoryScreen
import locutor.cristopher.sic.ui.RadioMainScreen
import locutor.cristopher.sic.ui.RadioPhrasesCrudScreen
import locutor.cristopher.sic.ui.RadioServiceScreen
import locutor.cristopher.sic.ui.RadioSettingsScreen
import locutor.cristopher.sic.ui.RadioViewModel
import locutor.cristopher.sic.ui.components.ServiceSelectorDialog
import locutor.cristopher.sic.ui.theme.CockpitOnPrimary
import locutor.cristopher.sic.ui.theme.CockpitOnSurface
import locutor.cristopher.sic.ui.theme.CockpitOnSurfaceVariant
import locutor.cristopher.sic.ui.theme.CockpitPrimary
import locutor.cristopher.sic.ui.theme.CockpitPrimaryContainer
import locutor.cristopher.sic.ui.theme.CockpitSurface
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainerHighest
import locutor.cristopher.sic.ui.theme.CockpitSurfaceContainerLow
import locutor.cristopher.sic.ui.theme.IAmFmTheme

class MainActivity : ComponentActivity() {

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Permiso gestionado
    }

    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        // Permiso de ubicación opcional para clima
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            IAmFmTheme {
                MainAppScaffold(
                    onRequestLocationPermission = {
                        requestLocationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                )
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Cabina : Screen("cabina", "Cabina", Icons.Default.Radio)
    object Frases : Screen("frases", "Frases", Icons.Default.FormatQuote)
    object Ajustes : Screen("ajustes", "Ajustes", Icons.Default.Tune)
    object Historial : Screen("historial", "Historial", Icons.Default.History)
    object Servicio : Screen("servicio", "Servicio", Icons.Default.Bolt)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(
    viewModel: RadioViewModel = viewModel(),
    onRequestLocationPermission: () -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Cabina.route

    val screens = listOf(
        Screen.Cabina,
        Screen.Frases,
        Screen.Ajustes,
        Screen.Historial,
        Screen.Servicio
    )

    val currentScreen = screens.find { it.route == currentRoute } ?: Screen.Cabina

    val isServiceSelectorOpen by viewModel.isServiceSelectorOpen.collectAsState()
    val availableServices = viewModel.availableServices
    val selectedService by viewModel.selectedService.collectAsState()

    if (isServiceSelectorOpen) {
        ServiceSelectorDialog(
            availableServices = availableServices,
            selectedPackage = selectedService,
            onSelectService = { pkg ->
                viewModel.selectMusicService(pkg)
            },
            onLaunchApp = {
                viewModel.launchMusicApp()
            },
            onDismiss = { viewModel.closeServiceSelector() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Ícono oficial de la app desde stitch/icon.png
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CockpitPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.app_icon),
                                contentDescription = "Logo I AM-FM",
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "I AM-FM",
                                style = MaterialTheme.typography.labelSmall,
                                color = CockpitPrimary,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = currentScreen.title,
                                style = MaterialTheme.typography.headlineSmall,
                                color = CockpitOnSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        // 1. Chip botón compacto "Vincular" en Barra Superior / Cabina
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(CockpitPrimaryContainer.copy(alpha = 0.2f))
                                .clickable { viewModel.openServiceSelector() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Link,
                                    contentDescription = "Vincular app de música",
                                    tint = CockpitPrimaryContainer,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Vincular",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CockpitPrimaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Badge 2do plano activo
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(CockpitSurfaceContainerHighest)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(CockpitPrimaryContainer)
                                )
                                Text(
                                    text = "2° PLANO",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CockpitOnSurfaceVariant,
                                    fontSize = 10.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.Sensors,
                                    contentDescription = null,
                                    tint = CockpitPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        // Icono de perfil de cabina
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(CockpitPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Perfil",
                                tint = CockpitOnPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CockpitSurfaceContainerLow
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = CockpitSurfaceContainerLow,
                contentColor = CockpitOnSurfaceVariant
            ) {
                screens.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                text = screen.title.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.sp
                            )
                        },
                        selected = isSelected,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CockpitPrimaryContainer,
                            selectedTextColor = CockpitPrimaryContainer,
                            unselectedIconColor = CockpitOnSurfaceVariant,
                            unselectedTextColor = CockpitOnSurfaceVariant,
                            indicatorColor = CockpitPrimaryContainer.copy(alpha = 0.15f)
                        ),
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = CockpitSurface
        ) {
            NavHost(
                navController = navController,
                startDestination = Screen.Cabina.route
            ) {
                composable(Screen.Cabina.route) {
                    RadioMainScreen(viewModel = viewModel)
                }
                composable(Screen.Frases.route) {
                    RadioPhrasesCrudScreen(viewModel = viewModel)
                }
                composable(Screen.Ajustes.route) {
                    RadioSettingsScreen(viewModel = viewModel)
                }
                composable(Screen.Historial.route) {
                    RadioHistoryScreen(viewModel = viewModel)
                }
                composable(Screen.Servicio.route) {
                    RadioServiceScreen(viewModel = viewModel)
                }
            }
        }
    }
}
