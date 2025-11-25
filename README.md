# 👔 DressCode - Tu Armario Inteligente

> **Gestor de Armario & Generador de Outfits con Inteligencia de Negocio**
>
> *Proyecto universitario para la asignatura de Desarrollo de Aplicaciones Móviles.*

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple?logo=kotlin)
![Android](https://img.shields.io/badge/Platform-Android-green?logo=android)
![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-blue)
![Spring Boot](https://img.shields.io/badge/Backend-Spring%20Boot-green?logo=spring)
![AWS](https://img.shields.io/badge/Cloud-AWS%20EC2-orange?logo=amazon-aws)

## 📖 Descripción

**DressCode** es una aplicación móvil nativa desarrollada en **Kotlin** que resuelve el problema cotidiano de "¿Qué me pongo hoy?". Permite a los usuarios digitalizar su inventario de ropa, gestionar prendas y utilizar un algoritmo inteligente para generar sugerencias de outfits aleatorios.

El sistema opera bajo una **arquitectura híbrida**, sincronizando el inventario pesado en la nube (AWS) mientras mantiene las preferencias del usuario y outfits generados de forma local para una experiencia fluida.

---

## ✨ Características Principales

### 📱 Funcionalidades del Usuario
* **🔐 Autenticación Segura:** Registro e inicio de sesión con persistencia de sesión local (Room Database).
* **📸 Digitalización de Armario:**
    * Carga de prendas vía **Cámara** o **Galería**.
    * **Procesamiento de Imagen:** Algoritmo de corrección automática de rotación EXIF.
    * Clasificación por Categoría y Color.
* **👗 Gestión de Outfits (Persistencia Híbrida):**
    * **Creación Manual:** Selección personalizada de prendas para guardar looks favoritos.
    * **Generador Automático:** Algoritmo que sugiere combinaciones válidas basándose en el inventario actual.
    * **Guardado Local:** Los outfits se persisten en el dispositivo (JSON), permitiendo consultarlos sin conexión inmediata.
* **🔍 Filtros Avanzados:**
    * Sistema de **FilterChips** para filtrar el armario por Categoría y Color en tiempo real.
    * Opción de "Ver Todos" y limpieza de filtros dinámica.

---

## 🛠️ Stack Tecnológico

### 📱 Cliente Móvil (Android)
* **Lenguaje:** Kotlin.
* **UI Toolkit:** Jetpack Compose (Material Design 3).
* **Arquitectura:** **MVVM** (Model-View-ViewModel) estricto.
* **Persistencia Local:**
    * **Room:** Para gestión de usuarios y sesiones.
    * **SharedPreferences + Gson:** Para serialización y almacenamiento de objetos complejos (Outfits).
* **Red (Networking):** Retrofit 2 + OkHttp.
* **Gestión de Estado:** `StateFlow` y `MutableState` de Compose.

### ☁️ Backend & Infraestructura
* **Framework:** Java 17 + Spring Boot 3.
* **Base de Datos:** MySQL (AWS RDS/Local).
* **Hosting:** AWS EC2 (Instancia Ubuntu Linux).
* **Gestión de Procesos:** Systemd (Daemon de servicio con reinicio automático).

---

## 🚀 Desafíos de Ingeniería y Optimizaciones

Este proyecto implementa soluciones técnicas avanzadas para operar eficientemente en la nube y en el dispositivo:

1.  **Optimización de Memoria en AWS (EC2):**
    * Implementación de **Swap File (2GB)** para mitigar el límite de RAM en instancias `t2.micro`.
    * Estrategia de **Paginación Simulada** en el Backend: La API limita la carga de imágenes en memoria (lotes de 20) para evitar desbordamientos (OOM), manteniendo la compatibilidad con el cliente móvil.
2.  **Refactorización MVVM:**
    * Desacoplamiento total de la UI y la Lógica de Negocio.
    * Los `Composables` son "vistas tontas" que solo reaccionan a estados del `ViewModel`.
    * Centralización de constantes y lógica de filtrado.
3.  **Estrategia de Persistencia Dual:**
    * **Prendas (Remoto):** Se almacenan en MySQL para escalabilidad.
    * **Outfits (Local):** Se almacenan localmente usando serialización JSON para acceso instantáneo y persistencia entre sesiones sin sobrecargar la base de datos relacional.

-----

## ⚙️ Instalación y Configuración

1.  **Clonar el repositorio:**
    ```bash
    git clone [https://github.com/bay-fuentesb/App_Ropa.git](https://github.com/bay-fuentesb/App_Ropa.git)
    ```
2.  **Abrir en Android Studio:** Se recomienda versión Koala o superior.
3.  **Configurar API:**
    * El proyecto apunta a una IP elástica de AWS EC2.
    * Para cambiar el entorno, editar `BASE_URL` en:
      `cl.duoc.myapplication.network.RetrofitClient`
4.  **Compilar:**
    * Sincronizar Gradle y ejecutar en Emulador o Dispositivo Físico (Requiere Android 8.0+).

---

## 👨‍💻 Autores:

* **[Bayron Fuentes]** - *Full Stack Developer* - [GitHub](https://github.com/bay-fuentesb)
* **[Miguel Campos]** - *Full Stack Developer* - [GitHub](https://github.com/miguelcamposdasilva)
* * **[Franco Garay]** - *Full Stack Developer* - [GitHub](https://github.com/francog14)
---
*Proyecto realizado para **Duoc UC**, 2025.*
*Todos los Derechos Reservados ©*