package ir.sajjadboodaghi.niraa_admin.Handlers.Interfaces;

/**
 * Created by Sajjad on 10/01/2018.
 */

public interface RequestCallback{
    void onNoInternetAccess();
    void onSuccess(String response);
    void onRequestError();
}