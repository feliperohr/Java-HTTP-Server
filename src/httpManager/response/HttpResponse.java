package httpManager.response;

import java.io.File;

public interface HttpResponse {

    public String getSimpleResponseMsg(int code, String text);

    public String getResponseWithData(int code, String text, File file);

}
