package com.example.sucustore.ui.theme.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.sucustore.R
import com.example.sucustore.data.db.entity.Product
import com.example.sucustore.viewmodel.ProductViewModel
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    existingProduct: Product? = null,
    onSave: () -> Unit,
    onBack: () -> Unit,
    productViewModel: ProductViewModel
) {
    // ---------- ESTADOS ----------
    var name by remember { mutableStateOf(existingProduct?.name ?: "") }
    var price by remember { mutableStateOf(existingProduct?.price?.toString() ?: "") }
    var description by remember { mutableStateOf(existingProduct?.description ?: "") }
    var stock by remember { mutableStateOf(existingProduct?.stock?.toString() ?: "") }
    var imageUri by remember { mutableStateOf<Uri?>(existingProduct?.imageUri?.let { Uri.parse(it) }) }

    val context = LocalContext.current

    // Errores
    var nameError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }
    var stockError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var imageError by remember { mutableStateOf<String?>(null) }

    var showSuccessAnimation by remember { mutableStateOf(false) }

    // Permiso de cámara
    var hasCamPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCamPermission = granted
    }

    // 📸 Launcher para tomar foto con la cámara
    val takePhotoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bmp ->
        if (bmp != null) {
            imageUri = saveBitmapAndGetUri(context, bmp)
            imageError = null
        }
    }

    // 🖼️ launcher para seleccionar imagen desde la galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            imageUri = uri
            imageError = null
        }
    }

    // ---------- UI ----------
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (existingProduct == null) "Agregar Producto" else "Editar Producto")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            // Éxito → animación
            if (showSuccessAnimation) {
                ProductAddedAnimation(onAnimationFinished = onSave)
                return@Box
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // ---------- IMAGEN ----------
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Foto planta",
                        modifier = Modifier
                            .size(150.dp)
                    )
                }

                if (imageError != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = imageError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(16.dp))

                // ---------- NOMBRE ----------
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = null
                    },
                    label = { Text("Nombre de la planta") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = nameError != null
                )
                nameError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }

                Spacer(Modifier.height(16.dp))

                // ---------- PRECIO ----------
                OutlinedTextField(
                    value = price,
                    onValueChange = {
                        price = it
                        priceError = null
                    },
                    label = { Text("Precio") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    isError = priceError != null
                )
                priceError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }

                Spacer(Modifier.height(16.dp))

                // ---------- STOCK ----------
                OutlinedTextField(
                    value = stock,
                    onValueChange = {
                        stock = it
                        stockError = null
                    },
                    label = { Text("Stock disponible") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    isError = stockError != null
                )
                stockError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }

                Spacer(Modifier.height(16.dp))

                // ---------- DESCRIPCIÓN ----------
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                        descriptionError = null
                    },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = descriptionError != null
                )
                descriptionError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }

                Spacer(Modifier.height(16.dp))

                // ---------- BOTÓN FOTO CÁMARA ----------
                Button(onClick = {
                    if (hasCamPermission) {
                        takePhotoLauncher.launch(null)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }) {
                    Text("Tomar foto de la planta")
                }

                Spacer(Modifier.height(8.dp))

                // BOTÓN FOTO DESDE GALERÍA
                Button(onClick = {
                    galleryLauncher.launch("image/*")
                }) {
                    Text("Seleccionar desde galería")
                }

                Spacer(Modifier.height(24.dp))

                // ---------- BOTÓN GUARDAR ----------
                Button(
                    onClick = {
                        var valid = true

                        // Limpio nombre y descripción para validar
                        val trimmedName = name.trim()
                        val trimmedDesc = description.trim()

                        // Normalizo el precio para soportar coma o punto
                        val priceValue = price.replace(",", ".")
                        val parsedPrice = priceValue.toDoubleOrNull()
                        val parsedStock = stock.toIntOrNull()

                        // ✅ Validar nombre
                        nameError = when {
                            trimmedName.isBlank() ->
                                "El nombre es obligatorio"
                            !Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$").matches(trimmedName) ->
                                "Solo se permiten letras y espacios"
                            trimmedName.length < 3 ->
                                "El nombre es demasiado corto"
                            trimmedName.length > 40 ->
                                "El nombre es demasiado largo"
                            else -> null
                        }
                        if (nameError != null) valid = false

                        // ✅ Validar precio
                        priceError = when {
                            priceValue.isBlank() ->
                                "El precio es obligatorio"
                            parsedPrice == null ->
                                "Debe ser un número válido"
                            parsedPrice <= 0.0 ->
                                "Debe ser mayor a 0"
                            parsedPrice > 1_000_000 ->
                                "El precio es demasiado alto"
                            else -> null
                        }
                        if (priceError != null) valid = false

                        // ✅ Validar stock
                        stockError = when {
                            stock.isBlank() ->
                                "El stock es obligatorio"
                            parsedStock == null ->
                                "Debe ser un número válido"
                            parsedStock < 0 ->
                                "El stock no puede ser negativo"
                            parsedStock > 10_000 ->
                                "El stock es demasiado alto"
                            else -> null
                        }
                        if (stockError != null) valid = false

                        // ✅ Validar descripción
                        descriptionError = when {
                            trimmedDesc.isBlank() ->
                                "La descripción es obligatoria"
                            trimmedDesc.length < 10 ->
                                "Mínimo 10 caracteres"
                            trimmedDesc.length > 300 ->
                                "La descripción es demasiado larga"
                            else -> null
                        }
                        if (descriptionError != null) valid = false

                        // ✅ Validar imagen
                        if (imageUri == null) {
                            imageError = "La foto de la planta es obligatoria"
                            valid = false
                        } else {
                            imageError = null
                        }

                        if (!valid) return@Button

                        // Guardar producto con valores ya limpios
                        val product = Product(
                            id = existingProduct?.id ?: 0,
                            name = trimmedName,
                            price = parsedPrice!!,
                            description = trimmedDesc,
                            stock = parsedStock!!,
                            imageUri = imageUri?.toString()
                        )

                        if (existingProduct == null) {
                            productViewModel.insertProduct(product)
                        } else {
                            productViewModel.updateProduct(product)
                        }

                        showSuccessAnimation = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}

@Composable
fun ProductAddedAnimation(onAnimationFinished: () -> Unit) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.success_animation)
    )

    LaunchedEffect(Unit) {
        delay(2000)
        onAnimationFinished()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = "Éxito",
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(16.dp))
        LottieAnimation(
            composition = composition,
            isPlaying = true,
            modifier = Modifier.size(200.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "¡Producto guardado con éxito!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun saveBitmapAndGetUri(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "photo_${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
    }
    return Uri.fromFile(file)
}
