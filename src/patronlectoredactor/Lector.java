package patronlectoredactor;

import estructurasdedatos.Pagina;
import estructurasdedatos.Paginador;
import estructurasdedatos.Paginas;
import java.util.*;

public class Lector extends Thread {
    private List<String> referencias;
    private Paginas tablaPaginas;
    private Paginador gestorMemoria;
    private int contadorHits = 0;
    private int contadorMisses = 0;

    public Lector(List<String> refs, Paginas tabla, Paginador gestor) {
        this.referencias = refs;
        this.tablaPaginas = tabla;
        this.gestorMemoria = gestor;
    }

    @Override
    public void run() {
        int procesadas = 0;
        for (String ref : referencias) {
            String[] partes = ref.split(", ");
            int paginaVirtual = Integer.parseInt(partes[1]);
            boolean esEscritura = partes[3].equals("W");

            synchronized (tablaPaginas) {
                Pagina pagina = tablaPaginas.getPagina(paginaVirtual);
                if (pagina.isEnRAM()) {
                    contadorHits++;
                    tablaPaginas.actualizarBits(paginaVirtual, true, esEscritura);
                } else {
                    contadorMisses++;
                    int marco = gestorMemoria.asignarMarco(pagina);
                    if (marco == -1) {
                        gestorMemoria.reemplazarPagina(pagina);
                    }
                    tablaPaginas.actualizarBits(paginaVirtual, true, esEscritura);
                }
            }

            procesadas++;
            if (procesadas % 10000 == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getHits() { return contadorHits; }
    public int getMisses() { return contadorMisses; }
}