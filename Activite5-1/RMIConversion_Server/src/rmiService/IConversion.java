package rmiService;

import java.rmi.Remote;
import java.rmi.RemoteException;

// Contrat du service distant de conversion.
public interface IConversion extends Remote {
    double convertirMontant(double mt) throws RemoteException;
}
