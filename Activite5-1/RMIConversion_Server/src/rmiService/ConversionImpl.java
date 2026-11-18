package rmiService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ConversionImpl extends UnicastRemoteObject implements IConversion {
    private static final long serialVersionUID = 1L;
    private static final double TAUX_EXEMPLE = 3.0;

    public ConversionImpl() throws RemoteException {
        super();
    }

    @Override
    public double convertirMontant(double mt) throws RemoteException {
        return mt * TAUX_EXEMPLE;
    }
}
