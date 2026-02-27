package client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import com.google.gson.Gson;

import exception.ResponseException;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.LoginRequest;
import request.RegisterRequest;
import response.ListResult;
import response.LoginResult;
import response.NewGameResult;
import response.RegisterResult;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    private final Gson gson = new Gson();

    // user methods
    public LoginResult login(LoginRequest req) throws ResponseException {
        var request = buildRequest("POST", "/session", null, req);
        var response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    public RegisterResult register(RegisterRequest req) throws ResponseException {
        var request = buildRequest("POST", "/user", null, req);
        var response = sendRequest(request);
        return handleResponse(response, RegisterResult.class);
    }

    public void logout(String authToken) throws ResponseException {
        var request = buildRequest("DELETE", "/session", authToken, null);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    // game methods
    public ListResult listGames(String authToken) throws ResponseException {
        var request = buildRequest("GET", "/game", authToken, null);
        var response = sendRequest(request);
        return handleResponse(response, ListResult.class);
    }

    public NewGameResult createGame(String authToken, CreateGameRequest req) throws ResponseException {
        var request = buildRequest("POST", "/game", authToken, req);
        var response = sendRequest(request);
        return handleResponse(response, NewGameResult.class);
    }

    public void joinGame(String authToken, JoinGameRequest req) throws ResponseException {
        var request = buildRequest("PUT", "/game", authToken, req);
        var response = sendRequest(request);
        handleResponse(response, null);
    }


    //clear function

    public void clear() throws ResponseException {
        var request = buildRequest("DELETE", "/db", null, null);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    private HttpRequest buildRequest(String method, String path, String authToken, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (authToken != null) {
            request.setHeader("authorization", authToken);
        }
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(gson.toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null && !body.isEmpty()) {
                throw ResponseException.fromJson(body);
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }

        if (responseClass != null) {
            return gson.fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}