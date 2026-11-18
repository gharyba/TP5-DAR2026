package rmiClient;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import rmiService.IConversion;

public class ConversionClient {
    public static void main(String[] args) {
        try {
            System.out.println("Connexion au service RMI...");
            Registry registre = LocateRegistry.getRegistry("localhost", 1099);
            IConversion conversion = (IConversion) registre.lookup("conversion");
            double montant = 500;
            double resultat = conversion.convertirMontant(montant);
            System.out.println("Montant en euros : " + montant);
            System.out.println("Montant converti en dinars : " + resultat);
        } catch (RemoteException | NotBoundException e) {
            System.err.println("Service de conversion indisponible : " + e.getMessage());
        }
    }
}
