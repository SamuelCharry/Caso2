import cosasdefault.Imagen;
import java.io.*;
import java.util.*;

public class GeneradorReferencias {

    private int tamanoPagina;
    private String archivoImagen;

    public GeneradorReferencias(int tamanoPagina, String archivoImagen) {
        this.tamanoPagina = tamanoPagina;
        this.archivoImagen = archivoImagen;
    }

    public void generarReferencias() throws IOException {
        Imagen imagen = new Imagen(archivoImagen);
        int filas = imagen.getAlto();
        int columnas = imagen.getAncho();

        long tamanoImagenBytes = (long) filas * columnas * 3;
        long tamanoFiltrosBytes = 3 * 3 * 4 * 2L;
        long tamanoRespuestaBytes = tamanoImagenBytes;
        long bytesTotales = tamanoImagenBytes + tamanoFiltrosBytes + tamanoRespuestaBytes;
        int paginasTotales = (int) Math.ceil(bytesTotales / (double) tamanoPagina);

        long offsetImagen = 0;
        long offsetFiltroX = tamanoImagenBytes;
        long offsetFiltroY = offsetFiltroX + 3 * 3 * 4;
        long offsetRespuesta = offsetFiltroY + 3 * 3 * 4;

        List<String> referencias = new ArrayList<>();

        for (int fila = 1; fila < filas - 1; fila++) {
            for (int col = 1; col < columnas - 1; col++) {
                procesarVecindad(fila, col, filas, columnas, offsetImagen, offsetFiltroX, offsetFiltroY, offsetRespuesta, referencias);

            }
        }

        escribirReferencias(filas, columnas, paginasTotales, referencias);
    }

    private void procesarVecindad(int fila, int col, int filas, int columnas, long offsetImagen, long offsetFiltroX, long offsetFiltroY, long offsetRespuesta, List<String> referencias) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int vecinoFila = fila + dx;
                int vecinoCol = col + dy;

                for (int canal = 0; canal < 3; canal++) {
                    long posicion = ((long) vecinoFila * columnas + vecinoCol) * 3 + canal + offsetImagen;
                    agregarReferencia(posicion, "Imagen[" + vecinoFila + "][" + vecinoCol + "]." + canalRGB(canal), referencias);
                }
                int fx = dx + 1;
                int fy = dy + 1;
                agregarReferencia(offsetFiltroX + (fx * 3 + fy) * 4, "SOBEL_X[" + fx + "][" + fy + "]", referencias);
                agregarReferencia(offsetFiltroY + (fx * 3 + fy) * 4, "SOBEL_Y[" + fx + "][" + fy + "]", referencias);
            }
        }
        for (int canal = 0; canal < 3; canal++) {
            long posicion = ((long) fila * columnas + col) * 3 + canal + offsetRespuesta;
            agregarReferencia(posicion, "Rta[" + fila + "][" + col + "]." + canalRGB(canal), referencias, true);
        }
    }

    private void agregarReferencia(long posicion, String etiqueta, List<String> referencias) {
        agregarReferencia(posicion, etiqueta, referencias, false);
    }

    private void agregarReferencia(long posicion, String etiqueta, List<String> referencias, boolean esEscritura) {
        int pagina = (int) (posicion / tamanoPagina);
        int desplazamiento = (int) (posicion % tamanoPagina);
        referencias.add(String.format("%s, %d, %d, %s", etiqueta, pagina, desplazamiento, esEscritura ? "W" : "R"));
    }

    private String canalRGB(int canal) {
        if (canal == 0) return "r";
        if (canal == 1) return "g";
        return "b";
    }

    private void escribirReferencias(int filas, int columnas, int paginasTotales, List<String> referencias) throws IOException {
        try (PrintWriter writer = new PrintWriter("referencias.txt")) {
            writer.println("TP=" + tamanoPagina);
            writer.println("NF=" + filas);
            writer.println("NC=" + columnas);
            writer.println("NR=" + referencias.size());
            writer.println("NP=" + paginasTotales);
            for (String ref : referencias) {
                writer.println(ref);
            }
        }
    }
}
