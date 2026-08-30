/**
 * @author Michael
 */
package fr.btsciel;
import java.net.*;
import java.io.*;
public class Client_TCP_Base {
    public static void main(String[] args) {
        try {
            System.out.println("Serveur en local (127.0.0.1) sur quel port ?");
            int port = In.readInteger();

            Socket socket = new Socket(InetAddress.getLoopbackAddress(), port);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintStream out = new PrintStream(socket.getOutputStream(), true);

            try {

                socket.setSoTimeout(5000);
                // Lecture du message d'accueil une seule fois.
                String message = in.readLine();

                if (message == null) {
                    System.out.println("Le serveur a fermé la connexion.");
                    return;
                }
                System.out.println("    MESSAGE SERVEUR >  \n      " + message + "\n");

                while (true) {
                    // Après chaque requête, retour à l'attente d'une nouvelle requête.
                    String requete = In.readString();
                    out.println(requete); // envoi réseau
                    System.out.println("La requête : " + requete);

                    try {
                        message = in.readLine();
                    } catch (SocketTimeoutException e) {
                        System.out.println("Aucune réponse après 5 secondes.");
                        System.out.println("En attente d'une nouvelle requête...\n");
                        continue;
                    }
                    System.out.println("    MESSAGE SERVEUR >  \n      " + message + "\n");
                }
            } finally {
                out.close();
                in.close();
                socket.close();
            }

        } catch (Exception Ignored) {
        }
    }
}
