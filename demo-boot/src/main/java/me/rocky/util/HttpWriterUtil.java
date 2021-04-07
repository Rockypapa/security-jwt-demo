package me.rocky.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Rocky
 * @version 1.0
 * @description
 * @email inaho00@foxmail.com
 * @createDate 2020/9/23 下午1:58
 * @log
 */

public class HttpWriterUtil {
    public static void outWriter(HttpServletResponse response,String encoding,String contentType,
                                 Integer code,String message,Object data ) throws IOException {
        response.setCharacterEncoding(encoding);
        response.setContentType(contentType);
        response.setStatus(code);
        Map<String, Object> params = new HashMap<>(4);
        params.put("code", code);
        params.put("message", message);
        params.put("data", data);
        response.getWriter().print(new ObjectMapper().writeValueAsString(params));
    }


}
