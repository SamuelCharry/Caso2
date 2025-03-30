import cosasdefault.Imagen;
import java.io.*;
import java.util.*;

public class GeneradorReferencias {

    private int tamañoPagina;
    private String nombreArchivoImagen;

    public GeneradorReferencias(int tamañoPagina, String nombreArchivoImagen) {
        this.tamañoPagina = tamañoPagina;
        this.nombreArchivoImagen = nombreArchivoImagen;
    }

    public void generarReferencias() throws IOException {
        Imagen img = new Imagen(nombreArchivoImagen);
        int alto = img.getAlto();
        int ancho = img.getAncho();

        long tamañoImagen = (long) alto * ancho * 3;
        long tamañoFiltros = 3 * 3 * 4 * 2L;
        long tamañoRespuesta = tamañoImagen;
        long totalBytes = tamañoImagen + tamañoFiltros + tamañoRespuesta;
        int numPaginas = (int) Math.ceil(totalBytes / (double) tamañoPagina);

        long offsetImagen = 0;
        long offsetFiltroX = tamañoImagen;
        long offsetFiltroY = offsetFiltroX + 3 * 3 * 4;
        long offsetRespuesta = offsetFiltroY + 3 * 3 * 4;

        List<String> referencias = new ArrayList<>();
        for (int i = 1; i < alto - 1; i++) {
            for (int j = 1; j < ancho - 1; j++) {
                for (int m = -1; m <= 1; m++) {
                    for (int n = -1; n <= 1; n++) {
                        for (int k = 0; k < 3; k++) {
                            long pos = ((i + m) * ancho + (j + n)) * 3 + k + offsetImagen;
                            int pagina = (int) (pos / tamañoPagina);
                            int desplazamiento = (int) (pos % tamañoPagina);
                            referencias.add(String.format("Imagen[%d][%d].%s, %d, %d, R",
                                    i + m, j + n, k == 0 ? "r" : k == 1 ? "g" : "b", pagina, desplazamiento));
                        }
                        int fm = m + 1, fn = n + 1;
                        long posX = (fm * 3 + fn) * 4 + offsetFiltroX;
                        int paginaX = (int) (posX / tamañoPagina);
                        int despX = (int) (posX % tamañoPagina);
                        referencias.add(String.format("SOBEL_X[%d][%d], %d, %d, R", fm, fn, paginaX, despX));
                        referencias.add(String.format("SOBEL_X[%d][%d], %d, %d, R", fm, fn, paginaX, despX));
                        referencias.add(String.format("SOBEL_X[%d][%d], %d, %d, R", fm, fn, paginaX, despX));

                        long posY = (fm * 3 + fn) * 4 + offsetFiltroY;
                        int paginaY = (int) (posY / tamañoPagina);
                        int despY = (int) (posY % tamañoPagina);
                        referencias.add(String.format("SOBEL_Y[%d][%d], %d, %d, R", fm, fn, paginaY, despY));
                        referencias.add(String.format("SOBEL_Y[%d][%d], %d, %d, R", fm, fn, paginaY, despY));
                        referencias.add(String.format("SOBEL_Y[%d][%d], %d, %d, R", fm, fn, paginaY, despY));
                    }
                }
                for (int k = 0; k < 3; k++) {
                    long pos = (i * ancho + j) * 3 + k + offsetRespuesta;
                    int pagina = (int) (pos / tamañoPagina);
                    int desplazamiento = (int) (pos % tamañoPagina);
                    referencias.add(String.format("Rta[%d][%d].%s, %d, %d, W",
                            i, j, k == 0 ? "r" : k == 1 ? "g" : "b", pagina, desplazamiento));
                }
            }
        }

        try (PrintWriter pw = new PrintWriter("referencias.txt")) {
            pw.println("TP=" + tamañoPagina);
            pw.println("NF=" + alto);
            pw.println("NC=" + ancho);
            pw.println("NR=" + referencias.size());
            pw.println("NP=" + numPaginas);
            for (String ref : referencias) {
                pw.println(ref);
            }
        }
    }
}
