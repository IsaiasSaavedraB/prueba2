package com.example.agencia.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.example.agencia.data.dao.ContactoDao
import com.example.agencia.data.dao.OportunidadDao
import com.example.agencia.data.dao.OrganizacionDao
import com.example.agencia.data.dao.SeguimientoDao
import com.example.agencia.data.dao.UsuarioDao
import com.example.agencia.model.Contacto
import com.example.agencia.model.EstadosOportunidad
import com.example.agencia.model.Oportunidad
import com.example.agencia.model.Organizacion
import com.example.agencia.model.Prioridades
import com.example.agencia.model.Roles
import com.example.agencia.model.Seguimiento
import com.example.agencia.model.TiposEvento
import com.example.agencia.model.TiposGestion
import com.example.agencia.model.Usuario

@Database(
    entities = [
        Usuario::class,
        Organizacion::class,
        Contacto::class,
        Oportunidad::class,
        Seguimiento::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun organizacionDao(): OrganizacionDao
    abstract fun contactoDao(): ContactoDao
    abstract fun oportunidadDao(): OportunidadDao
    abstract fun seguimientoDao(): SeguimientoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "crm_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {

            override fun onCreate(connection: SQLiteConnection) {
                super.onCreate(connection)
                seedDatabase(connection)
            }

            private fun seedDatabase(connection: SQLiteConnection) {
                // 1. Initial Mock Users (3 Executives, 1 Supervisor)
                connection.execSQL(
                    """
                    INSERT INTO usuarios (id, nombre, apellido, correo, password, rol, telefono) VALUES
                    (1, 'Juan', 'Pérez', 'juan.perez@crm.cl', '12345678', '${Roles.EJECUTIVO}', '+56911112222'),
                    (2, 'María', 'González', 'maria.gonzalez@crm.cl', '12345678', '${Roles.EJECUTIVO}', '+56933334444'),
                    (3, 'Carlos', 'Rodríguez', 'carlos.rodriguez@crm.cl', '12345678', '${Roles.EJECUTIVO}', '+56955556666'),
                    (4, 'Ana', 'Silva', 'supervisor@crm.cl', '12345678', '${Roles.SUPERVISOR}', '+56977778888')
                    """.trimIndent()
                )

                // 2. Organizations
                connection.execSQL(
                    """
                    INSERT INTO organizaciones (id, nombre, rut, industria, direccion, telefono) VALUES
                    (1, 'Empresas TechCorp Chile SpA', '76.123.456-7', 'Tecnología y Software', 'Av. Providencia 1234, Santiago', '+56221002000'),
                    (2, 'Banco Financiero del Sur', '96.987.654-3', 'Servicios Financieros', 'Huérfanos 850, Santiago', '+56223004000'),
                    (3, 'Constructora Horizonte S.A.', '77.456.789-1', 'Construcción y Minería', 'Av. Las Condes 9000, Las Condes', '+56225006000')
                    """.trimIndent()
                )

                // 3. Contacts
                connection.execSQL(
                    """
                    INSERT INTO contactos (id, organizacionId, nombre, cargo, correo, telefono) VALUES
                    (1, 1, 'Roberto Gómez', 'Gerente de Innovación', 'rgomez@techcorp.cl', '+56988881111'),
                    (2, 2, 'Camila Soto', 'Directora de Marketing', 'csoto@bancosur.cl', '+56988882222'),
                    (3, 3, 'Felipe Morales', 'Jefe de Compras', 'fmorales@horizonte.cl', '+56988883333'),
                    (4, 1, 'Patricia Fuentes', 'Subgerente Comercial', 'pfuentes@techcorp.cl', '+56988884444')
                    """.trimIndent()
                )

                // 4. Opportunities
                connection.execSQL(
                    """
                    INSERT INTO oportunidades (id, titulo, organizacionId, contactoId, ejecutivoId, montoEstimado, fechaEvento, ubicacion, tipoEvento, publicoEsperado, prioridad, estado, fotoPath, fechaCreacion) VALUES
                    (1, 'Summit Anual de Tecnología 2026', 1, 1, 1, 15000000.0, '2026-04-15', 'Centro de Eventos BordeRío, Vitacura', '${TiposEvento.CONFERENCIA}', 350, '${Prioridades.ALTA}', '${EstadosOportunidad.EN_GESTION}', NULL, '2026-02-01'),
                    (2, 'Lanzamiento App Banca Empresas', 2, 2, 1, 28000000.0, '2026-05-10', 'Hotel W Santiago, Las Condes', '${TiposEvento.LANZAMIENTO}', 200, '${Prioridades.ALTA}', '${EstadosOportunidad.PROPUESTA}', NULL, '2026-02-05'),
                    (3, 'Feria de Seguridad e Innovación', 3, 3, 2, 9500000.0, '2026-03-28', 'Espacio Riesco, Huechuraba', '${TiposEvento.FERIA}', 500, '${Prioridades.MEDIA}', '${EstadosOportunidad.EN_NEGOCIACION}', NULL, '2026-01-20'),
                    (4, 'Seminario Ciberseguridad Financiera', 2, 2, 2, 6000000.0, '2026-06-01', 'Auditorio Banco Sur, Santiago Centro', '${TiposEvento.SEMINARIO}', 120, '${Prioridades.BAJA}', '${EstadosOportunidad.NUEVA}', NULL, '2026-02-10'),
                    (5, 'Aniversario Corporativo 20 Años', 1, 4, 3, 18500000.0, '2026-07-20', 'Casona Las Condes, Las Condes', '${TiposEvento.CORPORATIVO}', 250, '${Prioridades.ALTA}', '${EstadosOportunidad.CONFIRMADA}', NULL, '2026-01-15'),
                    (6, 'Expo Vivienda y Ciudad 2026', 3, 3, NULL, 12000000.0, '2026-08-14', 'Estación Mapocho, Santiago', '${TiposEvento.FERIA}', 800, '${Prioridades.MEDIA}', '${EstadosOportunidad.NUEVA}', NULL, '2026-02-12')
                    """.trimIndent()
                )

                // 5. Initial Follow-ups
                connection.execSQL(
                    """
                    INSERT INTO seguimientos (id, oportunidadId, usuarioId, fechaHora, tipoGestion, descripcion, nuevoEstado) VALUES
                    (1, 1, 1, '2026-02-02 10:30', '${TiposGestion.LLAMADA}', 'Llamada inicial de presentación del evento. Cliente solicitó propuesta económica.', '${EstadosOportunidad.EN_GESTION}'),
                    (2, 2, 1, '2026-02-08 15:00', '${TiposGestion.CORREO}', 'Envío de propuesta formal por $28M. Pendiente revisión de directorio.', '${EstadosOportunidad.PROPUESTA}'),
                    (3, 3, 2, '2026-02-11 11:15', '${TiposGestion.REUNION}', 'Reunión de negociación de costos y requerimientos técnicos en Espacio Riesco.', '${EstadosOportunidad.EN_NEGOCIACION}')
                    """.trimIndent()
                )
            }
        }
    }
}