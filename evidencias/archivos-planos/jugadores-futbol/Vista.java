import java.util.Scanner;

public class Vista {

    static Scanner sc = new Scanner(System.in);

    // Menús

    static int menuPrincipal() {
        System.out.println("\n--- MUNDIAL DE FUTBOL ---");
        System.out.println("1. Jugadores");
        System.out.println("2. Selecciones");
        System.out.println("3. Equipos");
        System.out.println("4. Partidos");
        System.out.println("5. Busquedas relacionadas");
        System.out.println("6. Salir");
        return leerEntero("Seleccione una opción: ");
    }

    // Menú
    static int menuCrud(String entidad) {
        System.out.println("\n--- " + entidad + " ---");
        System.out.println("1. Crear");
        System.out.println("2. Actualizar");
        System.out.println("3. Eliminar");
        System.out.println("4. Volver");
        return leerEntero("Opción: ");
    }

    static int menuRelacionadas() {
        System.out.println("\n--- BUSQUEDAS RELACIONADAS ---");
        System.out.println("1. Ver la ficha completa de un jugador");
        System.out.println("2. Buscar la selección a partir de un jugador");
        System.out.println("3. Buscar el equipo a partir de un jugador");
        System.out.println("4. Buscar los partidos de la selección de un jugador");
        System.out.println("5. Listar los jugadores de una selección");
        System.out.println("6. Listar los jugadores de un equipo");
        System.out.println("7. Volver");
        return leerEntero("Opción: ");
    }

    // Entrada

    static String leerTexto(String etiqueta) {
        System.out.print(etiqueta);
        return sc.nextLine().trim();
    }

    static int leerEntero(String etiqueta) {
        System.out.print(etiqueta);

        while (true) {
            if (!sc.hasNextLine()) {
                System.out.println("\nNo hay más entrada disponible. Cerrando el programa.");
                sc.close();
                System.exit(0);
            }
            String entrada = sc.nextLine().trim();
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.print("Debe ingresar un número entero: ");
            }
        }
    }

    // Si el usuario deja el campo vacío se conserva el valor actual.
    static String pedirTextoOpcional(String etiqueta, String actual) {
        System.out.print(etiqueta + " [" + actual + "]: ");
        String entrada = sc.nextLine().trim();
        return entrada.isEmpty() ? actual : entrada;
    }

    static int pedirEnteroOpcional(String etiqueta, int actual) {
        while (true) {
            System.out.print(etiqueta + " [" + actual + "]: ");
            String entrada = sc.nextLine().trim();
            if (entrada.isEmpty()) {
                return actual;
            }
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número entero.");
            }
        }
    }

    static boolean confirmar() {
        System.out.print("¿Confirma la operación? (s/n): ");
        String respuesta = sc.nextLine().trim().toLowerCase();
        return respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí");
    }

    static void cerrar() {
        sc.close();
    }

    // Mensajes

    static void mensaje(String texto) {
        System.out.println(texto);
    }

    static void titulo(String texto) {
        System.out.println("\n" + texto);
    }

    static void registroActual(String[] campos) {
        System.out.println("Registro actual: " + String.join(" | ", campos));
    }

    static void avisoCamposOpcionales() {
        System.out.println("Deje vacío el campo para conservar el valor actual.");
    }

    // Tabla de jugadores

    static void encabezadoJugador() {
        System.out.println();
        System.out.printf("%-6s %-14s %-16s %-5s %-18s %-22s %-6s%n",
                "ID", "NOMBRE", "APELLIDO", "EDAD", "SELECCION", "EQUIPO", "DORSAL");
        System.out.println(linea(92));
    }

    // El nombre de la selección y del equipo ya vienen resueltos desde App.
    static void filaJugador(String[] jugador, String seleccion, String equipo) {
        System.out.printf("%-6s %-14s %-16s %-5s %-18s %-22s %-6s%n",
                jugador[0], recortar(jugador[1], 13), recortar(jugador[2], 15), jugador[3],
                recortar(seleccion, 17), recortar(equipo, 21), jugador[6]);
    }

    static void totalJugadores(int total) {
        System.out.println("Total de jugadores: " + total);
    }

    // Partidos

    static void filaPartido(String[] partido, String local, String visitante) {
        System.out.printf("[%s] %-12s %s - %s  %-12s | %s | %s | %s%n",
                partido[0], local, partido[3], partido[4], visitante,
                partido[5], partido[6], partido[7]);
    }

    static void resumenPartidos(int total, int ganados, int empatados, int perdidos) {
        System.out.println("Total: " + total + " partidos | Ganados: " + ganados
                + " | Empatados: " + empatados + " | Perdidos: " + perdidos);
    }

    // Fichas

    static void fichaJugador(String[] jugador, String[] seleccion, String[] equipo) {
        System.out.println("\n--- FICHA DEL JUGADOR ---");
        System.out.println("Id:        " + jugador[0]);
        System.out.println("Nombre:    " + jugador[1] + " " + jugador[2]);
        System.out.println("Edad:      " + jugador[3] + " años");
        System.out.println("Dorsal:    " + jugador[6]);

        if (seleccion != null) {
            System.out.println("Selección: [Id " + seleccion[0] + "] " + seleccion[1] + " ("
                    + seleccion[2] + ", ranking " + seleccion[3] + ", " + seleccion[4] + " títulos)");
        }
        if (equipo != null) {
            System.out.println("Equipo:    [Id " + equipo[0] + "] " + equipo[1] + " - " + equipo[4]
                    + " (" + equipo[3] + ", " + equipo[2] + ")");
        }
    }

    static void detalleSeleccion(String[] jugador, String[] seleccion, int convocados) {
        System.out.println("\n" + jugador[1] + " " + jugador[2] + " juega para:");
        System.out.println("Id:            " + seleccion[0]);
        System.out.println("Selección:     " + seleccion[1]);
        System.out.println("Confederación: " + seleccion[2]);
        System.out.println("Ranking FIFA:  " + seleccion[3]);
        System.out.println("Títulos:       " + seleccion[4]);
        System.out.println("Convocados:    " + convocados + " jugadores");
    }

    static void detalleEquipo(String[] jugador, String[] equipo, int plantilla) {
        System.out.println("\n" + jugador[1] + " " + jugador[2] + " milita en:");
        System.out.println("Id:        " + equipo[0]);
        System.out.println("Equipo:    " + equipo[1]);
        System.out.println("País:      " + equipo[2]);
        System.out.println("Ciudad:    " + equipo[3]);
        System.out.println("Liga:      " + equipo[4]);
        System.out.println("Fundación: " + equipo[5]);
        System.out.println("Plantilla: " + plantilla + " jugadores");
    }

    // Utilidades

    static String recortar(String texto, int maximo) {
        if (texto == null) {
            return "";
        }
        if (texto.length() <= maximo) {
            return texto;
        }
        return texto.substring(0, maximo - 1) + ".";
    }

    static String linea(int largo) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < largo; i++) {
            sb.append("-");
        }
        return sb.toString();
    }
}
