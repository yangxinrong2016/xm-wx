package com.imxiaomai.wxplatform.util;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by zengyaowen on 15-10-14.
 */
public class HttpReqUtils {
    public static String getDataFromRequest(HttpServletRequest request) throws IOException {
        byte[] bytes = StreamUtil.consume(request.getInputStream());
        String data =  new String(bytes,"UTF-8");
        return data;
    }

}
