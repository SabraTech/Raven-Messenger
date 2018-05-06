import spark.Request;
import spark.Response;
import spark.Route;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static spark.Spark.port;
import static spark.Spark.get;

public class Server {

    private static String PYTHON_PATH = "/usr/bin/python";
    private static String PYTHON_FILE_PATH = "/home/sabra/Desktop/test.py";
    private static String message;

    public static void main(String arg[]){

        // set the server port
        port(8080);

        // set the request receiver
        get("/emoji", new Route() {
            public Object handle(Request request, Response response) throws Exception {
                System.out.println("Java Server: URL = " + request.url());
                message = request.queryParams("message");
                System.out.println("Java Server: Message = " + message);
                ProcessBuilder processBuilder = new ProcessBuilder(PYTHON_PATH, PYTHON_FILE_PATH, message);
                String lastLine = "";
                try{
                    Process process = processBuilder.start();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    System.out.println("Java Server: ..... Python call Start .....");
                    String line = "";
                    while((line = bufferedReader.readLine()) != null){
                        System.out.println("Python logs: " + line);
                        lastLine = line;
                    }
                    response.body(lastLine);
                    response.status(200);
                    System.out.println("Java Server: ..... Python call Done .....");
                } catch (Exception e){
                    e.printStackTrace();
                }
                System.out.println("Java Server: lastline = " + lastLine);
                return lastLine;
            }
        });

    }
}
