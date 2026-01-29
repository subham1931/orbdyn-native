package com.example.myapplication

import android.content.Context
import android.os.Bundle
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

// --- Color Palette ---
val DarkBackground = Color(0xFF020610)
val SurfaceDark = Color(0xFF0F1528)
val PrimaryBlue = Color(0xFF2E65F3)
val TextWhite = Color(0xFFFFFFFF)
val TextGrey = Color(0xFF9098A8)
val InputBackground = Color(0xFF0B101E)
val BorderColor = Color(0xFF1F2937)
val DangerRed = Color(0xFFEF4444)
val WarningOrange = Color(0xFFF59E0B)

// --- Light Theme Palette ---
val LightBackground = Color(0xFFF9FAFB)
val SurfaceLight = Color(0xFFFFFFFF)
val TextBlack = Color(0xFF111827)
val TextGreyLight = Color(0xFF6B7280)
val InputBackgroundLight = Color(0xFFF3F4F6)
val BorderColorLight = Color(0xFFE5E7EB)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemDark = isSystemInDarkTheme()
            var isDarkTheme by remember { mutableStateOf(systemDark) }

            val darkColorScheme = darkColorScheme(
                background = DarkBackground,
                surface = SurfaceDark,
                primary = PrimaryBlue,
                onBackground = TextWhite,
                onSurface = TextWhite,
                onSurfaceVariant = TextGrey,
                surfaceVariant = InputBackground,
                outline = BorderColor
            )

            val lightColorScheme = lightColorScheme(
                background = LightBackground,
                surface = SurfaceLight,
                primary = PrimaryBlue,
                onBackground = TextBlack,
                onSurface = TextBlack,
                onSurfaceVariant = TextGreyLight,
                surfaceVariant = InputBackgroundLight,
                outline = BorderColorLight
            )

            val colors = if (isDarkTheme) darkColorScheme else lightColorScheme

            MyApplicationTheme {
                MaterialTheme(
                    colorScheme = colors
                ) {
                    // Update Status Bar info if needed, or rely on system
                    AppContent(
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = { isDarkTheme = !isDarkTheme }
                    )
                }
            }
        }
    }
}

// --- Navigation State ---
// --- Session Manager ---
object SessionManager {
    private const val PREF_NAME = "user_session"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"

    fun isLoggedIn(context: Context): Boolean {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return pref.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun setLoggedIn(context: Context, isLoggedIn: Boolean) {
        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        pref.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }
}

enum class Screen {
    Onboarding, SignIn, SignUp, Dashboard, Profile
}

data class Category(val name: String, val color: Color)

@Composable
fun AppContent(isDarkTheme: Boolean, onToggleTheme: () -> Unit) {
    val context = LocalContext.current
    var currentScreen by remember {
        mutableStateOf(if (SessionManager.isLoggedIn(context)) Screen.Dashboard else Screen.Onboarding)
    }
    var dashboardPage by remember { mutableStateOf("All Resources") }
    // Categories
    val categories = remember { mutableStateListOf<Category>().apply { addAll(PersistenceManager.loadCategories(context)) } }
    if (categories.isEmpty()) {
       categories.add(Category("Work", Color(0xFFEF4444)))
       categories.add(Category("Personal", Color(0xFF3B82F6)))
       categories.add(Category("Ideas", Color(0xFFA855F7)))
       PersistenceManager.saveCategories(context, categories)
    }

    // Resources
    val resources = remember { mutableStateListOf<Resource>().apply { addAll(PersistenceManager.loadResources(context)) } }

    // Wrap everything in a Surface to ensure the background follows the theme
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (currentScreen) {
            Screen.Onboarding -> OnboardingScreen(
                onGetStarted = { currentScreen = Screen.SignUp },
                onSignIn = { currentScreen = Screen.SignIn }
            )
            Screen.SignIn -> SignInScreen(
                onBack = { currentScreen = Screen.Onboarding },
                onSignUp = { currentScreen = Screen.SignUp },
                onLoginSuccess = { 
                    SessionManager.setLoggedIn(context, true)
                    currentScreen = Screen.Dashboard 
                }
            )
            Screen.SignUp -> SignUpScreen(
                onBack = { currentScreen = Screen.Onboarding },
                onSignIn = { currentScreen = Screen.SignIn }
            )
            Screen.Dashboard -> DashboardScreen(
                onSignOut = { 
                    SessionManager.setLoggedIn(context, false)
                    currentScreen = Screen.SignIn 
                },
                onProfileClick = { currentScreen = Screen.Profile },
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                currentPage = dashboardPage,
                onPageChanged = { dashboardPage = it },
                categories = categories,
                onAddCategory = { 
                    categories.add(it) 
                    PersistenceManager.saveCategories(context, categories)
                },
                resources = resources,
                onAddResource = { 
                    resources.add(0, it) 
                    PersistenceManager.saveResources(context, resources)
                },
                onResourceUpdated = { 
                    PersistenceManager.saveResources(context, resources)
                }
            )
            Screen.Profile -> ProfileScreen(
                onBack = { currentScreen = Screen.Dashboard },
                onSignOut = { 
                    SessionManager.setLoggedIn(context, false)
                    currentScreen = Screen.SignIn 
                },
                onNavigateToDashboard = { page ->
                    dashboardPage = page
                    currentScreen = Screen.Dashboard
                },
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                onAddCategory = { 
                    categories.add(it)
                    PersistenceManager.saveCategories(context, categories)
                },
                categories = categories
            )
        }
    }
}

// --- Screens: Onboarding (Restored) ---

@Composable
fun OnboardingScreen(onGetStarted: () -> Unit, onSignIn: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OrbdynLogo()
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Orbdyn",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Box(modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0F1528))
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "NEW STANDARD",
                color = PrimaryBlue,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        val titleText = buildAnnotatedString {
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
                append("Your Personal ")
            }
            withStyle(style = SpanStyle(color = PrimaryBlue)) {
                append("Resource\nManager")
            }
        }
        Text(
            text = titleText,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            lineHeight = 44.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "A private sanctuary for your bookmarks, notes, and ideas. Organized, accessible, and always secure.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Features
        FeatureCard(
            icon = Icons.Default.Layers,
            title = "Smart Categories",
            desc = "Automatically organize your bookmarks and notes into intuitive categories."
        )
        Spacer(modifier = Modifier.height(16.dp))
        FeatureCard(
            icon = Icons.Default.Search,
            title = "Powerful Search",
            desc = "Find anything instantly with our advanced search algorithms."
        )
        Spacer(modifier = Modifier.height(16.dp))
        FeatureCard(
            icon = Icons.Default.Security,
            title = "Private & Secure",
            desc = "Your data is encrypted and accessible only by you."
        )

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(text = "Get Started", onClick = onGetStarted, showArrow = true)

        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Already have an account? ", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = "Sign In",
                color = PrimaryBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onSignIn() }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun FeatureCard(icon: ImageVector, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF1F2937), RoundedCornerShape(16.dp))
            .background(Color(0xFF070C18), RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFF0F1C30), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = desc, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium, lineHeight = 20.sp)
        }
    }
}

@Composable
fun SignInScreen(onBack: () -> Unit, onSignUp: () -> Unit, onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .background(SurfaceDark, CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextWhite
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        OrbdynLogo()
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Sign in to access your resources and manage your digital life.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Email
        InputLabel("Email Address")
        AuthInput(
            value = email,
            onValueChange = { email = it },
            hint = "you@example.com",
            icon = Icons.Outlined.Email
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PASSWORD",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Forgot Password?",
                color = PrimaryBlue,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { /* Forgot Password */ }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        AuthInput(
            value = password,
            onValueChange = { password = it },
            hint = "••••••••",
            icon = Icons.Outlined.Lock,
            isPassword = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(text = "Sign In", onClick = onLoginSuccess, showArrow = false)

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Don't have an account? ", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = "Sign up",
                color = PrimaryBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onSignUp() }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SignUpScreen(onBack: () -> Unit, onSignIn: () -> Unit) {
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .background(SurfaceDark, CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextWhite
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        OrbdynLogo()
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        // Display Name
        InputLabel("Display Name")
        AuthInput(
            value = displayName,
            onValueChange = { displayName = it },
            hint = "Your name",
            icon = Icons.Outlined.Person
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email
        InputLabel("Email Address")
        AuthInput(
            value = email,
            onValueChange = { email = it },
            hint = "you@example.com",
            icon = Icons.Outlined.Email
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password
        InputLabel("Password")
        AuthInput(
            value = password,
            onValueChange = { password = it },
            hint = "••••••••",
            icon = Icons.Outlined.Lock,
            isPassword = true
        )

        Spacer(modifier = Modifier.height(32.dp))
        PrimaryButton(text = "Sign Up", onClick = { /* Handle Sign Up */ }, showArrow = false)

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Already have an account? ", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = "Sign In",
                color = PrimaryBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onSignIn() }
            )
        }
    }
}

@Composable
fun AuthInput(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    icon: ImageVector,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(hint, color = Color(0xFF4B5563)) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = TextGrey) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = InputBackground,
            unfocusedContainerColor = InputBackground,
            focusedBorderColor = BorderColor,
            unfocusedBorderColor = BorderColor,
            cursorColor = PrimaryBlue,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite
        )
    )
}

// --- Dashboard & Sidebar & Modal (Preserved) ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onSignOut: () -> Unit,
    onProfileClick: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    currentPage: String,
    onPageChanged: (String) -> Unit,
    categories: List<Category>,
    onAddCategory: (Category) -> Unit,
    resources: MutableList<Resource>,
    onAddResource: (Resource) -> Unit,
    onResourceUpdated: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showAddResourceModal by remember { mutableStateOf(false) }
    var showFilterModal by remember { mutableStateOf(false) }
    var showCreateCategoryModal by remember { mutableStateOf(false) }
    var modalInitialType by remember { mutableStateOf(ResourceType.Link) }

    if (showAddResourceModal) {
        AddResourceModal(
            onDismiss = { showAddResourceModal = false },
            initialType = modalInitialType,
            categories = categories,
            onCreateCategoryClick = { showCreateCategoryModal = true },
            onCreateResource = { 
                onAddResource(it)
                showAddResourceModal = false
            }
        )
    }

    if (showFilterModal) {
        FilterSortModal(onDismiss = { showFilterModal = false })
    }

    if (showCreateCategoryModal) {
        CreateCategoryModal(
            onDismiss = { showCreateCategoryModal = false },
            onCategoryCreated = {
                 onAddCategory(it)
                 showCreateCategoryModal = false
            }
        )
    }

    var selectedResourceForOptions by remember { mutableStateOf<Resource?>(null) }
    var showResourceOptionsModal by remember { mutableStateOf(false) }

    if (showResourceOptionsModal && selectedResourceForOptions != null) {
        val resource = selectedResourceForOptions!!
        ResourceOptionsModal(
            resource = resource,
            onDismiss = { showResourceOptionsModal = false },
            onEdit = { /* TODO: Implement Edit */ showResourceOptionsModal = false },
            onExport = { /* TODO: Implement Export */ showResourceOptionsModal = false },
            onShare = { /* TODO: Implement Share */ showResourceOptionsModal = false },
            onToggleFavorite = {
                val index = resources.indexOfFirst { it.id == resource.id }
                if (index != -1) {
                    resources[index] = resource.copy(isFavorite = !resource.isFavorite)
                    onResourceUpdated()
                }
                showResourceOptionsModal = false
            },
            onArchive = {
                val index = resources.indexOfFirst { it.id == resource.id }
                if (index != -1) {
                    resources[index] = resource.copy(isArchived = !resource.isArchived)
                    onResourceUpdated()
                }
                showResourceOptionsModal = false
            },
            onDelete = {
                val index = resources.indexOfFirst { it.id == resource.id }
                if (index != -1) {
                    // Start by marking as deleted, if already deleted then remove permanently
                    if (resource.isDeleted) {
                        resources.removeAt(index) // Permanent delete
                    } else {
                        resources[index] = resource.copy(isDeleted = true)
                    }
                    onResourceUpdated()
                }
                showResourceOptionsModal = false
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SidebarContent(
                onSignOut = onSignOut,
                selectedItem = currentPage,
                onItemSelected = {
                    onPageChanged(it)
                    scope.launch { drawerState.close() }
                },
                onProfileClick = onProfileClick,
                onCreateCategory = {
                    scope.launch { drawerState.close() }
                    showCreateCategoryModal = true
                },
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                categories = categories
            )
        },
        scrimColor = Color.Black.copy(alpha = 0.5f)
    ) {
        Scaffold(
            topBar = {
                val addButtonAction: (() -> Unit)? = when (currentPage) {
                    "Favorites", "Archive", "Recycle Bin" -> null
                    else -> {
                        {
                            modalInitialType = when (currentPage) {
                                "Notes" -> ResourceType.Note
                                "To Do" -> ResourceType.Todo
                                else -> ResourceType.Link
                            }
                            showAddResourceModal = true
                        }
                    }
                }
                
                DashboardTopBar(
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onAddClick = addButtonAction
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            DashboardContent(
                modifier = Modifier.padding(innerPadding),
                selectedPage = currentPage,
                onAddResourceClick = { type ->
                    modalInitialType = type
                    showAddResourceModal = true
                },
                onFilterClick = { showFilterModal = true },
                resources = resources,
                onResourceOptionsClick = { resource ->
                    selectedResourceForOptions = resource
                    showResourceOptionsModal = true
                }
            )
        }
    }
}

@Composable
fun SidebarContent(
    onSignOut: () -> Unit,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    onProfileClick: () -> Unit,
    onCreateCategory: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    categories: List<Category>
) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.background,
        drawerContentColor = MaterialTheme.colorScheme.onBackground,
        drawerTonalElevation = 0.dp,
        modifier = Modifier.width(300.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OrbdynLogo(size = 32.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Orbdyn",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Spacer(modifier = Modifier.height(32.dp))

            // Dashboard Items
            SidebarItem(Icons.Default.Dashboard, "All Resources", selectedItem == "All Resources") { onItemSelected("All Resources") }
            SidebarItem(Icons.Default.Link, "Links", selectedItem == "Links") { onItemSelected("Links") }
            SidebarItem(Icons.Default.Description, "Notes", selectedItem == "Notes") { onItemSelected("Notes") }
            SidebarItem(Icons.Default.CheckCircle, "To Do", selectedItem == "To Do") { onItemSelected("To Do") }
            SidebarItem(Icons.Default.Favorite, "Favorites", selectedItem == "Favorites") { onItemSelected("Favorites") }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp), color = MaterialTheme.colorScheme.outlineVariant)

            // CATEGORIES
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("CATEGORIES", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                Icon(
                    Icons.Default.Add, 
                    contentDescription = "Add Category", 
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp).clickable { onCreateCategory() }
                )
            }
            
            categories.forEach { category ->
                 SidebarItem(
                     label = category.name, 
                     isSelected = selectedItem == category.name,
                     indicatorColor = category.color
                 ) { onItemSelected(category.name) }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp), color = MaterialTheme.colorScheme.outlineVariant)
            
            SidebarItem(Icons.Default.Inventory2, "Archive", selectedItem == "Archive") { onItemSelected("Archive") }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SidebarItem(
            icon = Icons.Default.DeleteOutline,
            label = "Recycle Bin",
            isSelected = selectedItem == "Recycle Bin",
            iconColor = DangerRed,
            textColor = DangerRed,
            onClick = { onItemSelected("Recycle Bin") }
        )

        Spacer(modifier = Modifier.weight(1f))

        // User Profile Section
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onProfileClick() }
            ) {
                Box(modifier = Modifier.size(36.dp).background(Color(0xFF374151), CircleShape), contentAlignment = Alignment.Center) {
                    Text("GC", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Girish Chandwani", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text("girish@meensou.com", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onToggleTheme() }) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Outlined.DarkMode else Icons.Outlined.WbSunny,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (isDarkTheme) "Dark Mode" else "Light Mode",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onSignOut() }) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = DangerRed)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Sign Out", color = DangerRed)
            }
        }
    }
}

@Composable
fun SidebarItem(
    icon: ImageVector? = null,
    label: String,
    isSelected: Boolean,
    iconColor: Color = Color.Unspecified,
    textColor: Color = Color.Unspecified,
    indicatorColor: Color? = null,
    onClick: () -> Unit
) {
    val finalIconColor = if (iconColor != Color.Unspecified) iconColor else MaterialTheme.colorScheme.onSurfaceVariant
    val finalTextColor = if (textColor != Color.Unspecified) textColor else MaterialTheme.colorScheme.onSurfaceVariant

    NavigationDrawerItem(
        icon = { 
            if (indicatorColor != null) {
                Box(modifier = Modifier.size(12.dp).background(indicatorColor, CircleShape))
            } else if (icon != null) {
                Icon(icon, contentDescription = null, tint = if (isSelected) PrimaryBlue else finalIconColor) 
            }
        },
        label = { Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
        selected = isSelected,
        onClick = onClick,
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.surfaceVariant, // Use semantic color
            selectedTextColor = PrimaryBlue,
            unselectedContainerColor = Color.Transparent,
            unselectedTextColor = finalTextColor
        ),
        modifier = Modifier.padding(horizontal = 12.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopBar(onMenuClick: () -> Unit, onAddClick: (() -> Unit)? = null) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OrbdynLogo(size = 28.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Orbdyn", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            ) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.onBackground)
            }
        },
        actions = {
            if (onAddClick != null) {
                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(36.dp).padding(end = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add", fontSize = 14.sp)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Composable
fun DashboardContent(
    modifier: Modifier = Modifier,
    selectedPage: String,
    onAddResourceClick: (ResourceType) -> Unit,
    onFilterClick: () -> Unit,
    resources: List<Resource>,
    onResourceOptionsClick: (Resource) -> Unit
) {
    // Determine content based on selectedPage
    val (title, emptyText, addButtonText, defaultParams) = when (selectedPage) {
        "Links" -> Quadruple("Links", "No links found", "+ Add Link", ResourceType.Link)
        "Notes" -> Quadruple("Notes", "No notes found", "+ Add Note", ResourceType.Note)
        "To Do" -> Quadruple("To Do", "No tasks found", "+ Add Task", ResourceType.Todo)
        "Favorites" -> Quadruple("Favorites", "No favorites yet", "", ResourceType.Link)
        "Archive" -> Quadruple("All Archived", "Archive is empty", "", ResourceType.Link)
        "Recycle Bin" -> Quadruple("All Deleted", "Recycle Bin is empty", "", ResourceType.Link)
        else -> Quadruple("All Resources", "No resources yet", "+ Add Resource", ResourceType.Link)
    }



    val filteredResources = resources.filter { resource ->
        when (selectedPage) {
            "All Resources" -> !resource.isDeleted && !resource.isArchived
            "Links" -> resource.type == ResourceType.Link && !resource.isDeleted && !resource.isArchived
            "Notes" -> resource.type == ResourceType.Note && !resource.isDeleted && !resource.isArchived
            "To Do" -> resource.type == ResourceType.Todo && !resource.isDeleted && !resource.isArchived
            "Favorites" -> resource.isFavorite && !resource.isDeleted && !resource.isArchived
            "Archive" -> resource.isArchived && !resource.isDeleted
            "Recycle Bin" -> resource.isDeleted
            else -> false // Should not happen based on current navigation logic, but safe fallback
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Search $title...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            trailingIcon = { 
                Icon(
                    Icons.Default.FilterList, 
                    contentDescription = null, 
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.clickable { onFilterClick() } 
                ) 
            },
            modifier = Modifier.fillMaxWidth().clickable { /* TODO: Search */ }, // Make field seemingly interactive for now
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedBorderColor = MaterialTheme.colorScheme.outline,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = PrimaryBlue,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("${filteredResources.size} items", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        
        // Banners for Archive and Recycle Bin
        if (selectedPage == "Recycle Bin") {
            Spacer(modifier = Modifier.height(16.dp))
            InfoBanner(
                text = "Items in the bin are hidden from your collection\nRestored items will return to their original location",
                backgroundColor = Color(0xFF3B1214), // Dark Red
                icon = Icons.Default.Info,
                iconColor = Color(0xFFEF4444)
            )
        }
        if (selectedPage == "Archive") {
            Spacer(modifier = Modifier.height(16.dp))
            InfoBanner(
                text = "Archived resources are hidden from your main view\nUnarchive items to make them visible again in your collection",
                backgroundColor = Color(0xFF331E12), // Dark Orange
                icon = Icons.Default.Inventory2,
                iconColor = Color(0xFFF59E0B)
            )
        }

        if (filteredResources.isEmpty()) {
            Column(modifier = Modifier.fillMaxWidth().weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(80.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape), contentAlignment = Alignment.Center) {
                    val icon = when (selectedPage) {
                        "Recycle Bin" -> Icons.Default.Delete
                        "Archive" -> Icons.Default.Inventory2
                        else -> Icons.Default.FolderOpen
                    }
                    Icon(icon, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(emptyText, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                
                val desc = when (selectedPage) {
                    "Recycle Bin" -> "Items you delete will appear here. You can restore them or delete them permanently."
                    "Archive" -> "Archived resources will appear here. Archive items you want to keep but don't need to see regularly."
                    else -> "Start building your journal by adding links, notes, and professional resources."
                }
                
                Text(desc, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 32.dp))
                Spacer(modifier = Modifier.height(32.dp))
                
                if (addButtonText.isNotEmpty()) {
                    PrimaryButton(text = addButtonText, onClick = { onAddResourceClick(defaultParams) })
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                 filteredResources.forEach { resource ->
                     ResourceCard(
                         resource = resource,
                         onClick = { /* TODO: Open detail */ },
                         onFavoriteClick = { 
                             // Direct toggle from card handled via options modal logic duplication or separate callback?
                             // For simplicity let's rely on Options Modal for now or add direct callback in next iteration if needed.
                             // Actually, let's just trigger options or ignore for now as card action 
                         }, 
                         onMoreClick = { onResourceOptionsClick(resource) }
                     )
                 }
                 Spacer(modifier = Modifier.height(64.dp)) // Bottom padding
            }
        }
    }
}

@Composable
fun InfoBanner(text: String, backgroundColor: Color, icon: ImageVector, iconColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .border(1.dp, iconColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(iconColor.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = Color(0xFFE5E7EB), lineHeight = 18.sp)
    }
}

// Simple data holder for the when expression
// Quadruple removed as it is now in ResourceModels.kt

// --- Add Resource Modal (Preserved) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddResourceModal(onDismiss: () -> Unit, initialType: ResourceType = ResourceType.Link, categories: List<Category>, onCreateCategoryClick: () -> Unit, onCreateResource: (Resource) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.fillMaxHeight(0.95f)
    ) {
        AddResourceContent(onDismiss, initialType, categories, onCreateCategoryClick, onCreateResource)
    }
}

enum class ResourceType { Link, Note, Todo }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddResourceContent(onDismiss: () -> Unit, initialType: ResourceType, categories: List<Category>, onCreateCategoryClick: () -> Unit, onCreateResource: (Resource) -> Unit) {
    var selectedType by remember { mutableStateOf(initialType) }
    
    var url by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var expandedCategory by remember { mutableStateOf(false) }
    
    // Todo Specific State
    var selectedPriority by remember { mutableStateOf("Medium") }
    var isRepeatReminder by remember { mutableStateOf(false) }

    // Simplify slightly for length, assuming user has previous full version or uses this one
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("New Resource", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Capture and organize your vault.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onDismiss, modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape).size(32.dp)) {
                Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Resource Type Selector
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
           ResourceType.values().forEach { type ->
                val isSelected = selectedType == type
                Button(
                    onClick = { selectedType = type },
                    modifier = Modifier.weight(1f).height(42.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (isSelected) TextWhite else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = when(type) {
                            ResourceType.Link -> Icons.Default.Link
                            ResourceType.Note -> Icons.Default.Description
                            ResourceType.Todo -> Icons.Default.Check
                        },
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(type.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

        if (selectedType == ResourceType.Link) {
            InputLabel("URL / Link")
            CustomInput(value = url, onValueChange = { url = it }, icon = Icons.Default.Link, hint = "https://...")
            Spacer(modifier = Modifier.height(24.dp))
        }

        InputLabel("Title")
        CustomInput(value = title, onValueChange = { title = it }, hint = "What is this about?", trailingIcon = Icons.Default.Mic)
        Spacer(modifier = Modifier.height(24.dp))
        
        InputLabel("Description (Optional)")
        CustomInput(value = description, onValueChange = { description = it }, hint = "Brief summary...", singleLine = false, modifier = Modifier.height(80.dp))
        Spacer(modifier = Modifier.height(24.dp))

        if (selectedType == ResourceType.Note) {
            InputLabel("Content / Detail")
            CustomInput(
                value = noteContent, 
                onValueChange = { noteContent = it }, 
                hint = "Write down your thoughts...", 
                singleLine = false, 
                modifier = Modifier.height(150.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
        } else if (selectedType == ResourceType.Todo) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    InputLabel("Due Date")
                    Button(
                        onClick = { /* TODO: Date Picker */ },
                        modifier = Modifier.fillMaxWidth().height(56.dp).border(1.dp, BorderColor, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = InputBackground, contentColor = TextWhite)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Set Date", style = MaterialTheme.typography.bodyMedium)
                            Icon(Icons.Outlined.CalendarToday, null, tint = TextGrey, modifier = Modifier.size(18.dp))
                        }
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    InputLabel("Time")
                     Button(
                        onClick = { /* TODO: Time Picker */ },
                        modifier = Modifier.fillMaxWidth().height(56.dp).border(1.dp, BorderColor, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = InputBackground, contentColor = TextWhite)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("Set Time", style = MaterialTheme.typography.bodyMedium)
                            Icon(Icons.Outlined.Schedule, null, tint = TextGrey, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            
            InputLabel("Priority")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("Low", "Medium", "High").forEach { priority ->
                    val isSelected = selectedPriority == priority
                    val color = when(priority) {
                        "Low" -> if(isSelected) Color(0xFF6366F1) else Color(0xFF6366F1).copy(alpha = 0.2f)
                        "Medium" -> if(isSelected) Color(0xFFF59E0B) else Color(0xFFF59E0B).copy(alpha = 0.2f)
                        "High" -> if(isSelected) Color(0xFFEF4444) else Color(0xFFEF4444).copy(alpha = 0.2f)
                        else -> Color.Gray
                    }
                    Button(
                        onClick = { selectedPriority = priority },
                        modifier = Modifier.weight(1f).height(42.dp).border(if(isSelected) 0.dp else 1.dp, if(isSelected) Color.Transparent else BorderColor, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) color else InputBackground, contentColor = if (isSelected) Color.White else TextWhite),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(priority, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                }
            }
             Spacer(modifier = Modifier.height(24.dp))
             
             Box(modifier = Modifier.fillMaxWidth().background(InputBackground, RoundedCornerShape(12.dp)).border(1.dp, BorderColor, RoundedCornerShape(12.dp)).padding(16.dp)) {
                 Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                     Row(verticalAlignment = Alignment.CenterVertically) {
                         Box(modifier = Modifier.size(32.dp).background(PrimaryBlue.copy(alpha = 0.2f), CircleShape), contentAlignment = Alignment.Center) {
                             Icon(Icons.Outlined.Notifications, null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                         }
                         Spacer(modifier = Modifier.width(12.dp))
                         Column {
                             Text("Repeat Reminder", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextWhite)
                             Text("Get notifications for this task", style = MaterialTheme.typography.bodySmall, color = TextGrey)
                         }
                     }
                     Switch(
                         checked = isRepeatReminder, 
                         onCheckedChange = { isRepeatReminder = it },
                         colors = SwitchDefaults.colors(
                             checkedThumbColor = TextWhite,
                             checkedTrackColor = PrimaryBlue,
                             uncheckedThumbColor = TextGrey,
                             uncheckedTrackColor = InputBackground.copy(alpha = 0.5f),
                             uncheckedBorderColor = BorderColor
                         )
                     )
                 }
             }
             Spacer(modifier = Modifier.height(24.dp))
        }

        // Add Assets & Local Files (Common for Note and Todo, maybe Link too? Screenshot shows it for Note and Todo)
        if (selectedType != ResourceType.Link) {
             InputLabel("Add Assets")
             Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                 AssetButton("Add Images", Icons.Default.Add, Modifier.weight(1f))
                 AssetButton("Add Files", Icons.Default.AttachFile, Modifier.weight(1f))
             }
             Spacer(modifier = Modifier.height(24.dp))

             InputLabel("Local Files")
             Column(
                 modifier = Modifier
                     .fillMaxWidth()
                     .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                     .background(InputBackground.copy(alpha=0.5f), RoundedCornerShape(12.dp))
                     .padding(12.dp)
             ) {
                 Row(verticalAlignment = Alignment.CenterVertically) {
                     Icon(Icons.Default.Folder, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(24.dp))
                     Spacer(modifier = Modifier.width(12.dp))
                     Button(
                         onClick = {}, 
                         colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF374151)), 
                         shape = RoundedCornerShape(8.dp), 
                         contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp), 
                         modifier = Modifier.height(32.dp)
                     ) {
                         Text("Choose Files", style = MaterialTheme.typography.bodySmall, color = Color(0xFFF59E0B))
                     }
                     Spacer(modifier = Modifier.width(12.dp))
                     Text("No file chosen", style = MaterialTheme.typography.bodySmall, color = TextGrey)
                 }
                 Spacer(modifier = Modifier.height(8.dp))
                 Text(
                     "These files are stored locally on your device, not uploaded to cloud.\nOnly visible on this device.", 
                     style = MaterialTheme.typography.labelSmall, 
                     color = Color(0xFFF59E0B), 
                     fontSize = 10.sp, 
                     lineHeight = 12.sp
                 )
             }
             Spacer(modifier = Modifier.height(24.dp))
        }
        
        CategorySelector(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it },
            onCreateCategoryClick = onCreateCategoryClick
        )
        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton("Create Resource", onClick = {
            val newResource = Resource(
                type = selectedType,
                title = title,
                description = description,
                category = if (selectedCategory.isNotEmpty()) selectedCategory else null,
                url = if (selectedType == ResourceType.Link) url else null,
                content = if (selectedType == ResourceType.Note) noteContent else null,
                dueDate = if (selectedType == ResourceType.Todo) "Jan 31, 2026" else null, // Placeholder date, logic needs DatePicker
                time = if (selectedType == ResourceType.Todo) "10:00 AM" else null, // Placeholder time
                priority = if (selectedType == ResourceType.Todo) selectedPriority else null,
                isRepeatReminder = isRepeatReminder,
                assetCount = if (selectedType != ResourceType.Link) 1 else 0 // Placeholder count
            )
            onCreateResource(newResource)
        })
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// --- Shared Components ---
@Composable
fun OrbdynLogo(size: androidx.compose.ui.unit.Dp = 48.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape((size.value * 0.25).dp))
            .background(Brush.linearGradient(listOf(Color(0xFF2E65F3), Color(0xFF153060)))),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.Layers, "Logo", tint = Color.White, modifier = Modifier.size(size * 0.6f))
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    showArrow: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryBlue
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            if (showArrow) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun InputLabel(text: String) {
    Text(text.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
fun AssetButton(text: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Button(
        onClick = {},
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface, contentColor = MaterialTheme.colorScheme.onBackground),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
             Box(modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape), contentAlignment = Alignment.Center) {
                 Icon(icon, null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
             }
             Spacer(modifier = Modifier.height(8.dp))
             Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CustomInput(
    value: String, 
    onValueChange: (String) -> Unit, 
    hint: String, 
    icon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(hint, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)) },
        leadingIcon = if (icon != null) { { Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) } } else null,
        trailingIcon = if (trailingIcon != null) { { Icon(trailingIcon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) } } else null,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = singleLine,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedBorderColor = MaterialTheme.colorScheme.outline,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            cursorColor = PrimaryBlue,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

// --- New Screens and Modals ---

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    onNavigateToDashboard: (String) -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onAddCategory: (Category) -> Unit,
    categories: List<Category>
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showCreateCategoryModal by remember { mutableStateOf(false) }

    if (showCreateCategoryModal) {
        CreateCategoryModal(
            onDismiss = { showCreateCategoryModal = false },
            onCategoryCreated = {
                onAddCategory(it)
                showCreateCategoryModal = false
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SidebarContent(
                onSignOut = onSignOut,
                selectedItem = "", // Nothing selected in sidebar when on profile
                onItemSelected = {
                    onNavigateToDashboard(it)
                    scope.launch { drawerState.close() }
                },
                onProfileClick = { scope.launch { drawerState.close() } }, // Already on profile
                onCreateCategory = {
                    scope.launch { drawerState.close() }
                    showCreateCategoryModal = true
                },
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                categories = categories
            )
        },
        scrimColor = Color.Black.copy(alpha = 0.5f)
    ) {
        Scaffold(
            topBar = {
                DashboardTopBar(
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onAddClick = null
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Profile Avatar Large
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
           Column(horizontalAlignment = Alignment.CenterHorizontally) {
               Box(
                   modifier = Modifier
                       .size(100.dp)
                       .background(Color(0xFF22D3EE), CircleShape), // Cyan color from screenshot
                   contentAlignment = Alignment.Center
               ) {
                   Text("GC", style = MaterialTheme.typography.displayMedium, color = Color.Black, fontWeight = FontWeight.Bold)
               }
               Spacer(modifier = Modifier.height(24.dp))
               Text("Girish Chandwani", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
               Text("girish@meensou.com", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
               Spacer(modifier = Modifier.height(24.dp))
               Button(
                   onClick = {},
                   colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onBackground),
                   shape = RoundedCornerShape(12.dp),
                   border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
               ) {
                   Icon(Icons.Outlined.Edit, null, modifier = Modifier.size(16.dp))
                   Spacer(modifier = Modifier.width(8.dp))
                   Text("Edit Profile")
               }
           }
        }
        
        Spacer(modifier = Modifier.height(48.dp))

        // Account Details Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background), // Using background color for card to blend or surface if needed. Screenshot looks like it blends or is slightly lighter/darker. Let's use surfaceVariant for card background in dark mode context usually, but screenshot shows it dark. 
            // In screenshot, card is slightly lighter than background (070C18 vs 020610). 
            // We'll use a custom color for dark mode fidelity if needed, or Surface.
             modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp)),
             shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.Person, null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Account Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        Text("Manage your identity", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                
                InputLabel("FULL NAME")
                OutlinedTextField(
                    value = "Girish Chandwani",
                    onValueChange = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledTextColor = MaterialTheme.colorScheme.onBackground,
                        disabledBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                InputLabel("EMAIL ADDRESS")
                OutlinedTextField(
                    value = "girish@meensou.com",
                    onValueChange = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledTextColor = MaterialTheme.colorScheme.onBackground,
                        disabledBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("* Email cannot be changed for security reasons", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onSignOut,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp), // Pill shape
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C0B15), contentColor = DangerRed),
            border = BorderStroke(1.dp, DangerRed.copy(alpha = 0.3f))
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign Out Account", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSortModal(onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkBackground,
        contentColor = TextWhite
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Filter & Sort", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Done", color = PrimaryBlue, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onDismiss() })
            }
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("CUSTOM ORDER", style = MaterialTheme.typography.labelSmall, color = TextGrey, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            
            val options = listOf("Title (A-Z)", "Title (Z-A)", "Date Created (Newest)", "Date Created (Oldest)", "Favorites First")
            var selectedOption by remember { mutableStateOf("Date Created (Newest)") }
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
            ) {
                options.forEachIndexed { index, option ->
                    val isSelected = selectedOption == option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isSelected) Color(0xFF0F1C30) else SurfaceDark)
                            .clickable { selectedOption = option }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(option, color = if (isSelected) PrimaryBlue else TextWhite, fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal)
                        if (isSelected) {
                            Icon(Icons.Default.Check, null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                        }
                    }
                    if (index < options.size - 1) {
                        HorizontalDivider(color = BorderColor, thickness = 1.dp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("BY CATEGORY", style = MaterialTheme.typography.labelSmall, color = TextGrey, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("All Categories")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("DATE RANGE", style = MaterialTheme.typography.labelSmall, color = TextGrey, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.weight(1f).height(50.dp).border(1.dp, BorderColor, RoundedCornerShape(12.dp)).padding(12.dp), contentAlignment = Alignment.CenterStart) {
                     Row(verticalAlignment = Alignment.CenterVertically) {
                         Text("mm/dd/yyyy", color = TextGrey)
                         Spacer(modifier = Modifier.weight(1f))
                         Icon(Icons.Default.DateRange, null, tint = TextGrey)
                     }
                }
                 Box(modifier = Modifier.weight(1f).height(50.dp).border(1.dp, BorderColor, RoundedCornerShape(12.dp)).padding(12.dp), contentAlignment = Alignment.CenterStart) {
                     Row(verticalAlignment = Alignment.CenterVertically) {
                         Text("mm/dd/yyyy", color = TextGrey)
                         Spacer(modifier = Modifier.weight(1f))
                         Icon(Icons.Default.DateRange, null, tint = TextGrey)
                     }
                }
            }
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelector(
    categories: List<Category>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onCreateCategoryClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    InputLabel("Category (Optional)")
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        Box(modifier = Modifier
            .menuAnchor()
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(InputBackground)
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (selectedCategory.isNotEmpty()) selectedCategory else "Select Category (Optional)",
                    color = if (selectedCategory.isNotEmpty()) TextWhite else TextGrey
                )
                Icon(if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, null, tint = TextGrey)
            }
        }
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(SurfaceDark)
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { 
                        val isSelected = selectedCategory == category.name
                        Text(
                            category.name, 
                            color = if (isSelected) PrimaryBlue else TextWhite,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ) 
                    },
                    onClick = {
                        if (selectedCategory == category.name) {
                            onCategorySelected("")
                        } else {
                            onCategorySelected(category.name)
                        }
                        expanded = false
                    }
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            DropdownMenuItem(
                text = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp), tint = PrimaryBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create New Category", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                    }
                }, 
                onClick = { 
                    onCreateCategoryClick()
                    expanded = false 
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCategoryModal(onDismiss: () -> Unit, onCategoryCreated: (Category) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkBackground,
        contentColor = TextWhite
    ) {
         Column(modifier = Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("New Category", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                IconButton(onClick = onDismiss, modifier = Modifier.background(SurfaceDark, CircleShape).size(32.dp)) {
                    Icon(Icons.Default.Close, null, tint = TextGrey, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            var categoryName by remember { mutableStateOf("") }
            
            InputLabel("CATEGORY NAME")
            CustomInput(value = categoryName, onValueChange = { categoryName = it }, hint = "e.g., Work, Personal, Ideas")
            Spacer(modifier = Modifier.height(24.dp))
            InputLabel("THEME COLOR")
            
            val colors = listOf(
                Color(0xFFFF7F11), // Orange
                Color(0xFFEF4444), // Red
                Color(0xFF22C55E), // Green
                Color(0xFF3B82F6), // Blue
                Color(0xFFA855F7), // Purple
                Color(0xFFE879F9), // Pink
                Color(0xFF06B6D4), // Cyan
                Color(0xFFEAB308)  // Yellow
            )
            var selectedColor by remember { mutableStateOf(colors[0]) }
            
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    colors.take(4).forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(color, RoundedCornerShape(12.dp))
                                .clickable { selectedColor = color }
                                .border(if (selectedColor == color) 2.dp else 0.dp, Color.White, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedColor == color) Icon(Icons.Default.Check, null, tint = Color.White)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                     colors.drop(4).take(4).forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(color, RoundedCornerShape(12.dp))
                                .clickable { selectedColor = color }
                                .border(if (selectedColor == color) 2.dp else 0.dp, Color.White, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedColor == color) Icon(Icons.Default.Check, null, tint = Color.White)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(150.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = onDismiss,
                     modifier = Modifier.weight(1f).height(56.dp),
                     colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                ) {
                    Text("Cancel", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = {
                        if (categoryName.isNotEmpty()) {
                            onCategoryCreated(Category(categoryName, selectedColor))
                        } else {
                            onDismiss()
                        }
                    },
                     modifier = Modifier.weight(1f).height(56.dp),
                     colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1B4B), contentColor = PrimaryBlue),
                     shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Create Category", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
         }
    }
}