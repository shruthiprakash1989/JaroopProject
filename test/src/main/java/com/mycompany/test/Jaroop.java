/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Jaroop {
    /**
     * This is the main program which will receive the request, calls required methods
     * and processes the response.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Scanner scannedInput = new Scanner (System.in);
        String in = "";
        if (args.length == 0) {
            System.out.println("Enter the query");
            in = scannedInput.nextLine();
        } else {
            in = args[0];
        }
        in = in.toLowerCase().replaceAll("\\s+", "_");
        int httpStatus =  checkInvalidInput(in);
        if (httpStatus == 0) {
            System.out.print("Not found");
            System.exit(0);
        }
        String url = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles="+in;
        HttpURLConnection connection = getConnection(url);
        BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String request = "";
        StringBuilder response = new StringBuilder();
        while ((request = input.readLine()) != null) {
            //only appending what ever is required for JSON parsing and ignoring the rest
            response.append("{");
            //appending the key "extract" to the string so that the JSON parser can parse it's value,
            //also we don't need last 3 paranthesis in the response, excluding them as well.
            response.append(request.substring(request.indexOf("\"extract"), request.length()-3));
        }
        parseJSON(response.toString());
    }
    
    /**
     * @param response response string which is in JSON format.
     * This method parses the JSON formatted data and prints the output.
     */
    private static void parseJSON(String response) {
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response);
            String output = jsonObject.get("extract").toString();
            System.out.print(output);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * @throws MalformedURLException
     * @throws ProtocolException
     * @throws IOException
     * This method obtains the HTTP connection required in order to make a GET request.
     */
    private static HttpURLConnection getConnection (String url) throws MalformedURLException, ProtocolException, IOException {
        URL urlObject = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        connection.setRequestMethod("GET");
        return connection;
    }
    
    /**
     * @throws ProtocolException
     * @throws IOException
     * This method checks for the invalid input by the user.
     */
    private static int checkInvalidInput(String input) throws ProtocolException, IOException {
        String url = "https://en.wikipedia.org/wiki/"+input;
        HttpURLConnection connection  = getConnection(url);
        return connection.getResponseCode() == 200 ? 1 :0;
    }
}
