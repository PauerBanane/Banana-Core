package de.pauerbanane.core.addons.resourcepack;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ResourcepackManager {

    private final int port;
    private final String host;
    private final String hash;

    private ResourcepackServer server;
    private final File pack;

    public ResourcepackManager(final Resourcepack addon) {
        host = addon.getResourcepackHttpServerAddress();
        port = addon.getPort();
        pack = new File(addon.getPlugin().getDataFolder() + File.separator + "Resourcepack/packs/knickscraft.zip");
        hash = getFileHashChecksum(pack);
        System.out.println("IP: " + host);
        System.out.println("Port: " + port);
        startServer();
    }

    public String getResourceHash() {
        return hash;
    }

    public String getDownloadURL(final String packname) {
        return "http://" + host + ":" + port + "/" + packname;
    }

    public void shutdown() {
        server.terminate();
    }


    private void startServer() {
        try {
            server = new ResourcepackServer(port) {

                @Override
                public File requestFileCallback(final ResourceServerConnection connection,
                                                final String request) {
                    final Player player = getAddress(connection);

                    if (player == null) {
                        // Connection from unknown IP, refuse connection.
                        return null;
                    }
                    Bukkit.getLogger().info(
                            "Connection " + connection.getClient().getInetAddress() + " is requesting + "
                                    + request);
                    // Return the .zip file
                    return pack;
                }

                @Override
                public void onSuccessfulRequest(final ResourceServerConnection connection,
                                                final String request) {
                    Bukkit.getLogger().info(
                            "Successfully served " + request + " to " + connection.getClient().getInetAddress()
                                    .getHostAddress());
                }

                @Override
                public void onClientRequest(final ResourceServerConnection connection,
                                            final String request) {
                    Bukkit.getLogger()
                            .info(connection.getClient().getInetAddress() + " is requesting the resourcepack");
                }

                @Override
                public void onRequestError(final ResourceServerConnection connection, final int code) {
                    Bukkit.getLogger().info(
                            "Error " + code + " while attempting to serve " + connection.getClient()
                                    .getInetAddress().getHostAddress());
                }
            };

            server.start();
            Bukkit.getLogger().info("Successfully started the HTTP Server");
        } catch (final IOException ex) {
            Bukkit.getLogger().severe("Failed to start HTTP ResourceServer!");
            ex.printStackTrace();
        }
    }

    private String getFileHashChecksum(final File input) {
        try {
            return Files.hash(input, Hashing.sha1()).toString();
        } catch (final IOException e) {
            Bukkit.getLogger().severe("Failed to calculate resourcepack hashcode - " + e.getMessage());
            return null;
        }
    }


    private Player getAddress(final ResourcepackServer.ResourceServerConnection connection) {
        final byte[] ip = connection.getClient().getInetAddress().getAddress();

        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (Arrays.equals(player.getAddress().getAddress().getAddress(), ip)) {
                return player;
            }
        }
        return null;
    }


}