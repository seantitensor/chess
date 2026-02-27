package exception;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class ResponseException extends Exception {

    public enum Code {
        ServerError,
        ClientError,
    }

    final private Code code;

    public ResponseException(Code code, String message) {
        super(message);
        this.code = code;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", code));
    }

    public static ResponseException fromJson(String json) {
        try {
            var map = new Gson().fromJson(json, HashMap.class);
            if (map == null) {
                return new ResponseException(Code.ServerError, "Empty response from server");
            }
            String message = map.containsKey("message") ? map.get("message").toString() : "Unknown error";
            Code code = Code.ClientError; 
            if (map.containsKey("status")) {
                code = Code.valueOf(map.get("status").toString());
            }
            return new ResponseException(code, message);
        } catch (Exception e) {
            return new ResponseException(Code.ServerError, "Failed to parse error: " + json);
        }
    }

    public Code code() {
        return code;
    }

    public static Code fromHttpStatusCode(int httpStatusCode) {
        return switch (httpStatusCode) {
            case 500 -> Code.ServerError;
            default -> Code.ClientError;
        };
    }

    public int toHttpStatusCode() {
        return switch (code) {
            case ServerError -> 500;
            case ClientError -> 400;
        };
    }
}