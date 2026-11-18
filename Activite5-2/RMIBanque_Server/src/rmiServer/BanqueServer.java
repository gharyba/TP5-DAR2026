package rmiServer;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import rmiService.BanqueImpl;

public class BanqueServer {
    public static void main(String[] args) {
        try {
            // Le registre RMI reste l'annuaire utilisé par JNDI.
            LocateRegistry.createRegistry(1099);
            BanqueImpl banque = new BanqueImpl();
            Context contexte = new InitialContext();
            contexte.rebind("banque", banque);
            contexte.close();
            System.out.println("Service bancaire RMI et JNDI disponible sur le port 1099.");
        } catch (RemoteException | NamingException e) {
            System.err.println("Impossible de démarrer le service bancaire : " + e.getMessage());
        }
    }
}
