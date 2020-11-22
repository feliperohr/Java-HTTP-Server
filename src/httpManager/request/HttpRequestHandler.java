package httpManager.request;

public class HttpRequestHandler {

    private String request;

    private String method;
    private String path;
    private String body;

    public HttpRequestHandler(String request) {
        this.request = request;
        setMethod(request);
        setPath(request);
        setBody(request);
    }

    private void setMethod(String request) {
        String[] reqMethod = getRequest().split(" ");
        this.method = reqMethod[0];
    }

    private void setPath(String request) {
        String[] reqMethod = getRequest().split(" ");
        String[] reqPath = reqMethod[1].split(" ");
        this.path = reqPath[0];
    }

    private void setBody(String request) {
        String met;
        if((met = getMethod()).equals("POST")){
            String[] body = getRequest().split("\r\n\r\n");
            this.body = body[1];
        }
    }

    public String getRequest() {
        return request;
    }

    public String getMethod(){
        return method;
    }

    public String getPath(){
        return path;
    }

    public String getBody() {
        return body;
    }

}
