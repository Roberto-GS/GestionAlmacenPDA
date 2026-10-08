**GestionAlmacenPDA**

PROBLEMA QUE RESUELVE
El almacén necesitaba una forma rápida y fiable de que los operarios puedan consultar el stock, puedan registrar entradas y salidas de mercancía, y corrijan las discrepancias, sin depender de métodos manuales propensos a errores. La prioridad era que la app fuera sencilla de usar por cualquier operario, sin conocimientos técnicos, y que fuera manejable con una sola mano

FUNCIONALIDADES
- Consultas de Inventario con búsquedas en tiempo real, integradas con el lector de código de barras de la PDA
- Registro de Entradas, Salidas y Ajustes de Stock (Positivos y Negativos) con validaciones
- Historial de movimientos, general y por producto
- Detección automática de productos con bajo stock
- Sistema de usuarios con roles bien definidos (administrador / operario)
- Gestión de Categorías y Ubicaciones del almacén
- Bajas Lógicas: Los productos dados de baja no apareceran en el inventario pero conservaran su historial, y se pueden reactivar

<img width="500" height="800" alt="Screenshot_2026-10-08-12-51-58-067_com example gestionalmacenpda-edit" src="https://github.com/user-attachments/assets/c8e20d5b-a389-46b4-8952-f8a83c35f836" />

<img width="500" height="800" alt="Screenshot_2026-10-08-12-52-18-809_com example gestionalmacenpda-edit" src="https://github.com/user-attachments/assets/0f853116-c4f8-4508-8677-dabf9c796e53" />


ARQUITECTURA
Arquitectura por capas:
- data: DAOs de Room y repositorios
- di: módulos de inyección de dependencias con Hilt
- domain: modelos, interfaces de repositorio y casos de uso
- ui: Compose organizadas por funcionalidad, cada una con su ViewModel

TECNOLOGÍAS
Kotlin · Jetpack Compose · Material Design 3 · Room (SQLite) · Dagger Hilt · Navigation Compose · Coroutines/Flow

QUE APRENDÍ
- A resolver un problema real de negocio, no un ejercicio académico: priorizar la fiabilidad y la simplicidad de uso sobre la complejidad técnica.
- A depurar un bug no trivial relacionado con el ciclo de vida de Android (pérdida de sesión al rotar la pantalla o cambiar el tema), resuelto vinculando la sesión a un ViewModel que sobrevive a la recreación de la Activity.
- A tomar decisiones de diseño por iniciativa propia dentro de un encargo de empresa.
