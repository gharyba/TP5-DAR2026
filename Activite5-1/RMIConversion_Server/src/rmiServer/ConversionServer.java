package rmiServer;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import rmiService.ConversionImpl;

public class ConversionServer {
    public static void main(String[] args) {
        try {
            // Création de l'annuaire RMI sur le port habituel.
            Registry registre = LocateRegistry.createRegistry(1099);
            registre.rebind("conversion", new ConversionImpl());
            System.out.println("Service de conversion disponible sur le port 1099.");
        } catch (RemoteException e) {
            System.err.println("Impossible de démarrer le service de conversion : " + e.getMessage());
        }
    }
}
