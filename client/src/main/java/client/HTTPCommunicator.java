package client;

import com.google.gson.Gson;
import exception.ResponseException;
import model.ResponseExceptionContent;

import java.io.*;
import java.net.*;


public class HTTPCommunicator {
    private final String serverURL;
    private final ServerFacade server;

    public HTTPCommunicator(String url, ServerFacade server) {
        this.serverURL = url; //needs https:// prefix?
        this.server = server;
    }

    public <T> T makeRequest(String method, String path, Object requestBody, String authToken, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverURL + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            if (authToken != null && !authToken.isEmpty()) {
                http.setRequestProperty("Authorization", authToken);
            }
            writeBody(requestBody, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private void writeBody(Object requestBody, HttpURLConnection http) throws IOException {
        if (requestBody != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(requestBody);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        int status = http.getResponseCode();
        if (!isSuccessful(status)) {
            String errorMessage = "HTTP Error " + status;
            try (InputStream errorStream = http.getErrorStream()) {
                if (errorStream != null) {
                    ResponseExceptionContent content = parseErrorContents(errorStream);
                    if (content != null && content.message() != null) {//check that message isn't ""?
                        errorMessage = content.message();
                    }
                }
            }
            throw new ResponseException(status, errorMessage);
        }
    }

    private ResponseExceptionContent parseErrorContents(InputStream errorStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String jsonString = sb.toString();
            return new Gson().fromJson(jsonString, ResponseExceptionContent.class);
        }
    }


    private <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2; //status codes in the 200s are successful
    }

}
