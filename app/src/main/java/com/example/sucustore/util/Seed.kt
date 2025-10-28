package com.example.sucustore.util

import android.content.Context
import android.util.Log
import com.example.sucustore.R
import com.example.sucustore.data.db.AppDatabase
import com.example.sucustore.data.db.entity.Product
import com.example.sucustore.data.db.entity.User
import com.example.sucustore.data.db.entity.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object Seed {

    suspend fun plant(context: Context) {
        withContext(Dispatchers.IO) {
            val database = AppDatabase.getDatabase(context)
            val userDao = database.userDao()
            val productDao = database.productDao()

            if (userDao.count() > 0) return@withContext

            Log.d("SEED_DEBUG", "Plantando semillas...")

            userDao.insert(User(name = "Admin", email = "admin@sucustore.com", password = "admin123", role = UserRole.ADMIN))

            // ¡¡CORREGIDO!! Se guarda la URI completa y correcta del recurso drawable.
            val uriPrefix = "android.resource://${context.packageName}/"
            val product1 = Product(name="Suculenta Echeveria", price=12.50, description="Bonita y fácil de cuidar", imageUri="$uriPrefix${R.drawable.suculenta}", stock=10)
            val product2 = Product(name="Cactus San Pedro", price=25.00, description="Un cactus clásico", imageUri="$uriPrefix${R.drawable.cactus}", stock=5)
            val product3 = Product(name="Aloe Vera", price=8.00, description="Con propiedades medicinales", imageUri="$uriPrefix${R.drawable.aloevera}", stock=20)
            
            productDao.insert(product1)
            productDao.insert(product2)
            productDao.insert(product3)
            Log.d("SEED_DEBUG", "Semillas plantadas.")
        }
    }
}