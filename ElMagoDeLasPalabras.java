import java.util.*;
import java.util.stream.Collectors;

public class ElMagoDeLasPalabras {
    private static final Scanner scanner = new Scanner(System.in);
    private List<Jugador> jugadores;
    private Diccionario diccionario;
    private HashSet<String> palabrasUsadasGlobal;
    private Random random;
    private boolean modoExperto; // Indica si el juego se ejecuta en modo experto

    public ElMagoDeLasPalabras() {
        jugadores = new ArrayList<>();
        diccionario = new Diccionario();
        palabrasUsadasGlobal = new HashSet<>();
        random = new Random();
    }

    public void iniciarJuego() {
        System.out.println("Bienvenido a El Mago de las Palabras!");

        seleccionarModo(); // Pregunta al usuario por el modo de juego
        configurarJugadores(); // Configura la cantidad de jugadores y sus nombres
        jugarRondas(); // Ejecuta las 3 rondas del juego
        mostrarGanador(); // Determina y muestra al jugador con más puntos
    }

    private void seleccionarModo() {
        System.out.print("Selecciona modo de juego (1 = Normal, 2 = Experto): ");
        int opcion = scanner.nextInt();
        scanner.nextLine();
        modoExperto = (opcion == 2);

        if (modoExperto) {
            System.out.println("\n--- MODO EXPERTO ACTIVADO ---");
            System.out.println("- Se generan solo 3 vocales por conjunto de letras.");
            System.out.println("- Las vocales valen 3 puntos y las consonantes 5.");
            System.out.println("- Las palabras inválidas restan 10 puntos.\n");
        }
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
            palabrasUsadasGlobal.clear(); // Limpiar palabras de rondas anteriores

            List<Character> letrasCompartidas = generarLetrasCompartidas();

            // Asignar las mismas letras a todos los jugadores
            for (Jugador jugador : jugadores) {
                jugador.setLetras(new ArrayList<>(letrasCompartidas));
            }

            boolean continuar = true;
            while (continuar) {
                continuar = false; // Asume que nadie jugará hasta que alguien lo haga
                for (Jugador jugador : jugadores) {
                    System.out.println("\nTurno de " + jugador.getNombre());
                    System.out.println("Letras: " + jugador.getLetras());
                    System.out.println("Palabras ya adivinadas: " + palabrasUsadasGlobal);
                    System.out.print("Escribe una palabra (o presiona ENTER para pasar): ");
                    String palabra = scanner.nextLine().toLowerCase();

                    if (palabra.isEmpty()) continue;

                    if (palabrasUsadasGlobal.contains(palabra)) {
                        System.out.println("¡Esa palabra ya fue usada!");
                        continue;
                    }

                    if (!puedeFormarPalabra(palabra, jugador.getLetras())) {
                        System.out.println("¡No puedes formar esa palabra con las letras disponibles!");
                        jugador.agregarPuntaje(modoExperto ? -10 : -5); // Penalización
                        continue;
                    }

                    if (diccionario.esValida(palabra)) {
                        int puntos = calcularPuntos(palabra);
                        jugador.agregarPuntaje(puntos);
                        jugador.agregarPalabra(palabra);
                        palabrasUsadasGlobal.add(palabra);
                        System.out.println("¡Palabra válida! +" + puntos + " puntos.");
                    } else {
                        jugador.agregarPuntaje(modoExperto ? -10 : -5);
                        System.out.println("¡Palabra inválida! " + (modoExperto ? "-10" : "-5") + " puntos.");
                    }

                    continuar = true; // Al menos un jugador jugó
                }
            }

            // Mostrar resumen de la ronda actual
            System.out.println("\n--- Resumen de la ronda ---");
            for (Jugador jugador : jugadores) {
                System.out.println(jugador.getNombre() + " | Puntos: " + jugador.getPuntaje() + " | Palabras: " + jugador.getPalabrasUsadas());
            }
        }
    }

    private void mostrarGanador() {
        // Uso de lambda para obtener el jugador con más puntos
        Jugador ganador = Collections.max(jugadores, Comparator.comparingInt(Jugador::getPuntaje));
        System.out.println("\n¡El ganador es " + ganador.getNombre() + " con " + ganador.getPuntaje() + " puntos!");
    }

    private List<Character> generarLetrasCompartidas() {
        List<Character> letras = new ArrayList<>();
        String vocales = "aeiou";
        String consonantes = "lnrstgmpbcdfhjkvz";

        int cantidadVocales = modoExperto ? 3 : (4 + random.nextInt(2)); // 3 en experto, 4 o 5 en normal

        for (int i = 0; i < cantidadVocales; i++) {
            letras.add(vocales.charAt(random.nextInt(vocales.length())));
        }

        while (letras.size() < 10) {
            letras.add(consonantes.charAt(random.nextInt(consonantes.length())));
        }

        Collections.shuffle(letras); // Mezclar las letras
        return letras;
    }

    private int calcularPuntos(String palabra) {
        int puntos = 0;
        for (char c : palabra.toCharArray()) {
            if ("aeiou".indexOf(c) >= 0) {
                puntos += modoExperto ? 3 : 5; // Puntaje según modo
            } else {
                puntos += modoExperto ? 5 : 3;
            }
        }
        return puntos;
    }

    private boolean puedeFormarPalabra(String palabra, List<Character> letrasDisponibles) {
        // Crear mapa de frecuencia para la palabra ingresada
        Map<Character, Long> conteoPalabra = palabra.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

        // Crear mapa de frecuencia para las letras disponibles
        Map<Character, Long> conteoDisponibles = letrasDisponibles.stream()
                .collect(Collectors.groupingBy(c -> c, Collectors.counting()));

        // Verificar que cada letra requerida esté disponible en cantidad suficiente
        return conteoPalabra.entrySet().stream()
                .allMatch(entry -> conteoDisponibles.getOrDefault(entry.getKey(), 0L) >= entry.getValue());
    }

    public static void main(String[] args) {
        ElMagoDeLasPalabras juego = new ElMagoDeLasPalabras();
        juego.iniciarJuego();
    }
}