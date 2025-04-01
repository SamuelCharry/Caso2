package estructurasdedatos;
import java.util.*;

public class Paginador {
    private List<Marco> marcos;
    private Paginas tablaPaginas;

    public Paginador(int numMarcos, Paginas tabla) {
        marcos = new ArrayList<>();
        for (int i = 0; i < numMarcos; i++) {
            marcos.add(new Marco(i));
        }
        this.tablaPaginas = tabla;
    }

    public synchronized int asignarMarco(Pagina pagina) {
        for (Marco m : marcos) {
            if (m.getPaginaAsignada() == null) {
                m.setPaginaAsignada(pagina);
                pagina.setEnRAM(true);
                pagina.setMarcoAsignado(m.getNumeroMarco());
                return m.getNumeroMarco();
            }
        }
        return -1;
    }

    public synchronized void reemplazarPagina(Pagina nuevaPagina) {
        List<Pagina> enRAM = tablaPaginas.getPaginasEnRAM();
        Pagina victima = null;

        for (Pagina p : enRAM) {
            if (!p.isBitReferencia() && !p.isBitModificacion() && victima == null) {
                victima = p;
            }
        }

        if (victima == null) {
            for (Pagina p : enRAM) {
                if (!p.isBitReferencia() && p.isBitModificacion() && victima == null) {
                    victima = p;
                }
            }
        }

        if (victima == null) {
            for (Pagina p : enRAM) {
                if (p.isBitReferencia() && !p.isBitModificacion() && victima == null) {
                    victima = p;
                }
            }
        }

        if (victima == null && !enRAM.isEmpty()) {
            victima = enRAM.get(0);
        }

        if (victima != null) {
            Marco marco = marcos.get(victima.getMarcoAsignado());
            marco.setPaginaAsignada(nuevaPagina);
            victima.setEnRAM(false);
            victima.setMarcoAsignado(-1);
            nuevaPagina.setEnRAM(true);
            nuevaPagina.setMarcoAsignado(marco.getNumeroMarco());
        }
    }
} 
