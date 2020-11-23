package httpManager.response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class HttpResponseHandler implements HttpResponse{

    private static HttpResponseHandler instance;

    private HttpResponseHandler() {

    }

    public static HttpResponseHandler getInstance(){
        if(instance == null)
            instance = new HttpResponseHandler();

        return instance;
    };

    @Override
    public String getSimpleResponseMsg(int code, String text){
        String msg = "Code: " + code + " - "+ text + ".";
        String res =
                "HTTP/1.1 " + code + " " + text + "\r\n" +
                "Content-Type: text/html" + "\r\n" +
                "Content-Length:" + msg.getBytes().length + "\r\n" +
                "\r\n" +
                msg;

        return res;
    }

    @Override
    public String getResponseWithData(int code, String text, File file) {
        String res;
        String nextLine = "\r\n";

        if(file.isDirectory())
        {
            String content = listDirectoryFiles(file);
            res =
                    "HTTP/1.1 " + code + " " + text + nextLine +
                            "Content-Type: text/html" + nextLine +
                            "Content-Length:" + content.length() + nextLine +
                            nextLine +
                            content;
            return res;
        }
        int fileSize = (int) file.length();
        String fileData = getFileData(file, fileSize);


        res =
                "HTTP/1.1 " + code + " " + text + nextLine +
                        "Content-Type: " + getFileMimeType(file) + nextLine +
                        "Content-Length:" + fileSize + nextLine +
                        nextLine +
                        fileData;
        return res;

    }

    private String listDirectoryFiles(File file){
        String content = "";
        File[] files = file.listFiles();

        if(files.length == 0){
            content = "Empty folder.";
            return content;
        }

        for (File f : files) {
            if(f.isFile()) {
                content += "- " + f.getName()+ " --- " + f.length() + " bytes" + "\r\n";

            }else if(f.isDirectory()){
                content += "--> " + f.getName()+ " --- " + f.length() + " bytes" + "\r\n";
            }
        }
        System.out.println(content);
        return content;
    }

    private String getFileData(File file, int fileSize) {
        FileInputStream fileIn;
        byte[] fileData;
        String data = "";
        fileData = new byte[fileSize];

        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
            data = new String(fileData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;

    }

    private String getFileMimeType(File file){
        if(file.getPath().endsWith(".html") || file.getPath().endsWith(".txt")){
            return "text/html";
        }else if(file.getPath().endsWith(".png")){
            return "image/png";
        }else if(file.getPath().endsWith(".jpeg")){
            return "image/jpeg";
        }else if(file.getPath().endsWith(".mp4")){
            return "video/mp4";
        }else if(file.getPath().endsWith(".xml")){
            return "application/xml";
        }
        return null;
    }
    ;

    //400 Bad Request ok
//• 403 Forbidden ok
//• 404 Not Found ok
//• 418 I'm a teapot
//• 501 Not Implemented ok
//• 503 Service Unavailable
}
