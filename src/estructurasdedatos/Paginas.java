package estructurasdedatos;
import java.util.*;

public class Paginas {
    private List<Pagina> paginas;

    public Paginas(int numPaginas) {
        paginas = new ArrayList<>();
        for (int i = 0; i < numPaginas; i++) {
            paginas.add(new Pagina(i));
        }
    }

    public synchronized Pagina getPagina(int numeroPagina) {
        return paginas.get(numeroPagina);
    }

    public synchronized void actualizarBits(int numeroPagina, boolean ref, boolean mod) {
        Pagina p = paginas.get(numeroPagina);
        p.setBitReferencia(ref);
        p.setBitModificacion(mod);
    }

    public synchronized List<Pagina> getPaginasEnRAM() {
        List<Pagina> enRAM = new ArrayList<>();
        for (Pagina p : paginas) {
            if (p.isEnRAM()) {
                enRAM.add(p);
            }
        }
        return enRAM;
    }
}
