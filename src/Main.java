import estructurasdedatos.Paginador;
import estructurasdedatos.Paginas;
import java.io.*;
import java.util.*;
import patronlectoredactor.Lector;
import patronlectoredactor.Redactor;


public class Main {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        Boolean flag = true;

        while (flag) {
            System.out.println("1. Generar Referencias");
            System.out.println("2. Simular Paginación");
            System.out.println("3. Salir");
            int opcion = sc.nextInt();
            sc.nextLine(); 

            if (opcion == 1) {
                System.out.print("Tamaño de página (bytes): ");
                int tamañoPagina = sc.nextInt();
                sc.nextLine();
                System.out.println("Copy paste el nombre del archivo BMP");
                System.out.println("Ejemplos: caso2-parrotspeq_sal.bmp    caso2-parrotspeq.bmp");
                System.out.print("Nombre del archivo BMP: ");
                String nombreArchivo = sc.nextLine();

                GeneradorReferencias gen = new GeneradorReferencias(tamañoPagina, nombreArchivo);
                gen.generarReferencias();
                System.out.println("Referencias generadas en referencias.txt");
            } else if (opcion == 2) {
                System.out.print("Número de marcos: ");
                int numMarcos = sc.nextInt();
                sc.nextLine();
                System.out.println("Copy paste el nombre del archivo de referencias:");
                System.out.println("Ejemplo: referencias.txt");
                System.out.print("Nombre del archivo de referencias: ");
                String nombreArchivoReferencias = sc.nextLine();

                
                BufferedReader br = new BufferedReader(new FileReader(nombreArchivoReferencias));
                int tp = Integer.parseInt(br.readLine().split("=")[1]);
                int nf = Integer.parseInt(br.readLine().split("=")[1]);
                int nc = Integer.parseInt(br.readLine().split("=")[1]);
                int nr = Integer.parseInt(br.readLine().split("=")[1]);
                int np = Integer.parseInt(br.readLine().split("=")[1]);
                List<String> referencias = new ArrayList<>();
                String linea;
                while ((linea = br.readLine()) != null) {
                    referencias.add(linea);
                }
                br.close();

                
                Paginas tablaPaginas = new Paginas(np);
                Paginador Paginador = new Paginador(numMarcos, tablaPaginas);

                
                Lector lector = new Lector(referencias, tablaPaginas, Paginador);
                Redactor redactor = new Redactor(tablaPaginas);
                lector.start();
                redactor.start();

                
                try {
                    lector.join();
                    redactor.detener();
                    redactor.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int hits = lector.getHits();
                int misses = lector.getMisses();
                double porcentajeHits = (hits / (double) nr) * 100;
                long tiempoReal = hits * 50L + misses * 10_000_000L; 
                long tiempoTodosHits = nr * 50L; 
                long tiempoTodosMisses = nr * 10_000_000L; 
                System.out.printf("Hits: %d, Misses: %d, Porcentaje Hits: %.2f%%\n", hits, misses, porcentajeHits);
                System.out.printf("Tiempo Real: %d ns, Todos Hits: %d ns, Todos Misses: %d ns\n",
                        tiempoReal, tiempoTodosHits, tiempoTodosMisses);
            } else if (opcion == 3) {
                flag = false;
            } else {
                System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
        sc.close();
    }
}