package de.pauerbanane.core.addons.votifier.thread;

import de.pauerbanane.core.addons.votifier.Vote;
import de.pauerbanane.core.addons.votifier.Votifier;
import de.pauerbanane.core.addons.votifier.VotifierEvent;
import de.pauerbanane.core.addons.votifier.Votifier;
import de.pauerbanane.core.addons.votifier.crypto.RSA;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import javax.crypto.BadPaddingException;

public class VoteReceiver extends Thread {
    private final Votifier plugin;

    private final String host;

    private final int port;

    private ServerSocket server;

    private boolean running = true;

    public VoteReceiver(Votifier plugin, String host, int port) throws Exception {
        this.plugin = plugin;
        this.host = host;
        this.port = port;
        initialize();
    }

    private void initialize() throws Exception {
        try {
            this.server = new ServerSocket();
            this.server.bind(new InetSocketAddress(this.host, this.port));
        } catch (Exception ex) {
            Votifier.LOG.severe("Error initializing vote receiver. Please verify that the configured");
            Votifier.LOG.severe("IP address and port are not already in use. This is a common problem");
            Votifier.LOG.severe("with hosting services and, if so, you should check with your hosting provider.");
            throw new Exception(ex);
        }
    }

    public void shutdown() {
        this.running = false;
        if (this.server == null)
            return;
        try {
            this.server.close();
        } catch (Exception ex) {
            Votifier.LOG.severe("Unable to shut down vote receiver cleanly.");
        }
    }

    public void run() {
        while (this.running) {
            try {
                Socket socket = this.server.accept();
                socket.setSoTimeout(5000);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                InputStream in = socket.getInputStream();
                writer.write("VOTIFIER " + this.plugin.getVersion());
                writer.newLine();
                writer.flush();
                byte[] block = new byte[256];
                in.read(block, 0, block.length);
                block = RSA.decrypt(block, this.plugin.getKeyPair().getPrivate());
                int position = 0;
                String opcode = readString(block, position);
                position += opcode.length() + 1;
                if (!opcode.equals("VOTE"))
                    throw new Exception("Unable to decode RSA");
                String serviceName = readString(block, position);
                position += serviceName.length() + 1;
                String username = readString(block, position);
                position += username.length() + 1;
                String address = readString(block, position);
                position += address.length() + 1;
                String timeStamp = readString(block, position);
                position += timeStamp.length() + 1;
                Player player = Bukkit.getPlayer(username);
                if(player == null)
                    return;
                Vote vote = new Vote();
                vote.setServiceName(serviceName);
                vote.setUsername(username);
                vote.setAddress(address);
                vote.setTimeStamp(timeStamp);
                Votifier.LOG.info("Received vote record -> " + vote);
                new VotifierEvent(vote).callEvent();
                writer.close();
                in.close();
                socket.close();
            } catch (SocketException ex) {
                Votifier.LOG.warning("Protocol error. Ignoring packet - " + ex.getLocalizedMessage());
            } catch (BadPaddingException ex) {
                Votifier.LOG.warning("Unable to decrypt vote record. Make sure that that your public key");
                Votifier.LOG.warning("matches the one you gave the server list.");
            } catch (Exception ex) {
                Votifier.LOG.warning("Exception caught while receiving a vote notification");
            }
        }
    }

    private String readString(byte[] data, int offset) {
        StringBuilder builder = new StringBuilder();
        for (int i = offset; i < data.length &&
                data[i] != 10; i++)
            builder.append((char)data[i]);
        return builder.toString();
    }
}