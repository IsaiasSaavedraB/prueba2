package com.example.agencia.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.agencia.session.SessionManager
import com.example.agencia.ui.dashboard.DashboardScreen
import com.example.agencia.ui.login.LoginScreen
import com.example.agencia.ui.oportunidades.OportunidadDetailScreen
import com.example.agencia.ui.oportunidades.OportunidadFormScreen
import com.example.agencia.ui.oportunidades.OportunidadesListScreen
import com.example.agencia.ui.organizaciones.OrganizacionesScreen
import com.example.agencia.ui.seguimientos.SeguimientoScreen
import com.example.agencia.ui.supervisor.SupervisorPanelScreen

object Screen {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val OPORTUNIDADES_LIST = "oportunidades_list"
    const val OPORTUNIDAD_DETAIL = "oportunidad_detail/{id}"
    const val OPORTUNIDAD_FORM = "oportunidad_form?id={id}"
    const val ORGANIZACIONES = "organizaciones"
    const val SEGUIMIENTO = "seguimiento/{oportunidadId}"
    const val SUPERVISOR_PANEL = "supervisor_panel"

    fun crearOportunidadDetailRoute(id: Int) = "oportunidad_detail/$id"
    fun crearOportunidadFormRoute(id: Int = 0) = "oportunidad_form?id=$id"
    fun crearSeguimientoRoute(oportunidadId: Int) = "seguimiento/$oportunidadId"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val usuarioActual by SessionManager.currentUser.collectAsStateWithLifecycle()
    val estaAutenticado = usuarioActual != null
    val esSupervisor = usuarioActual?.esSupervisor == true

    val showTopBarAndBottomNav = (currentRoute != Screen.LOGIN) && estaAutenticado

    Scaffold(
        topBar = {
            if (showTopBarAndBottomNav) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = when {
                                    currentRoute?.startsWith("dashboard") == true -> "Dashboard CRM"
                                    currentRoute?.startsWith("oportunidades_list") == true -> "Oportunidades Comerciales"
                                    currentRoute?.startsWith("oportunidad_detail") == true -> "Detalle de Oportunidad"
                                    currentRoute?.startsWith("oportunidad_form") == true -> "Formulario de Oportunidad"
                                    currentRoute?.startsWith("organizaciones") == true -> "Organizaciones y Contactos"
                                    currentRoute?.startsWith("seguimiento") == true -> "Gestión de Seguimiento"
                                    currentRoute?.startsWith("supervisor_panel") == true -> "Panel de Supervisor"
                                    else -> "CRM Comercial"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            usuarioActual?.let { u ->
                                Text(
                                    text = "${u.nombreCompleto} (${if (u.esSupervisor) "Supervisor" else "Ejecutivo"})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        if (currentRoute != Screen.DASHBOARD && currentRoute != Screen.OPORTUNIDADES_LIST && currentRoute != Screen.ORGANIZACIONES && currentRoute != Screen.SUPERVISOR_PANEL) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                            }
                        }
                    },
                    actions = {
                        Surface(
                            color = if (esSupervisor) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = if (esSupervisor) "SUPERVISOR" else "EJECUTIVO",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (esSupervisor) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        IconButton(onClick = {
                            SessionManager.cerrarSesion()
                            navController.navigate(Screen.LOGIN) {
                                popUpTo(0) { inclusive = true }
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        },
        bottomBar = {
            if (showTopBarAndBottomNav) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == Screen.DASHBOARD,
                        onClick = {
                            navController.navigate(Screen.DASHBOARD) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Inicio") }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Screen.OPORTUNIDADES_LIST,
                        onClick = {
                            navController.navigate(Screen.OPORTUNIDADES_LIST) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.AutoMirrored.Filled.ListAlt, contentDescription = "Oportunidades") },
                        label = { Text("Oportunidades") }
                    )

                    NavigationBarItem(
                        selected = currentRoute == Screen.ORGANIZACIONES,
                        onClick = {
                            navController.navigate(Screen.ORGANIZACIONES) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Business, contentDescription = "Empresas") },
                        label = { Text("Empresas") }
                    )

                    if (esSupervisor) {
                        NavigationBarItem(
                            selected = currentRoute == Screen.SUPERVISOR_PANEL,
                            onClick = {
                                navController.navigate(Screen.SUPERVISOR_PANEL) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(Icons.Default.SupervisorAccount, contentDescription = "Supervisor") },
                            label = { Text("Supervisor") }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = if (estaAutenticado) Screen.DASHBOARD else Screen.LOGIN,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Screen 1: Login
            composable(Screen.LOGIN) {
                LoginScreen(
                    onLoginExitoso = {
                        navController.navigate(Screen.DASHBOARD) {
                            popUpTo(Screen.LOGIN) { inclusive = true }
                        }
                    }
                )
            }

            // Screen 2: Dashboard
            composable(Screen.DASHBOARD) {
                DashboardScreen(
                    onNavegarAOportunidades = { navController.navigate(Screen.OPORTUNIDADES_LIST) },
                    onNavegarACrearOportunidad = { navController.navigate(Screen.crearOportunidadFormRoute(0)) },
                    onNavegarAOrganizaciones = { navController.navigate(Screen.ORGANIZACIONES) },
                    onNavegarAPanelSupervisor = { navController.navigate(Screen.SUPERVISOR_PANEL) },
                    onVerOportunidadDetalle = { id -> navController.navigate(Screen.crearOportunidadDetailRoute(id)) }
                )
            }

            // Screen 3: Oportunidades List
            composable(Screen.OPORTUNIDADES_LIST) {
                OportunidadesListScreen(
                    onVerDetalle = { id -> navController.navigate(Screen.crearOportunidadDetailRoute(id)) },
                    onCrearNueva = { navController.navigate(Screen.crearOportunidadFormRoute(0)) }
                )
            }

            // Screen 4: Oportunidad Detail
            composable(
                route = Screen.OPORTUNIDAD_DETAIL,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: 0
                OportunidadDetailScreen(
                    oportunidadId = id,
                    onEditar = { opId -> navController.navigate(Screen.crearOportunidadFormRoute(opId)) },
                    onRegistrarSeguimiento = { opId -> navController.navigate(Screen.crearSeguimientoRoute(opId)) }
                )
            }

            // Screen 5: Oportunidad Form (Crear / Editar)
            composable(
                route = Screen.OPORTUNIDAD_FORM,
                arguments = listOf(navArgument("id") { type = NavType.IntType; defaultValue = 0 })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: 0
                OportunidadFormScreen(
                    oportunidadId = id,
                    onGuardadoExitoso = { navController.popBackStack() }
                )
            }

            // Screen 6: Organizaciones y Contactos
            composable(Screen.ORGANIZACIONES) {
                OrganizacionesScreen()
            }

            // Screen 7: Seguimientos
            composable(
                route = Screen.SEGUIMIENTO,
                arguments = listOf(navArgument("oportunidadId") { type = NavType.IntType })
            ) { backStackEntry ->
                val opId = backStackEntry.arguments?.getInt("oportunidadId") ?: 0
                SeguimientoScreen(
                    oportunidadId = opId,
                    onGuardadoExitoso = { navController.popBackStack() }
                )
            }

            // Screen 8: Supervisor Panel
            composable(Screen.SUPERVISOR_PANEL) {
                SupervisorPanelScreen(
                    onVerOportunidadDetalle = { id -> navController.navigate(Screen.crearOportunidadDetailRoute(id)) }
                )
            }
        }
    }
}