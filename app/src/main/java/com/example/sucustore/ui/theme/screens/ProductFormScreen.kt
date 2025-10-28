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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.sucustore.data.db.entity.Product
import com.example.sucustore.viewmodel.ProductViewModel
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
    var name by remember { mutableStateOf(existingProduct?.name ?: "") }
    var price by remember { mutableStateOf(existingProduct?.price?.toString() ?: "") }
    var description by remember { mutableStateOf(existingProduct?.description ?: "") }
    var stock by remember { mutableStateOf(existingProduct?.stock?.toString() ?: "") }
    var imageUri by remember { mutableStateOf<Uri?>(existingProduct?.imageUri?.let { Uri.parse(it) }) }
    val context = LocalContext.current

    // --- Lógica de Permisos y Cámara ---
    var hasCamPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCamPermission = isGranted
    }

    val takePhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp ->
        if (bmp != null) {
            imageUri = saveBitmapAndGetUri(context, bmp)
        }
    }
    // -------------------------------------

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existingProduct == null) "Agregar Producto" else "Editar Producto") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (imageUri != null) {
                Image(painter = rememberAsyncImagePainter(imageUri), contentDescription = "Foto", modifier = Modifier.size(150.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Precio") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                if (hasCamPermission) {
                    takePhotoLauncher.launch(null)
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }) {
                Text("Tomar Foto")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {
                val productToSave = Product(
                    id = existingProduct?.id ?: 0,
                    name = name,
                    price = price.toDoubleOrNull() ?: 0.0,
                    description = description,
                    stock = stock.toIntOrNull() ?: 0,
                    imageUri = imageUri?.toString()
                )
                productViewModel.insertProduct(productToSave)
                onSave()
            }) {
                Text("Guardar")
            }
        }
    }
}

private fun saveBitmapAndGetUri(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "photo_${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
    }
    return Uri.fromFile(file)
}