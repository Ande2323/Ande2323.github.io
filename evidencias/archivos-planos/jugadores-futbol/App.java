import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


// Entidades y formato de cada archivo (separador "|"):
//   selecciones.txt -> id|nombre|confederacion|ranking|titulos
//   equipos.txt     -> id|nombre|pais|ciudad|liga|anioFundacion
//   jugadores.txt   -> id|nombre|apellido|edad|idSeleccion|idEquipo|dorsal
//   partidos.txt    -> id|idLocal|idVisitante|golesLocal|golesVisitante|fecha|estadio|fase
public class App {

    // Rutas de los archivos
    static final String RUTA_SELECCIONES = "src/selecciones.txt";
    static final String RUTA_EQUIPOS = "src/equipos.txt";
    static final String RUTA_JUGADORES = "src/jugadores.txt";
    static final String RUTA_PARTIDOS = "src/partidos.txt";

    static final String SEPARADOR = "|";

    // Cantidad de campos de cada entidad
    static final int CAMPOS_SELECCION = 5;
    static final int CAMPOS_EQUIPO = 6;
    static final int CAMPOS_JUGADOR = 7;
    static final int CAMPOS_PARTIDO = 8;

    // Rangos válidos
    static final int EDAD_MINIMA = 15;
    static final int EDAD_MAXIMA = 60;
    static final int DORSAL_MINIMO = 1;
    static final int DORSAL_MAXIMO = 99;

    // Menú principal

    public static void main(String[] args) {

        int opcion = 0;

        do {
            opcion = Vista.menuPrincipal();

            switch (opcion) {
                case 1:
                    menuJugadores();
                    break;
                case 2:
                    menuSelecciones();
                    break;
                case 3:
                    menuEquipos();
                    break;
                case 4:
                    menuPartidos();
                    break;
                case 5:
                    menuRelacionadas();
                    break;
                case 6:
                    Vista.mensaje("Saliendo...");
                    break;
                default:
                    Vista.mensaje("Opción no válida");
            }

        } while (opcion != 6);

        Vista.cerrar();
    }

    // Jugadores

    static void menuJugadores() {
        int opcion = Vista.menuCrud("JUGADORES");

        switch (opcion) {
            case 1:
                crearJugador();
                break;
            case 2:
                actualizarJugador();
                break;
            case 3:
                eliminarJugador();
                break;
            case 4:
                break;
            default:
                Vista.mensaje("Opción no válida");
        }
    }

    static void crearJugador() {
        String nombre = Vista.leerTexto("Nombre: ");
        String apellido = Vista.leerTexto("Apellido: ");
        int edad = Vista.leerEntero("Edad: ");

        int idSeleccion = pedirIdExistente("selección", RUTA_SELECCIONES, CAMPOS_SELECCION);
        if (idSeleccion == -1) {
            return;
        }
        int idEquipo = pedirIdExistente("equipo", RUTA_EQUIPOS, CAMPOS_EQUIPO);
        if (idEquipo == -1) {
            return;
        }

        int dorsal = Vista.leerEntero("Dorsal: ");

        if (!textoValido(nombre, "nombre") || !textoValido(apellido, "apellido")) {
            return;
        }
        if (!edadValida(edad) || !dorsalValido(dorsal)) {
            return;
        }

        int id = siguienteId(RUTA_JUGADORES);
        if (agregarLinea(RUTA_JUGADORES, unir(id, nombre, apellido, edad, idSeleccion, idEquipo, dorsal))) {
            Vista.mensaje("Jugador creado con el Id " + id);
        }
    }

    static void actualizarJugador() {
        int id = Vista.leerEntero("Id del jugador a actualizar: ");

        String[] jugador = buscarPorId(RUTA_JUGADORES, id, CAMPOS_JUGADOR);
        if (jugador == null) {
            Vista.mensaje("No existe un jugador con el Id " + id);
            return;
        }

        Vista.mensaje("Registro actual:");
        mostrarJugador(jugador, true);
        Vista.avisoCamposOpcionales();

        String nombre = Vista.pedirTextoOpcional("Nombre", jugador[1]);
        String apellido = Vista.pedirTextoOpcional("Apellido", jugador[2]);
        int edad = Vista.pedirEnteroOpcional("Edad", aEntero(jugador[3]));
        int idSeleccion = Vista.pedirEnteroOpcional("Id selección", aEntero(jugador[4]));
        int idEquipo = Vista.pedirEnteroOpcional("Id equipo", aEntero(jugador[5]));
        int dorsal = Vista.pedirEnteroOpcional("Dorsal", aEntero(jugador[6]));

        if (!textoValido(nombre, "nombre") || !textoValido(apellido, "apellido")) {
            return;
        }
        if (!edadValida(edad) || !dorsalValido(dorsal)) {
            return;
        }
        if (!existeId(RUTA_SELECCIONES, idSeleccion, CAMPOS_SELECCION)) {
            Vista.mensaje("No existe una selección con el Id " + idSeleccion);
            return;
        }
        if (!existeId(RUTA_EQUIPOS, idEquipo, CAMPOS_EQUIPO)) {
            Vista.mensaje("No existe un equipo con el Id " + idEquipo);
            return;
        }

        if (actualizarLinea(RUTA_JUGADORES, id,
                unir(id, nombre, apellido, edad, idSeleccion, idEquipo, dorsal), CAMPOS_JUGADOR)) {
            Vista.mensaje("Jugador actualizado correctamente.");
        }
    }

    static void eliminarJugador() {
        int id = Vista.leerEntero("Id del jugador a eliminar: ");

        String[] jugador = buscarPorId(RUTA_JUGADORES, id, CAMPOS_JUGADOR);
        if (jugador == null) {
            Vista.mensaje("No existe un jugador con el Id " + id);
            return;
        }

        Vista.mensaje("Se eliminará el siguiente jugador:");
        mostrarJugador(jugador, true);

        if (!Vista.confirmar()) {
            Vista.mensaje("Operación cancelada.");
            return;
        }
        if (eliminarLinea(RUTA_JUGADORES, id, CAMPOS_JUGADOR)) {
            Vista.mensaje("Jugador eliminado.");
        }
    }

    // Selecciones

    static void menuSelecciones() {
        int opcion = Vista.menuCrud("SELECCIONES");

        switch (opcion) {
            case 1:
                crearSeleccion();
                break;
            case 2:
                actualizarSeleccion();
                break;
            case 3:
                eliminarSeleccion();
                break;
            case 4:
                break;
            default:
                Vista.mensaje("Opción no válida");
        }
    }

    static void crearSeleccion() {
        String nombre = Vista.leerTexto("Nombre: ");
        String confederacion = Vista.leerTexto("Confederación (CONMEBOL, UEFA, CAF, AFC, CONCACAF, OFC): ");
        int ranking = Vista.leerEntero("Ranking FIFA: ");
        int titulos = Vista.leerEntero("Títulos mundiales: ");

        if (!textoValido(nombre, "nombre") || !textoValido(confederacion, "confederación")) {
            return;
        }
        if (existeTexto(RUTA_SELECCIONES, 1, nombre, CAMPOS_SELECCION)) {
            Vista.mensaje("Ya existe una selección llamada " + nombre + ".");
            return;
        }
        if (ranking < 1) {
            Vista.mensaje("El ranking debe ser mayor que cero.");
            return;
        }
        if (titulos < 0) {
            Vista.mensaje("Los títulos no pueden ser negativos.");
            return;
        }

        int id = siguienteId(RUTA_SELECCIONES);
        if (agregarLinea(RUTA_SELECCIONES, unir(id, nombre, confederacion, ranking, titulos))) {
            Vista.mensaje("Selección creada con el Id " + id);
        }
    }

    static void actualizarSeleccion() {
        int id = Vista.leerEntero("Id de la selección a actualizar: ");

        String[] seleccion = buscarPorId(RUTA_SELECCIONES, id, CAMPOS_SELECCION);
        if (seleccion == null) {
            Vista.mensaje("No existe una selección con el Id " + id);
            return;
        }

        Vista.registroActual(seleccion);
        Vista.avisoCamposOpcionales();

        String nombre = Vista.pedirTextoOpcional("Nombre", seleccion[1]);
        String confederacion = Vista.pedirTextoOpcional("Confederación", seleccion[2]);
        int ranking = Vista.pedirEnteroOpcional("Ranking FIFA", aEntero(seleccion[3]));
        int titulos = Vista.pedirEnteroOpcional("Títulos", aEntero(seleccion[4]));

        if (!textoValido(nombre, "nombre") || !textoValido(confederacion, "confederación")) {
            return;
        }
        if (ranking < 1 || titulos < 0) {
            Vista.mensaje("Ranking o títulos fuera de rango.");
            return;
        }

        if (actualizarLinea(RUTA_SELECCIONES, id, unir(id, nombre, confederacion, ranking, titulos),
                CAMPOS_SELECCION)) {
            Vista.mensaje("Selección actualizada correctamente.");
        }
    }

    static void eliminarSeleccion() {
        int id = Vista.leerEntero("Id de la selección a eliminar: ");

        String[] seleccion = buscarPorId(RUTA_SELECCIONES, id, CAMPOS_SELECCION);
        if (seleccion == null) {
            Vista.mensaje("No existe una selección con el Id " + id);
            return;
        }

        int jugadores = contarPorColumna(RUTA_JUGADORES, 4, id, CAMPOS_JUGADOR);
        int partidos = contarPorColumna(RUTA_PARTIDOS, 1, id, CAMPOS_PARTIDO)
                + contarPorColumna(RUTA_PARTIDOS, 2, id, CAMPOS_PARTIDO);

        if (jugadores > 0 || partidos > 0) {
            Vista.mensaje("No se puede eliminar: la selección " + seleccion[1] + " tiene "
                    + jugadores + " jugadores y " + partidos + " partidos asociados.");
            return;
        }

        if (!Vista.confirmar()) {
            Vista.mensaje("Operación cancelada.");
            return;
        }
        if (eliminarLinea(RUTA_SELECCIONES, id, CAMPOS_SELECCION)) {
            Vista.mensaje("Selección eliminada.");
        }
    }

    // Equipos

    static void menuEquipos() {
        int opcion = Vista.menuCrud("EQUIPOS");

        switch (opcion) {
            case 1:
                crearEquipo();
                break;
            case 2:
                actualizarEquipo();
                break;
            case 3:
                eliminarEquipo();
                break;
            case 4:
                break;
            default:
                Vista.mensaje("Opción no válida");
        }
    }

    static void crearEquipo() {
        String nombre = Vista.leerTexto("Nombre: ");
        String pais = Vista.leerTexto("País: ");
        String ciudad = Vista.leerTexto("Ciudad: ");
        String liga = Vista.leerTexto("Liga: ");
        int fundacion = Vista.leerEntero("Año de fundación: ");

        if (!textoValido(nombre, "nombre") || !textoValido(pais, "país")
                || !textoValido(ciudad, "ciudad") || !textoValido(liga, "liga")) {
            return;
        }
        if (existeTexto(RUTA_EQUIPOS, 1, nombre, CAMPOS_EQUIPO)) {
            Vista.mensaje("Ya existe un equipo llamado " + nombre + ".");
            return;
        }
        if (!fundacionValida(fundacion)) {
            return;
        }

        int id = siguienteId(RUTA_EQUIPOS);
        if (agregarLinea(RUTA_EQUIPOS, unir(id, nombre, pais, ciudad, liga, fundacion))) {
            Vista.mensaje("Equipo creado con el Id " + id);
        }
    }

    static void actualizarEquipo() {
        int id = Vista.leerEntero("Id del equipo a actualizar: ");

        String[] equipo = buscarPorId(RUTA_EQUIPOS, id, CAMPOS_EQUIPO);
        if (equipo == null) {
            Vista.mensaje("No existe un equipo con el Id " + id);
            return;
        }

        Vista.registroActual(equipo);
        Vista.avisoCamposOpcionales();

        String nombre = Vista.pedirTextoOpcional("Nombre", equipo[1]);
        String pais = Vista.pedirTextoOpcional("País", equipo[2]);
        String ciudad = Vista.pedirTextoOpcional("Ciudad", equipo[3]);
        String liga = Vista.pedirTextoOpcional("Liga", equipo[4]);
        int fundacion = Vista.pedirEnteroOpcional("Año de fundación", aEntero(equipo[5]));

        if (!textoValido(nombre, "nombre") || !textoValido(pais, "país")
                || !textoValido(ciudad, "ciudad") || !textoValido(liga, "liga")) {
            return;
        }
        if (!fundacionValida(fundacion)) {
            return;
        }

        if (actualizarLinea(RUTA_EQUIPOS, id, unir(id, nombre, pais, ciudad, liga, fundacion),
                CAMPOS_EQUIPO)) {
            Vista.mensaje("Equipo actualizado correctamente.");
        }
    }

    static void eliminarEquipo() {
        int id = Vista.leerEntero("Id del equipo a eliminar: ");

        String[] equipo = buscarPorId(RUTA_EQUIPOS, id, CAMPOS_EQUIPO);
        if (equipo == null) {
            Vista.mensaje("No existe un equipo con el Id " + id);
            return;
        }

        int jugadores = contarPorColumna(RUTA_JUGADORES, 5, id, CAMPOS_JUGADOR);
        if (jugadores > 0) {
            Vista.mensaje("No se puede eliminar: el equipo tiene " + jugadores + " jugadores asociados.");
            return;
        }

        if (!Vista.confirmar()) {
            Vista.mensaje("Operación cancelada.");
            return;
        }
        if (eliminarLinea(RUTA_EQUIPOS, id, CAMPOS_EQUIPO)) {
            Vista.mensaje("Equipo eliminado.");
        }
    }

    // Partidos

    static void menuPartidos() {
        int opcion = Vista.menuCrud("PARTIDOS");

        switch (opcion) {
            case 1:
                crearPartido();
                break;
            case 2:
                actualizarPartido();
                break;
            case 3:
                eliminarPartido();
                break;
            case 4:
                break;
            default:
                Vista.mensaje("Opción no válida");
        }
    }

    static void crearPartido() {
        int idLocal = pedirIdExistente("selección local", RUTA_SELECCIONES, CAMPOS_SELECCION);
        if (idLocal == -1) {
            return;
        }
        int idVisitante = pedirIdExistente("selección visitante", RUTA_SELECCIONES, CAMPOS_SELECCION);
        if (idVisitante == -1) {
            return;
        }
        if (idLocal == idVisitante) {
            Vista.mensaje("Una selección no puede jugar contra sí misma.");
            return;
        }

        int golesLocal = Vista.leerEntero("Goles del local: ");
        int golesVisitante = Vista.leerEntero("Goles del visitante: ");
        String fecha = Vista.leerTexto("Fecha (AAAA-MM-DD): ");
        String estadio = Vista.leerTexto("Estadio: ");
        String fase = Vista.leerTexto("Fase: ");

        if (golesLocal < 0 || golesVisitante < 0) {
            Vista.mensaje("Los goles no pueden ser negativos.");
            return;
        }
        if (!fechaValida(fecha) || !textoValido(estadio, "estadio") || !textoValido(fase, "fase")) {
            return;
        }

        int id = siguienteId(RUTA_PARTIDOS);
        if (agregarLinea(RUTA_PARTIDOS,
                unir(id, idLocal, idVisitante, golesLocal, golesVisitante, fecha, estadio, fase))) {
            Vista.mensaje("Partido creado con el Id " + id);
        }
    }

    static void actualizarPartido() {
        int id = Vista.leerEntero("Id del partido a actualizar: ");

        String[] partido = buscarPorId(RUTA_PARTIDOS, id, CAMPOS_PARTIDO);
        if (partido == null) {
            Vista.mensaje("No existe un partido con el Id " + id);
            return;
        }

        Vista.mensaje("Registro actual:");
        mostrarPartido(partido);
        Vista.avisoCamposOpcionales();

        int idLocal = Vista.pedirEnteroOpcional("Id selección local", aEntero(partido[1]));
        int idVisitante = Vista.pedirEnteroOpcional("Id selección visitante", aEntero(partido[2]));
        int golesLocal = Vista.pedirEnteroOpcional("Goles del local", aEntero(partido[3]));
        int golesVisitante = Vista.pedirEnteroOpcional("Goles del visitante", aEntero(partido[4]));
        String fecha = Vista.pedirTextoOpcional("Fecha (AAAA-MM-DD)", partido[5]);
        String estadio = Vista.pedirTextoOpcional("Estadio", partido[6]);
        String fase = Vista.pedirTextoOpcional("Fase", partido[7]);

        if (!existeId(RUTA_SELECCIONES, idLocal, CAMPOS_SELECCION)
                || !existeId(RUTA_SELECCIONES, idVisitante, CAMPOS_SELECCION)) {
            Vista.mensaje("Alguna de las selecciones no existe.");
            return;
        }
        if (idLocal == idVisitante) {
            Vista.mensaje("Una selección no puede jugar contra sí misma.");
            return;
        }
        if (golesLocal < 0 || golesVisitante < 0) {
            Vista.mensaje("Los goles no pueden ser negativos.");
            return;
        }
        if (!fechaValida(fecha) || !textoValido(estadio, "estadio") || !textoValido(fase, "fase")) {
            return;
        }

        if (actualizarLinea(RUTA_PARTIDOS, id,
                unir(id, idLocal, idVisitante, golesLocal, golesVisitante, fecha, estadio, fase),
                CAMPOS_PARTIDO)) {
            Vista.mensaje("Partido actualizado correctamente.");
        }
    }

    static void eliminarPartido() {
        int id = Vista.leerEntero("Id del partido a eliminar: ");

        String[] partido = buscarPorId(RUTA_PARTIDOS, id, CAMPOS_PARTIDO);
        if (partido == null) {
            Vista.mensaje("No existe un partido con el Id " + id);
            return;
        }

        Vista.mensaje("Se eliminará el siguiente partido:");
        mostrarPartido(partido);

        if (!Vista.confirmar()) {
            Vista.mensaje("Operación cancelada.");
            return;
        }
        if (eliminarLinea(RUTA_PARTIDOS, id, CAMPOS_PARTIDO)) {
            Vista.mensaje("Partido eliminado.");
        }
    }

    // Búsquedas relacionadas

    static void menuRelacionadas() {
        int opcion = Vista.menuRelacionadas();

        switch (opcion) {
            case 1:
                fichaJugador();
                break;
            case 2:
                seleccionDeJugador();
                break;
            case 3:
                equipoDeJugador();
                break;
            case 4:
                partidosDeJugador();
                break;
            case 5:
                jugadoresDeSeleccion();
                break;
            case 6:
                jugadoresDeEquipo();
                break;
            case 7:
                break;
            default:
                Vista.mensaje("Opción no válida");
        }
    }

    static void fichaJugador() {
        String[] jugador = seleccionarJugador();
        if (jugador == null) {
            return;
        }

        Vista.fichaJugador(jugador,
                buscarPorId(RUTA_SELECCIONES, aEntero(jugador[4]), CAMPOS_SELECCION),
                buscarPorId(RUTA_EQUIPOS, aEntero(jugador[5]), CAMPOS_EQUIPO));
    }

    static void seleccionDeJugador() {
        String[] jugador = seleccionarJugador();
        if (jugador == null) {
            return;
        }

        String[] seleccion = buscarPorId(RUTA_SELECCIONES, aEntero(jugador[4]), CAMPOS_SELECCION);
        if (seleccion == null) {
            Vista.mensaje("El jugador apunta a una selección que ya no existe (Id " + jugador[4] + ").");
            return;
        }

        int convocados = contarPorColumna(RUTA_JUGADORES, 4, aEntero(seleccion[0]), CAMPOS_JUGADOR);
        Vista.detalleSeleccion(jugador, seleccion, convocados);
    }

    static void equipoDeJugador() {
        String[] jugador = seleccionarJugador();
        if (jugador == null) {
            return;
        }

        String[] equipo = buscarPorId(RUTA_EQUIPOS, aEntero(jugador[5]), CAMPOS_EQUIPO);
        if (equipo == null) {
            Vista.mensaje("El jugador apunta a un equipo que ya no existe (Id " + jugador[5] + ").");
            return;
        }

        int plantilla = contarPorColumna(RUTA_JUGADORES, 5, aEntero(equipo[0]), CAMPOS_JUGADOR);
        Vista.detalleEquipo(jugador, equipo, plantilla);
    }

    static void partidosDeJugador() {
        String[] jugador = seleccionarJugador();
        if (jugador == null) {
            return;
        }

        int idSeleccion = aEntero(jugador[4]);
        String[] seleccion = buscarPorId(RUTA_SELECCIONES, idSeleccion, CAMPOS_SELECCION);
        if (seleccion == null) {
            Vista.mensaje("El jugador apunta a una selección que ya no existe.");
            return;
        }

        Vista.titulo("Partidos de " + seleccion[1] + " (selección de " + jugador[1] + " "
                + jugador[2] + "):");

        ArrayList<String> lineas = leerArchivo(RUTA_PARTIDOS);
        int contador = 0;
        int ganados = 0;
        int empatados = 0;
        int perdidos = 0;

        for (int i = 0; i < lineas.size(); i++) {
            String[] partido = separar(lineas.get(i), CAMPOS_PARTIDO);
            if (partido == null) {
                continue;
            }
            int idLocal = aEntero(partido[1]);
            int idVisitante = aEntero(partido[2]);
            if (idLocal != idSeleccion && idVisitante != idSeleccion) {
                continue;
            }

            contador++;
            mostrarPartido(partido);

            int propios = (idLocal == idSeleccion) ? aEntero(partido[3]) : aEntero(partido[4]);
            int rivales = (idLocal == idSeleccion) ? aEntero(partido[4]) : aEntero(partido[3]);
            if (propios > rivales) {
                ganados++;
            } else if (propios == rivales) {
                empatados++;
            } else {
                perdidos++;
            }
        }

        if (contador == 0) {
            Vista.mensaje("Esa selección no tiene partidos registrados.");
            return;
        }
        Vista.resumenPartidos(contador, ganados, empatados, perdidos);
    }

    static void jugadoresDeSeleccion() {
        String nombre = Vista.leerTexto("Nombre de la selección: ");

        String[] seleccion = buscarPorTexto(RUTA_SELECCIONES, 1, nombre, CAMPOS_SELECCION);
        if (seleccion == null) {
            Vista.mensaje("No se encontró ninguna selección que coincida con " + nombre);
            return;
        }

        Vista.titulo("Jugadores de " + seleccion[1] + " (Id de la selección: " + seleccion[0] + "):");
        listarJugadoresPorColumna(4, aEntero(seleccion[0]));
    }

    static void jugadoresDeEquipo() {
        String nombre = Vista.leerTexto("Nombre del equipo: ");

        String[] equipo = buscarPorTexto(RUTA_EQUIPOS, 1, nombre, CAMPOS_EQUIPO);
        if (equipo == null) {
            Vista.mensaje("No se encontró ningún equipo que coincida con " + nombre);
            return;
        }

        Vista.titulo("Plantilla de " + equipo[1] + " (" + equipo[4] + ") - Id del equipo: " + equipo[0]);
        listarJugadoresPorColumna(5, aEntero(equipo[0]));
    }

    static void listarJugadoresPorColumna(int columna, int valor) {
        ArrayList<String> lineas = leerArchivo(RUTA_JUGADORES);
        int contador = 0;

        for (int i = 0; i < lineas.size(); i++) {
            String[] jugador = separar(lineas.get(i), CAMPOS_JUGADOR);
            if (jugador == null) {
                continue;
            }
            if (aEntero(jugador[columna]) == valor) {
                contador++;
                mostrarJugador(jugador, contador == 1);
            }
        }

        Vista.totalJugadores(contador);
    }

    // Pide un jugador por nombre o apellido y deja que el usuario elija cuál.
    static String[] seleccionarJugador() {
        String busqueda = Vista.leerTexto("Nombre o apellido del jugador: ").toLowerCase();

        if (busqueda.isEmpty()) {
            Vista.mensaje("Debe ingresar un valor.");
            return null;
        }

        ArrayList<String> lineas = leerArchivo(RUTA_JUGADORES);
        ArrayList<String[]> encontrados = new ArrayList<String[]>();

        for (int i = 0; i < lineas.size(); i++) {
            String[] jugador = separar(lineas.get(i), CAMPOS_JUGADOR);
            if (jugador == null) {
                continue;
            }
            String completo = (jugador[1] + " " + jugador[2]).toLowerCase();
            if (completo.contains(busqueda)) {
                encontrados.add(jugador);
            }
        }

        if (encontrados.isEmpty()) {
            Vista.mensaje("No se encontró ningún jugador con ese nombre.");
            return null;
        }
        if (encontrados.size() == 1) {
            return encontrados.get(0);
        }

        Vista.mensaje("Se encontraron " + encontrados.size() + " jugadores:");
        for (int i = 0; i < encontrados.size(); i++) {
            mostrarJugador(encontrados.get(i), i == 0);
        }

        int id = Vista.leerEntero("Ingrese el Id del jugador que desea: ");

        for (int i = 0; i < encontrados.size(); i++) {
            if (aEntero(encontrados.get(i)[0]) == id) {
                return encontrados.get(i);
            }
        }

        Vista.mensaje("Ese Id no está dentro de los resultados.");
        return null;
    }

    // Puente hacia la Vista

    // Resuelve los ids del jugador y le pasa a la Vista los nombres ya listos.
    static void mostrarJugador(String[] jugador, boolean conEncabezado) {
        if (conEncabezado) {
            Vista.encabezadoJugador();
        }

        String[] seleccion = buscarPorId(RUTA_SELECCIONES, aEntero(jugador[4]), CAMPOS_SELECCION);
        String[] equipo = buscarPorId(RUTA_EQUIPOS, aEntero(jugador[5]), CAMPOS_EQUIPO);

        Vista.filaJugador(jugador,
                seleccion == null ? "(sin selección)" : seleccion[1],
                equipo == null ? "(sin equipo)" : equipo[1]);
    }

    // Resuelve las dos selecciones del partido antes de mandarlo a la Vista.
    static void mostrarPartido(String[] partido) {
        String[] local = buscarPorId(RUTA_SELECCIONES, aEntero(partido[1]), CAMPOS_SELECCION);
        String[] visitante = buscarPorId(RUTA_SELECCIONES, aEntero(partido[2]), CAMPOS_SELECCION);

        Vista.filaPartido(partido,
                local == null ? "(?)" : local[1],
                visitante == null ? "(?)" : visitante[1]);
    }

    // Acceso a archivos

    static ArrayList<String> leerArchivo(String ruta) {
        ArrayList<String> lineas = new ArrayList<String>();
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(ruta));
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    lineas.add(linea);
                }
            }
        } catch (IOException e) {
            Vista.mensaje("Error al leer " + ruta + ": " + e.getMessage());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                Vista.mensaje("Error al cerrar el archivo: " + e.getMessage());
            }
        }

        return lineas;
    }

    // Reescribe el archivo completo. Se usa después de actualizar o eliminar.
    static boolean guardarArchivo(String ruta, ArrayList<String> lineas) {
        BufferedWriter bw = null;

        try {
            bw = new BufferedWriter(new FileWriter(ruta));
            for (int i = 0; i < lineas.size(); i++) {
                bw.write(lineas.get(i));
                if (i < lineas.size() - 1) {
                    bw.newLine();
                }
            }
            return true;
        } catch (IOException e) {
            Vista.mensaje("Error al escribir " + ruta + ": " + e.getMessage());
            return false;
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                Vista.mensaje("Error al cerrar el archivo: " + e.getMessage());
            }
        }
    }

    static boolean agregarLinea(String ruta, String registro) {
        ArrayList<String> lineas = leerArchivo(ruta);
        lineas.add(registro);
        return guardarArchivo(ruta, lineas);
    }

    static boolean actualizarLinea(String ruta, int id, String registro, int totalCampos) {
        ArrayList<String> lineas = leerArchivo(ruta);

        for (int i = 0; i < lineas.size(); i++) {
            String[] campos = separar(lineas.get(i), totalCampos);
            if (campos == null) {
                continue;
            }
            if (aEntero(campos[0]) == id) {
                lineas.set(i, registro);
                return guardarArchivo(ruta, lineas);
            }
        }

        Vista.mensaje("No se encontró el registro con Id " + id);
        return false;
    }

    static boolean eliminarLinea(String ruta, int id, int totalCampos) {
        ArrayList<String> lineas = leerArchivo(ruta);

        for (int i = 0; i < lineas.size(); i++) {
            String[] campos = separar(lineas.get(i), totalCampos);
            if (campos == null) {
                continue;
            }
            if (aEntero(campos[0]) == id) {
                lineas.remove(i);
                return guardarArchivo(ruta, lineas);
            }
        }

        Vista.mensaje("No se encontró el registro con Id " + id);
        return false;
    }

    static String[] buscarPorId(String ruta, int id, int totalCampos) {
        return buscarPorColumna(ruta, 0, id, totalCampos);
    }

    static String[] buscarPorColumna(String ruta, int columna, int valor, int totalCampos) {
        ArrayList<String> lineas = leerArchivo(ruta);

        for (int i = 0; i < lineas.size(); i++) {
            String[] campos = separar(lineas.get(i), totalCampos);
            if (campos == null) {
                continue;
            }
            if (aEntero(campos[columna]) == valor) {
                return campos;
            }
        }

        return null;
    }

    // Devuelve el primer registro cuya columna contenga el texto buscado.
    static String[] buscarPorTexto(String ruta, int columna, String valor, int totalCampos) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }

        String busqueda = valor.trim().toLowerCase();
        ArrayList<String> lineas = leerArchivo(ruta);

        for (int i = 0; i < lineas.size(); i++) {
            String[] campos = separar(lineas.get(i), totalCampos);
            if (campos == null) {
                continue;
            }
            if (campos[columna].toLowerCase().contains(busqueda)) {
                return campos;
            }
        }

        return null;
    }

    static int contarPorColumna(String ruta, int columna, int valor, int totalCampos) {
        ArrayList<String> lineas = leerArchivo(ruta);
        int contador = 0;

        for (int i = 0; i < lineas.size(); i++) {
            String[] campos = separar(lineas.get(i), totalCampos);
            if (campos == null) {
                continue;
            }
            if (aEntero(campos[columna]) == valor) {
                contador++;
            }
        }

        return contador;
    }

    static boolean existeId(String ruta, int id, int totalCampos) {
        return buscarPorId(ruta, id, totalCampos) != null;
    }

    static boolean existeTexto(String ruta, int columna, String valor, int totalCampos) {
        ArrayList<String> lineas = leerArchivo(ruta);

        for (int i = 0; i < lineas.size(); i++) {
            String[] campos = separar(lineas.get(i), totalCampos);
            if (campos == null) {
                continue;
            }
            if (campos[columna].equalsIgnoreCase(valor.trim())) {
                return true;
            }
        }

        return false;
    }

    static int siguienteId(String ruta) {
        ArrayList<String> lineas = leerArchivo(ruta);
        int ultimo = 0;

        for (int i = 0; i < lineas.size(); i++) {
            String[] campos = lineas.get(i).split("\\" + SEPARADOR);
            int id = aEntero(campos[0]);
            if (id > ultimo) {
                ultimo = id;
            }
        }

        return ultimo + 1;
    }

    // Validaciones

    static boolean textoValido(String valor, String etiqueta) {
        if (valor == null || valor.isEmpty()) {
            Vista.mensaje("El campo " + etiqueta + " no puede estar vacío.");
            return false;
        }
        if (valor.contains(SEPARADOR)) {
            Vista.mensaje("El campo " + etiqueta + " no puede contener el caracter '" + SEPARADOR + "'.");
            return false;
        }
        return true;
    }

    static boolean edadValida(int edad) {
        if (edad < EDAD_MINIMA || edad > EDAD_MAXIMA) {
            Vista.mensaje("La edad debe estar entre " + EDAD_MINIMA + " y " + EDAD_MAXIMA + " años.");
            return false;
        }
        return true;
    }

    static boolean dorsalValido(int dorsal) {
        if (dorsal < DORSAL_MINIMO || dorsal > DORSAL_MAXIMO) {
            Vista.mensaje("El dorsal debe estar entre " + DORSAL_MINIMO + " y " + DORSAL_MAXIMO + ".");
            return false;
        }
        return true;
    }

    static boolean fundacionValida(int fundacion) {
        if (fundacion < 1850 || fundacion > 2030) {
            Vista.mensaje("El año de fundación debe estar entre 1850 y 2030.");
            return false;
        }
        return true;
    }

    static boolean fechaValida(String fecha) {
        if (fecha == null || fecha.length() != 10 || fecha.charAt(4) != '-' || fecha.charAt(7) != '-') {
            Vista.mensaje("La fecha debe tener el formato AAAA-MM-DD.");
            return false;
        }

        int anio = aEntero(fecha.substring(0, 4));
        int mes = aEntero(fecha.substring(5, 7));
        int dia = aEntero(fecha.substring(8, 10));

        if (anio < 1900 || mes < 1 || mes > 12 || dia < 1 || dia > 31) {
            Vista.mensaje("La fecha no es válida.");
            return false;
        }
        return true;
    }

    // Pide un id y verifica que exista en el archivo indicado. Retorna -1 si no existe.
    static int pedirIdExistente(String etiqueta, String ruta, int totalCampos) {
        int id = Vista.leerEntero("Id de " + etiqueta + ": ");

        if (!existeId(ruta, id, totalCampos)) {
            Vista.mensaje("No existe " + etiqueta + " con el Id " + id + ".");
            return -1;
        }
        return id;
    }

    // Utilidades

    // Une varios valores con el separador "|".
    static String unir(Object... valores) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < valores.length; i++) {
            if (i > 0) {
                sb.append(SEPARADOR);
            }
            sb.append(valores[i]);
        }
        return sb.toString();
    }

    // Separa una línea en sus campos. Retorna null si la línea está incompleta.
    static String[] separar(String linea, int totalCampos) {
        if (linea == null || linea.trim().isEmpty()) {
            return null;
        }
        String[] campos = linea.split("\\" + SEPARADOR);
        if (campos.length < totalCampos) {
            return null;
        }
        return campos;
    }

    // Convierte texto a entero. Retorna -1 si el texto no es un número válido.
    static int aEntero(String valor) {
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
