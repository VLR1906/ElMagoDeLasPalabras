import java.util.*;

public class ElMagoDeLasPalabras {
    private static final Scanner scanner = new Scanner(System.in);
    private List<Jugador> jugadores;
    private Diccionario diccionario;
    private HashSet<String> palabrasUsadasGlobal;
    private Random random;

    public ElMagoDeLasPalabras() {
        jugadores = new ArrayList<>();
        diccionario = new Diccionario();
        palabrasUsadasGlobal = new HashSet<>();
        random = new Random();
    }

    public void iniciarJuego() {
        System.out.println("Bienvenido a El Mago de las Palabras!");
        configurarJugadores();
        jugarRondas();
        mostrarGanador();
    }

    private void configurarJugadores() {
        int cantidad;
        do {
            System.out.print("Ingresa la cantidad de jugadores (2 a 4): ");
            cantidad = scanner.nextInt();
            scanner.nextLine();
        } while (cantidad < 2 || cantidad > 4);

        for (int i = 0; i < cantidad; i++) {
            System.out.print("Nombre del jugador " + (i + 1) + ": ");
            String nombre = scanner.nextLine();
            jugadores.add(new Jugador(nombre));
        }
    }

    private void jugarRondas() {
        for (int ronda = 1; ronda <= 3; ronda++) {
            System.out.println("\n--- RONDA " + ronda + " ---");
            palabrasUsadasGlobal.clear();
            for (Jugador jugador : jugadores) {
                asignarLetras(jugador);
            }

            boolean continuar = true;
            while (continuar) {
                continuar = false;
                for (Jugador jugador : jugadores) {
                    System.out.println("\nTurno de " + jugador.getNombre());
                    System.out.println("Tus letras: " + jugador.getLetras());
                    System.out.print("Escribe una palabra (o presiona ENTER para pasar): ");
                    String palabra = scanner.nextLine().toLowerCase();

                    if (palabra.isEmpty()) continue;

                    if (palabrasUsadasGlobal.contains(palabra)) {
                        System.out.println("¡Esa palabra ya fue usada!");
                        continue;
                    }

                    if (!puedeFormarPalabra(palabra, jugador.getLetras())) {
                        System.out.println("¡No puedes formar esa palabra con tus letras!");
                        jugador.agregarPuntaje(-5);
                        continue;
                    }

                    if (diccionario.esValida(palabra)) {
                        int puntos = calcularPuntos(palabra);
                        jugador.agregarPuntaje(puntos);
                        jugador.agregarPalabra(palabra);
                        palabrasUsadasGlobal.add(palabra);
                        System.out.println("¡Palabra válida! +" + puntos + " puntos.");
                    } else {
                        jugador.agregarPuntaje(-5);
                        System.out.println("¡Palabra inválida! -5 puntos.");
                    }

                    continuar = true;
                }
            }

            // Mostrar resumen de ronda
            System.out.println("\n--- Resumen de la ronda ---");
            for (Jugador jugador : jugadores) {
                System.out.println(jugador.getNombre() + " | Puntos: " + jugador.getPuntaje() + " | Palabras: " + jugador.getPalabrasUsadas());
            }
        }
    }

    private void mostrarGanador() {
        Jugador ganador = jugadores.get(0);
        for (Jugador jugador : jugadores) {
            if (jugador.getPuntaje() > ganador.getPuntaje()) {
                ganador = jugador;
            }
        }

        System.out.println("\n¡El ganador es " + ganador.getNombre() + " con " + ganador.getPuntaje() + " puntos!");
    }

    private void asignarLetras(Jugador jugador) {
        List<Character> letras = new ArrayList<>();
        String letrasDisponibles = "aeioulnrstgmpbcdfhjkvz";
        for (int i = 0; i < 10; i++) {
            letras.add(letrasDisponibles.charAt(random.nextInt(letrasDisponibles.length())));
        }
        jugador.setLetras(letras);
    }

    private int calcularPuntos(String palabra) {
        int puntos = 0;
        for (char c : palabra.toCharArray()) {
            if ("aeiou".indexOf(c) >= 0) {
                puntos += 5;
            } else {
                puntos += 3;
            }
        }
        return puntos;
    }

    private boolean puedeFormarPalabra(String palabra, List<Character> letrasDisponibles) {
        List<Character> copia = new ArrayList<>(letrasDisponibles);
        for (char c : palabra.toCharArray()) {
            if (!copia.remove((Character) c)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        ElMagoDeLasPalabras juego = new ElMagoDeLasPalabras();
        juego.iniciarJuego();
    }
}
