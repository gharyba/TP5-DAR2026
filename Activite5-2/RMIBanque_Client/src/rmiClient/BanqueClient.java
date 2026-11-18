package rmiClient;

import java.rmi.RemoteException;
import java.util.Date;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import metier.Compte;
import rmiService.IBanque;

public class BanqueClient {
    public static void main(String[] args) {
        try {
            // Recherche de l'objet distant avec JNDI.
            Context contexte = new InitialContext();
            IBanque banque = (IBanque) contexte.lookup("banque");
            contexte.close();
            Compte compte = new Compte(101, 1000.0, new Date());
            System.out.println(banque.creerCompte(compte));
            System.out.println(banque.getInfoCompte(101));
            System.out.println(banque.creerCompte(compte));
            System.out.println(banque.getInfoCompte(999));
        } catch (RemoteException | NamingException e) {
            System.err.println("Service bancaire indisponible : " + e.getMessage());
        }
    }
}
