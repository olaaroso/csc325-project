package com.group4.macromanager.model;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

public class OAuthCallbackServer {
    private final HttpServer server;
    private final CompletableFuture<GoogleOAuthManager.GoogleTokenResponse> future;
    private final GoogleOAuthManager oAuthManager;

    public OAuthCallbackServer(int port, CompletableFuture<GoogleOAuthManager.GoogleTokenResponse> future) throws IOException, Exception {
        this.future = future;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.oAuthManager = new GoogleOAuthManager();

        server.createContext("/auth/callback", new CallbackHandler());
        server.setExecutor(null);
    }

    public void start() {
        server.start();
        System.out.println("OAuth callback server started on port " + server.getAddress().getPort());
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("OAuth callback server stopped");
        }
    }

    private class CallbackHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                URI requestUri = exchange.getRequestURI();
                String query = requestUri.getQuery();

                if (query != null && query.contains("code=")) {
                    String authCode = extractAuthCode(query);

                    // Send success response to browser
                    String response = "<html><body><h2>Authentication successful!</h2><p>You can close this window.</p></body></html>";
                    exchange.sendResponseHeaders(200, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();

                    // Exchange code for tokens in background
                    new Thread(() -> {
                        try {
                            GoogleOAuthManager.GoogleTokenResponse tokenResponse = oAuthManager.exchangeCodeForTokens(authCode);
                            future.complete(tokenResponse);
                        } catch (Exception e) {
                            future.completeExceptionally(e);
                        } finally {
                            stop();
                        }
                    }).start();

                } else {
                    // Handle error
                    String errorResponse = "<html><body><h2>Authentication failed</h2></body></html>";
                    exchange.sendResponseHeaders(400, errorResponse.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(errorResponse.getBytes());
                    os.close();

                    future.completeExceptionally(new RuntimeException("Authentication failed"));
                    stop();
                }

            } catch (Exception e) {
                future.completeExceptionally(e);
                stop();
            }
        }

        private String extractAuthCode(String query) {
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length == 2 && "code".equals(pair[0])) {
                    return pair[1];
                }
            }
            throw new RuntimeException("Authorization code not found in callback");
        }
    }
}
