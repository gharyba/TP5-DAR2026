package rmiService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import metier.Compte;

public class BanqueImpl extends UnicastRemoteObject implements IBanque {
    private static final long serialVersionUID = 1L;
    private final Map<Integer, Compte> comptes = new HashMap<>();

    public BanqueImpl() throws RemoteException {
        super();
    }

    @Override
    public synchronized String creerCompte(Compte c) throws RemoteException {
        if (comptes.containsKey(c.getCode())) {
            return "Le compte " + c.getCode() + " existe déjà.";
        }
        comptes.put(c.getCode(), c);
        return "Compte " + c.getCode() + " créé.";
    }

    @Override
    public synchronized String getInfoCompte(int code) throws RemoteException {
        Compte compte = comptes.get(code);
        if (compte == null) {
            return "Compte " + code + " introuvable.";
        }
        return compte.toString();
    }
}
