package com.nuno1212s.bungee.loginhandler;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 */
public class MojangAPIConnector {

    //http connection, read timeout and user agent for a connection to mojang api servers
    private static final int TIMEOUT = 3 * 1_000;
    private static final String USER_AGENT = "Premium-Checker";

    private static final String MCAPI_UUID_URL = "https://mcapi.ca/uuid/player/";

    //only premium (paid account) users have a uuid from here
    private static final String UUID_LINK = "https://api.mojang.com/users/profiles/minecraft/";
    //this includes a-zA-Z1-9_
    private static final String VALID_PLAYERNAME = "^\\w{2,16}$";

    private static final int RATE_LIMIT_CODE = 429;

    //compile the pattern only on plugin enable -> and this have to be threadsafe
    private final Pattern playernameMatcher = Pattern.compile(VALID_PLAYERNAME);

    private final ConcurrentMap<Object, Object> requests;
    private final BalancedSSLFactory sslFactory;
    private final int rateLimit;
    private long lastRateLimit;

    protected final Logger logger;

    public MojangAPIConnector(ConcurrentMap<Object, Object> requests, Logger logger, List<String> localAddresses
            , int rateLimit) {
        this.logger = logger;
        this.requests = requests;

        if (rateLimit > 600) {
            this.rateLimit = 600;
        } else {
            this.rateLimit = rateLimit * localAddresses.size();
        }

        if (localAddresses.isEmpty()) {
            this.sslFactory = null;
        } else {
            Set<InetAddress> addresses = new HashSet<>();
            try {
                System.out.println(InetAddress.getLocalHost().toString());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            for (String localAddress : localAddresses) {
                try {
                    InetAddress address = InetAddress.getByName(localAddress);
                    /*if (!address.isAnyLocalAddress()) {
                        System.out.println("Submitted IP-Address is not local " + address);
                        continue;
                    }*/

                    addresses.add(address);
                } catch (UnknownHostException ex) {
                    logger.log(Level.SEVERE, "IP-Address is unknown to us", ex);
                }
            }

            System.out.println(addresses);

            this.sslFactory = new BalancedSSLFactory(HttpsURLConnection.getDefaultSSLSocketFactory(), addresses);
        }
    }

    /**
     * @param playerName
     * @return null on non-premium
     */
    public UUID getPremiumUUID(String playerName) {
        //check if it's a valid playername
        if (playernameMatcher.matcher(playerName).matches()) {
            //only make a API call if the name is valid existing mojang account

            if (requests.size() >= rateLimit || System.currentTimeMillis() - lastRateLimit < 1_000 * 60 * 10) {
                logger.fine("STILL WAITING FOR RATE_LIMIT - TRYING Third-party API");
                return getUUIDFromAPI(playerName);
            }

            requests.put(new Object(), new Object());

            try {
                HttpsURLConnection connection = getConnection(UUID_LINK + playerName);
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line = reader.readLine();
                    if (line != null && !line.equals("null")) {
                        return getUUIDFromJson(line, playerName, false);
                    }
                } else if (connection.getResponseCode() == RATE_LIMIT_CODE) {
                    logger.info("RATE_LIMIT REACHED - TRYING THIRD-PARTY API");
                    lastRateLimit = System.currentTimeMillis();
                    return getUUIDFromAPI(playerName);
                }
                //204 - no content for not found
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Failed to check if player has a paid account", ex);
                System.out.println(requests);
            }
            //this connection doesn't need to be closed. So can make use of keep alive in java
        }

        return null;
    }

    public UUID getUUIDFromAPI(String playerName) {
        try {
            HttpURLConnection httpConnection = (HttpURLConnection) new URL(MCAPI_UUID_URL + playerName).openConnection();
            httpConnection.addRequestProperty("Content-Type", "application/json");
            httpConnection.setRequestProperty("User-Agent", USER_AGENT);

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                //cracked
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            StringBuilder inputBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                inputBuilder.append(line);
            }

            String input = inputBuilder.toString();
            return getUUIDFromJson(input, playerName, true);
        } catch (IOException iOException) {
            logger.log(Level.SEVERE, "Tried converting name->uuid from third-party api", iOException);
        }

        return null;
    }

    protected UUID getUUIDFromJson(String json, String playerName, boolean API) {
        MojangPlayer mojangPlayer;
        try {
            boolean isArray = json.startsWith("[");
            if (isArray) {
                mojangPlayer = BungeeCord.getInstance().gson.fromJson(json, MojangPlayer[].class)[0];
            } else {
                mojangPlayer = BungeeCord.getInstance().gson.fromJson(json, MojangPlayer.class);
            }
        } catch (Exception e) {
            if (API) {
                e.printStackTrace();
                return null;
            } else {
                return getUUIDFromAPI(playerName);
            }
        }
        return parseId(mojangPlayer.getId());
    }

    private UUID parseId(String line) {
        return UUID.fromString(line.substring(0, 8)
                + "-" + line.substring(8, 12)
                + "-" + line.substring(12, 16)
                + "-" + line.substring(16, 20)
                + "-" + line.substring(20, 32));
    }

    protected HttpsURLConnection getConnection(String url) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(TIMEOUT);
        connection.setReadTimeout(2 * TIMEOUT);
        //the new Mojang API just uses json as response
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", USER_AGENT);

        if (sslFactory != null) {
            connection.setSSLSocketFactory(sslFactory);
        }

        return connection;
    }

}
