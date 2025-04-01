package patronlectoredactor;

import estructurasdedatos.Pagina;
import estructurasdedatos.Paginas;

public class Redactor extends Thread {

    private Paginas tablaPaginas;
    private volatile boolean running = true;

    public Redactor(Paginas tabla) {
        this.tablaPaginas = tabla;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(1);
                synchronized (tablaPaginas) {
                    for (Pagina p : tablaPaginas.getPaginasEnRAM()) {
                        p.setBitReferencia(false);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void detener() {
        running = false;
    }
} 
